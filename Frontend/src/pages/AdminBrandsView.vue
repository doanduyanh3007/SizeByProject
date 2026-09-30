<template>
  <!-- Toast notification -->
  <Teleport to="body">
    <Transition name="slide-fade">
      <div v-if="successToast" class="fixed top-6 right-6 z-[9999] flex items-center gap-2 rounded-xl bg-emerald-600 px-5 py-3 text-sm font-semibold text-white shadow-lg">
        <span class="material-symbols-outlined text-[20px]">check_circle</span>
        {{ successToast }}
      </div>
    </Transition>
  </Teleport>
  <AdminShell>
    <template #header>
      <div class="flex flex-wrap items-center justify-between gap-2">
        <div>
          <p class="text-xs uppercase tracking-[0.18em] text-primary">
            Quản lý danh mục
          </p>
          <h2 class="mt-1 text-xl font-bold">Quản lý thương hiệu</h2>
        </div>

        <div class="flex flex-wrap items-center gap-2">
          <button
            type="button"
            class="inline-flex items-center gap-1.5 rounded-xl bg-primary px-3 py-1.5 text-sm font-semibold text-white hover:opacity-90"
            @click="openCreateModal"
          >
            <span class="material-symbols-outlined text-[18px]">add</span>
            Thêm thương hiệu
          </button>

          <button
            type="button"
            class="inline-flex items-center gap-1.5 rounded-xl border border-slate-200 bg-white px-3 py-1.5 text-sm font-medium hover:border-primary hover:text-primary dark:border-[#3c342e] dark:bg-[#1f1a17]"
            @click="fetchBrands"
          >
            <span class="material-symbols-outlined text-[18px]">refresh</span>
            Làm mới
          </button>
        </div>
      </div>
    </template>

    <div class="space-y-3">
      <section class="retail-card p-3">
        <label
          class="flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-1.5 dark:border-[#3c342e] dark:bg-[#1f1a17]"
        >
          <span class="material-symbols-outlined text-[18px] text-slate-400"
            >search</span
          >
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Tìm theo tên thương hiệu, ID..."
            class="w-full bg-transparent text-sm outline-none"
          />
        </label>
      </section>

      <section class="retail-card p-3">
        <div
          v-if="error"
          class="mb-4 rounded-xl border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700 dark:border-red-900/40 dark:bg-red-900/20 dark:text-red-300"
        >
          {{ error }}
        </div>

        

        <div
          v-if="loading"
          class="py-8 text-center text-sm text-slate-500 dark:text-[#b9aa9a]"
        >
          Đang tải danh sách thương hiệu...
        </div>

        <div
          v-else-if="filteredBrands.length === 0"
          class="rounded-2xl border border-dashed border-slate-300 p-6 text-center text-sm text-slate-500 dark:border-[#3c342e] dark:text-[#b9aa9a]"
        >
          Không tìm thấy thương hiệu.
        </div>

        <div v-else class="overflow-x-auto">
          <table class="w-full min-w-[680px] text-left text-xs">
            <thead>
              <tr
                class="border-b border-slate-200 text-slate-500 dark:border-[#3c342e] dark:text-[#b9aa9a]"
              >
                <th class="px-2 py-2 font-medium">STT</th>
                <th class="px-2 py-2 font-medium">Logo</th>
                <th class="px-2 py-2 font-medium">Tên thương hiệu</th>
                <th class="px-2 py-2 font-medium">Đường dẫn URL</th>
                <th class="px-2 py-2 font-medium">Trạng thái</th>
                <th class="px-2 py-2 text-right font-medium">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="(brand, index) in paginatedBrands"
                :key="brand.id"
                class="border-b border-slate-100 last:border-none dark:border-[#2b241f]"
              >
                <td
                  class="px-2 py-2 font-semibold text-slate-500 dark:text-[#b9aa9a]"
                >
                  {{ (currentPage - 1) * pageSize + index + 1 }}
                </td>
                <td class="px-2 py-2">
                  <div
                    class="flex h-12 w-12 items-center justify-center overflow-hidden rounded-xl bg-slate-100 dark:bg-[#2b241f]"
                  >
                    <img
                      v-if="brand.logoUrl"
                      :src="brand.logoUrl"
                      :alt="brand.name"
                      class="h-full w-full object-cover"
                      @error="handleImageError"
                    />
                    <span
                      v-else
                      class="material-symbols-outlined text-2xl text-slate-400"
                    >
                      brand_awareness
                    </span>
                  </div>
                </td>
                <td class="px-2 py-2 font-semibold">{{ brand.name }}</td>
                <td class="px-2 py-2 text-slate-500 dark:text-[#b9aa9a]">
                  {{ brand.slugDisplay || "—" }}
                </td>
                <td class="px-2 py-2 text-right">
                  <div class="flex items-center justify-end gap-1">
                    <button
                      class="rounded-lg p-1.5 hover:bg-slate-100 dark:hover:bg-[#2b241f]"
                      title="Chỉnh sửa"
                      @click="openEditModal(brand)"
                    >
                      <span
                        class="material-symbols-outlined text-[18px] text-slate-500"
                        >edit</span
                      >
                    </button>

                    <button
                      class="rounded-lg p-1.5 hover:bg-red-50 dark:hover:bg-red-900/20"
                      title="Xóa"
                      @click="requestDelete(brand)"
                    >
                      <span
                        class="material-symbols-outlined text-[18px] text-red-500"
                        >delete</span
                      >
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Phân trang -->
        <div
          v-if="filteredBrands.length > 0"
          class="mt-3 flex flex-wrap items-center justify-between gap-2 border-t border-slate-200 pt-3 dark:border-[#3c342e]"
        >
          <p class="text-xs text-slate-500 dark:text-[#b9aa9a]">
            Hiển thị {{ (currentPage - 1) * pageSize + 1 }} đến
            {{ Math.min(currentPage * pageSize, filteredBrands.length) }} /
            {{ filteredBrands.length }} thương hiệu
          </p>
          <div class="flex gap-1">
            <button
              @click="currentPage--"
              :disabled="currentPage === 1"
              class="rounded-lg border border-slate-200 px-2.5 py-1 text-xs transition-colors hover:bg-slate-50 disabled:opacity-50 dark:border-[#3c342e] dark:hover:bg-[#2b241f]"
            >
              Trước
            </button>
            <span class="px-3 py-1 text-xs">
              Trang {{ currentPage }} / {{ totalPages }}
            </span>
            <button
              @click="currentPage++"
              :disabled="currentPage === totalPages"
              class="rounded-lg border border-slate-200 px-2.5 py-1 text-xs transition-colors hover:bg-slate-50 disabled:opacity-50 dark:border-[#3c342e] dark:hover:bg-[#2b241f]"
            >
              Sau
            </button>
          </div>
        </div>
      </section>
    </div>

    <!-- Modal Thêm/Sửa -->
    <Teleport to="body">
      <div
        v-if="showModal"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4"
        @click.self="closeModal"
      >
        <div
          class="w-full max-w-lg max-h-[90vh] overflow-y-auto rounded-2xl bg-white p-4 shadow-2xl dark:bg-[#1f1a17]"
        >
          <div class="relative mb-4 flex items-center">
            <h3 class="text-base font-bold">{{ editingBrand ? `Sửa thương hiệu #${editingBrand.id}` : "Thêm thương hiệu mới" }}</h3>
            <button type="button" class="absolute right-0 top-0 inline-flex size-8 items-center justify-center rounded-lg text-slate-400 hover:bg-slate-100 hover:text-slate-600 dark:hover:bg-slate-800" @click="closeModal">
              <span class="material-symbols-outlined text-[20px]">close</span>
            </button>
          </div>


          <div class="grid gap-3">
            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Tên thương hiệu *</label
              >
              <input
                v-model.trim="form.name"
                type="text"
                class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                placeholder="VD: Nike, Adidas, Puma..."
                :class="{ 'border-red-500': errors.name }"
              />
              <p v-if="errors.name" class="mt-1 text-xs text-red-500">
                {{ errors.name }}
              </p>
            </div>

            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500">Hình ảnh thương hiệu</label>
              <div class="flex items-center gap-3">
                <div class="size-16 shrink-0 overflow-hidden rounded-xl border border-slate-200 bg-slate-50 dark:border-[#3c342e] dark:bg-[#2b241f]">
                  <img v-if="form.logoUrl" :src="form.logoUrl" alt="Preview" class="h-full w-full object-cover" />
                  <span v-else class="flex h-full items-center justify-center text-xs text-slate-400">Không ảnh</span>
                </div>
                <div class="flex-1 space-y-1.5">
                  <input
                    type="file"
                    accept="image/*"
                    class="block w-full text-xs text-slate-500 file:mr-2 file:rounded-lg file:border-0 file:bg-slate-100 file:px-3 file:py-1.5 file:text-xs file:font-semibold hover:file:bg-slate-200 dark:file:bg-[#3c342e] dark:file:text-slate-200"
                    @change="handleImageUpload"
                  />
                  <input
                    v-model.trim="form.logoUrl"
                    type="text"
                    placeholder="Hoặc nhập URL ảnh..."
                    class="w-full rounded-lg border border-slate-900 bg-gray-50 px-2 py-1 text-xs dark:border-[#3c342e] dark:bg-[#2b241f]"
                  />
                </div>
              </div>
            </div>
          </div>

          <p v-if="modalError" class="mt-3 text-xs text-red-500">
            {{ modalError }}
          </p>

          <div class="mt-4 flex justify-end gap-2">

            <button
              type="button"
              :disabled="saving"
              class="rounded-xl bg-primary px-4 py-2 text-sm font-medium text-white hover:opacity-90 disabled:opacity-60"
              @click="requestSave"
            >
              {{
                saving
                  ? "Đang lưu..."
                  : editingBrandId
                    ? "Cập nhật"
                    : "Thêm mới"
              }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>

    <ConfirmDialog
      :open="!!confirmModal"
      :title="confirmModal?.title"
      :message="confirmModal?.message"
      :confirm-text="confirmModal?.confirmText"
      :danger="!!confirmModal?.danger"
      :loading="!!confirmModal?.loading"
      @confirm="executeConfirm"
      @cancel="cancelConfirm"
    />
  </AdminShell>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import axios from "axios";
import AdminShell from "@/components/admin/AdminShell.vue";
import ConfirmDialog from "@/components/admin/ConfirmDialog.vue";
import { useConfirmDialog } from "@/composables/useConfirmDialog";
import { brandsApi, adminApi, resolveImageUrl } from "@/services/api";

const { confirmModal, requestConfirm, executeConfirm, cancelConfirm } =
  useConfirmDialog();

// State
const brands = ref([]);
const loading = ref(false);
const error = ref("");
const searchQuery = ref("");
const currentPage = ref(1);
const pageSize = ref(10);
const hiddenBrands = ref([]);

// Modal state
const showModal = ref(false);
const editingBrandId = ref(null);
const saving = ref(false);
const modalError = ref("");
const previewError = ref(false);
const successToast = ref("");

function showToast(message, duration = 3000) {
  successToast.value = message;
  setTimeout(() => { successToast.value = ""; }, duration);
}

const visibleBrands = computed(() => {
  return brands.value.filter(
    (brand) =>
      !hiddenBrands.value.some(
        (hidden) => Number(brand.id) === Number(hidden.id),
      ),
  );
});

// Form data
const form = ref({
  name: "",
  slug: "",
  logoUrl: "",
});

const errors = ref({});

// Computed
const filteredBrands = computed(() => {
  const keyword = searchQuery.value.trim().toLowerCase();
  if (!keyword) {
    return visibleBrands.value;
  }

  return visibleBrands.value.filter((brand) => {
    const haystack = [brand.name, brand.slug, brand.slugDisplay, brand.id]
      .filter((item) => item !== undefined && item !== null)
      .join(" ")
      .toLowerCase();

    return haystack.includes(keyword);
  });
});

const totalPages = computed(() =>
  Math.ceil(filteredBrands.value.length / pageSize.value),
);

const paginatedBrands = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredBrands.value.slice(start, end);
});

