<template>
  <div class="retail-shell flex flex-col overflow-x-hidden antialiased">
    <MainTopBar />

    <main class="flex-1 pb-16">
      <section class="mx-auto w-full max-w-[1240px] px-4 pt-8 md:px-8 xl:px-10">
        <div
          class="relative overflow-hidden rounded-[24px] lg:rounded-[32px] ring-1 ring-slate-200 dark:ring-zinc-800 bg-slate-100 dark:bg-zinc-900 w-full shadow-sm"
        >
          <img
            :src="bannerSlides[2].image"
            class="w-full h-auto invisible block pointer-events-none"
            aria-hidden="true"
            alt="placeholder"
          />

          <transition :name="bannerDirection">
            <div :key="currentSlide" class="absolute inset-0 w-full h-full">
              <img
                :src="bannerSlides[currentSlide].image"
                :alt="bannerSlides[currentSlide].alt"
                class="h-full w-full object-cover object-center"
              />
              <div
                v-if="currentSlide !== 2"
                class="absolute inset-0 bg-gradient-to-r from-black/50 via-black/20 to-transparent flex items-center p-8 md:p-16"
              >
                <div class="max-w-xl text-white">
                  <h2
                    class="text-xl font-bold sm:text-3xl md:text-4xl lg:text-5xl leading-tight drop-shadow-md"
                  >
                    {{ bannerSlides[currentSlide].title }}
                  </h2>
                </div>
              </div>
            </div>
          </transition>

          <button
            type="button"
            @click="prevSlide"
            class="absolute left-4 top-1/2 -translate-y-1/2 inline-flex size-9 md:size-11 items-center justify-center rounded-full bg-white/80 dark:bg-black/50 text-slate-900 dark:text-white shadow-md backdrop-blur-sm transition-colors hover:bg-white z-10"
          >
            <span class="material-symbols-outlined font-bold text-lg md:text-xl"
              >chevron_left</span
            >
          </button>
          <button
            type="button"
            @click="nextSlide"
            class="absolute right-4 top-1/2 -translate-y-1/2 inline-flex size-9 md:size-11 items-center justify-center rounded-full bg-white/80 dark:bg-black/50 text-slate-900 dark:text-white shadow-md backdrop-blur-sm transition-colors hover:bg-white z-10"
          >
            <span class="material-symbols-outlined font-bold text-lg md:text-xl"
              >chevron_right</span
            >
          </button>

          <div
            class="absolute bottom-4 md:bottom-5 left-1/2 flex -translate-x-1/2 gap-3 z-10"
          >
            <button
              v-for="(_, index) in bannerSlides"
              :key="index"
              @click="goToSlide(index)"
              class="size-3 md:size-3.5 rounded-full bg-white transition-all duration-300 shadow-sm"
              :class="
                currentSlide === index
                  ? 'border-[3px] border-black scale-110'
                  : 'opacity-80 hover:opacity-100 hover:scale-110'
              "
            ></button>
          </div>
        </div>
      </section>

      <section class="mx-auto mt-8 w-full max-w-[1240px] px-4 md:px-8 xl:px-10">
        <div class="grid gap-5 md:grid-cols-3">
          <article
            v-for="item in categoryShortcuts"
            :key="item.title"
            class="retail-card flex items-center justify-between gap-5 px-6 py-5 rounded-[24px]"
          >
            <div>
              <p
                class="text-[11px] font-semibold uppercase tracking-[0.22em] text-primary"
              >
                {{ item.kicker }}
              </p>
              <h3
                class="mt-2 text-xl font-semibold text-slate-900 dark:text-white"
              >
                {{ item.title }}
              </h3>
              <p
                class="mt-2 text-sm leading-6 text-slate-600 dark:text-[#cabdae]"
              >
                {{ item.description }}
              </p>
            </div>
            <router-link
              :to="item.to"
              class="inline-flex size-11 shrink-0 items-center justify-center rounded-full bg-primary/10 text-primary transition-colors hover:bg-primary hover:text-white"
            >
              <span class="material-symbols-outlined">arrow_forward</span>
            </router-link>
          </article>
        </div>
      </section>

      <section
        v-if="promotionalProducts.length > 0"
        class="mx-auto mt-16 w-full max-w-[1240px] px-4 md:px-8 xl:px-10"
      >
        <div class="mb-8 flex items-end justify-between gap-4">
          <div>
            <span class="retail-kicker">Đang khuyến mãi</span>
            <h2 class="retail-heading mt-4 text-[28px] leading-none text-slate-900 dark:text-white md:text-[36px]">
              Ưu đãi nổi bật hôm nay
            </h2>
          </div>
          <router-link to="/shop" class="text-sm font-semibold text-primary hover:underline">
            Xem sản phẩm
          </router-link>
        </div>
        <div class="grid grid-cols-1 gap-5 md:grid-cols-2 xl:grid-cols-4">
          <article
            v-for="product in promotionalProducts"
            :key="`promotion-${product.id}`"
            class="retail-card overflow-hidden p-0 transition-transform hover:-translate-y-1"
          >
            <router-link :to="`/product/${product.id}`" class="block">
              <div class="relative aspect-[5/4] overflow-hidden bg-[#ece4da] dark:bg-[#2b241f]">
                <img :src="getProductImage(product)" :alt="product.name" class="h-full w-full object-cover transition-transform duration-700 hover:scale-105" />
                <span class="absolute left-3 top-3 rounded-full bg-red-500 px-3 py-1 text-xs font-bold text-white">
                  -{{ promotionDiscount(product.id) }}%
                </span>
              </div>
            </router-link>
            <div class="px-5 py-5">
              <p class="text-[10px] uppercase tracking-[0.16em] text-slate-500">{{ getBrandName(product.brandId) }}</p>
              <router-link :to="`/product/${product.id}`" class="mt-2 block min-h-[48px] line-clamp-2 text-lg font-semibold text-slate-900 dark:text-white">
                {{ product.name }}
              </router-link>
              <div class="mt-3 flex flex-wrap items-baseline gap-2">
                <p class="text-sm font-semibold text-slate-400 line-through">
                  {{ formatMoney(getProductPrice(product)) }}
                </p>
                <p class="text-lg font-bold text-primary">
                  {{ formatMoney(getDiscountedProductPrice(product)) }}
                </p>
              </div>
            </div>
          </article>
        </div>
      </section>

      <section
        class="mx-auto mt-16 w-full max-w-[1240px] px-4 md:px-8 xl:px-10"
      >
        <div
          class="mb-8 flex flex-row items-end justify-between overflow-hidden"
        >
          <div class="flex-1 min-w-0 pr-4">
            <span class="retail-kicker whitespace-nowrap inline-block"
              >Danh mục nổi bật</span
            >
            <h2
              class="retail-heading mt-4 text-[28px] md:text-[36px] lg:text-[42px] leading-none text-slate-900 dark:text-white whitespace-nowrap truncate"
            >
              Chọn nhanh theo nhu cầu
            </h2>
          </div>

          <div class="flex items-center gap-4 justify-end shrink-0">
            <div v-if="totalCategoryPages > 1" class="flex gap-2">
              <button
                type="button"
                @click="prevCategoryPage"
                :disabled="categoryPage === 0"
                class="inline-flex size-10 items-center justify-center rounded-full border border-slate-300 dark:border-zinc-700 text-slate-700 dark:text-white transition-colors hover:border-primary hover:text-primary disabled:opacity-30 disabled:hover:border-slate-300 disabled:hover:text-slate-700"
              >
                <span class="material-symbols-outlined">chevron_left</span>
              </button>
              <button
                type="button"
                @click="nextCategoryPage"
                :disabled="categoryPage >= totalCategoryPages - 1"
                class="inline-flex size-10 items-center justify-center rounded-full border border-slate-300 dark:border-zinc-700 text-slate-700 dark:text-white transition-colors hover:border-primary hover:text-primary disabled:opacity-30 disabled:hover:border-slate-300 disabled:hover:text-slate-700"
              >
                <span class="material-symbols-outlined">chevron_right</span>
              </button>
            </div>
          </div>
        </div>

        <transition :name="'slide-' + categorySlideDirection" mode="out-in">
          <div
            :key="categoryPage"
            class="grid grid-cols-2 gap-5 lg:grid-cols-4"
          >
            <router-link
              v-for="cat in displayedCategoryCards"
              :key="cat.id"
              :to="{ path: '/shop', query: { categoryId: String(cat.id) } }"
              class="group overflow-hidden rounded-[24px] border border-slate-100 dark:border-zinc-800 shadow-sm transition-shadow hover:shadow-md"
            >
              <div
                class="retail-card h-full overflow-hidden p-0 border-none shadow-none"
              >
                <div
                  class="aspect-[4/5] overflow-hidden bg-[#f4f4f4] dark:bg-[#1a1a1a]"
                >
                  <img
                    :src="cat.image"
                    :alt="cat.name"
                    class="h-full w-full object-cover transition-transform duration-700 group-hover:scale-105"
                  />
                </div>
                <div class="px-5 py-5 bg-white dark:bg-[#221f1c]">
                  <p
                    class="text-[10px] md:text-[11px] uppercase tracking-[0.22em] text-primary whitespace-nowrap"
                  >
                    Danh mục
                  </p>
                  <h3
                    class="mt-2 text-base md:text-lg font-semibold text-slate-900 dark:text-white whitespace-nowrap truncate"
                  >
                    {{ cat.name }}
                  </h3>
                </div>
              </div>
            </router-link>
          </div>
        </transition>
      </section>

      <section
        class="mx-auto mt-16 w-full max-w-[1240px] px-4 md:px-8 xl:px-10"
      >
        <div
          class="mb-8 flex flex-col gap-3 md:flex-row md:items-end md:justify-between"
        >
          <div>
            <span class="retail-kicker">Sản phẩm nên xem</span>
            <h2
              class="retail-heading mt-4 text-[36px] md:text-[42px] leading-none text-slate-900 dark:text-white"
            >
              Mẫu đang được quan tâm
            </h2>
          </div>
          <router-link
            to="/shop"
            class="text-sm font-semibold text-primary hover:underline whitespace-nowrap"
          >
            Xem toàn bộ cửa hàng
          </router-link>
        </div>

        <div class="grid grid-cols-1 gap-5 md:grid-cols-2 xl:grid-cols-4">
          <article
            v-for="product in featuredProducts"
            :key="product.id"
            class="retail-card overflow-hidden p-0 transition-transform hover:-translate-y-1 rounded-[24px] border border-slate-100 dark:border-zinc-800"
          >
            <router-link :to="`/product/${product.id}`" class="block">
              <div
                class="aspect-[5/4] overflow-hidden bg-[#ece4da] dark:bg-[#2b241f]"
              >
                <img
                  :src="getProductImage(product)"
                  :alt="product.name"
                  class="h-full w-full object-cover transition-transform duration-700 hover:scale-105"
                />
              </div>
            </router-link>
            <div class="px-5 py-5 bg-white dark:bg-[#221f1c]">
              <p
                class="text-[10px] md:text-xs font-medium uppercase tracking-[0.16em] text-slate-500 dark:text-[#cabdae]"
              >
                {{ getBrandName(product.brandId) }}
              </p>
              <router-link
                :to="`/product/${product.id}`"
                class="mt-2 block min-h-[52px] text-base md:text-lg font-semibold leading-6 text-slate-900 transition-colors hover:text-primary dark:text-white line-clamp-2"
              >
                {{ product.name }}
              </router-link>
              <div class="mt-4 flex items-center justify-between">
                <div>
                  <p
                    class="text-[10px] md:text-xs uppercase tracking-[0.18em] text-slate-400 dark:text-[#a8988a]"
                  >
                    Giá tham khảo
                  </p>
                  <p
                    class="mt-1 text-lg md:text-xl font-semibold text-slate-900 dark:text-white"
                  >
                    {{ formatMoney(getProductPrice(product)) }}
                  </p>
                </div>
                <router-link
                  :to="`/product/${product.id}`"
                  class="inline-flex size-10 md:size-11 items-center justify-center rounded-full bg-primary text-white transition-colors hover:bg-primary-hover"
                >
                  <span class="material-symbols-outlined text-lg"
                    >arrow_forward</span
                  >
                </router-link>
              </div>
            </div>
          </article>
        </div>
      </section>

      <section
        id="brands"
        class="mx-auto mt-16 w-full max-w-[1240px] px-4 md:px-8 xl:px-10"
      >
        <div
          class="mb-8 flex flex-row items-end justify-between overflow-hidden"
        >
          <div class="flex-1 min-w-0 pr-4">
            <span class="retail-kicker">Danh mục thương hiệu</span>
            <h2
              class="retail-heading mt-4 text-[28px] md:text-[36px] text-slate-900 dark:text-white"
            >
              Những thương hiệu tại SizeBy
            </h2>
          </div>
          <div class="flex items-center gap-4 justify-end shrink-0">
            <div v-if="brandTotalPages > 1" class="flex gap-2">
              <button
                type="button"
                @click="brandPreviousPage"
                :disabled="brandCurrentPage === 1"
                class="inline-flex size-10 items-center justify-center rounded-full border border-slate-300 dark:border-zinc-700 text-slate-700 dark:text-white transition-colors hover:border-primary hover:text-primary disabled:opacity-30 disabled:hover:border-slate-300 disabled:hover:text-slate-700"
              >
                <span class="material-symbols-outlined">chevron_left</span>
              </button>
              <button
                type="button"
                @click="brandNextPage"
                :disabled="brandCurrentPage >= brandTotalPages"
                class="inline-flex size-10 items-center justify-center rounded-full border border-slate-300 dark:border-zinc-700 text-slate-700 dark:text-white transition-colors hover:border-primary hover:text-primary disabled:opacity-30 disabled:hover:border-slate-300 disabled:hover:text-slate-700"
              >
                <span class="material-symbols-outlined">chevron_right</span>
              </button>
            </div>
          </div>
        </div>

        <transition :name="'slide-' + brandSlideDirection" mode="out-in">
          <div
            :key="brandCurrentPage"
            class="grid grid-cols-2 gap-5 lg:grid-cols-4"
          >
            <router-link
              v-for="brand in paginatedBrands"
              :key="brand.id"
              :to="{ name: 'shop', query: { brandId: String(brand.id) } }"
              class="retail-card group overflow-hidden p-0 transition-shadow hover:shadow-md rounded-[24px] border border-slate-100 dark:border-zinc-800"
            >
              <div
                class="flex aspect-square items-center justify-center bg-[#f7efe7] p-6 dark:bg-[#2b241f]"
              >
                <img
                  :src="getBrandImageUrl(brand.logoUrl)"
                  :alt="brand.name"
                  class="h-full w-full object-contain mix-blend-multiply transition-transform duration-500 group-hover:scale-105"
                  @error="handleBrandImageError"
                />
              </div>
              <div
                class="border-t border-slate-200/70 px-6 py-5 dark:border-[#3c342e] bg-white dark:bg-[#221f1c]"
              >
                <p
                  class="text-[10px] font-semibold uppercase tracking-[0.2em] text-primary"
                >
                  Thương hiệu
                </p>
                <h3
                  class="mt-1 text-lg font-semibold text-slate-900 dark:text-white truncate"
                >
                  {{ brand.name }}
                </h3>
              </div>
            </router-link>
          </div>
        </transition>
      </section>

      <section
        v-if="isLoggedIn && activeVouchers.length > 0"
        class="mx-auto mt-16 w-full max-w-[1240px] px-4 md:px-8 xl:px-10"
      >
        <div class="retail-card px-6 py-7 sm:px-8 rounded-[24px]">
          <div
            class="mb-6 flex flex-col gap-3 md:flex-row md:items-end md:justify-between"
          >
            <div>
              <span class="retail-kicker whitespace-nowrap"
                >Ưu đãi của bạn</span
              >
              <h2
                class="retail-heading mt-4 text-[32px] md:text-[38px] leading-none text-slate-900 dark:text-white"
              >
                Mã giảm giá đang khả dụng
              </h2>
            </div>
            <router-link
              to="/vouchers"
              class="text-sm font-semibold text-primary hover:underline whitespace-nowrap"
            >
              Xem trung tâm voucher
            </router-link>
          </div>

          <div class="grid gap-4 md:grid-cols-3">
            <article
              v-for="voucher in activeVouchers.slice(0, 3)"
              :key="voucher.id"
              class="rounded-[24px] border border-dashed border-primary/30 bg-[#f6efe6] p-5 dark:bg-[#2b241f]"
            >
              <div class="flex items-start justify-between gap-4">
                <div>
                  <p
                    class="text-[11px] uppercase tracking-[0.22em] text-primary"
                  >
                    Mã áp dụng
                  </p>
                  <h3
                    class="mt-2 text-xl md:text-2xl font-bold text-slate-900 dark:text-white truncate"
                  >
                    {{ voucher.code }}
                  </h3>
                </div>
                <button
                  type="button"
                  class="inline-flex size-10 items-center justify-center rounded-full bg-white text-primary shadow-sm transition-colors hover:bg-primary hover:text-white dark:bg-[#1f1a17]"
                  @click="copyCode(voucher.code)"
                >
                  <span class="material-symbols-outlined text-lg"
                    >content_copy</span
                  >
                </button>
              </div>
              <p
                class="mt-4 text-sm leading-6 text-slate-600 dark:text-[#cabdae]"
              >
                Giảm {{ voucher.discountValue
                }}{{ voucher.discountType === "PERCENT" ? "%" : "đ" }} cho đơn
                đủ điều kiện.
              </p>
            </article>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, onBeforeUnmount } from "vue";
