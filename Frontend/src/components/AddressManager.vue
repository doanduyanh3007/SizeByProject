<template>
  <div class="flex flex-col gap-4">
    <div class="flex flex-wrap items-center justify-between gap-3">
      <div>
        <h3 class="text-lg font-bold text-slate-900 dark:text-white">
          {{ mode === 'select' ? 'Chọn địa chỉ giao hàng' : 'Sổ địa chỉ' }}
        </h3>
        <p class="text-sm text-slate-500 dark:text-[#9cabba]">
          {{ mode === 'select'
            ? 'Chọn một địa chỉ đã lưu hoặc thêm địa chỉ mới.'
            : 'Quản lý nhiều địa chỉ và chọn địa chỉ mặc định.' }}
        </p>
      </div>
      <button
        type="button"
        class="inline-flex items-center gap-2 rounded-lg bg-primary px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-primary-hover"
        @click="openCreateModal"
      >
        <span class="material-symbols-outlined text-[18px]">add_location</span>
        Thêm địa chỉ
      </button>
    </div>

    <div v-if="isLoading" class="flex justify-center py-8">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary" />
    </div>

    <div v-else-if="addresses.length === 0" class="rounded-xl border border-dashed border-slate-300 dark:border-slate-700 p-8 text-center">
      <span class="material-symbols-outlined text-4xl text-slate-400">location_off</span>
      <p class="mt-3 text-sm text-slate-500 dark:text-[#9cabba]">Bạn chưa có địa chỉ nào.</p>
      <button
        type="button"
        class="mt-4 text-sm font-semibold text-primary hover:text-primary-hover"
        @click="openCreateModal"
      >
        Thêm địa chỉ đầu tiên
      </button>
    </div>

    <div v-else class="grid grid-cols-1 gap-3 md:grid-cols-2">
      <article
        v-for="address in addresses"
        :key="address.id"
        class="relative rounded-xl border p-4 transition-all cursor-pointer"
        :class="cardClass(address)"
        @click="handleSelect(address)"
      >
        <div class="flex items-start justify-between gap-3">
          <div class="min-w-0 flex-1">
            <div class="flex flex-wrap items-center gap-2">
              <span
                v-if="address.label"
                class="rounded-md bg-slate-100 px-2 py-0.5 text-xs font-semibold text-slate-700 dark:bg-[#283039] dark:text-slate-200"
              >
                {{ address.label }}
              </span>
              <span
                v-if="address.isDefault"
                class="rounded-md bg-primary/10 px-2 py-0.5 text-xs font-semibold text-primary"
              >
                Mặc định
              </span>
            </div>
            <p class="mt-2 font-semibold text-slate-900 dark:text-white">
              {{ address.fullname || 'Chưa có tên' }}
              <span v-if="address.phone" class="font-normal text-slate-500 dark:text-[#9cabba]">
                | {{ address.phone }}
              </span>
            </p>
            <p class="mt-1 text-sm leading-6 text-slate-600 dark:text-[#9cabba]">
              {{ address.fullAddress }}
            </p>
          </div>

          <div
            v-if="mode === 'select'"
            class="flex h-5 w-5 shrink-0 items-center justify-center rounded-full border-2"
            :class="selectedId === address.id
              ? 'border-primary bg-primary text-white'
              : 'border-slate-300 dark:border-slate-600'"
          >
            <span
              v-if="selectedId === address.id"
              class="material-symbols-outlined text-[14px]"
            >
              check
            </span>
          </div>
        </div>

        <div
          v-if="mode === 'manage'"
          class="mt-4 flex flex-wrap gap-2 border-t border-slate-100 pt-3 dark:border-slate-800"
          @click.stop
        >
          <button
            type="button"
            class="text-xs font-semibold text-primary hover:text-primary-hover"
            @click="openEditModal(address)"
          >
            Sửa
          </button>
          <button
            v-if="!address.isDefault"
            type="button"
            class="text-xs font-semibold text-slate-600 hover:text-primary dark:text-[#9cabba]"
            @click="handleSetDefault(address)"
          >
            Đặt mặc định
          </button>
          <button
            type="button"
            class="text-xs font-semibold text-red-500 hover:text-red-600"
            @click="handleDelete(address)"
          >
            Xóa
          </button>
        </div>
      </article>
    </div>

    <p v-if="error" class="text-sm text-red-500">{{ error }}</p>

    <!-- Modal -->
    <teleport to="body">
      <div
        v-if="showModal"
        class="fixed inset-0 z-[200] flex items-center justify-center px-4"
      >
        <div class="absolute inset-0 bg-black/50 backdrop-blur-sm" @click="closeModal" />
        <div class="relative max-h-[90vh] w-full max-w-2xl overflow-y-auto rounded-2xl border border-slate-200 bg-white p-6 shadow-2xl dark:border-[#334455] dark:bg-[#18212b]">
          <div class="mb-5 flex items-center justify-between">
            <h4 class="text-xl font-bold text-slate-900 dark:text-white">
              {{ editingId ? 'Sửa địa chỉ' : 'Thêm địa chỉ mới' }}
            </h4>
            <button
              type="button"
              class="rounded-lg p-2 text-slate-500 hover:bg-slate-100 dark:hover:bg-[#283039]"
              @click="closeModal"
            >
              <span class="material-symbols-outlined">close</span>
            </button>
          </div>

          <form class="flex flex-col gap-4" @submit.prevent="handleSave">
            <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
              <label class="flex flex-col gap-2">
                <span class="text-sm font-medium text-slate-700 dark:text-slate-300">Nhãn địa chỉ</span>
                <select
                  v-model="form.label"
                  class="rounded-lg border border-slate-900 bg-white px-4 py-2.5 text-sm dark:border-slate-700 dark:bg-[#283039] dark:text-white"
                >
                  <option v-for="label in ADDRESS_LABELS" :key="label" :value="label">
                    {{ label }}
                  </option>
                </select>
              </label>

              <label class="flex flex-col gap-2">
                <span class="text-sm font-medium text-slate-700 dark:text-slate-300">Họ và tên</span>
                <input
                  v-model.trim="form.fullname"
                  type="text"
                  required
                  class="rounded-lg border border-slate-900 bg-white px-4 py-2.5 text-sm dark:border-slate-700 dark:bg-[#283039] dark:text-white"
                  placeholder="Nguyễn Văn A"
                />
              </label>

              <label class="flex flex-col gap-2 md:col-span-2">
                <span class="text-sm font-medium text-slate-700 dark:text-slate-300">Số điện thoại</span>
                <input
                  v-model.trim="form.phone"
                  type="tel"
                  required
                  class="rounded-lg border border-slate-900 bg-white px-4 py-2.5 text-sm dark:border-slate-700 dark:bg-[#283039] dark:text-white"
                  placeholder="09xxxxxxxx"
                />
              </label>
            </div>

            <div class="grid grid-cols-1 gap-4 md:grid-cols-3">
              <label class="flex flex-col gap-2">
                <span class="text-sm font-medium text-slate-700 dark:text-slate-300">Tỉnh/Thành phố</span>
                <SearchableSelect
                  :key="`province-${editingId || 'new'}`"
                  v-model="form.selectedProvince"
                  :options="provinces"
                  placeholder="Chọn tỉnh/thành phố"
                  @change="onProvinceChange"
                />
              </label>
              <label class="flex flex-col gap-2">
                <span class="text-sm font-medium text-slate-700 dark:text-slate-300">Quận/Huyện</span>
                <SearchableSelect
                  :key="`district-${editingId || 'new'}-${form.selectedProvince?.code || 'none'}`"
                  v-model="form.selectedDistrict"
                  :options="form.selectedProvince?.districts || []"
                  :disabled="!form.selectedProvince"
                  placeholder="Chọn quận/huyện"
                  @change="onDistrictChange"
                />
              </label>
              <label class="flex flex-col gap-2">
                <span class="text-sm font-medium text-slate-700 dark:text-slate-300">Phường/Xã</span>
                <SearchableSelect
                  :key="`ward-${editingId || 'new'}-${form.selectedDistrict?.code || 'none'}`"
                  v-model="form.selectedWard"
                  :options="form.selectedDistrict?.wards || []"
                  :disabled="!form.selectedDistrict"
                  placeholder="Chọn phường/xã"
                />
              </label>
            </div>

            <label class="flex flex-col gap-2">
              <span class="text-sm font-medium text-slate-700 dark:text-slate-300">Địa chỉ chi tiết</span>
              <textarea
                v-model.trim="form.streetAddress"
                rows="2"
                required
                class="resize-none rounded-lg border border-slate-900 bg-white px-4 py-2.5 text-sm dark:border-slate-700 dark:bg-[#283039] dark:text-white"
                placeholder="Số nhà, tên đường..."
              />
            </label>

            <label
              v-if="mode === 'manage'"
              class="inline-flex items-center gap-2 text-sm text-slate-700 dark:text-slate-300"
            >
              <input
                v-model="form.isDefault"
                type="checkbox"
                class="rounded border-slate-900 text-primary focus:ring-primary"
              />
              Đặt làm địa chỉ mặc định
            </label>

            <p v-if="formError" class="text-sm text-red-500">{{ formError }}</p>

            <div class="flex gap-3 pt-2">
              <button
                type="submit"
                :disabled="isSaving"
                class="flex-1 rounded-lg bg-primary px-4 py-3 text-sm font-bold text-white transition-colors hover:bg-primary-hover disabled:opacity-50"
              >
                {{ isSaving ? 'Đang lưu...' : 'Lưu địa chỉ' }}
              </button>
              <button
                type="button"
                class="rounded-lg border border-slate-200 px-4 py-3 text-sm font-bold text-slate-700 transition-colors hover:bg-slate-100 dark:border-slate-700 dark:text-white dark:hover:bg-[#283039]"
                @click="closeModal"
              >
                Hủy
              </button>
            </div>
          </form>
        </div>
      </div>
    </teleport>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, nextTick } from 'vue'
