<template>
  <div
    class="retail-shell text-[#111418] dark:text-white font-display overflow-x-hidden antialiased"
  >
    <div class="relative flex h-auto min-h-screen w-full flex-col">
      <MainTopBar />

      <transition name="fade-scale">
        <div
          v-if="showAuthRequiredPopup"
          class="fixed inset-0 z-[120] flex items-center justify-center px-4 py-6"
        >
          <button
            type="button"
            class="absolute inset-0 bg-black/45 backdrop-blur-sm"
            aria-label="Đóng thông báo đăng nhập"
            @click="closeAuthRequiredPopup"
          />

          <article
            class="relative w-full max-w-md rounded-2xl border border-[#e5e7eb] bg-white p-6 shadow-2xl dark:border-[#283039] dark:bg-[#111418]"
          >
            <h3 class="text-xl font-bold text-[#111418] dark:text-white">
              Bạn cần đăng nhập để mua hàng
            </h3>
            <p
              class="mt-3 text-sm leading-6 text-[#4f5b69] dark:text-[#9cabba]"
            >
              Bạn phải có tài khoản để thêm sản phẩm vào giỏ và tiến hành thanh
              toán.
            </p>

            <div class="mt-6 flex flex-wrap gap-3">
              <button
                type="button"
                class="flex-1 rounded-lg border border-[#d1d5db] px-4 py-2.5 text-sm font-semibold text-[#111418] transition-colors hover:bg-[#f3f4f6] dark:border-[#3b4754] dark:text-white dark:hover:bg-[#283039]"
                @click="closeAuthRequiredPopup"
              >
                Hủy
              </button>
              <button
                type="button"
                class="flex-1 rounded-lg border border-primary px-4 py-2.5 text-sm font-semibold text-primary transition-colors hover:bg-primary/10"
                @click="goToRegisterFromPopup"
              >
                Đăng ký
              </button>
              <button
                type="button"
                class="flex-1 rounded-lg bg-primary px-4 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-primary-hover"
                @click="goToLoginFromPopup"
              >
                Đăng nhập
              </button>
            </div>
          </article>
        </div>
      </transition>

      <main class="layout-container flex h-full grow flex-col">
        <div class="px-4 md:px-10 lg:px-40 flex flex-1 justify-center py-5">
          <div
            class="layout-content-container flex flex-col max-w-[1200px] flex-1"
          >
            <div class="flex flex-wrap gap-2 p-4 mb-4">
              <router-link
                class="text-[#9cabba] text-sm font-medium leading-normal hover:underline"
                to="/"
                >Trang chủ</router-link
              >
              <span class="text-[#9cabba] text-sm font-medium leading-normal"
                >/</span
              >
              <router-link
                class="text-[#9cabba] text-sm font-medium leading-normal hover:underline"
                to="/shop"
                >Cửa hàng</router-link
              >
              <template v-if="categoryName && categoryName !== 'San pham'">
                <span class="text-[#9cabba] text-sm font-medium leading-normal"
                  >/</span
                >
                <span
                  class="text-[#9cabba] text-sm font-medium leading-normal"
                  >{{ categoryName }}</span
                >
              </template>
              <span class="text-[#9cabba] text-sm font-medium leading-normal"
                >/</span
              >
              <span
                class="text-[#111418] dark:text-white text-sm font-medium leading-normal"
                >{{ productName }}</span
              >
            </div>

            <div class="flex flex-col lg:flex-row gap-10 px-4 py-6">
              <div class="flex flex-col gap-4 flex-1 lg:max-w-[60%]">
                <div
                  class="group relative w-full overflow-hidden rounded-2xl border border-[#e5e7eb] bg-slate-100 aspect-[4/3] dark:border-[#283039] dark:bg-[#1b2127]"
                >
                  <img
                    :alt="productName"
                    class="h-full w-full object-contain p-3 object-center transition-transform duration-300 group-hover:scale-[1.02]"
                    :src="mainImageUrl"
                    @error="handleImgError"
                  />
                  <template v-if="thumbnailUrls.length > 1">
                    <button
                      type="button"
                      aria-label="Ảnh trước"
                      class="absolute left-3 top-1/2 inline-flex size-9 -translate-y-1/2 items-center justify-center rounded-full bg-white/90 text-slate-700 shadow-md transition hover:bg-white dark:bg-slate-900/85 dark:text-white"
                      @click="showPreviousImage"
                    >
                      <span class="material-symbols-outlined text-[20px]">chevron_left</span>
                    </button>
                    <button
                      type="button"
                      aria-label="Ảnh tiếp theo"
                      class="absolute right-3 top-1/2 inline-flex size-9 -translate-y-1/2 items-center justify-center rounded-full bg-white/90 text-slate-700 shadow-md transition hover:bg-white dark:bg-slate-900/85 dark:text-white"
                      @click="showNextImage"
                    >
                      <span class="material-symbols-outlined text-[20px]">chevron_right</span>
                    </button>
                    <div class="absolute bottom-3 left-1/2 flex -translate-x-1/2 items-center gap-1.5 rounded-full bg-slate-900/45 px-2.5 py-1.5">
                      <button
                        v-for="(url, idx) in thumbnailUrls"
                        :key="`dot-${idx}-${url}`"
                        type="button"
                        :aria-label="`Chọn ảnh ${idx + 1}`"
                        class="size-2 rounded-full transition"
                        :class="idx === activeImageIndex ? 'bg-white' : 'bg-white/50 hover:bg-white/80'"
                        @click="activeImageIndex = idx"
                      />
                    </div>
                  </template>
                </div>
              </div>

              <div class="flex flex-col gap-6 flex-1">
                <div class="flex flex-col gap-2">
                  <div class="flex items-center gap-2">
                    <span
                      class="px-2 py-1 rounded text-xs font-bold uppercase tracking-wider"
                      :class="
                        inStock
                          ? 'bg-green-500/20 text-green-600 dark:text-green-400'
                          : 'bg-red-500/20 text-red-600 dark:text-red-400'
                      "
                      >{{ inStock ? "Còn hàng" : "Hết hàng" }}</span
                    >
                    <span
                      class="flex items-center text-yellow-500 gap-1 text-sm font-medium"
                    >
                      <span
                        class="material-symbols-outlined text-[18px] fill-current"
                        >star</span
                      >
                      <template v-if="averageRating !== null">
                        {{ averageRating.toFixed(1) }} ({{ reviewCount }} đánh
                        giá)
                      </template>
                      <template v-else> Chưa có đánh giá </template>
                    </span>
                  </div>
                  <h1
                    class="text-[#111418] dark:text-white tracking-tight text-3xl md:text-4xl font-black leading-tight"
                  >
                    {{ productName }}
                  </h1>
                  <div class="flex flex-wrap items-center gap-3">
                    <p
                      v-if="promotionPercent > 0"
                      class="text-base font-semibold text-slate-400 line-through"
                    >
                      {{ displayBasePrice }}
                    </p>
                    <p
                      class="text-2xl font-bold"
                      :class="promotionPercent > 0 ? 'text-red-600 dark:text-red-400' : 'text-[#111418] dark:text-white'"
                    >
                      {{ displayPrice }}
                    </p>
                    <span
                      v-if="promotionPercent > 0"
                      class="rounded-full bg-red-100 px-2 py-1 text-xs font-bold text-red-600"
                    >
                      -{{ promotionPercent }}%
                    </span>
                  </div>
                  <p class="text-sm font-semibold text-slate-600 dark:text-[#9cabba]">
                    Số lượng:
                    <span
                      :class="inStock ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'"
                    >
                      {{ selectedStockQuantity === null ? 'Không rõ' : selectedStockQuantity }}
                    </span>
                  </p>
                  <p class="text-sm font-semibold text-slate-600 dark:text-[#9cabba]">
                    Biến thể:
                    <span
                      :class="inStock ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'"
                    >
                      {{ selectedVariantStatusLabel }}
                    </span>
                  </p>
                </div>
                <p
                  class="text-[#4f5b69] dark:text-[#9cabba] text-base font-normal leading-normal"
                >
                  {{ productDescription }}
                </p>
                <hr class="border-[#e5e7eb] dark:border-[#283039]" />

                <div class="flex flex-col gap-4">
                  <div class="grid grid-cols-2 gap-4">
                    <label class="flex flex-col flex-1 gap-2">
                      <p
                        class="text-[#111418] dark:text-white text-sm font-bold"
                      >
                        Màu sắc
                      </p>
                      <div class="relative">
                        <select
                          v-model="selectedColorKey"
                          class="appearance-none w-full cursor-pointer rounded-lg border border-[#d1d5db] dark:border-[#3b4754] bg-[#f9fafb] dark:bg-[#1b2127] text-[#111418] dark:text-white h-12 px-4 pr-10 text-base focus:border-primary focus:ring-1 focus:ring-primary focus:outline-none"
                        >
                          <option
                            v-if="colorOptions.length === 0"
                            disabled
                            value=""
                          >
                            Không có màu
                          </option>
                          <option
                            v-for="c in colorOptions"
                            :key="c.value"
                            :value="c.value"
                          >
                            {{ c.label }}
                          </option>
                        </select>
                        <div
                          class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-3 text-[#9cabba]"
                        >
                          <span class="material-symbols-outlined"
                            >expand_more</span
                          >
                        </div>
                      </div>
                    </label>
                    <label class="flex flex-col flex-1 gap-2">
                      <p
                        class="text-[#111418] dark:text-white text-sm font-bold"
                      >
                        Size (US)
                      </p>
                      <div class="relative">
                        <select
                          v-model="selectedSizeKey"
                          class="appearance-none w-full cursor-pointer rounded-lg border border-[#d1d5db] dark:border-[#3b4754] bg-[#f9fafb] dark:bg-[#1b2127] text-[#111418] dark:text-white h-12 px-4 pr-10 text-base focus:border-primary focus:ring-1 focus:ring-primary focus:outline-none"
                        >
                          <option
                            v-if="sizeOptions.length === 0"
                            disabled
                            value=""
                          >
                            Không có size
                          </option>
                          <option
                            v-for="s in sizeOptions"
                            :key="s.value"
                            :value="s.value"
                          >
                            {{ s.label }}
                          </option>
                        </select>
                        <div
                          class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-3 text-[#9cabba]"
                        >
                          <span class="material-symbols-outlined"
                            >expand_more</span
                          >
                        </div>
                      </div>
                    </label>
                  </div>
                  <router-link
                    class="text-primary text-sm font-medium hover:underline"
                    to="/size-guide"
                    >Bảng size</router-link
                  >
                </div>

                <div class="flex flex-col sm:flex-row gap-3 mt-4">
                  <button
                    class="flex-1 flex items-center justify-center gap-2 rounded-lg h-12 px-5 bg-primary hover:bg-primary-hover text-white text-base font-bold transition-colors disabled:opacity-60 disabled:cursor-not-allowed"
                    :disabled="!canAddToCart"
                    @click="handleAddToCart"
                  >
                    <span class="material-symbols-outlined">shopping_cart</span>
                    <span>Thêm vào giỏ</span>
                  </button>

                  <button
                    class="flex-1 flex items-center justify-center gap-2 rounded-lg h-12 px-5 bg-transparent border-2 border-[#d1d5db] dark:border-[#3b4754] text-[#111418] dark:text-white hover:bg-gray-100 dark:hover:bg-[#283039] text-base font-bold transition-colors"
                    :class="
                      isCompared
                        ? 'border-primary text-primary dark:text-primary'
                        : ''
                    "
                    type="button"
                    @click="handleCompare"
                  >
                    <span class="material-symbols-outlined"
                      >compare_arrows</span
                    >
                    <span>{{ isCompared ? "Đã so sánh" : "So sánh" }}</span>
                  </button>
                </div>

                <div class="flex gap-6 mt-2">
                  <div
                    class="flex items-center gap-2 text-[#4f5b69] dark:text-[#9cabba]"
                  >
                    <span class="material-symbols-outlined text-[20px]"
                      >local_shipping</span
                    >
                    <span class="text-sm">Miễn phí vận chuyển</span>
                  </div>
                  <div
                    class="flex items-center gap-2 text-[#4f5b69] dark:text-[#9cabba]"
                  >
                    <span class="material-symbols-outlined text-[20px]"
                      >sync</span
                    >
                    <span class="text-sm">Đổi trả trong 30 ngày</span>
                  </div>
                </div>
              </div>
            </div>

            <div
              class="px-4 py-10 mt-10 border-t border-[#e5e7eb] dark:border-[#283039]"
            >
              <h2
                class="text-[#111418] dark:text-white text-2xl font-bold mb-6"
              >
                Đánh giá sản phẩm
              </h2>
              <div class="grid md:grid-cols-[300px_1fr] gap-10">
                <div class="flex flex-col gap-4">
                  <div
                    class="flex flex-col p-6 rounded-xl bg-[#f0f2f5] dark:bg-[#1b2127]"
                  >
                    <span
                      class="text-5xl font-black text-[#111418] dark:text-white"
                    >
                      {{
                        averageRating !== null ? averageRating.toFixed(1) : "--"
                      }}
                    </span>
                    <StarRating
                      v-if="averageRating !== null"
                      :rating="averageRating"
                      size-class="text-[20px]"
                    />
                    <StarRating
                      v-else
                      :rating="0"
                      size-class="text-[20px]"
                    />
                    <span class="text-[#4f5b69] dark:text-[#9cabba] text-sm">
                      {{
                        reviewCount > 0
                          ? `Được tổng hợp từ ${reviewCount} đánh giá`
                          : "Chưa có đánh giá"
                      }}
                    </span>
                    <router-link
                      class="mt-6 w-full py-2 px-4 border border-[#d1d5db] dark:border-[#3b4754] rounded-lg text-center text-[#111418] dark:text-white font-medium hover:bg-gray-200 dark:hover:bg-[#283039] transition-colors"
                      :to="
                        isLoggedIn ? `/product/${productId}/review` : '/login'
                      "
                    >
                      Viết đánh giá
                    </router-link>
                  </div>
                </div>
                <div class="flex flex-col gap-6">
                  <div
                    v-for="review in reviews"
                    :key="review.id"
                    class="rounded-xl border border-[#e5e7eb] bg-white p-5 shadow-sm dark:border-[#283039] dark:bg-[#111418]"
                  >
                    <div class="flex justify-between items-start">
                      <div>
                        <p
                          class="text-[#111418] dark:text-white font-bold text-sm"
                        >
                          {{ getReviewAuthorName(review) }}
                        </p>
                        <StarRating
                          :rating="review.rating"
                          size-class="text-[16px]"
                          container-class="mt-1"
                        />
                      </div>
                      <span class="text-[#9cabba] text-xs">
                        {{ formatReviewDate(review.createdAt) }}
                      </span>
                    </div>
                    <p
                      v-if="review.title"
                      class="text-[#111418] dark:text-white font-semibold text-sm"
                    >
                      {{ review.title }}
                    </p>
                    <p
                      class="text-[#4f5b69] dark:text-[#d0d6dc] text-sm leading-relaxed"
                    >
                      {{ review.comment }}
                    </p>
                  </div>
                  <div
                    v-if="reviews.length === 0"
                    class="text-sm text-[#4f5b69] dark:text-[#9cabba]"
                  >
                    Chưa có đánh giá nào cho sản phẩm này. Bạn có thể là người
                    đầu tiên viết đánh giá.
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useCartStore } from "@/stores/cart";
import { useCompareStore } from "@/stores/compare";
import { useProductStore } from "@/stores/products";
import { useReviewsStore } from "@/stores/reviews";
import {
  getVariantColorKey,
  getVariantSizeKey,
  resolveColorFromVariant,
  resolveSizeFromVariant,
  variantMatchesColorKey,
  variantMatchesSizeKey,
} from "@/utils/variantValues";
import { getProductImageUrl } from "@/utils/productImages";
import StarRating from "@/components/StarRating.vue";
import axios from "axios";

const route = useRoute();
const router = useRouter();
const productStore = useProductStore();
const cartStore = useCartStore();
const reviewsStore = useReviewsStore();
const compareStore = useCompareStore();

const activeImageIndex = ref(0);
const selectedColorKey = ref("");
const selectedSizeKey = ref("");
const isLoggedIn = ref(false);
const showAuthRequiredPopup = ref(false);
const showUserMenu = ref(false);

const productId = computed(() => Number.parseInt(route.params.id, 10));

const productRef = ref(null);
const product = computed(() => productRef.value);
const isCompared = computed(
  () => Number.isFinite(productId.value) && compareStore.has(productId.value),
);

const variants = ref([]);
const activePromotions = ref([]);

const productName = computed(() => product.value?.name || "Sản phẩm");
const productDescription = computed(
  () =>
    product.value?.description || "Sản phẩm này hiện chưa có mô tả chi tiết.",
);
const categoryName = computed(
  () => product.value?.category?.name || "Sản phẩm",
);

const reviews = computed(() => reviewsStore.getByProductId(productId.value));
const reviewCount = computed(() => reviews.value.length);
const averageRating = computed(() =>
  reviewsStore.getAverageRating(productId.value),
);

function getVariantColorId(variant) {
  return resolveColorFromVariant(variant).id;
}

function getVariantSizeId(variant) {
  return resolveSizeFromVariant(variant).id;
}

function getVisibleVariants(list) {
  return (list || []).filter(
    (variant) => normalizeVariantStatus(variant) !== "HIDDEN",
  );
}

function getVariantNumericPrice(variant) {
  const parsed = Number(variant?.price);
  return Number.isFinite(parsed) && parsed >= 0 ? parsed : null;
}

function normalizeVariantStatus(variant) {
  const rawStock = variant?.stockQuantity ?? variant?.stock_quantity;
  const hasKnownStock = rawStock !== null && rawStock !== undefined && rawStock !== "";
  const stock = Number(rawStock);
  const raw = String(
    variant?.status || variant?.variantStatus || variant?.variant_status || "",
  )
    .trim()
    .toUpperCase();

  if (["HIDDEN", "INACTIVE", "DISCONTINUED", "STOP_SELLING"].includes(raw)) {
    return "HIDDEN";
  }
  if (["OUT_OF_STOCK", "SOLD_OUT", "OUTOFSTOCK"].includes(raw)) {
    return "OUT_OF_STOCK";
  }
  return !hasKnownStock || (Number.isFinite(stock) && stock > 0)
    ? "SELLING"
    : "OUT_OF_STOCK";
}

function isVariantSellable(variant) {
  return normalizeVariantStatus(variant) === "SELLING";
}

function getLowestPriceVariant(list) {
  if (!Array.isArray(list) || list.length === 0) return null;

  let lowest = null;
  let lowestPrice = null;

  const visibleList = list.filter((variant) => normalizeVariantStatus(variant) !== "HIDDEN");
  const sellableList = visibleList.filter(isVariantSellable);
  const candidates = sellableList.length > 0 ? sellableList : visibleList;

  for (const variant of candidates) {
    const price = getVariantNumericPrice(variant);
    if (price === null) continue;
    if (lowestPrice === null || price < lowestPrice) {
      lowest = variant;
      lowestPrice = price;
    }
  }

  return lowest || candidates[0] || null;
}

const colorOptions = computed(() => {
  const mapped = new Map();
  for (const variant of getVisibleVariants(variants.value)) {
    const key = getVariantColorKey(variant);
    if (!key || mapped.has(key)) continue;

    const colorMeta = resolveColorFromVariant(variant);
    mapped.set(key, {
      value: key,
      label: colorMeta.name || `Màu ${key}`,
    });
  }

  return [...mapped.values()];
});

const sizeOptions = computed(() => {
  const mapped = new Map();
  for (const variant of getVisibleVariants(variants.value)) {
    if (!variantMatchesColorKey(variant, selectedColorKey.value)) continue;

    const key = getVariantSizeKey(variant);
    if (!key || mapped.has(key)) continue;

    const sizeMeta = resolveSizeFromVariant(variant);
    mapped.set(key, {
      value: key,
      label: `${sizeMeta.name || `Size ${key}`}${isVariantSellable(variant) ? "" : " - Hết hàng"}`,
    });
  }

  return [...mapped.values()];
});

const selectedVariant = computed(() => {
  if (!variants.value || variants.value.length === 0) return null;

  const exactMatch = variants.value.find(
    (variant) =>
      variantMatchesColorKey(variant, selectedColorKey.value) &&
      variantMatchesSizeKey(variant, selectedSizeKey.value),
  );
  if (exactMatch) return exactMatch;

  const colorMatch = selectedColorKey.value
    ? variants.value.find((variant) =>
        variantMatchesColorKey(variant, selectedColorKey.value),
      )
    : null;
  if (colorMatch) return colorMatch;

  return getLowestPriceVariant(variants.value);
});

const inStock = computed(() => {
  const v = selectedVariant.value;
  if (!v) return false;
  const qty = Number(v.stockQuantity ?? v.stock_quantity ?? 0);
  return isVariantSellable(v) && (Number.isFinite(qty) ? qty > 0 : false);
});

const selectedStockQuantity = computed(() => {
  const qty = Number(
    selectedVariant.value?.stockQuantity ?? selectedVariant.value?.stock_quantity,
  );
  return Number.isFinite(qty) ? Math.max(0, qty) : null;
});

const selectedVariantStatusLabel = computed(() => {
  const status = normalizeVariantStatus(selectedVariant.value);
  if (status === "HIDDEN") return "Ngừng bán";
  if (status === "OUT_OF_STOCK") return "Hết hàng";
  return "Đang bán";
});

const displayPrice = computed(() => {
  const price = selectedVariant.value?.price ?? product.value?.price;
  if (price === null || price === undefined) return "Liên hệ";
  const n = Number(price);
  if (!Number.isFinite(n)) return String(price);
  const finalPrice = promotionPercent.value > 0
    ? n * (100 - promotionPercent.value) / 100
    : n;
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
    maximumFractionDigits: 0,
  }).format(finalPrice);
});