const imageFile = ref(null);

function resetForm() {
  form.value = {
    name: "",
    slug: "",
    logoUrl: "",
  };
  errors.value = {};
  previewError.value = false;
  editingBrandId.value = null;
  modalError.value = "";
  imageFile.value = null;
}

function openCreateModal() {
  resetForm();
  showModal.value = true;
}

function handleImageUpload(event) {
  const file = event.target?.files?.[0];
  if (!file) return;
  imageFile.value = file;
  const reader = new FileReader();
  reader.onload = (e) => {
    form.value.logoUrl = e.target?.result || "";
  };
  reader.readAsDataURL(file);
}

async function uploadImageDirect(file) {
  const formData = new FormData();
  formData.append("file", file);
  const res = await axios.post(`${import.meta.env.VITE_API_URL || "http://localhost:8080/api"}/upload/image`, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return res.data?.url || res.data?.imageUrl || "";
}

function openEditModal(brand) {
  resetForm();
  editingBrandId.value = brand.id;
  form.value = {
    name: brand.name || "",
    slug: brand.slugDisplay || "",
    logoUrl: brand.logoUrl || "",
  };
  showModal.value = true;
}

function closeModal() {
  showModal.value = false;
  resetForm();
}

function validateForm() {
  errors.value = {};

  if (!form.value.name || form.value.name.trim() === "") {
    errors.value.name = "Vui lòng nhập tên thương hiệu";
    return false;
  }

  if (form.value.name.length > 100) {
    errors.value.name = "Tên thương hiệu không được vượt quá 100 ký tự";
    return false;
  }

  return true;
}

function generateSlug(name) {
  if (!name) return "";
  return name
    .toLowerCase()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/^-+|-+$/g, "");
}