import axios from "axios";

import { brandsApi } from "@/services/api";
import { useCartStore } from "@/stores/cart";
import { useProductStore } from "@/stores/products";
import { getProductImageUrl } from "@/utils/productImages";

import banner1 from "@/assets/image_89e749.png";
import banner2 from "@/assets/image_89ea6b.png";
import banner3 from "@/assets/image_89eec6.png";

const cartStore = useCartStore();
const productStore = useProductStore();

const isLoggedIn = ref(false);
const showUserMenu = ref(false);
const userName = ref("");
const userEmail = ref("");
const userAvatar = ref("");
const activeVouchers = ref([]);
const activePromotions = ref([]);
const brands = ref([]);
const brandsLoading = ref(false);
const brandsError = ref("");
const brandCurrentPage = ref(1);
const brandItemsPerPage = 4;
const brandSlideDirection = ref("left");

const currentSlide = ref(0);
const bannerDirection = ref("banner-slide-left");
let bannerInterval = null;

const bannerSlides = [
  {
    image: banner1,
    alt: "Nike Dynamic Support",
  },
  {
    image: banner2,
    alt: "Adidas Ultraboost",
  },
  {
    image: banner3,
    alt: "SizeBy Shopping Online",
  },
];

function setBannerDirection(oldIndex, newIndex) {
  if (newIndex > oldIndex) {
    bannerDirection.value = "banner-slide-left";
  } else {
    bannerDirection.value = "banner-slide-right";
  }
}

