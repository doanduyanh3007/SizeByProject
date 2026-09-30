<template>
  <AdminShell>
    <template #header>
      <div class="flex flex-wrap items-center justify-between gap-3">
        <div>
          <p class="text-xs uppercase tracking-[0.18em] text-primary">Ưu đãi sản phẩm</p>
          <h2 class="mt-1 text-2xl font-bold">Quản lý khuyến mãi</h2>
          <p class="mt-1 text-sm text-slate-500">
            Tạo chương trình giảm giá và chọn các sản phẩm được áp dụng.
          </p>
        </div>
        <button
          type="button"
          class="inline-flex items-center gap-2 rounded-xl bg-primary px-4 py-2.5 text-sm font-semibold text-white hover:opacity-90"
          @click="openCreate"
        >
          <span class="material-symbols-outlined text-[18px]">add</span>
          Thêm khuyến mãi
        </button>
      </div>
    </template>

    <section class="retail-card overflow-hidden">
      <div class="flex flex-wrap items-center gap-3 border-b border-slate-200 p-4">
        <label class="flex min-w-[260px] flex-1 items-center gap-2 rounded-xl border border-slate-200 px-3 py-2">
          <span class="material-symbols-outlined text-[18px] text-slate-400">search</span>
          <input
            v-model.trim="search"
            class="w-full bg-transparent text-sm outline-none"
            placeholder="Tìm theo tên khuyến mãi hoặc sản phẩm..."
          />
        </label>
        <select v-model="statusFilter" class="rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm">
          <option value="ALL">Tất cả trạng thái</option>
          <option value="ACTIVE">Đang diễn ra</option>
          <option value="SCHEDULED">Sắp diễn ra</option>
          <option value="EXPIRED">Đã kết thúc</option>
          <option value="INACTIVE">Đã tắt</option>
        </select>
        <button class="rounded-xl border border-slate-200 px-3 py-2 text-sm" type="button" @click="loadData">
          Làm mới
        </button>
      </div>

      <p v-if="error" class="m-4 rounded-xl border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
        {{ error }}
      </p>
      <div v-if="loading" class="p-10 text-center text-sm text-slate-500">Đang tải khuyến mãi...</div>
      <div v-else class="overflow-x-auto">
        <table class="w-full min-w-[980px] text-left text-sm">
          <thead class="border-b border-slate-200 bg-slate-50 text-xs uppercase tracking-wide text-slate-500">
            <tr>
              <th class="px-5 py-4">STT</th>
              <th class="px-5 py-4">Khuyến mãi</th>
              <th class="px-5 py-4">Mức giảm</th>
              <th class="px-5 py-4">Thời gian</th>
              <th class="px-5 py-4">Sản phẩm áp dụng</th>
              <th class="px-5 py-4">Trạng thái</th>
              <th class="px-5 py-4 text-right">Thao tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            <tr v-for="(promotion, index) in filteredPromotions" :key="promotion.id" class="hover:bg-slate-50">
              <td class="px-5 py-4 text-slate-500">{{ index + 1 }}</td>
              <td class="px-5 py-4">
                <p class="font-bold text-slate-900">{{ promotion.name }}</p>
                <p class="text-xs text-slate-500">ID: {{ promotion.id }}</p>
              </td>
              <td class="px-5 py-4 font-bold text-emerald-600">{{ Number(promotion.discountPercent) }}%</td>
              <td class="px-5 py-4 text-xs">
                <p>{{ formatDate(promotion.startDate) }}</p>
                <p class="mt-1 text-slate-500">đến {{ formatDate(promotion.endDate) }}</p>
              </td>
              <td class="max-w-[280px] px-5 py-4">
                <p class="truncate">{{ productSummary(promotion) }}</p>
                <p class="text-xs text-slate-500">{{ promotion.products?.length || 0 }} sản phẩm</p>
              </td>
              <td class="px-5 py-4">
                <span class="rounded-full px-2.5 py-1 text-xs font-semibold" :class="statusClass(promotion.status)">
                  {{ statusLabel(promotion.status) }}
                </span>
              </td>
              <td class="px-5 py-4">
                <div class="flex justify-end gap-1">
                  <button class="rounded-lg p-2 text-slate-500 hover:bg-slate-100 hover:text-primary" title="Sửa" @click="openEdit(promotion)">
                    <span class="material-symbols-outlined text-[19px]">edit</span>
                  </button>
                  <button class="rounded-lg p-2 text-red-500 hover:bg-red-50" title="Xóa" @click="requestRemovePromotion(promotion)">
                    <span class="material-symbols-outlined text-[19px]">delete</span>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="filteredPromotions.length === 0">
              <td colspan="7" class="px-5 py-12 text-center text-slate-500">Chưa có khuyến mãi phù hợp.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <Teleport to="body">
      <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4" @click.self="closeModal">
        <form class="max-h-[92vh] w-full max-w-2xl overflow-y-auto rounded-2xl bg-white p-6 shadow-2xl" @submit.prevent="requestSavePromotion">
          <div class="flex items-start justify-between">
            <div>
              <p class="text-xs uppercase tracking-wider text-primary">Khuyến mãi</p>
              <h3 class="text-xl font-bold">{{ editingId ? "Sửa khuyến mãi" : "Thêm khuyến mãi" }}</h3>
            </div>
            <button type="button" class="rounded-lg p-1 hover:bg-slate-100" @click="closeModal">
              <span class="material-symbols-outlined">close</span>
            </button>
          </div>

          <div class="mt-5 grid gap-4 sm:grid-cols-2">
            <label class="sm:col-span-2">
              <span class="mb-1 block text-sm font-medium">Tên chương trình *</span>
              <input v-model.trim="form.name" required maxlength="255" class="w-full rounded-xl border border-slate-900 px-3 py-2.5 outline-none focus:border-primary" />
            </label>
            <label>
              <span class="mb-1 block text-sm font-medium">Giảm giá (%) *</span>
              <input v-model.number="form.discountPercent" required type="number" min="1" max="100" step="0.01" class="w-full rounded-xl border border-slate-900 px-3 py-2.5 outline-none focus:border-primary" />
            </label>
            <label class="flex items-end gap-2 pb-2">
              <input v-model="form.isActive" type="checkbox" class="rounded border-slate-900 text-primary focus:ring-primary" />
              <span class="text-sm font-medium">Kích hoạt chương trình</span>
            </label>
            <label>
              <span class="mb-1 block text-sm font-medium">Bắt đầu *</span>
              <div class="relative">
                <input ref="startDateInput" v-model="form.startDate" required type="datetime-local" class="datetime-picker-input w-full rounded-xl border border-slate-900 py-2.5 pl-3 pr-11 outline-none focus:border-primary" />
                <button type="button" class="absolute right-2 top-1/2 inline-flex size-8 -translate-y-1/2 items-center justify-center rounded-lg text-primary hover:bg-blue-50" title="Chọn ngày giờ bắt đầu" @click="openDateTimePicker(startDateInput)">
                  <span class="material-symbols-outlined text-[20px]">calendar_month</span>
                </button>
              </div>
            </label>
            <label>
              <span class="mb-1 block text-sm font-medium">Kết thúc *</span>
              <div class="relative">
                <input ref="endDateInput" v-model="form.endDate" required type="datetime-local" class="datetime-picker-input w-full rounded-xl border border-slate-900 py-2.5 pl-3 pr-11 outline-none focus:border-primary" />
                <button type="button" class="absolute right-2 top-1/2 inline-flex size-8 -translate-y-1/2 items-center justify-center rounded-lg text-primary hover:bg-blue-50" title="Chọn ngày giờ kết thúc" @click="openDateTimePicker(endDateInput)">
                  <span class="material-symbols-outlined text-[20px]">calendar_month</span>
                </button>
              </div>
            </label>
          </div>

          <div class="mt-5">
            <div class="mb-3 flex items-center justify-between">
              <span class="text-sm font-medium">Sản phẩm áp dụng</span>
              <span class="text-xs text-slate-500">{{ form.productIds.length }} đã chọn</span>
            </div>
            <div class="mb-3 flex gap-1 rounded-xl bg-slate-100 p-1">
              <button type="button" class="flex-1 rounded-lg px-3 py-2 text-sm font-semibold transition" :class="productTab === 'available' ? 'bg-white text-primary shadow-sm' : 'text-slate-500'" @click="productTab = 'available'">
                Sản phẩm đang có
              </button>
              <button type="button" class="flex-1 rounded-lg px-3 py-2 text-sm font-semibold transition" :class="productTab === 'selected' ? 'bg-white text-primary shadow-sm' : 'text-slate-500'" @click="productTab = 'selected'">
                Đã chọn ({{ form.productIds.length }})
              </button>
            </div>
            <div v-if="productTab === 'available'" class="mb-2 flex gap-2">
              <input v-model.trim="productSearch" placeholder="Tìm trong sản phẩm đang có..." class="w-full rounded-xl border border-slate-900 px-3 py-2 text-sm outline-none focus:border-primary" />
              <button type="button" class="whitespace-nowrap rounded-xl border border-slate-200 px-3 py-2 text-xs font-semibold hover:border-primary hover:text-primary" @click="toggleAllProducts">
                {{ form.productIds.length === products.length ? "Bỏ chọn" : "Chọn tất cả" }}
              </button>
            </div>
            <div v-if="productTab === 'available'" class="grid max-h-64 gap-2 overflow-y-auto rounded-xl border border-slate-200 p-3 sm:grid-cols-2">
              <label v-for="product in filteredProducts" :key="product.id" class="flex cursor-pointer items-start gap-2 rounded-lg border border-transparent p-2 hover:border-slate-200 hover:bg-slate-50">
                <input v-model="form.productIds" type="checkbox" :value="Number(product.id)" class="mt-0.5 rounded border-slate-900 text-primary focus:ring-primary" />
                <span class="min-w-0"><span class="block truncate text-sm font-semibold">{{ product.name }}</span><span class="text-xs text-slate-500">{{ product.productCode || `ID: ${product.id}` }}</span></span>
              </label>
              <p v-if="filteredProducts.length === 0" class="col-span-full py-6 text-center text-sm text-slate-500">Không có sản phẩm phù hợp.</p>
            </div>
            <div v-else class="grid max-h-64 gap-2 overflow-y-auto rounded-xl border border-slate-200 p-3 sm:grid-cols-2">
              <label v-for="product in selectedProducts" :key="product.id" class="flex items-start gap-2 rounded-lg border border-emerald-200 bg-emerald-50 p-2">
                <input v-model="form.productIds" type="checkbox" :value="Number(product.id)" class="mt-0.5 rounded border-slate-900 text-primary focus:ring-primary" />
                <span class="min-w-0"><span class="block truncate text-sm font-semibold">{{ product.name }}</span><span class="text-xs text-slate-500">{{ product.productCode || `ID: ${product.id}` }}</span></span>
              </label>
              <p v-if="selectedProducts.length === 0" class="col-span-full py-6 text-center text-sm text-slate-500">Chưa chọn sản phẩm nào.</p>
            </div>
          </div>

          <p v-if="formError" class="mt-4 rounded-xl bg-red-50 px-3 py-2 text-sm text-red-700">{{ formError }}</p>
          <div class="mt-6 flex justify-end gap-2">
            
            <button type="submit" :disabled="saving" class="rounded-xl bg-primary px-4 py-2.5 text-sm font-semibold text-white disabled:opacity-60">
              {{ saving ? "Đang lưu..." : editingId ? "Lưu thay đổi" : "Tạo khuyến mãi" }}
            </button>
          </div>
        </form>
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
import { computed, onMounted, reactive, ref } from "vue";
import axios from "axios";
import AdminShell from "@/components/admin/AdminShell.vue";
import ConfirmDialog from "@/components/admin/ConfirmDialog.vue";
import { useConfirmDialog } from "@/composables/useConfirmDialog";
import { getSession } from "@/utils/auth";

