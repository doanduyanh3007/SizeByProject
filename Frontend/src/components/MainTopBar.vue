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
            v-if="isAdmin"
            to="/admin"
            class="transition-colors hover:text-zinc-900"
          >
            Vào Quản Trị
          </router-link>
        </nav>
      </div>

      <div class="flex items-center gap-3">
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
import { computed, onBeforeUnmount, onMounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";

import { useCartStore } from "@/stores/cart";
import { resolveBackendAssetUrl } from "@/services/api";

const cartStore = useCartStore();
const route = useRoute();
const router = useRouter();

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
  if (!rawUrl || typeof rawUrl !== "string") {
    return "";
  }

  const fixedUrl = rawUrl.replace(
    /^https?:\/\/[^/]+\/?(https?:\/\/.+)$/i,
    "$1",
  );

  return resolveBackendAssetUrl(fixedUrl) || "";
}

const resolvedUserAvatar = computed(() => normalizeAvatarUrl(userAvatar.value));

function syncSession() {
  const rawUser = localStorage.getItem("user");
  const token = localStorage.getItem("token");

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
  localStorage.removeItem("user");
  localStorage.removeItem("token");
  showUserMenu.value = false;
  syncSession();
  router.push("/login");
}

onMounted(() => {
  syncSession();
  window.addEventListener("click", handleOutsideClick);
});

onBeforeUnmount(() => {
  window.removeEventListener("click", handleOutsideClick);
});

watch(
  () => route.fullPath,
  () => {
    showUserMenu.value = false;
    syncSession();
  },
);

const isAdmin = computed(() => {
  const role = localStorage.getItem("userRole");
  return role === "ADMIN";
});
</script>