const displayBasePrice = computed(() => {
  const price = selectedVariant.value?.price ?? product.value?.price;
  const n = Number(price);
  return Number.isFinite(n)
    ? new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND", maximumFractionDigits: 0 }).format(n)
    : "Liên hệ";
});

const promotionPercent = computed(() => {
  const id = Number(productId.value);
  return activePromotions.value.reduce((best, promotion) => {
    const ids = promotion.productIds ||
      promotion.products?.map((item) => item.id) ||
      [];
    return ids.some((item) => Number(item) === id)
      ? Math.max(best, Number(promotion.discountPercent || 0))
      : best;
  }, 0);
});

async function loadActivePromotions() {
  try {
    const response = await axios.get("http://localhost:8080/api/promotions?page=0&size=200");
    const rows = Array.isArray(response.data?.data)
      ? response.data.data
      : Array.isArray(response.data) ? response.data : [];
    activePromotions.value = rows.filter((promotion) => promotion.status === "ACTIVE");
  } catch {
    activePromotions.value = [];
  }
}

const baseImageUrl = computed(() => {
  if (selectedVariant.value?.imageUrl) {
    return selectedVariant.value.imageUrl;
  }
  return getProductImageUrl({
    product: product.value,
    variants: variants.value,
    width: 800,
    height: 600,
  });
});