function authHeaders() {
  const token = getSession("token");
  return token ? { Authorization: `Bearer ${token}` } : {};
}

const { confirmModal, requestConfirm, executeConfirm, cancelConfirm } =
  useConfirmDialog();

const API_BASE = import.meta.env.VITE_API_URL || "http://localhost:8080/api";
const promotions = ref([]);
const products = ref([]);
const search = ref("");
const productSearch = ref("");
const productTab = ref("available");
const statusFilter = ref("ALL");
const loading = ref(false);
const saving = ref(false);
const error = ref("");
const formError = ref("");
const showModal = ref(false);
const editingId = ref(null);
const startDateInput = ref(null);
const endDateInput = ref(null);
const form = reactive(emptyForm());

function openDateTimePicker(input) {
  if (typeof input?.showPicker === "function") {
    input.showPicker();
  } else {
    input?.focus();
  }
}

function emptyForm() {
  return { name: "", discountPercent: 10, startDate: "", endDate: "", isActive: true, productIds: [] };
}

function extractRows(payload) {
  if (Array.isArray(payload)) return payload;
  if (Array.isArray(payload?.data)) return payload.data;
  if (Array.isArray(payload?.content)) return payload.content;
  if (Array.isArray(payload?.data?.content)) return payload.data.content;
  return [];
}

