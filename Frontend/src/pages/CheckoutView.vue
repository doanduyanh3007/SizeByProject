<template>
  <div
    class="retail-shell flex min-h-screen flex-col text-slate-900 dark:text-white font-display antialiased selection:bg-primary"
  >
    <transition name="fade-scale">
      <div
        v-if="modal.show"
        class="fixed inset-0 z-[100] flex items-center justify-center px-4"
      >
        <div
          class="absolute inset-0 bg-black/50 backdrop-blur-sm"
          @click="modal.show = false"
        />
        <div
          class="relative w-full max-w-sm rounded-2xl border border-slate-200 bg-white p-6 text-center shadow-2xl dark:border-[#334455] dark:bg-[#18212b]"
        >
          <div
            class="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-2xl"
            :class="
              modal.type === 'success'
                ? 'bg-green-500/15 text-green-600 dark:text-green-400'
                : 'bg-red-500/15 text-red-600 dark:text-red-400'
            "
          >
            <span class="material-symbols-outlined text-3xl">{{
              modal.type === "success" ? "verified" : "error"
            }}</span>
          </div>
          <h3 class="mb-2 text-xl font-black tracking-tight">
            {{ modal.title }}
          </h3>
          <p class="mb-6 text-sm text-slate-500 dark:text-[#9cabba]">
            {{ modal.message }}
          </p>
          <button
            class="w-full rounded-xl bg-primary px-4 py-3 text-sm font-bold text-white transition-colors hover:bg-primary-hover"
            @click="modal.show = false"
          >
            Đã hiểu
          </button>
        </div>
      </div>
    </transition>

    <!-- OTP Modal -->
    <transition name="fade-scale">
      <div v-if="otpModal.show" class="fixed inset-0 z-[100] flex items-center justify-center px-4">
        <div class="absolute inset-0 bg-black/50 backdrop-blur-sm" @click="otpModal.show = false"></div>
        <div class="relative w-full max-w-sm rounded-2xl border border-slate-200 bg-white p-6 text-center shadow-2xl dark:border-[#334455] dark:bg-[#18212b]">
          <h3 class="mb-2 text-xl font-bold">Xác nhận email</h3>
          <p class="mb-4 text-sm text-slate-500">
            Mã xác nhận 6 số đã được gửi đến email <strong>{{ shippingForm.gmail }}</strong>.
          </p>
          <input
            v-model="otpModal.code"
            type="text"
            placeholder="Nhập mã 6 số"
            class="mb-4 w-full rounded-xl border border-slate-300 px-4 py-3 text-center text-lg font-bold tracking-widest outline-none focus:border-primary focus:ring-2 focus:ring-primary/20 dark:border-[#3b4754] dark:bg-[#111418] dark:text-white"
            maxlength="6"
          />
          <p v-if="otpModal.error" class="mb-4 text-sm text-red-500">{{ otpModal.error }}</p>
          <div class="flex gap-3">
            <button class="w-full rounded-xl bg-slate-200 px-4 py-3 text-sm font-bold text-slate-700 hover:bg-slate-300 dark:bg-[#283039] dark:text-white" @click="otpModal.show = false">
              Hủy
            </button>
            <button
              class="w-full rounded-xl bg-primary px-4 py-3 text-sm font-bold text-white hover:bg-primary-hover disabled:opacity-50"
              :disabled="otpModal.loading || otpModal.code.length < 6"
              @click="verifyOtpAndPlaceOrder"
            >
              {{ otpModal.loading ? 'Đang xác thực...' : 'Xác nhận' }}
            </button>
          </div>
        </div>
      </div>
    </transition>

    <MainTopBar />

    <main class="flex-grow max-w-[1240px] w-full mx-auto px-4 py-8 md:px-8 xl:px-10">
      <div class="mb-6 border-b border-slate-200 pb-4 dark:border-[#283039]">
        <h1 class="text-3xl font-black tracking-tight">Thanh toán</h1>
        <p class="mt-1 text-sm text-slate-500 dark:text-[#9cabba]">
          Hoàn tất thông tin để xác nhận đơn hàng của bạn.
        </p>
      </div>

      <div class="grid grid-cols-1 gap-8 lg:grid-cols-12">
        <section class="space-y-6 lg:col-span-7">
          <article
            class="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm dark:border-[#283039] dark:bg-[#18212b]"
          >
            <h2 class="mb-5 flex items-center gap-2 text-xl font-bold">
              <span class="material-symbols-outlined text-primary">person</span>
              Thông tin giao hàng
            </h2>

            <AddressManager
              v-if="currentAccountId"
              ref="addressManagerRef"
              v-model="selectedAddressId"
              :account-id="currentAccountId"
              mode="select"
              :default-fullname="shippingForm.fullname"
              :default-phone="shippingForm.phone"
              class="mb-6"
              @select="applySelectedAddress"
              @ready="handleAddressesReady"
            />

            <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
              <label class="flex flex-col gap-2 md:col-span-2">
                <span
                  class="text-xs font-semibold uppercase tracking-wide text-slate-500 dark:text-[#9cabba]"
                  >Email liên hệ</span
                >
                <input
                  v-model.trim="shippingForm.gmail"
                  type="email"
                  class="rounded-xl border bg-slate-50 px-4 py-3 text-sm text-slate-900 outline-none focus:ring-2 focus:border-primary dark:bg-[#1b2127] dark:text-white dark:border-[#3b4754]"
                  placeholder="peak@example.com"
                />
              </label>

              <label class="flex flex-col gap-2">
                <span
                  class="text-xs font-semibold uppercase tracking-wide text-slate-500 dark:text-[#9cabba]"
                  >Họ và tên</span
                >
                <input
                  v-model.trim="shippingForm.fullname"
                  type="text"
                  class="rounded-xl border bg-slate-50 px-4 py-3 text-sm outline-none focus:ring-2 focus:border-primary dark:bg-[#1b2127] dark:text-white dark:border-[#3b4754]"
                  placeholder="Nguyen Van A"
                />
              </label>

              <label class="flex flex-col gap-2">
                <span
                  class="text-xs font-semibold uppercase tracking-wide text-slate-500 dark:text-[#9cabba]"
                  >Số điện thoại</span
                >
                <input
                  v-model.trim="shippingForm.phone"
                  type="text"
                  class="rounded-xl border bg-slate-50 px-4 py-3 text-sm outline-none focus:ring-2 focus:border-primary dark:bg-[#1b2127] dark:text-white dark:border-[#3b4754]"
                  placeholder="09xxxxxxxx"
                />
              </label>

              <label class="flex flex-col gap-2">
                <span class="text-xs font-semibold uppercase tracking-wide text-slate-500 dark:text-[#9cabba]">Tỉnh/Thành phố</span>
                <SearchableSelect
                  v-model="selectedProvince"
                  :options="provinces"
                  placeholder="Chọn Tỉnh/Thành phố"
                  @change="selectedDistrict = ''; selectedWard = ''"
                />
              </label>

              <label class="flex flex-col gap-2">
                <span class="text-xs font-semibold uppercase tracking-wide text-slate-500 dark:text-[#9cabba]">Quận/Huyện</span>
                <SearchableSelect
                  v-model="selectedDistrict"
                  :options="selectedProvince?.districts || []"
                  placeholder="Chọn Quận/Huyện"
                  :disabled="!selectedProvince"
                  @change="selectedWard = ''"
                />
              </label>

              <label class="flex flex-col gap-2">
                <span class="text-xs font-semibold uppercase tracking-wide text-slate-500 dark:text-[#9cabba]">Phường/Xã</span>
                <SearchableSelect
                  v-model="selectedWard"
                  :options="selectedDistrict?.wards || []"
                  placeholder="Chọn Phường/Xã"
                  :disabled="!selectedDistrict"
                />
              </label>

              <label class="flex flex-col gap-2 md:col-span-2">
                <span class="text-xs font-semibold uppercase tracking-wide text-slate-500 dark:text-[#9cabba]">Địa chỉ chi tiết</span>
                <input
                  v-model.trim="streetAddress"
                  type="text"
                  class="rounded-xl border border-slate-900 bg-slate-50 px-4 py-3 text-sm outline-none focus:ring-2 focus:border-primary dark:bg-[#1b2127] dark:text-white dark:border-[#3b4754]"
                  placeholder="Ví dụ: 42 Kim Giang"
                  @blur="checkAndCalculateRoute(true)"
                  @keyup.enter="checkAndCalculateRoute(true)"
                />
              </label>



              <label class="flex flex-col gap-2 md:col-span-2">
                <span class="text-xs font-semibold uppercase tracking-wide text-slate-500 dark:text-[#9cabba]">Hoặc chọn trên bản đồ</span>
                <div class="hidden"></div>
              </label>
            </div>

            <div
              v-show="isRouteCalculated"
              class="mt-6 rounded-xl border border-green-500/30 bg-green-50/50 p-5 dark:border-green-500/20 dark:bg-green-900/10"
            >
              <h3
                class="mb-4 flex items-center gap-2 text-base font-bold text-green-700 dark:text-green-400"
              >
                <span class="material-symbols-outlined">local_shipping</span>
                Thông tin giao hàng đã xác nhận
              </h3>
              <div class="space-y-3 text-sm">
                <div
                  class="flex items-start justify-between border-b border-green-500/20 pb-3"
                >
                  <span class="font-semibold text-slate-600 dark:text-slate-400"
                    >Kho hàng:</span
                  >
                  <span class="text-right font-medium"
                    >Số 22, Ngõ 1 Phố Mai Động, Tương Mai, Hà Nội</span
                  >
                </div>
                <div
                  class="flex items-start justify-between border-b border-green-500/20 pb-3"
                >
                  <span class="font-semibold text-slate-600 dark:text-slate-400"
                    >Giao đến:</span
                  >
                  <span class="text-right font-medium max-w-[70%]">{{
                    fullAddressString
                  }}</span>
                </div>
                <div class="flex items-center justify-between">
                  <span class="font-semibold text-slate-600 dark:text-slate-400"
                    >Dự kiến giao:</span
                  >
                  <span class="font-bold">{{ estimatedDays }} ngày</span>
                </div>
                <div class="flex items-center justify-between pt-2">
                  <span class="font-semibold text-slate-600 dark:text-slate-400"
                    >Phí vận chuyển:</span
                  >
                  <span class="text-lg font-black text-red-500">{{
                    formatMoney(shipping)
                  }}</span>
                </div>
              </div>
            </div>

            <div
              v-show="routeError"
              class="mt-4 text-sm text-red-500 font-medium"
            >
              {{ routeError }}
            </div>
          </article>

          <article
            class="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm dark:border-[#283039] dark:bg-[#18212b]"
          >
            <h2 class="mb-5 flex items-center gap-2 text-xl font-bold">
              <span class="material-symbols-outlined text-primary">wallet</span>
              Phương thức thanh toán
            </h2>

            <div class="grid grid-cols-1 gap-3 md:grid-cols-2">
              <label
                v-for="method in paymentMethods"
                :key="method.id"
                class="flex cursor-pointer items-center justify-between rounded-xl border px-4 py-3 transition-colors"
                :class="
                  selectedPayment === method.id
                    ? 'border-primary bg-primary/10 text-primary'
                    : 'border-slate-200 bg-white text-slate-700 dark:border-[#3b4754] dark:bg-[#1b2127] dark:text-white'
                "
              >
                <span class="flex items-center gap-3">
                  <input
                    v-model="selectedPayment"
                    type="radio"
                    :value="method.id"
                    class="size-4 accent-primary"
                  />
                  <span class="text-sm font-semibold">{{ method.name }}</span>
                </span>
                <span class="material-symbols-outlined text-base">{{
                  method.icon
                }}</span>
              </label>
            </div>

            <div
              v-if="selectedPayment === 2"
              class="mt-4 rounded-xl border border-slate-200 bg-white p-4 dark:border-[#3b4754] dark:bg-[#1b2127]"
            >
              <label
                class="mb-2 block text-xs font-semibold uppercase tracking-wide text-slate-500 dark:text-[#9cabba]"
                >Chọn kênh thanh toán online</label
              >
              <select
                v-model.number="selectedDigitalPayment"
                class="w-full cursor-pointer rounded-xl border border-slate-900 bg-slate-50 px-4 py-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-primary/20 dark:border-[#3b4754] dark:bg-[#111418]"
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
          </article>
        </section>

        <aside class="lg:col-span-5">
          <div class="sticky top-24 space-y-6">
            <article
              class="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm dark:border-[#283039] dark:bg-[#18212b]"
            >
              <h2 class="mb-5 text-xl font-bold">Tóm tắt đơn hàng</h2>

              <div
                v-if="cartItems.length === 0"
                class="rounded-xl border border-dashed border-slate-300 px-4 py-8 text-center text-sm text-slate-500"
              >
                Giỏ hàng hiện đang trống.
              </div>

              <template v-else>
                <div
                  class="max-h-[280px] space-y-4 overflow-y-auto pr-1 custom-scrollbar"
                >
                  <div
                    v-for="item in cartItems"
                    :key="item.id || item.variantId"
                    class="flex items-center gap-3"
                  >
                    <div
                      class="h-16 w-16 overflow-hidden rounded-lg bg-slate-100 dark:bg-[#1b2127]"
                    >
                      <img
                        :src="getItemImage(item)"
                        :alt="getItemName(item)"
                        class="h-full w-full object-cover"
                      />
                    </div>
                    <div class="min-w-0 flex-1">
                      <p class="truncate text-sm font-semibold">
                        {{ getItemName(item) }}
                      </p>
                      <p class="text-xs text-slate-500 dark:text-[#9cabba]">
                        {{ getSizeLabel(item) }} • {{ getColorLabel(item) }} •
                        x{{ item.quantity }}
                      </p>
                    </div>
                    <p class="text-sm font-bold text-slate-900 dark:text-white">
                      {{ formatMoney(item.price * item.quantity) }}
                    </p>
                    <p
                      v-if="getPromotionPercentForItem(item) > 0"
                      class="text-[11px] font-semibold text-red-500"
                    >
                      Đã giảm {{ getPromotionPercentForItem(item) }}% khuyến mãi
                    </p>
                  </div>
                </div>

                <div class="mt-6 rounded-xl bg-white p-4 dark:bg-[#1b2127]">
                  <label
                    class="mb-2 block text-xs font-semibold uppercase tracking-wide text-slate-500 dark:text-[#9cabba]"
                    >Mã giảm giá</label
                  >
                  <div class="flex gap-2">
                    <input
                      v-model.trim="voucherInput"
                      type="text"
                      placeholder="Nhập mã voucher"
                      class="flex-1 rounded-lg border border-slate-900 bg-slate-50 px-3 py-2 text-sm outline-none focus:border-primary dark:border-[#3b4754] dark:bg-[#111418]"
                    />
                    <button
                      type="button"
                      class="rounded-lg bg-slate-100 px-4 py-2 text-sm font-semibold hover:bg-slate-200 disabled:opacity-60 dark:bg-[#283039]"
                      :disabled="isApplyingVoucher"
                      @click="applyVoucher"
                    >
                      {{ isApplyingVoucher ? "Đang xử lý" : "Áp dụng" }}
                    </button>
                  </div>

                  <div class="mt-3">
                    <div
                      v-if="appliedVoucher && appliedVoucher.code"
                      class="mt-2 flex items-center justify-between rounded-lg border border-green-500/40 bg-green-500/10 px-3 py-2 text-xs"
                    >
                      <span
                        class="font-semibold text-green-600 dark:text-green-400"
                        >Đang dùng mã: {{ appliedVoucher.code }}</span
                      >
                      <button
                        type="button"
                        class="font-semibold text-red-500 hover:text-red-600"
                        @click="clearVoucher"
                      >
                        Gỡ
                      </button>
                    </div>
                    <p
                      class="text-xs font-semibold uppercase tracking-wide text-slate-500 dark:text-[#9cabba] mt-3"
                    >
                      Voucher khả dụng
                    </p>
                    <div
                      v-if="processedAvailableVouchers.length === 0"
                      class="mt-2 text-xs text-slate-500"
                    >
                      Chưa có voucher phù hợp.
                    </div>
                    <div v-else class="mt-2 space-y-2">
                      <div
                        v-for="(voucher, voucherIndex) in processedAvailableVouchers"
                        :key="voucher.id || voucher.code"
                        class="rounded-lg border px-3 py-2"
                        :class="voucher.isEligible ? 'border-slate-200 bg-white dark:border-[#3b4754] dark:bg-[#111418]' : 'border-slate-200 bg-white opacity-70 dark:border-[#3b4754] dark:bg-[#1b2127]'"
                      >
                        <div class="flex items-start justify-between gap-3">
                          <div>
                            <p class="flex flex-wrap items-center gap-2 text-sm font-bold">
                              {{ voucher.code }}
                              <span
                                v-if="voucherIndex === 0 && voucher.isEligible"
                                class="rounded-full bg-emerald-100 px-2 py-0.5 text-[10px] font-bold text-emerald-700"
                              >
                                Đề cử tốt nhất · tiết kiệm {{ formatMoney(voucher.expectedDiscount) }}
                              </span>
                            </p>
                            <p class="text-xs text-slate-500">
                              {{ voucher.description }}
                            </p>
                            <p v-if="!voucher.isEligible" class="mt-1 text-[11px] font-semibold text-amber-600 dark:text-amber-400">
                              Cần tối thiểu {{ formatMoney(voucher.minOrderValue || voucher.min_order_value || 0) }}
                            </p>
                          </div>
                          <button
                            type="button"
                            class="rounded-md px-3 py-1 text-xs font-semibold"
                            :class="voucher.isEligible ? 'bg-primary text-white hover:bg-primary-hover' : 'bg-slate-200 text-slate-500 cursor-not-allowed dark:bg-[#283039]'"
                            :disabled="!voucher.isEligible"
                            @click="applySuggestedVoucher(voucher)"
                          >
                            {{ voucher.isEligible ? 'Dùng mã' : 'Chưa đủ ĐK' }}
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div
                  class="mt-6 space-y-3 border-t border-slate-200 pt-4 dark:border-[#283039]"
                >
                  <div
                    class="flex items-center justify-between text-sm text-slate-600 dark:text-[#9cabba]"
                  >
                    <span>Tạm tính</span>
                    <span
                      class="font-semibold text-slate-900 dark:text-white"
                      >{{ formatMoney(subtotal) }}</span
                    >
                  </div>
                  <div
                    class="flex items-center justify-between text-sm text-slate-600 dark:text-[#9cabba]"
                  >
                    <span>Phí vận chuyển</span>
                    <span
                      class="font-semibold text-slate-900 dark:text-white"
                      >{{ formatMoney(shipping) }}</span
                    >
                  </div>
                  <div
                    v-if="discountAmount > 0"
                    class="flex items-center justify-between text-sm text-green-600 dark:text-green-400"
                  >
                    <span>Giảm giá</span>
                    <span class="font-bold"
                      >-{{ formatMoney(discountAmount) }}</span
                    >
                  </div>
                  <div
                    v-if="promotionDiscountAmount > 0"
                    class="flex items-center justify-between text-sm text-red-600 dark:text-red-400"
                  >
                    <span>Khuyến mãi sản phẩm</span>
                    <span class="font-bold">-{{ formatMoney(promotionDiscountAmount) }}</span>
                  </div>
                  <div
                    class="flex items-center justify-between border-t border-slate-200 pt-4 text-lg dark:border-[#283039]"
                  >
                    <span class="font-bold">Tổng thanh toán</span>
                    <span class="font-black text-primary">{{
                      formatMoney(total)
                    }}</span>
                  </div>
                </div>

                <button
                  type="button"
                  class="mt-6 w-full rounded-xl bg-primary px-4 py-3 text-sm font-bold text-white transition-colors hover:bg-primary-hover disabled:opacity-60"
                  :disabled="isProcessing || cartItems.length === 0"
                  @click="handlePlaceOrder"
                >
                  {{ isProcessing ? "Đang xử lý..." : "Xác nhận đặt hàng" }}
                </button>
              </template>
            </article>
          </div>
        </aside>
      </div>
    </main>
  </div>
