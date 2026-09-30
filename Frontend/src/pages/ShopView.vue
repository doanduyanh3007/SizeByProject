<template>
  <div
    class="retail-shell min-h-screen flex flex-col overflow-x-hidden font-display"
  >
    <MainTopBar />

    <div class="layout-container flex h-full grow flex-col">
      <div
        class="flex flex-1 justify-center py-5 px-4 md:px-8 lg:px-12 xl:px-40"
      >
        <div
          class="layout-content-container flex flex-col max-w-[1280px] flex-1"
        >
          <!-- Breadcrumbs -->
          <div class="flex flex-wrap gap-2 px-4 py-2">
            <router-link
              class="text-slate-500 dark:text-[#b9aa9a] text-sm font-medium leading-normal hover:text-primary transition-colors"
              to="/"
              >Trang chủ</router-link
            >
            <span
              class="text-slate-500 dark:text-[#b9aa9a] text-sm font-medium leading-normal"
              >/</span
            >
            <router-link
              class="text-slate-500 dark:text-[#b9aa9a] text-sm font-medium leading-normal hover:text-primary transition-colors"
              to="/shop"
              >Cửa hàng</router-link
            >
            <span
              class="text-slate-500 dark:text-[#b9aa9a] text-sm font-medium leading-normal"
              >/</span
            >
            <span
              class="text-slate-900 dark:text-white text-sm font-medium leading-normal"
              >Tất cả giày</span
            >
          </div>

          <!-- Header & Sort -->
          <div
            class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 p-4"
          >
            <div class="flex flex-col gap-2">
              <h1
                class="text-slate-900 dark:text-white text-3xl md:text-4xl font-black leading-tight tracking-[-0.033em]"
              >
                Tất cả sản phẩm giày
              </h1>
              <p
                class="text-slate-500 dark:text-[#b9aa9a] text-base font-normal leading-normal"
              >
                Tìm đôi giày hợp chân trong bộ sưu tập đang có tại cửa hàng.
              </p>
            </div>
            <div class="flex items-center gap-2">
              <span
                class="text-sm font-medium text-slate-500 dark:text-[#b9aa9a]"
                >Sắp xếp:</span
              >
              <select
                v-model="sortBy"
                class="rounded-xl border border-slate-900 bg-white px-3 py-2 text-sm font-medium text-slate-900 dark:border-[#3c342e] dark:bg-[#1f1a17] dark:text-white"
              >
                <option value="newest">Mới nhất</option>
                <option value="price-asc">Giá: thấp đến cao</option>
                <option value="price-desc">Giá: cao đến thấp</option>
                <option value="name">Tên A-Z</option>
              </select>
            </div>
          </div>

          <!-- Main Content Layout -->
          <div class="flex flex-col lg:flex-row gap-6 p-4">
            <!-- Bộ lọc Sidebar -->
            <aside class="w-full lg:w-64 flex-shrink-0">
              <!-- Áp dụng khung retail-card đồng bộ với admin -->
              <div
                class="retail-card flex flex-col gap-6 border border-slate-200 bg-white dark:border-[#3c342e] dark:bg-[#1f1a17] rounded-xl p-5 h-fit sticky top-24 shadow-sm"
              >
                <div class="flex items-center justify-between">
                  <h3 class="text-lg font-bold text-slate-900 dark:text-white">
                    Bộ lọc
                  </h3>
                  <button
                    class="text-slate-900 dark:text-white text-sm font-medium underline"
                    @click="clearAllFilters"
                  >
                    Xóa hết
                  </button>
                </div>

                <!-- Danh mục -->
                <div class="flex flex-col gap-3">
                  <button class="flex items-center justify-between w-full">
                    <span class="font-bold text-slate-900 dark:text-white"
                      >Danh mục</span
                    >
                    <span
                      class="material-symbols-outlined text-slate-900 dark:text-white text-sm"
                      >expand_less</span
                    >
                  </button>
                  <div class="flex flex-col gap-2 pl-1">
                    <label
                      v-for="category in filterCategories"
                      :key="category.id"
                      class="flex items-center gap-3 cursor-pointer"
                    >
                      <input
                        :checked="selectedCategories.includes(category.id)"
                        class="w-4 h-4 rounded border-slate-900 text-primary focus:ring-primary bg-slate-100 dark:bg-[#2b241f] dark:border-[#3c342e]"
                        type="checkbox"
                        @change="
                          toggleCategoryFilter(
                            category.id,
                            $event.target.checked,
                          )
                        "
                      />
                      <span
                        class="text-slate-900 dark:text-white text-sm font-medium"
                      >
                        {{ normalizeFilterLabel(category.name) }}
                      </span>
                    </label>
                  </div>
                </div>

                <!-- Thương hiệu -->
                <div class="flex flex-col gap-3">
                  <button class="flex items-center justify-between w-full">
                    <span class="font-bold text-slate-900 dark:text-white"
                      >Thương hiệu</span
                    >
                    <span
                      class="material-symbols-outlined text-slate-900 dark:text-white text-sm"
                      >expand_less</span
                    >
                  </button>
                  <div class="flex flex-col gap-2 pl-1">
                    <label
                      v-for="brand in filterBrands"
                      :key="brand.id"
                      class="flex items-center gap-3 cursor-pointer"
                    >
                      <input
                        :checked="selectedBrands.includes(brand.id)"
                        class="w-4 h-4 rounded border-slate-900 text-primary focus:ring-primary bg-slate-100 dark:bg-[#2b241f] dark:border-[#3c342e]"
                        type="checkbox"
                        @change="
                          toggleBrandFilter(brand.id, $event.target.checked)
                        "
                      />
                      <span
                        class="text-slate-900 dark:text-white text-sm font-medium"
                      >
                        {{ normalizeFilterLabel(brand.name) }}
                      </span>
                    </label>
                  </div>
                </div>
              </div>
            </aside>

            <!-- Danh sách sản phẩm -->
            <main class="flex-1 flex flex-col gap-6">
              <!-- Bộ lọc đang dùng -->
              <div class="flex gap-3 flex-wrap items-center">
                <span class="text-sm text-slate-500 dark:text-[#b9aa9a]"
                  >Bộ lọc đang dùng:</span
                >
                <template
                  v-for="categoryIdId in selectedCategories"
                  :key="`cat-${categoryIdId}`"
                >
                  <div
                    class="flex h-8 shrink-0 items-center justify-center gap-x-2 rounded-full bg-slate-200 dark:bg-[#2b241f] pl-3 pr-2 transition-colors hover:bg-slate-300 dark:hover:bg-[#3c342e] cursor-pointer group"
                  >
                    <p
                      class="text-slate-900 dark:text-white text-xs font-medium leading-normal"
                    >
                      {{ getCategoryName(categoryIdId) }}
                    </p>
                    <span
                      class="material-symbols-outlined text-slate-500 group-hover:text-red-500 text-[16px] cursor-pointer"
                      @click="removeCategory(categoryIdId)"
                      >close</span
                    >
                  </div>
                </template>
                <template
                  v-for="brandIdId in selectedBrands"
                  :key="`brand-${brandIdId}`"
                >
                  <div
                    class="flex h-8 shrink-0 items-center justify-center gap-x-2 rounded-full bg-slate-200 dark:bg-[#2b241f] pl-3 pr-2 transition-colors hover:bg-slate-300 dark:hover:bg-[#3c342e] cursor-pointer group"
                  >
                    <p
                      class="text-slate-900 dark:text-white text-xs font-medium leading-normal"
                    >
                      {{ getBrandName(brandIdId) }}
                    </p>
                    <span
                      class="material-symbols-outlined text-slate-500 group-hover:text-red-500 text-[16px] cursor-pointer"
                      @click="removeBrand(brandIdId)"
                      >close</span
                    >
                  </div>
                </template>
                <button
                  v-if="
                    selectedCategories.length > 0 || selectedBrands.length > 0
                  "
                  class="text-xs text-primary font-medium hover:underline ml-auto"
                  @click="clearAllFilters"
                >
                  Bỏ bộ lọc
                </button>
              </div>

              <!-- Grid sản phẩm -->
              <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                <!-- Áp dụng khung retail-card đồng bộ với admin cho từng sản phẩm -->
                <div
                  v-for="product in paginatedProducts"
                  :key="product.id"
                  class="retail-card group flex flex-col border border-slate-200 bg-white dark:border-[#3c342e] dark:bg-[#1f1a17] rounded-xl overflow-hidden transition-all duration-300 hover:border-primary/50 hover:shadow-lg hover:shadow-primary/10"
                >
                  <!-- Image section -->
                  <div
                    class="relative aspect-[4/3] bg-slate-100 dark:bg-[#181310] overflow-hidden"
                  >
                    <div
                      class="absolute top-3 right-3 z-10 flex flex-col gap-2"
                    >
                      <button
                        class="flex items-center justify-center w-8 h-8 rounded-full bg-white text-black transition-colors"
                        :class="
                          favoritesStore.isFavorite(product.id)
                            ? '!text-pink-500'
                            : ''
                        "
                        :title="
                          favoritesStore.isFavorite(product.id)
                            ? 'Bỏ khỏi yêu thích'
                            : 'Thêm vào yêu thích'
                        "
                        type="button"
                        @click.stop="favoritesStore.toggle(product.id)"
                      >
                        <span class="material-symbols-outlined text-[20px]"
                          >favorite</span
                        >
                      </button>
                      <button
                        class="flex items-center justify-center w-8 h-8 rounded-full bg-white/10 backdrop-blur-sm text-white hover:bg-white hover:text-primary transition-colors"
                        :class="
                          compareStore.has(product.id)
                            ? 'bg-white text-primary'
                            : ''
                        "
                        :title="
                          compareStore.has(product.id)
                            ? 'Đã thêm, mở bảng so sánh'
                            : 'Thêm vào so sánh'
                        "
                        type="button"
                        @click.stop="toggleCompare(product.id)"
                      >
                        <span class="material-symbols-outlined text-[20px]"
                          >compare_arrows</span
                        >
                      </button>
                    </div>
                    <div class="absolute top-3 left-3 z-10 flex flex-col gap-1">
                      <span
                        class="bg-green-600/90 text-white text-[10px] font-bold px-2 py-1 rounded uppercase tracking-wider"
                      >
                        {{ getSellingVariantCount(product) }} đang bán
                      </span>
                      <span
                        v-if="getUnavailableVariantCount(product) > 0"
                        class="bg-red-600/90 text-white text-[10px] font-bold px-2 py-1 rounded uppercase tracking-wider"
                      >
                        {{ getUnavailableVariantCount(product) }} hết/ngừng
                      </span>
                    </div>
                    <router-link :to="`/product/${product.id}`">
                      <img
                        :alt="product.name"
                        class="w-full h-full object-cover object-center group-hover:scale-105 transition-transform duration-500"
                        :src="getProductImage(product)"
                      />
                    </router-link>
                  </div>

                  <!-- Info section -->
                  <div class="p-4 flex flex-col flex-1">
                    <p
                      class="text-sm font-medium text-slate-500 dark:text-[#b9aa9a]"
                    >
                      {{ getBrandName(product.brandId) }}
                    </p>

                    <router-link
                      class="text-lg font-bold text-slate-900 dark:text-white mt-1 line-clamp-2"
                      :to="`/product/${product.id}`"
                      >{{ product.name }}</router-link
                    >
                    <p
                      v-if="isProductOutOfStock(product)"
                      class="mt-1 text-sm font-semibold text-red-600 dark:text-red-400"
                    >
                      Hết hàng
                    </p>
                    <p
                      v-else
                      class="mt-1 text-sm font-semibold text-emerald-600 dark:text-emerald-400"
                    >
                      Còn hàng
                    </p>

                    <div class="flex items-center gap-2 mt-2">
                      <template v-if="product.reviewCount > 0">
                        <div class="flex gap-0.5 text-yellow-500">
                          <span class="material-symbols-outlined text-[16px] fill-current">star</span>
                          <span class="text-sm font-semibold ml-1">{{ product.averageRating }}</span>
                        </div>
                        <span class="text-xs text-slate-500 dark:text-[#b9aa9a]">({{ product.reviewCount }})</span>
                      </template>
                      <template v-else>
                        <span class="text-xs text-slate-500 dark:text-[#b9aa9a]">Chưa có đánh giá</span>
                      </template>
                    </div>
                    <div class="mt-4 flex min-h-[52px] flex-wrap items-center gap-x-3 gap-y-1">
                      <template v-if="hasActivePromotion(product)">
                        <p class="text-sm font-semibold text-slate-400 line-through">
                          {{ formatProductPrice(getProductPrice(product)) }}
                        </p>
                        <p class="text-xl font-bold text-red-600 dark:text-red-400">
                          {{ formatProductPrice(getDiscountedPrice(product)) }}
                        </p>
                        <span class="rounded-full bg-red-100 px-2 py-1 text-[10px] font-bold text-red-600">
                          -{{ getPromotionPercent(product) }}%
                        </span>
                      </template>
                      <p
                        v-else
                        class="text-xl font-bold text-slate-900 dark:text-white"
                      >
                        {{ formatProductPrice(getProductPrice(product)) }}
                      </p>
                    </div>
                    <!-- Size pills -->
                    <div v-if="getProductSizes(product).length > 0" class="mt-2 flex flex-wrap gap-1">
                      <span
                        v-for="sz in getProductSizes(product)"
                        :key="sz.name"
                        class="inline-block rounded-full border px-2 py-0.5 text-[11px] font-semibold leading-tight"
                        :class="sz.inStock
                          ? 'border-slate-300 bg-white text-slate-900 dark:border-[#3c342e] dark:bg-[#1f1a17] dark:text-white'
                          : 'border-slate-200 bg-slate-100 text-slate-400 line-through dark:border-[#3c342e] dark:bg-[#2b241f] dark:text-slate-500'"
                      >{{ sz.name }}</span>
                    </div>
                    <p
                      class="mt-2 text-sm font-medium text-slate-600 dark:text-[#b9aa9a]"
                    >
                      Số lượng:
                      <span
                        :class="
                          isProductOutOfStock(product)
                            ? 'text-red-600 dark:text-red-400'
                            : 'text-green-600 dark:text-green-400'
                        "
                      >
                        {{ getProductStockDisplay(product) }}
                      </span>
                    </p>
                    <router-link
                      :to="`/product/${product.id}`"
                      class="mt-4 w-full bg-primary hover:bg-primary-hover text-white font-medium py-2 px-4 rounded-lg flex items-center justify-center gap-2 transition-colors"
                    >
                      <span class="material-symbols-outlined text-[20px]"
                        >shopping_cart</span
                      >
                      Xem chi tiết
                    </router-link>
                  </div>
                </div>

                <div
                  v-if="paginatedProducts.length === 0"
                  class="col-span-full py-12 text-center"
                >
                  <p class="text-slate-500 dark:text-[#b9aa9a] text-lg">
                    Không tìm thấy sản phẩm phù hợp với bộ lọc hiện tại.
                  </p>
                </div>
              </div>

              <!-- Pagination -->
              <div class="flex justify-center mt-8">
                <nav class="flex items-center gap-1">
                  <button
                    class="flex items-center justify-center w-10 h-10 rounded-lg border border-slate-200 bg-white dark:border-[#3c342e] dark:bg-[#1f1a17] text-slate-500 dark:text-[#b9aa9a] hover:bg-slate-100 dark:hover:bg-[#2b241f] disabled:opacity-50"
                    :disabled="currentPage === 1"
                    @click="previousPage"
                  >
                    <span class="material-symbols-outlined">chevron_left</span>
                  </button>
                  <template v-for="page in visiblePages" :key="page">
                    <button
                      v-if="page === '...'"
                      class="px-2 text-slate-500 dark:text-[#b9aa9a]"
                      disabled
                    >
                      ...
                    </button>
                    <button
                      v-else
                      :class="[
                        'flex items-center justify-center w-10 h-10 rounded-lg font-medium',
                        page === currentPage
                          ? 'bg-primary text-white'
                          : 'border border-slate-200 bg-white dark:border-[#3c342e] dark:bg-[#1f1a17] text-slate-500 dark:text-[#b9aa9a] hover:bg-slate-100 dark:hover:bg-[#2b241f]',
                      ]"
                      @click="goToPage(page)"
                    >
                      {{ page }}
                    </button>
                  </template>
                  <button
                    class="flex items-center justify-center w-10 h-10 rounded-lg border border-slate-200 bg-white dark:border-[#3c342e] dark:bg-[#1f1a17] text-slate-500 dark:text-[#b9aa9a] hover:bg-slate-100 dark:hover:bg-[#2b241f] disabled:opacity-50"
                    :disabled="currentPage === totalPages"
                    @click="nextPage"
                  >
                    <span class="material-symbols-outlined">chevron_right</span>
                  </button>
                </nav>
              </div>
            </main>
          </div>
        </div>
      </div>
    </div>

    <!-- Toast Notification -->
    <transition name="slide-up">
      <div
        v-if="toastMessage"
        :class="[
          'fixed bottom-6 left-1/2 -translate-x-1/2 z-[200] flex items-center gap-3 px-5 py-3 rounded-2xl shadow-2xl text-white font-semibold text-sm',
          toastType === 'success' ? 'bg-green-600' : 'bg-red-500'
        ]"
      >
        <span class="material-symbols-outlined text-[22px]">
          {{ toastType === 'success' ? 'check_circle' : 'error' }}
        </span>
        {{ toastMessage }}
      </div>
    </transition>
  </div>