async function loadData() {
  loading.value = true;
  error.value = "";
  try {
    const [promotionResponse, productResponse] = await Promise.all([
      axios.get(`${API_BASE}/promotions?page=0&size=500`),
      axios.get(`${API_BASE}/products?page=0&size=500`),
    ]);
    promotions.value = extractRows(promotionResponse.data);
    products.value = extractRows(productResponse.data);
  } catch (err) {
    error.value = err?.response?.data?.message || "Không thể tải dữ liệu khuyến mãi.";
  } finally {
    loading.value = false;
  }
}

const filteredPromotions = computed(() => {
  const keyword = search.value.toLowerCase();
  return promotions.value.filter((item) => {
    if (statusFilter.value !== "ALL" && item.status !== statusFilter.value) return false;
    const text = [item.name, ...(item.products || []).map((product) => product.name)].join(" ").toLowerCase();
    return !keyword || text.includes(keyword);
  });
});

const filteredProducts = computed(() => {
  const keyword = productSearch.value.toLowerCase();
  return products.value.filter((product) =>
    !keyword || `${product.name || ""} ${product.productCode || ""}`.toLowerCase().includes(keyword),
  );
});

const selectedProducts = computed(() =>
  products.value.filter((product) => form.productIds.includes(Number(product.id))),
);

