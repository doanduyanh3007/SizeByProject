<template>
  <div>
    <AdminShell class="print:hidden">
      <div
        v-if="showSuccessModal"
        class="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40 backdrop-blur-sm transition-opacity print:hidden"
      >
        <div
          class="w-[450px] animate-fade-in-up rounded-3xl bg-white p-8 text-center shadow-2xl dark:bg-[#1f1a17]"
        >
          <span class="material-symbols-outlined mb-4 text-7xl text-green-500"
            >check_circle</span
          >
          <h3 class="mb-2 text-2xl font-bold text-slate-900 dark:text-white">
            Thanh toán thành công!
          </h3>
          <p class="mb-6 text-slate-500 dark:text-[#b9aa9a]">
            Hóa đơn của bạn đã được lưu vào hệ thống.
          </p>

          <div class="mt-6 flex gap-3">
            <button
              @click="printInvoice"
              class="flex flex-1 items-center justify-center gap-2 rounded-xl bg-slate-100 py-3 font-bold text-slate-700 transition-colors hover:bg-slate-200 dark:bg-[#2b241f] dark:text-white dark:hover:bg-[#3c342e]"
            >
              <span class="material-symbols-outlined">print</span> In hóa đơn
            </button>
            <button
              @click="closeSuccessModal"
              class="flex flex-1 items-center justify-center gap-2 rounded-xl bg-primary py-3 font-bold text-white transition-transform hover:scale-[1.02] active:scale-[0.98]"
            >
              <span class="material-symbols-outlined">done_all</span> Hoàn tất
            </button>
          </div>
        </div>
      </div>

      <div class="flex h-[calc(100vh-100px)] flex-col overflow-hidden">
        <div
          class="flex items-center gap-2 overflow-x-auto border-b border-slate-200 pb-2 dark:border-[#3c342e]"
        >
          <button
            v-for="invoice in invoices"
            :key="invoice.id"
            @click="activeInvoiceId = invoice.id"
            class="group relative flex items-center gap-2 rounded-t-xl border-x border-t px-4 py-2 text-sm font-semibold transition-colors"
            :class="
              activeInvoiceId === invoice.id
                ? 'border-slate-200 bg-white text-primary dark:border-[#3c342e] dark:bg-[#1f1a17]'
                : 'border-transparent bg-slate-100 text-slate-500 hover:bg-slate-200 dark:bg-[#2b241f] dark:text-[#b9aa9a]'
            "
          >
            <span class="material-symbols-outlined text-[16px]"
              >receipt_long</span
            >
            {{ invoice.name }}
            <span
              v-if="invoices.length > 1"
              @click.stop="removeInvoice(invoice.id)"
              class="material-symbols-outlined ml-2 rounded-full text-[16px] opacity-0 transition-all hover:bg-red-50 hover:text-red-500 group-hover:opacity-100"
              >close</span
            >
          </button>
          <button
            @click="addInvoice"
            :disabled="invoices.length >= MAX_INVOICES"
            :title="invoices.length >= MAX_INVOICES ? 'Đã đạt giới hạn hóa đơn' : 'Thêm hóa đơn mới'"
            class="flex size-9 items-center justify-center rounded-full transition-colors"
            :class="invoices.length >= MAX_INVOICES
              ? 'bg-slate-200 text-slate-400 cursor-not-allowed dark:bg-[#26221f] dark:text-[#5a524d]'
              : 'bg-slate-100 text-slate-600 hover:bg-primary hover:text-white dark:bg-[#2b241f] dark:text-[#b9aa9a]'"
          >
            <span class="material-symbols-outlined text-[20px]">add</span>
          </button>
        </div>

        <div
          v-if="activeInvoice"
          class="mt-4 flex min-h-0 flex-1 grid-cols-1 flex-col gap-6 lg:grid lg:grid-cols-[1.3fr_0.7fr]"
        >
          <section class="flex flex-col gap-4 overflow-hidden">
            <div
              class="flex h-[45%] flex-col rounded-2xl border border-slate-200 bg-white dark:border-[#3c342e] dark:bg-[#1f1a17]"
            >
              <div
                class="border-b border-slate-200 bg-slate-50 p-3 dark:border-[#3c342e] dark:bg-[#2b241f]"
              >
                <div
                  class="flex items-center overflow-hidden rounded-xl border border-slate-300 bg-white focus-within:border-primary focus-within:ring-1 focus-within:ring-primary dark:border-[#4c4138] dark:bg-[#151311]"
                >
                  <span class="pl-3 text-slate-400"
                    ><span class="material-symbols-outlined">search</span></span
                  >
                  <input
                    v-model="searchQuery"
                    type="text"
                    :placeholder="
                      inventoryVariants.length === 0
                        ? 'Đang tải dữ liệu...'
                        : `Tìm kiếm trong ${inventoryVariants.length} sản phẩm...`
                    "
                    class="w-full bg-transparent px-3 py-2 text-sm text-slate-900 outline-none dark:text-white"
                    :disabled="inventoryVariants.length === 0"
                  />
                  <button
                    v-if="searchQuery"
                    @click="searchQuery = ''"
                    class="pr-3 text-slate-400 hover:text-slate-600"
                  >
                    <span class="material-symbols-outlined text-[18px]"
                      >close</span
                    >
                  </button>
                </div>
              </div>

              <div class="flex-1 overflow-y-auto p-3">
                <div class="grid grid-cols-2 gap-3 xl:grid-cols-3">
                  <div
                    v-for="variant in filteredVariants"
                    :key="variant.variantId"
                    @click="addToCart(variant)"
                    class="group flex gap-3 rounded-xl border p-2 transition-all dark:border-[#4c4138] dark:hover:border-primary dark:hover:bg-[#2b241f]"
                    :class="
                      Number(variant.stockQuantity || 0) > 0
                        ? 'cursor-pointer border-slate-200 hover:border-primary hover:bg-slate-50'
                        : 'cursor-not-allowed border-red-200 bg-red-50/40 opacity-75 dark:border-red-900/40 dark:bg-red-950/20'
                    "
                  >
                    <div
                      class="size-16 shrink-0 overflow-hidden rounded-lg bg-slate-100 dark:bg-[#151311]"
                    >
                      <img
                        v-if="variant.imageUrl"
                        :src="variant.imageUrl"
                        class="h-full w-full object-cover transition-transform group-hover:scale-110"
                      />
                      <span
                        v-else
                        class="material-symbols-outlined flex h-full items-center justify-center text-slate-400"
                        >inventory_2</span
                      >
                    </div>
                    <div
                      class="flex flex-1 flex-col justify-center overflow-hidden"
                    >
                      <p
                        class="truncate text-sm font-bold text-slate-900 dark:text-white"
                        :title="variant.productName"
                      >
                        {{ variant.productName }}
                      </p>
                      <p class="text-[11px] text-slate-500 dark:text-[#b9aa9a]">
                        {{ variant.colorName }} - {{ variant.sizeName }}
                      </p>
                      <div class="mt-1 flex items-center justify-between">
                        <p class="text-xs font-bold text-primary">
                          {{ formatMoney(variant.price) }}
                        </p>
                        <span
                          class="text-[10px] font-semibold"
                          :class="
                            variant.stockQuantity > 0
                              ? 'text-green-600'
                              : 'text-red-600'
                          "
                          >{{
                            variant.stockQuantity > 0
                              ? `Kho: ${variant.stockQuantity}`
                              : "Hết hàng"
                          }}</span
                        >
                      </div>
                    </div>
                  </div>
                </div>
                <div
                  v-if="filteredVariants.length === 0"
                  class="flex h-full items-center justify-center text-sm text-slate-500"
                >
                  Không tìm thấy sản phẩm.
                </div>

                <div
                  v-if="activeInvoice.paymentMethodId === 2"
                  class="rounded-xl border border-slate-200 bg-slate-50 p-3 dark:border-[#4c4138] dark:bg-[#151311]"
                >
                  <label
                    class="mb-2 block text-xs font-bold uppercase text-slate-500"
                    >Kênh thanh toán online</label
                  >
                  <select
                    v-model.number="activeInvoice.digitalPaymentMethodId"
                    class="w-full cursor-pointer rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none transition-colors focus:border-primary dark:border-[#4c4138] dark:bg-[#2b241f] dark:text-white"
                  >
                    <option
                      v-for="method in digitalPaymentMethods"
                      :key="method.id"
                      :value="method.id"
                    >
                      {{ method.name }}
                    </option>
                  </select>
                </div>
              </div>
            </div>

            <div
              class="flex h-[55%] flex-col rounded-2xl border border-slate-200 bg-white dark:border-[#3c342e] dark:bg-[#1f1a17]"
            >
              <div
                class="flex items-center gap-2 border-b border-slate-200 bg-slate-50 px-4 py-3 dark:border-[#3c342e] dark:bg-[#2b241f]"
              >
                <span class="material-symbols-outlined text-primary"
                  >shopping_cart</span
                >
                <h3 class="font-bold text-slate-900 dark:text-white">
                  Sản phẩm đã chọn
                </h3>
              </div>
              <div class="flex-1 overflow-y-auto p-0">
                <table class="w-full text-left text-sm">
                  <thead
                    class="sticky top-0 z-10 border-b border-slate-200 bg-slate-50 dark:border-[#3c342e] dark:bg-[#2b241f]"
                  >
                    <tr class="text-slate-500 dark:text-[#b9aa9a]">
                      <th class="w-12 px-4 py-3 font-semibold">STT</th>
                      <th class="px-2 py-3 font-semibold">Sản phẩm</th>
                      <th class="w-32 px-2 py-3 font-semibold">Số lượng</th>
                      <th class="px-2 py-3 text-right font-semibold">
                        Thành tiền
                      </th>
                      <th class="w-12 px-4 py-3 text-center font-semibold"></th>
                    </tr>
                  </thead>
                  <tbody
                    class="divide-y divide-slate-100 dark:divide-[#3c342e]"
                  >
                    <tr
                      v-for="(item, index) in paginatedCartItems"
                      :key="item.variantId"
                      class="hover:bg-slate-50 dark:hover:bg-zinc-50"
                    >
                      <td class="px-4 py-3 font-bold text-slate-400">
                        {{ (posCartPage - 1) * 10 + index + 1 }}
                      </td>
                      <td class="px-2 py-3">
                        <p class="font-bold text-slate-900 dark:text-white">
                          {{ item.productName }}
                        </p>
                        <p class="text-xs text-slate-500 dark:text-[#b9aa9a]">
                          {{ item.colorName }} / Size {{ item.sizeName }}
                        </p>
                      </td>
                      <td class="px-2 py-3">
                        <div
                          class="flex w-fit items-center overflow-hidden rounded-lg border border-slate-200 dark:border-[#4c4138]"
                        >
                          <button
                            @click="updateQuantity(item, -1)"
                            class="px-2 py-1 text-slate-700 hover:bg-slate-100 dark:text-white dark:hover:bg-[#3c342e]"
                          >
                            -
                          </button>
                          <input
                            v-model.number="item.quantity"
                            type="number"
                            min="1"
                            class="w-10 border-none bg-transparent p-0 text-center text-sm text-slate-900 focus:ring-0 dark:text-white"
                            @change="validateQuantity(item)"
                          />
                          <button
                            @click="updateQuantity(item, 1)"
                            class="px-2 py-1 text-slate-700 hover:bg-slate-100 dark:text-white dark:hover:bg-[#3c342e]"
                          >
                            +
                          </button>
                        </div>
                      </td>
                      <td
                        class="px-2 py-3 text-right font-bold text-slate-900 dark:text-white"
                      >
                        {{ formatMoney(item.price * item.quantity) }}
                      </td>
                      <td class="px-4 py-3 text-center">
                        <button
                          @click="removeFromCart(item.variantId)"
                          class="rounded-lg p-1.5 text-red-500 transition-colors hover:bg-red-50 dark:hover:bg-red-900/20"
                        >
                          <span class="material-symbols-outlined text-[18px]"
                            >delete</span
                          >
                        </button>
                      </td>
                    </tr>
                    <tr v-if="activeInvoice.cart.length === 0">
                      <td colspan="5" class="py-12 text-center text-slate-400">
                        Chưa có sản phẩm nào trong giỏ hàng.
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>

              <PaginationBar
                v-if="activeCartItems.length > 0"
                :current-page="posCartPage"
                :total-pages="posCartTotalPages"
                :page-start="posCartPageStart"
                :page-end="posCartPageEnd"
                :total-items="posCartTotalItems"
                label="sản phẩm"
                @previous="posCartPreviousPage"
                @next="posCartNextPage"
              />
            </div>
          </section>

          <section class="flex flex-col gap-4 overflow-y-auto pr-1">
            <article
              class="flex flex-1 flex-col rounded-2xl border border-slate-200 bg-white p-5 dark:border-[#3c342e] dark:bg-[#1f1a17] sticky-actions"
            >
              <div
                class="mb-4 flex items-center gap-2 border-b border-slate-100 pb-3 dark:border-[#3c342e]"
              >
                <span class="material-symbols-outlined text-primary">receipt_long</span>
                <h3 class="text-lg font-bold text-slate-900 dark:text-white">
                  Thanh toán &amp; khách hàng
                </h3>
              </div>

              <div class="space-y-4">
                <div>
                  <p class="mb-2 text-xs font-bold uppercase tracking-wider text-slate-500">
                    Khách hàng <span class="text-red-500">*</span>
                  </p>
                  <div class="space-y-3">
                    <div
                      class="flex items-center rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 focus-within:border-primary dark:border-[#4c4138] dark:bg-[#151311]"
                    >
                      <span class="material-symbols-outlined mr-2 text-[18px] text-slate-400">badge</span>
                      <input
                        v-model="activeInvoice.customer.fullname"
                        type="text"
                        placeholder="Nhập tên khách hàng..."
                        class="w-full bg-transparent text-sm text-slate-900 outline-none dark:text-white"
                      />
                    </div>
                    <div
                      class="flex items-center rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 focus-within:border-primary dark:border-[#4c4138] dark:bg-[#151311]"
                    >
                      <span class="material-symbols-outlined mr-2 text-[18px] text-slate-400">call</span>
                      <input
                        v-model="activeInvoice.customer.phone"
                        type="text"
                        placeholder="Nhập số điện thoại..."
                        class="w-full bg-transparent text-sm text-slate-900 outline-none dark:text-white"
                      />
                    </div>
                  </div>
                </div>

                <div class="border-t border-slate-100 pt-4 dark:border-[#3c342e]">
                  <p class="mb-2 text-xs font-bold uppercase tracking-wider text-slate-500">
                    Mã giảm giá
                  </p>
                  <div class="flex gap-2">
                    <div
                      class="flex flex-1 items-center rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 focus-within:border-primary dark:border-[#4c4138] dark:bg-[#151311]"
                    >
                      <input
                        v-model="activeInvoice.voucherInput"
                        @keyup.enter="applyVoucherByInput"
                        type="text"
                        placeholder="Nhập mã voucher..."
                        class="w-full bg-transparent text-sm font-semibold uppercase outline-none dark:text-white"
                      />
                    </div>
                    <button
                      @click="applyVoucherByInput"
                      class="rounded-xl border border-zinc-300 bg-white px-4 py-2 text-sm font-bold text-zinc-900 transition-colors hover:bg-zinc-50"
                    >
                      Áp dụng
                    </button>
                  </div>

                  <div class="mt-3 max-h-[160px] space-y-2 overflow-y-auto pr-1">
                    <div
                      v-for="(v, voucherIndex) in processedVouchers"
                      :key="v.id"
                      class="flex items-center justify-between rounded-xl border p-3 transition-colors"
                      :class="[
                        v.isApplied
                          ? 'border-primary bg-primary/5 dark:border-primary/50'
                          : 'border-slate-200 dark:border-[#4c4138]',
                        !v.isEligible ? 'opacity-60 grayscale' : '',
                      ]"
                    >
                      <div>
                        <p class="font-black text-slate-900 dark:text-white" :class="v.isApplied ? 'text-primary' : ''">
                          {{ v.code }}
                          <span
                            v-if="voucherIndex === 0 && v.isEligible"
                            class="ml-2 rounded-full bg-emerald-100 px-2 py-0.5 text-[10px] font-bold text-emerald-700"
                          >
                            Tốt nhất · giảm {{ formatMoney(v.calculatedDiscount) }}
                          </span>
                        </p>
                        <p class="mt-0.5 text-xs text-slate-500 dark:text-[#b9aa9a]">
                          Giảm {{ formatDiscountLabel(v) }}, đơn tối thiểu {{ formatMoney(v.minOrder) }}
                        </p>
                      </div>
                      <button
                        v-if="v.isApplied"
                        @click="removeVoucher"
                        class="rounded-lg bg-green-500 px-3 py-1.5 text-xs font-bold text-white"
                      >
                        Đang dùng
                      </button>
                      <button
                        v-else-if="v.isEligible"
                        @click="applySpecificVoucher(v)"
                        class="rounded-lg border border-zinc-300 bg-white px-3 py-1.5 text-xs font-bold text-zinc-900 transition-colors hover:bg-zinc-50"
                      >
                        Dùng mã
                      </button>
                      <span
                        v-else
                        class="rounded-lg bg-slate-100 px-3 py-1.5 text-xs font-bold text-slate-500 dark:bg-[#2b241f]"
                      >
                        Chưa đủ ĐK
                      </span>
                    </div>
                  </div>
                </div>

                <div class="border-t border-slate-100 pt-4 dark:border-[#3c342e]">
                  <div class="space-y-3 text-sm">
                    <div class="flex items-center justify-between text-slate-600 dark:text-[#b9aa9a]">
                      <span>Tạm tính:</span>
                      <span class="font-bold text-slate-900 dark:text-white">{{ formatMoney(activeSubtotal) }}</span>
                    </div>
                    <div class="flex items-center justify-between text-slate-600 dark:text-[#b9aa9a]">
                      <span>Phí vận chuyển:</span>
                      <span class="font-bold text-slate-900 dark:text-white">{{ formatMoney(activeShipping) }}</span>
                    </div>
                    <div
                      v-if="activeDiscount > 0"
                      class="flex items-center justify-between text-green-600 dark:text-green-400"
                    >
                      <span>Giảm giá:</span>
                      <span class="font-bold">- {{ formatMoney(activeDiscount) }}</span>
                    </div>
                    <div class="flex items-center justify-between border-t border-slate-100 pt-3 dark:border-[#3c342e]">
                      <span class="text-base font-bold text-slate-900 dark:text-white">Khách Cần Trả:</span>
                      <span class="text-2xl font-black text-primary">{{ formatMoney(activeTotal) }}</span>
                    </div>

                    <div class="space-y-2 pt-2">
                      <label class="text-xs font-bold uppercase text-slate-500">Thanh toán bằng</label>
                      <div class="flex gap-2">
                        <button
                          @click="activeInvoice.paymentMethodId = 1"
                          class="flex flex-1 items-center justify-center gap-1 rounded-xl border py-2 font-bold transition-colors"
                          :class="
                            activeInvoice.paymentMethodId === 1
                              ? 'border-primary bg-primary/10 text-primary'
                              : 'border-slate-200 bg-slate-50 text-slate-600 dark:border-[#4c4138] dark:bg-[#2b241f] dark:text-[#b9aa9a]'
                          "
                        >
                          <span class="material-symbols-outlined text-[18px]">payments</span>
                          Tiền mặt
                        </button>
                        <button
                          @click="activeInvoice.paymentMethodId = 2"
                          class="flex flex-1 items-center justify-center gap-1 rounded-xl border py-2 font-bold transition-colors"
                          :class="
                            activeInvoice.paymentMethodId === 2
                              ? 'border-primary bg-primary/10 text-primary'
                              : 'border-slate-200 bg-slate-50 text-slate-600 dark:border-[#4c4138] dark:bg-[#2b241f] dark:text-[#b9aa9a]'
                          "
                        >
                          <span class="material-symbols-outlined text-[18px]">qr_code_2</span>
                          Chuyển khoản
                        </button>
                      </div>
                    </div>

                    <div v-if="activeInvoice.paymentMethodId === 1" class="pt-2">
                      <div class="flex items-center justify-between rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 dark:border-[#4c4138] dark:bg-[#151311]">
                        <span class="text-slate-500">Khách đưa:</span>
                        <input
                          v-model.number="activeInvoice.customerPaid"
                          type="number"
                          class="w-32 border-none bg-transparent p-0 text-right text-lg font-bold text-slate-900 outline-none focus:ring-0 dark:text-white"
                        />
                      </div>
                      <div class="mt-2 flex items-center justify-between px-1">
                        <span class="text-slate-500">Tiền thừa:</span>
                        <span class="font-bold text-orange-500">{{
                          formatMoney(Math.max(0, activeInvoice.customerPaid - activeTotal))
                        }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <button
                @click="handlePlaceOrder"
                :disabled="activeInvoice.cart.length === 0 || isProcessing"
                class="mt-6 flex w-full items-center justify-center gap-2 rounded-xl border border-zinc-300 bg-white py-4 text-lg font-black text-zinc-900 shadow-sm transition-all hover:bg-zinc-50 active:scale-[0.98] disabled:cursor-not-allowed disabled:opacity-50"
              >
                <span v-if="isProcessing" class="material-symbols-outlined animate-spin">sync</span>
                <span v-else class="material-symbols-outlined">check_circle</span>
                {{ isProcessing ? "ĐANG XỬ LÝ..." : "XÁC NHẬN THANH TOÁN" }}
              </button>
            </article>
          </section>
        </div>
      </div>
    </AdminShell>

    <div
      id="print-section"
      class="hidden print:block print:bg-white print:text-black print:p-8 print:font-sans"
      v-if="completedOrderInfo"
    >
      <div class="text-center border-b-2 border-black pb-4 mb-4">
        <h1 class="text-3xl font-black uppercase tracking-widest">
          SizeBy — Invoice
        </h1>
        <p class="text-sm">Email: admin@sizeby.com</p>
      </div>

      <h2 class="text-2xl font-bold uppercase text-center mb-6">
        HÓA ĐƠN BÁN HÀNG
      </h2>

      <div class="flex justify-between text-sm mb-6">
        <div>
          <p>
            <strong>Tên khách hàng:</strong>
            {{ completedOrderInfo.customer.fullname }}
          </p>
          <p>
            <strong>Số điện thoại:</strong>
            {{ completedOrderInfo.customer.phone }}
          </p>
          <p>
            <strong>Phương thức:</strong>
            {{
              completedOrderInfo.paymentMethodId === 1
                ? "Tiền mặt"
                : "Chuyển khoản"
            }}
          </p>
        </div>
        <div class="text-right">
          <p><strong>Mã hóa đơn:</strong> {{ completedOrderInfo.orderCode }}</p>
          <p>
            <strong>Ngày tạo:</strong> {{ new Date().toLocaleString("vi-VN") }}
          </p>
        </div>
      </div>

      <h3 class="text-center font-bold text-lg mb-2 uppercase">
        Danh sách sản phẩm
      </h3>

      <table class="w-full text-sm border-collapse mb-4">
        <thead>
          <tr class="border-y border-gray-300">
            <th class="py-2 text-center w-12">STT</th>
            <th class="py-2 text-left">Tên sản phẩm</th>
            <th class="py-2 text-center w-24">Số lượng</th>
            <th class="py-2 text-right w-32">Đơn giá</th>
            <th class="py-2 text-right w-32">Thành tiền</th>
          </tr>
        </thead>
        <tbody class="border-b border-gray-300">
          <tr
            v-for="(item, idx) in completedOrderInfo.cart"
            :key="idx"
            class="border-b border-gray-100 last:border-0"
          >
            <td class="py-3 text-center align-top">{{ idx + 1 }}</td>
            <td class="py-3 pr-2">
              <p class="font-bold">{{ item.productName }}</p>
              <p class="text-xs text-gray-500">
                {{ item.colorName }} / Size {{ item.sizeName }}
              </p>
              <p class="text-xs text-gray-500">Mã SP: {{ item.productCode }}</p>
            </td>
            <td class="py-3 text-center align-top">{{ item.quantity }}</td>
            <td class="py-3 text-right align-top">
              {{ formatMoney(item.price) }}
            </td>
            <td class="py-3 text-right align-top">
              {{ formatMoney(item.price * item.quantity) }}
            </td>
          </tr>
        </tbody>
      </table>

      <div class="flex justify-between text-sm py-1">
        <span>Tổng tiền hàng:</span>
        <span>{{ formatMoney(completedOrderInfo.subtotal) }}</span>
      </div>
      <div class="flex justify-between text-sm py-1">
        <span>Phí vận chuyển:</span>
        <span>{{ formatMoney(completedOrderInfo.shipping) }}</span>
      </div>
      <div
        v-if="completedOrderInfo.discount > 0"
        class="flex justify-between text-sm py-1"
      >
        <span>Giảm giá (Voucher: {{ completedOrderInfo.voucherCode }}):</span>
        <span>-{{ formatMoney(completedOrderInfo.discount) }}</span>
      </div>
      <div
        class="flex justify-between text-lg font-bold border-y border-gray-300 py-2 mt-2"
      >
        <span>Tổng tiền cần thanh toán:</span>
        <span>{{ formatMoney(completedOrderInfo.total) }}</span>
      </div>
      <div class="flex justify-between text-sm py-2">
        <span>Khách đã thanh toán:</span>
        <span>{{ formatMoney(completedOrderInfo.customerPaid) }}</span>
      </div>

      <div class="text-center mt-12 text-xs text-gray-500">
        <p>Cảm ơn quý khách! — SIZEBY STORE</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import axios from "axios";
import { adminApi, colorsApi, sizesApi } from "@/services/api";
import AdminShell from "@/components/admin/AdminShell.vue";
import PaginationBar from "@/components/PaginationBar.vue";
import { usePagination } from "@/composables/usePagination";
import { computeVoucherDiscount } from "@/utils/voucherValues";
import { STANDARD_SHIPPING_FEE } from "@/utils/orderConstants";

function toList(data) {
  if (!data) return [];
  if (Array.isArray(data)) return data;
  if (data.content && Array.isArray(data.content)) return data.content;
  if (data.data) {
    if (Array.isArray(data.data)) return data.data;
    if (data.data.content && Array.isArray(data.data.content))
      return data.data.content;
  }
  return [];
}

const inventoryVariants = ref([]);
const systemVouchers = ref([]);
const searchQuery = ref("");
const isProcessing = ref(false);

const showSuccessModal = ref(false);
const completedOrderInfo = ref(null);

let invoiceCounter = 1;
const MAX_INVOICES = 5;
const createNewInvoice = () => ({
  id: invoiceCounter++,
  name: `Hóa đơn ${invoiceCounter - 1}`,
  cart: [],
  customer: { fullname: "", phone: "", email: "" },
  paymentMethodId: 1,
  digitalPaymentMethodId: 4,
  customerPaid: 0,
  voucherInput: "",
  appliedVoucher: null,
});

const digitalPaymentMethods = [
  { id: 2, name: "VNPay" },
  { id: 3, name: "Momo" },
  { id: 4, name: "Chuyển khoản ngân hàng" },
  { id: 5, name: "ZaloPay" },
];

const resolveEffectivePaymentMethodId = (invoice) => {
  if (!invoice || invoice.paymentMethodId === 1) {
    return 1;
  }

  const selected = Number(invoice.digitalPaymentMethodId || 4);
  return Number.isFinite(selected) ? selected : 4;
};

const invoices = ref([createNewInvoice()]);
const activeInvoiceId = ref(1);

const activeInvoice = computed(() =>
  invoices.value.find((i) => i.id === activeInvoiceId.value),
);

const activeCartItems = computed(() => activeInvoice.value?.cart || []);

const {
  currentPage: posCartPage,
  totalPages: posCartTotalPages,
  paginatedItems: paginatedCartItems,
  pageStart: posCartPageStart,
  pageEnd: posCartPageEnd,
  totalItems: posCartTotalItems,
  previousPage: posCartPreviousPage,
  nextPage: posCartNextPage,
} = usePagination(activeCartItems, 10);

const activeSubtotal = computed(() => {
  if (!activeInvoice.value) return 0;
  return activeInvoice.value.cart.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0,
  );
});