</template>

<script setup>
import { getSession } from "@/utils/auth";
import { computed, onMounted, reactive, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useCartStore } from "@/stores/cart";
import SearchableSelect from "@/components/SearchableSelect.vue";
import AddressManager from "@/components/AddressManager.vue";
import { orderAPI, orderUtils, voucherAPI } from "@/services/orders";
import { paymentApi, addressesApi, vouchersApi } from "@/services/api";
import { resolveColorMeta, resolveSizeMeta } from "@/utils/variantValues";
import { computeVoucherDiscount } from "@/utils/voucherValues";
import { STANDARD_SHIPPING_FEE } from "@/utils/orderConstants";
import { resolveImageUrl } from "@/services/api";
import {
  loadProvinces,
  normalizeAddressId,
  pickDefaultAddress,
  resolveAddressHierarchy,
} from "@/utils/addressUtils";

const cartStore = useCartStore();
const route = useRoute();
const router = useRouter();

const currentUser = ref(null);
const currentAccountId = ref(null);
const selectedAddressId = ref(null);
const pendingSelectedAddress = ref(null);
const addressManagerRef = ref(null);
const isProcessing = ref(false);
const isApplyingVoucher = ref(false);
const activePromotions = ref([]);

const otpModal = reactive({
  show: false,
  code: "",
  loading: false,
  error: "",
  verified: false
});

