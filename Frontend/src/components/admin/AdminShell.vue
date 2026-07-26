<template>
  <div class="retail-shell min-h-screen text-zinc-900">
    <header
      class="sticky top-0 z-40 border-b border-zinc-200 bg-white/95 backdrop-blur-md"
    >
      <div
        class="mx-auto flex w-full items-center justify-between px-4 py-3 md:px-6"
      >
        <div class="flex items-center gap-3">
          <div
            class="flex size-10 items-center justify-center rounded-xl border border-zinc-200 bg-white text-zinc-900 shadow-sm"
          >
            <span class="material-symbols-outlined text-xl"
              >admin_panel_settings</span
            >
          </div>
          <div>
            <p class="text-[11px] uppercase tracking-[0.2em] text-zinc-500">
              SizeBy
            </p>
            <h1 class="text-lg font-bold leading-none text-zinc-900">
              Bảng quản trị
            </h1>
          </div>
        </div>

        <div class="flex items-center gap-3">
          <p class="hidden text-sm text-zinc-600 md:block">
            Xin chào,
            <span class="font-semibold text-zinc-900">{{
              currentUserName
            }}</span>
          </p>

          <button
            type="button"
            class="inline-flex items-center gap-2 rounded-xl border border-zinc-200 bg-white px-3 py-2 text-sm font-medium transition-colors hover:border-red-300 hover:bg-red-50 hover:text-red-600"
            @click="handleLogout"
          >
            <span class="material-symbols-outlined text-[18px]">logout</span>
            Đăng xuất
          </button>
        </div>
      </div>
    </header>

    <div class="flex w-full gap-6 px-4 py-6 md:px-6">
      <aside class="hidden w-72 shrink-0 lg:block">
        <div class="retail-card p-3">
          <nav class="grid gap-1">
            <router-link
              v-for="item in navItems"
              :key="item.path"
              :to="item.path"
              class="flex items-center gap-3 rounded-xl px-4 py-3 text-sm font-medium transition-colors"
              :class="
                isActive(item.path)
                  ? 'border border-zinc-300 bg-white text-zinc-900 shadow-sm'
                  : 'text-zinc-700 hover:bg-zinc-50 hover:text-zinc-900'
              "
            >
              <span class="material-symbols-outlined text-[20px]">{{
                item.icon
              }}</span>
              <span>{{ item.label }}</span>
            </router-link>

            <router-link
              to="/"
              class="flex items-center gap-3 rounded-xl px-4 py-3 text-sm font-medium text-zinc-700 transition-colors hover:bg-zinc-50 hover:text-zinc-900"
            >
              <span class="material-symbols-outlined text-[20px]"
                >storefront</span
              >
              <span>Về Trang chủ</span>
            </router-link>
          </nav>
        </div>
      </aside>

      <section class="min-w-0 flex-1 space-y-6">
        <div class="overflow-x-auto lg:hidden">
          <div class="flex gap-2">
            <router-link
              v-for="item in navItems"
              :key="`mobile-${item.path}`"
              :to="item.path"
              class="inline-flex items-center gap-2 whitespace-nowrap rounded-full px-4 py-2 text-xs font-semibold transition-colors"
              :class="
                isActive(item.path)
                  ? 'border border-zinc-300 bg-white text-zinc-900 shadow-sm'
                  : 'border border-zinc-200 bg-white text-zinc-700'
              "
            >
              <span class="material-symbols-outlined text-[16px]">{{
                item.icon
              }}</span>
              <span>{{ item.label }}</span>
            </router-link>

            <router-link
              to="/"
              class="inline-flex items-center gap-2 whitespace-nowrap rounded-full border border-zinc-200 bg-white px-4 py-2 text-xs font-semibold text-zinc-700 transition-colors hover:bg-zinc-50"
            >
              <span class="material-symbols-outlined text-[16px]"
                >storefront</span
              >
              <span>Về cửa hàng</span>
            </router-link>
          </div>
        </div>

        <header v-if="$slots.header" class="retail-card p-5 md:p-6">
          <slot name="header" />
        </header>

        <slot />
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();

const navItems = [
  { path: "/admin", label: "Tổng quan", icon: "dashboard" },
  { path: "/admin/pos", label: "Bán hàng POS", icon: "point_of_sale" },
  { path: "/admin/orders", label: "Đơn hàng", icon: "shopping_bag" },
  { path: "/admin/products", label: "Sản phẩm", icon: "inventory_2" },
  { path: "/admin/inventory", label: "Kho hàng", icon: "warehouse" },
  { path: "/admin/attributes", label: "Thuộc tính", icon: "palette" },
  { path: "/admin/brands", label: "Thương hiệu", icon: "branding_watermark" },
  { path: "/admin/customers", label: "Khách hàng", icon: "group" },
  { path: "/admin/vouchers", label: "Voucher", icon: "local_activity" },
  { path: "/admin/promotions", label: "Khuyến mãi", icon: "campaign" },
];

const currentUserName = computed(() => {
  try {
    const raw = localStorage.getItem("user");
    if (!raw) return "Admin";
    const parsed = JSON.parse(raw);
    return parsed.username || parsed.gmail || "Admin";
  } catch {
    return "Admin";
  }
});

function isActive(path) {
  if (path === "/admin") {
    return route.path === "/admin";
  }
  return route.path.startsWith(path);
}

function handleLogout() {
  localStorage.removeItem("user");
  localStorage.removeItem("userRole");
  localStorage.removeItem("token");
  router.push({ name: "login" });
}
</script>
