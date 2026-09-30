<template>
  <header class="retail-header">
    <div
      class="mx-auto flex w-full max-w-[1240px] items-center justify-between px-4 py-4 md:px-8 xl:px-10"
    >
      <div class="flex items-center gap-8">
        <router-link to="/" class="group flex items-center gap-3">
          <div
            class="flex size-10 items-center justify-center rounded-xl border border-zinc-200 bg-white text-zinc-900 shadow-sm transition-transform group-hover:scale-105"
          >
            <span class="material-symbols-outlined text-xl">steps</span>
          </div>
          <div>
            <p
              class="text-[11px] font-semibold uppercase tracking-[0.24em] text-zinc-500"
            >
              SizeBy
            </p>
            <h2
              class="font-headline text-[26px] font-bold leading-none tracking-tight text-zinc-900"
            >
              Sneaker Store
            </h2>
          </div>
        </router-link>

        <nav
          class="hidden items-center gap-7 text-sm font-medium text-zinc-600 xl:flex"
        >
          <router-link
            to="/shop"
            class="transition-colors hover:text-zinc-900"
            >Cửa Hàng</router-link
          >
          <router-link
            to="/vouchers"
            class="transition-colors hover:text-zinc-900"
            >Ưu đãi</router-link
          >
          <router-link
            to="/about"
            class="transition-colors hover:text-zinc-900"
            >Về SizeBy</router-link
          >

          <router-link
            v-if="isAdmin || isStaff"
            to="/admin"
            class="transition-colors hover:text-zinc-900"
          >
            Vào Quản Trị
          </router-link>
        </nav>
      </div>

      <div class="flex items-center gap-3">
        <!-- SEARCH BAR WITH DROPDOWN -->
        <div class="relative hidden sm:block" ref="searchContainerRef">
          <form @submit.prevent="onSearch" class="relative">
            <input
              v-model="searchQuery"
              @focus="showSearchDropdown = true"
              @input="showSearchDropdown = true"
              type="text"
              placeholder="Tìm kiếm sản phẩm..."
              class="h-10 w-56 rounded-full border border-zinc-200 bg-zinc-50 pl-4 pr-10 text-sm outline-none transition-colors focus:border-zinc-400 focus:bg-white focus:w-72 transition-[width]"
            />
            <button
              type="submit"
              class="absolute right-3 top-1/2 -translate-y-1/2 flex items-center justify-center text-zinc-400 hover:text-zinc-600"
            >
              <span class="material-symbols-outlined text-[18px]">search</span>
            </button>
          </form>

          <!-- SEARCH DROPDOWN -->
          <div
            v-if="showSearchDropdown && searchQuery && typeof searchQuery === 'string' && searchQuery.trim() && searchResults.length > 0"
            class="absolute top-12 left-0 w-full bg-white border border-zinc-200 rounded-2xl shadow-lg z-50 overflow-hidden max-h-80 overflow-y-auto"
          >
            <router-link
              v-for="product in searchResults"
              :key="product.id"
              :to="`/product/${product.id}`"
              class="flex items-center gap-3 p-3 hover:bg-zinc-50 transition-colors border-b border-zinc-100 last:border-0"
              @click="closeSearch"
            >
              <div class="h-12 w-12 bg-zinc-100 rounded-lg overflow-hidden flex-shrink-0">
                <img
                  v-if="product.image"
                  :src="product.image"
                  :alt="product.name"
                  class="w-full h-full object-cover"
                />
                <div
                  v-else
                  class="w-full h-full flex items-center justify-center text-zinc-400"
                >
                  <span class="material-symbols-outlined text-xl">image</span>
                </div>
              </div>
              <div class="flex-1 min-w-0 text-left">
                <p class="text-sm font-semibold text-zinc-800 truncate">{{ product.name }}</p>
                <p class="text-xs text-primary font-medium mt-0.5">{{ formatCurrency(product.price) }}</p>
              </div>
            </router-link>

            <!-- Xem tất cả kết quả -->
            <button
              class="w-full py-2.5 text-xs font-semibold text-primary hover:bg-zinc-50 text-center transition-colors"
              @click="onSearch"
            >
              Xem tất cả kết quả cho "{{ searchQuery }}"
            </button>
          </div>

          <!-- No results -->
          <div
            v-else-if="showSearchDropdown && searchQuery && typeof searchQuery === 'string' && searchQuery.trim() && searchResults.length === 0 && productsLoaded"
            class="absolute top-12 left-0 w-full bg-white border border-zinc-200 rounded-2xl shadow-lg z-50 p-4 text-center text-sm text-zinc-500"
          >
            Không tìm thấy sản phẩm nào
          </div>
        </div>

        <router-link
          to="/wishlist"
          class="flex size-10 items-center justify-center rounded-full bg-slate-100 transition-colors dark:bg-[#2b241f] relative"
        >
          <span
            class="material-symbols-outlined text-[20px] text-slate-700 transition-colors dark:text-slate-300"
            >favorite</span
          >
        </router-link>

        <router-link
          to="/cart"
          class="relative flex size-11 items-center justify-center rounded-full border border-zinc-200 bg-white text-zinc-700 transition-colors hover:border-zinc-400 hover:text-zinc-900"
        >
          <span class="material-symbols-outlined">shopping_bag</span>
          <span
            v-if="cartCount > 0"
            class="absolute -right-1 -top-1 flex size-5 items-center justify-center rounded-full border border-zinc-300 bg-white text-[10px] font-bold text-zinc-900"
          >
            {{ cartCount }}
          </span>
        </router-link>

        <div v-if="isLoggedIn" ref="userMenuRef" class="relative">
          <button
            class="flex h-11 items-center gap-3 rounded-full border border-zinc-200 bg-white pl-2 pr-4 text-left shadow-sm transition-colors hover:border-zinc-300"
            type="button"
            @click="toggleUserMenu"
          >
            <div
              class="flex size-8 items-center justify-center overflow-hidden rounded-full bg-zinc-100 text-xs font-bold uppercase text-zinc-700"
            >
              <img
                v-if="resolvedUserAvatar"
                :src="resolvedUserAvatar"
                :alt="userName"
                class="h-full w-full object-cover"
              />
              <span v-else>{{ userInitials }}</span>
            </div>
            <div class="hidden sm:block">
              <p class="text-xs text-zinc-500">Khách hàng thân thiết</p>
              <p class="text-sm font-semibold text-zinc-900">
                {{ userName }}
              </p>
            </div>
          </button>

          <div
            v-if="showUserMenu"
            class="absolute right-0 mt-3 w-64 rounded-2xl border border-zinc-200 bg-white p-3 shadow-card-warm"
          >
            <div class="rounded-xl bg-zinc-50 px-4 py-3">
              <p class="text-[11px] uppercase tracking-[0.22em] text-zinc-500">
                Tài khoản
              </p>
              <p class="mt-1 truncate text-sm font-semibold text-zinc-900">
                {{ userEmail || "Không có email" }}
              </p>
            </div>
            <div class="mt-3 grid gap-1 text-sm">
              <router-link
                to="/profile"
                class="rounded-xl px-4 py-3 font-medium hover:bg-zinc-50"
                @click="showUserMenu = false"
                >Hồ sơ của tôi</router-link
              >
              <router-link
                to="/orders"
                class="block rounded-xl px-4 py-2 text-sm text-zinc-700 hover:bg-zinc-50"
              >
                Đơn hàng của tôi
              </router-link>
              <router-link
                to="/warranty"
                class="block rounded-xl px-4 py-2 text-sm text-zinc-700 hover:bg-zinc-50"
              >
                Kiểm tra bảo hành
              </router-link>
              <button
                type="button"
                class="rounded-xl px-4 py-3 text-left font-medium text-red-600 hover:bg-red-50"
                @click="handleLogout"
              >
                Đăng xuất
              </button>
            </div>
          </div>
        </div>

        <router-link
          v-else
          to="/login"
          class="inline-flex items-center rounded-full border border-zinc-300 bg-white px-6 py-3 text-sm font-semibold text-zinc-900 transition-all hover:bg-zinc-50"
        >
          Đăng nhập
        </router-link>
      </div>
    </div>
  </header>