const modal = reactive({
  show: false,
  title: "",
  message: "",
  type: "success",
});

const shippingForm = reactive({
  gmail: "",
  fullname: "",
  phone: "",
  address: "",
});



let map = null;
let marker = null;

function initMap() {
  const mapElement = document.getElementById("checkout-map");
  if (!mapElement) return;

  // Default coordinate (Hanoi)
  const defaultCoord = [21.028511, 105.804817];
  
  map = L.map("checkout-map").setView(defaultCoord, 12);
  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    attribution: "© OpenStreetMap contributors",
  }).addTo(map);

  marker = L.marker(defaultCoord).addTo(map);

  let geocodeTimeout = null;
  map.on("click", (e) => {
    const lat = e.latlng.lat;
    const lng = e.latlng.lng;
    marker.setLatLng([lat, lng]);
    
    if (geocodeTimeout) clearTimeout(geocodeTimeout);
    geocodeTimeout = setTimeout(async () => {
      await reverseGeocode(lat, lng);
    }, 600); // Debounce to avoid 429 Too Many Requests
  });
}

async function reverseGeocode(lat, lng) {
  routeError.value = ""; // Clear previous errors
  try {
    const res = await fetch(`/nominatim/reverse?format=json&lat=${lat}&lon=${lng}&addressdetails=1`);
    if (!res.ok) {
      if (res.status === 429) throw new Error('Dịch vụ bản đồ đang quá tải, vui lòng thử lại sau.');
      const txt = await safeParseResponse(res);
      throw new Error(typeof txt === 'string' ? txt : `Lỗi bản đồ (HTTP ${res.status})`);
    }
    const data = await safeParseResponse(res);
    if (data && data.address) {
      // Find matching Province
      const cityOrProvince = data.address.city || data.address.state || data.address.province;
      if (cityOrProvince) {
        const matchedProv = findBestMatchByName(provinces.value, cityOrProvince);
        if (matchedProv) {
          selectedProvince.value = matchedProv;
          
          await nextTick();
          
          // Find matching District
          const districtOrCounty = data.address.county || data.address.district || data.address.suburb || data.address.town || data.address.city_district;
          if (districtOrCounty && selectedProvince.value.districts) {
            const matchedDist = findBestMatchByName(selectedProvince.value.districts, districtOrCounty);
            if (matchedDist) {
              selectedDistrict.value = matchedDist;
              
              await nextTick();
              
              // Find matching Ward
              const wardOrVillage = data.address.ward || data.address.village || data.address.hamlet || data.address.quarter || data.address.suburb || data.address.neighbourhood;
              if (wardOrVillage && selectedDistrict.value.wards) {
                const matchedWard = findBestMatchByName(selectedDistrict.value.wards, wardOrVillage);
                if (matchedWard) {
                  selectedWard.value = matchedWard;
                }
              }
            }
          }
        }
      }
      
      // Update street address
      const road = data.address.road || "";
      const houseNumber = data.address.house_number || "";
      streetAddress.value = [houseNumber, road].filter(Boolean).join(" ");
    }
  } catch (err) {
    console.error("Lỗi khi reverse geocoding", err);
    routeError.value = err.message || "Không thể lấy địa chỉ tự động.";
  }
}

