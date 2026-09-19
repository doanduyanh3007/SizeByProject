package com.finalproject2.service;

import com.finalproject2.entity.UserAddress;

import java.util.List;
import java.util.Map;

public interface UserAddressService {

    List<Map<String, Object>> getByAccountId(Integer accountId);

    Map<String, Object> create(Integer accountId, Map<String, Object> payload);

    Map<String, Object> update(Integer accountId, Integer addressId, Map<String, Object> payload);

    void delete(Integer accountId, Integer addressId);

    Map<String, Object> setDefault(Integer accountId, Integer addressId);
}
