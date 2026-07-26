<template>
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
          v-if="hiddenBrands.length > 0"
          class="mb-4 rounded-xl border border-amber-200 bg-amber-50 px-3 py-3 text-sm text-slate-700 dark:border-amber-900/20 dark:bg-[#3f2e11] dark:text-[#fbe7a1]"
        >
          <div class="flex flex-wrap items-center justify-between gap-3">
            <div>
              <p class="font-semibold">Thương hiệu ẩn tạm</p>
              <p class="text-xs text-slate-500 dark:text-[#e1c797]">
                Những thương hiệu đã xóa mềm sẽ tạm ẩn khỏi danh sách được dùng.
                Bạn có thể khôi phục lại khi cần.
              </p>
            </div>
            <button
              type="button"
              class="rounded-xl bg-primary px-3 py-2 text-xs font-semibold text-white hover:opacity-90"
              @click="restoreAllHiddenBrands"
            >
              Khôi phục tất cả
            </button>
          </div>
          <div class="mt-3 space-y-2">
            <div
              v-for="brand in hiddenBrands"
              :key="brand.id"
              class="flex flex-wrap items-center justify-between gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2 dark:border-[#4c4138] dark:bg-[#1e1812]"
            >
              <div class="text-sm">
                <span class="font-semibold">#{{ brand.id }}</span>
                <span class="ml-2">{{ brand.name || "Thương hiệu" }}</span>
              </div>
              <button
                type="button"
                class="rounded-xl bg-emerald-500 px-3 py-2 text-xs font-semibold text-white hover:bg-emerald-600"
                @click="restoreBrand(brand)"
              >
                Khôi phục
              </button>
            </div>
          </div>
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
                      @click="confirmDelete(brand)"
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
          <h3 class="mb-4 text-base font-bold">
            {{
              editingBrandId
                ? `Sửa thương hiệu #${editingBrandId}`
                : "Thêm thương hiệu mới"
            }}
          </h3>

          <div class="grid gap-3">
            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Tên thương hiệu *</label
              >
              <input
                v-model.trim="form.name"
                type="text"
                class="w-full rounded-xl border border-slate-200 bg-white px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                placeholder="VD: Nike, Adidas, Puma..."
                :class="{ 'border-red-500': errors.name }"
              />
              <p v-if="errors.name" class="mt-1 text-xs text-red-500">
                {{ errors.name }}
              </p>
            </div>

            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Slug (URL thân thiện)</label
              >
              <input
                v-model.trim="form.slug"
                type="text"
                class="w-full rounded-xl border border-slate-200 bg-white px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                placeholder="tu-khoa-url-thuong-hieu"
              />
              <p class="mt-1 text-xs text-slate-400">
                Để trống để tự động tạo từ tên thương hiệu
              </p>
            </div>

            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >URL Logo</label
              >
              <input
                v-model.trim="form.logoUrl"
                type="text"
                class="w-full rounded-xl border border-slate-200 bg-white px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                placeholder="https://example.com/logo.png"
              />
            </div>

            <!-- Preview logo -->
            <div
              v-if="form.logoUrl"
              class="rounded-xl bg-slate-50 p-3 dark:bg-[#2b241f]"
            >
              <label class="mb-2 block text-xs font-medium text-slate-500"
                >Xem trước logo</label
              >
              <div class="flex items-center gap-3">
                <div
                  class="flex h-16 w-16 items-center justify-center overflow-hidden rounded-lg bg-white p-1 shadow-sm"
                >
                  <img
                    :src="form.logoUrl"
                    alt="Preview"
                    class="h-full w-full object-contain"
                    @error="previewError = true"
                  />
                </div>
                <span v-if="previewError" class="text-xs text-red-500"
                  >Không thể tải ảnh</span
                >
              </div>
            </div>
          </div>

          <p v-if="modalError" class="mt-3 text-xs text-red-500">
            {{ modalError }}
          </p>

          <div class="mt-4 flex justify-end gap-2">
            <button
              type="button"
              class="rounded-xl border border-slate-200 px-4 py-2 text-sm font-medium hover:border-slate-400 dark:border-[#3c342e]"
              @click="closeModal"
            >
              Hủy
            </button>

            <button
              type="button"
              :disabled="saving"
              class="rounded-xl bg-primary px-4 py-2 text-sm font-medium text-white hover:opacity-90 disabled:opacity-60"
              @click="submitForm"
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

    <!-- Modal xác nhận xóa -->
    <Teleport to="body">
      <div
        v-if="deleteTarget"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4"
        @click.self="deleteTarget = null"
      >
        <div
          class="w-full max-w-sm rounded-2xl bg-white p-6 shadow-2xl dark:bg-[#1f1a17]"
        >
          <h3 class="mb-2 text-base font-bold">Xác nhận xóa</h3>
          <p class="text-sm text-slate-500 dark:text-[#b9aa9a]">
            Bạn chắc chắn muốn xóa thương hiệu
            <strong>#{{ deleteTarget.id }}</strong> ({{
              deleteTarget.name || "Thương hiệu"
            }})?
          </p>
          <p class="mt-2 text-xs text-red-500">
            Lưu ý: Sẽ không thể xóa nếu có sản phẩm đang sử dụng thương hiệu
            này.
          </p>

          <div class="mt-5 flex justify-end gap-3">
            <button
              class="rounded-xl border border-slate-200 px-4 py-2 text-sm font-medium dark:border-[#3c342e]"
              @click="deleteTarget = null"
            >
              Hủy
            </button>
            <button
              :disabled="deleting"
              class="rounded-xl bg-red-500 px-4 py-2 text-sm font-medium text-white hover:bg-red-600 disabled:opacity-60"
              @click="deleteBrand"
            >
              {{ deleting ? "Đang xóa..." : "Xóa" }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>
  </AdminShell>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import AdminShell from "@/components/admin/AdminShell.vue";
import { brandsApi, resolveImageUrl } from "@/services/api";

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

const visibleBrands = computed(() => {
  return brands.value.filter(
    (brand) =>
      !hiddenBrands.value.some(
        (hidden) => Number(brand.id) === Number(hidden.id),
      ),
  );
});

// Delete state
const deleteTarget = ref(null);
const deleting = ref(false);

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

// Methods
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
}

function openCreateModal() {
  resetForm();
  showModal.value = true;
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

async function submitForm() {
  if (!validateForm()) return;

  saving.value = true;
  modalError.value = "";

  try {
    const payload = {
      name: form.value.name.trim(),
      slug: form.value.slug?.trim() || generateSlug(form.value.name),
      logoUrl: form.value.logoUrl || null,
    };

    if (editingBrandId.value) {
      await brandsApi.update(editingBrandId.value, payload);
    } else {
      await brandsApi.create(payload);
    }

    closeModal();
    await fetchBrands();
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

function confirmDelete(brand) {
  deleteTarget.value = brand;
}

async function deleteBrand() {
  if (!deleteTarget.value) return;

  deleting.value = true;
  try {
    const result = await adminApi.deleteBrand(deleteTarget.value.id);

    if (result && result.softDeleted) {
      if (
        !hiddenBrands.value.some(
          (brand) => Number(brand.id) === Number(deleteTarget.value.id),
        )
      ) {
        hiddenBrands.value.push(deleteTarget.value);
      }
      deleteTarget.value = null;
      return;
    }

    deleteTarget.value = null;
    await fetchBrands();
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
    deleteTarget.value = null;
  } finally {
    deleting.value = false;
  }
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
