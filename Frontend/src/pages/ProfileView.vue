<template>
  <div
    class="retail-shell text-slate-900 dark:text-white overflow-x-hidden transition-colors duration-200 min-h-screen flex flex-col"
  >
    <MainTopBar />

    <div v-if="isLoading" class="flex-1 flex items-center justify-center">
      <div
        class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"
      />
    </div>

    <div v-else class="flex flex-1 justify-center py-10 px-4 sm:px-8">
      <div class="flex flex-col max-w-[1080px] flex-1 w-full gap-8">
        <section class="retail-card overflow-hidden p-0">
          <div
            class="grid gap-6 bg-[#f8f1e8] px-6 py-7 md:grid-cols-[1.1fr_0.9fr] md:px-8 md:py-8 dark:bg-[#221c18]"
          >
            <div class="space-y-3">
              <span class="retail-kicker">Tài khoản</span>
              <h1
                class="text-3xl md:text-4xl font-black tracking-tight text-slate-900 dark:text-white"
              >
                Xin chào {{ user.username || "bạn" }}
              </h1>
              <p class="text-slate-600 dark:text-[#cabdae] text-base leading-7">
                Quản lý hồ sơ, theo dõi đơn hàng và xem lại toàn bộ đánh giá mua
                sắm của bạn tại một nơi.
              </p>
              <div class="flex flex-wrap gap-3 pt-2">
                <button
                  class="inline-flex items-center justify-center gap-2 rounded-full bg-primary px-5 py-2.5 text-sm font-semibold text-white transition-transform hover:-translate-y-0.5"
                  @click="editProfile"
                >
                  <span class="material-symbols-outlined text-[18px]"
                    >edit</span
                  >
                  Sửa hồ sơ
                </button>
                <router-link
                  class="inline-flex items-center justify-center gap-2 rounded-full border border-slate-300 px-5 py-2.5 text-sm font-semibold text-slate-700 transition-colors hover:border-primary hover:text-primary dark:border-[#4c4138] dark:text-white"
                  to="/shop"
                >
                  <span class="material-symbols-outlined text-[18px]"
                    >shopping_bag</span
                  >
                  Mua sắm thêm
                </router-link>
              </div>
            </div>

            <div class="grid grid-cols-2 gap-3 md:justify-self-end">
              <article
                class="rounded-2xl border border-slate-200/80 bg-white/80 px-4 py-4 dark:border-[#3c342e] dark:bg-[#2a221d]"
              >
                <p
                  class="text-[11px] uppercase tracking-[0.18em] text-slate-500 dark:text-[#b9aa9a]"
                >
                  Đơn hàng
                </p>
                <p
                  class="mt-2 text-2xl font-bold text-slate-900 dark:text-white"
                >
                  {{ orders.length }}
                </p>
              </article>
              <article
                class="rounded-2xl border border-slate-200/80 bg-white/80 px-4 py-4 dark:border-[#3c342e] dark:bg-[#2a221d]"
              >
                <p
                  class="text-[11px] uppercase tracking-[0.18em] text-slate-500 dark:text-[#b9aa9a]"
                >
                  Đánh giá
                </p>
                <p
                  class="mt-2 text-2xl font-bold text-slate-900 dark:text-white"
                >
                  {{ feedbacks.length }}
                </p>
              </article>
              <article
                class="rounded-2xl border border-slate-200/80 bg-white/80 px-4 py-4 dark:border-[#3c342e] dark:bg-[#2a221d] col-span-2"
              >
                <p
                  class="text-[11px] uppercase tracking-[0.18em] text-slate-500 dark:text-[#b9aa9a]"
                >
                  Trạng thái
                </p>
                <p
                  class="mt-2 text-sm font-semibold"
                  :class="
                    user.isActive
                      ? 'text-green-700 dark:text-green-400'
                      : 'text-red-700 dark:text-red-400'
                  "
                >
                  {{
                    user.isActive
                      ? "Tài khoản đang hoạt động"
                      : "Tài khoản tạm khóa"
                  }}
                </p>
              </article>
            </div>
          </div>
        </section>

        <section class="retail-card overflow-hidden">
          <div
            class="p-6 md:p-8 flex flex-col md:flex-row gap-6 items-start md:items-center justify-between border-b border-slate-200/70 dark:border-[#3c342e]"
          >
            <div class="flex items-center gap-6">
              <div
                class="size-24 shrink-0 rounded-full shadow-inner flex items-center justify-center bg-slate-100 text-slate-500 dark:bg-[#283039] dark:text-[#c8d0d8] overflow-hidden"
              >
                <img
                  v-if="resolvedProfileAvatar"
                  :src="resolvedProfileAvatar"
                  :alt="user.username"
                  class="w-full h-full object-cover"
                />
                <span v-else class="material-symbols-outlined text-5xl"
                  >account_circle</span
                >
              </div>
              <div class="flex flex-col gap-1">
                <h2 class="text-slate-900 dark:text-white text-2xl font-bold">
                  {{ user.username || "Thành viên" }}
                </h2>
                <div
                  class="flex items-center gap-2 text-slate-500 dark:text-[#9cabba]"
                >
                  <span class="material-symbols-outlined text-lg">mail</span>
                  <span class="text-sm">{{
                    user.gmail || user.email || "email@example.com"
                  }}</span>
                </div>
                <div
                  class="flex items-center gap-2 text-slate-500 dark:text-[#9cabba] mt-1"
                >
                  <span
                    class="px-2 py-0.5 rounded-full text-xs font-bold uppercase tracking-wider"
                    :class="isAdminAccount
                      ? 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400'
                      : 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400'"
                    >{{ membershipLabel }}</span
                  >
                </div>
              </div>
            </div>
          </div>

          <div
            class="grid grid-cols-1 md:grid-cols-2 divide-y md:divide-y-0 md:divide-x divide-slate-200/70 dark:divide-[#3c342e]"
          >
            <div class="p-6 md:p-8 flex flex-col gap-4">
              <div class="flex items-center gap-2 mb-2">
                <span class="material-symbols-outlined text-primary"
                  >location_on</span
                >
                <h3 class="text-slate-900 dark:text-white font-bold text-lg">
                  Địa chỉ mặc định
                </h3>
              </div>
              <div class="pl-8">
                <p
                  v-if="user.address"
                  class="text-slate-600 dark:text-slate-300"
                >
                  {{ user.address }}
                </p>
                <p v-else class="text-slate-400 dark:text-slate-500 italic">
                  Bạn chưa thêm địa chỉ
                </p>
              </div>
            </div>
            <div class="p-6 md:p-8 flex flex-col gap-4">
              <div class="flex items-center gap-2 mb-2">
                <span class="material-symbols-outlined text-primary">info</span>
                <h3 class="text-slate-900 dark:text-white font-bold text-lg">
                  Thông tin cá nhân
                </h3>
              </div>
              <div class="pl-8 grid grid-cols-2 gap-y-4">
                <div>
                  <p
                    class="text-slate-400 dark:text-[#9cabba] text-xs uppercase font-semibold"
                  >
                    Số điện thoại
                  </p>
                  <p
                    class="text-slate-900 dark:text-white text-sm font-medium mt-1"
                  >
                    {{ user.phone || "Chưa cập nhật" }}
                  </p>
                </div>
                <div>
                  <p
                    class="text-slate-400 dark:text-[#9cabba] text-xs uppercase font-semibold"
                  >
                    Trạng thái
                  </p>
                  <p
                    class="text-slate-900 dark:text-white text-sm font-medium mt-1"
                  >
                    <span
                      :class="user.isActive ? 'text-green-600' : 'text-red-600'"
                    >
                      {{ user.isActive ? "Đang hoạt động" : "Tạm khóa" }}
                    </span>
                  </p>
                </div>
                <div>
                  <p
                    class="text-slate-400 dark:text-[#9cabba] text-xs uppercase font-semibold"
                  >
                    Mã tài khoản
                  </p>
                  <p
                    class="text-slate-900 dark:text-white text-sm font-medium mt-1 font-mono"
                  >
                    {{ user.id || "N/A" }}
                  </p>
                </div>
                <div>
                  <p
                    class="text-slate-400 dark:text-[#9cabba] text-xs uppercase font-semibold"
                  >
                    Ngày tham gia
                  </p>
                  <p
                    class="text-slate-900 dark:text-white text-sm font-medium mt-1"
                  >
                    {{ formatDate(user.createdAt) || "Không rõ" }}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section class="flex flex-col gap-4">
          <div class="flex items-center justify-between">
            <h2 class="text-slate-900 dark:text-white text-xl font-bold">
              Đơn hàng của tôi
            </h2>
            <router-link
              to="/orders"
              class="block px-4 py-2 text-sm text-slate-700 hover:bg-slate-100 dark:text-slate-300 dark:hover:bg-slate-800"
            >
              Đơn hàng của tôi
            </router-link>
          </div>

          <div v-if="isLoadingOrders" class="flex justify-center py-8">
            <div
              class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"
            />
          </div>

          <div
            v-else-if="recentOrders.length > 0"
            class="retail-card overflow-hidden"
          >
            <div
              class="hidden md:grid grid-cols-12 gap-4 p-4 border-b border-slate-200/70 dark:border-[#3c342e] bg-[#f7efe6] dark:bg-[#241d19] text-xs font-semibold uppercase text-slate-500 dark:text-[#b9aa9a] tracking-wider"
            >
              <div class="col-span-1">STT</div>
              <div class="col-span-4">Sản phẩm</div>
              <div class="col-span-2">Ngày</div>
              <div class="col-span-2">Trạng thái</div>
              <div class="col-span-2 text-right">Tổng tiền</div>
              <div class="col-span-1 text-right">Thao tác</div>
            </div>

            <div
              v-for="(order, index) in recentOrders"
              :key="order.id || index"
              class="grid grid-cols-1 md:grid-cols-12 gap-4 p-4 items-center border-b border-slate-200/70 dark:border-[#3c342e] hover:bg-[#fbf4ec] dark:hover:bg-[#2a231f] transition-colors group"
            >
              <div class="col-span-1 flex items-center gap-3">
                <div
                  class="p-2 rounded bg-slate-100 dark:bg-[#283039] text-slate-500 dark:text-slate-400 md:hidden"
                >
                  <span class="material-symbols-outlined text-lg"
                    >receipt_long</span
                  >
                </div>
                <span class="font-bold text-slate-900 dark:text-white">{{
                  index + 1
                }}</span>
              </div>
              <div class="col-span-4">
                <p class="text-sm font-medium text-slate-900 dark:text-white">
                  {{ getOrderProductPreview(order) }}
                </p>
              </div>
              <div
                class="col-span-2 text-slate-600 dark:text-slate-300 text-sm"
              >
                {{ formatDate(getOrderDate(order)) }}
              </div>
              <div class="col-span-2">
                <span
                  class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                  :class="getStatusClass(order.status)"
                >
                  <span
                    class="w-1.5 h-1.5 mr-1.5"
                    :class="getStatusDotClass(order.status)"
                  />
                  {{ formatOrderStatus(order.status) }}
                </span>
              </div>
              <div
                class="col-span-2 text-right font-medium text-slate-900 dark:text-white"
              >
                {{ formatPrice(getOrderTotal(order)) }}
              </div>
              <div class="col-span-1 flex justify-end">
                <router-link
                  class="text-sm font-medium text-primary hover:text-[#a84f24] flex items-center gap-1 group-hover:translate-x-1 transition-transform"
                  :to="`/order/${order.id}`"
                >
                  Xem chi tiết
                  <span class="material-symbols-outlined text-sm"
                    >arrow_forward</span
                  >
                </router-link>
              </div>
            </div>
          </div>

          <div v-else class="retail-card text-center py-12">
            <span class="material-symbols-outlined text-5xl text-slate-400 mb-4"
              >shopping_bag</span
            >
            <p class="text-slate-500 dark:text-[#9cabba]">
              Bạn chưa có đơn hàng nào
            </p>
            <router-link
              class="inline-block mt-4 text-primary hover:text-[#a84f24] text-sm font-medium"
              to="/shop"
              >Bắt đầu mua sắm</router-link
            >
          </div>
        </section>

        <section class="flex flex-col gap-4">
          <h2 class="text-slate-900 dark:text-white text-xl font-bold">
            Đánh giá của tôi
          </h2>

          <div v-if="isLoadingFeedback" class="flex justify-center py-8">
            <div
              class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"
            />
          </div>

          <div
            v-else-if="feedbacks.length > 0"
            class="grid grid-cols-1 md:grid-cols-2 gap-4"
          >
            <div
              v-for="feedback in feedbacks"
              :key="feedback.id"
              class="retail-card p-4 flex flex-col gap-3"
            >
              <div class="flex items-start justify-between gap-3">
                <div>
                  <p
                    class="text-[11px] uppercase tracking-wide text-slate-400 dark:text-[#9cabba]"
                  >
                    Sản phẩm
                  </p>
                  <h3 class="text-sm font-bold text-slate-900 dark:text-white">
                    {{ getReviewProductName(feedback.productId) }}
                  </h3>
                  <p class="text-xs text-slate-400 dark:text-[#9cabba] mt-0.5">
                    Đánh giá #{{ feedback.id }}
                  </p>
                </div>
                <span class="text-xs text-slate-500 dark:text-[#9cabba]">{{
                  formatDate(feedback.createdAt)
                }}</span>
              </div>

              <div class="flex text-yellow-400 text-sm">
                <span
                  v-for="i in 5"
                  :key="i"
                  class="material-symbols-outlined text-[16px]"
                  :class="
                    i <= feedback.rating
                      ? 'fill-current'
                      : 'text-slate-300 dark:text-slate-600'
                  "
                >
                  star
                </span>
              </div>

              <p
                class="text-sm text-slate-600 dark:text-slate-300 leading-relaxed"
              >
                {{ feedback.comment }}
              </p>
            </div>
          </div>

          <div v-else class="retail-card text-center py-12">
            <span class="material-symbols-outlined text-5xl text-slate-400 mb-4"
              >rate_review</span
            >
            <p class="text-slate-500 dark:text-[#9cabba]">
              Bạn chưa viết đánh giá nào
            </p>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { getSession } from "@/utils/auth";
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { useProductStore } from "@/stores/products";
import { useReviewsStore } from "@/stores/reviews";
import { accountsApi, resolveBackendAssetUrl } from "../services/api";
import { orderAPI } from "@/services/orders";