</template>

<script setup>
import { getSession, clearSession } from "@/utils/auth";
import { computed, onBeforeUnmount, onMounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";

import { useCartStore } from "@/stores/cart";
import { useProductStore } from "@/stores/products";
import { resolveBackendAssetUrl } from "@/services/api";

const cartStore = useCartStore();
const productStore = useProductStore();
const route = useRoute();
const router = useRouter();

// ===== SEARCH =====
const searchQuery = ref("");
const showSearchDropdown = ref(false);
const searchContainerRef = ref(null);
const productsLoaded = ref(false);

function getLowestPriceForProduct(productId) {
  const id = Number(productId);
  const variants = productStore.variants.filter(
    (v) => Number(v.productId) === id && v.status === 'SELLING'
  );
  let minPrice = null;
  for (const v of variants) {
    const price = Number(v.price);
    if (Number.isFinite(price) && price > 0) {
      if (minPrice === null || price < minPrice) minPrice = price;
    }
  }
  return minPrice;
}

const searchResults = computed(() => {
  if (!searchQuery.value || typeof searchQuery.value !== "string" || !searchQuery.value.trim()) return [];
  const query = searchQuery.value.toLowerCase().trim();
  return productStore.products
    .filter((p) => p.name && p.name.toLowerCase().includes(query))
    .slice(0, 6)
    .map((p) => {
      const variantPrice = getLowestPriceForProduct(p.id);
      let price = variantPrice; if (price === null) price = Number.isFinite(Number(p.price)) ? Number(p.price) : 0;
      return {
        id: p.id,
        name: p.name,
        price: Number.isFinite(price) ? price : 0,
        image: p.imageUrl || null,
      };
    });
});

function formatCurrency(value) {
  if (!value) return "0 ₫";
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(value);
}

function onSearch() {
  if (searchQuery.value && typeof searchQuery.value === "string" && searchQuery.value.trim()) {
    showSearchDropdown.value = false;
    router.push({ path: "/shop", query: { search: searchQuery.value.trim() } });
    searchQuery.value = "";
  }
}

function closeSearch() {
  showSearchDropdown.value = false;
  searchQuery.value = "";
}

function handleClickOutsideSearch(event) {
  if (
    searchContainerRef.value &&
    !searchContainerRef.value.contains(event.target)
  ) {
    showSearchDropdown.value = false;
  }
}

// ===== USER MENU =====
const showUserMenu = ref(false);
const isLoggedIn = ref(false);
const userName = ref("Khách SizeBy");
const userEmail = ref("");
const userAvatar = ref("");
const userMenuRef = ref(null);

const cartCount = computed(() => cartStore.cartItems.length);

const userInitials = computed(() => {
  if (!userName.value) return "PS";
  return userName.value
    .split(" ")
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0])
    .join("")
    .toUpperCase();
});