function nextSlideAuto() {
  const next = (currentSlide.value + 1) % bannerSlides.length;
  bannerDirection.value = "banner-slide-left";
  currentSlide.value = next;
}

function startBannerTimer() {
  bannerInterval = setInterval(nextSlideAuto, 4000);
}

function resetBannerTimer() {
  if (bannerInterval) clearInterval(bannerInterval);
  startBannerTimer();
}

function nextSlide() {
  nextSlideAuto();
  resetBannerTimer();
}

function prevSlide() {
  const prev =
    (currentSlide.value - 1 + bannerSlides.length) % bannerSlides.length;
  bannerDirection.value = "banner-slide-right";
  currentSlide.value = prev;
  resetBannerTimer();
}

function goToSlide(index) {
  if (index === currentSlide.value) return;
  setBannerDirection(currentSlide.value, index);
  currentSlide.value = index;
  resetBannerTimer();
}

const categoryPage = ref(0);
const categoriesPerPage = 4;
const categorySlideDirection = ref("left");

const categoryShortcuts = [
  {
    kicker: "Tập luyện",
    title: "Giày chạy bộ",
    description: "Ưu tiên upper thoáng, đế bật lực và form ôm chân.",
    to: "/shop",
  },
  {
    kicker: "Đi làm, đi chơi",
    title: "Giày mang hằng ngày",
    description: "Màu trung tính, dễ phối đồ, đủ êm để đi lâu.",
    to: "/shop",
  },
  {
    kicker: "Tiết kiệm hơn",
    title: "Ưu đãi thành viên",
    description: "Mã giảm giá và ưu đãi ngắn hạn được gom về một chỗ.",
    to: "/vouchers",
  },
];