import SearchableSelect from '@/components/SearchableSelect.vue'
import { addressesApi } from '@/services/api'
import {
  ADDRESS_LABELS,
  addressToForm,
  createEmptyAddressForm,
  formToPayload,
  isTruthyDefault,
  loadProvinces,
  normalizeAddressId,
  pickDefaultAddress,
} from '@/utils/addressUtils'

const props = defineProps({
  accountId: {
    type: [Number, String],
    required: true,
  },
  mode: {
    type: String,
    default: 'manage',
    validator: (value) => ['manage', 'select'].includes(value),
  },
  modelValue: {
    type: [Number, String, null],
    default: null,
  },
  defaultFullname: {
    type: String,
    default: '',
  },
  defaultPhone: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['update:modelValue', 'select', 'change', 'ready'])

const addresses = ref([])
const provinces = ref([])
const isLoading = ref(true)
const isSaving = ref(false)
const error = ref('')
const formError = ref('')
const showModal = ref(false)
const editingId = ref(null)
const editingWasDefault = ref(false)
const selectedId = ref(normalizeAddressId(props.modelValue))
const form = ref(createEmptyAddressForm())

watch(
  () => props.modelValue,
  (value) => {
    selectedId.value = normalizeAddressId(value)
  },
)

function isSameAddressId(a, b) {
  return normalizeAddressId(a) === normalizeAddressId(b)
}

function cardClass(address) {
  const isSelected = props.mode === 'select' && isSameAddressId(selectedId.value, address.id)
  return isSelected
    ? 'border-primary bg-primary/5 ring-1 ring-primary/30'
    : 'border-slate-200 bg-white hover:border-primary/40 dark:border-slate-800 dark:bg-[#1a222b]'
}

async function fetchAddresses() {
  if (!props.accountId) {
    addresses.value = []
    return
  }

  isLoading.value = true
  error.value = ''
  try {
    const list = await addressesApi.getAll(props.accountId)
    addresses.value = Array.isArray(list) ? list : []

    const defaultAddress = pickDefaultAddress(addresses.value)
    emit('ready', defaultAddress, addresses.value)

    if (props.mode === 'select') {
      const initial =
        addresses.value.find((item) => isSameAddressId(item.id, selectedId.value)) ||
        defaultAddress
      if (initial) {
        handleSelect(initial, true)
      }
    }

    emit('change', addresses.value)
  } catch (err) {
    error.value = err.message || 'Không tải được danh sách địa chỉ'
  } finally {
    isLoading.value = false
  }
}

function openCreateModal() {
  editingId.value = null
  form.value = createEmptyAddressForm({
    fullname: props.defaultFullname,
    phone: props.defaultPhone,
    label: 'Nhà riêng',
  })
  form.value.isDefault = addresses.value.length === 0
  formError.value = ''
  showModal.value = true
}

async function openEditModal(address) {
  if (!provinces.value.length) {
    provinces.value = await loadProvinces()
  }

  editingId.value = address.id
  editingWasDefault.value = isTruthyDefault(address.isDefault)
  form.value = addressToForm(address, provinces.value)
  if (editingWasDefault.value) {
    form.value.isDefault = true
  }
  formError.value = ''
  showModal.value = true
  await nextTick()
}

function closeModal() {
  showModal.value = false
  editingId.value = null
  editingWasDefault.value = false
  formError.value = ''
}

function handleSelect(address, emitEvent = true) {
  if (props.mode !== 'select') return

  selectedId.value = normalizeAddressId(address.id)
  emit('update:modelValue', selectedId.value)
  if (emitEvent) {
    emit('select', address)
  }
}

async function handleSave() {
  formError.value = ''

  if (!form.value.selectedProvince || !form.value.selectedDistrict || !form.value.selectedWard) {
    formError.value = 'Vui lòng chọn đầy đủ Tỉnh/Quận/Phường'
    return
  }

  if (!form.value.streetAddress?.trim()) {
    formError.value = 'Vui lòng nhập địa chỉ chi tiết'
    return
  }

  isSaving.value = true
  try {
    const preserveDefault = editingId.value && editingWasDefault.value
    const payload = formToPayload(form.value, { preserveDefault })

    if (editingId.value) {
      await addressesApi.update(props.accountId, editingId.value, payload)
    } else {
      const created = await addressesApi.create(props.accountId, payload)
      if (props.mode === 'select') {
        handleSelect(created)
      }
    }

    closeModal()
    await fetchAddresses()
  } catch (err) {
    const raw = err.message || 'Lưu địa chỉ thất bại'
    formError.value = raw.includes('static resource')
      ? 'Backend chưa có API địa chỉ. Hãy restart server Backend sau khi cập nhật code.'
      : raw
  } finally {
    isSaving.value = false
  }
}

async function handleSetDefault(address) {
  try {
    await addressesApi.setDefault(props.accountId, address.id)
    await fetchAddresses()
  } catch (err) {
    error.value = err.message || 'Không thể đặt địa chỉ mặc định'
  }
}

async function handleDelete(address) {
  if (!window.confirm('Bạn có chắc muốn xóa địa chỉ này?')) {
    return
  }

  try {
    await addressesApi.delete(props.accountId, address.id)
    if (isSameAddressId(selectedId.value, address.id)) {
      selectedId.value = null
      emit('update:modelValue', null)
    }
    await fetchAddresses()
  } catch (err) {
    error.value = err.message || 'Xóa địa chỉ thất bại'
  }
}

function onProvinceChange() {
  form.value.selectedDistrict = null
  form.value.selectedWard = null
}

function onDistrictChange() {
  form.value.selectedWard = null
}

onMounted(async () => {
  provinces.value = await loadProvinces()
  await fetchAddresses()
})

defineExpose({
  refresh: fetchAddresses,
})
</script>