</template>

<script setup>
import { getSession } from "@/utils/auth";
import { computed, onMounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useCompareStore } from "@/stores/compare";
import { useFavoritesStore } from "@/stores/favorites";
import { useProductStore } from "@/stores/products";
import { useCartStore } from "@/stores/cart";
import { getProductImageUrl } from "@/utils/productImages";
import axios from "axios";

const productStore = useProductStore();
const cartStore = useCartStore();
const favoritesStore = useFavoritesStore();
const compareStore = useCompareStore();
const router = useRouter();
const route = useRoute();
const currentUser = ref(null);
const activePromotions = ref([]);
const toastMessage = ref('');
const toastType = ref('success'); // 'success' | 'error'
let toastTimer = null;

function showToast(msg, type = 'success') {
  toastMessage.value = msg;
  toastType.value = type;
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => { toastMessage.value = ''; }, 3000);
}

onMounted(() => {
  const userData = getSession("user");
  if (userData) {
    currentUser.value = typeof userData === 'string' ? JSON.parse(userData) : userData;
  }
});

const PRODUCTS_PER_PAGE = 9;

const selectedCategories = ref([]);
const selectedBrands = ref([]);
const searchQuery = ref("");
const sortBy = ref("newest");
const currentPage = ref(1);

function normalizeQueryIds(value) {
  const values = Array.isArray(value)
    ? value
    : value !== undefined
      ? [value]
      : [];
  return [
    ...new Set(values.map((item) => Number(item)).filter(Number.isFinite)),
  ];
}