// 1. CẬP NHẬT LẠI DANH SÁCH ẢNH THUMBNAIL (ẢNH NHỎ)
const thumbnailUrls = computed(() => {
  const urls = [];

  // Thêm ảnh chính vào đầu tiên
  if (baseImageUrl.value) {
    urls.push(baseImageUrl.value);
  }

  // Thêm mảng ảnh phụ (extraImages) từ Backend trả về
  if (
    selectedVariant.value &&
    selectedVariant.value.extraImages &&
    selectedVariant.value.extraImages.length > 0
  ) {
    urls.push(...selectedVariant.value.extraImages);
  }

  return [...new Set(urls.filter(Boolean))];
});

// 2. CẬP NHẬT ẢNH CHÍNH (THAY ĐỔI KHI BẤM VÀO ẢNH NHỎ)
const mainImageUrl = computed(() => {
  if (thumbnailUrls.value.length > 0) {
    // Trả về ảnh dựa trên cái index người dùng đang bấm
    return (
      thumbnailUrls.value[activeImageIndex.value] || thumbnailUrls.value[0]
    );
  }
  return getProductImageUrl({
    product: product.value,
    variants: variants.value,
    width: 800,
    height: 600,
  });
});

function showPreviousImage() {
  const total = thumbnailUrls.value.length;
  if (total < 2) return;
  activeImageIndex.value = (activeImageIndex.value - 1 + total) % total;
}