const allCategoryCards = computed(() => {
  const source = Array.isArray(productStore.categories)
    ? productStore.categories
    : [];
  return source.map((category) => {
    let catImage = category.imageUrl;
    if (!catImage) {
      const firstProduct = productStore.products.find(
        (p) =>
          p.categoryId === category.id ||
          (p.category && p.category.id === category.id),
      );
      if (firstProduct) catImage = getProductImage(firstProduct);
    }
    if (!catImage) {
      catImage =
        "https://res.cloudinary.com/dmas2qrrw/image/upload/v1773290010/AIR_MAX_270_1_ipqieq.avif";
    }
    return {
      id: category.id,
      name: String(category.name || "Danh mục").trim(),
      image: catImage,
    };
  });
});

const totalCategoryPages = computed(() =>
  Math.ceil(allCategoryCards.value.length / categoriesPerPage),
);

const displayedCategoryCards = computed(() => {
  const start = categoryPage.value * categoriesPerPage;
  return allCategoryCards.value.slice(start, start + categoriesPerPage);
});

function nextCategoryPage() {
  if (categoryPage.value < totalCategoryPages.value - 1) {
    categorySlideDirection.value = "left";
    categoryPage.value++;
  }
}
function prevCategoryPage() {
  if (categoryPage.value > 0) {
    categorySlideDirection.value = "right";
    categoryPage.value--;
  }
}