function areIdListsEqual(left, right) {
  if (left.length !== right.length) return false;
  return left.every((item, index) => item === right[index]);
}

function syncRouteQueryFilters(
  categories = selectedCategories.value,
  brands = selectedBrands.value,
) {
  const normalizedCategories = [
    ...new Set(categories.map(Number).filter(Number.isFinite)),
  ];
  const normalizedBrands = [
    ...new Set(brands.map(Number).filter(Number.isFinite)),
  ];
  const currentCategoryIds = normalizeQueryIds(route.query.categoryId);
  const currentBrandIds = normalizeQueryIds(route.query.brandId);

  if (
    areIdListsEqual(currentCategoryIds, normalizedCategories) &&
    areIdListsEqual(currentBrandIds, normalizedBrands)
  ) {
    return;
  }

  const nextQuery = { ...route.query };

  if (normalizedCategories.length > 0) {
    nextQuery.categoryId = normalizedCategories.map(String);
  } else {
    delete nextQuery.categoryId;
  }

  if (normalizedBrands.length > 0) {
    nextQuery.brandId = normalizedBrands.map(String);
  } else {
    delete nextQuery.brandId;
  }

  router.replace({ query: nextQuery });
}

function applyRouteFilters() {
  const categoryIds = normalizeQueryIds(route.query.categoryId);
  const brandIds = normalizeQueryIds(route.query.brandId);

  if (!areIdListsEqual(selectedCategories.value, categoryIds)) {
    selectedCategories.value = categoryIds;
  }

  if (!areIdListsEqual(selectedBrands.value, brandIds)) {
    selectedBrands.value = brandIds;
  }

  currentPage.value = 1;
}

