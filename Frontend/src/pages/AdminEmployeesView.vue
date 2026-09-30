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
          <p class="text-xs uppercase tracking-[0.18em] text-primary">Quản lý nhân viên</p>
          <h2 class="mt-1 text-xl font-bold">Danh sách nhân viên</h2>
          <p class="mt-1 text-sm text-slate-500 dark:text-[#b9aa9a]">
            {{ isAdmin ? "ADMIN xem tất cả nhân viên." : "Nhân viên chỉ xem được danh sách cơ bản." }}
          </p>
        </div>
        <div class="flex flex-wrap items-center gap-2">
          <button v-if="isAdmin" type="button" class="inline-flex items-center gap-1.5 rounded-xl bg-primary px-3 py-1.5 text-sm font-medium text-white hover:opacity-90" @click="openCreate">
            <span class="material-symbols-outlined text-[18px]">person_add</span>
            Thêm nhân viên
          </button>
          <button type="button" class="inline-flex items-center gap-1.5 rounded-xl border border-slate-900 bg-white px-3 py-1.5 text-sm font-medium hover:border-primary hover:text-primary dark:border-[#3c342e] dark:bg-[#1f1a17]" @click="loadAccounts">
            <span class="material-symbols-outlined text-[18px]">refresh</span>
            Làm mới
          </button>
        </div>
      </div>
    </template>

    <div class="space-y-3">
      <section class="retail-card p-3">
        <div class="grid gap-3 md:grid-cols-[1fr_220px]">
          <label class="flex items-center gap-2 rounded-xl border border-slate-900 bg-white px-3 py-1.5 dark:border-[#3c342e] dark:bg-[#1f1a17]">
            <span class="material-symbols-outlined text-[18px] text-slate-400">search</span>
            <input v-model="search" type="text" placeholder="Tìm theo tên, email, số điện thoại" class="w-full bg-transparent text-sm outline-none" />
          </label>
          <select v-model="statusFilter" class="rounded-xl border border-slate-900 bg-gray-50 px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#1f1a17]">
            <option value="ALL">Tất cả trạng thái</option>
            <option value="ACTIVE">Đang hoạt động</option>
            <option value="LOCKED">Tạm khóa</option>
          </select>
        </div>
      </section>

      <section class="retail-card p-3">
        <div class="mb-4 text-sm text-slate-500 dark:text-[#b9aa9a]">
          Tổng nhân viên: <span class="font-semibold text-slate-900 dark:text-white">{{ filteredAccounts.length }}</span>
          <span class="mx-2">/</span>
          Tổng dữ liệu: <span class="font-semibold text-slate-900 dark:text-white">{{ accounts.length }}</span>
        </div>
        <div v-if="error" class="mb-4 rounded-xl border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">{{ error }}</div>
        <div v-if="loading" class="py-8 text-center text-sm text-slate-500">Đang tải danh sách nhân viên...</div>
        <div v-else-if="filteredAccounts.length === 0" class="rounded-2xl border border-dashed border-slate-300 p-6 text-center text-sm text-slate-500">Không tìm thấy nhân viên phù hợp.</div>
        <div v-else class="overflow-x-auto">
          <table class="w-full min-w-[920px] text-left text-xs">
            <thead>
              <tr class="border-b border-slate-900 text-slate-500 dark:border-[#3c342e] dark:text-[#b9aa9a]">
                <th class="px-2 py-2 font-medium">STT</th>
                <th class="px-2 py-2 font-medium">Nhân viên</th>
                <th class="px-2 py-2 font-medium">Vai trò</th>
                <th class="px-2 py-2 font-medium">Liên hệ</th>
                <th class="px-2 py-2 font-medium">Trạng thái</th>
                <th class="px-2 py-2 font-medium">Địa chỉ</th>
                <th class="px-2 py-2 text-right font-medium">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(account, index) in paginatedAccounts" :key="account.id" class="border-b border-slate-100 last:border-none dark:border-[#2b241f]">
                <td class="px-2 py-2 font-semibold text-slate-500">{{ (accountsPage - 1) * 10 + index + 1 }}</td>
                <td class="px-2 py-2">
                  <div class="flex items-center gap-3">
                    <div class="size-10 overflow-hidden rounded-full bg-slate-100 dark:bg-[#2b241f]">
                      <img v-if="account.imgUrl" :src="account.imgUrl" alt="Avatar" class="h-full w-full object-cover" />
                      <span v-else class="flex h-full items-center justify-center text-xs text-slate-500">{{ getInitials(account.username) }}</span>
                    </div>
                    <div>
                      <p class="font-semibold">{{ account.username || "Nhân viên" }}</p>
                      <p class="text-xs text-slate-500">ID: {{ account.id }}</p>
                    </div>
                  </div>
                </td>
                <td class="px-2 py-2">
                  <span :class="getRoleBadgeClasses(account.role)" class="rounded-full px-2 py-1 text-xs font-semibold">{{ formatRoleLabel(account.role) }}</span>
                </td>
                <td class="px-2 py-2 text-slate-500 dark:text-[#b9aa9a]">
                  <p>{{ account.gmail || "-" }}</p>
                  <p>{{ account.phone || "-" }}</p>
                </td>
                <td class="px-2 py-2">
                  <span class="rounded-full px-2 py-1 text-xs font-semibold" :class="account.isActive ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'">{{ account.isActive ? "Hoạt động" : "Tạm khóa" }}</span>
                </td>
                <td class="px-2 py-2 text-slate-500">{{ account.address || "-" }}</td>
                <td class="px-2 py-2 text-right">
                  <div class="flex items-center justify-end gap-1">
                    <button v-if="isCustomerOnlyRole(account)" class="rounded-lg p-1.5 hover:bg-blue-50" title="Lịch sử đơn hàng" @click="openOrderHistory(account)">
                      <span class="material-symbols-outlined text-[18px] text-blue-500">receipt_long</span>
                    </button>
                    <button class="rounded-lg p-1.5 hover:bg-slate-100" title="Chỉnh sửa" @click="openEdit(account)">
                      <span class="material-symbols-outlined text-[18px] text-slate-500">edit</span>
                    </button>
                    <button class="rounded-lg p-1.5 hover:bg-amber-50" :title="account.isActive ? 'Khóa tài khoản' : 'Mở khóa tài khoản'" @click="requestToggleActive(account)">
                      <span class="material-symbols-outlined text-[18px] text-amber-500">{{ account.isActive ? "lock" : "lock_open" }}</span>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <PaginationBar :current-page="accountsPage" :total-pages="accountsTotalPages" :page-start="accountsPageStart" :page-end="accountsPageEnd" :total-items="accountsTotalItems" label="nhân viên" @previous="accountsPreviousPage" @next="accountsNextPage" />
      </section>
    </div>

    <Teleport to="body">
      <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4" @click.self="closeModal">
        <div class="w-full max-w-lg max-h-[90vh] overflow-y-auto rounded-2xl bg-white p-4 shadow-2xl dark:bg-[#1f1a17]">
          <div class="relative mb-4 flex items-center">
            <h3 class="text-base font-bold">{{ editingAccountId ? `Sửa người dùng #${editingAccountId}` : "Thêm người dùng mới" }}</h3>
            <button type="button" class="absolute right-0 top-0 inline-flex size-8 items-center justify-center rounded-lg text-slate-400 hover:bg-slate-100 hover:text-slate-600 dark:hover:bg-slate-800" @click="closeModal">
              <span class="material-symbols-outlined text-[20px]">close</span>
            </button>
          </div>
          <div class="grid gap-3 md:grid-cols-2">
            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500">Tên đăng nhập *</label>
              <input v-model.trim="form.username" type="text" class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]" placeholder="Nhập tên đăng nhập" />
            </div>
            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500">Email *</label>
              <input v-model.trim="form.gmail" type="email" class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]" placeholder="example@email.com" />
            </div>
            <div v-if="canManageRoles">
              <label class="mb-1 block text-xs font-medium text-slate-900">Vai trò</label>
              <select v-model="form.role" class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]">
                <option value="ADMIN">Admin</option>
                <option value="STAFF">Nhân viên</option>
              </select>
            </div>
            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500">Số điện thoại</label>
              <input v-model.trim="form.phone" type="text" class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]" placeholder="09..." />
            </div>
            <div v-if="!editingAccountId">
              <label class="mb-1 block text-xs font-medium text-slate-500">Mật khẩu *</label>
              <input v-model="form.password" type="password" class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]" placeholder="Tối thiểu 6 ký tự" />
            </div>
            <div class="md:col-span-2">
              <label class="mb-1 block text-xs font-medium text-slate-500">Địa chỉ</label>
              <textarea v-model.trim="form.address" rows="3" class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]" placeholder="Nhập địa chỉ nhân viên" />
            </div>
            <label class="md:col-span-2 inline-flex items-center gap-2 text-sm">
              <input v-model="form.isActive" type="checkbox" />
              <span>Tài khoản đang hoạt động</span>
            </label>
          </div>
          <p v-if="modalError" class="mt-3 text-xs text-red-500">{{ modalError }}</p>
          <div class="mt-4 flex justify-end gap-2">
            <button type="button" :disabled="saving" class="rounded-xl bg-primary px-4 py-2 text-sm font-medium text-white hover:opacity-90 disabled:opacity-60" @click="requestSave">{{ saving ? "Đang lưu..." : editingAccountId ? "Cập nhật" : "Thêm mới" }}</button>
          </div>
        </div>
      </div>
    </Teleport>

    <Teleport to="body">
      <div v-if="showOrderHistoryModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4" @click.self="closeOrderHistory">
        <div class="w-full max-w-3xl max-h-[90vh] overflow-y-auto rounded-2xl bg-white p-4 shadow-2xl dark:bg-[#1f1a17]">
          <div class="mb-4 flex flex-wrap items-start justify-between gap-3">
            <div>
              <h3 class="text-base font-bold">Lịch sử đơn hàng - {{ orderHistoryAccount?.username || "Nhân viên" }}</h3>
              <p class="mt-1 text-xs text-slate-500">ID: {{ orderHistoryAccount?.id }}</p>
            </div>
            <button type="button" class="rounded-lg p-1.5 hover:bg-slate-100" @click="closeOrderHistory"><span class="material-symbols-outlined text-[20px]">close</span></button>
          </div>
          <div class="mb-4 grid gap-3 sm:grid-cols-3">
            <div class="rounded-xl border border-slate-900 bg-slate-50 px-3 py-2"><p class="text-xs text-slate-500">Tổng đơn hàng</p><p class="mt-1 text-lg font-bold">{{ customerOrders.length }}</p></div>
            <div class="rounded-xl border border-emerald-200 bg-emerald-50 px-3 py-2"><p class="text-xs text-emerald-600">Đơn thành công</p><p class="mt-1 text-lg font-bold text-emerald-700">{{ successfulOrdersCount }}</p></div>
            <div class="rounded-xl border border-primary/20 bg-primary/5 px-3 py-2"><p class="text-xs text-primary">Tổng tiền đã chi</p><p class="mt-1 text-lg font-bold text-primary">{{ formatCurrency(customerTotalSpent) }}</p></div>
          </div>
          <div v-if="orderHistoryLoading" class="py-8 text-center text-sm text-slate-500">Dang tai lich su don hang...</div>
          <div v-else-if="orderHistoryError" class="rounded-xl border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">{{ orderHistoryError }}</div>
          <div v-else-if="customerOrders.length === 0" class="rounded-2xl border border-dashed border-slate-300 p-6 text-center text-sm text-slate-500">Nhân viên chưa có đơn hàng nào.</div>
          <div v-else class="overflow-x-auto">
            <table class="w-full min-w-[640px] text-left text-xs">
              <thead><tr class="border-b border-slate-900 text-slate-500"><th class="px-2 py-2 font-medium">Mã đơn</th><th class="px-2 py-2 font-medium">Ngày đặt</th><th class="px-2 py-2 font-medium">Trạng thái</th><th class="px-2 py-2 text-right font-medium">Thành tiền</th></tr></thead>
              <tbody>
                <tr v-for="order in customerOrders" :key="order.id" class="border-b border-slate-100 last:border-none">
                  <td class="px-2 py-2 font-semibold">#{{ order.id }}</td>
                  <td class="px-2 py-2 text-slate-500">{{ formatOrderDate(order.createdAt || order.createdDate) }}</td>
                  <td class="px-2 py-2"><span class="rounded-full px-2 py-1 text-xs font-semibold" :class="getOrderStatusBadgeClass(order.status)">{{ getOrderStatusLabel(order.status) }}</span></td>
                  <td class="px-2 py-2 text-right font-semibold">{{ formatCurrency(getOrderAmount(order)) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </Teleport>

    <ConfirmDialog :open="!!confirmModal" :title="confirmModal?.title" :message="confirmModal?.message" :confirm-text="confirmModal?.confirmText" :danger="!!confirmModal?.danger" :loading="!!confirmModal?.loading" @confirm="executeConfirm" @cancel="cancelConfirm" />
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
import { adminApi, accountsApi } from "@/services/api";
import { orderAPI } from "@/services/orders";

const { confirmModal, requestConfirm, executeConfirm, cancelConfirm } = useConfirmDialog();
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
function showToast(message, duration = 3000) { successToast.value = message; setTimeout(() => { successToast.value = ""; }, duration); }
const showOrderHistoryModal = ref(false);
const orderHistoryAccount = ref(null);
const customerOrders = ref([]);
const orderHistoryLoading = ref(false);
const orderHistoryError = ref("");
const ORDER_STATUS_LABELS = { PENDING: "Chờ xử lý", PROCESSING: "Đóng gói và xử lý", SHIPPING: "Đang giao", SUCCESS: "Thành công", RETURN_REQUEST: "Yêu cầu hoàn", RETURNING: "Đang hoàn về", RETURNED: "Đã hoàn trả", CANCELLED: "Đã hủy" };
const isAdmin = computed(() => currentUserRole.value === "ADMIN");
const canManageRoles = computed(() => isAdmin.value);
const filteredAccounts = computed(() => {
  const keyword = search.value.trim().toLowerCase();
  return accounts.value.filter((account) => {
    const activeMatch = statusFilter.value === "ALL" || (statusFilter.value === "ACTIVE" ? !!account.isActive : !account.isActive);
    if (!activeMatch) return false;
    if (!keyword) return true;
    const haystack = [account.username, account.gmail, account.phone, account.address, account.id, account.role].filter((item) => item !== undefined && item !== null).join(" ").toLowerCase();
    return haystack.includes(keyword);
  });
});
const { currentPage: accountsPage, totalPages: accountsTotalPages, paginatedItems: paginatedAccounts, pageStart: accountsPageStart, pageEnd: accountsPageEnd, totalItems: accountsTotalItems, previousPage: accountsPreviousPage, nextPage: accountsNextPage } = usePagination(filteredAccounts, 10);
const successfulOrdersCount = computed(() => customerOrders.value.filter((order) => isSuccessfulOrderStatus(order.status)).length);
const customerTotalSpent = computed(() => customerOrders.value.filter((order) => isSuccessfulOrderStatus(order.status)).reduce((sum, order) => sum + getOrderAmount(order), 0));
function normalizeOrderStatusKey(status) {
  const key = String(status || "").trim().replace(/\s+/g, "_").replace(/-/g, "_").toUpperCase();
  if (["NEW","PENDING","WAITING","CHUA_XU_LY","CHO_XU_LY"].includes(key)) return "PENDING";
  if (["CONFIRMED","PROCESSING","DANG_XU_LY","XAC_NHAN","IN_PROGRESS"].includes(key)) return "PROCESSING";
  if (["SHIPPING","DELIVERING","DANG_GIAO","IN_TRANSIT"].includes(key)) return "SHIPPING";
  if (["SUCCESS","COMPLETED","DELIVERED","THANH_CONG","HOAN_THANH"].includes(key)) return "SUCCESS";
  if (["CANCELLED","CANCELED","DA_HUY","HUY","FAILED"].includes(key)) return "CANCELLED";
  if (["RETURN_REQUEST","YEU_CAU_TRA","TRA_HANG"].includes(key)) return "RETURN_REQUEST";
  if (["RETURNING","DANG_HOAN"].includes(key)) return "RETURNING";
  if (["RETURNED","DA_HOAN_TRA"].includes(key)) return "RETURNED";
  return key || "PENDING";
}
function isSuccessfulOrderStatus(status) { return normalizeOrderStatusKey(status) === "SUCCESS"; }
function getOrderStatusLabel(status) { const normalized = normalizeOrderStatusKey(status); return ORDER_STATUS_LABELS[normalized] || "Không xác định"; }
function getOrderStatusBadgeClass(status) {
  const normalized = normalizeOrderStatusKey(status);
  if (normalized === "SUCCESS") return "bg-emerald-100 text-emerald-700";
  if (normalized === "SHIPPING") return "bg-blue-100 text-blue-700";
  if (normalized === "CANCELLED") return "bg-red-100 text-red-700";
  if (["RETURN_REQUEST","RETURNING","RETURNED"].includes(normalized)) return "bg-amber-100 text-amber-700";
  return "bg-slate-100 text-slate-700";
}
function getOrderAmount(order) { const amount = order?.finalAmount ?? order?.final_amount ?? order?.totalMoney ?? order?.total_money ?? 0; const parsed = Number(amount); return Number.isFinite(parsed) ? parsed : 0; }
function formatCurrency(value) { return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND", maximumFractionDigits: 0 }).format(Number(value) || 0); }
function formatOrderDate(value) { const date = new Date(value || 0); if (Number.isNaN(date.getTime())) return "Khong ro"; return new Intl.DateTimeFormat("vi-VN", { day: "2-digit", month: "2-digit", year: "numeric", hour: "2-digit", minute: "2-digit" }).format(date); }
function formatRoleLabel(role) { const n = String(role || "USER").trim().toUpperCase(); if (n === "ADMIN") return "Admin"; if (n === "STAFF") return "Nhân viên"; if (n === "CUSTOMER") return "Khách hàng"; return "Khách hàng"; }
function getRoleBadgeClasses(role) { const n = String(role || "USER").trim().toUpperCase(); if (n === "STAFF") return "bg-blue-100 text-blue-700"; if (n === "CUSTOMER" || n === "USER") return "bg-green-100 text-green-700"; return "bg-slate-100 text-slate-700"; }
function isCustomerOnlyRole(account) { const role = String(account?.role || "USER").trim().toUpperCase(); return role === "USER" || role === "CUSTOMER"; }
function emptyForm() { return { username: "", gmail: "", phone: "", address: "", password: "", isActive: true, role: "STAFF" }; }
function getInitials(name) { const value = String(name || "").trim(); if (!value) return "NV"; const parts = value.split(/\s+/).filter(Boolean); if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase(); return `${parts[0][0] || ""}${parts[parts.length - 1][0] || ""}`.toUpperCase(); }
function openCreate() { editingAccountId.value = null; form.value = emptyForm(); modalError.value = ""; showModal.value = true; }
function openEdit(account) { editingAccountId.value = account.id; form.value = { username: account.username || "", gmail: account.gmail || "", phone: account.phone || "", address: account.address || "", password: "", isActive: !!account.isActive, role: account.role || "STAFF" }; modalError.value = ""; showModal.value = true; }
function closeModal() { showModal.value = false; }
async function openOrderHistory(account) {
  orderHistoryAccount.value = account; showOrderHistoryModal.value = true; customerOrders.value = []; orderHistoryError.value = ""; orderHistoryLoading.value = true;
  try { const orders = await orderAPI.getUserOrders(account.id); customerOrders.value = [...(Array.isArray(orders) ? orders : [])].sort((a, b) => new Date(b.createdAt || b.createdDate || 0).getTime() - new Date(a.createdAt || a.createdDate || 0).getTime()); }
  catch (err) { orderHistoryError.value = err?.message || "Không thể tải lich su don hang."; }
  finally { orderHistoryLoading.value = false; }
}
function closeOrderHistory() { showOrderHistoryModal.value = false; orderHistoryAccount.value = null; customerOrders.value = []; orderHistoryError.value = ""; }
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
  if (f.phone && !/^\d{10}$/.test(f.phone)) {
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
  requestConfirm({ title: isEdit ? "Xác nhận cập nhật" : "Xác nhận thêm mới", message: isEdit ? `Bạn chắc chắn muốn cập nhật người dùng "${f.username}"?` : `Bạn chắc chắn muốn thêm người dùng "${f.username}"?`, confirmText: isEdit ? "Cập nhật" : "Thêm mới", action: saveAccount });
}
async function saveAccount() {
  const f = form.value; modalError.value = "";
  if (!f.username || !f.gmail) { modalError.value = "Vui long dien day du Tên đăng nhập va Email."; return; }
  if (!editingAccountId.value && (!f.password || f.password.length < 6)) { modalError.value = "Mật khẩu phai co it nhat 6 ky tu khi tao moi."; return; }
  saving.value = true;
  try {
    const payload = { username: f.username, gmail: f.gmail, phone: f.phone || "", address: f.address || "", isActive: !!f.isActive, role: canManageRoles.value ? f.role : "STAFF" };
    if (!editingAccountId.value && f.password) payload.password = f.password;
    const isEdit = !!editingAccountId.value;
    if (editingAccountId.value) { await accountsApi.update(editingAccountId.value, payload); } else { await accountsApi.create(payload); }
    closeModal(); await loadAccounts(); showToast(isEdit ? "Cập nhật tai khoan thanh cong!" : "Them tai khoan thanh cong!");
  } catch (err) { modalError.value = err?.message || "Không thể lưu tài khoản nhân viên."; }
  finally { saving.value = false; }
}
function requestToggleActive(account) {
  const locking = !!account.isActive;
  requestConfirm({ title: locking ? "Xác nhận khóa tài khoản" : "Xác nhận mở khóa", message: locking ? `Bạn chắc chắn muốn khóa tài khoản "${account.username}"?` : `Bạn chắc chắn muốn mở khóa tài khoản "${account.username}"?`, confirmText: locking ? "Khoa" : "Mo khoa", danger: locking, action: () => toggleActive(account) });
}
async function toggleActive(account) {
  try {
    const wasActive = !!account.isActive;
    await accountsApi.update(account.id, { username: account.username, gmail: account.gmail, phone: account.phone || "", address: account.address || "", isActive: !account.isActive, role: account.role || "STAFF" });
    account.isActive = !account.isActive;
    showToast(wasActive ? `Đã khóa tài khoản "${account.username}".` : `Đã mở khóa tài khoản "${account.username}".`);
  } catch (err) { error.value = err?.message || "Không thể cập nhật trang thai tai khoan."; }
}
async function loadAccounts() {
  loading.value = true; error.value = "";
  try {
    const data = await accountsApi.getEmployees();
    accounts.value = [...(Array.isArray(data) ? data : [])].sort((a, b) => Number(b.id || 0) - Number(a.id || 0));
  } catch (err) { console.error("Failed to load employee accounts:", err); error.value = "Không thể tải danh sách nhân viên. Vui lòng kiểm tra backend API."; }
  finally { loading.value = false; }
}
onMounted(loadAccounts);
</script>