// -----------------------------------------===== LOGIC CHỌN TỈNH THÀNH & TÍNH SHIP =====
const provinces = ref([]);
const districts = ref([]);
const wards = ref([]);

const selectedProvince = ref(null);
const selectedDistrict = ref(null);
const selectedWard = ref(null);
const streetAddress = ref("");
const deliveryDate = ref("");
const minimumDeliveryDate = new Date().toISOString().slice(0, 10);

const isRouteCalculated = ref(false);
const calculatedShippingFee = ref(0);
const estimatedDays = ref(0);
const fullAddressString = ref("");
const routeError = ref("");
let autoRouteTimer = null;
let autoAddressFillTimer = null;
const isAutoFillingAddress = ref(false);
const skipNextWatcherUpdate = ref(false);

// Tọa độ kho hàng (Số 22, Ngõ 1 Phố Mai Động, Tương Mai, Hà Nội)
const SHOP_COORD = [20.988500, 105.862800];

function normalizeAddressText(value) {
  return String(value || "")
    .toLowerCase()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/[^a-z0-9\s]/g, " ")
    .replace(/\s+/g, " ")
    .trim();
}

function normalizePhone(value) {
  return String(value || "").replace(/\D/g, "").trim();
}

function resolveUserAddress(user) {
  const source = user && typeof user === "object" ? user : {};
  return (
    source.address ||
    source.shippingAddress ||
    source.shipping_address ||
    source.fullAddress ||
    source.full_address ||
    source.diaChi ||
    ""
  );
}

function applyTextFallbackShipping(addressText = "") {
  const normalized = normalizeAddressText(addressText);
  const isHaNoi = normalized.includes("ha noi");

  estimatedDays.value = isHaNoi ? 1 : 3;
  calculatedShippingFee.value = STANDARD_SHIPPING_FEE;
  fullAddressString.value = buildCurrentAddressString(addressText);
  
  routeError.value = "";
  isRouteCalculated.value = true;
}

function buildDetectedAddressString() {
  return [
    selectedWard.value?.name,
    selectedDistrict.value?.name,
    selectedProvince.value?.name,
  ]
    .filter(Boolean)
    .join(", ");
}

function buildCurrentAddressString() {
  return [
    streetAddress.value,
    selectedWard.value?.name,
    selectedDistrict.value?.name,
    selectedProvince.value?.name,
  ]
    .filter(Boolean)
    .join(", ");
}

function scheduleAutoRouteCalculation() {
  if (autoRouteTimer) {
    clearTimeout(autoRouteTimer);
  }

  autoRouteTimer = setTimeout(() => {
    void checkAndCalculateRoute(true);
  }, 500);
}

function scheduleAddressAutoFill() {
  if (autoAddressFillTimer) {
    clearTimeout(autoAddressFillTimer);
  }

  autoAddressFillTimer = setTimeout(async () => {
    if (isAutoFillingAddress.value) return;
    if (String(streetAddress.value || "").trim().length < 3) return;
    await autoFillAddressHierarchyFromText(streetAddress.value);
  }, 800);
}

function splitAddressCandidates(...values) {
  return values
    .flatMap((value) => String(value || "").split(","))
    .map((part) => part.trim())
    .filter(Boolean);
}

function stripAdministrativePrefix(value) {
  return normalizeAddressText(value)
    .replace(/\b(phuong|p|xa|x|thi tran|tt|quan|q|huyen|h|thi xa|tx|thanh pho|tp|tinh)\b/g, " ")
    .replace(/\s+/g, " ")
    .trim();
}

function findBestMatchByName(options, ...rawNames) {
  const list = Array.isArray(options) ? options : [];
  const candidates = splitAddressCandidates(...rawNames)
    .map((part) => stripAdministrativePrefix(part))
    .filter(Boolean);

  if (!list.length || !candidates.length) return null;

  let best = null;
  let bestScore = -1;

  for (const item of list) {
    const name = stripAdministrativePrefix(item?.name);
    if (!name) continue;

    let score = 0;
    for (const target of candidates) {
      if (!target) continue;
      if (target === name) score = Math.max(score, 100);
      else if (target.includes(name)) score = Math.max(score, 80);
      else if (name.includes(target)) score = Math.max(score, 70);
      else {
        const targetTokens = target.split(" ").filter(Boolean);
        const nameTokens = name.split(" ").filter(Boolean);
        const overlap = targetTokens.filter((token) => nameTokens.includes(token)).length;
        if (overlap > 0) {
          const tokenScore = overlap * 10;
          score = Math.max(score, tokenScore);
        }
      }
    }

    if (score > bestScore) {
      bestScore = score;
      best = item;
    }
  }

  return bestScore >= 20 ? best : null;
}

async function safeParseResponse(res) {
  if (!res) return null;
  try {
    const data = await res.json();
    return data;
  } catch (err) {
    try {
      const text = await res.text();
      return text;
    } catch {
      return null;
    }
  }
}