const allFilteredProducts = computed(() => {
  const filtered = productStore.products.filter((product) => {
    const productVariants = getProductVariants(product);
    const hasVisibleVariant =
      productVariants.length === 0 ||
      productVariants.some((v) => normalizeVariantStatus(v) !== "HIDDEN");
    const isNotHidden = product.status !== "HIDDEN" && hasVisibleVariant;

    const matchCategory =
      selectedCategories.value.length === 0 ||
      selectedCategories.value.includes(product.categoryId);
    const matchBrand =
      selectedBrands.value.length === 0 ||
      selectedBrands.value.includes(product.brandId);
    const matchSearch =
      searchQuery.value === "" ||
      product.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      product.description
        ?.toLowerCase()
        .includes(searchQuery.value.toLowerCase());

    return isNotHidden && matchCategory && matchBrand && matchSearch;
  });

  filtered.sort((a, b) => {
    const stockOrder =
      Number(isProductOutOfStock(a)) - Number(isProductOutOfStock(b));
    if (stockOrder !== 0) return stockOrder;

    switch (sortBy.value) {
      case "price-asc": {
        const priceA =
          getProductPrice(a) === "N/A" ? Infinity : getDiscountedPrice(a);
        const priceB =
          getProductPrice(b) === "N/A" ? Infinity : getDiscountedPrice(b);
        return priceA - priceB;
      }
      case "price-desc": {
        const priceA = getProductPrice(a) === "N/A" ? 0 : getDiscountedPrice(a);
        const priceB = getProductPrice(b) === "N/A" ? 0 : getDiscountedPrice(b);
        return priceB - priceA;
      }
      case "name":
        return a.name.localeCompare(b.name);
      case "newest":
      default:
        return b.id - a.id;
    }
  });

  return filtered;
});