function toLocalInput(value) {
  if (!value) return "";
  const date = new Date(value);
  const offset = date.getTimezoneOffset() * 60000;
  return new Date(date.getTime() - offset).toISOString().slice(0, 16);
}

function openCreate() {
  editingId.value = null;
  Object.assign(form, emptyForm());
  productSearch.value = "";
  productTab.value = "available";
  formError.value = "";
  showModal.value = true;
}

function openEdit(promotion) {
  editingId.value = promotion.id;
  Object.assign(form, {
    name: promotion.name || "",
    discountPercent: Number(promotion.discountPercent || 0),
    startDate: toLocalInput(promotion.startDate),
    endDate: toLocalInput(promotion.endDate),
    isActive: promotion.isActive !== false,
    productIds: (promotion.productIds || promotion.products?.map((product) => product.id) || []).map(Number),
  });
  productSearch.value = "";
  productTab.value = "available";
  formError.value = "";
  showModal.value = true;
}

function closeModal() {
  if (!saving.value) showModal.value = false;
}

function toggleAllProducts() {
  form.productIds = form.productIds.length === products.value.length ? [] : products.value.map((product) => Number(product.id));
}

function requestSavePromotion() {
  formError.value = "";
  if (!form.name || !form.name.trim()) {
    formError.value = "Vui lòng nhập tên chương trình.";
    return;
  }
  const start = new Date(form.startDate);
  const end = new Date(form.endDate);
  if (!(end > start)) {
    formError.value = "Thời gian kết thúc phải sau thời gian bắt đầu.";
    return;
  }

  const isEdit = !!editingId.value;
  requestConfirm({
    title: isEdit ? "Xác nhận cập nhật khuyến mãi" : "Xác nhận thêm mới khuyến mãi",
    message: isEdit
      ? `Bạn chắc chắn muốn cập nhật khuyến mãi "${form.name}"?`
      : `Bạn chắc chắn muốn tạo khuyến mãi "${form.name}"?`,
    confirmText: isEdit ? "Cập nhật" : "Thêm mới",
    action: savePromotion,
  });
}