async function autoFillAddressHierarchyFromText(rawAddress) {
  const input = String(rawAddress || "").trim();
  if (!input || !provinces.value.length) return;

  isAutoFillingAddress.value = true;
  skipNextWatcherUpdate.value = true;

  try {
    const geoRes = await fetch(
      `/nominatim/search?format=json&addressdetails=1&limit=1&q=${encodeURIComponent(input + ", Việt Nam")}`,
    );
    if (!geoRes.ok) {
      if (geoRes.status === 429) {
        throw new Error('Geocoding service rate-limited (429). Please try again later.');
      }
      const txt = await safeParseResponse(geoRes);
      throw new Error(typeof txt === 'string' ? txt : `Geocoding failed (HTTP ${geoRes.status})`);
    }
    const geoData = await safeParseResponse(geoRes);
    const first = Array.isArray(geoData) ? geoData[0] : null;
    
    // Extract from Nominatim ONLY for basic hints
    const addressMeta = first?.address || {};
    const provinceHint =
      addressMeta.state ||
      addressMeta.city ||
      addressMeta.province ||
      addressMeta.region ||
      "";
    const districtHint =
      addressMeta.county ||
      addressMeta.city_district ||
      addressMeta.district ||
      addressMeta.suburb ||
      "";
    const wardHint =
      addressMeta.ward ||
      addressMeta.suburb ||
      addressMeta.quarter ||
      addressMeta.neighbourhood ||
      "";
    const displayNameHint = first?.display_name || "";

    // STEP 1: Match Province - try hint first, then fallback to input search
    let matchedProvince =
      findBestMatchByName(provinces.value, provinceHint, displayNameHint) ||
      findBestMatchByName(provinces.value, input, displayNameHint);
      
    if (!matchedProvince) {
      skipNextWatcherUpdate.value = false;
      scheduleAutoRouteCalculation();
      return;
    }

    selectedProvince.value = matchedProvince;
    districts.value = matchedProvince.districts || [];

    // STEP 2: Match District - try hint first, then search whole input text
    let matchedDistrict =
      findBestMatchByName(districts.value, districtHint, displayNameHint) ||
      findBestMatchByName(districts.value, input, displayNameHint);

    if (matchedDistrict) {
      selectedDistrict.value = matchedDistrict;
      wards.value = matchedDistrict.wards || [];

      // STEP 3: Match Ward - try hint first, then search whole input
      const matchedWard =
        findBestMatchByName(wards.value, wardHint, displayNameHint) ||
        findBestMatchByName(wards.value, input, displayNameHint);
      
      if (matchedWard) {
        selectedWard.value = matchedWard;
      } else {
        // No ward match - keep district but leave ward empty
        selectedWard.value = "";
      }
    } else {
      // District hint didn't work - try searching inside each district's wards for the hint
      let foundDistrictAndWard = false;
      
      for (const dist of districts.value) {
        const districtWards = dist.wards || [];
        
        // Try to find ward using ward hint
        const wardByHint = findBestMatchByName(
          districtWards,
          wardHint,
          displayNameHint,
          input,
        );
        if (wardByHint) {
          selectedDistrict.value = dist;
          selectedWard.value = wardByHint;
          wards.value = districtWards;
          foundDistrictAndWard = true;
          break;
        }
      }

      // If still no match, try searching entire input text across all districts/wards
      if (!foundDistrictAndWard) {
        for (const dist of districts.value) {
          const districtWards = dist.wards || [];
          
          // Try to find by input text
          const wardByInput = findBestMatchByName(
            districtWards,
            input,
            displayNameHint,
          );
          if (wardByInput) {
            selectedDistrict.value = dist;
            selectedWard.value = wardByInput;
            wards.value = districtWards;
            foundDistrictAndWard = true;
            break;
          }
        }
      }

      // If still no exact match found, just leave ward empty but keep district set
      if (!foundDistrictAndWard) {
        selectedWard.value = "";
        // Set wards array to empty so user can manually select
        wards.value = [];
      }
    }

    fullAddressString.value = buildCurrentAddressString(input);
  } catch (error) {
    console.error("Auto-fill error:", error);
  } finally {
    isAutoFillingAddress.value = false;
    // Reset flag AFTER this microtask completes so watcher definitely skips
    Promise.resolve().then(() => {
      skipNextWatcherUpdate.value = false;
    });
  }
}

async function calculateRouteFromFreeformAddress(rawAddress, silent = false) {
  const addressText = String(rawAddress || "").trim();
  if (!addressText) {
    if (!silent) {
      routeError.value = "Vui lòng nhập địa chỉ giao hàng để tính phí ship.";
    }
    return false;
  }

  // Phí giao hàng là mức cố định, không gọi geocoding/định tuyến để tính khoảng cách.
  applyTextFallbackShipping(addressText);
  return true;
}

async function autoCalculateShippingOnEnter() {
  const rawAddress = String(streetAddress.value || shippingForm.address || "").trim();
  if (!rawAddress) {
    return;
  }

  if (!streetAddress.value) {
    streetAddress.value = rawAddress;
  }

  const ok = await checkAndCalculateRoute(true);
  if (!ok) {
    applyTextFallbackShipping(rawAddress);
  }
}

onMounted(async () => {
  window.scrollTo(0, 0);

  await hydrateCheckout();

  try {
    provinces.value = await loadProvinces();
    await loadAndApplyDefaultAddress();
    await autoCalculateShippingOnEnter();
  } catch (error) {
    console.error("Không tải được API Tỉnh Thành", error);
    await autoCalculateShippingOnEnter();
  }
});

async function loadAndApplyDefaultAddress() {
  if (!currentAccountId.value) return;

  try {
    const list = await addressesApi.getAll(currentAccountId.value);
    const addresses = Array.isArray(list) ? list : [];
    const defaultAddress = pickDefaultAddress(addresses);
    if (!defaultAddress) return;

    selectedAddressId.value = normalizeAddressId(defaultAddress.id);
    applySelectedAddress(defaultAddress);
  } catch (error) {
    console.error("Không tải được địa chỉ mặc định:", error);
  }
}

function handleAddressesReady(defaultAddress) {
  if (!defaultAddress || selectedAddressId.value) return;
  selectedAddressId.value = normalizeAddressId(defaultAddress.id);
  applySelectedAddress(defaultAddress);
}

async function checkAndCalculateRoute(silent = false) {
  if (!streetAddress.value && shippingForm.address) {
    streetAddress.value = String(shippingForm.address).trim();
  }

  const hasFullAddressHierarchy =
    selectedProvince.value && selectedDistrict.value && selectedWard.value;

  if (streetAddress.value && !hasFullAddressHierarchy) {
    await autoFillAddressHierarchyFromText(streetAddress.value);
  }

  if (!streetAddress.value) {
    if (!silent) {
      routeError.value = "Vui lòng nhập địa chỉ giao hàng để tính phí ship.";
    }
    isRouteCalculated.value = false;
    return false;
  }

  const calculatedFromFreeform = await calculateRouteFromFreeformAddress(
    streetAddress.value,
    silent,
  );
  return calculatedFromFreeform;
}