const totalPages = computed(() => {
  return Math.ceil(allFilteredProducts.value.length / PRODUCTS_PER_PAGE) || 1;
});

const paginatedProducts = computed(() => {
  const startIndex = (currentPage.value - 1) * PRODUCTS_PER_PAGE;
  const endIndex = startIndex + PRODUCTS_PER_PAGE;
  return allFilteredProducts.value.slice(startIndex, endIndex);
});

const visiblePages = computed(() => {
  const pages = [];
  const maxPagesToShow = 5;

  if (totalPages.value <= maxPagesToShow) {
    for (let i = 1; i <= totalPages.value; i++) {
      pages.push(i);
    }
  } else {
    pages.push(1);
    const startPage = Math.max(2, currentPage.value - 1);
    const endPage = Math.min(totalPages.value - 1, currentPage.value + 1);

    if (startPage > 2) {
      pages.push("...");
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }

    if (endPage < totalPages.value - 1) {
      pages.push("...");
    }

    if (totalPages.value > 1) {
      pages.push(totalPages.value);
    }
  }

  return pages;
});

function goToPage(page) {
  if (typeof page === "number" && page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
    window.scrollTo({ top: 0, behavior: "smooth" });
  }
}

function previousPage() {
  if (currentPage.value > 1) {
    goToPage(currentPage.value - 1);
  }
}

