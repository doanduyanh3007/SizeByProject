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
            Quản lý người dùng
          </p>
          <h2 class="mt-1 text-xl font-bold">
            Quản lý khách hàng
          </h2>
          <p class="mt-1 text-sm text-slate-500 dark:text-[#b9aa9a]">
            {{
              isAdmin
                ? "ADMIN xem tất cả khách hàng"
                : "Nhân viên chỉ xem được khách hàng."
            }}
          </p>
        </div>

        <div class="flex flex-wrap items-center gap-2">

          <button
            type="button"
            class="inline-flex items-center gap-1.5 rounded-xl border border-slate-900 bg-white px-3 py-1.5 text-sm font-medium hover:border-primary hover:text-primary dark:border-[#3c342e] dark:bg-[#1f1a17]"
            @click="loadAccounts"
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
            class="flex items-center gap-2 rounded-xl border border-slate-900 bg-white px-3 py-1.5 dark:border-[#3c342e] dark:bg-[#1f1a17]"
          >
            <span class="material-symbols-outlined text-[18px] text-slate-400"
              >search</span
            >
            <input
              v-model="search"
              type="text"
              placeholder="Tìm theo tên, email, số điện thoại"
              class="w-full bg-transparent text-sm outline-none"
            />
          </label>

          <select
            v-model="statusFilter"
            class="rounded-xl border border-slate-900 bg-gray-50 px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#1f1a17]"
          >
            <option value="ALL">Tất cả trạng thái</option>
            <option value="ACTIVE">Đang hoạt động</option>
            <option value="LOCKED">Tạm khóa</option>
          </select>
        </div>
      </section>

      <section class="retail-card p-3">
        <div class="mb-4 text-sm text-slate-500 dark:text-[#b9aa9a]">
          Tổng người dùng:
          <span class="font-semibold text-slate-900 dark:text-white">{{
            filteredAccounts.length
          }}</span>
          <span class="mx-2">/</span>
          Tổng dữ liệu:
          <span class="font-semibold text-slate-900 dark:text-white">{{
            accounts.length
          }}</span>
        </div>

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
          Đang tải danh sách người dùng...
        </div>

        <div
          v-else-if="filteredAccounts.length === 0"
          class="rounded-2xl border border-dashed border-slate-300 p-6 text-center text-sm text-slate-500 dark:border-[#3c342e] dark:text-[#b9aa9a]"
        >
          Không tìm thấy khách hàng phù hợp.
        </div>

        <div v-else class="overflow-x-auto">
          <table class="w-full min-w-[920px] text-left text-xs">
            <thead>
              <tr
                class="border-b border-slate-900 text-slate-500 dark:border-[#3c342e] dark:text-[#b9aa9a]"
              >
                <th class="px-2 py-2 font-medium">STT</th>
                <th class="px-2 py-2 font-medium">Khách hàng</th>
                <th class="px-2 py-2 font-medium">Vai trò</th>
                <th class="px-2 py-2 font-medium">Liên hệ</th>
                <th class="px-2 py-2 font-medium">Trạng thái</th>
                <th class="px-2 py-2 font-medium">Địa chỉ</th>
                <th class="px-2 py-2 text-right font-medium">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="(account, index) in paginatedAccounts"
                :key="account.id"
                class="border-b border-slate-100 last:border-none dark:border-[#2b241f]"
              >
                <td
                  class="px-2 py-2 font-semibold text-slate-500 dark:text-[#b9aa9a]"
                >
                  {{ (accountsPage - 1) * 10 + index + 1 }}
                </td>

                <td class="px-2 py-2">
                  <div class="flex items-center gap-3">
                    <div
                      class="size-10 overflow-hidden rounded-full bg-slate-100 dark:bg-[#2b241f]"
                    >
                      <img
                        v-if="account.imgUrl"
                        :src="account.imgUrl"
                        alt="Avatar"
                        class="h-full w-full object-cover"
                      />
                      <span
                        v-else
                        class="flex h-full items-center justify-center text-xs text-slate-500 dark:text-[#b9aa9a]"
                      >
                        {{ getInitials(account.username) }}
                      </span>
                    </div>
                    <div>
                      <p class="font-semibold">
                        {{ account.username || "Khách hàng" }}
                      </p>
                      <p class="text-xs text-slate-500 dark:text-[#b9aa9a]">
                        ID: {{ account.id }}
                      </p>
                    </div>
                  </div>
                </td>

                <td class="px-2 py-2">
                  <span
                    :class="getRoleBadgeClasses(account.role)"
                    class="rounded-full px-2 py-1 text-xs font-semibold"
                  >
                    {{ formatRoleLabel(account.role) }}
                  </span>
                </td>

                <td class="px-2 py-2 text-slate-500 dark:text-[#b9aa9a]">
                  <p>{{ account.gmail || "-" }}</p>
                  <p>{{ account.phone || "-" }}</p>
                </td>

                <td class="px-2 py-2">
                  <span
                    class="rounded-full px-2 py-1 text-xs font-semibold"
                    :class="
                      account.isActive
                        ? 'bg-green-100 text-green-700 dark:bg-green-900/20 dark:text-green-400'
                        : 'bg-red-100 text-red-700 dark:bg-red-900/20 dark:text-red-400'
                    "
                  >
                    {{ account.isActive ? "Hoạt động" : "Tạm khóa" }}
                  </span>
                </td>

                <td class="px-2 py-2 text-slate-500 dark:text-[#b9aa9a]">
                  {{ account.address || "-" }}
                </td>

                <td class="px-2 py-2 text-right">
                  <div class="flex items-center justify-end gap-1">
                    <button
                      v-if="isCustomerOnlyRole(account)"
                      class="rounded-lg p-1.5 hover:bg-blue-50 dark:hover:bg-blue-900/20"
                      title="Lịch sử đơn hàng"
                      @click="openOrderHistory(account)"
                    >
                      <span
                        class="material-symbols-outlined text-[18px] text-blue-500"
                        >receipt_long</span
                      >
                    </button>



                    <button
                      class="rounded-lg p-1.5 hover:bg-amber-50 dark:hover:bg-amber-900/20"
                      :title="
                        account.isActive
                          ? 'Khóa tài khoản'
                          : 'Mở khóa tài khoản'
                      "
                      @click="requestToggleActive(account)"
                    >
                      <span
                        class="material-symbols-outlined text-[18px] text-amber-500"
                      >
                        {{ account.isActive ? "lock" : "lock_open" }}
                      </span>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <PaginationBar
          :current-page="accountsPage"
          :total-pages="accountsTotalPages"
          :page-start="accountsPageStart"
          :page-end="accountsPageEnd"
          :total-items="accountsTotalItems"
          label="khách hàng"
          @previous="accountsPreviousPage"
          @next="accountsNextPage"
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
          <div class="relative mb-4 flex items-center">
            <h3 class="text-base font-bold">
              {{
                editingAccountId
                  ? `Sửa người dùng #${editingAccountId}`
                  : "Thêm người dùng mới"
              }}
            </h3>
            <button
              type="button"
              class="absolute right-0 top-0 inline-flex size-8 items-center justify-center rounded-lg text-slate-400 hover:bg-slate-100 hover:text-slate-600 dark:hover:bg-slate-800"
              @click="closeModal"
            >
              <span class="material-symbols-outlined text-[20px]">close</span>
            </button>
          </div>

          <div class="grid gap-3 md:grid-cols-2">
            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Tên người dùng *</label
              >
              <input
                v-model.trim="form.username"
                type="text"
                class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                placeholder="Nhập tên người dùng"
              />
            </div>

            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Email *</label
              >
              <input
                v-model.trim="form.gmail"
                type="email"
                class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                placeholder="example@email.com"
              />
            </div>

                          <div>
                <label class="mb-1 block text-xs font-medium text-slate-900">Vai trò</label>
                <input
                  type="text"
                  value="Khách hàng"
                  readonly
                  class="w-full rounded-xl border border-slate-900 bg-gray-100 px-3 py-1.5 text-sm text-gray-500 cursor-not-allowed dark:border-[#3c342e] dark:bg-[#2b241f]"
                />
              </div>

            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Số điện thoại</label
              >
              <input
                v-model.trim="form.phone"
                type="text"
                class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                placeholder="09..."
              />
            </div>

            <div v-if="!editingAccountId">
              <label class="mb-1 block text-xs font-medium text-slate-500">
                Mật khẩu *
              </label>
              <input
                v-model="form.password"
                type="password"
                class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                placeholder="Tối thiểu 6 ký tự"
              />
            </div>

            <div class="md:col-span-2">
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Địa chỉ</label
              >
              <textarea
                v-model.trim="form.address"
                rows="3"
                class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                placeholder="Nhập địa chỉ khách hàng"
              />
            </div>

            <label class="md:col-span-2 inline-flex items-center gap-2 text-sm">
              <input v-model="form.isActive" type="checkbox" />
              <span>Tài khoản đang hoạt động</span>
            </label>
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
                  : editingAccountId
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
        v-if="showOrderHistoryModal"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4"
        @click.self="closeOrderHistory"
      >
        <div
          class="w-full max-w-3xl max-h-[90vh] overflow-y-auto rounded-2xl bg-white p-4 shadow-2xl dark:bg-[#1f1a17]"
        >
          <div class="mb-4 flex flex-wrap items-start justify-between gap-3">
            <div>
              <h3 class="text-base font-bold">
                Lịch sử đơn hàng — {{ orderHistoryAccount?.username || "Khách hàng" }}
              </h3>
              <p class="mt-1 text-xs text-slate-500 dark:text-[#b9aa9a]">
                ID: {{ orderHistoryAccount?.id }}
              </p>
            </div>
            <button
              type="button"
              class="rounded-lg p-1.5 hover:bg-slate-100 dark:hover:bg-[#2b241f]"
              @click="closeOrderHistory"
            >
              <span class="material-symbols-outlined text-[20px]">close</span>
            </button>
          </div>

          <div
            class="mb-4 grid gap-3 sm:grid-cols-3"
          >
            <div
              class="rounded-xl border border-slate-900 bg-slate-50 px-3 py-2 dark:border-[#3c342e] dark:bg-[#2b241f]"
            >
              <p class="text-xs text-slate-500 dark:text-[#b9aa9a]">Tổng đơn hàng</p>
              <p class="mt-1 text-lg font-bold">{{ customerOrders.length }}</p>
            </div>
            <div
              class="rounded-xl border border-emerald-200 bg-emerald-50 px-3 py-2 dark:border-emerald-900/40 dark:bg-emerald-900/20"
            >
              <p class="text-xs text-emerald-600 dark:text-emerald-400">Đơn thành công</p>
              <p class="mt-1 text-lg font-bold text-emerald-700 dark:text-emerald-300">
                {{ successfulOrdersCount }}
              </p>
            </div>
            <div
              class="rounded-xl border border-primary/20 bg-primary/5 px-3 py-2"
            >
              <p class="text-xs text-primary">Tổng tiền đã chi</p>
              <p class="mt-1 text-lg font-bold text-primary">
                {{ formatCurrency(customerTotalSpent) }}
              </p>
            </div>
          </div>

          <div
            v-if="orderHistoryLoading"
            class="py-8 text-center text-sm text-slate-500 dark:text-[#b9aa9a]"
          >
            Đang tải lịch sử đơn hàng...
          </div>

          <div
            v-else-if="orderHistoryError"
            class="rounded-xl border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700 dark:border-red-900/40 dark:bg-red-900/20 dark:text-red-300"
          >
            {{ orderHistoryError }}
          </div>

          <div
            v-else-if="customerOrders.length === 0"
            class="rounded-2xl border border-dashed border-slate-300 p-6 text-center text-sm text-slate-500 dark:border-[#3c342e] dark:text-[#b9aa9a]"
          >
            Khách hàng chưa có đơn hàng nào.
          </div>

          <div v-else class="overflow-x-auto">
            <table class="w-full min-w-[640px] text-left text-xs">
              <thead>
                <tr
                  class="border-b border-slate-900 text-slate-500 dark:border-[#3c342e] dark:text-[#b9aa9a]"
                >
                  <th class="px-2 py-2 font-medium">Mã đơn</th>
                  <th class="px-2 py-2 font-medium">Ngày đặt</th>
                  <th class="px-2 py-2 font-medium">Trạng thái</th>
                  <th class="px-2 py-2 text-right font-medium">Thành tiền</th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="order in customerOrders"
                  :key="order.id"
                  class="border-b border-slate-100 last:border-none dark:border-[#2b241f]"
                >
                  <td class="px-2 py-2 font-semibold">#{{ order.id }}</td>
                  <td class="px-2 py-2 text-slate-500 dark:text-[#b9aa9a]">
                    {{ formatOrderDate(order.createdAt || order.createdDate) }}
                  </td>
                  <td class="px-2 py-2">
                    <span
                      class="rounded-full px-2 py-1 text-xs font-semibold"
                      :class="getOrderStatusBadgeClass(order.status)"
                    >
                      {{ getOrderStatusLabel(order.status) }}
                    </span>
                  </td>
                  <td class="px-2 py-2 text-right font-semibold">
                    {{ formatCurrency(getOrderAmount(order)) }}
                  </td>
                </tr>
              </tbody>
            </table>
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
import { getSession } from "@/utils/auth";
import { computed, onMounted, ref } from "vue";