const featuredProducts = computed(() => [...productStore.products].slice(0, 4));

const promotionalProducts = computed(() => {
  const ids = new Set(
    activePromotions.value.flatMap((promotion) =>
      (promotion.productIds || promotion.products?.map((product) => product.id) || []).map(Number),
    ),
  );
  return productStore.products.filter((product) => ids.has(Number(product.id))).slice(0, 4);
});

function promotionDiscount(productId) {
  const promotion = activePromotions.value.find((item) =>
    (item.productIds || item.products?.map((product) => product.id) || []).some(
      (id) => Number(id) === Number(productId),
    ),
  );
  return Number(promotion?.discountPercent || 0);
}

function getDiscountedProductPrice(product) {
  const price = Number(getProductPrice(product));
  const percent = promotionDiscount(product?.id);
  if (!Number.isFinite(price) || percent <= 0) return price;
  return Math.max(0, Math.round(price * (100 - percent) / 100));
}

function getBrandName(brandId) {
  const brand = productStore.brands.find((item) => item.id === brandId);
  return brand?.name || "SizeBy Select";
}

const brandTotalPages = computed(() =>
  Math.max(1, Math.ceil(brands.value.length / brandItemsPerPage)),
);

const paginatedBrands = computed(() => {
  const start = (brandCurrentPage.value - 1) * brandItemsPerPage;
  return brands.value.slice(start, start + brandItemsPerPage);
});