function showNextImage() {
  const total = thumbnailUrls.value.length;
  if (total < 2) return;
  activeImageIndex.value = (activeImageIndex.value + 1) % total;
}

// 3. THÊM ĐOẠN NÀY ĐỂ RESET LẠI ẢNH CHÍNH KHI ĐỔI MÀU GIÀY KHÁC
watch(selectedVariant, () => {
  activeImageIndex.value = 0;
});

watch(selectedColorKey, () => {
  const options = sizeOptions.value;
  if (!options.length) {
    selectedSizeKey.value = "";
    return;
  }

  const hasCurrentSize = options.some(
    (opt) => opt.value === selectedSizeKey.value,
  );
  if (!hasCurrentSize) {
    selectedSizeKey.value = options[0].value;
  }
});

const canAddToCart = computed(
  () => !!product.value && !!selectedVariant.value && inStock.value,
);

function getAccountIdFromUser(user) {
  const source = user && typeof user === "object" ? user : null;
  if (!source) {
    return null;
  }

  const candidate =
    source.accountId !== undefined && source.accountId !== null
      ? source.accountId
      : source.account_id !== undefined && source.account_id !== null
        ? source.account_id
        : source.id;

  const parsed = Number(candidate);
  return Number.isFinite(parsed) && parsed > 0 ? parsed : null;
}

