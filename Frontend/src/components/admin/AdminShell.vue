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
          <!-- BELL NOTIFICATION -->
          <div class="relative mr-2" @click.stop>
            <div class="relative cursor-pointer flex items-center justify-center" @click="toggleNotificationDropdown" title="Thông báo đơn hàng mới">
              <span class="material-symbols-outlined text-zinc-800 transition-colors text-[24px]">notifications</span>
              <span v-if="newOrderCount > 0" class="absolute -top-1 -right-1 flex h-[16px] w-[16px] items-center justify-center rounded-full bg-red-500 text-[10px] font-bold text-white shadow-sm ring-2 ring-white">
                {{ newOrderCount > 9 ? '9+' : newOrderCount }}
              </span>
            </div>
            
            <!-- DROPDOWN -->
            <div v-if="showNotificationDropdown" class="absolute right-0 top-10 w-80 bg-white border border-zinc-200 rounded-xl shadow-lg z-50 overflow-hidden">
              <div class="px-4 py-3 border-b border-zinc-100 flex justify-between items-center bg-zinc-50">
                <h3 class="font-bold text-sm text-zinc-800">Thông báo mới</h3>
                <span v-if="newOrderCount > 0" class="text-xs text-primary font-medium cursor-pointer" @click="markAllRead">Đánh dấu đã đọc</span>
              </div>
              <div class="max-h-[300px] overflow-y-auto">
                <div v-if="recentOrders.length === 0" class="px-4 py-6 text-center text-zinc-500 text-sm">
                  Không có thông báo mới
                </div>
                <div v-else class="divide-y divide-zinc-100">
                  <div 
                    v-for="order in recentOrders" :key="order.id"
                    class="px-4 py-3 hover:bg-zinc-50 cursor-pointer transition-colors flex items-start gap-3"
                    @click="goToOrder(order.id)"
                  >
                    <div class="h-8 w-8 rounded-full bg-blue-50 text-blue-500 flex items-center justify-center flex-shrink-0 mt-0.5">
                      <span class="material-symbols-outlined text-[18px]">local_shipping</span>
                    </div>
                    <div>
                      <p class="text-sm text-zinc-800 font-medium">Đơn hàng online #{{ order.id }}</p>
                      <p class="text-xs text-zinc-500 mt-0.5">{{ order.customerName || order.customerEmail || 'Khách vãng lai' }} vừa đặt hàng</p>
                    </div>
                  </div>
                </div>
              </div>
              <div class="px-4 py-2 bg-zinc-50 border-t border-zinc-100 text-center">
                <router-link to="/admin/orders" class="text-xs text-zinc-600 font-medium" @click="showNotificationDropdown = false">Xem tất cả đơn hàng</router-link>
              </div>
            </div>
          </div>

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
                  : 'text-zinc-700 hover:bg-zinc-50 text-zinc-900'
              "
            >
              <span class="material-symbols-outlined text-[20px]">{{
                item.icon
              }}</span>
              <span>{{ item.label }}</span>
            </router-link>

            <router-link v-if="userRole === 'ADMIN'" to="/" class="flex items-center gap-3 rounded-xl px-4 py-3 text-sm font-medium text-zinc-700 transition-colors hover:bg-zinc-50 hover:text-zinc-900"
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

            <router-link v-if="userRole === 'ADMIN'" to="/" class="inline-flex items-center gap-2 whitespace-nowrap rounded-full border border-zinc-200 bg-white px-4 py-2 text-xs font-semibold text-zinc-700 transition-colors hover:bg-zinc-50"
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
import { getSession, clearSession } from "@/utils/auth";
import { computed, onMounted, onUnmounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { adminApi } from "@/services/api";

const newOrderCount = ref(0);
const lastSeenOrderId = ref(localStorage.getItem('lastSeenOrderId'));
let pollInterval = null;

async function checkNewOrders() {
  try {
    const data = await adminApi.getOrders({ page: 0, size: 10 });
    const ordersList = Array.isArray(data) ? data : (data?.content || []);
    if (ordersList.length > 0) {
      if (!lastSeenOrderId.value) {
        const maxId = Math.max(...ordersList.map(o => o.id));
        lastSeenOrderId.value = maxId;
        localStorage.setItem('lastSeenOrderId', maxId.toString());
      } else {
        let latestId = parseInt(lastSeenOrderId.value, 10);
        let count = 0;
        recentOrders.value = ordersList.filter(o => o.id > latestId).slice(0, 5);
        for (const order of ordersList) {
          if (order.id > latestId) {
            count++;
          }
        }
        newOrderCount.value = count;
      }
    }
  } catch (err) {
    console.error("Lỗi poll đơn hàng mới:", err);
  }
}

const showNotificationDropdown = ref(false);
const recentOrders = ref([]);

function toggleNotificationDropdown() {
  showNotificationDropdown.value = !showNotificationDropdown.value;
}

function markAllRead() {
  if (recentOrders.value.length > 0) {
    const maxId = Math.max(...recentOrders.value.map(o => o.id));
    localStorage.setItem('lastSeenOrderId', maxId.toString());
    lastSeenOrderId.value = maxId;
    newOrderCount.value = 0;
  }
}

function goToOrder(orderId) {
  showNotificationDropdown.value = false;
  markAllRead();
  router.push('/admin/orders');
}

onMounted(() => {
  setTimeout(checkNewOrders, 1000);
  pollInterval = setInterval(checkNewOrders, 5000);
  
  document.addEventListener('click', () => {
    showNotificationDropdown.value = false;
  });
});

onUnmounted(() => {
  if (pollInterval) clearInterval(pollInterval);
});

const route = useRoute();
const router = useRouter();

const userRole = computed(() => {
  try {
    const rawUser = getSession("user");
    if (!rawUser) return localStorage.getItem("userRole") || "USER";
    const parsedUser = JSON.parse(rawUser);
    return parsedUser.role || localStorage.getItem("userRole") || "USER";
  } catch {
    return localStorage.getItem("userRole") || "USER";
  }
});

const navItems = computed(() => {
  const role = userRole.value;
  const items = [
    { path: "/admin", label: "Tổng quan", icon: "dashboard" },
    { path: "/admin/pos", label: "Bán hàng POS", icon: "point_of_sale" },
    { path: "/admin/orders", label: "Đơn hàng", icon: "shopping_bag" },
    { path: "/admin/products", label: "Sản phẩm", icon: "inventory_2" },
    { path: "/admin/customers", label: "Khách hàng", icon: "group" },
  ];
  if (role === "ADMIN") {
    items.push(
      { path: "/admin/categories", label: "Danh mục", icon: "category" },
      { path: "/admin/attributes", label: "Thuộc tính", icon: "palette" },
      { path: "/admin/brands", label: "Thương hiệu", icon: "branding_watermark" },
      { path: "/admin/employees", label: "Nhân viên", icon: "badge" },
      { path: "/admin/vouchers", label: "Voucher", icon: "local_activity" },
      { path: "/admin/promotions", label: "Khuyến mãi", icon: "campaign" }
    );
  }
  return items;
});

const currentUserName = computed(() => {
  try {
    const raw = getSession("user");
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
  clearSession();
  router.push({ name: "login" });
}
</script>