import AdminShell from "@/components/admin/AdminShell.vue";
import ConfirmDialog from "@/components/admin/ConfirmDialog.vue";
import PaginationBar from "@/components/PaginationBar.vue";
import { useConfirmDialog } from "@/composables/useConfirmDialog";
import { usePagination } from "@/composables/usePagination";
import { adminApi } from "@/services/api";
import { orderAPI } from "@/services/orders";

const { confirmModal, requestConfirm, executeConfirm, cancelConfirm } =
  useConfirmDialog();

const loading = ref(false);
const error = ref("");
const accounts = ref([]);
const search = ref("");
const statusFilter = ref("ALL");
const currentUserRole = ref(getSession("userRole") || null);

const showModal = ref(false);
const editingAccountId = ref(null);
const saving = ref(false);
const modalError = ref("");
const form = ref(emptyForm());

const successToast = ref("");

function showToast(message, duration = 3000) {
  successToast.value = message;
  setTimeout(() => { successToast.value = ""; }, duration);
}


const showOrderHistoryModal = ref(false);
const orderHistoryAccount = ref(null);
const customerOrders = ref([]);
const orderHistoryLoading = ref(false);
const orderHistoryError = ref("");

const ORDER_STATUS_LABELS = {
  PENDING: "Chờ xử lý",
  PROCESSING: "Đóng gói và xử lý",
  SHIPPING: "Đang giao",
  SUCCESS: "Thành công",
  RETURN_REQUEST: "Yêu cầu hoàn",
  RETURNING: "Đang hoàn về",
  RETURNED: "Đã hoàn trả",
  CANCELLED: "Đã hủy",
};

