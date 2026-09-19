const fs = require('fs');

const code = \<template>
  <div class="bg-background-light dark:bg-background-dark text-black dark:text-white font-display antialiased selection:bg-primary selection:text-white">
    <div class="relative flex h-auto min-h-screen w-full flex-col overflow-x-hidden">

      <MainTopBar />

      <main class="flex-1 flex flex-col items-center py-10 px-4 md:px-10 lg:px-40">
        <div class="w-full max-w-[1200px] flex flex-col gap-8">
          <div class="flex flex-col gap-4 text-center md:text-left md:flex-row md:justify-between md:items-end">
            <div class="flex flex-col gap-2">
              <h1 class="text-slate-900 dark:text-white text-3xl md:text-4xl lg:text-5xl font-black leading-tight tracking-[-0.033em]">Sản phẩm yêu thích</h1>
              <p class="text-text-secondary text-base md:text-lg font-normal leading-normal max-w-2xl">Danh sách những sản phẩm bạn đã lưu lại. Bạn có thể xem chi tiết hoặc thêm vào giỏ hàng bất cứ lúc nào.</p>
            </div>
            <router-link class="text-primary hover:text-primary/80 font-medium text-sm flex items-center gap-1 self-center md:self-end" to="/shop">
              <span class="material-symbols-outlined text-lg">arrow_back</span>
              Tiếp tục mua sắm
            </router-link>
          </div>

          <div v-if="loading" class="flex justify-center items-center py-20">
            <span class="material-symbols-outlined animate-spin text-4xl text-primary">autorenew</span>
          </div>
          <div v-else-if="favoriteProducts.length === 0" class="flex flex-col items-center py-20 bg-white dark:bg-[#1f1a17] rounded-xl border border-slate-200 dark:border-[#3c342e]">
            <span class="material-symbols-outlined text-6xl text-slate-300 dark:text-slate-600 mb-4">favorite_border</span>
            <p class="text-lg font-medium text-slate-600 dark:text-slate-300">Danh sách yêu thích đang trống</p>
            <p class="text-sm text-slate-500 mt-2 mb-6 text-center max-w-md">Hãy duyệt qua cửa hàng và thả tim những sản phẩm bạn thích nhé.</p>
            <router-link to="/shop" class="px-6 py-3 bg-primary text-white rounded-lg font-medium hover:bg-primary/90 transition-colors">Đến Cửa Hàng</router-link>
          </div>
          <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            <div
              v-for="product in favoriteProducts"
              :key="product.id"
              class="retail-card group flex flex-col border border-slate-200 bg-white dark:border-[#3c342e] dark:bg-[#1f1a17] rounded-xl overflow-hidden transition-all duration-300 hover:border-primary/50 hover:shadow-lg hover:shadow-primary/10"
            >
              <div class="relative aspect-[4/3] bg-slate-100 dark:bg-[#181310] overflow-hidden">
                <div class="absolute top-3 right-3 z-10 flex flex-col gap-2">
                  <button
                    class="flex items-center justify-center w-8 h-8 rounded-full bg-white/10 backdrop-blur-sm text-white hover:bg-white hover:text-red-500 transition-colors bg-white text-red-500"
                    title="Bỏ khỏi yêu thích"
                    type="button"
                    @click.stop="favoritesStore.toggle(product.id); fetchFavorites()"
                  >
                    <span class="material-symbols-outlined text-[20px]">favorite</span>
                  </button>
                </div>
                <div class="absolute top-3 left-3 z-10 flex flex-col gap-1">
                  <span class="bg-green-600/90 text-white text-[10px] font-bold px-2 py-1 rounded uppercase tracking-wider">
                    {{ getSellingVariantCount(product) }} đang bán
                  </span>
                </div>
                <img
                  v-if="product.imageUrl"
                  :src="getProductImageUrl(product.imageUrl)"
                  alt=""
                  class="h-full w-full object-cover transition-transform duration-500 group-hover:scale-110"
                />
              </div>

              <div class="flex flex-1 flex-col p-5">
                <div class="mb-2">
                  <p class="text-[10px] uppercase tracking-wider text-slate-500 dark:text-slate-400 font-bold mb-1">
                    {{ getBrandName(product.brandId) }}
                  </p>
                  <router-link
                    :to="'/product/' + product.id"
                    class="text-lg font-bold text-slate-900 dark:text-white line-clamp-2 hover:text-primary transition-colors"
                  >
                    {{ product.name }}
                  </router-link>
                  <p class="text-xs text-slate-500 dark:text-slate-400 mt-1 line-clamp-1">
                    {{ product.description || 'Chưa có mô tả' }}
                  </p>
                </div>

                <div class="mt-auto flex items-center justify-between pt-4">
                  <div class="flex flex-col">
                    <p class="text-lg font-black text-slate-900 dark:text-white">
                      {{ formatCurrency(getMinPrice(product)) }}
                    </p>
                    <p class="text-xs text-slate-500 dark:text-slate-400">
                      Số lượng: <span class="font-bold text-primary">{{ getTotalQuantity(product) }}</span>
                    </p>
                  </div>
                  <router-link
                    :to="'/product/' + product.id"
                    class="flex items-center gap-2 rounded-lg bg-primary px-4 py-2 text-sm font-bold text-white transition-all hover:-translate-y-0.5 hover:shadow-lg hover:shadow-primary/30"
                  >
                    <span class="material-symbols-outlined text-[18px]">shopping_cart</span>
                    Xem
                  </router-link>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
      <AppFooter />
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useFavoritesStore } from "@/stores/favorites";
import { adminApi, brandsApi } from "@/services/api";
import { getProductImageUrl } from "@/utils/productImages";
import MainTopBar from "@/components/MainTopBar.vue";
import AppFooter from "@/components/AppFooter.vue";