// ✅ LOGIC ĐÃ ĐƯỢC SỬA: Lọc bỏ những voucher quá hạn sử dụng hoặc hết số lượng
const processedVouchers = computed(() => {
  if (!systemVouchers.value) return [];
  const now = new Date();

  return systemVouchers.value
    .filter((v) => {
      const endDate = new Date(v.endDate || v.end_date);
      const limit = v.usageLimit || v.usage_limit || 0;
      const used = v.usedCount || v.used_count || 0;
      // TRẢ VỀ TRUE NẾU: Hạn > Hiện tại VÀ Còn lượt dùng
      return endDate > now && limit > used;
    })
    .map((v) => {
      const minOrder = v.minOrderValue || v.min_order_value || 0;
      const isEligible = activeSubtotal.value >= minOrder;

      let calculatedDiscount = 0;
      if (isEligible) {
        calculatedDiscount = computeVoucherDiscount(v, activeSubtotal.value);
      }

      const isApplied =
        activeInvoice.value &&
        activeInvoice.value.appliedVoucher &&
        activeInvoice.value.appliedVoucher.id === v.id;

      return { ...v, minOrder, isEligible, calculatedDiscount, isApplied };
    })
    .sort((a, b) => b.calculatedDiscount - a.calculatedDiscount); // Sắp xếp mã giảm nhiều nhất lên đầu
});