const isAdmin = computed(() => currentUserRole.value === "ADMIN");
const canManageRoles = computed(() => isAdmin.value);

const filteredAccounts = computed(() => {
  const keyword = search.value.trim().toLowerCase();

  return accounts.value.filter((account) => {
    const activeMatch =
      statusFilter.value === "ALL" ||
      (statusFilter.value === "ACTIVE"
        ? !!account.isActive
        : !account.isActive);

    if (!activeMatch) {
      return false;
    }

    if (!keyword) {
      return true;
    }

    const haystack = [
      account.username,
      account.gmail,
      account.phone,
      account.address,
      account.id,
      account.role,
    ]
      .filter((item) => item !== undefined && item !== null)
      .join(" ")
      .toLowerCase();

    return haystack.includes(keyword);
  });
});

const {
  currentPage: accountsPage,
  totalPages: accountsTotalPages,
  paginatedItems: paginatedAccounts,
  pageStart: accountsPageStart,
  pageEnd: accountsPageEnd,
  totalItems: accountsTotalItems,
  previousPage: accountsPreviousPage,
  nextPage: accountsNextPage,
} = usePagination(filteredAccounts, 10);

const successfulOrdersCount = computed(() => {
  return customerOrders.value.filter((order) =>
    isSuccessfulOrderStatus(order.status),
  ).length;
});