async function load() {
  if (!Number.isFinite(productId.value)) {
    router.replace("/shop");
    return;
  }

  // Ensure base lists exist (brand/category enrichment + variant lookups).
  // Fetch the specific product by id to ensure name/category are correct.
  productRef.value = await productStore.fetchProductById(productId.value);
  await loadActivePromotions();

  // Backfill lists if needed for other pages/components.
  if (productStore.products.length === 0) await productStore.fetchProducts();
  if (productStore.variants.length === 0) {
    await productStore.fetchVariants();
  }
  if (productStore.brands.length === 0) {
    await productStore.fetchBrands();
  }
  if (productStore.categories.length === 0) {
    await productStore.fetchCategories();
  }

  // Load variants for this product.
  try {
    variants.value = await productStore.fetchVariantsByProductId(
      productId.value,
    );
  } catch {
    variants.value = productStore.variants.filter(
      (v) => v.productId === productId.value,
    );
  }

  await reviewsStore.fetchAccounts();
  await reviewsStore.fetchReviews({ force: true });

  // Default selections based on the lowest-priced variant.
  const lowestVariant = getLowestPriceVariant(variants.value);
  if (lowestVariant) {
    const lowestColorKey = getVariantColorKey(lowestVariant);
    const lowestSizeKey = getVariantSizeKey(lowestVariant);
    if (lowestColorKey) selectedColorKey.value = lowestColorKey;
    if (lowestSizeKey) selectedSizeKey.value = lowestSizeKey;
  }
  activeImageIndex.value = 0;
}

