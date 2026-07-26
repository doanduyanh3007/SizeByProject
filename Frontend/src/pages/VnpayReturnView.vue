<template>
  <div
    class="retail-shell flex min-h-screen flex-col text-slate-900 dark:text-white font-display antialiased selection:bg-primary selection:text-white"
  >
    <MainTopBar />

    <main class="flex-grow container mx-auto px-4 py-12 lg:px-8">
      <div class="mx-auto max-w-lg">
        <!-- Loading State -->
        <div v-if="loading" class="text-center py-20">
          <div
            class="mx-auto mb-6 flex h-20 w-20 items-center justify-center rounded-3xl bg-primary/10"
          >
            <span
              class="material-symbols-outlined text-4xl text-primary animate-spin"
              >progress_activity</span
            >
          </div>
          <h2 class="text-xl font-bold mb-2">Đang xác minh thanh toán...</h2>
          <p class="text-sm text-slate-500 dark:text-[#9cabba]">
            Vui lòng chờ trong giây lát.
          </p>
        </div>

        <!-- Success State -->
        <div
          v-else-if="paymentSuccess"
          class="rounded-2xl border border-green-500/30 bg-white p-8 text-center shadow-lg dark:border-green-500/20 dark:bg-[#18212b]"
        >
          <div
            class="mx-auto mb-6 flex h-20 w-20 items-center justify-center rounded-3xl bg-green-500/15"
          >
            <span
              class="material-symbols-outlined text-5xl text-green-500"
              >check_circle</span
            >
          </div>
          <h2 class="text-2xl font-black tracking-tight text-green-600 dark:text-green-400 mb-2">
            Thanh toán thành công!
          </h2>
          <p class="text-sm text-slate-500 dark:text-[#9cabba] mb-6">
            {{ resultMessage }}
          </p>

          <div
            class="mb-6 rounded-xl bg-slate-50 p-4 text-left text-sm dark:bg-[#1b2127]"
          >
            <div
              class="flex items-center justify-between border-b border-slate-200 pb-3 mb-3 dark:border-[#283039]"
            >
              <span class="text-slate-500 dark:text-[#9cabba]">Mã đơn hàng</span>
              <span class="font-bold text-primary">#{{ orderId }}</span>
            </div>
            <div
              v-if="transactionNo"
              class="flex items-center justify-between border-b border-slate-200 pb-3 mb-3 dark:border-[#283039]"
            >
              <span class="text-slate-500 dark:text-[#9cabba]"
                >Mã giao dịch VNPay</span
              >
              <span class="font-semibold">{{ transactionNo }}</span>
            </div>
            <div
              v-if="paymentAmount > 0"
              class="flex items-center justify-between"
            >
              <span class="text-slate-500 dark:text-[#9cabba]">Số tiền</span>
              <span class="font-bold text-lg">{{ formatMoney(paymentAmount) }}</span>
            </div>
          </div>

          <div class="flex gap-3">
            <button
              class="flex-1 rounded-xl bg-primary px-4 py-3 text-sm font-bold text-white transition-colors hover:bg-primary-hover"
              @click="$router.push('/orders')"
            >
              Xem đơn hàng
            </button>
            <button
              class="flex-1 rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm font-bold transition-colors hover:bg-slate-50 dark:border-[#3b4754] dark:bg-[#1b2127]"
              @click="$router.push('/')"
            >
              Về trang chủ
            </button>
          </div>
        </div>

        <!-- Failed State -->
        <div
          v-else
          class="rounded-2xl border border-red-500/30 bg-white p-8 text-center shadow-lg dark:border-red-500/20 dark:bg-[#18212b]"
        >
          <div
            class="mx-auto mb-6 flex h-20 w-20 items-center justify-center rounded-3xl bg-red-500/15"
          >
            <span
              class="material-symbols-outlined text-5xl text-red-500"
              >cancel</span
            >
          </div>
          <h2
            class="text-2xl font-black tracking-tight text-red-600 dark:text-red-400 mb-2"
          >
            Thanh toán thất bại
          </h2>
          <p class="text-sm text-slate-500 dark:text-[#9cabba] mb-2">
            {{ resultMessage }}
          </p>
          <p
            v-if="responseCode && responseCode !== '00'"
            class="text-xs text-slate-400 mb-6"
          >
            Mã lỗi VNPay: {{ responseCode }}
          </p>

          <div
            v-if="orderId"
            class="mb-6 rounded-xl bg-slate-50 p-4 text-left text-sm dark:bg-[#1b2127]"
          >
            <div class="flex items-center justify-between">
              <span class="text-slate-500 dark:text-[#9cabba]">Mã đơn hàng</span>
              <span class="font-bold">#{{ orderId }}</span>
            </div>
          </div>

          <div class="flex gap-3">
            <button
              class="flex-1 rounded-xl bg-primary px-4 py-3 text-sm font-bold text-white transition-colors hover:bg-primary-hover"
              @click="$router.push('/checkout')"
            >
              Thử lại
            </button>
            <button
              class="flex-1 rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm font-bold transition-colors hover:bg-slate-50 dark:border-[#3b4754] dark:bg-[#1b2127]"
              @click="$router.push('/orders')"
            >
              Xem đơn hàng
            </button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { paymentApi } from "@/services/api";
