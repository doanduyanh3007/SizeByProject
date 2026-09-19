<template>
  <div class="retail-shell min-h-screen">
    <MainTopBar />

    <main
      class="mx-auto flex w-full max-w-[1240px] flex-col gap-8 px-4 py-10 md:px-8 xl:px-10"
    >
      <section class="grid gap-6 lg:grid-cols-[1.04fr_0.96fr]">
        <article class="retail-card px-6 py-7 sm:px-8 lg:px-9 lg:py-9">
          <span class="retail-kicker">Đơn hàng của bạn</span>
          <h1
            class="retail-heading mt-4 text-[50px] leading-[0.94] text-slate-900 dark:text-white sm:text-[66px]"
          >
            Lịch sử mua sắm
          </h1>
          <p
            class="mt-5 max-w-2xl text-base leading-8 text-slate-600 dark:text-[#cabdae] sm:text-lg"
          >
            Theo dõi trạng thái đơn, tổng chi tiêu và kiểm tra thanh toán trên
            cùng một bảng điều khiển.
          </p>

          <div class="mt-8 grid gap-4 sm:grid-cols-2">
            <article
              class="rounded-[24px] border border-slate-200/70 bg-white/75 p-5 dark:border-[#3c342e] dark:bg-[#241d19]/80"
            >
              <p
                class="text-[11px] font-semibold uppercase tracking-[0.2em] text-primary"
              >
                Tổng đơn
              </p>
              <p
                class="mt-3 font-headline text-[42px] leading-none text-slate-900 dark:text-white"
              >
                {{ totalOrders }}
              </p>
            </article>
            <article
              class="rounded-[24px] border border-slate-200/70 bg-white/75 p-5 dark:border-[#3c342e] dark:bg-[#241d19]/80"
            >
              <p
                class="text-[11px] font-semibold uppercase tracking-[0.2em] text-primary"
              >
                Đã giao
              </p>
              <p
                class="mt-3 font-headline text-[42px] leading-none text-slate-900 dark:text-white"
              >
                {{ deliveredCount }}
              </p>
            </article>
            <article
              class="rounded-[24px] border border-slate-200/70 bg-white/75 p-5 dark:border-[#3c342e] dark:bg-[#241d19]/80"
            >
              <p
                class="text-[11px] font-semibold uppercase tracking-[0.2em] text-primary"
              >
                Tổng chi
              </p>
              <p
                class="mt-3 break-words font-headline text-[22px] leading-tight text-slate-900 dark:text-white sm:text-[26px]"
                :title="formatMoney(totalSpent)"
              >
                {{ formatMoney(totalSpent) }}
              </p>
            </article>
          </div>

          <div class="mt-7 flex flex-wrap gap-3">
            <router-link
              to="/profile"
              class="inline-flex items-center gap-2 rounded-full border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-700 transition-colors hover:border-primary hover:text-primary dark:border-[#4c4138] dark:text-white"
            >
              <span class="material-symbols-outlined text-[18px]">person</span>
              Quay lại tài khoản
            </router-link>
            <router-link
              to="/shop"
              class="inline-flex items-center gap-2 rounded-full bg-primary px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-primary-hover"
            >
              <span class="material-symbols-outlined text-[18px]"
                >storefront</span
              >
              Tiếp tục mua sắm
            </router-link>
          </div>
        </article>

        <article class="retail-card overflow-hidden p-0">
          <div
            class="relative flex h-full min-h-[320px] items-end overflow-hidden bg-[radial-gradient(circle_at_top,_rgba(232,164,90,0.35),_transparent_45%),linear-gradient(135deg,_#1f2937,_#0f172a_58%,_#312e81)] p-8 lg:min-h-[430px]"
          >
            <div
              class="absolute inset-0 bg-[linear-gradient(120deg,transparent_0%,rgba(255,255,255,0.08)_35%,transparent_70%)]"
            />
            <div
              class="absolute right-8 top-8 flex size-24 items-center justify-center rounded-full border border-white/20 bg-white/10 text-white shadow-2xl backdrop-blur"
            >
              <span class="material-symbols-outlined text-5xl"
                >receipt_long</span
              >
            </div>
            <div class="relative z-10 max-w-sm text-white">
              <p
                class="text-[11px] font-semibold uppercase tracking-[0.24em] text-white/70"
              >
                Tình trạng hiện tại
              </p>
              <h2 class="mt-4 font-headline text-4xl leading-tight">
                {{ processingCount }} đơn đang xử lý.
              </h2>
              <p class="mt-4 text-sm leading-7 text-white/75">
                Danh sách bên dưới tự động đồng bộ theo dữ liệu đã lưu từ
                backend sau khi checkout thành công.
              </p>
            </div>
          </div>
        </article>
      </section>

      <section v-if="isLoading" class="retail-card py-20 text-center">
        <div
          class="mx-auto mb-4 h-10 w-10 animate-spin rounded-full border-2 border-slate-300 border-t-primary"
        />
        <p class="text-lg font-semibold text-slate-700 dark:text-white">
          Đang tải lịch sử đơn hàng...
        </p>
      </section>

      <section v-else-if="errorMessage" class="retail-card py-20 text-center">
        <span class="material-symbols-outlined text-6xl text-red-400 opacity-50"
          >error</span
        >
        <p class="mt-4 text-lg font-semibold text-red-600 dark:text-red-400">
          {{ errorMessage }}
        </p>
        <button
          type="button"
          class="mt-5 rounded-full bg-primary px-5 py-2 text-sm font-semibold text-white transition-colors hover:bg-primary-hover"
          @click="loadOrders"
        >
          Tải lại
        </button>
      </section>

      <section
        v-else-if="orders.length === 0"
        class="retail-card py-20 text-center"
      >
        <span
          class="material-symbols-outlined text-6xl text-slate-400 opacity-50"
          >inventory_2</span
        >
        <p class="mt-4 text-lg font-semibold text-slate-700 dark:text-white">
          Bạn chưa có đơn hàng nào.
        </p>
        <p class="mt-2 text-sm text-slate-500 dark:text-[#cabdae]">
          Hãy bắt đầu từ Shop hoặc vào trang chỉnh sửa hồ sơ để quản lý nhanh.
        </p>
        <div class="mt-6 flex flex-wrap justify-center gap-3">
          <router-link
            to="/shop"
            class="rounded-full bg-primary px-5 py-2 text-sm font-semibold text-white transition-colors hover:bg-primary-hover"
          >
            Đi tới Shop
          </router-link>
          <router-link
            to="/profile/edit"
            class="rounded-full border border-slate-300 px-5 py-2 text-sm font-semibold text-slate-700 transition-colors hover:border-primary hover:text-primary dark:border-[#4c4138] dark:text-white"
          >
            Mở hồ sơ
          </router-link>
        </div>
      </section>

      <section v-else class="space-y-5">
        <article class="retail-card hidden overflow-hidden p-0 md:block">
          <div class="overflow-x-auto">
            <table class="min-w-full text-left">
              <thead
                class="border-b border-slate-200 bg-[#f7efe6] dark:border-[#3c342e] dark:bg-[#241d19]"
              >
                <tr>
                  <th
                    class="px-5 py-4 text-[11px] font-semibold uppercase tracking-[0.16em] text-slate-500 dark:text-[#b9aa9a]"
                  >
                    STT
                  </th>
                  <th
                    class="px-5 py-4 text-[11px] font-semibold uppercase tracking-[0.16em] text-slate-500 dark:text-[#b9aa9a]"
                  >
                    Sản phẩm
                  </th>
                  <th
                    class="px-5 py-4 text-[11px] font-semibold uppercase tracking-[0.16em] text-slate-500 dark:text-[#b9aa9a]"
                  >
                    Ngày đặt
                  </th>
                  <th
                    class="px-5 py-4 text-[11px] font-semibold uppercase tracking-[0.16em] text-slate-500 dark:text-[#b9aa9a]"
                  >
                    Trạng thái đơn
                  </th>
                  <th
                    class="px-5 py-4 text-[11px] font-semibold uppercase tracking-[0.16em] text-slate-500 dark:text-[#b9aa9a]"
                  >
                    Thanh toán
                  </th>
                  <th
                    class="px-5 py-4 text-right text-[11px] font-semibold uppercase tracking-[0.16em] text-slate-500 dark:text-[#b9aa9a]"
                  >
                    Thành tiền
                  </th>
                  <th
                    class="px-5 py-4 text-right text-[11px] font-semibold uppercase tracking-[0.16em] text-slate-500 dark:text-[#b9aa9a]"
                  >
                    Chi tiết
                  </th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-200/70 dark:divide-[#3c342e]">
                <tr
                  v-for="(order, index) in paginatedOrders"
                  :key="order.id || `${order.createdAt}-${order.finalAmount}`"
                  class="transition-colors hover:bg-[#fbf4ec] dark:hover:bg-[#2a231f]"
                >
                  <td
                    class="px-5 py-4 text-sm font-bold text-slate-900 dark:text-white"
                  >
                    {{ (historyPage - 1) * 10 + index + 1 }}
                  </td>
                  <td
                    class="px-5 py-4 text-sm font-medium text-slate-900 dark:text-white"
                  >
                    {{ getOrderProductPreview(order) }}
                  </td>
                  <td
                    class="px-5 py-4 text-sm text-slate-600 dark:text-[#cabdae]"
                  >
                    {{ formatDate(order.createdAt) }}
                  </td>
                  <td class="px-5 py-4">
                    <span
                      class="inline-flex items-center rounded-full px-3 py-1 text-[11px] font-semibold uppercase tracking-[0.08em]"
                      :class="getStatusMeta(order.status).className"
                    >
                      {{ getStatusMeta(order.status).label }}
                    </span>
                  </td>
                  <td class="px-5 py-4">
                    <span
                      class="inline-flex items-center rounded-full px-3 py-1 text-[11px] font-semibold uppercase tracking-[0.08em]"
                      :class="getPaymentMeta(order.paymentStatus).className"
                    >
                      {{ getPaymentMeta(order.paymentStatus).label }}
                    </span>
                  </td>
                  <td
                    class="px-5 py-4 text-right text-sm font-bold text-slate-900 dark:text-white"
                  >
                    {{ formatMoney(order.finalAmount) }}
                  </td>
                  <td class="px-5 py-4 text-right">
                    <button
                      type="button"
                      class="inline-flex items-center gap-1 rounded-full border border-slate-300 px-3 py-1.5 text-xs font-semibold text-slate-700 transition-colors hover:border-primary hover:text-primary disabled:cursor-not-allowed disabled:opacity-60 dark:border-[#4c4138] dark:text-[#f5ece4]"
                      :disabled="isDetailLoading"
                      @click="openOrderDetailsFromList(order)"
                    >
                      <span class="material-symbols-outlined text-[15px]"
                        >visibility</span
                      >
                      Xem
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </article>

        <article class="space-y-3 md:hidden">
          <div
            v-for="(order, index) in paginatedOrders"
            :key="order.id || `${order.createdAt}-${order.finalAmount}`"
            class="retail-card p-4"
          >
            <div class="flex items-start justify-between gap-3">
              <div>
                <p
                  class="text-xs uppercase tracking-[0.14em] text-slate-500 dark:text-[#b9aa9a]"
                >
                  STT
                </p>
                <p
                  class="mt-1 text-sm font-bold text-slate-900 dark:text-white"
                >
                  {{ index + 1 }}
                </p>
              </div>
              <span
                class="inline-flex items-center rounded-full px-3 py-1 text-[11px] font-semibold uppercase tracking-[0.08em]"
                :class="getStatusMeta(order.status).className"
              >
                {{ getStatusMeta(order.status).label }}
              </span>
            </div>

            <div class="mt-3 grid grid-cols-2 gap-3 text-sm">
              <div>
                <p
                  class="text-xs uppercase tracking-[0.14em] text-slate-500 dark:text-[#b9aa9a]"
                >
                  Ngày đặt
                </p>
                <p class="mt-1 text-slate-700 dark:text-[#cabdae]">
                  {{ formatDate(order.createdAt) }}
                </p>
              </div>
              <div>
                <p
                  class="text-xs uppercase tracking-[0.14em] text-slate-500 dark:text-[#b9aa9a]"
                >
                  Thanh toán
                </p>
                <p class="mt-1 text-slate-700 dark:text-[#cabdae]">
                  {{ getPaymentMeta(order.paymentStatus).label }}
                </p>
              </div>
              <div class="col-span-2">
                <p
                  class="text-xs uppercase tracking-[0.14em] text-slate-500 dark:text-[#b9aa9a]"
                >
                  Sản phẩm
                </p>
                <p class="mt-1 text-slate-700 dark:text-[#cabdae]">
                  {{ getOrderProductPreview(order) }}
                </p>
              </div>
            </div>

            <div
              class="mt-4 border-t border-slate-200/70 pt-3 text-right dark:border-[#3c342e]"
            >
              <p
                class="text-xs uppercase tracking-[0.14em] text-slate-500 dark:text-[#b9aa9a]"
              >
                Thành tiền
              </p>
              <p
                class="mt-1 text-base font-bold text-slate-900 dark:text-white"
              >
                {{ formatMoney(order.finalAmount) }}
              </p>
            </div>

            <button
              type="button"
              class="mt-4 inline-flex w-full items-center justify-center gap-2 rounded-full border border-slate-300 px-3 py-2 text-sm font-semibold text-slate-700 transition-colors hover:border-primary hover:text-primary disabled:cursor-not-allowed disabled:opacity-60 dark:border-[#4c4138] dark:text-[#f5ece4]"
              :disabled="isDetailLoading"
              @click="openOrderDetailsFromList(order)"
            >
              <span class="material-symbols-outlined text-[18px]"
                >receipt_long</span
              >
              Xem chi tiết đơn
            </button>
          </div>
        </article>

        
          <div class="flex justify-end mt-4">
            <article class="w-full sm:w-1/3 rounded-[24px] border border-slate-200/70 bg-white p-5 shadow-sm dark:border-[#3c342e] dark:bg-[#241d19]">
              <p class="text-[11px] font-semibold uppercase tracking-[0.2em] text-primary">Tổng chi</p>
              <p class="mt-3 break-words font-headline text-[22px] leading-tight text-slate-900 dark:text-white sm:text-[26px]" :title="formatMoney(totalSpent)">
                {{ formatMoney(totalSpent) }}
              </p>
            </article>
          </div>

          <PaginationBar
          v-if="orders.length > 0"
          :current-page="historyPage"
          :total-pages="historyTotalPages"
          :page-start="historyPageStart"
          :page-end="historyPageEnd"
          :total-items="historyTotalItems"
          label="đơn hàng"
          @previous="historyPreviousPage"
          @next="historyNextPage"
        />
      </section>

      <transition name="fade-scale">
        <div
          v-if="isDetailOpen"
          class="fixed inset-0 z-[120] flex items-center justify-center px-4 py-6"
        >
          <button
            type="button"
            class="absolute inset-0 bg-black/45 backdrop-blur-sm"
            aria-label="Đóng chi tiết đơn hàng"
            @click="closeOrderDetails"
          />

          <article
            class="relative w-full max-w-3xl overflow-hidden rounded-[26px] border border-slate-200 bg-white shadow-2xl dark:border-[#3c342e] dark:bg-[#1f1915]"
          >
            <header
              class="flex items-start justify-between gap-3 border-b border-slate-200 px-6 py-5 dark:border-[#3c342e]"
            >
              <div>
                <p
                  class="text-[11px] font-semibold uppercase tracking-[0.18em] text-primary"
                >
                  Chi tiết đơn hàng
                </p>
                <h3
                  class="mt-2 text-lg font-bold text-slate-900 dark:text-white"
                >
                  {{ getOrderDisplayLabel(activeDetailOrder) }}
                </h3>
              </div>
              <button
                type="button"
                class="inline-flex size-9 items-center justify-center rounded-full border border-slate-300 text-slate-600 transition-colors hover:border-primary hover:text-primary dark:border-[#4c4138] dark:text-[#f5ece4]"
                @click="closeOrderDetails"
              >
                <span class="material-symbols-outlined text-[18px]">close</span>
              </button>
            </header>

            <div class="max-h-[72vh] overflow-y-auto px-6 py-5">
              <div v-if="isDetailLoading" class="py-12 text-center">
                <div
                  class="mx-auto mb-3 h-8 w-8 animate-spin rounded-full border-2 border-slate-300 border-t-primary"
                />
                <p
                  class="text-sm font-semibold text-slate-600 dark:text-[#cabdae]"
                >
                  Đang tải chi tiết đơn...
                </p>
              </div>

              <div v-else-if="activeDetailOrder" class="space-y-5">
                <div
                  v-if="detailError"
                  class="rounded-2xl border border-red-200 bg-red-50 px-4 py-5 text-sm text-red-700 dark:border-red-900/30 dark:bg-red-900/10 dark:text-red-300"
                >
                  <p class="font-semibold">
                    Không thể tải đủ dữ liệu chi tiết từ server.
                  </p>
                  <p class="mt-1">{{ detailError }}</p>
                  <p class="mt-1">
                    Đang hiển thị thông tin tóm tắt hiện có của đơn hàng.
                  </p>
                </div>

                <div class="grid gap-3 sm:grid-cols-2">
                  <article
                    class="rounded-2xl border border-slate-200/70 bg-[#f7efe6] px-4 py-3 dark:border-[#3c342e] dark:bg-[#2a231f]"
                  >
                    <p
                      class="text-[11px] uppercase tracking-[0.18em] text-slate-500 dark:text-[#b9aa9a]"
                    >
                      Ngày đặt
                    </p>
                    <p
                      class="mt-1 text-sm font-semibold text-slate-900 dark:text-white"
                    >
                      {{ formatDateTime(activeDetailOrder.createdAt) }}
                    </p>
                  </article>
                  <article
                    class="rounded-2xl border border-slate-200/70 bg-[#f7efe6] px-4 py-3 dark:border-[#3c342e] dark:bg-[#2a231f]"
                  >
                    <p
                      class="text-[11px] uppercase tracking-[0.18em] text-slate-500 dark:text-[#b9aa9a]"
                    >
                      Trạng thái
                    </p>
                    <p
                      class="mt-1 text-sm font-semibold text-slate-900 dark:text-white"
                    >
                      {{ getStatusMeta(activeDetailOrder.status).label }}
                    </p>
                  </article>
                  <article
                    class="rounded-2xl border border-slate-200/70 bg-[#f7efe6] px-4 py-3 dark:border-[#3c342e] dark:bg-[#2a231f]"
                  >
                    <p
                      class="text-[11px] uppercase tracking-[0.18em] text-slate-500 dark:text-[#b9aa9a]"
                    >
                      Thanh toán
                    </p>
                    <p
                      class="mt-1 text-sm font-semibold text-slate-900 dark:text-white"
                    >
                      {{
                        getPaymentMeta(activeDetailOrder.paymentStatus).label
                      }}
                    </p>
                  </article>
                  <article
                    class="rounded-2xl border border-slate-200/70 bg-[#f7efe6] px-4 py-3 dark:border-[#3c342e] dark:bg-[#2a231f]"
                  >
                    <p
                      class="text-[11px] uppercase tracking-[0.18em] text-slate-500 dark:text-[#b9aa9a]"
                    >
                      Người nhận
                    </p>
                    <p
                      class="mt-1 text-sm font-semibold text-slate-900 dark:text-white"
                    >
                      {{ getOrderRecipient(activeDetailOrder) }}
                    </p>
                  </article>
                </div>

                <article
                  class="rounded-2xl border border-slate-200/70 bg-white px-4 py-4 dark:border-[#3c342e] dark:bg-[#241d19]"
                >
                  <p
                    class="text-[11px] uppercase tracking-[0.18em] text-slate-500 dark:text-[#b9aa9a]"
                  >
                    Địa chỉ giao hàng
                  </p>
                  <p
                    class="mt-2 text-sm leading-6 text-slate-700 dark:text-[#cabdae]"
                  >
                    {{ getOrderAddress(activeDetailOrder) }}
                  </p>
                </article>

                <article
                  class="rounded-2xl border border-slate-200/70 bg-white px-4 py-4 dark:border-[#3c342e] dark:bg-[#241d19]"
                >
                  <div class="mb-3 flex items-center justify-between">
                    <p
                      class="text-[11px] uppercase tracking-[0.18em] text-slate-500 dark:text-[#b9aa9a]"
                    >
                      Sản phẩm trong đơn
                    </p>
                    <span
                      class="text-xs font-semibold text-slate-500 dark:text-[#b9aa9a]"
                      >{{ detailItems.length }} mục</span
                    >
                  </div>

                  <div v-if="detailItems.length > 0" class="space-y-2">
                    <div
                      v-for="item in detailItems"
                      :key="item.key"
                      class="grid grid-cols-[1fr_auto_auto] items-center gap-3 rounded-xl border border-slate-200/70 px-3 py-3 dark:border-[#3c342e]"
                    >
                      <div>
                        <p
                          class="text-sm font-semibold text-slate-900 dark:text-white"
                        >
                          {{ item.productName }}
                        </p>
                        <p
                          class="mt-0.5 text-xs text-slate-500 dark:text-[#b9aa9a]"
                        >
                          Đơn giá {{ formatMoney(item.unitPrice) }}
                        </p>
                      </div>
                      <span
                        class="text-sm font-semibold text-slate-600 dark:text-[#cabdae]"
                        >x{{ item.quantity }}</span
                      >
                      <span
                        class="text-sm font-bold text-slate-900 dark:text-white"
                        >{{ formatMoney(item.lineTotal) }}</span
                      >
                    </div>
                  </div>

                  <p
                    v-else
                    class="rounded-xl border border-dashed border-slate-300 px-3 py-5 text-sm text-slate-500 dark:border-[#4c4138] dark:text-[#b9aa9a]"
                  >
                    Không có dữ liệu chi tiết sản phẩm cho đơn này.
                  </p>
                </article>

                <article
                  class="grid gap-3 rounded-2xl border border-slate-200/70 bg-[#f7efe6] p-4 dark:border-[#3c342e] dark:bg-[#2a231f] sm:grid-cols-3"
                >
                  <div>
                    <p
                      class="text-[11px] uppercase tracking-[0.18em] text-slate-500 dark:text-[#b9aa9a]"
                    >
                      Tạm tính
                    </p>
                    <p
                      class="mt-1 text-sm font-semibold text-slate-900 dark:text-white"
                    >
                      {{ formatMoney(detailSubtotal) }}
                    </p>
                  </div>
                  <div>
                    <p
                      class="text-[11px] uppercase tracking-[0.18em] text-slate-500 dark:text-[#b9aa9a]"
                    >
                      Giảm giá
                    </p>
                    <p
                      class="mt-1 text-sm font-semibold text-green-600 dark:text-green-400"
                    >
                      -{{ formatMoney(detailDiscount) }}
                    </p>
                  </div>
                  <div>
                    <p
                      class="text-[11px] uppercase tracking-[0.18em] text-slate-500 dark:text-[#b9aa9a]"
                    >
                      Thành tiền
                    </p>
                    <p
                      class="mt-1 text-base font-bold text-slate-900 dark:text-white"
                    >
                      {{ formatMoney(getDetailFinalAmount(activeDetailOrder)) }}
                    </p>
                  </div>
                </article>

                <article
                  class="rounded-2xl border border-slate-200/70 bg-white p-4 dark:border-[#3c342e] dark:bg-[#241d19]"
                >
                  <div
                    class="flex flex-wrap items-center justify-between gap-3"
                  >
                    <p
                      class="text-[11px] uppercase tracking-[0.18em] text-slate-500 dark:text-[#b9aa9a]"
                    >
                      Hành động đơn hàng
                    </p>
                    <button
                      v-if="canRepayActiveOrder"
                      type="button"
                      class="inline-flex items-center gap-2 rounded-full border border-blue-300 bg-blue-50 px-4 py-2 text-sm font-semibold text-blue-700 transition-colors hover:bg-blue-100 disabled:cursor-not-allowed disabled:opacity-60 dark:border-blue-900/30 dark:bg-blue-900/10 dark:text-blue-300 dark:hover:bg-blue-900/20"
                      :disabled="isRepaying"
                      @click="repayWithVNPay"
                    >
                      <span class="material-symbols-outlined text-[18px]">payment</span>
                      {{ isRepaying ? "Đang tạo link..." : "Thanh toán lại bằng VNPay" }}
                    </button>
                    <button
                      v-if="canCancelActiveOrder"
                      type="button"
                      class="inline-flex items-center gap-2 rounded-full border border-red-300 bg-red-50 px-4 py-2 text-sm font-semibold text-red-700 transition-colors hover:bg-red-100 disabled:cursor-not-allowed disabled:opacity-60 dark:border-red-900/30 dark:bg-red-900/10 dark:text-red-300 dark:hover:bg-red-900/20"
                      :disabled="isCancellingOrder || isConfirmingOrder"
                      @click="promptCancelOrder"
                    >
                      <span class="material-symbols-outlined text-[18px]"
                        >cancel</span
                      >
                      {{
                        isCancellingOrder ? "Đang hủy đơn..." : "Hủy đơn này"
                      }}
                    </button>
                    <button
                      v-if="canConfirmActiveOrder"
                      type="button"
                      class="inline-flex items-center gap-2 rounded-full border border-green-300 bg-green-50 px-4 py-2 text-sm font-semibold text-green-700 transition-colors hover:bg-green-100 disabled:cursor-not-allowed disabled:opacity-60 dark:border-green-900/30 dark:bg-green-900/10 dark:text-green-300 dark:hover:bg-green-900/20"
                      :disabled="isConfirmingOrder || isCancellingOrder"
                      @click="promptConfirmOrder"
                    >
                      <span class="material-symbols-outlined text-[18px]"
                        >task_alt</span
                      >
                      {{
                        isConfirmingOrder
                          ? "Đang xác nhận..."
                          : "Xác nhận đã nhận hàng"
                      }}
                    </button>
                    <button
                      v-if="canReturnActiveOrder"
                      type="button"
                      class="inline-flex items-center gap-2 rounded-full border border-orange-300 bg-orange-50 px-4 py-2 text-sm font-semibold text-orange-700 transition-colors hover:bg-orange-100 disabled:cursor-not-allowed disabled:opacity-60 dark:border-orange-900/30 dark:bg-orange-900/10 dark:text-orange-300 dark:hover:bg-orange-900/20"
                      :disabled="isReturningOrder"
                      @click="promptReturnOrder"
                    >
                      <span class="material-symbols-outlined text-[18px]"
                        >assignment_return</span
                      >
                      {{
                        isReturningOrder ? "Đang gửi yêu cầu..." : "Trả hàng"
                      }}
                    </button>
                    <span
                      v-if="!hasAvailableOrderAction"
                      class="text-xs font-medium text-slate-500 dark:text-[#b9aa9a]"
                      >Đơn này chưa có thao tác khả dụng.</span
                    >
                  </div>

                  <p
                    v-if="cancelOrderMessage"
                    class="mt-3 text-sm"
                    :class="
                      isCancelOrderError
                        ? 'text-red-600 dark:text-red-300'
                        : 'text-green-600 dark:text-green-300'
                    "
                  >
                    {{ cancelOrderMessage }}
                  </p>
                  <p
                    v-if="confirmOrderMessage"
                    class="mt-3 text-sm"
                    :class="
                      isConfirmOrderError
                        ? 'text-red-600 dark:text-red-300'
                        : 'text-green-600 dark:text-green-300'
                    "
                  >
                    {{ confirmOrderMessage }}
                  </p>
                </article>
              </div>

              <div
                v-else
                class="rounded-2xl border border-slate-200 px-4 py-5 text-sm text-slate-600 dark:border-[#3c342e] dark:text-[#cabdae]"
              >
                Không có dữ liệu chi tiết cho đơn hàng này.
              </div>
            </div>
          </article>
        </div>
      </transition>

      <transition name="fade-scale">
        <div
          v-if="confirmModal.show"
          class="fixed inset-0 z-[150] flex items-center justify-center px-4 py-6"
        >
          <div
            class="absolute inset-0 bg-black/60 backdrop-blur-sm"
            @click="!confirmModal.isLoading && (confirmModal.show = false)"
          ></div>

          <div
            class="relative w-full max-w-sm rounded-2xl border border-slate-200 bg-white p-6 text-center shadow-2xl dark:border-[#3c342e] dark:bg-[#1f1915]"
          >
            <button
              type="button"
              class="absolute right-3 top-3 inline-flex size-8 items-center justify-center rounded-lg text-slate-400 hover:bg-slate-100 hover:text-slate-600 dark:hover:bg-slate-800"
              @click="!confirmModal.isLoading && (confirmModal.show = false)"
            >
              <span class="material-symbols-outlined text-[20px]">close</span>
            </button>
            <div
              class="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-full"
              :class="
                confirmModal.type === 'cancel'
                  ? 'bg-red-500/10 text-red-500'
                  : 'bg-green-500/10 text-green-500'
              "
            >
              <span class="material-symbols-outlined text-4xl">{{
                confirmModal.type === "cancel" ? "warning" : "task_alt"
              }}</span>
            </div>

            <h3
              class="mb-2 text-xl font-bold tracking-tight text-slate-900 dark:text-white"
            >
              {{ confirmModal.title }}
            </h3>
            <p class="mb-6 text-sm text-slate-500 dark:text-[#cabdae]">
              {{ confirmModal.message }}
            </p>

            <div v-if="confirmModal.type === 'return'" class="mb-6 text-left">
              <label class="mb-2 block text-sm font-semibold text-slate-700 dark:text-white">Lý do hoàn hàng <span class="text-red-500">*</span></label>
              <textarea 
                v-model="confirmModal.returnReason" 
                rows="3"
                placeholder="Vui lòng mô tả chi tiết lý do bạn muốn hoàn hàng..."
                class="w-full rounded-xl border border-slate-900 bg-slate-50 px-4 py-3 text-sm text-slate-700 focus:border-primary focus:outline-none dark:border-[#4c4138] dark:bg-[#2a231f] dark:text-white dark:focus:border-primary"
              ></textarea>
              <label class="mb-2 block text-sm font-semibold text-slate-700 dark:text-white mt-3">Ảnh minh chứng</label>
              <input type="file" @change="handleReturnEvidenceUpload" accept="image/*" class="w-full rounded-xl border border-slate-900 bg-slate-50 px-4 py-2 text-sm text-slate-700 dark:border-[#4c4138] dark:bg-[#2a231f] dark:text-white" />
            </div>

            <div class="flex gap-3">
              
              <button
                @click="executeModalAction"
                :disabled="confirmModal.isLoading"
                class="flex-1 rounded-xl px-4 py-3 text-sm font-bold text-white transition disabled:opacity-50"
                :class="
                  confirmModal.type === 'cancel'
                    ? 'bg-red-600 hover:bg-red-700'
                    : 'bg-green-600 hover:bg-green-700'
                "
              >
                {{
                  confirmModal.isLoading
                    ? "Đang xử lý..."
                    : confirmModal.confirmText
                }}
              </button>
            </div>
          </div>
        </div>
      </transition>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import MainTopBar from "@/components/MainTopBar.vue";
import PaginationBar from "@/components/PaginationBar.vue";
import { usePagination } from "@/composables/usePagination";
import { orderAPI, orderUtils } from "@/services/orders";
import { paymentApi, API_BASE_URL } from "@/services/api";
import axios from "axios";

const route = useRoute();
const router = useRouter();
const currentUser = ref(null);
const orders = ref([]);
const isLoading = ref(false);
const errorMessage = ref("");
const isDetailOpen = ref(false);
const isDetailLoading = ref(false);
const detailError = ref("");
const selectedOrder = ref(null);
const selectedOrderDetail = ref(null);
const isCancellingOrder = ref(false);
const cancelOrderMessage = ref("");
const isCancelOrderError = ref(false);
const isConfirmingOrder = ref(false);
const confirmOrderMessage = ref("");
const isConfirmOrderError = ref(false);
const isReturningOrder = ref(false);
const isRepaying = ref(false);

//Trạng thái trả hàng
const canReturnActiveOrder = computed(() => {
  if (!activeDetailOrder.value || isDetailLoading.value) {
    return false;
  }
  const category = getOrderStatusCategory(activeDetailOrder.value.status);
  return category === "delivered" || category === "completed";
});

const canRepayActiveOrder = computed(() => {
  if (!activeDetailOrder.value || isDetailLoading.value) return false;
  const method = activeDetailOrder.value.paymentMethodName || "";
  const isVnPay = method.toUpperCase().includes("VNPAY");

  // Ẩn nút nếu đã thanh toán thành công
  const pStatus = (activeDetailOrder.value.paymentStatus || "").toUpperCase();
  if (pStatus === "PAID") return false;

  if (activeDetailOrder.value.status !== "PENDING" || !isVnPay) return false;

  const createdDate = new Date(activeDetailOrder.value.createdAt || activeDetailOrder.value.created_at || Date.now());
  const now = new Date();
  const diffMinutes = (now - createdDate) / (1000 * 60);

  return diffMinutes <= 15;
});

const hasAvailableOrderAction = computed(
  () =>
    canCancelActiveOrder.value ||
    canConfirmActiveOrder.value ||
    canReturnActiveOrder.value ||
    canRepayActiveOrder.value,
);

// Trạng thái cho Modal Xác nhận
const confirmModal = reactive({
  show: false,
  type: "cancel", // 'cancel' | 'confirm' | 'return'
  title: "",
  message: "",
  confirmText: "",
  isLoading: false,
  returnReason: "",
  returnEvidenceImageFile: null,
});

const totalOrders = computed(() => orders.value.length);

const {
  currentPage: historyPage,
  totalPages: historyTotalPages,
  paginatedItems: paginatedOrders,
  pageStart: historyPageStart,
  pageEnd: historyPageEnd,
  totalItems: historyTotalItems,
  previousPage: historyPreviousPage,
  nextPage: historyNextPage,
} = usePagination(orders, 10);

const deliveredCount = computed(
  () =>
    orders.value.filter(
      (order) => getOrderStatusCategory(order.status) === "delivered",
    ).length,
);
const processingCount = computed(
  () =>
    orders.value.filter(
      (order) => getOrderStatusCategory(order.status) === "processing",
    ).length,
);
const totalSpent = computed(() =>
  orders.value.reduce((sum, order) => {
    if (!isPaidPaymentStatus(order.paymentStatus)) {
      return sum;
    }
    return sum + Number(order.finalAmount || 0);
  }, 0),
);
const routeOrderId = computed(() => {
  const id = Number(route.params.id);
  return Number.isFinite(id) ? id : null;
});
const activeDetailOrder = computed(() => {
  if (
    selectedOrderDetail.value &&
    typeof selectedOrderDetail.value === "object"
  ) {
    return selectedOrderDetail.value;
  }

  if (selectedOrder.value && typeof selectedOrder.value === "object") {
    return selectedOrder.value;
  }

  return null;
});
const canCancelActiveOrder = computed(() => {
  if (!activeDetailOrder.value || isDetailLoading.value) {
    return false;
  }
  return isCancelableOrderStatus(activeDetailOrder.value.status);
});
const canConfirmActiveOrder = computed(() => {
  if (!activeDetailOrder.value || isDetailLoading.value) {
    return false;
  }
  return getOrderStatusCategory(activeDetailOrder.value.status) === "delivered";
});
const detailItems = computed(() =>
  normalizeOrderDetailItems(activeDetailOrder.value),
);
const detailDiscount = computed(() =>
  Math.max(
    0,
    getOrderAmount(
      activeDetailOrder.value,
      ["discountAmount", "discount_amount"],
      0,
    ),
  ),
);
const detailSubtotal = computed(() => {
  const explicitSubtotal = getOrderAmount(
    activeDetailOrder.value,
    [
      "subtotalAmount",
      "subtotal_amount",
      "totalMoney",
      "total_money",
      "totalAmount",
      "total_amount",
    ],
    Number.NaN,
  );

  if (Number.isFinite(explicitSubtotal) && explicitSubtotal > 0) {
    return explicitSubtotal;
  }

  return detailItems.value.reduce((sum, item) => sum + item.lineTotal, 0);
});

async function repayWithVNPay() {
  if (!activeDetailOrder.value) return;
  try {
    isRepaying.value = true;
    const vnpayResult = await paymentApi.createVnpayPayment(
      activeDetailOrder.value.id,
      getDetailFinalAmount(activeDetailOrder.value)
    );
    if (vnpayResult?.paymentUrl) {
      window.location.href = vnpayResult.paymentUrl;
    } else {
      alert("Không tạo được link thanh toán VNPay.");
    }
  } catch (err) {
    console.error("Lỗi tạo lại link VNPay:", err);
    alert("Có lỗi xảy ra khi tạo link thanh toán.");
  } finally {
    isRepaying.value = false;
  }
}

function getAccountId(user) {
  const source = user && typeof user === "object" ? user : {};
  const value =
    source.id !== undefined && source.id !== null
      ? source.id
      : source.accountId !== undefined && source.accountId !== null
        ? source.accountId
        : source.account_id;

  const n = Number(value);
  return Number.isFinite(n) ? n : null;
}

function normalizeStatusKey(status) {
  return String(status || "")
    .trim()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/\s+/g, "_")
    .replace(/-/g, "_")
    .toUpperCase();
}

