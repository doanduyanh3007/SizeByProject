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
            Quản lý đơn hàng
          </p>
          <h2 class="mt-1 text-xl font-bold">Quản lý đơn hàng</h2>
          <p class="mt-1 text-sm text-slate-500 dark:text-[#b9aa9a]">
            Lợi nhuận được tính theo trạng thái thanh toán: đã thanh toán cộng,
            hoàn tiền trừ.
          </p>
        </div>

        <div class="flex flex-wrap items-center gap-2">
          <button
            type="button"
            class="inline-flex items-center gap-1.5 rounded-xl border border-slate-200 bg-white px-3 py-1.5 text-sm font-medium hover:border-primary hover:text-primary dark:border-[#3c342e] dark:bg-[#1f1a17]"
            @click="loadOrders"
          >
            <span class="material-symbols-outlined text-[18px]">refresh</span>
            Làm mới
          </button>
        </div>
      </div>
    </template>

    <div class="space-y-3">
      <section class="retail-card p-3">
        <div class="grid gap-3 md:grid-cols-[1fr_220px_220px]">
          <label
            class="flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-1.5 dark:border-[#3c342e] dark:bg-[#1f1a17]"
          >
            <span class="material-symbols-outlined text-[18px] text-slate-400"
              >search</span
            >
            <input
              v-model="search"
              type="text"
              placeholder="Tìm theo ID, tên khách hàng, số điện thoại"
              class="w-full bg-transparent text-sm outline-none"
            />
          </label>

          <label
            class="flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-1.5 dark:border-[#3c342e] dark:bg-[#1f1a17]"
          >
            <span class="material-symbols-outlined text-[18px] text-slate-400"
              >filter_list</span
            >
            <select
              v-model="statusFilter"
              class="w-full cursor-pointer bg-transparent text-sm text-slate-900 outline-none dark:text-white"
            >
              <option value="ALL">Tất cả trạng thái</option>
              <option
                v-for="status in statusOptions"
                :key="status.value"
                :value="status.value"
              >
                {{ status.label }}
              </option>
            </select>
          </label>

          <label
            class="flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-1.5 dark:border-[#3c342e] dark:bg-[#1f1a17]"
          >
            <span class="material-symbols-outlined text-[18px] text-slate-400"
              >storefront</span
            >
            <select
              v-model="channelFilter"
              class="w-full cursor-pointer bg-transparent text-sm text-slate-900 outline-none dark:text-white"
            >
              <option value="ALL">Tất cả kênh bán</option>
              <option value="ONLINE">Đơn Online</option>
              <option value="POS">Đơn tại quầy (POS)</option>
            </select>
          </label>
        </div>

        <div class="hidden">
          <button
            class="rounded-full px-4 py-1.5 text-sm font-medium transition-colors"
            :class="statusFilter === 'ALL' ? 'bg-primary text-white' : 'bg-slate-100 text-slate-600 hover:bg-slate-200 dark:bg-[#2b241f] dark:text-[#b9aa9a] dark:hover:bg-[#3c342e]'"
            @click="statusFilter = 'ALL'"
          >
            Tất cả
          </button>
          <button
            v-for="status in statusOptions"
            :key="status.value"
            class="rounded-full px-4 py-1.5 text-sm font-medium transition-colors"
            :class="statusFilter === status.value ? 'bg-primary text-white' : 'bg-slate-100 text-slate-600 hover:bg-slate-200 dark:bg-[#2b241f] dark:text-[#b9aa9a] dark:hover:bg-[#3c342e]'"
            @click="statusFilter = status.value"
          >
            {{ status.label }}
          </button>
        </div>
      </section>

      <section class="retail-card p-3">
        <div class="mb-3 grid grid-cols-1 gap-2 text-xs md:grid-cols-4">
          <div
            class="rounded-xl border border-slate-200 px-3 py-2 dark:border-[#3c342e]"
          >
            Tổng đơn: <span class="font-semibold">{{ orders.length }}</span>
          </div>
          <div
            class="rounded-xl border border-slate-200 px-3 py-2 dark:border-[#3c342e]"
          >
            Đơn ghi nhận doanh thu:
            <span class="font-semibold">{{ profitableOrdersCount }}</span>
          </div>
          <div
            class="rounded-xl border border-slate-200 px-3 py-2 dark:border-[#3c342e]"
          >
            Đơn hoàn tiền:
            <span class="font-semibold">{{ deductedOrdersCount }}</span>
          </div>
          <div
            class="rounded-xl border border-slate-200 px-3 py-2 dark:border-[#3c342e]"
          >
            Lợi nhuận ròng:
            <span class="font-semibold">{{ formatCurrency(netRevenue) }}</span>
          </div>
        </div>

        <!-- Order Progress Tracker -->
        <div v-if="activeTrackedOrder" class="mb-4 rounded-xl border border-slate-200 bg-white p-4 dark:border-[#3c342e] dark:bg-[#1f1a17]">
          <div class="mb-4 flex items-center justify-between">
            <h3 class="font-bold text-slate-800 dark:text-white flex items-center gap-2">
              <span class="material-symbols-outlined text-primary">local_shipping</span>
              Theo dõi tiến độ đơn hàng <span class="text-primary">#{{ activeTrackedOrder.id }}</span>
            </h3>
            <span class="text-xs text-slate-500 dark:text-[#b9aa9a]">Khách hàng: {{ activeTrackedOrder.fullName || activeTrackedOrder.fullname || 'Khách hàng' }}</span>
          </div>
          
          <div class="relative flex items-center justify-between w-full mt-6 px-4">
            <div class="absolute left-4 right-4 top-1/2 h-1 -translate-y-1/2 bg-slate-100 dark:bg-[#2b241f] rounded-full"></div>
            <div class="absolute left-4 top-1/2 h-1 -translate-y-1/2 rounded-full transition-all duration-500" 
                 :class="trackingSteps.some(s => s.isError) ? 'bg-red-500' : (trackingSteps.some(s => s.isWarning) ? 'bg-amber-500' : 'bg-primary')"
                 :style="{ width: `calc(${(Math.max(0, trackingSteps.map(s => s.isActive).lastIndexOf(true)) / (trackingSteps.length - 1 || 1) * 100)}% - 32px)` }"></div>
            
            <div v-for="(step, idx) in trackingSteps" :key="idx" class="relative flex flex-col items-center gap-2" :class="step.isActive ? (step.isError ? 'text-red-500' : step.isWarning ? 'text-amber-500' : 'text-primary') : 'text-slate-400 dark:text-slate-600'">
              <div class="flex h-8 w-8 items-center justify-center rounded-full border-2 bg-white dark:bg-[#1f1a17] transition-colors" 
                   :class="step.isActive ? (step.isError ? 'border-red-500 shadow-[0_0_10px_rgba(239,68,68,0.3)]' : step.isWarning ? 'border-amber-500 shadow-[0_0_10px_rgba(245,158,11,0.3)]' : 'border-primary shadow-[0_0_10px_rgba(198,98,49,0.3)]') : 'border-slate-200 dark:border-[#3c342e]'">
                <span class="material-symbols-outlined text-[16px]">{{ step.icon }}</span>
              </div>
              <span class="text-xs font-semibold whitespace-nowrap">{{ step.label }}</span>
            </div>
          </div>
        </div>

        <div
          v-if="error"
          class="mb-4 rounded-xl border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700 dark:border-red-900/40 dark:bg-red-900/20 dark:text-red-300"
        >
          {{ error }}
        </div>

        <div
          v-if="hiddenOrders.length > 0"
          class="mb-4 rounded-xl border border-amber-200 bg-amber-50 px-3 py-3 text-sm text-slate-700 dark:border-amber-900/20 dark:bg-[#3f2e11] dark:text-[#fbe7a1]"
        >
          <div class="flex flex-wrap items-center justify-between gap-3">
            <div>
              <p class="font-semibold">Đơn hàng ẩn tạm</p>
              <p class="text-xs text-slate-500 dark:text-[#e1c797]">
                Những đơn hàng đã xóa mềm sẽ tạm ẩn khỏi danh sách được dùng.
                Bạn có thể khôi phục lại khi cần.
              </p>
            </div>
            <button
              type="button"
              class="rounded-xl bg-primary px-3 py-2 text-xs font-semibold text-white hover:opacity-90"
              @click="restoreAllHiddenOrders"
            >
              Khôi phục tất cả
            </button>
          </div>
          <div class="mt-3 space-y-2">
            <div
              v-for="order in hiddenOrders"
              :key="order.id"
              class="flex flex-wrap items-center justify-between gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2 dark:border-[#4c4138] dark:bg-[#1e1812]"
            >
              <div class="text-sm">
                <span class="font-semibold">#{{ order.id }}</span>
                <span class="ml-2">{{ order.fullName || "Khách hàng" }}</span>
              </div>
              <button
                type="button"
                class="rounded-xl bg-emerald-500 px-3 py-2 text-xs font-semibold text-white hover:bg-emerald-600"
                @click="restoreOrder(order)"
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
          Đang tải danh sách đơn hàng...
        </div>

        <div
          v-else-if="filteredOrders.length === 0"
          class="rounded-2xl border border-dashed border-slate-300 p-6 text-center text-sm text-slate-500 dark:border-[#3c342e] dark:text-[#b9aa9a]"
        >
          Không tìm thấy đơn hàng phù hợp.
        </div>

        <div v-else class="admin-orders-table-scroll overflow-x-auto">
          <table class="admin-orders-table w-full min-w-[1328px] table-fixed text-left text-xs">
            <colgroup>
              <col class="w-[64px]" />
              <col class="w-[140px]" />
              <col class="w-[190px]" />
              <col class="w-[145px]" />
              <col class="w-[220px]" />
              <col class="w-[120px]" />
              <col class="w-[140px]" />
              <col class="w-[132px]" />
              <col class="w-[145px]" />
              <col class="w-[112px]" />
            </colgroup>
            <thead>
              <tr
                class="border-b border-slate-200 text-slate-500 dark:border-[#3c342e] dark:text-[#b9aa9a]"
              >
                <th class="sticky left-0 z-20 bg-white px-2 py-2 font-medium shadow-[1px_0_0_rgba(226,232,240,0.9)]">ID</th>
                <th class="px-2 py-2 font-medium">Khách hàng</th>
                <th class="px-2 py-2 font-medium">Sản phẩm</th>
                <th class="px-2 py-2 font-medium">Liên hệ</th>
                <th class="px-2 py-2 font-medium">
                  Địa chỉ giao hàng
                </th>
                <th class="px-2 py-2 font-medium">Tổng tiền</th>
                <th class="px-2 py-2 font-medium">Thanh toán</th>
                <th class="px-2 py-2 font-medium whitespace-nowrap">Trạng thái</th>
                <th class="px-2 py-2 font-medium">Ngày tạo</th>
                <th class="sticky right-0 z-20 bg-white px-2 py-2 text-right font-medium shadow-[-1px_0_0_rgba(226,232,240,0.9)]">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="order in paginatedOrders"
                :key="order.id"
                class="border-b border-slate-100 last:border-none dark:border-[#2b241f] cursor-pointer transition-colors hover:bg-slate-50 dark:hover:bg-[#2b241f]/80"
                :class="{ 'bg-primary/5 dark:bg-primary/10 border-l-2 border-l-primary': activeTrackedOrder?.id === order.id }"
                @click="selectOrderToTrack(order)"
              >
                <td class="sticky left-0 z-10 bg-white px-2 py-2 font-semibold shadow-[1px_0_0_rgba(226,232,240,0.75)]">
                  #{{ order.id || "-" }}
                  <span
                    v-if="isPosOrder(order)"
                    class="ml-1 inline-block rounded bg-amber-100 px-1.5 py-0.5 text-[10px] font-bold text-amber-700 dark:bg-amber-900/30 dark:text-amber-400"
                  >POS</span>
                  <span
                    v-else
                    class="ml-1 inline-block rounded bg-blue-100 px-1.5 py-0.5 text-[10px] font-bold text-blue-700 dark:bg-blue-900/30 dark:text-blue-400"
                  >Online</span>
                </td>
                <td class="px-2 py-2">
                  <p class="truncate" :title="order.fullName || 'Khách hàng'">
                    {{ order.fullName || "Khách hàng" }}
                  </p>
                </td>
                <td class="px-2 py-2 text-slate-600 dark:text-[#b9aa9a]">
                  <p
                    v-for="(itemText, idx) in getOrderItemsPreview(order)"
                    :key="`${order.id || 'order'}-item-${idx}`"
                    class="truncate"
                  >
                    {{ itemText }}
                  </p>
                </td>
                <td class="px-2 py-2 text-slate-500 dark:text-[#b9aa9a]">
                  <p class="truncate" :title="order.phone || '-'">{{ order.phone || "-" }}</p>
                  <p class="truncate" :title="order.email || '-'">{{ order.email || "-" }}</p>
                </td>
                <td
                  class="px-2 py-2 text-xs text-slate-600 dark:text-[#b9aa9a]"
                >
                  <p
                    class="line-clamp-2"
                    :title="
                      order.shippingAddress ||
                      order.shipping_address ||
                      order.address
                    "
                  >
                    {{
                      order.shippingAddress ||
                      order.shipping_address ||
                      order.address ||
                      "Mua tại quầy"
                    }}
                  </p>
                </td>
                <td class="px-2 py-2 font-bold text-primary">
                  {{ formatCurrency(order.finalAmount || order.totalMoney) }}
                </td>
                <td class="px-2 py-2">
                  <div class="truncate font-medium text-slate-700 dark:text-[#e6d7c8]" :title="getPaymentMethodLabel(order)">{{ getPaymentMethodLabel(order) }}</div>
                  <div class="mt-0.5 text-[11px] text-slate-500">{{ getPaymentStatusLabel(order.paymentStatus) }}</div>
                </td>
                <td class="px-2 py-2 whitespace-nowrap">
                  <span
                    class="inline-flex whitespace-nowrap rounded-full px-2 py-1 text-xs font-semibold"
                    :class="statusClass(order.status)"
                  >
                    {{ getOrderStatusLabel(order.status) }}
                  </span>
                </td>
                <td class="px-2 py-2 text-slate-500 dark:text-[#b9aa9a] whitespace-nowrap">
                  {{ formatDate(order.createdAt) }}
                </td>
                <td class="sticky right-0 z-10 bg-white px-2 py-2 text-right shadow-[-1px_0_0_rgba(226,232,240,0.75)]">
                  <button
                    class="rounded-lg p-1.5 hover:bg-slate-100 dark:hover:bg-[#2b241f]"
                    title="Chỉnh sửa"
                    @click.stop="openEdit(order)"
                  >
                    <span
                      class="material-symbols-outlined text-[18px] text-slate-500"
                      >edit</span
                    >
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <PaginationBar
          :current-page="ordersPage"
          :total-pages="ordersTotalPages"
          :page-start="ordersPageStart"
          :page-end="ordersPageEnd"
          :total-items="ordersTotalItems"
          label="đơn hàng"
          @previous="ordersPreviousPage"
          @next="ordersNextPage"
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
          <div class="relative mb-4 flex items-center">
            <h3 class="text-base font-bold">
              {{
                editingOrderId
                  ? `Sửa đơn hàng #${editingOrderId}`
                  : "Tạo đơn hàng mới"
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
              <label class="mb-1 block text-xs font-medium text-slate-500">
                ID tài khoản
                <span v-if="accountLookupLoading" class="ml-1 text-primary"
                  >(Đang tải...)</span
                >
              </label>
              <input
                v-model.number="form.accountId"
                type="number"
                min="1"
                :disabled="editingOrderId !== null"
                class="w-full rounded-xl border border-slate-900 bg-slate-50 px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#2b241f] disabled:opacity-60"
              />
            </div>

            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Tên khách hàng *</label
              >
              <input
                v-model.trim="form.fullName"
                type="text"
                :disabled="editingOrderId !== null && normalizeOrderStatusValue(form.status) !== 'PENDING'"
                class="w-full rounded-xl border border-slate-900 bg-slate-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f] disabled:opacity-60"
              />
            </div>

            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Số điện thoại</label
              >
              <input
                v-model.trim="form.phone"
                type="text"
                :disabled="editingOrderId !== null && normalizeOrderStatusValue(form.status) !== 'PENDING'"
                class="w-full rounded-xl border border-slate-900 bg-slate-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f] disabled:opacity-60"
              />
            </div>

            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Email</label
              >
              <input
                v-model.trim="form.email"
                type="email"
                :disabled="editingOrderId !== null && normalizeOrderStatusValue(form.status) !== 'PENDING'"
                class="w-full rounded-xl border border-slate-900 bg-slate-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f] disabled:opacity-60"
              />
            </div>

            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Trạng thái</label
              >
              <select
                v-model="form.status"
                :disabled="editingOrderId !== null"
                class="w-full rounded-xl border border-slate-900 bg-slate-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f] disabled:opacity-60"
              >
                <option
                  v-for="status in statusOptions"
                  :key="status.value"
                  :value="status.value"
                >
                  {{ status.label }}
                </option>
              </select>
            </div>

            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Thanh toán</label
              >
              <select
                v-model="form.paymentStatus"
                :disabled="editingOrderId !== null"
                class="w-full rounded-xl border border-slate-900 bg-slate-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f] disabled:opacity-60"
              >
                <option
                  v-for="status in paymentStatusOptions"
                  :key="status.value"
                  :value="status.value"
                  :disabled="isPaymentStatusOptionDisabled(status.value)"
                >
                  {{ status.label }}
                </option>
              </select>
            </div>

            <!-- Badge đã hoàn tiền -->
            <div v-if="editingOrderId && form.paymentStatus === 'REFUNDED'" class="md:col-span-2">
              <span class="inline-flex items-center gap-1.5 rounded-full bg-slate-100 px-3 py-1 text-sm font-semibold text-slate-600 dark:bg-slate-800 dark:text-slate-300">
                <span class="material-symbols-outlined text-[16px]">check_circle</span>
                Đã hoàn tiền
              </span>
            </div>

            <div class="md:col-span-2">
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Phương thức thanh toán</label
              >
              <input
                :value="getPaymentMethodLabel(form)"
                readonly
                disabled
                title="Phương thức thanh toán (chỉ hiển thị)"
                class="w-full rounded-xl border border-slate-900 bg-slate-50 px-3 py-2 text-sm text-slate-700 dark:border-[#3c342e] dark:bg-[#2b241f] dark:text-[#e6d7c8] disabled:opacity-60"
              />
            </div>

            <div v-if="form.returnReason" class="md:col-span-2">
              <label class="mb-1 block text-xs font-medium text-red-500"
                >Lý do yêu cầu trả hàng</label
              >
              <div
                class="w-full rounded-xl border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700 dark:border-[#3c342e] dark:bg-red-900/20 dark:text-red-300 whitespace-pre-wrap"
              >
                {{ form.returnReason }}
              </div>
            </div>

            <!-- Yêu cầu hủy đơn từ khách hàng -->
            <div v-if="form.status === 'CANCEL_REQUEST'" class="md:col-span-2">
              <div class="rounded-xl border border-orange-200 bg-orange-50 p-4 dark:border-orange-900/40 dark:bg-orange-900/10">
                <p class="mb-1 text-xs font-bold uppercase tracking-wide text-orange-600 dark:text-orange-400">⚠ Khách hàng yêu cầu hủy đơn</p>
                <div v-if="form.returnReason" class="mt-2 text-sm text-orange-700 dark:text-orange-300 whitespace-pre-wrap">{{ form.returnReason }}</div>
                <div class="mt-3 flex gap-2">
                  <button
                    type="button"
                    class="rounded-xl bg-red-600 px-4 py-2 text-sm font-semibold text-white hover:bg-red-700 disabled:opacity-60"
                    :disabled="statusChanging"
                    @click="approveCancelRequest"
                  >
                    ✓ Xác nhận hủy & hoàn kho
                  </button>
                  <button
                    type="button"
                    class="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100 disabled:opacity-60 dark:border-[#3c342e] dark:bg-[#2b241f] dark:text-white"
                    :disabled="statusChanging"
                    @click="rejectCancelRequest"
                  >
                    ✕ Từ chối hủy (giữ đơn)
                  </button>
                </div>
              </div>
            </div>

            <div v-if="form.returnEvidenceImages" class="md:col-span-2">
              <label class="mb-1 block text-xs font-medium text-red-500"
                >Ảnh minh chứng</label
              >
              <img
                :src="form.returnEvidenceImages"
                alt="Ảnh minh chứng hoàn hàng"
                class="mt-2 max-h-48 rounded-lg border border-slate-200 object-contain dark:border-[#3c342e]"
              />
            </div>

            <div
              class="md:col-span-2 rounded-xl border border-slate-200 bg-slate-50 p-3 text-xs dark:border-[#3c342e] dark:bg-[#2b241f]"
            >
              <p class="mb-3 font-semibold text-slate-700 dark:text-[#eadfd3]">
                Sản phẩm trong đơn
              </p>

              <div
                v-if="selectedOrderDetailedItems.length === 0 && selectedOrderItems.length === 0"
                class="text-slate-500 dark:text-[#b9aa9a]"
              >
                Chưa có dữ liệu sản phẩm cho đơn hàng này.
              </div>

              <div v-else-if="selectedOrderDetailedItems.length > 0" class="space-y-3">
                <div
                  v-for="(item, idx) in selectedOrderDetailedItems"
                  :key="`modal-item-detail-${idx}`"
                  class="flex items-center gap-3 border-b border-slate-200/60 pb-3 last:border-0 last:pb-0 dark:border-[#3c342e]"
                >
                  <div class="size-12 shrink-0 overflow-hidden rounded-lg bg-slate-200 dark:bg-[#181310]">
                    <img
                      v-if="item.imageUrl"
                      :src="item.imageUrl"
                      :alt="item.productName"
                      class="h-full w-full object-cover"
                    />
                    <span v-else class="material-symbols-outlined flex h-full items-center justify-center text-slate-400">
                      inventory_2
                    </span>
                  </div>

                  <div class="min-w-0 flex-1">
                    <p class="font-bold text-slate-800 dark:text-white truncate">
                      {{ item.productName }}
                      <span v-if="item.variantText" class="font-normal text-slate-500 dark:text-[#b9aa9a]">({{ item.variantText }})</span>
                      <span class="ml-1 text-primary">x{{ item.quantity }}</span>
                    </p>
                    <p class="text-[11px] text-slate-500 dark:text-[#b9aa9a] mt-0.5">
                      {{ formatCurrency(item.price) }} x {{ item.quantity }}
                      <span v-if="item.productCode" class="ml-2">| Mã SP: {{ item.productCode }}</span>
                    </p>
                  </div>
                </div>
              </div>

              <div v-else class="space-y-1 text-slate-600 dark:text-[#b9aa9a]">
                <p
                  v-for="(itemText, idx) in selectedOrderItems"
                  :key="`modal-item-${idx}`"
                  class="truncate"
                >
                  {{ itemText }}
                </p>
              </div>

              <!-- Tổng kết phí ship, giảm giá, tổng cộng (Ảnh 3) -->
              <div class="mt-3 border-t border-slate-200 pt-3 text-right space-y-1 text-xs dark:border-[#3c342e]">
                <div class="text-slate-500 dark:text-[#b9aa9a]">
                  Phí ship: <span class="font-medium text-slate-700 dark:text-slate-300">{{ formatCurrency(modalShippingFee) }}</span>
                </div>
                <div class="text-slate-500 dark:text-[#b9aa9a]">
                  Giảm giá: <span class="font-medium text-slate-700 dark:text-slate-300">-{{ formatCurrency(modalDiscountAmount) }}</span>
                </div>
                <div class="text-sm font-bold text-slate-800 dark:text-white pt-1">
                  Tổng cộng: <span class="text-primary font-black text-sm">{{ formatCurrency(form.finalAmount || form.totalMoney) }}</span>
                </div>
              </div>
            </div>

            <div class="md:col-span-2">
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Địa chỉ giao hàng</label
              >
              <textarea
                v-model.trim="form.address"
                rows="2"
                :disabled="editingOrderId !== null && normalizeOrderStatusValue(form.status) !== 'PENDING'"
                class="w-full rounded-xl border border-slate-900 bg-slate-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f] disabled:opacity-60"
              />
            </div>
          </div>

          <p v-if="modalError" class="mt-3 text-xs text-red-500">
            {{ modalError }}
          </p>

          <div class="mt-4 flex flex-wrap items-center justify-end gap-2">

            <template v-if="editingOrderId">
              <button  v-for="action in availableStatusActions" :key="action.status" type="button" class="rounded-xl px-4 py-2 text-sm font-medium text-white disabled:opacity-60"
                :class="statusActionClass(action.variant)"
                :disabled="statusChanging"
                @click="requestStatusChange(action.status, action.label)"
              >
                {{ action.label }}
              </button>

              <button v-if="canCancelOrder" type="button"
                class="rounded-xl border border-red-200 bg-red-50 px-4 py-2 text-sm font-medium text-red-600 hover:bg-red-100 disabled:opacity-60 dark:border-red-900/40 dark:bg-red-900/20 dark:text-red-300 dark:hover:bg-red-900/30"
                :disabled="statusChanging"
                @click="requestCancelOrder"
              >
                Hủy đơn hàng
              </button>

              <button v-if="canCustomerReturn" type="button"
                class="rounded-xl border border-amber-200 bg-amber-50 px-4 py-2 text-sm font-medium text-amber-700 hover:bg-amber-100 disabled:opacity-60 dark:border-amber-900/40 dark:bg-amber-900/20 dark:text-amber-300 dark:hover:bg-amber-900/30"
                :disabled="statusChanging"
                @click="requestCustomerReturn"
              >
                Khách hoàn trả
              </button>
            </template>

            <button
              type="button"
              :disabled="saving"
              class="rounded-xl bg-primary px-4 py-2 text-sm font-medium text-white hover:opacity-90 disabled:opacity-60"
              @click="requestSaveOrder"
            >
              {{
                saving ? "Đang lưu..." : editingOrderId ? "Cập nhật" : "Tạo mới"
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

    <!-- Return Modal for Admin -->
    <div v-if="returnModal.show" class="fixed inset-0 z-[150] flex items-center justify-center px-4 py-6">
      <div class="absolute inset-0 bg-black/60" @click="returnModal.show = false"></div>
      <div class="relative w-full max-w-sm rounded-2xl border border-slate-200 bg-white p-6 shadow-2xl dark:border-[#3c342e] dark:bg-[#1f1915]">
        <h3 class="mb-2 text-xl font-bold tracking-tight text-slate-900 dark:text-white">Yêu cầu trả hàng</h3>
        <p class="mb-4 text-sm text-slate-500">Chọn serial và lý do hoàn trả cho đơn #{{ returnModal.orderId }}</p>
        
        <textarea v-model="returnModal.reason" rows="3" class="w-full mb-4 rounded-xl border border-slate-900 bg-slate-50 px-4 py-3 text-sm text-slate-700 focus:border-primary focus:outline-none dark:border-[#4c4138] dark:bg-[#2a231f] dark:text-white" placeholder="Lý do hoàn trả..."></textarea>
        <div class="flex justify-end gap-2">
          <button @click="returnModal.show = false" class="rounded-xl bg-slate-100 px-4 py-2 font-bold text-slate-700">Hủy</button>
          <button @click="executeAdminReturnOrder" class="rounded-xl bg-primary px-4 py-2 font-bold text-white">Xác nhận</button>
        </div>
      </div>
    </div>

    <!-- Toast Notification -->
    <Teleport to="body">
      <div v-if="newOrderToast" class="fixed bottom-4 right-4 z-50 animate-slide-in-up">
        <div class="flex items-center gap-3 rounded-xl border border-primary/20 bg-white p-4 shadow-xl dark:border-primary/20 dark:bg-[#1f1a17]">
          <div class="flex h-10 w-10 items-center justify-center rounded-full bg-primary/10 text-primary">
            <span class="material-symbols-outlined">notifications_active</span>
          </div>
          <div>
            <h4 class="font-bold text-slate-800 dark:text-white">{{ newOrderToast.title }}</h4>
            <p class="text-sm text-slate-500 dark:text-[#b9aa9a]">{{ newOrderToast.message }}</p>
          </div>
          <button class="ml-4 text-slate-400 hover:text-slate-600" @click="newOrderToast = null">
            <span class="material-symbols-outlined text-lg">close</span>
          </button>
        </div>
      </div>
    </Teleport>
  </AdminShell>
</template>

<script setup>
import { computed, onMounted, ref, watch } from "vue";

import AdminShell from "@/components/admin/AdminShell.vue";
import ConfirmDialog from "@/components/admin/ConfirmDialog.vue";
import PaginationBar from "@/components/PaginationBar.vue";
import { useConfirmDialog } from "@/composables/useConfirmDialog";
import { usePagination } from "@/composables/usePagination";
import { accountsApi, adminApi, ordersApi } from "@/services/api";

const { confirmModal, requestConfirm, executeConfirm, cancelConfirm } =
  useConfirmDialog();

const loading = ref(false);
const error = ref("");
const orders = ref([]);
const search = ref("");
const statusFilter = ref("ALL");
const channelFilter = ref("ALL");

const showModal = ref(false);
const editingOrderId = ref(null);
const originalPaymentStatusValue = computed(() => { const o = orders.value.find(x => Number(x.id) === Number(editingOrderId.value)); return o ? normalizePaymentStatusValue(o.paymentStatus) : ''; });
const saving = ref(false);
const modalError = ref("");
const form = ref(emptyForm());
const originalPaymentStatus = ref('');
const isApplyingOrderRules = ref(false);
const orderRuleHint = ref("");
const selectedOrderItems = ref([]);

const hiddenOrders = ref([]);
const statusChanging = ref(false);

const newOrderToast = ref(null);
const trackedOrder = ref(null);
let pollingInterval = null;
const isFirstLoad = ref(true);

const successToast = ref("");

function showToast(message, duration = 3000) {
  successToast.value = message;
  setTimeout(() => { successToast.value = ""; }, duration);
}


const visibleOrders = computed(() => {
  return orders.value.filter(
    (order) =>
      !hiddenOrders.value.some(
        (hidden) => Number(order.id) === Number(hidden.id),
      ),
  );
});

const latestOrder = computed(() => {
  return visibleOrders.value.length > 0 ? visibleOrders.value[0] : null;
});

const activeTrackedOrder = computed(() => {
  return trackedOrder.value || latestOrder.value;
});

function selectOrderToTrack(order) {
  trackedOrder.value = order;
}

const trackingSteps = computed(() => {
  if (!activeTrackedOrder.value) return [];
  const status = normalizeOrderStatusValue(activeTrackedOrder.value.status);
  
  const steps = [
    { label: "Chờ xử lý", icon: "receipt_long", isActive: true, isError: false, isWarning: false },
    { label: "Đóng gói và xử lý", icon: "inventory_2", isActive: ["PROCESSING", "SHIPPING", "SUCCESS", "RETURN_REQUEST", "RETURNING", "RETURNED"].includes(status), isError: false, isWarning: false },
  ];

  if (status === "CANCELLED") {
    steps.push({ label: "Đã hủy", icon: "cancel", isActive: true, isError: true, isWarning: false });
  } else if (["RETURN_REQUEST", "RETURNING", "RETURNED"].includes(status)) {
    steps.push({ label: "Đang giao", icon: "local_shipping", isActive: true, isError: false, isWarning: false });
    steps.push({ label: "Thành công", icon: "check_circle", isActive: true, isError: false, isWarning: false });
    steps.push({ label: "Yêu cầu hoàn", icon: "assignment_return", isActive: true, isError: false, isWarning: true });
    steps.push({ label: "Đang hoàn về", icon: "local_shipping", isActive: ["RETURNING", "RETURNED"].includes(status), isError: false, isWarning: true });
    steps.push({ label: "Đã hoàn", icon: "settings_backup_restore", isActive: status === "RETURNED", isError: false, isWarning: true });
  } else {
    steps.push({ label: "Đang giao", icon: "local_shipping", isActive: ["SHIPPING", "SUCCESS"].includes(status), isError: false, isWarning: false });
    steps.push({ label: "Thành công", icon: "check_circle", isActive: status === "SUCCESS", isError: false, isWarning: false });
  }
  
  return steps;
});

function normalizeStatusKey(value) {
  return String(value || "")
    .trim()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/\s+/g, "_")
    .replace(/-/g, "_")
    .toUpperCase();
}

function normalizeOrderStatusValue(value) {
  const key = normalizeStatusKey(value);

  if (["NEW", "PENDING", "WAITING", "CHUA_XU_LY", "CHO_XU_LY"].includes(key)) {
    return "PENDING";
  }
  if (
    [
      "CONFIRMED",
      "PROCESSING",
      "DANG_XU_LY",
      "XAC_NHAN",
      "IN_PROGRESS",
    ].includes(key)
  ) {
    return "PROCESSING";
  }
  if (["SHIPPING", "DANG_GIAO", "DELIVERING", "IN_TRANSIT"].includes(key)) {
    return "SHIPPING";
  }
  if (
    ["SUCCESS", "COMPLETED", "DELIVERED", "THANH_CONG", "HOAN_THANH"].includes(
      key,
    )
  ) {
    return "SUCCESS";
  }
  if (["RETURN_REQUEST", "YEU_CAU_TRA"].includes(key)) {
    return "RETURN_REQUEST";
  }
  if (["RETURNING", "TRA_HANG", "DANG_HOAN"].includes(key)) {
    return "RETURNING";
  }
  if (
    ["RETURNED", "HOAN_TRA", "HOAN_TIEN", "REFUND", "REFUNDED"].includes(key)
  ) {
    return "RETURNED";
  }
  if (["PARTIAL_RETURN_REQUEST", "YEU_CAU_HOAN_MOT_PHAN"].includes(key)) return "PARTIAL_RETURN_REQUEST";
  if (["PARTIAL_RETURNED", "HOAN_MOT_PHAN"].includes(key)) return "PARTIAL_RETURNED";
  if (["CANCEL_REQUEST", "YEU_CAU_HUY"].includes(key)) {
    return "CANCEL_REQUEST";
  }
  if (["CANCELLED", "CANCELED", "DA_HUY", "HUY", "FAILED"].includes(key)) {
    return "CANCELLED";
  }

  return key || "PENDING";
}

function normalizePaymentStatusValue(paymentStatus) {
  const key = normalizeStatusKey(paymentStatus);

  if (
    ["PENDING", "UNPAID", "CHUA_THANH_TOAN", "AWAITING_PAYMENT"].includes(key)
  ) {
    return "PENDING";
  }
  if (["PAID", "DA_THANH_TOAN", "SETTLED", "CAPTURED"].includes(key)) {
    return "PAID";
  }
  if (
    ["REFUNDED", "REFUND", "HOAN_TIEN", "CHARGEBACK", "REVERSED"].includes(key)
  ) {
    return "REFUNDED";
  }

  return key || "PENDING";
}

const statusOptions = [
  { value: "PENDING", label: "Chờ xử lý" },
  { value: "PROCESSING", label: "Đóng gói và xử lý" },
  { value: "SHIPPING", label: "Đang giao" },
  { value: "SUCCESS", label: "Thành công" },
  { value: "CANCEL_REQUEST", label: "Yêu cầu hủy" },
  { value: "RETURN_REQUEST", label: "Yêu cầu hoàn" },
  { value: "PARTIAL_RETURN_REQUEST", label: "Yêu cầu hoàn (1 phần)" },
  { value: "PARTIAL_RETURNED", label: "Đã hoàn (1 phần)" },
  { value: "RETURNING", label: "Đang hoàn về" },
  { value: "RETURNED", label: "Đã hoàn trả" },
  { value: "CANCELLED", label: "Đã hủy" },
];

const availableStatusActions = computed(() => {
  const status = normalizeOrderStatusValue(form.value.status);
  const actions = [];

  if (status === "PARTIAL_RETURN_REQUEST") {
    actions.push({
      status: "PARTIAL_RETURNED",
      label: "Xác nhận hoàn (1 phần)",
      variant: "warning",
    });
  } else if (status === "PENDING") {
    actions.push({
      status: "PROCESSING",
      label: "Đóng gói và xử lý",
      variant: "primary",
    });
  } else if (status === "PROCESSING") {
    actions.push({ status: "SHIPPING", label: "Đang giao", variant: "primary" });
  } else if (status === "SHIPPING") {
    actions.push({ status: "SUCCESS", label: "Thành công", variant: "success" });
  } else if (status === "RETURN_REQUEST") {
    actions.push({
      status: "RETURNING",
      label: "Duyệt hoàn trả",
      variant: "warning",
    });
    actions.push({
      status: "SUCCESS",
      label: "Từ chối hoàn",
      variant: "primary",
    });
  } else if (status === "RETURNING") {
    actions.push({
      status: "RETURNED",
      label: "Xác nhận hoàn trả",
      variant: "warning",
    });
  }

  return actions;
});

const canCancelOrder = computed(() => {
  const status = normalizeOrderStatusValue(form.value.status);
  return (
    !isFinalOrderStatus(status) &&
    !["RETURN_REQUEST", "RETURNING", "SHIPPING"].includes(status)
  );
});

const canCustomerReturn = computed(() => {
  const status = normalizeOrderStatusValue(form.value.status);
  return status === "SHIPPING";
});

function statusActionClass(variant) {
  if (variant === "success") {
    return "bg-emerald-500 hover:bg-emerald-600";
  }
  if (variant === "warning") {
    return "bg-amber-500 hover:bg-amber-600";
  }
  return "bg-primary hover:opacity-90";
}

const paymentStatusOptions = [
  { value: "PENDING", label: "Chưa thanh toán" },
  { value: "PAID", label: "Đã thanh toán" },
  { value: "REFUNDED", label: "Hoàn tiền" },
];
const profitablePaymentStatuses = ["PAID", "SETTLED", "CAPTURED"];
const deductedPaymentStatuses = [
  "REFUNDED",
  "REFUND",
  "CHARGEBACK",
  "REVERSED",
];

function isPosOrder(order) {
  const addr = String(
    order?.shippingAddress ||
    order?.shipping_address ||
    order?.address ||
    ""
  ).toLowerCase();
  return (
    addr.includes("mua trực tiếp") ||
    addr.includes("tại cửa hàng") ||
    addr.includes("pos") ||
    addr.includes("tại quầy")
  );
}

const filteredOrders = computed(() => {
  const keyword = search.value.trim().toLowerCase();

  return visibleOrders.value
    .filter((order) => {
      const statusMatch =
        statusFilter.value === "ALL" ||
        normalizeOrderStatusValue(order.status) === statusFilter.value;

      if (!statusMatch) {
        return false;
      }

      if (channelFilter.value === "POS" && !isPosOrder(order)) {
        return false;
      }

      if (channelFilter.value === "ONLINE" && isPosOrder(order)) {
        return false;
      }

      if (!keyword) {
        return true;
      }

      const haystack = [
        order.id,
        order.fullName,
        order.phone,
        order.email,
        order.status,
        order.paymentStatus,
      ]
        .filter((item) => item !== undefined && item !== null)
        .join(" ")
        .toLowerCase();

      return haystack.includes(keyword);
    })
    .sort((a, b) => {
      const timeA = new Date(a.createdAt || 0).getTime();
      const timeB = new Date(b.createdAt || 0).getTime();
      if (timeA !== timeB) {
        return timeB - timeA;
      }
      return Number(b.id || 0) - Number(a.id || 0);
    });
});

const {
  currentPage: ordersPage,
  totalPages: ordersTotalPages,
  paginatedItems: paginatedOrders,
  pageStart: ordersPageStart,
  pageEnd: ordersPageEnd,
  totalItems: ordersTotalItems,
  previousPage: ordersPreviousPage,
  nextPage: ordersNextPage,
} = usePagination(filteredOrders, 10);

const profitableOrders = computed(() => {
  return orders.value.filter((order) => {
    const paymentStatus = normalizePaymentStatusValue(order.paymentStatus);
    return profitablePaymentStatuses.includes(paymentStatus);
  });
});

const deductedOrders = computed(() => {
  return orders.value.filter((order) => {
    const paymentStatus = normalizePaymentStatusValue(order.paymentStatus);
    return deductedPaymentStatuses.includes(paymentStatus);
  });
});

const profitableOrdersCount = computed(() => profitableOrders.value.length);
const deductedOrdersCount = computed(() => deductedOrders.value.length);

const profitableRevenue = computed(() => {
  return profitableOrders.value.reduce((sum, order) => {
    const amount = Math.abs(Number(order.finalAmount ?? order.totalMoney ?? 0));
    return sum + (Number.isFinite(amount) ? amount : 0);
  }, 0);
});

const deductedRevenue = computed(() => {
  return deductedOrders.value.reduce((sum, order) => {
    const amount = Math.abs(Number(order.finalAmount ?? order.totalMoney ?? 0));
    return sum + (Number.isFinite(amount) ? amount : 0);
  }, 0);
});

const netRevenue = computed(
  () => profitableRevenue.value - deductedRevenue.value,
);

function emptyForm() {
  return {
    accountId: null,
    fullName: "",
    phone: "",
    email: "",
    address: "",
    paymentMethodId: null,
    paymentMethod: "",
    paymentMethodName: "",
    totalMoney: 0,
    finalAmount: 0,
    status: "PENDING",
    paymentStatus: "PENDING",
    returnReason: "",
    returnEvidenceImages: "",
  };
}

function normalizePaymentMethodKey(value) {
  return normalizeStatusKey(value).replace(/\s+/g, "_");
}

function getPaymentMethodId(orderLike) {
  const source = orderLike && typeof orderLike === "object" ? orderLike : {};
  const rawId =
    source.paymentMethodId ??
    source.payment_method_id ??
    source.paymentMethod?.id ??
    source.payment_method?.id ??
    null;
  const methodId = Number(rawId);

  if (Number.isFinite(methodId) && methodId > 0) {
    return methodId;
  }

  const methodKey = normalizePaymentMethodKey(
    source.paymentMethodName ??
      source.payment_method_name ??
      source.paymentMethod?.name ??
      source.payment_method?.name ??
      source.paymentMethod ??
      source.payment_method ??
      "",
  );

  if (["COD", "CASH", "TIEN_MAT", "THANH_TOAN_KHI_NHAN_HANG"].some((token) => methodKey.includes(token))) return 1;
  if (methodKey.includes("VNPAY")) return 2;
  if (methodKey.includes("MOMO")) return 3;
  if (["BANK", "TRANSFER", "CHUYEN_KHOAN"].some((token) => methodKey.includes(token))) return 4;
  if (methodKey.includes("ZALOPAY")) return 5;

  return null;
}

function isCashOnDeliveryMethod(orderLike) {
  const source = orderLike && typeof orderLike === "object" ? orderLike : {};
  const methodId = getPaymentMethodId(source);
  const methodKey = normalizePaymentMethodKey(
    source.paymentMethod ??
      source.payment_method ??
      source.paymentMethodName ??
      source.payment_method_name ??
      "",
  );

  if (methodId === 1) return true;
  return ["COD", "CASH", "TIEN_MAT", "THANH_TOAN_KHI_NHAN_HANG"].some((token) =>
    methodKey.includes(token),
  );
}

function isPrepaidMethod(orderLike) {
  const source = orderLike && typeof orderLike === "object" ? orderLike : {};
  const methodId = getPaymentMethodId(source);
  const methodKey = normalizePaymentMethodKey(
    source.paymentMethod ??
      source.payment_method ??
      source.paymentMethodName ??
      source.payment_method_name ??
      "",
  );

  if (methodId === 2) return true;
  return ["QR", "VNPAY", "MOMO", "BANK", "TRANSFER", "WALLET", "CARD"].some(
    (token) => methodKey.includes(token),
  );
}

function resolveManagedOrderState(
  orderLike,
  nextStatusInput,
  nextPaymentStatusInput,
) {
  let nextStatus = normalizeOrderStatusValue(nextStatusInput);
  let nextPaymentStatus = normalizePaymentStatusValue(nextPaymentStatusInput);
  const currentStatus = normalizeOrderStatusValue(orderLike?.status);
  const currentPayment = normalizePaymentStatusValue(orderLike?.paymentStatus);
  const isCod = isCashOnDeliveryMethod(orderLike);
  const isPrepaid = isPrepaidMethod(orderLike);
  const isReturnRequestPending =
    currentStatus === "SUCCESS" && nextStatus === "PENDING";

  if (isFinalOrderStatus(currentStatus) && nextStatus !== currentStatus) {
    return {
      status: currentStatus,
      paymentStatus: currentPayment,
    };
  }

  // Return request phase: move back to waiting while keeping payment as paid.
  if (isReturnRequestPending) {
    nextPaymentStatus = "PAID";
  }

  // Returning stage keeps paid state until return is fully confirmed.
  if (nextStatus === "RETURNING") {
    nextPaymentStatus = "PAID";
  }

  // Returned orders must be refunded.
  if (nextStatus === "RETURNED") {
    nextPaymentStatus = "REFUNDED";
  }

  // Cancelled orders: prepaid -> refunded, COD -> pending (customer did not receive item).
  if (nextStatus === "CANCELLED") {
    if (isPrepaid) {
      nextPaymentStatus = "REFUNDED";
    } else if (isCod) {
      nextPaymentStatus = "PENDING";
    }
  }

  // Always keep successful orders in paid state.
  if (nextStatus === "SUCCESS") {
    nextPaymentStatus = "PAID";
  }

  // Prepaid/QR orders should remain paid throughout the fulfillment pipeline.
  if (
    isPrepaid &&
    ["PROCESSING", "SHIPPING", "SUCCESS"].includes(nextStatus)
  ) {
    nextPaymentStatus = "PAID";
  }

  // COD orders are unpaid until successful completion.
  if (
    !isReturnRequestPending &&
    isCod &&
    ["PENDING", "PROCESSING", "SHIPPING"].includes(nextStatus)
  ) {
    nextPaymentStatus = "PENDING";
  }

  // Refunded payment implies a fully confirmed return or prepaid cancellation.
  if (nextPaymentStatus === "REFUNDED") {
    if (isCod && nextStatus === "CANCELLED") {
      nextPaymentStatus = "PENDING";
    } else if (!["RETURNED", "CANCELLED"].includes(nextStatus)) {
      nextStatus = "RETURNED";
    }
  }

  // If admin marks COD as paid, promote to success to keep states linked.
  // Exception: Do not promote to success if the order is in the return flow.
  if (
    isCod &&
    nextPaymentStatus === "PAID" &&
    !["SUCCESS", "RETURN_REQUEST", "RETURNING", "RETURNED", "CANCELLED"].includes(nextStatus)
  ) {
    return {
      status: "SUCCESS",
      paymentStatus: "PAID",
    };
  }

  return {
    status: nextStatus,
    paymentStatus: nextPaymentStatus,
  };
}

function isFinalOrderStatus(statusValue) {
  const normalized = normalizeOrderStatusValue(statusValue);
  return ["SUCCESS", "CANCELLED", "RETURNED"].includes(normalized);
}

function isPaymentStatusOptionDisabled(paymentStatusValue) {
  if (!showModal.value) return false;

  const normalizedPayment = normalizePaymentStatusValue(paymentStatusValue);
  const currentStatus = normalizeOrderStatusValue(form.value.status);
  const managedState = resolveManagedOrderState(
    form.value,
    currentStatus,
    normalizedPayment,
  );

  return managedState.paymentStatus !== normalizedPayment;
}

const paymentFlowLabel = computed(() => {
  if (isPrepaidMethod(form.value)) return "Trả trước (QR/chuyển khoản)";
  if (isCashOnDeliveryMethod(form.value))
    return "COD (thanh toán khi nhận hàng)";
  return "Không xác định";
});

function applyManagedOrderRules(trigger = "") {
  if (!showModal.value || isApplyingOrderRules.value) return;

  const currentStatus = normalizeOrderStatusValue(form.value.status);
  const currentPaymentStatus = normalizePaymentStatusValue(
    form.value.paymentStatus,
  );
  const managed = resolveManagedOrderState(
    form.value,
    currentStatus,
    currentPaymentStatus,
  );

  const statusChanged = managed.status !== currentStatus;
  const paymentChanged = managed.paymentStatus !== currentPaymentStatus;

  if (!statusChanged && !paymentChanged) {
    if (trigger === "status" || trigger === "payment") {
      orderRuleHint.value = "Trạng thái và thanh toán đã đúng theo quy tắc.";
    }
    return;
  }

  isApplyingOrderRules.value = true;
  form.value.status = managed.status;
  form.value.paymentStatus = managed.paymentStatus;
  isApplyingOrderRules.value = false;

  if (statusChanged && paymentChanged) {
    orderRuleHint.value =
      "Đã tự đồng bộ cả trạng thái đơn và trạng thái thanh toán.";
    return;
  }
  if (statusChanged) {
    orderRuleHint.value =
      "Đã tự cập nhật trạng thái đơn để đồng bộ với trạng thái thanh toán.";
    return;
  }
  orderRuleHint.value =
    "Đã tự cập nhật trạng thái thanh toán để đồng bộ với trạng thái đơn.";
}

function getOrderStatusLabel(status) {
  const value = normalizeOrderStatusValue(status);
  const found = statusOptions.find((item) => item.value === value);
  return found ? found.label : "Không xác định";
}

function getPaymentStatusLabel(status) {
  const value = normalizePaymentStatusValue(status);
  const found = paymentStatusOptions.find((item) => item.value === value);
  return found ? found.label : "Chưa thanh toán";
}

function getPaymentMethodLabel(orderLike) {
  const source = orderLike && typeof orderLike === "object" ? orderLike : {};
  const methodId = getPaymentMethodId(source);
  const rawMethod = String(
    source.paymentMethodName ??
      source.payment_method_name ??
      source.paymentMethod?.name ??
      source.payment_method?.name ??
      source.paymentMethod ??
      source.payment_method ??
      "",
  ).trim();

  const normalized = normalizePaymentMethodKey(rawMethod);
  if (methodId === 1) return "Thanh toán khi nhận hàng (COD)";
  if (methodId === 2 || normalized.includes("VNPAY")) return "VNPay";
  if (methodId === 3 || normalized.includes("MOMO")) return "Momo";
  if (
    methodId === 4 ||
    normalized.includes("BANK") ||
    normalized.includes("TRANSFER")
  ) {
    return "Chuyển khoản ngân hàng";
  }
  if (methodId === 5 || normalized.includes("ZALOPAY")) return "ZaloPay";

  return rawMethod || "Không xác định";
}

function asNumber(value, fallback = 0) {
  const n = Number(value);
  return Number.isFinite(n) ? n : fallback;
}

function formatCurrency(value) {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
    maximumFractionDigits: 0,
  }).format(asNumber(value, 0));
}

function formatDate(value) {
  const time = new Date(value || 0);
  if (Number.isNaN(time.getTime())) {
    return "Không rõ";
  }
  return new Intl.DateTimeFormat("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(time);
}

function unwrapOrderPayload(payload) {
  if (!payload || typeof payload !== "object") return null;
  if (payload.data && typeof payload.data === "object") return payload.data;
  if (payload.content && typeof payload.content === "object")
    return payload.content;
  return payload;
}

function extractOrderItems(orderLike) {
  const source = unwrapOrderPayload(orderLike);
  if (!source || typeof source !== "object") return [];

  const candidates = [
    source.orderItems,
    source.order_items,
    source.items,
    source.orderDetails,
    source.order_details,
  ];

  const found = candidates.find((candidate) => Array.isArray(candidate));
  return Array.isArray(found) ? found : [];
}

function formatOrderItemText(itemLike) {
  const item = itemLike && typeof itemLike === "object" ? itemLike : {};
  const productName =
    item.productName ||
    item.product_name ||
    item.name ||
    item.variantName ||
    item.variant_name ||
    item.variant?.productName ||
    item.variant?.product?.name ||
    `Sản phẩm #${item.productId ?? item.product_id ?? item.variantId ?? item.variant_id ?? "?"}`;

  const colorName =
    item.colorName ||
    item.color_name ||
    item.color?.name ||
    item.variant?.colorName ||
    item.variant?.color?.name ||
    "";

  const sizeName =
    item.sizeName ||
    item.size_name ||
    item.size?.name ||
    item.variant?.sizeName ||
    item.variant?.size?.name ||
    "";

  const quantity = Number(item.quantity ?? item.qty ?? 0);
  const qtyLabel =
    Number.isFinite(quantity) && quantity > 0 ? `x${quantity}` : "x1";
  const variantText = [colorName, sizeName].filter(Boolean).join("/");

  return variantText
    ? `${productName} (${variantText}) ${qtyLabel}`
    : `${productName} ${qtyLabel}`;
}

const selectedOrderDetailedItems = ref([]);

const returnModal = ref({ show: false, orderId: null,  reason: '' });

async function executeAdminReturnOrder() {
  if (!returnModal.value.reason.trim()) {
    alert('Vui lòng nhập lý do hoàn trả.');
    return;
  }
  const nextStatus = 'RETURN_REQUEST';

  try {
    await adminApi.updateOrder(returnModal.value.orderId, {
      status: nextStatus,
      returnReason: returnModal.value.reason.trim(),
      
    });
    form.value.status = nextStatus;
    await loadOrders();
    showToast('Đã gửi yêu cầu hoàn trả thành công!');
    returnModal.value.show = false;
  } catch (e) {
    alert('Lỗi: ' + (e?.message || e));
  }
}

const modalShippingFee = computed(() => {
  if (isPosOrder(form.value)) return 0;
  return 30000;
});

const modalDiscountAmount = computed(() => {
  return Number(form.value.discountAmount || 0);
});

function normalizeOrderItemsDetailed(orderLike) {
  const items = extractOrderItems(orderLike);
  return items.map((itemLike) => {
    const item = itemLike && typeof itemLike === "object" ? itemLike : {};
    const productName =
      item.productName ||
      item.product_name ||
      item.name ||
      item.variantName ||
      item.variant_name ||
      item.variant?.productName ||
      item.variant?.product?.name ||
      `Sản phẩm #${item.productId ?? item.product_id ?? item.variantId ?? item.variant_id ?? "?"}`;

    const productCode =
      item.productCode ||
      item.product_code ||
      item.variant?.productCode ||
      item.variant?.product?.productCode ||
      "";

    const colorName =
      item.colorName ||
      item.color_name ||
      item.color?.name ||
      item.variant?.colorName ||
      item.variant?.color?.name ||
      "";

    const sizeName =
      item.sizeName ||
      item.size_name ||
      item.size?.name ||
      item.variant?.sizeName ||
      item.variant?.size?.name ||
      "";

    const quantity = Number(item.quantity ?? item.qty ?? 1) || 1;
    const price = Number(item.price ?? item.unitPrice ?? item.variant?.price ?? 0);
    const imageUrl = item.imageUrl || item.image_url || item.variant?.imageUrl || item.variant?.product?.imageUrl || "";

    const variantText = [colorName, sizeName].filter(Boolean).join("/");

    return {
      productName,
      productCode,
      variantText,
      quantity,
      price,
      imageUrl,
    };
  });
}

function normalizeOrderItemsForDisplay(orderLike) {
  return extractOrderItems(orderLike).map(formatOrderItemText).filter(Boolean);
}

function getOrderItemsPreview(orderLike) {
  const lines = normalizeOrderItemsForDisplay(orderLike);
  if (lines.length === 0) {
    return ["Chưa có dữ liệu sản phẩm"];
  }

  const max = 2;
  if (lines.length <= max) {
    return lines;
  }

  return [...lines.slice(0, max), `+${lines.length - max} sản phẩm khác`];
}

function statusClass(status) {
  const normalized = normalizeOrderStatusValue(status);
  if (["SUCCESS"].includes(normalized)) {
    return "bg-green-100 text-green-700 dark:bg-green-900/20 dark:text-green-400";
  }
  if (["PROCESSING", "SHIPPING"].includes(normalized)) {
    return "bg-blue-100 text-blue-700 dark:bg-blue-900/20 dark:text-blue-400";
  }
  if (["PARTIAL_RETURN_REQUEST"].includes(normalized)) return "bg-orange-100 text-orange-700 dark:bg-orange-900/20 dark:text-orange-300";
  if (["PARTIAL_RETURNED"].includes(normalized)) return "bg-slate-200 text-slate-700 dark:bg-slate-700/40 dark:text-slate-200";
  if (["RETURN_REQUEST", "RETURNING"].includes(normalized)) {
    return "bg-orange-100 text-orange-700 dark:bg-orange-900/20 dark:text-orange-300";
  }
  if (["CANCELLED", "RETURNED"].includes(normalized)) {
    return "bg-red-100 text-red-700 dark:bg-red-900/20 dark:text-red-400";
  }
  return "bg-yellow-100 text-yellow-700 dark:bg-yellow-900/20 dark:text-yellow-400";
}

const accountLookupLoading = ref(false);

async function fillAccountInfo(accountId, allowOverwrite = false) {
  const id = Number(accountId);
  if (!Number.isFinite(id) || id <= 0) return;
  accountLookupLoading.value = true;
  try {
    const raw = await accountsApi.getById(id);
    const acc = raw && (raw.data || raw);
    if (!acc) return;
    if (allowOverwrite || !form.value.fullName)
      form.value.fullName =
        acc.username || acc.fullName || acc.name || form.value.fullName;
    if (allowOverwrite || !form.value.phone)
      form.value.phone = acc.phone || form.value.phone;
    if (allowOverwrite || !form.value.email)
      form.value.email = acc.gmail || acc.email || form.value.email;
    if (allowOverwrite || !form.value.address)
      form.value.address = acc.address || form.value.address;
  } catch {
    // Silently ignore lookup failure — admin can fill fields manually
  } finally {
    accountLookupLoading.value = false;
  }
}

function openCreate() {
  editingOrderId.value = null;
  form.value = emptyForm();
  orderRuleHint.value = "";
  selectedOrderItems.value = [];
  selectedOrderDetailedItems.value = [];
  modalError.value = "";
  showModal.value = true;
  applyManagedOrderRules("init");
}

async function openEdit(order) {
  selectedOrderItems.value = normalizeOrderItemsForDisplay(order);
  selectedOrderDetailedItems.value = normalizeOrderItemsDetailed(order);

  if (
    selectedOrderItems.value.length === 0 &&
    Number.isFinite(Number(order.id))
  ) {
    try {
      const orderDetail = await ordersApi.getOrderById(order.id);
      selectedOrderItems.value = normalizeOrderItemsForDisplay(orderDetail);
      selectedOrderDetailedItems.value = normalizeOrderItemsDetailed(orderDetail);
    } catch {
      // Keep empty fallback text in modal when order detail cannot be loaded.
    }
  }

  editingOrderId.value = order.id;
  form.value = {
    accountId: Number.isFinite(Number(order.accountId))
      ? Number(order.accountId)
      : Number.isFinite(Number(order.account_id))
        ? Number(order.account_id)
        : Number.isFinite(Number(order.account?.id))
          ? Number(order.account.id)
          : null,
    fullName: order.fullName || order.fullname || "",
    phone: order.phone || "",
    email: order.email || order.gmail || "",
    address:
      order.shippingAddress || order.shipping_address || order.address || "",
    paymentMethodId: getPaymentMethodId(order),
    paymentMethod:
      order.paymentMethod ||
      order.payment_method ||
      order.paymentMethod?.name ||
      order.payment_method?.name ||
      order.paymentMethodName ||
      "",
    paymentMethodName:
      order.paymentMethodName ||
      order.payment_method_name ||
      order.paymentMethod?.name ||
      order.payment_method?.name ||
      "",
    totalMoney: asNumber(order.totalMoney || order.total_money, 0),
    finalAmount: asNumber(
      order.finalAmount ||
        order.final_amount ||
        order.totalMoney ||
        order.total_money,
      0,
    ),
    status: normalizeOrderStatusValue(order.status),
    paymentStatus: normalizePaymentStatusValue(order.paymentStatus),
    returnReason: order.returnReason || "",
    returnEvidenceImages: order.returnEvidenceImages || "",
  };
  orderRuleHint.value = "";
  modalError.value = "";
  showModal.value = true;
  applyManagedOrderRules("init");

  // Auto-fill missing email / address from the account record
  const hasAccountId = Number.isFinite(Number(order.accountId));
  const missingContactInfo = !order.email || !order.address;
  if (hasAccountId && missingContactInfo) {
    await fillAccountInfo(order.accountId, false);
  }
}

// When admin changes the accountId field while creating / editing, auto-fill details
watch(
  () => form.value.accountId,
  (newId, oldId) => {
    if (!showModal.value) return;
    if (newId === oldId) return;
    fillAccountInfo(newId, true);
  },
);

watch(
  () => form.value.status,
  (next, prev) => {
    if (next === prev) return;
    applyManagedOrderRules("status");
  },
);

watch(
  () => form.value.paymentStatus,
  (next, prev) => {
    if (next === prev) return;
    applyManagedOrderRules("payment");
  },
);

watch(
  () => [form.value.paymentMethodId, form.value.paymentMethod, form.value.paymentMethodName],
  () => {
    applyManagedOrderRules("method");
  },
);

function closeModal() {
  showModal.value = false;
}

function requestSaveOrder() {
  const f = form.value;
  if (!f.fullName) {
    modalError.value = "Vui lòng nhập tên khách hàng.";
    return;
  }

  confirmModal.value = {
    title: editingOrderId.value ? "Xác nhận cập nhật" : "Xác nhận tạo mới",
    message: editingOrderId.value
      ? `Bạn có chắc muốn cập nhật thông tin đơn hàng #${editingOrderId.value}?`
      : "Bạn có chắc muốn tạo đơn hàng mới?",
    confirmText: editingOrderId.value ? "Cập nhật" : "Tạo mới",
    danger: false,
    loading: false,
    action: saveOrder,
  };
}

function requestStatusChange(newStatus, label) {
  if (!editingOrderId.value) return;

  requestConfirm({
    title: "Xác nhận thay đổi trạng thái",
    message: `Bạn có chắc muốn chuyển đơn hàng #${editingOrderId.value} sang "${label}"?`,
    confirmText: "Xác nhận",
    danger: false,
    action: () => applyStatusChange(newStatus),
  });
}

function approveCancelRequest() {
  if (!editingOrderId.value) return;
  requestConfirm({
    title: 'Xác nhận hủy đơn',
    message: `Bạn đồng ý hủy đơn hàng #${editingOrderId.value}? Tồn kho sẽ được cộng lại và đơn chuyển sang "Đã hủy".`,
    confirmText: 'Xác nhận hủy',
    danger: true,
    action: () => applyStatusChange('CANCELLED'),
  });
}

function rejectCancelRequest() {
  if (!editingOrderId.value) return;
  requestConfirm({
    title: 'Từ chối yêu cầu hủy',
    message: `Bạn từ chối yêu cầu hủy đơn #${editingOrderId.value}? Đơn sẽ trở về trạng thái "Chờ xử lý".`,
    confirmText: 'Từ chối hủy',
    danger: false,
    action: () => applyStatusChange('PENDING'),
  });
}

function requestCancelOrder() {
  if (!editingOrderId.value) return;

  requestConfirm({
    title: "Xác nhận hủy đơn hàng",
    message: `Bạn có chắc muốn hủy đơn hàng #${editingOrderId.value}? Hành động này không thể hoàn tác.`,
    confirmText: "Hủy đơn hàng",
    danger: true,
    action: () => applyStatusChange("CANCELLED"),
  });
}

function requestCustomerReturn() {
  if (!editingOrderId.value) return;

  requestConfirm({
    title: "Xác nhận khách hoàn trả",
    message: `Khách hàng yêu cầu hoàn trả đơn hàng #${editingOrderId.value}? Đơn sẽ chuyển sang trạng thái "Yêu cầu hoàn".`,
    confirmText: "Xác nhận hoàn trả",
    danger: false,
    action: () => applyStatusChange("RETURN_REQUEST"),
  });
}

function requestRefund() {
  if (!editingOrderId.value) return;

  requestConfirm({
    title: "Xác nhận hoàn tiền",
    message: `Bạn có chắc muốn hoàn tiền cho đơn hàng #${editingOrderId.value}? Trạng thái thanh toán sẽ chuyển thành "Đã hoàn tiền".`,
    confirmText: "Hoàn tiền",
    danger: false,
    action: async () => {
      statusChanging.value = true;
      modalError.value = "";
      try {
        await adminApi.updateOrder(editingOrderId.value, { paymentStatus: "REFUNDED" });
        form.value.paymentStatus = "REFUNDED";
        await loadOrders();
      } catch (e) {
        modalError.value = "Lỗi khi hoàn tiền: " + (e?.message || e);
      } finally {
        statusChanging.value = false;
      }
    },
  });
}

async function applyStatusChange(newStatus) {
  if (!editingOrderId.value) return;

  statusChanging.value = true;
  modalError.value = "";

  try {
    const currentStatus = normalizeOrderStatusValue(form.value.status);
    if (
      isFinalOrderStatus(currentStatus) &&
      currentStatus !== normalizeOrderStatusValue(newStatus)
    ) {
      throw new Error(
        "Đơn hàng đã ở trạng thái cuối, không thể thay đổi trạng thái.",
      );
    }

    const managedState = resolveManagedOrderState(
      form.value,
      newStatus,
      form.value.paymentStatus,
    );

    await adminApi.updateOrder(editingOrderId.value, {
      status: managedState.status,
      paymentStatus: managedState.paymentStatus,
    });

    form.value.status = managedState.status;
    form.value.paymentStatus = managedState.paymentStatus;
    await loadOrders();
    showToast("Cập nhật trạng thái đơn hàng thành công!");
  } finally {
    statusChanging.value = false;
  }
}

async function saveOrder() {
  modalError.value = "";
  const f = form.value;

  const managedState = resolveManagedOrderState(f, f.status, f.paymentStatus);

  const payload = {
    accountId: Number.isFinite(Number(f.accountId))
      ? Number(f.accountId)
      : null,
    fullname: f.fullName,
    email: f.email || "",
    phone: f.phone || "",
    shippingAddress: f.address || "",
    address: f.address || "", // Bao lô ở đây để backend luôn nhận được!
    totalMoney: asNumber(f.totalMoney, 0),
    finalAmount: asNumber(f.finalAmount, 0),
    status: managedState.status,
    paymentStatus: managedState.paymentStatus,
    paymentMethodId: Number.isFinite(Number(f.paymentMethodId))
      ? Number(f.paymentMethodId)
      : 1,
  };

  saving.value = true;
  try {
    const isEdit = !!editingOrderId.value;
    if (editingOrderId.value) {
      await adminApi.updateOrder(editingOrderId.value, payload);
    } else {
      await adminApi.createOrder(payload);
    }

    closeModal();
    await loadOrders();
    showToast(isEdit ? "Cập nhật đơn hàng thành công!" : "Tạo đơn hàng mới thành công!");
  } catch (err) {
    modalError.value = err?.message || "Không thể lưu đơn hàng.";
  } finally {
    saving.value = false;
  }
}

function restoreOrder(order) {
  confirmModal.value = {
    title: "Xác nhận khôi phục",
    message: `Bạn có chắc muốn khôi phục đơn hàng #${order.id}?`,
    confirmText: "Khôi phục",
    danger: false,
    loading: false,
    action: async () => {
      await adminApi.restoreOrder(order.id, order.status);
      const restoredId = Number(order?.id);
      hiddenOrders.value = hiddenOrders.value.filter(
        (hidden) => Number(hidden.id) !== restoredId,
      );
      await loadOrders();
      showToast(`Đã khôi phục đơn hàng #${order.id}.`);
    },
  };
}

function restoreAllHiddenOrders() {
  confirmModal.value = {
    title: "Xác nhận khôi phục",
    message: `Bạn có chắc muốn khôi phục tất cả ${hiddenOrders.value.length} đơn hàng đã ẩn?`,
    confirmText: "Khôi phục tất cả",
    danger: false,
    loading: false,
    action: async () => {
      for (const order of hiddenOrders.value) {
        await adminApi.restoreOrder(order.id, order.status);
      }
      hiddenOrders.value = [];
      await loadOrders();
      showToast("Đã khôi phục tất cả đơn hàng thành công!");
    },
  };
}

async function loadOrders(silent = false) {
  if (!silent) {
    loading.value = true;
    error.value = "";
  }
  try {
    const data = await adminApi.getOrders({ page: 0, size: 250 });
    const newOrdersList = Array.isArray(data) ? data : [];
    
    // Check for new orders
    if (!isFirstLoad.value && newOrdersList.length > 0) {
      const oldLatestId = orders.value.length > 0 ? orders.value[0].id : 0;
      const newLatestId = newOrdersList[0].id;
      if (newLatestId > oldLatestId) {
         newOrderToast.value = {
            title: "Có đơn hàng mới!",
            message: `Khách hàng ${newOrdersList[0].fullname || newOrdersList[0].fullName || 'mới'} vừa đặt đơn #${newLatestId}.`
         };
         setTimeout(() => { newOrderToast.value = null; }, 5000);
      }
    }
    
    orders.value = newOrdersList;
    isFirstLoad.value = false;
  } catch (err) {
    console.error("Failed to load admin orders:", err);
    if (!silent) error.value = "Không thể tải đơn hàng. Vui lòng kiểm tra backend API.";
  } finally {
    if (!silent) loading.value = false;
  }
}

onMounted(() => {
  loadOrders();
  pollingInterval = setInterval(() => {
    loadOrders(true);
  }, 10000); // Poll every 10 seconds
});

import { onUnmounted } from "vue";
onUnmounted(() => {
  if (pollingInterval) clearInterval(pollingInterval);
});
</script>

<style scoped>
@keyframes slideInUp {
  from {
    transform: translateY(100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}
.animate-slide-in-up {
  animation: slideInUp 0.3s ease-out forwards;
}

.admin-orders-table-scroll {
  scrollbar-width: thin;
  scrollbar-color: #cbd5e1 transparent;
}

.admin-orders-table-scroll::-webkit-scrollbar {
  height: 8px;
}

.admin-orders-table-scroll::-webkit-scrollbar-track {
  background: transparent;
}

.admin-orders-table-scroll::-webkit-scrollbar-thumb {
  border: 2px solid transparent;
  background-clip: content-box;
  border-radius: 999px;
  background: #cbd5e1;
}

.admin-orders-table-scroll::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

.admin-orders-table tbody tr:hover > td {
  background-color: #f9fafb;
}

:global(.dark) .admin-orders-table-scroll {
  scrollbar-color: #cbd5e1 transparent;
}

:global(.dark) .admin-orders-table-scroll::-webkit-scrollbar-track {
  background: transparent;
}

:global(.dark) .admin-orders-table-scroll::-webkit-scrollbar-thumb {
  background: #475569;
}

:global(.dark) .admin-orders-table tbody tr:hover > td {
  background-color: #1f2937;
}
</style>