const router = useRouter();
const productStore = useProductStore();
const reviewsStore = useReviewsStore();
const isLoading = ref(true);
const isLoadingOrders = ref(false);
const isLoadingFeedback = ref(false);
const user = ref({});
const orders = ref([]);
const feedbacks = ref([]);
const recentOrders = computed(() => orders.value.slice(0, 4));

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

const resolvedProfileAvatar = computed(() =>
  normalizeAvatarUrl(user.value.imgUrl),
);

const isAdminAccount = computed(() => {
  const role = String(user.value.role || "").trim().toUpperCase();
  return role === "ADMIN";
});

const membershipLabel = computed(() => {
  if (isAdminAccount.value) return "Quản trị viên";
  return user.value.membership || "Thành viên thường";
});

function normalizeIsActive(value, fallback = true) {
  if (value === true || value === 1 || value === "1" || value === "true")
    return true;
  if (value === false || value === 0 || value === "0" || value === "false")
    return false;
  return fallback;
}

function getAccountId(sourceUser) {
  const source = sourceUser && typeof sourceUser === "object" ? sourceUser : {};
  const candidate =
    source.accountId !== undefined && source.accountId !== null
      ? source.accountId
      : source.account_id !== undefined && source.account_id !== null
        ? source.account_id
        : source.id;
  const parsed = Number(candidate);
  return Number.isFinite(parsed) && parsed > 0 ? parsed : null;
}