function isActivelyProcessingStatus(status) {
  const key = normalizeStatusKey(status);
  if (!key) return false;

  return (
    key.indexOf("SHIP") !== -1 ||
    key.indexOf("TRANSIT") !== -1 ||
    key === "PROCESSING" ||
    key === "CONFIRMED" ||
    key === "DANG_XU_LY" ||
    key === "XAC_NHAN" ||
    key === "IN_PROGRESS" ||
    key === "IN-PROGRESS"
  );
}

function isWaitingForProcessingStatus(status) {
  const key = normalizeStatusKey(status);
  if (!key) return false;

  if (
    key.indexOf("DELIVER") !== -1 ||
    key.indexOf("SHIP") !== -1 ||
    key.indexOf("TRANSIT") !== -1 ||
    key.indexOf("RETURN") !== -1 ||
    key.indexOf("REFUND") !== -1 ||
    key.indexOf("CANCEL") !== -1 ||
    key.indexOf("FAIL") !== -1 ||
    key.indexOf("REJECT") !== -1
  ) {
    return false;
  }

  return (
    key === "PENDING" ||
    key === "CREATED" ||
    key === "WAITING" ||
    key === "CHUA_XU_LY" ||
    key === "CHO_XU_LY" ||
    key === "PROCESSING" ||
    key === "NEW" ||
    key === "AWAITING_PAYMENT" ||
    key === "AWAITING_CONFIRMATION" ||
    key.indexOf("PEND") !== -1 ||
    key.indexOf("WAIT") !== -1 ||
    key.indexOf("AWAIT") !== -1
  );
}

