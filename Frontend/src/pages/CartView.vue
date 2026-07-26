<template>
  <div
    class="retail-shell flex min-h-screen flex-col text-slate-900 dark:text-white font-display antialiased selection:bg-primary selection:text-slate-900 dark:selection:text-white"
  >
    <MainTopBar />

    <main
      class="flex-grow max-w-7xl w-full mx-auto px-4 pt-6 pb-8 lg:pl-12 lg:pr-8"
    >
      <div class="flex flex-col lg:flex-row gap-12">
        <div class="flex-1 flex flex-col gap-6">
          <div class="pb-4 border-b border-slate-200 dark:border-slate-800">
            <h1 class="text-3xl font-black tracking-tight mb-2">
              Giỏ hàng của bạn
            </h1>
            <p class="text-slate-500 dark:text-[#9cabba]">
              {{ cartCount }} sản phẩm trong giỏ
            </p>
          </div>
          <div class="flex flex-col gap-4">
            <article
              v-for="item in cartItems"
              :key="item.id"
              class="group grid min-h-[196px] grid-cols-1 gap-5 rounded-xl border border-slate-200 bg-white p-5 shadow-sm transition-colors hover:border-primary/50 dark:border-[#283039] dark:bg-[#18212b] sm:h-[196px] sm:grid-cols-[120px_minmax(0,1fr)]"
            >
              <div class="flex items-center justify-center">
                <div
                  class="size-[120px] rounded-lg bg-slate-100 bg-contain bg-center bg-no-repeat dark:bg-slate-800"
                  data-alt="Product image"
                  :style="{
                    backgroundImage: item.imageUrl
                      ? `url('${item.imageUrl}')`
                      : undefined,
                  }"
                />
              </div>
              <div class="grid min-w-0 grid-rows-[1fr_40px] gap-2">
                <div class="grid min-h-0 grid-cols-1 items-start gap-4 sm:grid-cols-[minmax(0,1fr)_180px]">
                  <div class="min-w-0">
                    <h3
                      class="line-clamp-2 text-lg font-bold text-slate-900 dark:text-white"
                    >
                      {{ item.productName }}
                    </h3>
                    <p class="text-sm text-slate-500 dark:text-[#9cabba] mt-1">
                      Mã SP: {{ item.productCode || "--" }}
                    </p>
                    <div class="flex gap-3 mt-2">
                      <span
                        class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-slate-100 dark:bg-[#283039] text-slate-800 dark:text-slate-200"
                        >Size: {{ getSizeLabel(item) }}</span
                      >
                      <span
                        class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-slate-100 dark:bg-[#283039] text-slate-800 dark:text-slate-200"
                        >Màu: {{ getColorLabel(item) }}</span
                      >
                    </div>
                  </div>
                  <div class="min-w-0 text-left sm:text-right">
                    <template v-if="getPromotionPercentForItem(item) > 0">
                      <p class="text-sm font-semibold text-slate-400 line-through">
                        {{ formatMoney(item.price * item.quantity) }}
                      </p>
                      <p class="text-lg font-bold text-red-600 dark:text-red-400">
                        {{ formatMoney(getDiscountedItemTotal(item)) }}
                      </p>
                    </template>
                    <p v-else class="text-lg font-bold text-slate-900 dark:text-white">
                      {{ formatMoney(item.price * item.quantity) }}
                    </p>
                    <p v-if="getPromotionPercentForItem(item) > 0" class="text-xs font-semibold text-red-500">
                      -{{ getPromotionPercentForItem(item) }}% khuyến mãi
                    </p>
                    <p
                      v-if="item.quantity > 1"
                      class="text-xs text-slate-400 mt-1"
                    >
                      {{ formatMoney(item.price) }} / đôi
                    </p>
                  </div>
                </div>
                <div class="flex h-10 items-center justify-between gap-4">
                  <button
                    class="text-sm font-medium text-red-500 hover:text-red-600 flex items-center gap-1"
                    type="button"
                    @click="removeItem(item)"
                  >
                    <span class="material-symbols-outlined text-[18px]"
                      >delete</span
                    >
                    Xóa
                  </button>
                  <div
                    class="flex items-center bg-slate-100 dark:bg-[#283039] rounded-lg p-1"
                  >
                    <button
                      class="size-8 flex items-center justify-center rounded-md hover:bg-white dark:hover:bg-[#111418] text-slate-600 dark:text-white transition-colors disabled:opacity-60"
                      :disabled="item.quantity <= 1"
                      type="button"
                      @click="decrement(item)"
                    >
                      <span class="material-symbols-outlined text-[16px]"
                        >remove</span
                      >
                    </button>
                    <input
                      class="w-10 bg-transparent text-center text-sm font-medium text-slate-900 dark:text-white border-none focus:ring-0 p-0"
                      min="1"
                      type="number"
                      :value="item.quantity"
                      @change="(e) => setQuantity(item, e.target.value)"
                    />
                    <button
                      class="size-8 flex items-center justify-center rounded-md hover:bg-white dark:hover:bg-[#111418] text-slate-600 dark:text-white transition-colors"
                      type="button"
                      @click="increment(item)"
                    >
                      <span class="material-symbols-outlined text-[16px]"
                        >add</span
                      >
                    </button>
                  </div>
                </div>
              </div>
            </article>

            <div
              v-if="cartItems.length === 0"
              class="py-12 text-center text-slate-500 dark:text-[#9cabba]"
            >
              Giỏ hàng của bạn đang trống.
            </div>
          </div>
        </div>
        <div class="w-full lg:w-[360px] xl:w-[400px] shrink-0">
          <div class="sticky top-24 flex flex-col gap-6">
            <div
              class="bg-white dark:bg-[#18212b] rounded-xl border border-slate-200 dark:border-[#283039] shadow-sm p-6"
            >
              <h2 class="text-xl font-bold mb-6">Tóm tắt đơn hàng</h2>
              <div class="mb-6">
                <p
                  v-if="voucherMessage"
                  class="text-xs mt-2 font-medium"
                  :class="isVoucherError ? 'text-red-500' : 'text-green-500'"
                >
                  {{ voucherMessage }}
                </p>
              </div>

              <div
                class="space-y-3 pb-6 border-b border-slate-200 dark:border-[#283039]"
              >
                <div
                  class="flex justify-between text-slate-600 dark:text-[#9cabba]"
                >
                  <span>Tạm tính</span>
                  <span class="font-medium text-slate-900 dark:text-white">{{
                    formatMoney(subtotal)
                  }}</span>
                </div>

                <div
                  class="flex justify-between text-slate-600 dark:text-[#9cabba]"
                >
                  <span>Phí vận chuyển</span>
                  <span class="font-medium text-slate-900 dark:text-white">{{
                    formatMoney(shipping)
                  }}</span>
                </div>

                <div
                  v-if="discountAmount > 0"
                  class="flex justify-between text-green-500 animate-pulse"
                >
                  <span>Giảm giá</span>
                  <span class="font-bold"
                    >-{{ formatMoney(discountAmount) }}</span
                  >
                </div>
                <div
                  v-if="promotionDiscountAmount > 0"
                  class="flex justify-between text-red-500"
                >
                  <span>Khuyến mãi sản phẩm</span>
                  <span class="font-bold">-{{ formatMoney(promotionDiscountAmount) }}</span>
                </div>
              </div>
              <div class="flex justify-between items-center py-6">
                <span class="text-lg font-bold text-slate-900 dark:text-white"
                  >Tổng thanh toán</span
                >
                <span
                  class="text-2xl font-black text-slate-900 dark:text-white"
                  >{{ formatMoney(total) }}</span
                >
              </div>
              <router-link
                class="w-full bg-primary hover:bg-primary-hover text-white font-bold py-3.5 px-4 rounded-xl shadow-lg shadow-zinc-900/10 transition-all transform active:scale-[0.98] flex justify-center items-center gap-2"
                :to="checkoutRoute"
              >
                <span class="material-symbols-outlined text-[20px]">lock</span>
                {{ checkoutButtonLabel }}
              </router-link>

              <div class="mt-4 text-center">
                <p class="text-xs text-slate-400">
                  Thanh toán an toàn, thông tin được bảo mật
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, watch } from "vue";
import { useCartStore } from "@/stores/cart";
import { voucherAPI, orderUtils } from "@/services/orders";
import { resolveColorMeta, resolveSizeMeta } from "@/utils/variantValues";
import { normalizeAppliedVoucher } from "@/utils/voucherValues";
import { STANDARD_SHIPPING_FEE } from "@/utils/orderConstants";

const cartStore = useCartStore();
const currentUser = ref(null);
const currentAccountId = ref(null);
const isLoading = ref(false);
const activePromotions = ref([]);

function getAccountId(user) {
  const source = user && typeof user === "object" ? user : {};
  const candidate =
    source.accountId !== undefined && source.accountId !== null
      ? source.accountId
      : source.account_id !== undefined && source.account_id !== null
        ? source.account_id
        : source.id;
  const n = Number(candidate);
  return Number.isFinite(n) && n > 0 ? n : null;
}

onMounted(async () => {
  const userData = localStorage.getItem("user");
  if (userData) {
    try {
      currentUser.value = JSON.parse(userData);
    } catch {
      currentUser.value = null;
    }

    currentAccountId.value = getAccountId(currentUser.value);
    if (currentAccountId.value !== null) {
      cartStore.accountId = currentAccountId.value;
      await cartStore.loadCartFromBackend(currentAccountId.value);
      await loadActivePromotions();
      return;
    }
  }

  currentAccountId.value = null;
  cartStore.accountId = null;
  cartStore.clearCart();
});

const voucherInput = ref("");
const voucherData = ref(null);
const voucherMessage = ref("");
const isVoucherError = ref(false);

const cartItems = computed(() => cartStore.cartItems);
const cartCount = computed(() => cartStore.cartCount);

const subtotal = computed(() => cartStore.cartTotal);
const promotionDiscountAmount = computed(() =>
  cartItems.value.reduce((totalAmount, item) => {
    const percent = getPromotionPercentForItem(item);
    return totalAmount + (Number(item.price || 0) * Number(item.quantity || 0) * percent) / 100;
  }, 0),
);
const shipping = computed(() =>
  cartItems.value.length > 0 ? STANDARD_SHIPPING_FEE : 0,
);
const discountAmount = computed(() => {
  return voucherData.value?.discountAmount || 0;
});
const total = computed(() =>
  Math.max(
    0,
    subtotal.value +
      shipping.value -
      discountAmount.value -
      promotionDiscountAmount.value,
  ),
);

function getPromotionPercentForItem(item) {
  const productId = Number(item?.productId || item?.product_id);
  if (!Number.isFinite(productId)) return 0;
  return activePromotions.value.reduce((best, promotion) => {
    const ids = promotion.productIds ||
      promotion.products?.map((product) => product.id) ||
      [];
    return ids.some((id) => Number(id) === productId)
      ? Math.max(best, Number(promotion.discountPercent || 0))
      : best;
  }, 0);
}

function getDiscountedItemTotal(item) {
  const percent = getPromotionPercentForItem(item);
  return Number(item.price || 0) * Number(item.quantity || 0) * (100 - percent) / 100;
}

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

const voucherBaseAmount = computed(() =>
  Math.max(0, subtotal.value - promotionDiscountAmount.value),
);

const checkoutRoute = computed(() => {
  if (currentAccountId.value === null) {
    return {
      name: "login",
      query: {
        redirect: "/checkout",
      },
    };
  }

  const code = voucherData.value?.code || "";
  if (!code) {
    return { name: "checkout" };
  }
  return {
    name: "checkout",
    query: {
      voucher: code,
    },
  };
});

const checkoutButtonLabel = computed(() => {
  return currentAccountId.value === null
    ? "Đăng nhập để thanh toán"
    : "Tiến hành thanh toán";
});

function getSizeLabel(item) {
  return resolveSizeMeta({ id: item.sizeId, name: item.sizeName }).name || "--";
}

function getColorLabel(item) {
  return (
    resolveColorMeta({
      id: item.colorId,
      name: item.colorName,
      hexCode: item.colorHexCode,
    }).name || "--"
  );
}

async function applyVoucherByCode(codeOverride = "", options = {}) {
  const code = String(codeOverride || voucherInput.value || "").trim();
  if (!code) {
    if (!options.silent) {
      voucherMessage.value = "Vui lòng nhập mã giảm giá";
      isVoucherError.value = true;
    }
    return;
  }

  try {
    isLoading.value = true;
    const response = await voucherAPI.applyVoucher(code, voucherBaseAmount.value);
    const normalized = normalizeAppliedVoucher(response, code);

    voucherData.value = normalized;
    voucherInput.value = normalized.code || code;

    if (!options.silent) {
      voucherMessage.value = `Đã áp dụng mã ${voucherInput.value} thành công!`;
    }
    isVoucherError.value = false;
  } catch (error) {
    voucherData.value = null;
    if (!options.silent) {
      voucherMessage.value = error.message || "Mã không hợp lệ";
      isVoucherError.value = true;
    }
  } finally {
    isLoading.value = false;
  }
}

async function applyVoucher() {
  await applyVoucherByCode(voucherInput.value, { silent: false });
}

watch([subtotal, promotionDiscountAmount], async ([next], [previous]) => {
  if (next === previous) return;

  const code = voucherData.value?.code;
  if (!code) return;

  if (next <= 0) {
    voucherData.value = null;
    voucherMessage.value = "";
    isVoucherError.value = false;
    return;
  }

  await applyVoucherByCode(code, { silent: true });
});

function formatMoney(n) {
  return orderUtils.formatPrice(n);
}

function shouldUseBackendItem(item) {
  return Boolean(currentAccountId.value && !item.isLocalOnly);
}

async function removeItem(item) {
  if (shouldUseBackendItem(item)) {
    await cartStore.removeFromCartBackend(item.cartItemId || item.id);
  } else {
    cartStore.removeFromCart(item.variantId);
  }
}

async function increment(item) {
  const newQty = item.quantity + 1;
  if (newQty > item.stockAvailable) {
    voucherMessage.value = "Số lượng vượt quá tồn kho";
    isVoucherError.value = true;
    return;
  }
  if (shouldUseBackendItem(item)) {
    await cartStore.updateCartItemBackend(item.cartItemId || item.id, newQty);
  } else {
    cartStore.updateCartItemQuantity(item.variantId, newQty);
  }
}

async function decrement(item) {
  const newQty = item.quantity - 1;
  if (newQty <= 0) {
    await removeItem(item);
    return;
  }
  if (shouldUseBackendItem(item)) {
    await cartStore.updateCartItemBackend(item.cartItemId || item.id, newQty);
  } else {
    cartStore.updateCartItemQuantity(item.variantId, newQty);
  }
}

async function setQuantity(item, raw) {
  const qty = Number.parseInt(raw, 10);
  if (!Number.isFinite(qty) || qty <= 0) return;
  if (qty > (item.stockAvailable || item.stockQuantity)) {
    voucherMessage.value = "Số lượng vượt quá tồn kho";
    isVoucherError.value = true;
    return;
  }
  if (shouldUseBackendItem(item)) {
    await cartStore.updateCartItemBackend(item.cartItemId || item.id, qty);
  } else {
    cartStore.updateCartItemQuantity(item.variantId, qty);
  }
}
</script>