function nextPage() {
  if (currentPage.value < totalPages.value) {
    goToPage(currentPage.value + 1);
  }
}

function getCategoryName(categoryId) {
  const category = productStore.categories.find((c) => c.id === categoryId);
  return normalizeFilterLabel(category?.name) || "Danh mục khác";
}

function getBrandName(brandId) {
  const brand = productStore.brands.find((b) => b.id === brandId);
  return normalizeFilterLabel(brand?.name) || "Thương hiệu khác";
}

function normalizeFilterLabel(label) {
  if (!label) return "";
  return String(label)
    .replace(/-updated$/i, "")
    .trim();
}

function dedupeByNormalizedName(items) {
  const map = new Map();
  for (const item of items || []) {
    if (!item) continue;
    const key = normalizeFilterLabel(item.name).toLowerCase();
    if (!key) continue;

    const existing = map.get(key);
    if (!existing) {
      map.set(key, item);
      continue;
    }
    const existingIsUpdated = /-updated$/i.test(existing.name || "");
    const currentIsUpdated = /-updated$/i.test(item.name || "");
    if (existingIsUpdated && !currentIsUpdated) {
      map.set(key, item);
    }
  }
  return [...map.values()];
}

const filterCategories = computed(() =>
  dedupeByNormalizedName(productStore.categories),
);
const filterBrands = computed(() =>
  dedupeByNormalizedName(productStore.brands),
);

function getLowestVariantPrice(productId, { sellableOnly = false } = {}) {
  const normalizedProductId = Number(productId);
  if (!Number.isFinite(normalizedProductId)) return null;

  const related = productStore.variants.filter((v) => {
    if (Number(v.productId) !== normalizedProductId) return false;
    if (sellableOnly) return isVariantSellable(v);
    return normalizeVariantStatus(v) !== "HIDDEN";
  });
  let minPrice = null;

  for (const variant of related) {
    const price = Number(variant?.price);
    if (!Number.isFinite(price) || price < 0) continue;
    if (minPrice === null || price < minPrice) {
      minPrice = price;
    }
  }

  return minPrice;
}