function pickBrandSlug(source) {
  const item = source && typeof source === "object" ? source : {};
  return (
    item.slug ??
    item.slugName ??
    item.slug_name ??
    item.seoSlug ??
    item.seo_slug ??
    ""
  );
}

function normalizeBrandRecord(source) {
  const item = source && typeof source === "object" ? source : {};
  const name = String(item.name || "").trim();
  const slugValue = String(pickBrandSlug(item) || "").trim();

  return {
    ...item,
    name,
    slugDisplay: slugValue || (name ? generateSlug(name) : ""),
    logoUrl:
      item.logoUrl || item.logo_url
        ? resolveImageUrl(item.logoUrl || item.logo_url)
        : null,
  };
}

// Watch name to auto-generate slug
watch(
  () => form.value.name,
  (newName, oldName) => {
    if (!form.value.slug || form.value.slug === generateSlug(oldName || "")) {
      form.value.slug = generateSlug(newName);
    }
  },
);

function requestSave() {
  if (!validateForm()) return;
  const isEdit = !!editingBrandId.value;
  requestConfirm({
    title: isEdit ? "Xác nhận cập nhật" : "Xác nhận thêm mới",
    message: isEdit
      ? `Bạn chắc chắn muốn cập nhật thương hiệu "${form.value.name}"?`
      : `Bạn chắc chắn muốn thêm thương hiệu "${form.value.name}"?`,
    confirmText: isEdit ? "Cập nhật" : "Thêm mới",
    action: submitForm,
  });
}

