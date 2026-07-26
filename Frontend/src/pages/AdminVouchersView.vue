<template>
  <AdminShell>
    <template #header>
      <div class="flex flex-wrap items-center justify-between gap-2">
        <div>
          <p class="text-xs uppercase tracking-[0.18em] text-primary">
            Quản lý ưu đãi
          </p>
          <h2 class="mt-1 text-xl font-bold">Quản lý voucher</h2>
        </div>

        <div class="flex flex-wrap items-center gap-2">
          <button
            type="button"
            class="inline-flex items-center gap-1.5 rounded-xl bg-primary px-3 py-1.5 text-sm font-semibold text-white hover:opacity-90"
            @click="openCreate"
          >
            <span class="material-symbols-outlined text-[18px]">add</span>
            Thêm voucher
          </button>

          <button
            type="button"
            class="inline-flex items-center gap-1.5 rounded-xl border border-slate-200 bg-white px-3 py-1.5 text-sm font-medium hover:border-primary hover:text-primary dark:border-[#3c342e] dark:bg-[#1f1a17]"
            @click="loadVouchers"
          >
            <span class="material-symbols-outlined text-[18px]">refresh</span>
            Làm mới
          </button>
        </div>
      </div>
    </template>

    <div class="space-y-3">
      <section class="retail-card p-3">
        <div class="grid gap-3 md:grid-cols-[1fr_220px]">
          <label
            class="flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-1.5 dark:border-[#3c342e] dark:bg-[#1f1a17]"
          >
            <span class="material-symbols-outlined text-[18px] text-slate-400"
              >search</span
            >
            <input
              v-model="search"
              type="text"
              placeholder="Tìm theo mã voucher"
              class="w-full bg-transparent text-sm outline-none"
            />
          </label>

          <select
            v-model="statusFilter"
            class="rounded-xl border border-slate-200 bg-white px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#1f1a17]"
          >
            <option value="ALL">Tất cả trạng thái</option>
            <option value="ACTIVE">Đang hoạt động</option>
            <option value="INACTIVE">Không hoạt động</option>
            <option value="SCHEDULED">Sắp diễn ra</option>
            <option value="USED">Đã hết lượt</option>
            <option value="EXPIRED">Hết hạn</option>
          </select>
        </div>
      </section>

      <section class="retail-card p-3">
        <div class="mb-3 grid grid-cols-1 gap-2 text-xs md:grid-cols-4">
          <div
            class="rounded-xl border border-slate-200 px-3 py-2 dark:border-[#3c342e]"
          >
            Tổng voucher:
            <span class="font-semibold">{{ vouchers.length }}</span>
          </div>
          <div
            class="rounded-xl border border-slate-200 px-3 py-2 dark:border-[#3c342e]"
          >
            Đang hoạt động: <span class="font-semibold">{{ activeCount }}</span>
          </div>
          <div
            class="rounded-xl border border-slate-200 px-3 py-2 dark:border-[#3c342e]"
          >
            Hết hạn/hết lượt:
            <span class="font-semibold">{{ inactiveCount }}</span>
          </div>
          <div
            class="rounded-xl border border-slate-200 px-3 py-2 dark:border-[#3c342e]"
          >
            Đang hiển thị:
            <span class="font-semibold">{{ filteredVouchers.length }}</span>
          </div>
        </div>

        <div
          v-if="error"
          class="mb-4 rounded-xl border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700 dark:border-red-900/40 dark:bg-red-900/20 dark:text-red-300"
        >
          {{ error }}
        </div>

        <div
          v-if="hiddenVouchers.length > 0"
          class="mb-4 rounded-xl border border-amber-200 bg-amber-50 px-3 py-3 text-sm text-slate-700 dark:border-amber-900/20 dark:bg-[#3f2e11] dark:text-[#fbe7a1]"
        >
          <div class="flex flex-wrap items-center justify-between gap-3">
            <div>
              <p class="font-semibold">Voucher ẩn tạm</p>
              <p class="text-xs text-slate-500 dark:text-[#e1c797]">
                Những voucher đã xóa mềm sẽ tạm ẩn khỏi danh sách được dùng. Bạn
                có thể khôi phục lại khi cần.
              </p>
            </div>
            <button
              type="button"
              class="rounded-xl bg-primary px-3 py-2 text-xs font-semibold text-white hover:opacity-90"
              @click="restoreAllHiddenVouchers"
            >
              Khôi phục tất cả
            </button>
          </div>
          <div class="mt-3 space-y-2">
            <div
              v-for="voucher in hiddenVouchers"
              :key="voucher.id"
              class="flex flex-wrap items-center justify-between gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2 dark:border-[#4c4138] dark:bg-[#1e1812]"
            >
              <div class="text-sm">
                <span class="font-semibold">#{{ voucher.id }}</span>
                <span class="ml-2">{{ voucher.code || "Voucher" }}</span>
              </div>
              <button
                type="button"
                class="rounded-xl bg-emerald-500 px-3 py-2 text-xs font-semibold text-white hover:bg-emerald-600"
                @click="restoreVoucher(voucher)"
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
          Đang tải danh sách voucher...
        </div>

        <div
          v-else-if="filteredVouchers.length === 0"
          class="rounded-2xl border border-dashed border-slate-300 p-6 text-center text-sm text-slate-500 dark:border-[#3c342e] dark:text-[#b9aa9a]"
        >
          Không có voucher phù hợp.
        </div>

        <div v-else class="overflow-x-auto">
          <table class="w-full min-w-[1080px] text-left text-xs">
            <thead>
              <tr
                class="border-b border-slate-200 text-slate-500 dark:border-[#3c342e] dark:text-[#b9aa9a]"
              >
                <th class="px-2 py-2 font-medium">STT</th>
                <th class="px-2 py-2 font-medium">Mã</th>
                <th class="px-2 py-2 font-medium">Loại giảm</th>
                <th class="px-2 py-2 font-medium">Giá trị</th>
                <th class="px-2 py-2 font-medium">Đơn tối thiểu</th>
                <th class="px-2 py-2 font-medium">Thời gian</th>
                <th class="px-2 py-2 font-medium">Trạng thái</th>
                <th class="px-2 py-2 font-medium">Sử dụng</th>
                <th class="px-2 py-2 text-right font-medium">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="(voucher, index) in paginatedVouchers"
                :key="voucher.id"
                class="border-b border-slate-100 last:border-none dark:border-[#2b241f]"
              >
                <td
                  class="px-2 py-2 font-semibold text-slate-500 dark:text-[#b9aa9a]"
                >
                  {{ (vouchersPage - 1) * 10 + index + 1 }}
                </td>
                <td class="px-2 py-2 font-semibold">
                  {{ voucher.code || "-" }}
                </td>
                <td class="px-2 py-2">{{ voucher.discountType }}</td>
                <td class="px-2 py-2">{{ formatDiscount(voucher) }}</td>
                <td class="px-2 py-2">
                  {{ formatMoney(voucher.minOrderValue) }}
                </td>
                <td class="px-2 py-2 text-slate-500 dark:text-[#b9aa9a]">
                  <p>{{ formatDate(voucher.startDate) }}</p>
                  <p>{{ formatDate(voucher.endDate) }}</p>
                </td>
                <td class="px-2 py-2">
                  <span
                    class="rounded-full px-2 py-1 text-xs font-semibold"
                    :class="statusClass(voucher.status)"
                  >
                    {{ voucher.statusLabel || voucherStatusLabel(voucher.status) }}
                  </span>
                </td>
                <td class="px-2 py-2">
                  {{ voucher.usedCount || 0 }}
                  <span v-if="voucher.usageLimit"
                    >/ {{ voucher.usageLimit }}</span
                  >
                </td>
                <td class="px-2 py-2 text-right">
                  <div class="flex items-center justify-end gap-1">
                    <button
                      class="rounded-lg p-1.5 hover:bg-slate-100 dark:hover:bg-[#2b241f]"
                      title="Chỉnh sửa"
                      @click="openEdit(voucher)"
                    >
                      <span
                        class="material-symbols-outlined text-[18px] text-slate-500"
                        >edit</span
                      >
                    </button>
                    <button
                      class="rounded-lg p-1.5 hover:bg-red-50 dark:hover:bg-red-900/20"
                      title="Xóa"
                      @click="confirmDelete(voucher)"
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
          :current-page="vouchersPage"
          :total-pages="vouchersTotalPages"
          :page-start="vouchersPageStart"
          :page-end="vouchersPageEnd"
          :total-items="vouchersTotalItems"
          label="voucher"
          @previous="vouchersPreviousPage"
          @next="vouchersNextPage"
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
          class="w-full max-w-2xl max-h-[90vh] overflow-y-auto rounded-2xl bg-white p-4 shadow-2xl dark:bg-[#1f1a17]"
        >
          <h3 class="mb-4 text-base font-bold">
            {{
              editingVoucherId
                ? `Sửa voucher #${editingVoucherId}`
                : "Thêm voucher mới"
            }}
          </h3>

          <div class="grid gap-3 md:grid-cols-2">
            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Mã voucher *</label
              >
              <input
                v-model.trim="form.code"
                type="text"
                class="w-full rounded-xl border border-slate-200 bg-white px-3 py-1.5 text-sm uppercase dark:border-[#3c342e] dark:bg-[#2b241f]"
                placeholder="VD: PEAK10"
              />
            </div>

            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Loại giảm *</label
              >
              <select
                v-model="form.discountType"
                class="w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
              >
                <option value="PERCENT">PERCENT</option>
                <option value="AMOUNT">AMOUNT</option>
              </select>
            </div>

            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Giá trị giảm *</label
              >
              <input
                v-model.number="form.discountValue"
                type="number"
                min="0"
                class="w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
              />
            </div>

            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Đơn tối thiểu</label
              >
              <input
                v-model.number="form.minOrderValue"
                type="number"
                min="0"
                class="w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
              />
            </div>

            <div v-if="form.discountType === 'PERCENT'">
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Giảm tối đa (đ)</label
              >
              <input
                v-model.number="form.maxDiscountAmount"
                type="number"
                min="0"
                placeholder="VD: 50000"
                class="w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
              />
              <p class="mt-1 text-[11px] text-slate-400">Để trống = không giới hạn</p>
            </div>

            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Bắt đầu</label
              >
              <div class="relative">
                <input
                  ref="startDateInput"
                  v-model="form.startDate"
                  type="datetime-local"
                  class="datetime-picker-input w-full rounded-xl border border-slate-200 bg-white py-2 pl-3 pr-11 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                />
                <button type="button" class="absolute right-2 top-1/2 inline-flex size-7 -translate-y-1/2 items-center justify-center rounded-lg text-primary hover:bg-blue-50" title="Chọn ngày giờ bắt đầu" @click="openDateTimePicker(startDateInput)">
                  <span class="material-symbols-outlined text-[19px]">calendar_month</span>
                </button>
              </div>
            </div>

            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Kết thúc</label
              >
              <div class="relative">
                <input
                  ref="endDateInput"
                  v-model="form.endDate"
                  type="datetime-local"
                  class="datetime-picker-input w-full rounded-xl border border-slate-200 bg-white py-2 pl-3 pr-11 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                />
                <button type="button" class="absolute right-2 top-1/2 inline-flex size-7 -translate-y-1/2 items-center justify-center rounded-lg text-primary hover:bg-blue-50" title="Chọn ngày giờ kết thúc" @click="openDateTimePicker(endDateInput)">
                  <span class="material-symbols-outlined text-[19px]">calendar_month</span>
                </button>
              </div>
            </div>

            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Giới hạn sử dụng</label
              >
              <input
                v-model.number="form.usageLimit"
                type="number"
                min="0"
                class="w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
              />
            </div>

            <label class="inline-flex items-center gap-2 text-sm md:pt-6">
              <input v-model="form.isActive" type="checkbox" />
              <span>Voucher đang hoạt động</span>
            </label>
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
              @click="saveVoucher"
            >
              {{
                saving
                  ? "Đang lưu..."
                  : editingVoucherId
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
            Bạn chắc chắn muốn xóa voucher
            <strong>{{ deleteTarget.code }}</strong
            >?
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

import AdminShell from "@/components/admin/AdminShell.vue";
import PaginationBar from "@/components/PaginationBar.vue";
import { usePagination } from "@/composables/usePagination";
import { adminApi } from "@/services/api";

const loading = ref(false);
const error = ref("");
const vouchers = ref([]);
const search = ref("");
const statusFilter = ref("ALL");
const hiddenVouchers = ref([]);

const showModal = ref(false);
const editingVoucherId = ref(null);
const saving = ref(false);
const modalError = ref("");
const startDateInput = ref(null);
const endDateInput = ref(null);
const form = ref(emptyForm());

function openDateTimePicker(input) {
  if (typeof input?.showPicker === "function") {
    input.showPicker();
  } else {
    input?.focus();
  }
}

const deleteTarget = ref(null);
const deleting = ref(false);

const visibleVouchers = computed(() => {
  return vouchers.value.filter(
    (voucher) =>
      !hiddenVouchers.value.some(
        (hidden) => Number(voucher.id) === Number(hidden.id),
      ),
  );
});

const filteredVouchers = computed(() => {
  const keyword = search.value.trim().toLowerCase();
  return visibleVouchers.value.filter((voucher) => {
    const statusMatch =
      statusFilter.value === "ALL" || voucher.status === statusFilter.value;
    if (!statusMatch) {
      return false;
    }

    if (!keyword) {
      return true;
    }

    const haystack = [voucher.code, voucher.discountType, voucher.status]
      .filter(Boolean)
      .join(" ")
      .toLowerCase();
    return haystack.includes(keyword);
  });
});

const {
  currentPage: vouchersPage,
  totalPages: vouchersTotalPages,
  paginatedItems: paginatedVouchers,
  pageStart: vouchersPageStart,
  pageEnd: vouchersPageEnd,
  totalItems: vouchersTotalItems,
  previousPage: vouchersPreviousPage,
  nextPage: vouchersNextPage,
} = usePagination(filteredVouchers, 10);

const activeCount = computed(() => {
  return vouchers.value.filter((voucher) => voucher.status === "ACTIVE").length;
});

const inactiveCount = computed(() => {
  return vouchers.value.filter((voucher) =>
    ["USED", "EXPIRED", "INACTIVE"].includes(voucher.status),
  ).length;
});

function normalizeVoucherStatusValue(status) {
  const normalized = String(status || "INACTIVE")
    .trim()
    .toUpperCase();
  if (
    ["ACTIVE", "INACTIVE", "SCHEDULED", "USED", "EXPIRED"].includes(normalized)
  ) {
    return normalized;
  }
  return "INACTIVE";
}

function voucherStatusLabel(status) {
  const normalized = normalizeVoucherStatusValue(status);
  if (normalized === "ACTIVE") return "Đang hoạt động";
  if (normalized === "SCHEDULED") return "Sắp diễn ra";
  if (normalized === "USED") return "Đã hết lượt";
  if (normalized === "EXPIRED") return "Hết hạn";
  return "Không hoạt động";
}

function emptyForm() {
  return {
    code: "",
    discountType: "PERCENT",
    discountValue: 0,
    minOrderValue: 0,
    maxDiscountAmount: null,
    startDate: "",
    endDate: "",
    usageLimit: null,
    isActive: true,
  };
}

function formatMoney(value) {
  const amount = Number(value);
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
    maximumFractionDigits: 0,
  }).format(Number.isFinite(amount) ? amount : 0);
}