function getProductPrice(product) {
  const stock = getProductStock(product);
  if (stock === null) return "Sắp ra mắt";
  
  const lowestVariantPrice = getLowestVariantPrice(product?.id);
  if (lowestVariantPrice !== null) {
    return lowestVariantPrice;
  }

  const basePrice = Number(product?.price);
  if (Number.isFinite(basePrice) && basePrice >= 0) {
    return basePrice;
  }

  return "N/A";
}

function getPromotionPercent(product) {
  const productId = Number(product?.id);
  if (!Number.isFinite(productId)) return 0;

  return activePromotions.value.reduce((best, promotion) => {
    const ids = promotion.productIds ||
      promotion.products?.map((item) => item.id) ||
      [];
    if (!ids.some((id) => Number(id) === productId)) return best;
    return Math.max(best, Number(promotion.discountPercent || 0));
  }, 0);
}

function hasActivePromotion(product) {
  return getPromotionPercent(product) > 0 && getProductPrice(product) !== "N/A";
}

function getDiscountedPrice(product) {
  const price = Number(getProductPrice(product));
  const percent = getPromotionPercent(product);
  if (!Number.isFinite(price) || percent <= 0) return price;
  return Math.max(0, Math.round(price * (100 - percent) / 100));
}

function formatProductPrice(value) {
  if (value === "Sắp ra mắt") return value;
  const price = Number(value);
  if (!Number.isFinite(price)) return "N/A";
  return `${new Intl.NumberFormat("vi-VN").format(price)} đ`;
}

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

function getProductStock(product) {
  const productId = Number(product && product.id);
  if (!Number.isFinite(productId)) return null;

  const related = productStore.variants.filter(
    (v) => Number(v.productId) === productId,
  );
  if (related.length === 0) return null;

  return related.reduce((sum, variant) => {
    if (!isVariantSellable(variant)) return sum;
    const qty = Number(
      variant &&
        (variant.stockQuantity ??
          variant.stock_quantity ??
          variant.quantity ??
          0),
    );
    return sum + (Number.isFinite(qty) ? qty : 0);
  }, 0);
}

function getProductStockDisplay(product) {
  const stock = getProductStock(product);
  if (stock === null) return "Không rõ";
  return new Intl.NumberFormat("vi-VN").format(Math.max(0, stock));
}

function isProductOutOfStock(product) {
  const stock = getProductStock(product);
  return stock === null || stock <= 0;
}

function normalizeVariantStatus(variant) {
  const rawStock =
    variant &&
    (variant.stockQuantity ?? variant.stock_quantity ?? variant.quantity);
  const hasKnownStock =
    rawStock !== null && rawStock !== undefined && rawStock !== "";
  const stock = Number(rawStock);
  const raw = String(
    (variant &&
      (variant.status || variant.variantStatus || variant.variant_status)) ||
      "",
  )
    .trim()
    .toUpperCase();

  if (["HIDDEN", "INACTIVE", "DISCONTINUED", "STOP_SELLING"].includes(raw))
    return "HIDDEN";
  if (["OUT_OF_STOCK", "SOLD_OUT", "OUTOFSTOCK"].includes(raw))
    return "OUT_OF_STOCK";
  return !hasKnownStock || (Number.isFinite(stock) && stock > 0)
    ? "SELLING"
    : "OUT_OF_STOCK";
}

function isVariantSellable(variant) {
  return normalizeVariantStatus(variant) === "SELLING";
}

function getProductVariants(product) {
  const productId = Number(product && product.id);
  if (!Number.isFinite(productId)) return [];
  return productStore.variants.filter((v) => Number(v.productId) === productId);
}

function getSellingVariantCount(product) {
  return getProductVariants(product).filter(isVariantSellable).length;
}

function getUnavailableVariantCount(product) {
  return getProductVariants(product).filter(
    (v) => normalizeVariantStatus(v) !== "SELLING",
  ).length;
}

function getProductImage(product) {
  return getProductImageUrl({
    product,
    variants: productStore.variants,
    width: 400,
    height: 300,
  });
}

