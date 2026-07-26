<template>
  <AdminShell>
    <template #header>
      <div class="flex flex-wrap items-center justify-between gap-2">
        <div>
          <p class="text-xs uppercase tracking-[0.18em] text-primary">
            Quản lý danh mục
          </p>
          <h2 class="mt-1 text-xl font-bold">Quản lý sản phẩm</h2>
        </div>

        <div class="flex flex-wrap items-center gap-2">
          <button
            type="button"
            class="inline-flex items-center gap-1.5 rounded-xl bg-primary px-3 py-1.5 text-sm font-semibold text-white hover:opacity-90"
            @click="openCreate"
          >
            <span class="material-symbols-outlined text-[18px]">add</span>
            Thêm sản phẩm
          </button>

          <button
            type="button"
            class="inline-flex items-center gap-1.5 rounded-xl border border-slate-200 bg-white px-3 py-1.5 text-sm font-medium hover:border-primary hover:text-primary dark:border-[#3c342e] dark:bg-[#1f1a17]"
            @click="loadProducts"
          >
            <span class="material-symbols-outlined text-[18px]">refresh</span>
            Làm mới
          </button>
        </div>
      </div>
    </template>

    <div class="space-y-3">
      <section class="retail-card p-3">
        <div class="grid gap-3 lg:grid-cols-3">
          <label
            class="flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-1.5 dark:border-[#3c342e] dark:bg-[#1f1a17]"
          >
            <span class="material-symbols-outlined text-[18px] text-slate-400"
              >search</span
            >
            <input
              v-model="search"
              type="text"
              placeholder="Tìm theo tên, mã sản phẩm, thương hiệu"
              class="w-full bg-transparent text-sm outline-none"
            />
          </label>

          <select
            v-model.number="filterBrandId"
            class="rounded-xl border border-slate-200 bg-white px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#1f1a17]"
          >
            <option :value="null">Tất cả thương hiệu</option>
            <option
              v-for="brand in brands"
              :key="brand.id"
              :value="Number(brand.id)"
            >
              {{ brand.name }}
            </option>
          </select>

          <select
            v-model.number="filterCategoryId"
            class="rounded-xl border border-slate-200 bg-white px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#1f1a17]"
          >
            <option :value="null">Tất cả danh mục</option>
            <option
              v-for="category in categories"
              :key="category.id"
              :value="Number(category.id)"
            >
              {{ category.name }}
            </option>
          </select>
        </div>
      </section>

      <section class="retail-card p-3">
        <div
          v-if="error"
          class="mb-4 rounded-xl border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700 dark:border-red-900/40 dark:bg-red-900/20 dark:text-red-300"
        >
          {{ error }}
        </div>

        <div
          v-if="hiddenProducts.length > 0"
          class="mb-4 rounded-xl border border-amber-200 bg-amber-50 px-3 py-3 text-sm text-slate-700 dark:border-amber-900/20 dark:bg-[#3f2e11] dark:text-[#fbe7a1]"
        >
          <div class="flex flex-wrap items-center justify-between gap-3">
            <div>
              <p class="font-semibold">Sản phẩm ẩn tạm</p>
              <p class="text-xs text-slate-500 dark:text-[#e1c797]">
                Những sản phẩm đã xóa mềm sẽ tạm ẩn khỏi danh sách được dùng.
                Bạn có thể khôi phục lại khi cần.
              </p>
            </div>
            <button
              type="button"
              class="rounded-xl bg-primary px-3 py-2 text-xs font-semibold text-white hover:opacity-90"
              @click="restoreAllHiddenProducts"
            >
              Khôi phục tất cả
            </button>
          </div>
          <div class="mt-3 space-y-2">
            <div
              v-for="product in hiddenProducts"
              :key="product.id"
              class="flex flex-wrap items-center justify-between gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2 dark:border-[#4c4138] dark:bg-[#1e1812]"
            >
              <div class="text-sm">
                <span class="font-semibold">#{{ product.id }}</span>
                <span class="ml-2">{{ product.name || "Sản phẩm" }}</span>
              </div>
              <button
                type="button"
                class="rounded-xl bg-emerald-500 px-3 py-2 text-xs font-semibold text-white hover:bg-emerald-600"
                @click="restoreProduct(product)"
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
          Đang tải danh sách sản phẩm...
        </div>

        <div
          v-else-if="filteredProducts.length === 0"
          class="rounded-2xl border border-dashed border-slate-300 p-6 text-center text-sm text-slate-500 dark:border-[#3c342e] dark:text-[#b9aa9a]"
        >
          Không tìm thấy sản phẩm.
        </div>

        <div v-else class="overflow-x-auto">
          <table class="w-full min-w-[900px] text-left text-xs">
            <thead>
              <tr
                class="border-b border-slate-200 text-slate-500 dark:border-[#3c342e] dark:text-[#b9aa9a]"
              >
                <th class="px-2 py-2 font-medium">STT</th>
                <th class="px-2 py-2 font-medium">Sản phẩm</th>
                <th class="px-2 py-2 font-medium">Mã</th>
                <th class="px-2 py-2 font-medium">Thương hiệu</th>
                <th class="px-2 py-2 font-medium">Danh mục</th>
                <th class="px-2 py-2 text-right font-medium">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="(product, index) in paginatedProducts"
                :key="product.id"
                class="border-b border-slate-100 last:border-none dark:border-[#2b241f]"
              >
                <td
                  class="px-2 py-2 font-semibold text-slate-500 dark:text-[#b9aa9a]"
                >
                  {{ (productsPage - 1) * 10 + index + 1 }}
                </td>

                <td class="px-2 py-2">
                  <div class="flex items-center gap-3">
                    <div
                      class="size-12 overflow-hidden rounded-xl bg-slate-100 dark:bg-[#2b241f]"
                    >
                      <img
                        v-if="product.imageUrl"
                        :src="product.imageUrl"
                        alt="Product"
                        class="h-full w-full object-cover"
                      />
                      <span
                        v-else
                        class="flex h-full items-center justify-center text-xs text-slate-400"
                      ></span>
                    </div>
                    <div>
                      <p class="font-semibold">
                        {{ product.name || "Sản phẩm" }}
                      </p>
                      <p class="text-xs text-slate-500 dark:text-[#b9aa9a]">
                        ID: {{ product.id }}
                      </p>
                    </div>
                  </div>
                </td>

                <td class="px-2 py-2">{{ product.productCode || "-" }}</td>
                <td class="px-2 py-2">{{ brandName(product.brandId) }}</td>
                <td class="px-2 py-2">
                  {{ categoryName(product.categoryId) }}
                </td>
                <td class="px-2 py-2 text-right">
                  <div class="flex items-center justify-end gap-1">
                    <button
                      class="rounded-lg p-1.5 hover:bg-slate-100 dark:hover:bg-[#2b241f]"
                      title="Chỉnh sửa"
                      @click="openEdit(product)"
                    >
                      <span
                        class="material-symbols-outlined text-[18px] text-slate-500"
                        >edit</span
                      >
                    </button>

                    <button
                      class="rounded-lg p-1.5 hover:bg-red-50 dark:hover:bg-red-900/20"
                      title="Xóa"
                      @click="confirmDelete(product)"
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

        <PaginationBar
          :current-page="productsPage"
          :total-pages="productsTotalPages"
          :page-start="productsPageStart"
          :page-end="productsPageEnd"
          :total-items="productsTotalItems"
          label="sản phẩm"
          @previous="productsPreviousPage"
          @next="productsNextPage"
        />
      </section>
    </div>

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
              editingProductId
                ? `Sửa sản phẩm #${editingProductId}`
                : "Thêm sản phẩm mới"
            }}
          </h3>

          <div class="grid gap-3 md:grid-cols-2">
            <div class="md:col-span-2">
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Tên sản phẩm *</label
              >
              <input
                v-model.trim="form.name"
                type="text"
                class="w-full rounded-xl border border-slate-200 bg-white px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                placeholder="Nhập tên sản phẩm"
              />
            </div>

            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Mã sản phẩm</label
              >
              <input
                v-model.trim="form.productCode"
                type="text"
                class="w-full rounded-xl border border-slate-200 bg-white px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                placeholder="VD: SNK-001"
              />
            </div>

            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Thương hiệu</label
              >
              <select
                v-model.number="form.brandId"
                class="w-full rounded-xl border border-slate-200 bg-white px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
              >
                <option :value="null">-- Chọn thương hiệu --</option>
                <option
                  v-for="brand in brands"
                  :key="brand.id"
                  :value="Number(brand.id)"
                >
                  {{ brand.name }}
                </option>
              </select>
            </div>

            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Danh mục</label
              >
              <select
                v-model.number="form.categoryId"
                class="w-full rounded-xl border border-slate-200 bg-white px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
              >
                <option :value="null">-- Chọn danh mục --</option>
                <option
                  v-for="category in categories"
                  :key="category.id"
                  :value="Number(category.id)"
                >
                  {{ category.name }}
                </option>
              </select>
            </div>

            <div class="md:col-span-2">
              <label class="mb-3 block text-xs font-medium text-slate-500"
                >Ảnh sản phẩm</label
              >

              <!-- Image Preview -->
              <div v-if="form.imageUrl" class="mb-4 flex items-end gap-3">
                <div
                  class="size-20 overflow-hidden rounded-lg border border-slate-200 dark:border-[#3c342e]"
                >
                  <img
                    :src="form.imageUrl"
                    alt="Preview"
                    class="h-full w-full object-cover"
                  />
                </div>
                <button
                  type="button"
                  class="rounded-lg bg-red-100 px-2.5 py-1.5 text-xs font-medium text-red-600 hover:bg-red-200 dark:bg-red-900/30 dark:text-red-400"
                  @click="clearProductImage"
                >
                  Xóa ảnh
                </button>
              </div>

              <!-- Upload Status -->
              <div
                v-if="productUploadStatus === 'uploading'"
                class="mb-4 flex items-center gap-2 text-sm text-blue-600 dark:text-blue-400"
              >
                <span
                  class="h-4 w-4 animate-spin rounded-full border-2 border-blue-600 border-t-transparent"
                />
                Đang tải lên...
              </div>
              <div
                v-else-if="productUploadError"
                class="mb-4 rounded-lg border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-600 dark:border-red-900/40 dark:bg-red-900/20 dark:text-red-400"
              >
                {{ productUploadError }}
              </div>

              <!-- File Upload Input (Hidden) -->
              <input
                ref="productImageInput"
                type="file"
                accept="image/*"
                class="hidden"
                @change="handleProductImageUpload"
              />

              <!-- Upload Button -->
              <button
                type="button"
                :disabled="productUploadStatus === 'uploading'"
                class="flex w-full items-center justify-center gap-2 rounded-xl border-2 border-dashed border-slate-300 bg-slate-50 px-3 py-2 text-sm font-medium text-slate-600 transition-colors hover:border-primary hover:bg-primary/5 hover:text-primary disabled:opacity-50 disabled:cursor-not-allowed dark:border-[#3c342e] dark:bg-[#0f0d0a] dark:text-[#b9aa9a]"
                @click="$refs.productImageInput?.click()"
              >
                <span class="material-symbols-outlined text-[20px]">image</span>
                Chọn ảnh từ máy tính
              </button>
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
              @click="saveProduct"
            >
              {{
                saving
                  ? "Đang lưu..."
                  : editingProductId
                    ? "Cập nhật"
                    : "Thêm mới"
              }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>

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
            Bạn chắc chắn muốn xóa sản phẩm
            <strong>#{{ deleteTarget.id }}</strong> ({{
              deleteTarget.name || "Sản phẩm"
            }})?
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
              @click="doDelete"
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
import { computed, onMounted, ref } from "vue";
import axios from "axios";
import AdminShell from "@/components/admin/AdminShell.vue";
import PaginationBar from "@/components/PaginationBar.vue";
import { usePagination } from "@/composables/usePagination";
import { adminApi, brandsApi, categoriesApi } from "@/services/api";