function isCancelableOrderStatus(status) {
  const key = normalizeStatusKey(status);
  return (
    key === "NEW" ||
    key === "PENDING" ||
    key === "WAITING" ||
    key === "CHUA_XU_LY" ||
    key === "CHO_XU_LY" ||
    key === "CONFIRMED" ||
    key === "PROCESSING" ||
    key === "DANG_XU_LY" ||
    key === "XAC_NHAN" ||
    key === "IN_PROGRESS"
  );
}

function getOrderStatusCategory(status) {
  const key = normalizeStatusKey(status);

  if (key === "DELIVERED" || key === "DA_GIAO" || key === "GIAO_THANH_CONG") {
    return "delivered";
  }

  if (
    key.indexOf("RECEIV") !== -1 ||
    key.indexOf("SUCCESS") !== -1 ||
    key.indexOf("COMPLETE") !== -1 ||
    key === "THANH_CONG" ||
    key === "HOAN_THANH"
  ) {
    return "completed";
  }

  if (
    key.indexOf("SHIP") !== -1 ||
    key.indexOf("TRANSIT") !== -1 ||
    key === "DANG_GIAO"
  ) {
    return "shipping";
  }

  if (
    key === "DELIVERY_FAILED" ||
    key === "FAILED_DELIVERY" ||
    key === "GIAO_THAT_BAI" ||
    key === "UNDELIVERED"
  ) {
    return "deliveryFailed";
  }

  if (key === "LOST" || key === "THAT_LAC" || key === "PARCEL_LOST") {
    return "lost";
  }

  if (
    key.indexOf("CANCEL") !== -1 ||
    key.indexOf("FAIL") !== -1 ||
    key.indexOf("REJECT") !== -1 ||
    key === "DA_HUY" ||
    key === "HUY"
  ) {
    return "cancelled";
  }

  if (
    key.indexOf("RETURNING") !== -1 ||
    key === "TRA_HANG" ||
    key === "YEU_CAU_TRA"
  ) {
    return "returning";
  }

  if (
    key.indexOf("RETURN") !== -1 ||
    key.indexOf("REFUND") !== -1 ||
    key === "HOAN_TRA" ||
    key === "HOAN_TIEN"
  ) {
    return "returned";
  }

  return "processing";
}