function getProductSizes(product) {
  const variants = getProductVariants(product);
  if (variants.length === 0) return [];
  const sizeMap = new Map();
  for (const v of variants) {
    const name = v.sizeName || v.size_name || v.size?.name;
    if (!name) continue;
    const status = normalizeVariantStatus(v);
    if (status === "HIDDEN") continue;
    const qty = Number(v.stockQuantity ?? v.stock_quantity ?? v.quantity ?? 0);
    const existing = sizeMap.get(name);
    if (existing) {
      if (status === "SELLING" && qty > 0) existing.inStock = true;
    } else {
      sizeMap.set(name, { name, inStock: status === "SELLING" && qty > 0 });
    }
  }
  // Sort sizes numerically if possible
  return [...sizeMap.values()].sort((a, b) => {
    const na = parseFloat(a.name);
    const nb = parseFloat(b.name);
    if (Number.isFinite(na) && Number.isFinite(nb)) return na - nb;
    return a.name.localeCompare(b.name);
  });
}

function toggleCategoryFilter(categoryId, isChecked) {
  const normalizedId = Number(categoryId);
  if (!Number.isFinite(normalizedId)) return;

  selectedCategories.value = isChecked
    ? [...new Set([...selectedCategories.value, normalizedId])]
    : selectedCategories.value.filter((id) => id !== normalizedId);
  currentPage.value = 1;
  syncRouteQueryFilters(selectedCategories.value, selectedBrands.value);
}

function toggleBrandFilter(brandId, isChecked) {
  const normalizedId = Number(brandId);
  if (!Number.isFinite(normalizedId)) return;

  selectedBrands.value = isChecked
    ? [...new Set([...selectedBrands.value, normalizedId])]
    : selectedBrands.value.filter((id) => id !== normalizedId);
  currentPage.value = 1;
  syncRouteQueryFilters(selectedCategories.value, selectedBrands.value);
}

function removeCategory(categoryId) {
  selectedCategories.value = selectedCategories.value.filter(
    (id) => id !== categoryId,
  );
  currentPage.value = 1;
  syncRouteQueryFilters(selectedCategories.value, selectedBrands.value);
}

function removeBrand(brandId) {
  selectedBrands.value = selectedBrands.value.filter((id) => id !== brandId);
  currentPage.value = 1;
  syncRouteQueryFilters(selectedCategories.value, selectedBrands.value);
}

function clearAllFilters() {
  selectedCategories.value = [];
  selectedBrands.value = [];
  searchQuery.value = "";
  sortBy.value = "newest";
  currentPage.value = 1;
  syncRouteQueryFilters([], []);
}

onMounted(() => {
  if (productStore.products.length === 0) {
    productStore.fetchProducts();
  }
  if (productStore.variants.length === 0) {
    productStore.fetchVariants();
  }
  loadActivePromotions();

  applyRouteFilters();
});

watch(
  () => [route.query.categoryId, route.query.brandId],
  () => {
    applyRouteFilters();
  },
);

async function handleQuickAdd(product) {
  if (!product) return;

  // Lấy user từ session (hỗ trợ cả sessionStorage & localStorage)
  if (!currentUser.value) {
    const userData = getSession("user");
    if (userData) {
      currentUser.value = typeof userData === 'string' ? JSON.parse(userData) : userData;
    }
  }

  if (!currentUser.value) {
    showToast("Vui lòng đăng nhập để thêm vào giỏ hàng", "error");
    return;
  }

  // Lấy variants từ store nếu product.variants chưa có
  const storeVariants = product.variants && product.variants.length > 0
    ? product.variants
    : (productStore.variants || []).filter(v => Number(v.productId) === Number(product.id));

  if (!storeVariants || storeVariants.length === 0) {
    showToast("Vui lòng bấm 'Xem chi tiết' để chọn phân loại sản phẩm", "error");
    return;
  }
  const availableVariant = storeVariants.find(v => (v.stockQuantity || 0) > 0) || storeVariants[0];
  if (!availableVariant || (availableVariant.stockQuantity || 0) <= 0) {
    showToast("Sản phẩm đã hết hàng!", "error");
    return;
  }

  cartStore.accountId = currentUser.value.id;
  const added = await cartStore.addToCart(product, availableVariant, 1);
  if (added !== false) {
    showToast(`Đã thêm "${product.name || 'sản phẩm'}" vào giỏ hàng!`, "success");
  } else {
    showToast("Thêm vào giỏ thất bại, vui lòng thử lại.", "error");
  }
}

function toggleCompare(productId) {
  compareStore.add(productId);
  router.push("/compare");
}
</script>

<style scoped>
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}
.slide-up-enter-from {
  opacity: 0;
  transform: translate(-50%, 20px);
}
.slide-up-leave-to {
  opacity: 0;
  transform: translate(-50%, 20px);
}
</style>