async function submitForm() {
  if (!validateForm()) return;

  saving.value = true;
  modalError.value = "";

  try {
    let finalLogoUrl = form.value.logoUrl || null;
    if (imageFile.value) {
      finalLogoUrl = await uploadImageDirect(imageFile.value);
    }

    const payload = {
      name: form.value.name.trim(),
      slug: form.value.slug?.trim() || generateSlug(form.value.name),
      logoUrl: finalLogoUrl,
    };

    const isEdit = !!editingBrandId.value;

    if (editingBrandId.value) {
      await brandsApi.update(editingBrandId.value, payload);
    } else {
      await brandsApi.create(payload);
    }

    closeModal();
    await fetchBrands();
    showToast(isEdit ? "Cập nhật thương hiệu thành công!" : "Thêm thương hiệu thành công!");
  } catch (err) {
    console.error("Lỗi khi lưu thương hiệu:", err);
    modalError.value =
      err?.message ||
      (editingBrandId.value
        ? "Không thể cập nhật thương hiệu"
        : "Không thể thêm thương hiệu");
  } finally {
    saving.value = false;
  }
}

function requestDelete(brand) {
  requestConfirm({
    title: "Xác nhận xóa thương hiệu",
    message: `Bạn chắc chắn muốn xóa thương hiệu "${brand.name || "Thương hiệu"}" (#${brand.id})?`,
    confirmText: "Xóa",
    danger: true,
    action: () => deleteBrand(brand),
  });
}