const customerTotalSpent = computed(() => {
  return customerOrders.value
    .filter((order) => isSuccessfulOrderStatus(order.status))
    .reduce((sum, order) => sum + getOrderAmount(order), 0);
});

function normalizeOrderStatusKey(status) {
  const key = String(status || "")
    .trim()
    .replace(/\s+/g, "_")
    .replace(/-/g, "_")
    .toUpperCase();

  if (["NEW", "PENDING", "WAITING", "CHUA_XU_LY", "CHO_XU_LY"].includes(key)) {
    return "PENDING";
  }
  if (
    ["CONFIRMED", "PROCESSING", "DANG_XU_LY", "XAC_NHAN", "IN_PROGRESS"].includes(
      key,
    )
  ) {
    return "PROCESSING";
  }
  if (["SHIPPING", "DELIVERING", "DANG_GIAO", "IN_TRANSIT"].includes(key)) {
    return "SHIPPING";
  }
  if (
    ["SUCCESS", "COMPLETED", "DELIVERED", "THANH_CONG", "HOAN_THANH"].includes(
      key,
    )
  ) {
    return "SUCCESS";
  }
  if (["CANCELLED", "CANCELED", "DA_HUY", "HUY", "FAILED"].includes(key)) {
    return "CANCELLED";
  }
  if (["RETURN_REQUEST", "YEU_CAU_TRA", "TRA_HANG"].includes(key)) {
    return "RETURN_REQUEST";
  }
  if (["RETURNING", "DANG_HOAN"].includes(key)) {
    return "RETURNING";
  }
  if (["RETURNED", "DA_HOAN_TRA"].includes(key)) {
    return "RETURNED";
  }
  return key || "PENDING";
}