// Lấy thông tin user từ localStorage khi component được mount
onMounted(async () => {
  await loadUserData();
  await fetchUserOrders();
  await fetchUserFeedbacks();
});

async function loadUserData() {
  try {
    const userData = getSession("user");
    if (userData) {
      const localUser = JSON.parse(userData);
      const localRawActive =
        localUser.isActive ?? localUser.is_active ?? localUser.status;

      user.value = {
        ...localUser,
        // Treat numeric 1 as active, 0 as inactive.
        isActive: normalizeIsActive(localRawActive, true),
      };

      // Refresh from backend to avoid stale localStorage status.
      const accountId = getAccountId(user.value);
      if (accountId !== null) {
        try {
          const latestAccount = await accountsApi.getById(accountId);
          const latestRawActive =
            latestAccount?.isActive ??
            latestAccount?.is_active ??
            latestAccount?.status;
          if (latestAccount && typeof latestAccount === "object") {
            user.value = {
              ...user.value,
              ...latestAccount,
              isActive: normalizeIsActive(latestRawActive, user.value.isActive),
            };
            saveSession(user.value);
          }
        } catch (refreshError) {
          console.error("Failed to refresh account status:", refreshError);
        }
      }

      console.log("User data loaded:", user.value);
      console.log("User email field:", user.value.gmail || user.value.email); // Debug để xem tên trường
    } else {
      router.push("/login");
    }
  } catch (error) {
    console.error("Error loading user data:", error);
  } finally {
    isLoading.value = false;
  }
}