const loading = ref(false);
const error = ref("");
const search = ref("");
const filterBrandId = ref(null);
const filterCategoryId = ref(null);
const products = ref([]);
const brands = ref([]);
const categories = ref([]);
const hiddenProducts = ref([]);

const showModal = ref(false);
const editingProductId = ref(null);
const saving = ref(false);
const modalError = ref("");
const form = ref(emptyForm());

const deleteTarget = ref(null);
const deleting = ref(false);

const visibleProducts = computed(() => {
  return products.value.filter(
    (product) =>
      !hiddenProducts.value.some(
        (hidden) => Number(product.id) === Number(hidden.id),
      ),
  );
});

const productImageInput = ref(null);
const productUploadStatus = ref("idle"); // idle, uploading, success, error
const productUploadError = ref("");
const productImageFile = ref(null);

const API_BASE_URL =
  import.meta.env.VITE_API_URL || "http://localhost:8080/api";

const brandMap = computed(() => {
  return brands.value.reduce((acc, item) => {
    const id = Number(item.id);
    if (Number.isFinite(id)) {
      acc[id] = item.name;
    }
    return acc;
  }, {});
});

const categoryMap = computed(() => {
  return categories.value.reduce((acc, item) => {
    const id = Number(item.id);
    if (Number.isFinite(id)) {
      acc[id] = item.name;
    }
    return acc;
  }, {});
});