import MainTopBar from "@/components/MainTopBar.vue";

const route = useRoute();

const loading = ref(true);
const paymentSuccess = ref(false);
const resultMessage = ref("");
const orderId = ref("");
const transactionNo = ref("");
const paymentAmount = ref(0);
const responseCode = ref("");

function formatMoney(value) {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(value || 0);
}

onMounted(async () => {
  // VNPay redirects with query params like:
  // ?vnp_Amount=...&vnp_ResponseCode=00&vnp_TxnRef=123&vnp_TransactionNo=...&vnp_SecureHash=...
  const queryParams = route.query;

  orderId.value = queryParams.vnp_TxnRef || "";
  responseCode.value = queryParams.vnp_ResponseCode || "";
  transactionNo.value = queryParams.vnp_TransactionNo || "";

  // Parse amount (VNPay sends amount * 100)
  if (queryParams.vnp_Amount) {
    paymentAmount.value = parseInt(queryParams.vnp_Amount, 10) / 100;
  }

  // Quick client-side check first
  if (responseCode.value === "00") {
    // Try to verify via backend for extra security
    try {
      const result = await paymentApi.verifyVnpayReturn(queryParams);
      paymentSuccess.value = result.success === true;
      resultMessage.value =
        result.message || "Đơn hàng của bạn đã được thanh toán thành công qua VNPay.";
      if (result.orderId) orderId.value = result.orderId;
      if (result.amount) paymentAmount.value = result.amount;
    } catch {
      // Backend verify failed, but VNPay said success — trust client-side
      paymentSuccess.value = true;
      resultMessage.value =
        "Đơn hàng của bạn đã được thanh toán thành công qua VNPay.";
    }
  } else {
    paymentSuccess.value = false;
    resultMessage.value = getErrorMessage(responseCode.value);
  }

  loading.value = false;
});

function getErrorMessage(code) {
  const messages = {
    "07": "Trừ tiền thành công nhưng giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).",
    "09": "Thẻ/Tài khoản chưa đăng ký dịch vụ InternetBanking tại ngân hàng.",
    "10": "Xác thực thông tin thẻ/tài khoản không đúng quá 3 lần.",
    "11": "Đã hết hạn chờ thanh toán. Vui lòng thực hiện lại giao dịch.",
    "12": "Thẻ/Tài khoản bị khóa.",
    "13": "Bạn nhập sai mật khẩu xác thực giao dịch (OTP).",
    "24": "Khách hàng hủy giao dịch.",
    "51": "Tài khoản không đủ số dư để thực hiện giao dịch.",
    "65": "Tài khoản đã vượt quá hạn mức giao dịch trong ngày.",
    "75": "Ngân hàng thanh toán đang bảo trì.",
    "79": "Nhập sai mật khẩu thanh toán quá số lần quy định.",
    "99": "Lỗi không xác định.",
  };
  return (
    messages[code] ||
    "Giao dịch không thành công. Vui lòng thử lại hoặc liên hệ hỗ trợ."
  );
}
</script>