function applyFallbackShipping() {
  const addressForFee = buildCurrentAddressString();
  const detectedProvince = selectedProvince.value?.name || "";
  const isHaNoi =
    normalizeAddressText(addressForFee).includes("ha noi") ||
    normalizeAddressText(detectedProvince).includes("ha noi");
  estimatedDays.value = isHaNoi ? 1 : 3;
  calculatedShippingFee.value = STANDARD_SHIPPING_FEE;
  fullAddressString.value = addressForFee;
  routeError.value = "";
  isRouteCalculated.value = true;
}
// ===== END LOGIC ROUTING =====

// ===== LOGIC THANH TOÁN VÀ VOUCHER =====
const selectedPayment = ref(1);
const paymentMethods = [
  { id: 1, name: "Thanh toán khi nhận hàng", icon: "local_shipping" },
  { id: 2, name: "VNPay QR / Ví điện tử", icon: "qr_code_2" },
];
const selectedDigitalPayment = ref(2);
const digitalPaymentMethods = [
  { id: 2, name: "VNPay" },
  { id: 3, name: "Momo" },
  { id: 4, name: "Bank Transfer" },
  { id: 5, name: "ZaloPay" },
];
const effectivePaymentMethodId = computed(() =>
  selectedPayment.value === 1 ? 1 : Number(selectedDigitalPayment.value || 2),
);

const voucherInput = ref("");
const appliedVoucher = ref(null);
const availableVouchers = ref([]);

function getVoucherMinOrder(voucher) {
  return Number(voucher?.minOrderValue ?? voucher?.min_order_value ?? 0) || 0;
}

function getVoucherByCode(code) {
  const normalizedCode = String(code || "").trim().toUpperCase();
  if (!normalizedCode) return null;

  return (
    (availableVouchers.value || []).find(
      (voucher) => String(voucher?.code || "").trim().toUpperCase() === normalizedCode,
    ) || null
  );
}

function enforceVoucherMinOrder(voucher, orderBaseAmount) {
  const fromList = getVoucherByCode(voucher?.code);
  const minOrder = Math.max(
    getVoucherMinOrder(voucher),
    getVoucherMinOrder(fromList),
  );

  if (minOrder > 0 && Number(orderBaseAmount) < minOrder) {
    throw new Error(
      `Đơn tối thiểu ${formatMoney(minOrder)} mới áp dụng được mã ${voucher?.code || ""}.`,
    );
  }
}

function isVoucherActive(voucher) {
  // Prefer explicit status when provided by backend
  const statusRaw = String(voucher?.status || voucher?.state || "").trim();
  if (statusRaw) {
    const st = String(statusRaw).toUpperCase();
    // Treat these as active states
    if (['ACTIVE', 'ENABLED', 'AVAILABLE', 'APPROVED'].includes(st)) {
      // continue to further checks (dates/usages)
    } else {
      return false;
    }
  } else {
    if (!(voucher?.isActive || voucher?.is_active)) return false;
  }

  const now = new Date();
  const endDateRaw = voucher?.endDate || voucher?.end_date;
  if (endDateRaw) {
    const endDate = new Date(endDateRaw);
    if (!Number.isNaN(endDate.getTime()) && endDate.getTime() < now.getTime()) {
      return false;
    }
  }

  const usageLimit = Number(voucher?.usageLimit ?? voucher?.usage_limit ?? 0) || 0;
  const usedCount = Number(voucher?.usedCount ?? voucher?.used_count ?? 0) || 0;
  if (usageLimit > 0 && usedCount >= usageLimit) {
    return false;
  }

  return true;
}

const processedAvailableVouchers = computed(() => {
  const orderBase = Math.max(0, voucherBaseAmount.value);

  return (availableVouchers.value || [])
    .filter((voucher) => isVoucherActive(voucher))
    .map((voucher) => {
      const minOrder = getVoucherMinOrder(voucher);
      const isEligible = orderBase >= minOrder;
      const expectedDiscount = isEligible
        ? computeVoucherDiscount(voucher, orderBase)
        : 0;
      return {
        ...voucher,
        isEligible,
        expectedDiscount,
      };
    })
    .sort((a, b) => (b.expectedDiscount || 0) - (a.expectedDiscount || 0));
});

const cartItems = computed(() => cartStore.cartItems);
const subtotal = computed(() => cartStore.cartTotal);
const shipping = computed(() =>
  cartItems.value.length > 0 ? STANDARD_SHIPPING_FEE : 0,
);

const voucherBaseAmount = computed(() =>
  Math.max(0, subtotal.value - promotionDiscountAmount.value),
);

function getPromotionPercentForItem(item) {
  const productId = Number(item?.productId || item?.product_id);
  if (!Number.isFinite(productId)) return 0;
  return activePromotions.value.reduce((best, promotion) => {
    const ids = promotion.productIds ||
      promotion.products?.map((product) => product.id) ||
      [];
    if (!ids.some((id) => Number(id) === productId)) return best;
    return Math.max(best, Number(promotion.discountPercent || 0));
  }, 0);
}

const promotionDiscountAmount = computed(() =>
  cartItems.value.reduce((totalAmount, item) => {
    const percent = getPromotionPercentForItem(item);
    return totalAmount + (Number(item.price || 0) * Number(item.quantity || 0) * percent) / 100;
  }, 0),
);

const discountAmount = computed(() => {
  if (!appliedVoucher.value) return 0;

  const currentCode = appliedVoucher.value.code || voucherInput.value;
  const found = availableVouchers.value.find(
    (v) => v.code.toUpperCase() === String(currentCode).toUpperCase(),
  );

  const voucherSource = found
    ? { ...appliedVoucher.value, ...found }
    : appliedVoucher.value;

  return computeVoucherDiscount(voucherSource, voucherBaseAmount.value);
});

const orderTotalBeforeDiscount = computed(() =>
  Math.max(0, subtotal.value + shipping.value),
);
const total = computed(() =>
  Math.max(
    0,
    orderTotalBeforeDiscount.value -
      discountAmount.value -
      promotionDiscountAmount.value,
  ),
);

async function loadActivePromotions() {
  try {
    const response = await fetch("http://localhost:8080/api/promotions?page=0&size=200");
    const payload = await response.json();
    const rows = Array.isArray(payload?.data)
      ? payload.data
      : Array.isArray(payload) ? payload : [];
    activePromotions.value = rows.filter((promotion) => promotion.status === "ACTIVE");
  } catch {
    activePromotions.value = [];
  }
}

function showAlert(title, message, type = "success") {
  modal.title = title;
  modal.message = message;
  modal.type = type;
  modal.show = true;
}

function formatMoney(value) {
  return orderUtils.formatPrice(value || 0);
}
function getItemName(item) {
  return item.productName || item.name || "Sản phẩm";
}
function getItemImage(item) {
  const raw =
    item?.imageUrl ||
    item?.image_url ||
    item?.image ||
    item?.variant?.imageUrl ||
    item?.product?.imageUrl ||
    "";
  return resolveImageUrl(raw) || "https://via.placeholder.com/150";
}
function getSizeLabel(item) {
  return (
    resolveSizeMeta({ id: item.sizeId, name: item.sizeName || item.size })
      .name || "--"
  );
}
function getColorLabel(item) {
  return (
    resolveColorMeta({
      id: item.colorId,
      name: item.colorName || item.color,
      hexCode: item.colorHexCode,
    }).name || "--"
  );
}

function getAccountId(user) {
  const source = user && typeof user === "object" ? user : {};
  const candidate = source.accountId ?? source.account_id ?? source.id;
  const n = Number(candidate);
  return Number.isFinite(n) && n > 0 ? n : null;
}

function redirectToLoginForCheckout() {
  router.replace({ name: "login", query: { redirect: "/checkout" } });
}