function getStatusMeta(status) {
  const category = getOrderStatusCategory(status);

  if (category === "delivered") {
    return {
      label: "Đã giao",
      className:
        "bg-teal-100 text-teal-700 dark:bg-teal-900/20 dark:text-teal-300",
    };
  }

  if (category === "completed") {
    return {
      label: "Thành công",
      className:
        "bg-green-100 text-green-700 dark:bg-green-900/20 dark:text-green-300",
    };
  }

  if (category === "shipping") {
    return {
      label: "Đang giao",
      className:
        "bg-blue-100 text-blue-700 dark:bg-blue-900/20 dark:text-blue-300",
    };
  }

  if (category === "cancelled") {
    return {
      label: "Đã hủy",
      className: "bg-red-100 text-red-700 dark:bg-red-900/20 dark:text-red-300",
    };
  }

  if (category === "deliveryFailed") {
    return {
      label: "Giao thất bại",
      className:
        "bg-rose-100 text-rose-700 dark:bg-rose-900/20 dark:text-rose-300",
    };
  }

  if (category === "lost") {
    return {
      label: "Thất lạc",
      className:
        "bg-red-100 text-red-700 dark:bg-red-900/20 dark:text-red-300",
    };
  }

  if (category === "returned") {
    return {
      label: "Đã trả hàng",
      className:
        "bg-slate-200 text-slate-700 dark:bg-slate-700/40 dark:text-slate-200",
    };
  }

  if (category === "returning") {
    return {
      label: "Đang trả hàng",
      className:
        "bg-orange-100 text-orange-700 dark:bg-orange-900/20 dark:text-orange-300",
    };
  }

  const waiting = isWaitingForProcessingStatus(status);
  return {
    label: waiting ? "Chờ xử lý" : "Đang xử lý",
    className: waiting
      ? "bg-amber-100 text-amber-700 dark:bg-amber-900/20 dark:text-amber-300"
      : "bg-blue-100 text-blue-700 dark:bg-blue-900/20 dark:text-blue-300",
  };
}