// Fetch orders của user
async function fetchUserOrders() {
  const accountId = getAccountId(user.value);
  if (accountId === null) {
    orders.value = [];
    return;
  }

  isLoadingOrders.value = true;
  try {
    const response = await orderAPI.getUserOrders(accountId);
    const orderList = Array.isArray(response) ? response : [];
    const sortedOrders = orderList.slice().sort((a, b) => {
      const timeA = getOrderTime(getOrderDate(a));
      const timeB = getOrderTime(getOrderDate(b));
      if (timeA !== timeB) {
        return timeB - timeA;
      }

      const idA = Number(a && a.id);
      const idB = Number(b && b.id);
      const normalizedA = Number.isFinite(idA) ? idA : 0;
      const normalizedB = Number.isFinite(idB) ? idB : 0;
      return normalizedB - normalizedA;
    });

    orders.value = await enrichLatestOrderNames(sortedOrders, 4);
  } catch (error) {
    console.error("Error fetching orders:", error);
    orders.value = [];
  } finally {
    isLoadingOrders.value = false;
  }
}

function getOrderTime(value) {
  const parsed = new Date(value || 0).getTime();
  return Number.isFinite(parsed) ? parsed : 0;
}

async function enrichLatestOrderNames(orderList, limit = 4) {
  const source = Array.isArray(orderList) ? orderList.slice() : [];
  if (source.length === 0) {
    return source;
  }

  const cappedLimit = Math.min(limit, source.length);
  const detailList = await Promise.all(
    source.slice(0, cappedLimit).map(async (order) => {
      const orderId = Number(order && order.id);
      if (!Number.isFinite(orderId) || orderId <= 0) {
        return null;
      }

      try {
        return await orderAPI.getOrder(orderId);
      } catch (error) {
        console.warn("Could not load order detail for mini history:", error);
        return null;
      }
    }),
  );

  for (let i = 0; i < cappedLimit; i += 1) {
    const detail = detailList[i];
    if (detail && typeof detail === "object") {
      source[i] = {
        ...source[i],
        ...detail,
        id: source[i].id || detail.id,
      };
    }
  }

  return source;
}