function getBrandImageUrl(logoUrl) {
  if (!logoUrl) return "";
  if (logoUrl.startsWith("http://") || logoUrl.startsWith("https://"))
    return logoUrl;
  if (logoUrl.startsWith("/images/")) return `http://localhost:8080${logoUrl}`;
  return `http://localhost:8080/images/${logoUrl}`;
}

function handleBrandImageError(event) {
  event.target.style.display = "none";
}

function brandNextPage() {
  if (brandCurrentPage.value < brandTotalPages.value) {
    brandSlideDirection.value = "left";
    brandCurrentPage.value++;
  }
}
function brandPreviousPage() {
  if (brandCurrentPage.value > 1) {
    brandSlideDirection.value = "right";
    brandCurrentPage.value--;
  }
}

function getProductPrice(product) {
  const hasVariants = productStore.variants.some((v) => v.productId === product.id);
  if (!hasVariants) return "Sắp ra mắt";
  const variant = productStore.variants.find(
    (item) => item.productId === product.id && item.price,
  );
  return variant?.price ?? product.price ?? 0;
}

function getProductImage(product) {
  return getProductImageUrl({
    product,
    variants: productStore.variants,
    width: 800,
    height: 600,
  });
}

function formatMoney(value) {
  if (value === "Sắp ra mắt") return value;
  const amount = Number(value);
  if (!Number.isFinite(amount) || amount <= 0) return "Liên hệ";
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
    maximumFractionDigits: 0,
  }).format(amount);
}