const formatDiscountLabel = (v) => {
  const type = v.discountType || v.discount_type;
  const val = v.discountValue || v.discount_value || 0;
  const maxCap = v.maxDiscountAmount ?? v.max_discount_amount;
  if (type === "PERCENT") {
    let label = `Giảm ${val}%`;
    if (maxCap != null && Number(maxCap) > 0) {
      label += `, tối đa ${formatMoney(maxCap)}`;
    }
    return label;
  }
  return `Giảm ${formatMoney(val)}`;
};

const activeDiscount = computed(() => {
  if (!activeInvoice.value || !activeInvoice.value.appliedVoucher) return 0;
  return computeVoucherDiscount(
    activeInvoice.value.appliedVoucher,
    activeSubtotal.value,
  );
});

const activeShipping = computed(() =>
  activeInvoice.value?.cart?.length ? STANDARD_SHIPPING_FEE : 0,
);

const activeTotal = computed(() => {
  return Math.max(
    0,
    activeSubtotal.value + activeShipping.value - activeDiscount.value,
  );
});

// THEO DÕI TỔNG TIỀN VÀ TỰ ĐỘNG ÁP DỤNG VOUCHER TỐT NHẤT
watch([activeSubtotal, processedVouchers], ([newVal]) => {
  const inv = activeInvoice.value;
  if (!inv) return;

  if (inv.appliedVoucher) {
    const minOrder =
      inv.appliedVoucher.minOrderValue ||
      inv.appliedVoucher.min_order_value ||
      0;
    if (newVal < minOrder) {
      inv.appliedVoucher = null;
      inv.voucherInput = ""; // Xóa input khi voucher bị remove
    }
  }

  if (!inv.appliedVoucher && newVal > 0) {
    const bestVoucher = processedVouchers.value.find((v) => v.isEligible);
    if (bestVoucher) {
      inv.appliedVoucher = bestVoucher;
      inv.voucherInput = bestVoucher.code;
    }
  }
}, { immediate: true });