function handleAddToCart() {
  if (!canAddToCart.value) return;

  if (!isLoggedIn.value) {
    showAuthRequiredPopup.value = true;
    return;
  }

  const added = cartStore.addToCart(product.value, selectedVariant.value, 1);
  if (added === false) {
    showAuthRequiredPopup.value = true;
    return;
  }

  router.push("/cart");
}

function handleCompare() {
  if (!Number.isFinite(productId.value)) return;
  compareStore.add(productId.value);
  router.push("/compare");
}

function checkLoginStatus() {
  const user = localStorage.getItem("user");

  let parsedUser = null;
  if (user) {
    try {
      parsedUser = JSON.parse(user);
    } catch {
      parsedUser = null;
    }
  }

  const parsedAccountId = getAccountIdFromUser(parsedUser);
  isLoggedIn.value = parsedAccountId !== null;

  if (parsedAccountId !== null) {
    cartStore.accountId = parsedAccountId;
    return;
  }

  cartStore.accountId = null;
  showUserMenu.value = false;
}

function closeAuthRequiredPopup() {
  showAuthRequiredPopup.value = false;
}

function goToLoginFromPopup() {
  showAuthRequiredPopup.value = false;
  router.push({
    name: "login",
    query: {
      redirect: route.fullPath,
    },
  });
}