async function loadBrands() {
  try {
    brandsLoading.value = true;
    brandsError.value = "";
    const data = await brandsApi.getAll();
    brands.value = Array.isArray(data?.list)
      ? data.list
      : Array.isArray(data)
        ? data
        : [];
  } catch (error) {
    console.error("Error fetching brands:", error);
    brandsError.value = "Không thể tải danh sách thương hiệu.";
  } finally {
    brandsLoading.value = false;
  }
}

async function loadHomeData() {
  const tasks = [];
  if (productStore.categories.length === 0)
    tasks.push(productStore.fetchCategories());
  if (productStore.products.length === 0)
    tasks.push(productStore.fetchProducts());
  if (productStore.variants.length === 0)
    tasks.push(productStore.fetchVariants());
  if (productStore.brands.length === 0) tasks.push(productStore.fetchBrands());
  await Promise.all(tasks);
}

async function loadPromotions() {
  try {
    const response = await axios.get("http://localhost:8080/api/promotions?page=0&size=100");
    const rows = Array.isArray(response.data?.data)
      ? response.data.data
      : Array.isArray(response.data) ? response.data : [];
    activePromotions.value = rows.filter((promotion) => promotion.status === "ACTIVE");
  } catch {
    activePromotions.value = [];
  }
}

async function loadUserData() {
  const rawUser = localStorage.getItem("user");
  if (!rawUser) return;
  try {
    const user = JSON.parse(rawUser);
    isLoggedIn.value = true;
    userName.value = user.username || "Khách SizeBy";
    userEmail.value = user.gmail || user.email || "";
    userAvatar.value = user.imgUrl || "";
    try {
      const response = await axios.get(`http://localhost:8080/api/vouchers`, {
        params: { accountId: user.id },
      });
      if (response.data)
        activeVouchers.value = Array.isArray(response.data)
          ? response.data.filter((v) => v.status === "ACTIVE")
          : [];
    } catch (err1) {
      try {
        const response2 = await axios.get(
          `http://localhost:8080/api/vouchers/user/${user.id}`,
        );
        if (response2.data)
          activeVouchers.value = Array.isArray(response2.data)
            ? response2.data.filter((v) => v.status === "ACTIVE")
            : [];
      } catch (err2) {
        activeVouchers.value = [];
      }
    }
  } catch (error) {
    activeVouchers.value = [];
  }
}

function copyCode(code) {
  navigator.clipboard.writeText(code);
  alert(`Đã sao chép mã ${code}`);
}

onMounted(async () => {
  await Promise.all([loadHomeData(), loadUserData(), loadBrands(), loadPromotions()]);
  startBannerTimer();
});

onBeforeUnmount(() => {
  if (bannerInterval) clearInterval(bannerInterval);
});
</script>

<style scoped>
.banner-slide-left-enter-active,
.banner-slide-left-leave-active,
.banner-slide-right-enter-active,
.banner-slide-right-leave-active {
  transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}
.banner-slide-left-enter-from {
  transform: translateX(100%);
}
.banner-slide-left-leave-to {
  transform: translateX(-100%);
}
.banner-slide-right-enter-from {
  transform: translateX(-100%);
}
.banner-slide-right-leave-to {
  transform: translateX(100%);
}

.slide-left-enter-active,
.slide-left-leave-active {
  transition: all 0.4s ease-in-out;
}
.slide-left-enter-from {
  opacity: 0;
  transform: translateX(40px);
}
.slide-left-leave-to {
  opacity: 0;
  transform: translateX(-40px);
}

.slide-right-enter-active,
.slide-right-leave-active {
  transition: all 0.4s ease-in-out;
}
.slide-right-enter-from {
  opacity: 0;
  transform: translateX(-40px);
}
.slide-right-leave-to {
  opacity: 0;
  transform: translateX(40px);
}
</style>