function isSuccessfulOrderStatus(status) {
  return normalizeOrderStatusKey(status) === "SUCCESS";
}

function getOrderStatusLabel(status) {
  const normalized = normalizeOrderStatusKey(status);
  return ORDER_STATUS_LABELS[normalized] || "Không xác định";
}

function getOrderStatusBadgeClass(status) {
  const normalized = normalizeOrderStatusKey(status);
  if (normalized === "SUCCESS") {
    return "bg-emerald-100 text-emerald-700 dark:bg-emerald-900/20 dark:text-emerald-400";
  }
  if (normalized === "SHIPPING") {
    return "bg-blue-100 text-blue-700 dark:bg-blue-900/20 dark:text-blue-400";
  }
  if (normalized === "CANCELLED") {
    return "bg-red-100 text-red-700 dark:bg-red-900/20 dark:text-red-400";
  }
  if (["RETURN_REQUEST", "RETURNING", "RETURNED"].includes(normalized)) {
    return "bg-amber-100 text-amber-700 dark:bg-amber-900/20 dark:text-amber-400";
  }
  return "bg-slate-100 text-slate-700 dark:bg-slate-900/20 dark:text-slate-300";
}

function getOrderAmount(order) {
  const amount =
    order?.finalAmount ?? order?.final_amount ?? order?.totalMoney ?? order?.total_money ?? 0;
  const parsed = Number(amount);
  return Number.isFinite(parsed) ? parsed : 0;
}

function formatCurrency(value) {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
    maximumFractionDigits: 0,
  }).format(Number(value) || 0);
}