// Fetch feedbacks của user (nếu có API)
async function fetchUserFeedbacks() {
  const accountId = getAccountId(user.value);
  if (accountId === null) {
    feedbacks.value = [];
    return;
  }

  isLoadingFeedback.value = true;
  try {
    if (productStore.products.length === 0) {
      await productStore.fetchProducts();
    }

    await reviewsStore.fetchAccounts();
    await reviewsStore.fetchReviews({ force: true });

    feedbacks.value = reviewsStore
      .getByAccountId(accountId)
      .slice()
      .sort(
        (a, b) =>
          new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
      );
  } catch (error) {
    console.error("Error fetching feedbacks:", error);
    feedbacks.value = [];
  } finally {
    isLoadingFeedback.value = false;
  }
}

// Format ngày tháng
function formatDate(dateString) {
  if (!dateString) return "Không rõ";
  const date = new Date(dateString);
  return new Intl.DateTimeFormat("vi-VN", {
    year: "numeric",
    month: "long",
    day: "numeric",
  }).format(date);
}

// Format giá tiền
function formatPrice(price) {
  if (!price) return "0 ₫";
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
    maximumFractionDigits: 0,
  }).format(Number(price));
}

function getOrderDate(order) {
  if (!order || typeof order !== "object") return null;
  return (
    order.createdAt ||
    order.orderDate ||
    order.created_at ||
    order.order_date ||
    null
  );
}

function getOrderTotal(order) {
  if (!order || typeof order !== "object") return 0;

  const candidate =
    order.finalAmount !== undefined && order.finalAmount !== null
      ? order.finalAmount
      : order.totalAmount !== undefined && order.totalAmount !== null
        ? order.totalAmount
        : order.totalMoney !== undefined && order.totalMoney !== null
          ? order.totalMoney
          : order.final_amount !== undefined && order.final_amount !== null
            ? order.final_amount
            : order.total_amount !== undefined && order.total_amount !== null
              ? order.total_amount
              : order.total_money;

  const parsed = Number(candidate);
  return Number.isFinite(parsed) ? parsed : 0;
}

function getReviewProductName(productId) {
  const id = Number(productId);
  if (!Number.isFinite(id)) return "Sản phẩm không rõ";
  const product = productStore.getProductById(id);
  return product?.name || `Sản phẩm #${id}`;
}

function getOrderItems(order) {
  const source = order && typeof order === "object" ? order : {};
  if (Array.isArray(source.orderItems)) return source.orderItems;
  if (Array.isArray(source.order_items)) return source.order_items;
  if (Array.isArray(source.items)) return source.items;
  if (Array.isArray(source.orderDetails)) return source.orderDetails;
  if (Array.isArray(source.order_details)) return source.order_details;
  return [];
}