const favoritesStore = useFavoritesStore();
const router = useRouter();
const route = useRoute();

const favoriteProducts = ref([]);
const brands = ref([]);
const loading = ref(true);

function getSellingVariantCount(product) {
  if (!product || !product.variants) return 0;
  return product.variants.filter((v) => v.status === "AVAILABLE").length;
}

function getBrandName(brandId) {
  const brand = brands.value.find((b) => Number(b.id) === Number(brandId));
  return brand ? brand.name : "N/A";
}

function getMinPrice(product) {
  if (!product || !product.variants || product.variants.length === 0) return 0;
  return Math.min(...product.variants.map((v) => v.price || 0));
}

function getTotalQuantity(product) {
  if (!product || !product.variants || product.variants.length === 0) return "Không rõ";
  return product.variants.reduce((sum, v) => sum + (v.stockQuantity || 0), 0);
}

function formatCurrency(value) {
  if (!value) return "0 đ";
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(value);
}

async function fetchFavorites() {
  loading.value = true;
  try {
    const ids = favoritesStore.favoriteIds;
    if (ids.length === 0) {
      favoriteProducts.value = [];
      return;
    }
    
    // Fetch all products and filter locally for simplicity, 
    // or fetch one by one if backend doesn't support batch get
    const allProducts = await adminApi.getProducts({ page: 0, size: 500 });
    const productList = Array.isArray(allProducts) ? allProducts : (allProducts.content || []);
    favoriteProducts.value = productList.filter(p => ids.includes(Number(p.id)));
  } catch (err) {
    console.error("Lỗi lấy danh sách sản phẩm:", err);
  } finally {
    loading.value = false;
  }
}

onMounted(async () => {
  try {
    const brandData = await brandsApi.getAll();
    brands.value = Array.isArray(brandData) ? brandData : (brandData.content || []);
  } catch (e) {
    console.error(e);
  }
  await fetchFavorites();
});
</script>
\

fs.writeFileSync('src/pages/WishlistView.vue', code);
console.log('WishlistView.vue created');