function formatOrderDate(value) {
  const date = new Date(value || 0);
  if (Number.isNaN(date.getTime())) {
    return "Không rõ";
  }
  return new Intl.DateTimeFormat("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(date);
}

function formatRoleLabel(role) {
  const normalized = String(role || "USER")
    .trim()
    .toUpperCase();
  if (normalized === "ADMIN") return "Admin";
  if (normalized === "STAFF") return "Nhân viên";
  if (normalized === "CUSTOMER") return "Khách hàng";
  if (normalized === "GUEST") return "Khách vãng lai";
  return "Khách hàng";
}

function getRoleBadgeClasses(role) {
  const normalized = String(role || "USER")
    .trim()
    .toUpperCase();
  if (normalized === "STAFF") {
    return "bg-blue-100 text-blue-700 dark:bg-blue-900/20 dark:text-blue-400";
  }
  if (normalized === "CUSTOMER" || normalized === "USER") {
    return "bg-green-100 text-green-700 dark:bg-green-900/20 dark:text-green-400";
  }
  return "bg-slate-100 text-slate-700 dark:bg-slate-900/20 dark:text-slate-300";
}

function isCustomerOnlyRole(account) {
  const role = String(account?.role || "USER")
    .trim()
    .toUpperCase();
  return role === "USER" || role === "CUSTOMER";
}

function emptyForm() {
  return {
    username: "",
    gmail: "",
    phone: "",
    address: "",
    password: "",
    isActive: true,
    role: "USER",
  };
}

function getInitials(name) {
  const value = String(name || "").trim();
  if (!value) return "KH";

  const parts = value.split(/\s+/).filter(Boolean);
  if (parts.length === 1) {
    return parts[0].slice(0, 2).toUpperCase();
  }
  return `${parts[0][0] || ""}${parts[parts.length - 1][0] || ""}`.toUpperCase();
}

function openCreate() {
  editingAccountId.value = null;
  form.value = emptyForm();
  modalError.value = "";
  showModal.value = true;
}

function openEdit(account) {
  editingAccountId.value = account.id;
  form.value = {
    username: account.username || "",
    gmail: account.gmail || "",
    phone: account.phone || "",
    address: account.address || "",
    password: "",
    isActive: !!account.isActive,
    role: account.role || "USER",
  };
  modalError.value = "";
  showModal.value = true;
}

function closeModal() {
  showModal.value = false;
}

async function openOrderHistory(account) {
  orderHistoryAccount.value = account;
  showOrderHistoryModal.value = true;
  customerOrders.value = [];
  orderHistoryError.value = "";
  orderHistoryLoading.value = true;

  try {
    const orders = await orderAPI.getUserOrders(account.id);
    customerOrders.value = [...(Array.isArray(orders) ? orders : [])].sort(
      (a, b) =>
        new Date(b.createdAt || b.createdDate || 0).getTime() -
        new Date(a.createdAt || a.createdDate || 0).getTime(),
    );
  } catch (err) {
    orderHistoryError.value =
      err?.message || "Không thể tải lịch sử đơn hàng của khách hàng.";
  } finally {
    orderHistoryLoading.value = false;
  }
}

function closeOrderHistory() {
  showOrderHistoryModal.value = false;
  orderHistoryAccount.value = null;
  customerOrders.value = [];
  orderHistoryError.value = "";
}

function requestSave() {
  const f = form.value;
  modalError.value = "";

  if (!f.username || !f.gmail) {
    modalError.value = "Vui lòng điền đầy đủ Tên đăng nhập và Email.";
    return;
  }
  if (f.username.length > 100) {
    modalError.value = "Tên đăng nhập không được quá 100 ký tự.";
    return;
  }
  if (!/^[^\s@]+@gmail\.com$/.test(f.gmail)) {
    modalError.value = "Email phải có đuôi @gmail.com.";
    return;
  }
  if (f.phone && (!/^\d{10}$/.test(f.phone))) {
    modalError.value = "Số điện thoại phải là 10 chữ số.";
    return;
  }
  if (f.address && f.address.length > 100) {
    modalError.value = "Địa chỉ không được quá 100 ký tự.";
    return;
  }
  if (!editingAccountId.value && (!f.password || f.password.length < 6)) {
    modalError.value = "Mật khẩu phải có ít nhất 6 ký tự khi tạo mới.";
    return;
  }

  const isEdit = !!editingAccountId.value;
  requestConfirm({
    title: isEdit ? "Xác nhận cập nhật" : "Xác nhận thêm mới",
    message: isEdit
      ? `Bạn chắc chắn muốn cập nhật người dùng "${f.username}"?`
      : `Bạn chắc chắn muốn thêm người dùng "${f.username}"?`,
    confirmText: isEdit ? "Cập nhật" : "Thêm mới",
    action: saveAccount,
  });
}

async function saveAccount() {
  const f = form.value;
  modalError.value = "";

  if (!f.username || !f.gmail) {
    modalError.value = "Vui lòng điền đầy đủ Tên đăng nhập và Email.";
    return;
  }

  if (!editingAccountId.value && (!f.password || f.password.length < 6)) {
    modalError.value = "Mật khẩu phải có ít nhất 6 ký tự khi tạo mới.";
    return;
  }

  saving.value = true;
  try {
    const payload = {
      username: f.username,
      gmail: f.gmail,
      phone: f.phone || "",
      address: f.address || "",
      isActive: !!f.isActive,
      role: canManageRoles.value ? f.role : "USER",
    };

    if (!editingAccountId.value && f.password) {
      payload.password = f.password;
    }

    const isEdit = !!editingAccountId.value;

    if (editingAccountId.value) {
      await adminApi.updateAccount(editingAccountId.value, payload);
    } else {
      await adminApi.createAccount(payload);
    }

    closeModal();
    await loadAccounts();
    showToast(isEdit ? "Cập nhật tài khoản thành công!" : "Thêm tài khoản thành công!");
  } catch (err) {
    modalError.value = err?.message || "Không thể lưu tài khoản khách hàng.";
  } finally {
    saving.value = false;
  }
}

function requestToggleActive(account) {
  const locking = !!account.isActive;
  requestConfirm({
    title: locking ? "Xác nhận khóa tài khoản" : "Xác nhận mở khóa",
    message: locking
      ? `Bạn chắc chắn muốn khóa tài khoản "${account.username}"?`
      : `Bạn chắc chắn muốn mở khóa tài khoản "${account.username}"?`,
    confirmText: locking ? "Khóa" : "Mở khóa",
    danger: locking,
    action: () => toggleActive(account),
  });
}

async function toggleActive(account) {
  try {
    const wasActive = !!account.isActive;
    await adminApi.updateAccount(account.id, {
      username: account.username,
      gmail: account.gmail,
      phone: account.phone || "",
      address: account.address || "",
      isActive: !account.isActive,
      role: account.role || "USER",
    });

    account.isActive = !account.isActive;
    showToast(wasActive ? `Đã khóa tài khoản "${account.username}".` : `Đã mở khóa tài khoản "${account.username}".`);
  } catch (err) {
    error.value = err?.message || "Không thể cập nhật trạng thái tài khoản.";
  }
}

async function loadAccounts() {
  loading.value = true;
  error.value = "";

  try {
    const data = await adminApi.getCustomerAccounts();
    const accountsToShow = Array.isArray(data) ? data : [];

    accounts.value = [...accountsToShow]
      .filter((account) => isCustomerOnlyRole(account))
      .sort((a, b) => Number(b.id || 0) - Number(a.id || 0));
  } catch (err) {
    console.error("Failed to load customer accounts:", err);
    error.value =
      "Không thể tải danh sách người dùng. Vui lòng kiểm tra backend API.";
  } finally {
    loading.value = false;
  }
}

onMounted(loadAccounts);
</script>