function getOrderItemName(item) {
  const source = item && typeof item === "object" ? item : {};
  const rawName =
    source.productName ||
    source.product_name ||
    source.name ||
    source.itemName ||
    source.item_name ||
    (source.product &&
      (source.product.name ||
        source.product.productName ||
        source.product.product_name)) ||
    (source.variant &&
      (source.variant.name ||
        source.variant.productName ||
        source.variant.product_name)) ||
    "";

  return String(rawName || "").trim();
}

function getOrderProductPreview(order) {
  const items = getOrderItems(order);
  if (items.length === 0) {
    const source = order && typeof order === "object" ? order : {};
    const orderLevelName =
      source.productName ||
      source.product_name ||
      source.name ||
      source.title ||
      "";
    const normalizedOrderLevelName = String(orderLevelName || "").trim();
    if (normalizedOrderLevelName) {
      return normalizedOrderLevelName;
    }

    return "Không rõ sản phẩm";
  }

  const uniqueNames = [];
  for (const item of items) {
    const name = getOrderItemName(item);
    if (name && uniqueNames.indexOf(name) === -1) {
      uniqueNames.push(name);
    }
  }

  if (uniqueNames.length === 0) {
    return "Không rõ sản phẩm";
  }

  if (uniqueNames.length === 1) {
    return uniqueNames[0];
  }

  return `${uniqueNames[0]} +${uniqueNames.length - 1} sản phẩm`;
}

function normalizeStatusKey(status) {
  return String(status || "")
    .trim()
    .toUpperCase();
}

function getOrderStatusCategory(status) {
  const key = normalizeStatusKey(status);

  if (
    key.indexOf("DELIVER") !== -1 ||
    key.indexOf("RECEIV") !== -1 ||
    key.indexOf("SUCCESS") !== -1 ||
    key.indexOf("COMPLETE") !== -1
  ) {
    return "delivered";
  }

  if (key.indexOf("SHIP") !== -1 || key.indexOf("TRANSIT") !== -1) {
    return "shipping";
  }

  if (
    key.indexOf("CANCEL") !== -1 ||
    key.indexOf("FAIL") !== -1 ||
    key.indexOf("REJECT") !== -1
  ) {
    return "cancelled";
  }

  if (
    key.indexOf("RETURN") !== -1 ||
    key.indexOf("REFUND") !== -1 ||
    key === "TRA_HANG" ||
    key === "YEU_CAU_TRA"
  ) {
    return "returned";
  }

  return "processing";
}

function getStatusMeta(status) {
  const category = getOrderStatusCategory(status);

  if (category === "delivered") {
    return {
      label: "Đã giao",
      className:
        "bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-300",
      dotClass: "bg-green-500",
    };
  }

  if (category === "shipping") {
    return {
      label: "Đang giao",
      className:
        "bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-300",
      dotClass: "bg-blue-500",
    };
  }

  if (category === "cancelled") {
    return {
      label: "Đã hủy",
      className: "bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-300",
      dotClass: "bg-red-500",
    };
  }

  if (category === "returned") {
    return {
      label: "Hoàn hàng",
      className:
        "bg-slate-100 text-slate-800 dark:bg-slate-700/50 dark:text-slate-300",
      dotClass: "bg-slate-500",
    };
  }

  return {
    label: "Chờ xử lý",
    className:
      "bg-amber-100 text-amber-800 dark:bg-amber-900/30 dark:text-amber-300",
    dotClass: "bg-amber-500",
  };
}

function formatOrderStatus(status) {
  return getStatusMeta(status).label;
}

// Xử lý edit profile
function editProfile() {
  router.push("/profile/edit");
}

// Xử lý logout
function handleLogout() {
  localStorage.removeItem("user");
  localStorage.removeItem("token");
  router.push("/login");
}

// Lấy class cho status badge
function getStatusClass(status) {
  return getStatusMeta(status).className;
}

// Lấy class cho status dot
function getStatusDotClass(status) {
  return getStatusMeta(status).dotClass;
}
</script>

<style scoped>
/* Scrollbar styles for consistent dark mode look */
::-webkit-scrollbar {
  width: 8px;
}
::-webkit-scrollbar-track {
  background: #101922;
}
::-webkit-scrollbar-thumb {
  background: #f97316;
  border-radius: 4px;
}
::-webkit-scrollbar-thumb:hover {
  background: #ea580c;
}
</style>