function normalizeAvatarUrl(rawUrl) {
  if (!rawUrl || typeof rawUrl !== "string") return "";
  const fixedUrl = rawUrl.replace(
    /^https?:\/\/[^/]+\/?(https?:\/.+)$/i,
    "$1",
  );
  return resolveBackendAssetUrl(fixedUrl) || "";
}

const resolvedUserAvatar = computed(() =>
  normalizeAvatarUrl(userAvatar.value),
);

function syncSession() {
  const rawUser = getSession("user");
  const token = getSession("token");

  if (!rawUser && !token) {
    isLoggedIn.value = false;
    userName.value = "Khách SizeBy";
    userEmail.value = "";
    userAvatar.value = "";
    return;
  }

  isLoggedIn.value = true;

  if (!rawUser) return;

  try {
    const user = JSON.parse(rawUser);
    userName.value = user.username || "Khách SizeBy";
    userEmail.value = user.gmail || user.email || "";
    userAvatar.value = user.imgUrl || "";
  } catch {
    userName.value = "Khách SizeBy";
    userEmail.value = "";
    userAvatar.value = "";
  }
}

function toggleUserMenu(event) {
  event.stopPropagation();
  if (!isLoggedIn.value) {
    router.push("/login");
    return;
  }
  showUserMenu.value = !showUserMenu.value;
}

function handleOutsideClick(event) {
  if (!showUserMenu.value) return;
  if (userMenuRef.value && !userMenuRef.value.contains(event.target)) {
    showUserMenu.value = false;
  }
}

function handleLogout() {
  clearSession();
  // Reset reactive state ngay lập tức, không chờ watcher
  isLoggedIn.value = false;
  showUserMenu.value = false;
  userName.value = "Khách SizeBy";
  userEmail.value = "";
  userAvatar.value = "";
  router.push("/login");
}

const isAdmin = computed(() => {
  try {
    const rawUser = getSession("user");
    if (!rawUser) return getSession("userRole") === "ADMIN";
    const parsed = JSON.parse(rawUser);
    return (parsed.role || getSession("userRole")) === "ADMIN";
  } catch {
    return false;
  }
});

const isStaff = computed(() => {
  try {
    const rawUser = getSession("user");
    if (!rawUser) return getSession("userRole") === "STAFF";
    const parsed = JSON.parse(rawUser);
    return (parsed.role || getSession("userRole")) === "STAFF";
  } catch {
    return false;
  }
});

// ===== LIFECYCLE =====
onMounted(async () => {
  syncSession();
  window.addEventListener("click", handleOutsideClick);
  document.addEventListener("click", handleClickOutsideSearch);
  try {
    const fetches = [];
    if (productStore.products.length === 0) fetches.push(productStore.fetchProducts());
    if (productStore.variants.length === 0) fetches.push(productStore.fetchVariants());
    if (fetches.length > 0) await Promise.all(fetches);
  } catch {
    // ignore
  } finally {
    productsLoaded.value = true;
  }
});

onBeforeUnmount(() => {
  window.removeEventListener("click", handleOutsideClick);
  document.removeEventListener("click", handleClickOutsideSearch);
});

watch(
  () => route.fullPath,
  () => {
    showUserMenu.value = false;
    syncSession();
  },
);
</script>
