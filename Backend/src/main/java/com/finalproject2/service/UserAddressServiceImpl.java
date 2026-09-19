package com.finalproject2.service;

import com.finalproject2.entity.Account;
import com.finalproject2.entity.UserAddress;
import com.finalproject2.exception.NotFoundException;
import com.finalproject2.repository.AccountRepository;
import com.finalproject2.repository.UserAddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressRepository addressRepo;
    private final AccountRepository accountRepo;

    public UserAddressServiceImpl(UserAddressRepository addressRepo, AccountRepository accountRepo) {
        this.addressRepo = addressRepo;
        this.accountRepo = accountRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getByAccountId(Integer accountId) {
        Account account = findAccount(accountId);
        migrateLegacyAddressIfNeeded(account);

        return addressRepo.findByAccountIdOrderByIsDefaultDescCreatedAtDesc(accountId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Map<String, Object> create(Integer accountId, Map<String, Object> payload) {
        Account account = findAccount(accountId);
        migrateLegacyAddressIfNeeded(account);

        String fullAddress = buildFullAddress(payload);
        if (fullAddress.isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập đầy đủ thông tin địa chỉ");
        }

        UserAddress address = new UserAddress();
        address.setAccount(account);
        applyPayload(address, payload, fullAddress);

        boolean shouldBeDefault = Boolean.TRUE.equals(payload.get("isDefault"))
                || addressRepo.countByAccountId(accountId) == 0;
        if (shouldBeDefault) {
            addressRepo.clearDefaultForAccount(accountId);
            address.setIsDefault(true);
        }

        UserAddress saved = addressRepo.save(address);
        if (Boolean.TRUE.equals(saved.getIsDefault())) {
            syncAccountAddress(account, saved.getFullAddress());
        }

        return toResponse(saved);
    }

    @Override
    @Transactional
    public Map<String, Object> update(Integer accountId, Integer addressId, Map<String, Object> payload) {
        UserAddress address = findAddress(accountId, addressId);
        String fullAddress = buildFullAddress(payload);
        if (fullAddress.isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập đầy đủ thông tin địa chỉ");
        }

        applyPayload(address, payload, fullAddress);

        boolean wasDefault = Boolean.TRUE.equals(address.getIsDefault());
        if (payload.containsKey("isDefault")) {
            boolean wantsDefault = Boolean.TRUE.equals(payload.get("isDefault"));
            if (wantsDefault) {
                addressRepo.clearDefaultForAccountExcept(accountId, addressId);
                address.setIsDefault(true);
            } else if (!wasDefault) {
                address.setIsDefault(false);
            }
        } else if (wasDefault) {
            address.setIsDefault(true);
        }

        UserAddress saved = addressRepo.save(address);
        if (Boolean.TRUE.equals(saved.getIsDefault())) {
            syncAccountAddress(findAccount(accountId), saved.getFullAddress());
        }

        return toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Integer accountId, Integer addressId) {
        UserAddress address = findAddress(accountId, addressId);
        boolean wasDefault = Boolean.TRUE.equals(address.getIsDefault());
        addressRepo.delete(address);

        if (wasDefault) {
            List<UserAddress> remaining = addressRepo.findByAccountIdOrderByIsDefaultDescCreatedAtDesc(accountId);
            if (!remaining.isEmpty()) {
                UserAddress nextDefault = remaining.get(0);
                nextDefault.setIsDefault(true);
                addressRepo.save(nextDefault);
                syncAccountAddress(nextDefault.getAccount(), nextDefault.getFullAddress());
            } else {
                syncAccountAddress(address.getAccount(), null);
            }
        }
    }

    @Override
    @Transactional
    public Map<String, Object> setDefault(Integer accountId, Integer addressId) {
        UserAddress address = findAddress(accountId, addressId);
        addressRepo.clearDefaultForAccountExcept(accountId, addressId);
        address.setIsDefault(true);
        UserAddress saved = addressRepo.save(address);
        syncAccountAddress(findAccount(accountId), saved.getFullAddress());
        return toResponse(saved);
    }

    private Account findAccount(Integer accountId) {
        return accountRepo.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản ID " + accountId));
    }

    private UserAddress findAddress(Integer accountId, Integer addressId) {
        return addressRepo.findByIdAndAccountId(addressId, accountId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy địa chỉ ID " + addressId));
    }

    private void migrateLegacyAddressIfNeeded(Account account) {
        if (addressRepo.countByAccountId(account.getId()) > 0) {
            return;
        }

        String legacyAddress = account.getAddress();
        if (legacyAddress == null || legacyAddress.isBlank()) {
            return;
        }

        UserAddress migrated = new UserAddress();
        migrated.setAccount(account);
        migrated.setLabel("\u0110\u1ecba ch\u1ec9 m\u1eb7c \u0111\u1ecbnh");
        migrated.setFullname(account.getUsername());
        migrated.setPhone(account.getPhone());
        migrated.setFullAddress(legacyAddress.trim());
        migrated.setIsDefault(true);
        addressRepo.save(migrated);
    }

    private void applyPayload(UserAddress address, Map<String, Object> payload, String fullAddress) {
        if (payload.containsKey("label")) {
            address.setLabel(asString(payload.get("label")));
        }
        if (payload.containsKey("fullname")) {
            address.setFullname(asString(payload.get("fullname")));
        }
        if (payload.containsKey("phone")) {
            address.setPhone(asString(payload.get("phone")));
        }
        if (payload.containsKey("streetAddress")) {
            address.setStreetAddress(asString(payload.get("streetAddress")));
        }
        if (payload.containsKey("ward")) {
            address.setWard(asString(payload.get("ward")));
        }
        if (payload.containsKey("district")) {
            address.setDistrict(asString(payload.get("district")));
        }
        if (payload.containsKey("province")) {
            address.setProvince(asString(payload.get("province")));
        }
        address.setFullAddress(fullAddress);
    }

    private String buildFullAddress(Map<String, Object> payload) {
        String provided = asString(payload.get("fullAddress"));
        if (provided != null && !provided.isBlank()) {
            return provided.trim();
        }

        return java.util.stream.Stream.of(
                        asString(payload.get("streetAddress")),
                        asString(payload.get("ward")),
                        asString(payload.get("district")),
                        asString(payload.get("province"))
                )
                .filter(part -> part != null && !part.isBlank())
                .collect(Collectors.joining(", "));
    }

    private void syncAccountAddress(Account account, String fullAddress) {
        account.setAddress(fullAddress);
        accountRepo.save(account);
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value).trim();
    }

    private Map<String, Object> toResponse(UserAddress address) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", address.getId());
        response.put("accountId", address.getAccount().getId());
        response.put("label", address.getLabel());
        response.put("fullname", address.getFullname());
        response.put("phone", address.getPhone());
        response.put("streetAddress", address.getStreetAddress());
        response.put("ward", address.getWard());
        response.put("district", address.getDistrict());
        response.put("province", address.getProvince());
        response.put("fullAddress", address.getFullAddress());
        response.put("isDefault", Boolean.TRUE.equals(address.getIsDefault()));
        response.put("createdAt", address.getCreatedAt());
        return response;
    }
}