onMounted(async () => {
  try {
    try {
      const resVoucher = await axios.get("http://localhost:8080/api/vouchers");
      systemVouchers.value = toList(resVoucher.data).filter(
        (v) => v.status !== "INACTIVE" && v.is_active !== false,
      );
    } catch (err) {
      console.warn("Không tải được danh sách voucher", err);
    }

    const [variantData, productData, colorData, sizeData] = await Promise.all([
      adminApi.getVariants({ page: 0, size: 1000 }).catch(() => []),
      adminApi.getProducts({ page: 0, size: 1000 }).catch(() => []),
      colorsApi?.getAll
        ? colorsApi.getAll().catch(() => [])
        : Promise.resolve([]),
      sizesApi?.getAll
        ? sizesApi.getAll().catch(() => [])
        : Promise.resolve([]),
    ]);

    const rawVariants = toList(variantData);
    const rawProducts = toList(productData);
    const rawColors = toList(colorData);
    const rawSizes = toList(sizeData);

    const productMap = new Map();
    rawProducts.forEach((p) => {
      if (p && p.id) productMap.set(Number(p.id), p);
    });

    const colorMap = new Map();
    rawColors.forEach((c) => {
      if (c && c.id) colorMap.set(Number(c.id), c);
    });

    const sizeMap = new Map();
    rawSizes.forEach((s) => {
      if (s && s.id) sizeMap.set(Number(s.id), s);
    });

    inventoryVariants.value = rawVariants.map((v) => {
      const pId = Number(v.productId || v.product_id);
      const cId = Number(v.colorId || v.color_id);
      const sId = Number(v.sizeId || v.size_id);

      const product = productMap.get(pId) || {};
      const color = colorMap.get(cId) || {};
      const size = sizeMap.get(sId) || {};

      let pName = v.productName || v.product_name || product.name;
      if (!pName || pName === "undefined")
        pName = `Sản phẩm #${pId || v.id || "?"}`;

      return {
        ...v,
        variantId: v.id || Math.random(),
        productName: pName,
        productCode:
          v.productCode ||
          v.product_code ||
          product.productCode ||
          product.product_code ||
          "N/A", // Đã lấy mã SP
        colorName: v.colorName || color.name || "Mặc định",
        sizeName: v.sizeName || size.name || "Mặc định",
        price: Number(v.price) || 0,
        stockQuantity: Number(v.stockQuantity || v.stock_quantity) || 0,
        status: v.status || v.variantStatus || v.variant_status || null,
        imageUrl: v.imageUrl || product.imageUrl || null,
      };
    });
  } catch (error) {
    console.error("Lỗi tải kho hàng POS", error);
  }
});