function getPaymentMeta(paymentStatus) {
  const key = normalizeStatusKey(paymentStatus);
  if (key === "PAID") {
    return {
      label: "Đã thanh toán",
      className:
        "bg-green-100 text-green-700 dark:bg-green-900/20 dark:text-green-300",
    };
  }

  if (
    key === "REFUNDED" ||
    key === "REFUND" ||
    key === "HOAN_TIEN" ||
    key === "CHARGEBACK" ||
    key === "REVERSED"
  ) {
    return {
      label: "Hoàn tiền",
      className:
        "bg-slate-100 text-slate-700 dark:bg-slate-700/40 dark:text-slate-200",
    };
  }

  return {
    label: "Chưa thanh toán",
    className:
      "bg-amber-100 text-amber-700 dark:bg-amber-900/20 dark:text-amber-300",
  };
}

function isPaidPaymentStatus(paymentStatus) {
  const key = normalizeStatusKey(paymentStatus);
  return (
    key === "PAID" ||
    key === "DA_THANH_TOAN" ||
    key === "SETTLED" ||
    key === "CAPTURED"
  );
}

function formatDate(value) {
  if (!value) return "--";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "--";
  return new Intl.DateTimeFormat("vi-VN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  }).format(date);
}

function formatDateTime(value) {
  if (!value) return "--";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "--";
  return new Intl.DateTimeFormat("vi-VN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  }).format(date);
}

function formatMoney(value) {
  return orderUtils.formatPrice(Number(value || 0));
}

function getOrderCreatedTime(order) {
  const source = order && typeof order === "object" ? order : {};
  const dateValue =
    source.createdAt ||
    source.created_at ||
    source.orderDate ||
    source.order_date ||
    source.createdOn ||
    source.created_on;
  const date = new Date(dateValue || 0);
  return Number.isFinite(date.getTime()) ? date.getTime() : 0;
}

function sortOrdersNewestFirst(list) {
  return Array.isArray(list)
    ? list.slice().sort((a, b) => {
        const timeA = getOrderCreatedTime(a);
        const timeB = getOrderCreatedTime(b);
        if (timeA !== timeB) {
          return timeB - timeA;
        }
        return Number(b.id || 0) - Number(a.id || 0);
      })
    : [];
}

function getOrderItems(order) {
  const source = order && typeof order === "object" ? order : {};
  if (Array.isArray(source.orderItems)) return source.orderItems;
  if (Array.isArray(source.order_items)) return source.order_items;
  if (Array.isArray(source.items)) return source.items;
  if (Array.isArray(source.orderDetails)) return source.orderDetails;
  if (Array.isArray(source.order_details)) return source.order_details;
  return [];
}

function getOrderItemName(item) {
  const source = item && typeof item === "object" ? item : {};
  const rawName = firstDefined([
    source.productName,
    source.product_name,
    source.name,
    source.itemName,
    source.item_name,
    source.variantName,
    source.variant_name,
    source.title,
    source.product && source.product.name,
    source.product && source.product.productName,
    source.product && source.product.product_name,
    source.variant && source.variant.name,
    source.variant && source.variant.productName,
    source.variant && source.variant.product_name,
  ]);

  return rawName ? String(rawName).trim() : "";
}

function getOrderProductPreview(order) {
  const items = getOrderItems(order);

  if (items.length > 0) {
    const uniqueNames = [];
    for (const item of items) {
      const name = getOrderItemName(item);
      if (name && uniqueNames.indexOf(name) === -1) {
        uniqueNames.push(name);
      }
    }

    if (uniqueNames.length === 1) {
      return uniqueNames[0];
    }

    if (uniqueNames.length > 1) {
      return `${uniqueNames[0]} +${uniqueNames.length - 1} sản phẩm`;
    }
  }

  const source = order && typeof order === "object" ? order : {};
  const orderLevelName = firstDefined([
    source.productName,
    source.product_name,
    source.name,
    source.itemName,
    source.item_name,
    source.title,
  ]);

  if (orderLevelName) {
    const normalizedName = String(orderLevelName).trim();
    if (normalizedName) {
      return normalizedName;
    }
  }

  return "Không rõ sản phẩm";
}

function hasProductPreviewData(order) {
  const preview = getOrderProductPreview(order);
  return preview !== "Không rõ sản phẩm";
}

async function enrichOrdersWithItemNames(orderList) {
  const source = Array.isArray(orderList) ? orderList.slice() : [];
  if (source.length === 0) {
    return source;
  }

  const details = await Promise.all(
    source.map(async (order) => {
      if (hasProductPreviewData(order)) {
        return null;
      }

      const id = Number(order && order.id);
      if (!Number.isFinite(id)) {
        return null;
      }

      try {
        return await orderAPI.getOrder(id);
      } catch (error) {
        console.warn("Không thể tải tên sản phẩm cho đơn hàng:", error);
        return null;
      }
    }),
  );

  return source.map((order, index) => {
    const detail = details[index];
    if (!detail || typeof detail !== "object") {
      return order;
    }

    return {
      ...order,
      ...detail,
      id: order && order.id ? order.id : detail.id,
    };
  });
}

function firstDefined(values) {
  for (const value of values) {
    if (value !== null && value !== undefined && value !== "") {
      return value;
    }
  }
  return null;
}

function getOrderAmount(order, keys, fallback = 0) {
  const source = order && typeof order === "object" ? order : {};
  const raw = firstDefined(keys.map((key) => source[key]));
  const n = Number(raw);
  return Number.isFinite(n) ? n : fallback;
}

function normalizeOrderDetailItems(order) {
  const source = order && typeof order === "object" ? order : {};
  const rawItems = Array.isArray(source.orderItems)
    ? source.orderItems
    : Array.isArray(source.order_items)
      ? source.order_items
      : Array.isArray(source.items)
        ? source.items
        : Array.isArray(source.orderDetails)
          ? source.orderDetails
          : Array.isArray(source.order_details)
            ? source.order_details
            : [];

  return rawItems.map((rawItem, index) => {
    const item = rawItem && typeof rawItem === "object" ? rawItem : {};
    const variant =
      item.variant && typeof item.variant === "object" ? item.variant : {};
    const product =
      item.product && typeof item.product === "object" ? item.product : {};
    const quantityRaw = firstDefined([
      item.quantity,
      item.qty,
      item.amount,
      item.count,
      1,
    ]);
    const quantityNumber = Number(quantityRaw);
    const quantity =
      Number.isFinite(quantityNumber) && quantityNumber > 0
        ? quantityNumber
        : 1;

    const unitPriceRaw = firstDefined([
      item.price,
      item.unitPrice,
      item.unit_price,
      item.productPrice,
      item.product_price,
      item.variantPrice,
      item.variant_price,
      item.priceAtOrder,
      item.price_at_order,
      item.basePrice,
      item.base_price,
      item.salePrice,
      item.sale_price,
      variant.price,
      variant.salePrice,
      variant.sale_price,
      product.price,
      product.salePrice,
      product.sale_price,
    ]);
    const unitPriceNumber = Number(unitPriceRaw);
    const parsedUnitPrice = Number.isFinite(unitPriceNumber)
      ? Math.max(0, unitPriceNumber)
      : Number.NaN;

    const lineTotalRaw = firstDefined([
      item.lineTotal,
      item.line_total,
      item.totalPrice,
      item.total_price,
      item.subtotal,
      item.sub_total,
      item.totalMoney,
      item.total_money,
      item.totalAmount,
      item.total_amount,
      item.total,
      item.amountTotal,
      item.amount_total,
      item.finalAmount,
      item.final_amount,
    ]);
    const lineTotalNumber = Number(lineTotalRaw);
    const parsedLineTotal = Number.isFinite(lineTotalNumber)
      ? Math.max(0, lineTotalNumber)
      : Number.NaN;

    const unitPrice =
      Number.isFinite(parsedUnitPrice) && parsedUnitPrice > 0
        ? parsedUnitPrice
        : Number.isFinite(parsedLineTotal) &&
            parsedLineTotal > 0 &&
            quantity > 0
          ? parsedLineTotal / quantity
          : 0;

    const lineTotal =
      Number.isFinite(parsedLineTotal) && parsedLineTotal > 0
        ? parsedLineTotal
        : unitPrice * quantity;

    const productNameRaw = firstDefined([
      item.productName,
      item.product_name,
      item.name,
      item.variantName,
      item.variant_name,
      item.title,
      product.name,
      product.productName,
      product.product_name,
      variant.name,
      variant.productName,
      variant.product_name,
    ]);

    const keyRaw = firstDefined([
      item.id,
      item.orderItemId,
      item.order_item_id,
      `${index + 1}-${productNameRaw || "item"}`,
    ]);

    return {
      key: String(keyRaw),
      productName: productNameRaw
        ? String(productNameRaw)
        : `Sản phẩm ${index + 1}`,
      quantity,
      unitPrice,
      lineTotal,
    };
  });
}

function getOrderDisplayLabel(order) {
  const orderNumber = getOrderSequence(order);
  if (Number.isFinite(orderNumber)) {
    return String(orderNumber);
  }

  return "--";
}

function getOrderActionLabel(order) {
  const orderNumber = getOrderSequence(order);
  if (Number.isFinite(orderNumber)) {
    return `đơn số ${orderNumber}`;
  }

  return "đơn hàng này";
}

function getOrderSequence(order) {
  const source = order && typeof order === "object" ? order : {};
  const id = Number(source.id);

  if (Number.isFinite(id)) {
    const index = orders.value.findIndex(
      (item) => Number(item && item.id) === id,
    );
    if (index !== -1) {
      return index + 1;
    }
  }

  return null;
}

function getOrderRecipient(order) {
  const source = order && typeof order === "object" ? order : {};
  const value = firstDefined([
    source.fullname,
    source.fullName,
    source.receiverName,
    source.receiver_name,
    source.customerName,
    source.customer_name,
    source.username,
    source.accountName,
  ]);
  return value ? String(value) : "Không rõ";
}

function getOrderAddress(order) {
  const source = order && typeof order === "object" ? order : {};
  const value = firstDefined([
    source.shippingAddress,
    source.shipping_address,
    source.deliveryAddress,
    source.delivery_address,
    source.address,
  ]);
  return value ? String(value) : "Chưa có thông tin địa chỉ";
}

function getDetailFinalAmount(order) {
  const finalAmount = getOrderAmount(
    order,
    ["finalAmount", "final_amount"],
    Number.NaN,
  );
  if (Number.isFinite(finalAmount) && finalAmount > 0) {
    return finalAmount;
  }

  const fallbackAmount = detailSubtotal.value - detailDiscount.value;
  return Math.max(0, fallbackAmount);
}

async function openOrderDetails(order) {
  cancelOrderMessage.value = "";
  isCancelOrderError.value = false;
  confirmOrderMessage.value = "";
  isConfirmOrderError.value = false;
  selectedOrder.value = order && typeof order === "object" ? order : null;
  selectedOrderDetail.value = null;
  detailError.value = "";
  isDetailOpen.value = true;

  const id = selectedOrder.value ? Number(selectedOrder.value.id) : Number.NaN;
  if (!Number.isFinite(id)) {
    detailError.value = "Không xác định được đơn hàng để tải chi tiết";
    return;
  }

  try {
    isDetailLoading.value = true;
    const detail = await orderAPI.getOrder(id);
    selectedOrderDetail.value = detail;
  } catch (error) {
    detailError.value =
      error && error.message
        ? error.message
        : "Không thể tải chi tiết đơn hàng";
  } finally {
    isDetailLoading.value = false;
  }
}

function applyOrderPatch(orderId, patch) {
  const normalizedId = Number(orderId);
  if (!Number.isFinite(normalizedId) || !patch || typeof patch !== "object") {
    return;
  }

  // Cập nhật in-place: KHÔNG reassign orders.value = orders.value.map(...)
  // Vì reassign kích hoạt watch({ deep: true }) trong usePagination → reset trang về 1
  const idx = orders.value.findIndex((order) => Number(order && order.id) === normalizedId);
  if (idx !== -1) {
    const updated = { ...orders.value[idx], ...patch };
    orders.value.splice(idx, 1, updated);
  }

  if (selectedOrder.value && Number(selectedOrder.value.id) === normalizedId) {
    selectedOrder.value = {
      ...selectedOrder.value,
      ...patch,
    };
  }

  if (
    selectedOrderDetail.value &&
    Number(selectedOrderDetail.value.id) === normalizedId
  ) {
    selectedOrderDetail.value = {
      ...selectedOrderDetail.value,
      ...patch,
    };
  }
}

async function openOrderDetailsFromList(order) {
  const id = Number(order && order.id);
  if (Number.isFinite(id)) {
    await router.push({ name: "order-detail", params: { id: String(id) } });
    return;
  }

  await openOrderDetails(order);
}

async function openOrderDetailsById(orderId) {
  const normalizedId = Number(orderId);
  if (!Number.isFinite(normalizedId)) {
    return;
  }

  const existingOrder = orders.value.find(
    (order) => Number(order && order.id) === normalizedId,
  );
  await openOrderDetails(existingOrder || { id: normalizedId });
}

// ========= LOGIC MỞ POPUP XÁC NHẬN HỦY ĐƠN =========
function promptCancelOrder() {
  const id = Number(activeDetailOrder.value && activeDetailOrder.value.id);
  const orderLabel = getOrderActionLabel(activeDetailOrder.value);
  cancelOrderMessage.value = "";
  isCancelOrderError.value = false;
  confirmOrderMessage.value = "";
  isConfirmOrderError.value = false;

  if (!Number.isFinite(id)) {
    cancelOrderMessage.value = "Không xác định được đơn hàng để hủy.";
    isCancelOrderError.value = true;
    return;
  }

  if (
    !isCancelableOrderStatus(
      activeDetailOrder.value && activeDetailOrder.value.status,
    )
  ) {
    cancelOrderMessage.value = "Chỉ có thể hủy đơn trước khi đơn chuyển sang đang giao.";
    isCancelOrderError.value = true;
    return;
  }

  confirmModal.type = "cancel";
  confirmModal.title = "Xác nhận hủy đơn";
  confirmModal.message = `Bạn chắc chắn muốn hủy ${orderLabel}? Hành động này không thể hoàn tác.`;
  confirmModal.confirmText = "Có, Hủy đơn";
  confirmModal.show = true;
}

// ========= THỰC THI HỦY ĐƠN =========
async function executeCancelOrder() {
  const id = Number(activeDetailOrder.value && activeDetailOrder.value.id);
  const orderLabel = getOrderActionLabel(activeDetailOrder.value);

  try {
    isCancellingOrder.value = true;
    cancelOrderMessage.value = "";
    isCancelOrderError.value = false;

    const cancelledOrder = await orderAPI.cancelOrder(id);
    const nextStatus = (cancelledOrder && cancelledOrder.status) || "CANCELLED";

    applyOrderPatch(id, {
      ...cancelledOrder,
      status: nextStatus,
    });

    cancelOrderMessage.value = `Đã hủy ${orderLabel} thành công.`;
    
    // Auto-close modal and go back to order list after short delay
    setTimeout(() => {
      confirmModal.show = false;
      isDetailOpen.value = false;
      cancelOrderMessage.value = "";
      isCancelOrderError.value = false;
      router.push({ name: "orders" });
    }, 1500);
  } catch (error) {
    cancelOrderMessage.value =
      error && error.message ? error.message : "Không thể hủy đơn hàng.";
    isCancelOrderError.value = true;
  } finally {
    isCancellingOrder.value = false;
  }
}

// ========= LOGIC MỞ POPUP XÁC NHẬN NHẬN HÀNG =========
function promptConfirmOrder() {
  const id = Number(activeDetailOrder.value && activeDetailOrder.value.id);
  const orderLabel = getOrderActionLabel(activeDetailOrder.value);
  cancelOrderMessage.value = "";
  isCancelOrderError.value = false;

  if (!Number.isFinite(id)) {
    confirmOrderMessage.value =
      "Không xác định được đơn hàng để xác nhận nhận hàng.";
    isConfirmOrderError.value = true;
    return;
  }

  if (
    getOrderStatusCategory(
      activeDetailOrder.value && activeDetailOrder.value.status,
    ) !== "delivered"
  ) {
    confirmOrderMessage.value =
      "Chỉ đơn đã giao mới có thể xác nhận đã nhận hàng.";
    isConfirmOrderError.value = true;
    return;
  }

  confirmModal.type = "confirm";
  confirmModal.title = "Xác nhận nhận hàng";
  confirmModal.message = `Bạn xác nhận đã nhận ${orderLabel} và đồng ý thanh toán?`;
  confirmModal.confirmText = "Đã nhận hàng";
  confirmModal.show = true;
}

// ========= THỰC THI XÁC NHẬN NHẬN HÀNG =========
async function executeConfirmOrder() {
  const id = Number(activeDetailOrder.value && activeDetailOrder.value.id);
  const orderLabel = getOrderActionLabel(activeDetailOrder.value);

  try {
    isConfirmingOrder.value = true;
    confirmOrderMessage.value = "";
    isConfirmOrderError.value = false;

    const confirmedOrder = await orderAPI.confirmOrderReceived(id);
    const nextStatus = (confirmedOrder && confirmedOrder.status) || "SUCCESS";
    const nextPaymentStatus = "PAID";

    applyOrderPatch(id, {
      ...confirmedOrder,
      status: nextStatus,
      paymentStatus: nextPaymentStatus,
    });

    confirmOrderMessage.value = `Đã xác nhận nhận hàng cho ${orderLabel} và cập nhật thanh toán thành đã thanh toán.`;
  } catch (error) {
    confirmOrderMessage.value =
      error && error.message ? error.message : "Không thể xác nhận đơn hàng.";
    isConfirmOrderError.value = true;
  } finally {
    isConfirmingOrder.value = false;
  }
}

// ===== XÁC NHẬN HỦY HÀNG ======
function promptReturnOrder() {
  const id = Number(activeDetailOrder.value && activeDetailOrder.value.id);
  const orderLabel = getOrderActionLabel(activeDetailOrder.value);
  confirmOrderMessage.value = "";
  isConfirmOrderError.value = false;

  if (!Number.isFinite(id)) {
    confirmOrderMessage.value = "Không xác định được đơn hàng để trả.";
    isConfirmOrderError.value = true;
    return;
  }

  confirmModal.type = "return";
  confirmModal.title = "Yêu cầu trả hàng";
  confirmModal.message = `Bạn muốn gửi yêu cầu trả hàng cho ${orderLabel}? Đơn sẽ chuyển về chờ xử lý để admin xác nhận các bước trả hàng.`;
  confirmModal.confirmText = "Xác nhận trả hàng";
  confirmModal.returnReason = ""; // Reset reason
  confirmModal.returnEvidenceImageFile = null;
  confirmModal.show = true;
}

function handleReturnEvidenceUpload(event) {
  const file = event.target.files[0];
  if (file) {
    confirmModal.returnEvidenceImageFile = file;
  }
}

async function executeReturnOrder() {
  const id = Number(activeDetailOrder.value && activeDetailOrder.value.id);
  const orderLabel = getOrderActionLabel(activeDetailOrder.value);

  try {
    if (!confirmModal.returnReason || confirmModal.returnReason.trim() === "") {
      confirmOrderMessage.value = "Vui lòng nhập lý do hoàn hàng.";
      isConfirmOrderError.value = true;
      return;
    }

    isReturningOrder.value = true;
    confirmOrderMessage.value = "";
    isConfirmOrderError.value = false;

    let uploadedUrl = "";
    if (confirmModal.returnEvidenceImageFile) {
      const formData = new FormData();
      formData.append("file", confirmModal.returnEvidenceImageFile);
      try {
        const res = await axios.post(`${API_BASE_URL}/upload/image`, formData, {
          headers: { "Content-Type": "multipart/form-data" },
        });
        uploadedUrl = res.data;
      } catch (uploadError) {
        console.error("Lỗi upload ảnh minh chứng:", uploadError);
        confirmOrderMessage.value = "Lỗi khi tải ảnh minh chứng lên.";
        isConfirmOrderError.value = true;
        isReturningOrder.value = false;
        return;
      }
    }

    const returnedOrder = await orderAPI.updateOrder(id, {
      status: "RETURN_REQUEST",
      paymentStatus: "PAID",
      returnReason: confirmModal.returnReason.trim(),
      returnEvidenceImages: uploadedUrl,
    });
    const nextStatus = (returnedOrder && returnedOrder.status) || "RETURN_REQUEST";
    const nextPaymentStatus =
      (returnedOrder && returnedOrder.paymentStatus) || "PAID";

    applyOrderPatch(id, {
      ...returnedOrder,
      status: nextStatus,
      paymentStatus: nextPaymentStatus,
    });

    confirmOrderMessage.value = `Đã gửi yêu cầu trả hàng cho ${orderLabel}. Admin sẽ kiểm tra và xử lý yêu cầu.`;
  } catch (error) {
    confirmOrderMessage.value =
      error && error.message
        ? error.message
        : "Không thể gửi yêu cầu trả hàng.";
    isConfirmOrderError.value = true;
  } finally {
    isReturningOrder.value = false;
  }
}

// ========= ĐIỀU PHỐI HÀNH ĐỘNG CỦA MODAL =========
async function executeModalAction() {
  confirmModal.isLoading = true;
  try {
    if (confirmModal.type === "cancel") {
      await executeCancelOrder();
    } else if (confirmModal.type === "return") {
      await executeReturnOrder();
    } else {
      await executeConfirmOrder();
    }

    if (!isCancelOrderError.value && !isConfirmOrderError.value) {
      confirmModal.show = false;
    }
  } finally {
    confirmModal.isLoading = false;
  }
}

function closeOrderDetails() {
  cancelOrderMessage.value = "";
  isCancelOrderError.value = false;
  confirmOrderMessage.value = "";
  isConfirmOrderError.value = false;
  isDetailOpen.value = false;

  if (routeOrderId.value !== null) {
    router.replace({ name: "orders" });
  }
}

async function loadOrders() {
  isLoading.value = true;
  errorMessage.value = "";

  try {
    const rawUser = localStorage.getItem("user");
    if (!rawUser) {
      router.push("/login");
      return;
    }

    const parsedUser = JSON.parse(rawUser);
    const accountId = getAccountId(parsedUser);

    if (accountId === null) {
      errorMessage.value =
        "Không xác định được tài khoản để tải lịch sử đơn hàng";
      return;
    }

    currentUser.value = parsedUser;
    const list = await orderAPI.getUserOrders(accountId);
    const normalizedList = Array.isArray(list) ? list : [];
    orders.value = sortOrdersNewestFirst(
      await enrichOrdersWithItemNames(normalizedList),
    );
  } catch (error) {
    console.error("Lỗi lấy đơn hàng:", error);
    errorMessage.value =
      error && error.message
        ? error.message
        : "Không thể tải lịch sử đơn hàng. Vui lòng thử lại.";
    orders.value = [];
  } finally {
    isLoading.value = false;
  }
}

onMounted(async () => {
  await loadOrders();

  if (routeOrderId.value !== null) {
    await openOrderDetailsById(routeOrderId.value);
  }
});

watch(routeOrderId, async (next, previous) => {
  if (next === previous) return;

  if (next === null) {
    isDetailOpen.value = false;
    return;
  }

  await openOrderDetailsById(next);
});
</script>

<style scoped>
@keyframes fade-scale-in {
  from {
    opacity: 0;
    transform: scale(0.96);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.fade-scale-enter-active {
  animation: fade-scale-in 0.2s ease-out;
}

.fade-scale-leave-active {
  animation: fade-scale-in 0.16s reverse ease-in;
}
</style>