// =====================================
// ĐÂY LÀ CHỖ ĐỂ TÍCH HỢP PAYLOAD ĐẶT HÀNG!
// =====================================
async function verifyOtpAndPlaceOrder() {
  otpModal.loading = true;
  otpModal.error = "";
  try {
    const res = await fetch("http://localhost:8080/api/orders/verify-otp", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email: shippingForm.gmail, otp: otpModal.code })
    });
    const data = await res.json();
    if (!res.ok || !data.success) {
      throw new Error(data.message || "Mã xác nhận không đúng");
    }
    otpModal.verified = true;
    otpModal.show = false;
    handlePlaceOrder();
  } catch (err) {
    otpModal.error = err.message;
  } finally {
    otpModal.loading = false;
  }
}

async function handlePlaceOrder() {
  if (isProcessing.value) return;

  if (!isRouteCalculated.value) {
    const autoCalculated = await checkAndCalculateRoute(true);
    if (autoCalculated && isRouteCalculated.value) {
      // Continue placing order below.
    } else {
      showAlert(
        "Thiếu thông tin",
        'Vui lòng nhập địa chỉ giao hàng đầy đủ để hệ thống tự tính phí ship.',
        "error",
      );
      return;
    }
  }

  const normalizedName = String(shippingForm.fullname || "").trim();
  const normalizedPhone = normalizePhone(shippingForm.phone);
  if (normalizedName.length < 2) {
    showAlert(
      "Thiếu thông tin",
      "Vui lòng nhập họ tên hợp lệ (ít nhất 2 ký tự).",
      "error",
    );
    return;
  }
  if (!/^\d{7,15}$/.test(normalizedPhone)) {
    showAlert(
      "Thiếu thông tin",
      "Số điện thoại chỉ gồm chữ số và dài từ 7 đến 15 ký tự.",
      "error",
    );
    return;
  }

  // Validate email khi COD - kiểm tra trước khi làm bất cứ điều gì khác
  if (effectivePaymentMethodId.value === 1 && !otpModal.verified) {
    if (!shippingForm.gmail || !shippingForm.gmail.includes('@')) {
      showAlert("Thiếu email", "Vui lòng nhập Email hợp lệ để nhận mã xác nhận khi thanh toán COD.", "error");
      return;
    }
  }

  if (appliedVoucher.value?.code) {
    try {
      const validatedVoucher = await voucherAPI.applyVoucher(
        appliedVoucher.value.code,
        voucherBaseAmount.value,
        currentAccountId.value,
      );
      enforceVoucherMinOrder(validatedVoucher, voucherBaseAmount.value);
      appliedVoucher.value = validatedVoucher;
      voucherInput.value = validatedVoucher.code || appliedVoucher.value.code;
    } catch (voucherError) {
      showAlert(
        "Voucher không hợp lệ",
        voucherError?.message || "Mã giảm giá không còn đủ điều kiện áp dụng.",
        "error",
      );
      clearVoucher();
      return;
    }
  }

  if (effectivePaymentMethodId.value === 1 && !otpModal.verified) {
    isProcessing.value = true;
    try {
      const res = await fetch("http://localhost:8080/api/orders/send-otp", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: shippingForm.gmail })
      });
      if (!res.ok) throw new Error("Không thể gửi mã OTP");
      otpModal.code = "";
      otpModal.error = "";
      otpModal.show = true;
    } catch (err) {
      showAlert("Lỗi", "Không thể gửi mã xác nhận. " + err.message, "error");
    } finally {
      isProcessing.value = false;
    }
    return;
  }

  isProcessing.value = true;

  try {
    const finalShippingAddress = fullAddressString.value || buildCurrentAddressString();
    const effectiveShippingFee = STANDARD_SHIPPING_FEE;
    const effectiveTotalBeforeDiscount = Math.max(
      0,
      Number(subtotal.value || 0) + effectiveShippingFee,
    );
    const effectiveFinalAmount = Math.max(
      0,
      effectiveTotalBeforeDiscount -
        Number(discountAmount.value || 0) -
        Number(promotionDiscountAmount.value || 0),
    );

    const orderData = {
      accountId: currentAccountId.value,
      fullname: normalizedName,
      gmail: shippingForm.gmail || null,
      email: shippingForm.gmail || null,
      phone: normalizedPhone,
      shippingAddress: finalShippingAddress,
      streetAddress: streetAddress.value || null,
      wardName: selectedWard.value?.name || null,
      districtName: selectedDistrict.value?.name || null,
      provinceName: selectedProvince.value?.name || null,
      paymentMethodId: effectivePaymentMethodId.value,
      voucherId: appliedVoucher.value?.id || null,
      voucherCode: appliedVoucher.value?.code || null,
      subtotalAmount: subtotal.value,
      subtotal: subtotal.value,
      subtotal_amount: subtotal.value,
      shippingFee: effectiveShippingFee,
      shipping_fee: effectiveShippingFee,
      shipping: effectiveShippingFee,
      shippingAmount: effectiveShippingFee,
      shipping_amount: effectiveShippingFee,
      deliveryDate: deliveryDate.value || null,
      deliveryFee: effectiveShippingFee,
      delivery_fee: effectiveShippingFee,
      shipFee: effectiveShippingFee,
      ship_fee: effectiveShippingFee,
      feeShip: effectiveShippingFee,
      fee_ship: effectiveShippingFee,
      totalMoney: effectiveTotalBeforeDiscount,
      total_money: effectiveTotalBeforeDiscount,
      totalAmount: effectiveTotalBeforeDiscount,
      total_amount: effectiveTotalBeforeDiscount,
      discountAmount: discountAmount.value,
      discount_amount: discountAmount.value,
      promotionDiscountAmount: promotionDiscountAmount.value,
      promotion_discount_amount: promotionDiscountAmount.value,
      totalDiscountAmount:
        Number(discountAmount.value || 0) +
        Number(promotionDiscountAmount.value || 0),
      finalAmount: effectiveFinalAmount,
      final_amount: effectiveFinalAmount,

      // >>> DANH SÁCH GIÀY GỬI KÈM MỚI Ở ĐÂY NÈ! <<<
      items: cartItems.value.map((item) => ({
        variantId:
          item.variantId || item.id || (item.variant && item.variant.id),
        quantity: item.quantity,
      })),
    };

    const result = await orderAPI.placeOrder(orderData);

    // ── VNPay: nếu chọn thanh toán online → redirect sang VNPay ──
    if (selectedPayment.value === 2) {
      try {
        const createdOrderId =
          result?.orderId ||
          result?.id ||
          result?.data?.orderId ||
          result?.data?.id;

        if (!createdOrderId) {
          throw new Error("Không lấy được mã đơn hàng để thanh toán VNPay.");
        }

        const vnpayResult = await paymentApi.createVnpayPayment(
          createdOrderId,
          effectiveFinalAmount,
        );

        if (vnpayResult?.paymentUrl) {
          // Redirect user to VNPay payment page
          window.location.href = vnpayResult.paymentUrl;
          return; // stop here — user will be redirected
        } else {
          throw new Error("Không tạo được link thanh toán VNPay.");
        }
      } catch (vnpayError) {
        showAlert(
          "Lỗi thanh toán VNPay",
          vnpayError?.message || "Không thể kết nối cổng thanh toán. Đơn hàng đã được tạo, vui lòng thử thanh toán lại.",
          "error",
        );
        isProcessing.value = false;
        return;
      }
    }
    // ── End VNPay ──

    if (currentAccountId.value !== null) await cartStore.clearCartBackend();
    else cartStore.clearCart();

    showAlert(
      "Đặt hàng thành công",
      "Đơn hàng của bạn đã được ghi nhận.",
      "success",
    );
    setTimeout(() => {
      router.push("/orders");
    }, 1000);
  } catch (error) {
    showAlert(
      "Không thể đặt hàng",
      error.message || "Vui lòng thử lại sau",
      "error",
    );
  } finally {
    isProcessing.value = false;
  }
}