const filteredVariants = computed(() => {
  try {
    const visibleVariants = inventoryVariants.value.filter(
      (v) =>
        !["HIDDEN", "INACTIVE", "DISCONTINUED", "STOP_SELLING"].includes(
          String(v.status || "").trim().toUpperCase(),
        ),
    );

    const q = searchQuery.value.toLowerCase().trim();
    if (!q) return visibleVariants;

    return visibleVariants.filter((v) => {
      const name = String(v.productName || "").toLowerCase();
      const code = String(v.productCode || "").toLowerCase();
      return name.includes(q) || code.includes(q);
    });
  } catch (error) {
    return [];
  }
});

const addInvoice = () => {
  if (invoices.value.length >= MAX_INVOICES) {
    // Prevent creating more than allowed invoices
    return;
  }

  const newInv = createNewInvoice();
  invoices.value.push(newInv);
  activeInvoiceId.value = newInv.id;
};

const removeInvoice = (id) => {
  invoices.value = invoices.value.filter((i) => i.id !== id);
  if (activeInvoiceId.value === id && invoices.value.length > 0) {
    activeInvoiceId.value = invoices.value[0].id;
  }
};

const addToCart = (variant) => {
  if (variant.stockQuantity <= 0) {
    alert("Sản phẩm này đã hết hàng!");
    return;
  }

  const cart = activeInvoice.value.cart;
  const existing = cart.find((i) => i.variantId === variant.variantId);

  if (existing) {
    if (existing.quantity < variant.stockQuantity) existing.quantity++;
    else alert("Vượt quá số lượng tồn kho!");
  } else {
    cart.push({
      variantId: variant.variantId,
      productName: variant.productName,
      productCode: variant.productCode, // Lưu mã để in
      colorName: variant.colorName,
      sizeName: variant.sizeName,
      price: variant.price,
      maxStock: variant.stockQuantity,
      quantity: 1,
    });
  }
};

