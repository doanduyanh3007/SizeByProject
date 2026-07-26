import { createRouter, createWebHistory } from "vue-router";

import AboutView from "@/pages/AboutView.vue";
import CartView from "@/pages/CartView.vue";
import CheckoutView from "@/pages/CheckoutView.vue";
import CompareView from "@/pages/CompareView.vue";
import HomeView from "@/pages/HomeView.vue";
import LoginView from "@/pages/LoginView.vue";
import ForgotPasswordView from "@/pages/ForgotPasswordView.vue";
import ChangePasswordView from "@/pages/ChangePasswordView.vue";
import ProductDetailView from "@/pages/ProductDetailView.vue";
import ProductView from "@/pages/ProductView.vue";
import ProfileEdit from "@/pages/ProfileEdit.vue";
import ProfileView from "@/pages/ProfileView.vue";
import RegisterView from "@/pages/RegisterView.vue";
import ReviewFormView from "@/pages/ReviewFormView.vue";
import VoucherView from "@/pages/VoucherView.vue";
import ShopView from "@/pages/ShopView.vue";
import SizeGuideView from "@/pages/SizeGuideView.vue";
import OrderHistoryView from "@/pages/OrderHistoryView.vue";
import VnpayReturnView from "@/pages/VnpayReturnView.vue";

import AdminHomeView from "@/pages/AdminHomeView.vue";
import AdminPOSView from "@/pages/AdminPOSView.vue";
import AdminOrdersView from "@/pages/AdminOrdersView.vue";
import AdminProductsView from "@/pages/AdminProductsView.vue";
import AdminInventoryView from "@/pages/AdminInventoryView.vue";
import AdminCustomersView from "@/pages/AdminCustomersView.vue";
import AdminVouchersView from "@/pages/AdminVouchersView.vue";
import AdminPromotionsView from "@/pages/AdminPromotionsView.vue";
import AdminBrandsView from "@/pages/AdminBrandsView.vue";
import AdminAttributesView from "@/pages/AdminAttributesView.vue";

function getLoggedInAccountId() {
  const rawUser = localStorage.getItem("user");
  if (!rawUser) return null;
  try {
    const parsedUser = JSON.parse(rawUser);
    const candidate =
      parsedUser &&
      parsedUser.accountId !== undefined &&
      parsedUser.accountId !== null
        ? parsedUser.accountId
        : parsedUser &&
            parsedUser.account_id !== undefined &&
            parsedUser.account_id !== null
          ? parsedUser.account_id
          : parsedUser
            ? parsedUser.id
            : null;
    const n = Number(candidate);
    return Number.isFinite(n) && n > 0 ? n : null;
  } catch {
    return null;
  }
}

function getUserRole() {
  const rawUser = localStorage.getItem("user");
  if (!rawUser) return null;
  try {
    const parsedUser = JSON.parse(rawUser);
    return parsedUser.role || localStorage.getItem("userRole") || null;
  } catch {
    return localStorage.getItem("userRole") || null;
  }
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: "/", name: "home", component: HomeView },
    { path: "/about", name: "about", component: AboutView },
    { path: "/shop", name: "shop", component: ShopView },
    { path: "/brands", redirect: { path: "/", hash: "#brands" } },
    { path: "/size-guide", name: "size-guide", component: SizeGuideView },
    {
      path: "/product/:id",
      name: "product-detail",
      component: ProductDetailView,
    },
    {
      path: "/product/:id/review",
      name: "review-form",
      component: ReviewFormView,
    },
    { path: "/cart", name: "cart", component: CartView },
    { path: "/checkout", name: "checkout", component: CheckoutView },
    { path: "/login", name: "login", component: LoginView },
    {
      path: "/forgot-password",
      name: "forgot-password",
      component: ForgotPasswordView,
    },
    { path: "/register", name: "register", component: RegisterView },
    { path: "/profile", name: "profile", component: ProfileView },
    { path: "/profile/edit", name: "profile-edit", component: ProfileEdit },
    {
      path: "/profile/change-password",
      name: "profile-change-password",
      component: ChangePasswordView,
    },
    {
      path: "/profile/edit/reviews",
      name: "profile-edit-reviews",
      component: ProfileEdit,
    },
    { path: "/vouchers", name: "vouchers", component: VoucherView },
    { path: "/compare", name: "compare", component: CompareView },
    { path: "/orders", name: "orders", component: OrderHistoryView },
    { path: "/order/:id", name: "order-detail", component: OrderHistoryView },
    {
      path: "/payment/vnpay-return",
      name: "vnpay-return",
      component: VnpayReturnView,
    },

    // --- ADMIN ROUTES ---
    {
      path: "/admin",
      name: "admin-home",
      component: AdminHomeView,
      meta: { requiresAdmin: true },
    },
    {
      path: "/admin/pos",
      name: "admin-pos",
      component: AdminPOSView,
      meta: { requiresAdmin: true },
    },
    {
      path: "/admin/orders",
      name: "admin-orders",
      component: AdminOrdersView,
      meta: { requiresAdmin: true },
    },
    {
      path: "/admin/products",
      name: "admin-products",
      component: AdminProductsView,
      meta: { requiresAdmin: true },
    },
    {
      path: "/admin/brands",
      name: "admin-brands",
      component: AdminBrandsView,
      meta: { requiresAdmin: true },
    },
    {
      path: "/admin/inventory",
      name: "admin-inventory",
      component: AdminInventoryView,
      meta: { requiresAdmin: true },
    },
    {
      path: "/admin/attributes",
      name: "admin-attributes",
      component: AdminAttributesView,
      meta: { requiresAdmin: true },
    },
    {
      path: "/admin/customers",
      name: "admin-customers",
      component: AdminCustomersView,
      meta: { allowedRoles: ["ADMIN", "STAFF"] },
    },
    {
      path: "/admin/vouchers",
      name: "admin-vouchers",
      component: AdminVouchersView,
      meta: { requiresAdmin: true },
    },
    {
      path: "/admin/promotions",
      name: "admin-promotions",
      component: AdminPromotionsView,
      meta: { requiresAdmin: true },
    },
    {
      path: "/admin/settings",
      redirect: "/admin",
    },
    {
      path: "/:pathMatch(.*)*",
      redirect: "/",
    },
  ],
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    }
    return { top: 0 };
  },
});

router.beforeEach((to, from, next) => {
  if (to.name === "checkout" && getLoggedInAccountId() === null) {
    next({ name: "login", query: { redirect: to.fullPath } });
    return;
  }

  const userRole = getUserRole();

  if (Array.isArray(to.meta.allowedRoles)) {
    if (!to.meta.allowedRoles.includes(userRole)) {
      next({ name: "home" });
      return;
    }
  } else if (to.meta.requiresAdmin) {
    if (userRole !== "ADMIN") {
      next({ name: "home" });
      return;
    }
  }

  next();
});

export default router;