const filteredProducts = computed(() => {
  const keyword = search.value.trim().toLowerCase();
  const brandId = toNullableId(filterBrandId.value);
  const categoryId = toNullableId(filterCategoryId.value);

  return visibleProducts.value.filter((product) => {
    if (brandId !== null && Number(product.brandId) !== brandId) {
      return false;
    }

    if (categoryId !== null && Number(product.categoryId) !== categoryId) {
      return false;
    }

    if (!keyword) {
      return true;
    }

    const haystack = [
      product.name,
      product.productCode,
      brandName(product.brandId),
      categoryName(product.categoryId),
      product.id,
    ]
      .filter((item) => item !== undefined && item !== null)
      .join(" ")
      .toLowerCase();

    return haystack.includes(keyword);
  });
});

const {
  currentPage: productsPage,
  totalPages: productsTotalPages,
  paginatedItems: paginatedProducts,
  pageStart: productsPageStart,
  pageEnd: productsPageEnd,
  totalItems: productsTotalItems,
  previousPage: productsPreviousPage,
  nextPage: productsNextPage,
} = usePagination(filteredProducts, 10);

function emptyForm() {
  return {
    name: "",
    productCode: "",
    brandId: null,
    categoryId: null,
    imageUrl: "",
  };
}

function toList(payload) {
  if (Array.isArray(payload)) {
    return payload;
  }
  if (payload && typeof payload === "object") {
    if (Array.isArray(payload.content)) return payload.content;
    if (Array.isArray(payload.list)) return payload.list;
    if (Array.isArray(payload.data)) return payload.data;
  }
  return [];
}