const updateQuantity = (item, change) => {
  const newQty = item.quantity + change;
  if (newQty > 0 && newQty <= item.maxStock) {
    item.quantity = newQty;
  }
};

const validateQuantity = (item) => {
  if (item.quantity > item.maxStock) item.quantity = item.maxStock;
  if (item.quantity < 1) item.quantity = 1;
};

const removeFromCart = (variantId) => {
  activeInvoice.value.cart = activeInvoice.value.cart.filter(
    (i) => i.variantId !== variantId,
  );
};

const applySpecificVoucher = (voucher) => {
  if (activeInvoice.value) {
    activeInvoice.value.appliedVoucher = voucher;
    activeInvoice.value.voucherInput = voucher.code;
  }
};

const applyVoucherByInput = () => {
  const inv = activeInvoice.value;
  const code = inv.voucherInput.trim().toUpperCase();
  if (!code) return;

  if (activeSubtotal.value === 0) {
    alert("Vui lòng thêm sản phẩm vào giỏ hàng trước khi nhập mã!");
    return;
  }

  const found = processedVouchers.value.find(
    (v) => v.code && v.code.toUpperCase() === code,
  );
  if (!found) {
    alert("Mã giảm giá không tồn tại!");
    return;
  }

  if (!found.isEligible) {
    alert(
      `Đơn hàng cần đạt tối thiểu ${formatMoney(found.minOrder)} để áp dụng mã này!`,
    );
    return;
  }

  inv.appliedVoucher = found;
  inv.voucherInput = found.code;
};