async function deleteBrand(brand) {
  if (!brand) return;

  try {
    const result = await adminApi.deleteBrand(brand.id);

    if (result && result.softDeleted) {
      if (
        !hiddenBrands.value.some(
          (item) => Number(item.id) === Number(brand.id),
        )
      ) {
        hiddenBrands.value.push(brand);
      }
      showToast(`Đã xóa thương hiệu "${brand.name || 'Thương hiệu'}".`);
      return;
    }

    await fetchBrands();
    showToast(`Đã xóa thương hiệu "${brand.name || 'Thương hiệu'}".`);
  } catch (err) {
    console.error("Lỗi khi xóa thương hiệu:", err);
    let errorMsg = "Không thể xóa thương hiệu";
    if (err.message && err.message.includes("foreign key")) {
      errorMsg = "Không thể xóa thương hiệu vì đang có sản phẩm liên quan";
    } else if (err.message) {
      errorMsg += ": " + err.message;
    }
    error.value = errorMsg;
    setTimeout(() => {
      error.value = "";
    }, 3000);
  }
}

function requestRestore(brand) {
  requestConfirm({
    title: "Xác nhận khôi phục",
    message: `Bạn chắc chắn muốn khôi phục thương hiệu "${brand.name || "Thương hiệu"}"?`,
    confirmText: "Khôi phục",
    action: () => restoreBrand(brand),
  });
}

function requestRestoreAll() {
  requestConfirm({
    title: "Xác nhận khôi phục",
    message: "Bạn chắc chắn muốn khôi phục tất cả thương hiệu đang ẩn?",
    confirmText: "Khôi phục tất cả",
    action: restoreAllHiddenBrands,
  });
}

async function fetchBrands() {
  loading.value = true;
  error.value = "";

  try {
    const response = await brandsApi.getAll();
    let brandList = [];

    if (Array.isArray(response)) {
      brandList = response;
    } else if (
      response &&
      response.content &&
      Array.isArray(response.content)
    ) {
      brandList = response.content;
    } else if (response && response.data && Array.isArray(response.data)) {
      brandList = response.data;
    }

    brands.value = brandList.map(normalizeBrandRecord);
  } catch (err) {
    console.error("Lỗi khi tải danh sách thương hiệu:", err);
    error.value =
      "Không thể tải danh sách thương hiệu. Vui lòng kiểm tra backend API.";
  } finally {
    loading.value = false;
  }
}

function handleImageError(e) {
  previewError.value = true;
  e.target.style.display = "none";
}

async function restoreBrand(brand) {
  try {
    await brandsApi.restoreBrand(brand.id);
    const restoredId = Number(brand?.id);
    hiddenBrands.value = hiddenBrands.value.filter(
      (hidden) => Number(hidden.id) !== restoredId,
    );
    await fetchBrands();
    showToast(`Đã khôi phục thương hiệu "${brand.name || 'Thương hiệu'}".`);
  } catch (err) {
    error.value = "Không thể khôi phục thương hiệu.";
  }
}

async function restoreAllHiddenBrands() {
  try {
    for (const brand of hiddenBrands.value) {
      await brandsApi.restoreBrand(brand.id);
    }
    hiddenBrands.value = [];
    await fetchBrands();
    showToast("Đã khôi phục tất cả thương hiệu thành công!");
  } catch (err) {
    error.value = "Có lỗi xảy ra khi khôi phục.";
  }
}

// Watch search to reset page
watch(searchQuery, () => {
  currentPage.value = 1;
});

// Lifecycle
onMounted(() => {
  fetchBrands();
});
</script>

<style scoped>
.retail-card {
  background: white;
  border-radius: 1rem;
  border: 1px solid #e2e8f0;
  transition: all 0.2s ease;
}

.dark .retail-card {
  background: #1a1a1a;
  border-color: #2d2d2d;
}

.material-symbols-outlined {
  font-size: 20px;
}
</style>
