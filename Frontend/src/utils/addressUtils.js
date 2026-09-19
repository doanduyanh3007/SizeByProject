export function normalizeAddressText(value) {
  return String(value || "")
    .toLowerCase()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/[^a-z0-9\s]/g, " ")
    .replace(/\s+/g, " ")
    .trim();
}

function stripAdminPrefix(value) {
  return normalizeAddressText(value)
    .replace(/\b(phuong|p|xa|x|thi tran|tt|quan|q|huyen|h|thi xa|tx|thanh pho|tp|tinh)\b/g, " ")
    .replace(/\s+/g, " ")
    .trim();
}

export function findAddressOption(list, source) {
  if (!source || !Array.isArray(list) || !list.length) {
    return null;
  }

  const candidates = String(source)
    .split(",")
    .map((part) => part.trim())
    .filter(Boolean);

  if (!candidates.length) {
    candidates.push(String(source));
  }

  let best = null;
  let bestScore = -1;

  for (const item of list) {
    const name = normalizeAddressText(item?.name);
    const strippedName = stripAdminPrefix(item?.name);
    if (!name) continue;

    for (const candidate of candidates) {
      const normalizedCandidate = normalizeAddressText(candidate);
      const strippedCandidate = stripAdminPrefix(candidate);
      if (!normalizedCandidate) continue;

      let score = 0;
      if (normalizedCandidate === name || strippedCandidate === strippedName) score = 100;
      else if (normalizedCandidate.includes(name) || name.includes(normalizedCandidate)) score = 80;
      else if (strippedCandidate.includes(strippedName) || strippedName.includes(strippedCandidate)) score = 70;

      if (score > bestScore) {
        bestScore = score;
        best = item;
      }
    }
  }

  return bestScore >= 20 ? best : null;
}

export function buildFullAddress({
  streetAddress,
  ward,
  district,
  province,
  selectedWard,
  selectedDistrict,
  selectedProvince,
} = {}) {
  const wardName = typeof ward === "object" ? ward?.name : ward ?? selectedWard?.name;
  const districtName = typeof district === "object" ? district?.name : district ?? selectedDistrict?.name;
  const provinceName = typeof province === "object" ? province?.name : province ?? selectedProvince?.name;

  return [streetAddress, wardName, districtName, provinceName].filter(Boolean).join(", ");
}

export function hydrateAddressFields(value, provinces = []) {
  const parts = String(value || "")
    .split(",")
    .map((part) => part.trim())
    .filter(Boolean);

  if (!parts.length || !provinces.length) {
    return {
      selectedProvince: null,
      selectedDistrict: null,
      selectedWard: null,
      streetAddress: parts.join(", "),
    };
  }

  const selectedProvince =
    findAddressOption(provinces, parts[parts.length - 1]) || findAddressOption(provinces, value);

  const selectedDistrict = selectedProvince?.districts
    ? findAddressOption(selectedProvince.districts, parts[parts.length - 2]) ||
      findAddressOption(selectedProvince.districts, value)
    : null;

  const selectedWard = selectedDistrict?.wards
    ? findAddressOption(selectedDistrict.wards, parts[parts.length - 3]) ||
      findAddressOption(selectedDistrict.wards, value)
    : null;

  const usedParts = new Set(
    [selectedProvince, selectedDistrict, selectedWard]
      .filter(Boolean)
      .map((item) => normalizeAddressText(item.name)),
  );

  const streetParts = parts.filter((part) => !usedParts.has(normalizeAddressText(part)));
  const streetAddress = streetParts.join(", ") || parts[0] || "";

  return {
    selectedProvince,
    selectedDistrict,
    selectedWard,
    streetAddress,
  };
}