function goToRegisterFromPopup() {
  showAuthRequiredPopup.value = false;
  router.push({
    name: "register",
    query: {
      redirect: route.fullPath,
    },
  });
}

function toggleUserMenu(event) {
  event.stopPropagation();
  showUserMenu.value = !showUserMenu.value;
}

function handleLogout() {
  localStorage.removeItem("user");
  localStorage.removeItem("token");
  isLoggedIn.value = false;
  showUserMenu.value = false;
  router.push("/");
}

function handleClickOutside(event) {
  const target = event.target;
  if (!(target instanceof Element) || !target.closest(".account-menu")) {
    showUserMenu.value = false;
  }
}

function handleImgError(e) {
  // Always fall back to a placeholder if the backend image is missing.
  e.target.src =
    "https://via.placeholder.com/800x600?text=" +
    encodeURIComponent(productName.value);
}

function formatReviewDate(iso) {
  if (!iso) return "";
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return "";
  return d.toLocaleDateString("vi-VN");
}

function getReviewAuthorName(review) {
  return reviewsStore.getAuthorName(review);
}

onMounted(() => {
  checkLoginStatus();
  load();
  document.addEventListener("click", handleClickOutside);
  window.addEventListener("storage", checkLoginStatus);
});

onUnmounted(() => {
  document.removeEventListener("click", handleClickOutside);
  window.removeEventListener("storage", checkLoginStatus);
});

watch(
  () => route.fullPath,
  () => {
    checkLoginStatus();
    showUserMenu.value = false;
  },
);

watch(
  () => route.params.id,
  () => {
    load();
  },
);
</script>