function formatDiscount(voucher) {
  if (voucher.discountType === "PERCENT") {
    const pct = `${Number(voucher.discountValue || 0)}%`;
    if (voucher.maxDiscountAmount) {
      return `${pct} (tối đa ${formatMoney(voucher.maxDiscountAmount)})`;
    }
    return pct;
  }
  return formatMoney(voucher.discountValue || 0);
}

function formatDate(value) {
  const date = new Date(value || 0);
  if (Number.isNaN(date.getTime())) {
    return "-";
  }
  return new Intl.DateTimeFormat("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(date);
}

function toDatetimeLocal(value) {
  const date = new Date(value || "");
  if (Number.isNaN(date.getTime())) {
    return "";
  }

  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hour = String(date.getHours()).padStart(2, "0");
  const minute = String(date.getMinutes()).padStart(2, "0");
  return `${year}-${month}-${day}T${hour}:${minute}`;
}

function statusClass(status) {
  const normalized = normalizeVoucherStatusValue(status);
  if (normalized === "ACTIVE") {
    return "bg-green-100 text-green-700 dark:bg-green-900/20 dark:text-green-400";
  }
  if (normalized === "USED") {
    return "bg-slate-100 text-slate-700 dark:bg-slate-700/50 dark:text-slate-300";
  }
  if (normalized === "SCHEDULED") {
    return "bg-blue-100 text-blue-700 dark:bg-blue-900/20 dark:text-blue-400";
  }
  if (normalized === "INACTIVE") {
    return "bg-amber-100 text-amber-700 dark:bg-amber-900/20 dark:text-amber-400";
  }
  return "bg-red-100 text-red-700 dark:bg-red-900/20 dark:text-red-400";
}

function openCreate() {
  editingVoucherId.value = null;
  form.value = emptyForm();
  modalError.value = "";
  showModal.value = true;
}

function openEdit(voucher) {
  editingVoucherId.value = voucher.id;
  form.value = {
    code: voucher.code || "",
    discountType: voucher.discountType || "PERCENT",
    discountValue: Number(voucher.discountValue || 0),
    minOrderValue: Number(voucher.minOrderValue || 0),
    maxDiscountAmount:
      voucher.maxDiscountAmount !== null && voucher.maxDiscountAmount !== undefined
        ? Number(voucher.maxDiscountAmount)
        : null,
    startDate: toDatetimeLocal(voucher.startDate),
    endDate: toDatetimeLocal(voucher.endDate),
    usageLimit:
      voucher.usageLimit !== null && voucher.usageLimit !== undefined
        ? Number(voucher.usageLimit)
        : null,
    isActive: !!voucher.isActive,
  };
  modalError.value = "";
  showModal.value = true;
}

function closeModal() {
  showModal.value = false;
}

async function saveVoucher() {
  const f = form.value;
  modalError.value = "";

  if (!f.code) {
    modalError.value = "Vui lòng nhập mã voucher.";
    return;
  }

  if (Number(f.discountValue) <= 0) {
    modalError.value = "Giá trị giảm phải lớn hơn 0.";
    return;
  }

  saving.value = true;
  try {
    const payload = {
      code: String(f.code).trim().toUpperCase(),
      discountType: String(f.discountType || "PERCENT").toUpperCase(),
      discountValue: Number(f.discountValue || 0),
      minOrderValue: Number(f.minOrderValue || 0),
      maxDiscountAmount:
        f.discountType === "PERCENT" &&
        f.maxDiscountAmount !== "" &&
        f.maxDiscountAmount !== null &&
        f.maxDiscountAmount !== undefined
          ? Number(f.maxDiscountAmount)
          : null,
      startDate: f.startDate || null,
      endDate: f.endDate || null,
      usageLimit:
        f.usageLimit === "" ||
        f.usageLimit === null ||
        f.usageLimit === undefined
          ? null
          : Number(f.usageLimit),
      isActive: !!f.isActive,
    };

    if (editingVoucherId.value) {
      await adminApi.updateVoucher(editingVoucherId.value, payload);
    } else {
      await adminApi.createVoucher(payload);
    }

    closeModal();
    await loadVouchers();
  } catch (err) {
    modalError.value = err?.message || "Không thể lưu voucher.";
  } finally {
    saving.value = false;
  }
}

function confirmDelete(voucher) {
  deleteTarget.value = voucher;
}

async function doDelete() {
  if (!deleteTarget.value) {
    return;
  }

  deleting.value = true;
  try {
    const result = await adminApi.deleteVoucher(deleteTarget.value.id);

    if (result && result.softDeleted) {
      if (
        !hiddenVouchers.value.some(
          (voucher) => Number(voucher.id) === Number(deleteTarget.value.id),
        )
      ) {
        hiddenVouchers.value.push(deleteTarget.value);
      }
      deleteTarget.value = null;
      return;
    }

    deleteTarget.value = null;
    await loadVouchers();
  } catch (err) {
    error.value = err?.message || "Không thể xóa voucher.";
  } finally {
    deleting.value = false;
  }
}

async function restoreVoucher(voucher) {
  try {
    await adminApi.restoreVoucher(voucher.id);
    const restoredId = Number(voucher?.id);
    hiddenVouchers.value = hiddenVouchers.value.filter(
      (hidden) => Number(hidden.id) !== restoredId,
    );
    await loadVouchers();
  } catch (err) {
    error.value = "Không thể khôi phục voucher.";
  }
}

async function restoreAllHiddenVouchers() {
  try {
    for (const voucher of hiddenVouchers.value) {
      await adminApi.restoreVoucher(voucher.id);
    }
    hiddenVouchers.value = [];
    await loadVouchers();
  } catch (err) {
    error.value = "Có lỗi xảy ra khi khôi phục.";
  }
}

async function loadVouchers() {
  loading.value = true;
  error.value = "";

  try {
    const data = await adminApi.getVouchers({ page: 0, size: 400 });
    vouchers.value = Array.isArray(data) ? data : [];
  } catch (err) {
    console.error("Failed to load vouchers:", err);
    error.value =
      "Không thể tải danh sách voucher. Vui lòng kiểm tra backend API.";
  } finally {
    loading.value = false;
  }
}

onMounted(loadVouchers);
</script>

<style scoped>
.datetime-picker-input::-webkit-calendar-picker-indicator {
  opacity: 0;
  pointer-events: none;
}
</style>