const removeVoucher = () => {
  if (activeInvoice.value) {
    activeInvoice.value.appliedVoucher = null;
    activeInvoice.value.voucherInput = "";
  }
};

const formatMoney = (val) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
    val || 0,
  );

function normalizePhone(rawValue) {
  return String(rawValue || "")
    .replace(/\D/g, "")
    .trim();
}

function isValidCustomerName(rawValue) {
  const value = String(rawValue || "").trim();
  if (value.length < 2) return false;
  return /^[\p{L}\s'.-]+$/u.test(value);
}

function isValidCustomerPhone(rawValue) {
  const phone = normalizePhone(rawValue);
  return /^\d{7,15}$/.test(phone);
}

const handlePlaceOrder = async () => {
  const inv = activeInvoice.value;

  // Bắt buộc nhập thông tin khách
  if (!inv.customer.fullname || inv.customer.fullname.trim() === "") {
    alert("⚠️ Vui lòng nhập Tên khách hàng trước khi thanh toán!");
    return;
  }
  if (!isValidCustomerName(inv.customer.fullname)) {
    alert("⚠️ Tên khách hàng không hợp lệ. Chỉ dùng chữ và tối thiểu 2 ký tự.");
    return;
  }
  if (!inv.customer.phone || inv.customer.phone.trim() === "") {
    alert("⚠️ Vui lòng nhập Số điện thoại khách hàng!");
    return;
  }
  if (!isValidCustomerPhone(inv.customer.phone)) {
    alert("⚠️ Số điện thoại chỉ gồm chữ số và dài từ 7 đến 15 ký tự.");
    return;
  }

  inv.customer.fullname = String(inv.customer.fullname || "").trim();
  inv.customer.phone = normalizePhone(inv.customer.phone);

  // Kiểm tra tiền thừa
  if (
    inv.paymentMethodId === 1 &&
    inv.customerPaid > 0 &&
    inv.customerPaid < activeTotal.value
  ) {
    alert("Tiền khách đưa không đủ!");
    return;
  }

  isProcessing.value = true;
  try {
    // --- VÁ LỖI 1: LẤY ID NGƯỜI BÁN ĐỂ TRÁNH LỖI NULL TRANSACTION ---
    let adminId = 1; // Mặc định là 1 nếu không lấy được
    try {
      const userStr = localStorage.getItem("user");
      if (userStr) {
        const userObj = JSON.parse(userStr);
        adminId = userObj.id || userObj.accountId || userObj.account_id || 1;
      }
    } catch (e) {}

    const orderData = {
      accountId: adminId, // BẮT BUỘC PHẢI CÓ DÒNG NÀY ĐỂ JAVA KHÔNG BỊ SẬP
      fullname: inv.customer.fullname,
      phone: inv.customer.phone,
      email: inv.customer.email || "khachle@sizeby.com",
      shippingAddress: "Mua trực tiếp tại cửa hàng",
      paymentMethodId: resolveEffectivePaymentMethodId(inv),
      voucherCode: inv.appliedVoucher ? inv.appliedVoucher.code : null, // VÁ LỖI 2: CHUYỀN MÃ VOUCHER
      shippingFee: STANDARD_SHIPPING_FEE,
      items: inv.cart.map((item) => ({
        // Đổi thành items cho chuẩn với Java
        variantId: item.variantId,
        price: item.price,
        quantity: item.quantity,
      })),
    };

    const token = localStorage.getItem("token");
    const reqHeaders = {
      Authorization: token ? `Bearer ${token}` : "",
      "Content-Type": "application/json",
    };

    // BƯỚC 1: Gọi API Tạo Đơn Hàng
    const response = await axios.post(
      "http://localhost:8080/api/orders",
      orderData,
      { headers: reqHeaders },
    );

    const returnedOrderId = response.data?.orderId;

    // BƯỚC 2: TỰ ĐỘNG CHUYỂN TRẠNG THÁI ĐƠN POS THÀNH ĐÃ THANH TOÁN
    if (returnedOrderId) {
      await axios.put(
        `http://localhost:8080/api/orders/${returnedOrderId}`,
        {
          status: "SUCCESS",
          paymentStatus: "PAID",
        },
        { headers: reqHeaders },
      );
    }

    // Đóng gói thông tin để in Bill
    const displayOrderCode = returnedOrderId
      ? "HD" + returnedOrderId
      : "HD" + Date.now().toString().slice(-5);

    completedOrderInfo.value = {
      orderCode: displayOrderCode,
      paymentMethodId: inv.paymentMethodId,
      cart: [...inv.cart],
      customer: { ...inv.customer },
      subtotal: activeSubtotal.value,
      shipping: activeShipping.value,
      discount: activeDiscount.value,
      total: activeTotal.value,
      customerPaid: inv.customerPaid || activeTotal.value,
      voucherCode: inv.appliedVoucher ? inv.appliedVoucher.code : "",
    };

    showSuccessModal.value = true;
  } catch (error) {
    console.error("Chi tiết lỗi thanh toán:", error);
    const msg =
      error.response?.data?.message || error.message || "Không xác định";
    alert("Lỗi thanh toán: " + msg);
  } finally {
    isProcessing.value = false;
  }
};

const printInvoice = () => {
  window.print();
};

const closeSuccessModal = () => {
  showSuccessModal.value = false;
  completedOrderInfo.value = null;
  const currentId = activeInvoiceId.value;
  removeInvoice(currentId);
  if (invoices.value.length === 0) addInvoice();
};
</script>

<style scoped>
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
.animate-fade-in-up {
  animation: fadeInUp 0.4s ease-out forwards;
}

/* Sticky action container inside POS right column */
.sticky-actions {
  position: sticky;
  bottom: 1rem;
  z-index: 30;
}

/* Generic drag-handle style to better fit theme if present */
.drag-handle {
  height: 6px;
  background: linear-gradient(90deg, rgba(59,130,246,0.12), rgba(59,130,246,0.06));
  border-radius: 4px;
}
</style>