async function savePromotion() {
  formError.value = "";
  const start = new Date(form.startDate);
  const end = new Date(form.endDate);
  if (!(end > start)) {
    formError.value = "Thời gian kết thúc phải sau thời gian bắt đầu.";
    return;
  }
  saving.value = true;
  try {
    const payload = {
      name: form.name,
      discountPercent: Number(form.discountPercent),
      startDate: start.toISOString(),
      endDate: end.toISOString(),
      isActive: form.isActive,
      productIds: form.productIds.map(Number),
    };
    if (editingId.value) {
      await axios.put(`${API_BASE}/promotions/${editingId.value}`, payload, { headers: authHeaders() });
    } else {
      await axios.post(`${API_BASE}/promotions`, payload, { headers: authHeaders() });
    }
    showModal.value = false;
    await loadData();
  } catch (err) {
    formError.value = err?.response?.data?.message || "Không thể lưu khuyến mãi.";
  } finally {
    saving.value = false;
  }
}

function requestRemovePromotion(promotion) {
  requestConfirm({
    title: "Xác nhận xóa khuyến mãi",
    message: `Bạn chắc chắn muốn xóa khuyến mãi "${promotion.name}"?`,
    confirmText: "Xóa",
    danger: true,
    action: () => removePromotion(promotion),
  });
}

async function removePromotion(promotion) {
  try {
    await axios.delete(`${API_BASE}/promotions/${promotion.id}`, { headers: authHeaders() });
    await loadData();
  } catch (err) {
    error.value = err?.response?.data?.message || "Không thể xóa khuyến mãi.";
  }
}

function formatDate(value) {
  return new Intl.DateTimeFormat("vi-VN", { dateStyle: "short", timeStyle: "short" }).format(new Date(value));
}

function productSummary(promotion) {
  const names = (promotion.products || []).map((product) => product.name);
  return names.length ? names.join(", ") : "Toàn bộ sản phẩm";
}

function statusLabel(status) {
  return { ACTIVE: "Đang diễn ra", SCHEDULED: "Sắp diễn ra", EXPIRED: "Đã kết thúc", INACTIVE: "Đã tắt" }[status] || status;
}

function statusClass(status) {
  return {
    ACTIVE: "bg-emerald-100 text-emerald-700",
    SCHEDULED: "bg-blue-100 text-blue-700",
    EXPIRED: "bg-slate-100 text-slate-600",
    INACTIVE: "bg-red-100 text-red-700",
  }[status] || "bg-slate-100 text-slate-600";
}

onMounted(loadData);
</script>

<style scoped>
.datetime-picker-input::-webkit-calendar-picker-indicator {
  opacity: 0;
  pointer-events: none;
}
</style>