async function loadAvailableVouchers() {
  try {
    // Get current user info for filtering used vouchers
    let accountId = null;
    let phone = null;
    try {
      const userStr = getSession("user") || sessionStorage.getItem("user");
      if (userStr) {
        const user = JSON.parse(userStr);
        accountId = user.id ?? user.accountId ?? null;
        phone = user.phone ?? user.sdt ?? null;
      }
    } catch {}

    const response = await vouchersApi.getActiveVouchers({ accountId, phone });

    const rows = response.data || response.content || response || [];
    const estimateDiscount = (voucher) =>
      computeVoucherDiscount(voucher, voucherBaseAmount.value);

    availableVouchers.value = rows
      .filter((v) => isVoucherActive(v) && !v.usedByCurrentUser)
      .sort((a, b) => estimateDiscount(b) - estimateDiscount(a))
      .slice(0, 20);
  } catch (e) {
    availableVouchers.value = [];
  }
}

async function applyVoucher() {
  const code = String(voucherInput.value || "").trim();
  if (!code) return;

  isApplyingVoucher.value = true;
  try {
    const validated = await voucherAPI.applyVoucher(
      code,
      voucherBaseAmount.value,
      currentAccountId.value,
    );
    enforceVoucherMinOrder(validated, voucherBaseAmount.value);
    appliedVoucher.value = validated;
    voucherInput.value = validated.code || code;
  } catch (error) {
    showAlert(
      "Voucher không hợp lệ",
      error?.message || "Mã giảm giá không thể áp dụng cho đơn hiện tại.",
      "error",
    );
    clearVoucher();
  } finally {
    isApplyingVoucher.value = false;
  }
}

function applySuggestedVoucher(v) {
  if (!v?.isEligible) {
    showAlert(
      "Chưa đủ điều kiện",
      `Đơn cần tối thiểu ${formatMoney(v.minOrderValue || v.min_order_value || 0)} để dùng mã này.`,
      "error",
    );
    return;
  }
  voucherInput.value = v.code;
  appliedVoucher.value = v;
}

function clearVoucher() {
  appliedVoucher.value = null;
  voucherInput.value = "";
}

function autoPickBestEligibleVoucher() {
  const bestVoucher = processedAvailableVouchers.value.find(
    (voucher) => voucher.isEligible,
  );

  if (!bestVoucher) {
    return;
  }

  const currentCode = String(appliedVoucher.value?.code || "").toUpperCase();
  const bestCode = String(bestVoucher.code || "").toUpperCase();
  if (currentCode && currentCode === bestCode) {
    return;
  }

  voucherInput.value = bestVoucher.code || "";
  appliedVoucher.value = bestVoucher;
}

function applySelectedAddress(address) {
  if (!address) return;

  pendingSelectedAddress.value = address;
  selectedAddressId.value = normalizeAddressId(address.id);

  if (address.fullname) {
    shippingForm.fullname = address.fullname;
  }
  if (address.phone) {
    shippingForm.phone = address.phone;
  }

  shippingForm.address = address.fullAddress || buildCurrentAddressString();
  fullAddressString.value = shippingForm.address;

  fillAddressHierarchyFromSelection(address);
  void checkAndCalculateRoute(true);
}

function fillAddressHierarchyFromSelection(address) {
  if (!address) return;

  pendingSelectedAddress.value = address;

  if (!provinces.value.length) {
    streetAddress.value = address.streetAddress || address.fullAddress || "";
    return;
  }

  skipNextWatcherUpdate.value = true;

  const hierarchy = resolveAddressHierarchy(address, provinces.value);
  selectedProvince.value = hierarchy.selectedProvince;
  selectedDistrict.value = hierarchy.selectedDistrict;
  selectedWard.value = hierarchy.selectedWard;
  streetAddress.value = hierarchy.streetAddress;
  shippingForm.address = address.fullAddress || buildCurrentAddressString();
  fullAddressString.value = shippingForm.address;

  Promise.resolve().then(() => {
    skipNextWatcherUpdate.value = false;
  });
}

async function hydrateCheckout() {
  const userData = getSession("user");
  if (userData) {
    try {
      currentUser.value = JSON.parse(userData);
    } catch {
      currentUser.value = null;
    }
  }
  currentAccountId.value = getAccountId(currentUser.value);
  if (currentAccountId.value === null) {
    showAlert("Yêu cầu đăng nhập", "Bạn cần đăng nhập để thanh toán", "error");
    setTimeout(() => redirectToLoginForCheckout(), 250);
    return;
  }
  cartStore.accountId = currentAccountId.value;
  await cartStore.loadCartFromBackend(currentAccountId.value);
  await loadActivePromotions();
  await loadAvailableVouchers();
  autoPickBestEligibleVoucher();

  shippingForm.fullname =
    currentUser.value?.fullName || currentUser.value?.username || "";
  shippingForm.gmail =
    currentUser.value?.email || currentUser.value?.gmail || "";
  shippingForm.phone = currentUser.value?.phone || "";
}

watch(
  () => provinces.value.length,
  (length) => {
    if (length > 0 && pendingSelectedAddress.value) {
      fillAddressHierarchyFromSelection(pendingSelectedAddress.value);
      void checkAndCalculateRoute(true);
    }
  },
);

watch(
  () => streetAddress.value,
  () => {
    fullAddressString.value = buildCurrentAddressString();
    scheduleAutoRouteCalculation();
  },
);

watch(
  () => selectedWard.value,
  () => {
    if (skipNextWatcherUpdate.value) return;
    
    // Only recalculate if we have at least province + district
    if (!selectedProvince.value || !selectedDistrict.value) {
      isRouteCalculated.value = false;
      calculatedShippingFee.value = 0;
      return;
    }
    
    // Update address string and trigger route calculation
    fullAddressString.value = buildCurrentAddressString();
    scheduleAutoRouteCalculation();
  },
);

watch(
  [() => voucherBaseAmount.value, processedAvailableVouchers],
  () => {
    if (!appliedVoucher.value?.code) {
      autoPickBestEligibleVoucher();
      return;
    }

    const currentCode = String(appliedVoucher.value.code || "").toUpperCase();
    const matched = processedAvailableVouchers.value.find(
      (voucher) => String(voucher.code || "").toUpperCase() === currentCode,
    );

    if (matched && matched.isEligible) {
      appliedVoucher.value = matched;
      return;
    }

    clearVoucher();
    autoPickBestEligibleVoucher();
  },
);
</script>

<style scoped>
@keyframes fade-scale-in {
  from {
    opacity: 0;
    transform: scale(0.94);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
.fade-scale-enter-active {
  animation: fade-scale-in 0.18s ease-out;
}
.fade-scale-leave-active {
  animation: fade-scale-in 0.16s reverse ease-in;
}
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #94a3b8;
  border-radius: 9999px;
}
:deep(label:has(> .hidden)) {
  display: none;
}
</style>