function toNullableId(value) {
  if (
    value === null ||
    value === undefined ||
    value === "" ||
    value === "null"
  ) {
    return null;
  }
  const id = Number(value);
  return Number.isFinite(id) ? id : null;
}

function brandName(brandId) {
  const id = Number(brandId);
  if (!Number.isFinite(id)) return "-";
  return brandMap.value[id] || `Brand #${id}`;
}

function categoryName(categoryId) {
  const id = Number(categoryId);
  if (!Number.isFinite(id)) return "-";
  return categoryMap.value[id] || `Category #${id}`;
}

function openCreate() {
  editingProductId.value = null;
  form.value = emptyForm();
  modalError.value = "";
  showModal.value = true;
}

function openEdit(product) {
  editingProductId.value = product.id;
  form.value = {
    name: product.name || "",
    productCode: product.productCode || "",
    brandId: Number.isFinite(Number(product.brandId))
      ? Number(product.brandId)
      : null,
    categoryId: Number.isFinite(Number(product.categoryId))
      ? Number(product.categoryId)
      : null,
    imageUrl: product.imageUrl || "",
  };
  modalError.value = "";
  showModal.value = true;
}

function closeModal() {
  showModal.value = false;
}

async function saveProduct() {
  const f = form.value;
  modalError.value = "";

  if (!f.name) {
    modalError.value = "Vui lòng nhập tên sản phẩm.";
    return;
  }

  saving.value = true;
  try {
    let imageUrl = f.imageUrl;
    let productIdForUpload = editingProductId.value;

    // If creating new product, create it first
    if (!editingProductId.value) {
      const payload = {
        name: f.name,
        productCode: f.productCode || null,
        brandId: Number.isFinite(Number(f.brandId)) ? Number(f.brandId) : null,
        categoryId: Number.isFinite(Number(f.categoryId))
          ? Number(f.categoryId)
          : null,
        imageUrl: null, // Don't set image URL yet
      };

      const createdProduct = await adminApi.createProduct(payload);
      productIdForUpload = createdProduct.id || createdProduct.data?.id;
    }

    // Upload image if there's a file selected
    if (productImageFile.value && productIdForUpload) {
      try {
        imageUrl = await uploadProductImage(productIdForUpload);
      } catch (uploadErr) {
        // Image upload failed, but if it's a new product, we already created it
        // For editing, we'll just warn the user
        console.warn("Image upload failed:", uploadErr);
        modalError.value = "Tải ảnh lên thất bại, nhưng sản phẩm đã được lưu.";
      }
    }

    // Update product with final image URL if editing or if we uploaded new image
    if (editingProductId.value || (imageUrl && imageUrl !== f.imageUrl)) {
      const updatePayload = {
        name: f.name,
        productCode: f.productCode || null,
        brandId: Number.isFinite(Number(f.brandId)) ? Number(f.brandId) : null,
        categoryId: Number.isFinite(Number(f.categoryId))
          ? Number(f.categoryId)
          : null,
        imageUrl: imageUrl || null,
      };

      await adminApi.updateProduct(
        editingProductId.value || productIdForUpload,
        updatePayload,
      );
    }

    closeModal();
    await loadProducts();
  } catch (err) {
    modalError.value = err?.message || "Không thể lưu sản phẩm.";
  } finally {
    saving.value = false;
  }
}