export function bindHierarchyToProvinceTree(hierarchy, provinces = []) {
  const selectedProvince = hierarchy.selectedProvince
    ? findAddressOption(provinces, hierarchy.selectedProvince.name)
    : null;

  const selectedDistrict =
    hierarchy.selectedDistrict && selectedProvince?.districts
      ? findAddressOption(selectedProvince.districts, hierarchy.selectedDistrict.name)
      : null;

  const selectedWard =
    hierarchy.selectedWard && selectedDistrict?.wards
      ? findAddressOption(selectedDistrict.wards, hierarchy.selectedWard.name)
      : null;

  return {
    selectedProvince,
    selectedDistrict,
    selectedWard,
    streetAddress: hierarchy.streetAddress || "",
  };
}

export function resolveAddressHierarchy(address, provinces = []) {
  if (!address) {
    return {
      selectedProvince: null,
      selectedDistrict: null,
      selectedWard: null,
      streetAddress: "",
    };
  }

  const hydrated = hydrateAddressFields(address.fullAddress, provinces);
  let selectedProvince = hydrated.selectedProvince;
  let selectedDistrict = hydrated.selectedDistrict;
  let selectedWard = hydrated.selectedWard;

  if (address.province) {
    selectedProvince = findAddressOption(provinces, address.province) || selectedProvince;
  }
  if (address.district && selectedProvince?.districts) {
    selectedDistrict =
      findAddressOption(selectedProvince.districts, address.district) || selectedDistrict;
  }
  if (address.ward && selectedDistrict?.wards) {
    selectedWard = findAddressOption(selectedDistrict.wards, address.ward) || selectedWard;
  }

  const bound = bindHierarchyToProvinceTree(
    {
      selectedProvince,
      selectedDistrict,
      selectedWard,
      streetAddress: address.streetAddress || hydrated.streetAddress || "",
    },
    provinces,
  );

  return bound;
}

export async function loadProvinces() {
  const urls = ["/provinces-api/?depth=3", "https://provinces.open-api.vn/api/?depth=3"];

  for (const url of urls) {
    try {
      const response = await fetch(url);
      if (!response.ok) continue;
      const data = await response.json();
      if (Array.isArray(data) && data.length > 0) {
        return data;
      }
    } catch {
      // try next source
    }
  }

  return [];
}

export const ADDRESS_LABELS = ["Nhà riêng", "Văn phòng", "Nhà bạn bè", "Khác"];

export function isTruthyDefault(value) {
  return value === true || value === 1 || value === "1" || value === "true";
}

export function normalizeAddressId(value) {
  const id = Number(value);
  return Number.isFinite(id) ? id : null;
}

export function createEmptyAddressForm(defaults = {}) {
  return {
    label: defaults.label || "Nhà riêng",
    fullname: defaults.fullname || "",
    phone: defaults.phone || "",
    streetAddress: "",
    selectedProvince: null,
    selectedDistrict: null,
    selectedWard: null,
    isDefault: false,
  };
}

export function addressToForm(address, provinces = []) {
  const hierarchy = resolveAddressHierarchy(address, provinces);

  return {
    label: address?.label || "Nhà riêng",
    fullname: address?.fullname || "",
    phone: address?.phone || "",
    streetAddress: hierarchy.streetAddress,
    selectedProvince: hierarchy.selectedProvince,
    selectedDistrict: hierarchy.selectedDistrict,
    selectedWard: hierarchy.selectedWard,
    isDefault: isTruthyDefault(address?.isDefault),
  };
}

export function formToPayload(form, { preserveDefault = false } = {}) {
  return {
    label: form.label,
    fullname: form.fullname,
    phone: form.phone,
    streetAddress: form.streetAddress,
    ward: form.selectedWard?.name || "",
    district: form.selectedDistrict?.name || "",
    province: form.selectedProvince?.name || "",
    fullAddress: buildFullAddress(form),
    isDefault: preserveDefault ? true : isTruthyDefault(form.isDefault),
  };
}

export function pickDefaultAddress(addresses = []) {
  const list = Array.isArray(addresses) ? addresses : [];
  return list.find((item) => isTruthyDefault(item.isDefault)) || list[0] || null;
}