function confirmDelete(product) {
  deleteTarget.value = product;
}

async function doDelete() {
  if (!deleteTarget.value) {
    return;
  }

  deleting.value = true;
  try {
    const result = await adminApi.deleteProduct(deleteTarget.value.id);

    if (result && result.softDeleted) {
      if (
        !hiddenProducts.value.some(
          (product) => Number(product.id) === Number(deleteTarget.value.id),
        )
      ) {
        hiddenProducts.value.push(deleteTarget.value);
      }
      deleteTarget.value = null;
      return;
    }

    deleteTarget.value = null;
    await loadProducts();
  } catch (err) {
    error.value = err?.message || "Không thể xóa sản phẩm.";
  } finally {
    deleting.value = false;
  }
}

async function restoreProduct(product) {
  try {
    await adminApi.restoreProduct(product.id);
    const restoredId = Number(product?.id);
    hiddenProducts.value = hiddenProducts.value.filter(
      (hidden) => Number(hidden.id) !== restoredId,
    );
    await loadProducts();
  } catch (err) {
    error.value = "Không thể khôi phục sản phẩm.";
  }
}

async function restoreAllHiddenProducts() {
  try {
    for (const product of hiddenProducts.value) {
      await adminApi.restoreProduct(product.id);
    }
    hiddenProducts.value = [];
    await loadProducts();
  } catch (err) {
    error.value = "Có lỗi xảy ra khi khôi phục.";
  }
}

async function loadProducts() {
  loading.value = true;
  error.value = "";

  try {
    const [productList, brandList, categoryList] = await Promise.all([
      adminApi.getProducts({ page: 0, size: 300 }),
      brandsApi.getAll().catch(() => []),
      categoriesApi.getAll().catch(() => []),
    ]);

    products.value = toList(productList);
    brands.value = toList(brandList);
    categories.value = toList(categoryList);
  } catch (err) {
    console.error("Failed to load admin products:", err);
    error.value = "Không thể tải sản phẩm. Vui lòng kiểm tra backend API.";
  } finally {
    loading.value = false;
  }
}

function clearProductImage() {
  form.value.imageUrl = "";
  productImageFile.value = null;
  productUploadStatus.value = "idle";
  productUploadError.value = "";
}

async function uploadProductImage(productId) {
  if (!productImageFile.value) {
    return form.value.imageUrl; // Return existing preview/URL if no new file
  }

  try {
    productUploadStatus.value = "uploading";
    productUploadError.value = "";

    const formData = new FormData();
    formData.append("file", productImageFile.value);

    const response = await axios.post(
      `${API_BASE_URL}/products/${productId}/upload-image`,
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      },
    );

    if (response.data && response.data.imageUrl) {
      form.value.imageUrl = response.data.imageUrl;
      productUploadStatus.value = "success";
      productImageFile.value = null;
      return response.data.imageUrl;
    } else {
      throw new Error("Máy chủ không trả về đường dẫn ảnh");
    }
  } catch (err) {
    console.error("Error uploading product image:", err);
    productUploadStatus.value = "error";
    productUploadError.value =
      "Tải ảnh lên thất bại: " + (err.response?.data?.error || err.message);
    throw new Error(productUploadError.value);
  }
}

function handleProductImageUpload(event) {
  const file = event.target?.files?.[0];
  if (!file) return;

  // Validate file type
  if (!file.type.startsWith("image/")) {
    productUploadError.value = "Vui lòng chọn tệp ảnh hợp lệ.";
    productUploadStatus.value = "error";
    return;
  }

  // Validate file size (max 5MB)
  if (file.size > 5 * 1024 * 1024) {
    productUploadError.value = "Kích thước ảnh không được vượt quá 5MB.";
    productUploadStatus.value = "error";
    return;
  }

  productImageFile.value = file;
  productUploadError.value = "";

  // Create preview
  const reader = new FileReader();
  reader.onload = (e) => {
    form.value.imageUrl = e.target?.result || "";
  };
  reader.readAsDataURL(file);

  // Reset input
  event.target.value = "";
}

onMounted(loadProducts);
</script>
