<template>
  <AdminShell>
    <template #header>
      <div class="flex flex-wrap items-start justify-between gap-4">
        <div>
          <p class="text-xs uppercase tracking-[0.18em] text-primary">Bảng điều khiển admin</p>
          <h2 class="mt-1 text-2xl font-bold">Tổng quan kế toán</h2>
          <p class="mt-1 text-sm text-slate-500 dark:text-[#b9aa9a]">
            Xem nhanh doanh thu, đơn hàng và xu hướng theo ngày, tuần, tháng hoặc năm.
          </p>
        </div>
        <div class="flex flex-wrap items-center gap-2">
          <details class="relative">
            <summary class="list-none inline-flex cursor-pointer items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm font-medium hover:border-primary hover:text-primary dark:border-[#3c342e] dark:bg-[#1f1a17]">
              <span class="material-symbols-outlined text-[18px]">download</span>
              Xuất dữ liệu
              <span class="material-symbols-outlined text-[16px]">expand_more</span>
            </summary>
            <div class="absolute right-0 z-20 mt-2 w-56 overflow-hidden rounded-xl border border-slate-200 bg-white shadow-xl dark:border-[#3c342e] dark:bg-[#1f1a17]">
              <button type="button" class="flex w-full items-center gap-2 px-3 py-2 text-left text-sm hover:bg-slate-100 dark:hover:bg-[#2b241f]" @click="exportOverviewSummaryCsv">
                <span class="material-symbols-outlined text-[16px]">overview</span>
                CSV tóm tắt nhanh
              </button>
              <button type="button" class="flex w-full items-center gap-2 px-3 py-2 text-left text-sm hover:bg-slate-100 dark:hover:bg-[#2b241f]" @click="exportOverviewCsv">
                <span class="material-symbols-outlined text-[16px]">table_chart</span>
                CSV chi tiết chuẩn
              </button>
              <button type="button" class="flex w-full items-center gap-2 px-3 py-2 text-left text-sm hover:bg-slate-100 dark:hover:bg-[#2b241f]" @click="exportOverviewAuditCsv">
                <span class="material-symbols-outlined text-[16px]">fact_check</span>
                CSV kiểm toán
              </button>
            </div>
          </details>

          <button
            type="button"
            class="inline-flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm font-medium hover:border-primary hover:text-primary dark:border-[#3c342e] dark:bg-[#1f1a17]"
            @click="printOverview"
          >
            <span class="material-symbols-outlined text-[18px]">print</span>
            In tổng quan
          </button>

          <button
            type="button"
            class="inline-flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm font-medium hover:border-primary hover:text-primary dark:border-[#3c342e] dark:bg-[#1f1a17]"
            @click="loadDashboard"
          >
            <span class="material-symbols-outlined text-[18px]">sync</span>
            Làm mới
          </button>
        </div>
      </div>
    </template>

    <div class="space-y-6">
      <div v-if="error" class="retail-card border-red-200 bg-red-50 p-4 text-sm text-red-700 dark:border-red-900/40 dark:bg-red-900/20 dark:text-red-300">
        {{ error }}
      </div>

      <section class="retail-card p-4">
        <div class="grid gap-3 xl:grid-cols-4">
          <label class="flex min-w-0 flex-col gap-1 text-xs font-medium uppercase tracking-wide text-slate-500 dark:text-[#b9aa9a]">
            Xem theo
            <select v-model="overviewMode" class="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm font-medium text-slate-900 dark:border-[#3c342e] dark:bg-[#1f1a17] dark:text-white">
              <option value="day">Theo ngày</option>
              <option value="week">Theo tuần</option>
              <option value="month">Theo tháng</option>
              <option value="year">Theo năm</option>
            </select>
          </label>

          <label class="flex min-w-0 flex-col gap-1 text-xs font-medium uppercase tracking-wide text-slate-500 dark:text-[#b9aa9a]">
            Từ ngày
            <input v-model="startDateInput" type="date" class="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm font-medium text-slate-900 dark:border-[#3c342e] dark:bg-[#1f1a17] dark:text-white" />
          </label>

          <label class="flex min-w-0 flex-col gap-1 text-xs font-medium uppercase tracking-wide text-slate-500 dark:text-[#b9aa9a]">
            Đến ngày
            <input v-model="endDateInput" type="date" class="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm font-medium text-slate-900 dark:border-[#3c342e] dark:bg-[#1f1a17] dark:text-white" />
          </label>

          <label class="flex min-w-0 flex-col gap-1 text-xs font-medium uppercase tracking-wide text-slate-500 dark:text-[#b9aa9a]">
            Thứ tự hiển thị
            <select v-model="overviewSort" class="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm font-medium text-slate-900 dark:border-[#3c342e] dark:bg-[#1f1a17] dark:text-white">
              <option value="newest">Mới nhất ở trên</option>
              <option value="oldest">Cũ nhất ở trên</option>
            </select>
          </label>
        </div>

        <div class="mt-3 flex flex-wrap items-center gap-2">
          <span class="text-xs font-semibold uppercase tracking-wide text-slate-500 dark:text-[#b9aa9a]">Khoảng nhanh</span>
          <button
            v-for="preset in overviewPresets"
            :key="preset.value"
            type="button"
            class="rounded-lg border px-2.5 py-1 text-xs font-semibold transition"
            :class="overviewPreset === preset.value ? 'border-primary bg-primary text-white' : 'border-slate-200 bg-white text-slate-600 hover:border-primary hover:text-primary dark:border-[#3c342e] dark:bg-[#1f1a17] dark:text-[#b9aa9a]'"
            @click="applyOverviewPreset(preset.value)"
          >
            {{ preset.label }}
          </button>
        </div>

        <div class="mt-4 grid gap-3 md:grid-cols-2">
          <div class="rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm dark:border-[#3c342e] dark:bg-[#181310]">
            <p class="text-xs uppercase tracking-wide text-slate-500 dark:text-[#b9aa9a]">Khoảng đang xem</p>
            <p class="mt-1 font-semibold">{{ selectedRangeText }}</p>
          </div>
          <div class="rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm dark:border-[#3c342e] dark:bg-[#181310]">
            <p class="text-xs uppercase tracking-wide text-slate-500 dark:text-[#b9aa9a]">Cách nhóm số liệu</p>
            <p class="mt-1 font-semibold">{{ overviewModeLabel }}</p>
          </div>
        </div>
      </section>

      <div class="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-3">
        <article v-for="item in summaryCards" :key="item.label" class="retail-card min-w-0 p-5 bg-gradient-to-b from-white to-slate-50 dark:from-[#1f1a17] dark:to-[#181310]">
          <div class="flex items-start justify-between gap-3">
            <div class="min-w-0">
              <p class="text-sm text-slate-500 dark:text-[#b9aa9a]">{{ item.label }}</p>
              <p class="mt-1 break-words text-xl font-bold xl:text-2xl">{{ item.value }}</p>
              <p class="mt-2 text-xs text-slate-500 dark:text-[#b9aa9a]">{{ item.note }}</p>
            </div>
            <div class="flex size-10 items-center justify-center rounded-xl" :class="item.iconBg">
              <span class="material-symbols-outlined" :class="item.iconColor">{{ item.icon }}</span>
            </div>
          </div>
        </article>
      </div>

      <div class="grid grid-cols-1 gap-6 xl:grid-cols-[1.4fr_0.9fr]">
        <section class="retail-card min-w-0 p-5">
          <div class="mb-4 flex flex-wrap items-center justify-between gap-3">
            <div class="min-w-0">
              <h3 class="text-lg font-semibold">Biểu đồ doanh thu {{ overviewModeText }}</h3>
              <p class="text-sm text-slate-500 dark:text-[#b9aa9a]">{{ overviewDescription }}</p>
            </div>
            <div class="text-right text-sm space-y-2">
              <div class="inline-flex rounded-xl border border-slate-200 bg-white p-1 dark:border-[#3c342e] dark:bg-[#1f1a17]">
                <button
                  v-for="item in overviewMetricOptions"
                  :key="item.value"
                  type="button"
                  class="rounded-lg px-2.5 py-1 text-xs font-semibold transition"
                  :class="overviewChartMetric === item.value ? 'bg-primary text-white' : 'text-slate-600 hover:bg-slate-100 dark:text-[#b9aa9a] dark:hover:bg-[#2b241f]'"
                  @click="overviewChartMetric = item.value"
                >
                  {{ item.label }}
                </button>
              </div>
              <p class="text-slate-500 dark:text-[#b9aa9a]">{{ overviewMetricHeadline }}</p>
              <p class="font-semibold text-primary">{{ overviewMetricTotalFormatted }}</p>
            </div>
          </div>

          <div class="mb-4 flex flex-wrap items-center gap-3 text-xs text-slate-500 dark:text-[#b9aa9a]">
            <span class="inline-flex items-center gap-2 rounded-full bg-slate-100 px-3 py-1 dark:bg-[#2b241f]"><span class="h-2.5 w-2.5 rounded-full" :style="{ backgroundColor: overviewMetricColor }"></span>{{ overviewMetricLegend }}</span>
            <span class="inline-flex items-center gap-2 rounded-full bg-slate-100 px-3 py-1 dark:bg-[#2b241f]">Xu hướng theo {{ overviewModeText }}</span>
          </div>

          <div v-if="loading" class="flex items-center justify-center py-16 text-sm text-slate-500 dark:text-[#b9aa9a]">
            Đang tổng hợp dữ liệu...
          </div>

          <div v-else>
            <div class="overflow-x-auto rounded-2xl border border-slate-200 bg-gradient-to-b from-white to-slate-50 p-4 dark:border-[#3c342e] dark:from-[#1f1a17] dark:to-[#181310]">
              <svg viewBox="0 0 640 240" class="h-full min-h-[250px] w-full" preserveAspectRatio="none">
                <line x1="24" y1="200" x2="616" y2="200" stroke="rgba(148,163,184,0.35)" stroke-width="1" />
                <line x1="24" y1="24" x2="24" y2="200" stroke="rgba(148,163,184,0.2)" stroke-width="1" />
                <g v-for="bar in overviewBarSeries" :key="bar.key">
                  <rect :x="bar.x" :y="bar.y" :width="bar.width" :height="bar.height" rx="6" :fill="bar.fill" opacity="0.88" />
                  <text :x="bar.x + bar.width / 2" y="220" text-anchor="middle" fill="currentColor" class="fill-slate-500 text-[10px] dark:fill-[#b9aa9a]">
                    {{ bar.label }}
                  </text>
                </g>
                <path :d="overviewTrendPath" fill="none" :stroke="overviewMetricColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" />
                <g v-for="point in overviewLinePoints" :key="point.key">
                  <circle :cx="point.x" :cy="point.y" r="4.5" fill="#fff" :stroke="overviewMetricColor" stroke-width="2.5" />
                </g>
              </svg>
            </div>
          </div>
        </section>

        <section class="retail-card min-w-0 p-5">
          <div class="mb-4">
            <h3 class="text-lg font-semibold">Tóm tắt kỳ kế toán</h3>
            <p class="text-sm text-slate-500 dark:text-[#b9aa9a]">Trình bày theo kiểu báo cáo lãi lỗ rút gọn để đọc nhanh và chính xác.</p>
          </div>

          <div class="overflow-hidden rounded-2xl border border-slate-200 dark:border-[#3c342e]">
            <table class="w-full text-sm">
              <tbody>
                <tr class="border-b border-slate-200 dark:border-[#3c342e]">
                  <td class="px-4 py-3 text-slate-500 dark:text-[#b9aa9a]">Doanh thu vào</td>
                  <td class="px-4 py-3 text-right font-semibold">{{ formatCurrency(overviewGrossRevenue) }}</td>
                </tr>
                <tr class="border-b border-slate-200 dark:border-[#3c342e]">
                  <td class="px-4 py-3 text-slate-500 dark:text-[#b9aa9a]">Hoàn tiền</td>
                  <td class="px-4 py-3 text-right font-semibold text-red-500 dark:text-red-400">-{{ formatCurrency(overviewRefundRevenue) }}</td>
                </tr>
                <tr class="border-b border-slate-200 bg-slate-50 dark:border-[#3c342e] dark:bg-[#181310]">
                  <td class="px-4 py-3 font-semibold">Doanh thu ròng</td>
                  <td class="px-4 py-3 text-right font-bold" :class="overviewNetRevenue >= 0 ? 'text-green-600 dark:text-green-400' : 'text-red-500 dark:text-red-400'">{{ formatCurrency(overviewNetRevenue) }}</td>
                </tr>
                <tr class="border-b border-slate-200 dark:border-[#3c342e]">
                  <td class="px-4 py-3 text-slate-500 dark:text-[#b9aa9a]">Tỷ lệ hoàn tiền</td>
                  <td class="px-4 py-3 text-right font-semibold">{{ overviewRefundRate }}%</td>
                </tr>
                <tr>
                  <td class="px-4 py-3 text-slate-500 dark:text-[#b9aa9a]">Tổng đơn trong kỳ</td>
                  <td class="px-4 py-3 text-right font-semibold">{{ filteredOverviewOrders.length }}</td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="mt-3 rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm dark:border-[#3c342e] dark:bg-[#181310]">
            <p class="font-medium">Điểm nhấn kỳ này</p>
            <p class="mt-1 text-slate-500 dark:text-[#b9aa9a]">
              {{ currentOverviewBucket.sublabel }} có doanh thu ròng {{ formatCurrency(currentOverviewBucket.netRevenue) }}, chênh lệch {{ overviewDelta >= 0 ? '+' : '' }}{{ formatCurrency(overviewDelta) }} so với mốc cuối kỳ.
            </p>
          </div>
        </section>
      </div>


    </div>
  </AdminShell>
</template>

<script setup>
import { computed, onMounted, ref, watch } from "vue";

import AdminShell from "@/components/admin/AdminShell.vue";
import { adminApi } from "@/services/api";
import {
  buildRevenueBuckets,
  filterOrdersByDateRange,
  formatCurrency,
  formatLongDateTime,
  getOrderPaymentImpact,
  paymentStatusLabel,
  sortRevenueBuckets,
} from "@/utils/adminAnalytics";

const loading = ref(false);
const error = ref("");
const orders = ref([]);
const productsCount = ref(0);
const customersCount = ref(0);
const overviewMode = ref("day");
const overviewSort = ref("newest");
const overviewChartMetric = ref("net");
const overviewPreset = ref("this_month");
const startDateInput = ref(defaultStartDateValue());
const endDateInput = ref(defaultEndDateValue());

const overviewPresets = [
  { value: "7d", label: "7 ngày" },
  { value: "30d", label: "30 ngày" },
  { value: "qtd", label: "Quý này" },
  { value: "ytd", label: "Năm nay" },
  { value: "this_month", label: "Tháng này" },
];

function defaultEndDateValue() {
  return formatDateInput(new Date());
}

function defaultStartDateValue() {
  const today = new Date();
  return formatDateInput(new Date(today.getFullYear(), today.getMonth(), 1));
}

function formatDateInput(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

function parseDateInput(value, fallbackDate) {
  if (!value) {
    return new Date(fallbackDate);
  }

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return new Date(fallbackDate);
  }

  return date;
}

function formatDateCell(value) {
  return new Intl.DateTimeFormat("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  }).format(value);
}

function createCsvContent(rows) {
  return `\uFEFF${rows
    .map((row) => row.map((cell) => `"${String(cell).replace(/"/g, '""')}"`).join(";"))
    .join("\r\n")}`;
}

function emptyBucket() {
  return {
    sublabel: "Không có dữ liệu",
    orderCount: 0,
    paidCount: 0,
    refundedCount: 0,
    netRevenue: 0,
  };
}

const selectedStartDate = computed(() => parseDateInput(startDateInput.value, new Date()));
const selectedEndDate = computed(() => parseDateInput(endDateInput.value, new Date()));

watch([startDateInput, endDateInput], () => {
  if (selectedStartDate.value.getTime() > selectedEndDate.value.getTime()) {
    endDateInput.value = startDateInput.value;
  }

  overviewPreset.value = "custom";
});

function applyOverviewPreset(preset) {
  const now = new Date();
  const end = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  let start = new Date(end);

  if (preset === "7d") {
    start.setDate(end.getDate() - 6);
    overviewMode.value = "day";
  } else if (preset === "30d") {
    start.setDate(end.getDate() - 29);
    overviewMode.value = "day";
  } else if (preset === "qtd") {
    const quarterStartMonth = Math.floor(end.getMonth() / 3) * 3;
    start = new Date(end.getFullYear(), quarterStartMonth, 1);
    overviewMode.value = "month";
  } else if (preset === "ytd") {
    start = new Date(end.getFullYear(), 0, 1);
    overviewMode.value = "month";
  } else {
    start = new Date(end.getFullYear(), end.getMonth(), 1);
    overviewMode.value = "day";
  }

  overviewPreset.value = preset;
  startDateInput.value = formatDateInput(start);
  endDateInput.value = formatDateInput(end);
}

const filteredOverviewOrders = computed(() => {
  return filterOrdersByDateRange(orders.value, selectedStartDate.value, selectedEndDate.value);
});

const overviewBuckets = computed(() => {
  return buildRevenueBuckets(filteredOverviewOrders.value, {
    mode: overviewMode.value,
    startDate: selectedStartDate.value,
    endDate: selectedEndDate.value,
  });
});

const sortedOverviewBuckets = computed(() => sortRevenueBuckets(overviewBuckets.value, overviewSort.value));

const overviewNetRevenue = computed(() => {
  return overviewBuckets.value.reduce((sum, item) => sum + item.netRevenue, 0);
});

const overviewGrossRevenue = computed(() => {
  return overviewBuckets.value.reduce((sum, item) => sum + item.grossRevenue, 0);
});

const overviewRefundRevenue = computed(() => {
  return overviewBuckets.value.reduce((sum, item) => sum + item.refundedRevenue, 0);
});

const overviewRefundRate = computed(() => {
  if (overviewGrossRevenue.value <= 0) return 0;
  return Math.round((overviewRefundRevenue.value / overviewGrossRevenue.value) * 1000) / 10;
});

const currentOverviewBucket = computed(() => {
  return sortedOverviewBuckets.value.length > 0 ? sortedOverviewBuckets.value[0] : emptyBucket();
});

const oldestOverviewBucket = computed(() => {
  return sortedOverviewBuckets.value.length > 0 ? sortedOverviewBuckets.value[sortedOverviewBuckets.value.length - 1] : emptyBucket();
});

const overviewDelta = computed(() => currentOverviewBucket.value.netRevenue - oldestOverviewBucket.value.netRevenue);

const latestVisibleOrders = computed(() => {
  return filteredOverviewOrders.value
    .slice()
    .sort((left, right) => new Date(right.createdAt || 0) - new Date(left.createdAt || 0))
    .slice(0, 8);
});

const selectedRangeText = computed(() => `${formatDateCell(selectedStartDate.value)} đến ${formatDateCell(selectedEndDate.value)}`);

const overviewModeLabel = computed(() => {
  if (overviewMode.value === "week") return "Theo tuần";
  if (overviewMode.value === "month") return "Theo tháng";
  if (overviewMode.value === "year") return "Theo năm";
  return "Theo ngày";
});

const overviewModeText = computed(() => {
  if (overviewMode.value === "week") return "theo tuần";
  if (overviewMode.value === "month") return "theo tháng";
  if (overviewMode.value === "year") return "theo năm";
  return "theo ngày";
});

const overviewColumnLabel = computed(() => {
  if (overviewMode.value === "week") return "Tuần";
  if (overviewMode.value === "month") return "Tháng";
  if (overviewMode.value === "year") return "Năm";
  return "Ngày";
});

const overviewDescription = computed(() => {
  if (overviewMode.value === "week") return "Mỗi cột là dòng một tuần nằm trong khoảng ngày bạn đã chọn.";
  if (overviewMode.value === "month") return "Mỗi cột là dòng một tháng nằm trong khoảng ngày bạn đã chọn.";
  if (overviewMode.value === "year") return "Mỗi cột là dòng một năm nằm trong khoảng ngày bạn đã chọn.";
  return "Mỗi cột là dòng một ngày nằm trong khoảng ngày bạn đã chọn.";
});

const overviewMetricOptions = [
  { value: "net", label: "Ròng" },
  { value: "gross", label: "Doanh thu" },
  { value: "refund", label: "Hoàn" },
  { value: "orders", label: "Đơn" },
];

function getOverviewMetricValue(item) {
  if (!item) return 0;
  if (overviewChartMetric.value === "gross") return Number(item.grossRevenue || 0);
  if (overviewChartMetric.value === "refund") return Number(item.refundedRevenue || 0);
  if (overviewChartMetric.value === "orders") return Number(item.orderCount || 0);
  return Number(item.netRevenue || 0);
}

const overviewMetricColor = computed(() => {
  if (overviewChartMetric.value === "gross") return "#2563eb";
  if (overviewChartMetric.value === "refund") return "#ef4444";
  if (overviewChartMetric.value === "orders") return "#7c3aed";
  return "#111827";
});

const overviewMetricLegend = computed(() => {
  if (overviewChartMetric.value === "gross") return "Doanh thu vào";
  if (overviewChartMetric.value === "refund") return "Giá trị hoàn tiền";
  if (overviewChartMetric.value === "orders") return "Số lượng đơn";
  return "Doanh thu ròng";
});

const overviewMetricHeadline = computed(() => {
  if (overviewChartMetric.value === "gross") return "Tổng doanh thu vào trong khoảng";
  if (overviewChartMetric.value === "refund") return "Tổng giá trị hoàn tiền trong khoảng";
  if (overviewChartMetric.value === "orders") return "Tổng số đơn trong khoảng";
  return "Tổng doanh thu ròng trong khoảng";
});

const overviewMetricTotal = computed(() => {
  if (overviewChartMetric.value === "gross") return overviewGrossRevenue.value;
  if (overviewChartMetric.value === "refund") return overviewRefundRevenue.value;
  if (overviewChartMetric.value === "orders") return filteredOverviewOrders.value.length;
  return overviewNetRevenue.value;
});

const overviewMetricTotalFormatted = computed(() => {
  if (overviewChartMetric.value === "orders") return `${overviewMetricTotal.value} đơn`;
  return formatCurrency(overviewMetricTotal.value);
});

const summaryCards = computed(() => {
  const avgNetPerOrder = filteredOverviewOrders.value.length > 0 ? Math.round(overviewNetRevenue.value / filteredOverviewOrders.value.length) : 0;

  return [
    {
      label: "Tổng doanh thu ròng trong khoảng",
      value: formatCurrency(overviewNetRevenue.value),
      note: selectedRangeText.value,
      icon: "insights",
      iconBg: "bg-green-100 dark:bg-green-900/20",
      iconColor: "text-green-600 dark:text-green-400",
    },
    {
      label: "Tổng đơn trong khoảng",
      value: String(filteredOverviewOrders.value.length),
      note: `TB ròng/đơn: ${formatCurrency(avgNetPerOrder)}`,
      icon: "shopping_bag",
      iconBg: "bg-orange-100 dark:bg-orange-900/20",
      iconColor: "text-orange-600 dark:text-orange-400",
    },
    {
      label: "Tỷ lệ hoàn tiền",
      value: `${overviewRefundRate.value}%`,
      note: `Giá trị hoàn tiền: ${formatCurrency(overviewRefundRevenue.value)}`,
      icon: "refresh",
      iconBg: "bg-red-100 dark:bg-red-900/20",
      iconColor: "text-red-600 dark:text-red-400",
    },
  ];
});

const overviewBarSeries = computed(() => {
  const values = sortedOverviewBuckets.value.map((item) => Math.abs(getOverviewMetricValue(item)));
  const maxValue = Math.max(...values, 1);
  const count = Math.max(sortedOverviewBuckets.value.length, 1);
  const step = count > 10 ? 42 : 82;
  const width = count > 10 ? 20 : 42;

  return sortedOverviewBuckets.value.map((item, index) => {
    const metricValue = getOverviewMetricValue(item);
    const height = Math.max(10, (Math.abs(metricValue) / maxValue) * 150);
    return {
      key: item.key,
      label: item.label,
      x: 52 + index * step,
      y: 200 - height,
      width,
      height,
      fill: metricValue >= 0 ? overviewMetricColor.value : "#ef4444",
    };
  });
});

const overviewLinePoints = computed(() => {
  const values = sortedOverviewBuckets.value.map((item) => getOverviewMetricValue(item));
  const maxValue = Math.max(...values, 1);
  const minValue = Math.min(...values, 0);
  const range = Math.max(maxValue - minValue, 1);
  const count = Math.max(sortedOverviewBuckets.value.length, 1);
  const step = count > 10 ? 42 : 82;
  const width = count > 10 ? 20 : 42;

  return sortedOverviewBuckets.value.map((item, index) => ({
    key: `${item.key}-point`,
    x: 52 + index * step + (width / 2),
    y: 190 - (((getOverviewMetricValue(item) - minValue) / range) * 140),
  }));
});

const overviewTrendPath = computed(() => {
  return overviewLinePoints.value.map((point, index) => `${index === 0 ? "M" : "L"}${point.x},${point.y}`).join(" ");
});

async function loadDashboard() {
  loading.value = true;
  error.value = "";

  try {
    const [ordersData, productsData, customersData] = await Promise.all([
      adminApi.getOrders({ page: 0, size: 800 }),
      adminApi.getProducts({ page: 0, size: 500 }),
      adminApi.getCustomerAccounts(),
    ]);

    orders.value = Array.isArray(ordersData) ? ordersData : [];
    productsCount.value = Array.isArray(productsData) ? productsData.length : 0;
    customersCount.value = Array.isArray(customersData) ? customersData.length : 0;
  } catch (err) {
    console.error("Failed to load dashboard:", err);
    error.value = "Không thể tải tổng quan kế toán. Vui lòng kiểm tra backend API và thử lại.";
  } finally {
    loading.value = false;
  }
}

function buildOverviewCsvRows(mode = "detailed") {
  const rows = [
    ["TỔNG QUAN KẾ TOÁN"],
    ["Generated At", new Date().toISOString()],
    ["Khoảng xem", selectedRangeText.value],
    ["Nhóm số liệu", overviewModeLabel.value],
    ["Thứ tự hiển thị", overviewSort.value === "oldest" ? "Cũ nhất ở trên" : "Mới nhất ở trên"],
    ["Xuất lúc", formatLongDateTime(new Date())],
    [],
    ["TÓM TẮT KPI"],
    ["KPI", "Giá trị số"],
    ["Doanh thu vào", overviewGrossRevenue.value],
    ["Hoàn tiền", overviewRefundRevenue.value],
    ["Doanh thu ròng", overviewNetRevenue.value],
    ["Tỷ lệ hoàn tiền (%)", overviewRefundRate.value],
    ["Tổng đơn", filteredOverviewOrders.value.length],
  ];

  if (mode === "summary") {
    return rows;
  }

  rows.push([]);
  rows.push(["DIỄN BIẾN THEO MỐC"]);
  rows.push([
    "period_key",
    "period_label",
    "from_date_iso",
    "to_date_iso",
    "order_count",
    "paid_count",
    "refunded_count",
    "gross_revenue",
    "refunded_revenue",
    "net_revenue",
    "refund_rate_percent",
  ]);

  for (const item of sortedOverviewBuckets.value) {
    rows.push([
      item.key,
      item.sublabel,
      item.startDate instanceof Date ? item.startDate.toISOString() : "",
      item.endDate instanceof Date ? item.endDate.toISOString() : "",
      item.orderCount,
      item.paidCount,
      item.refundedCount,
      item.grossRevenue,
      item.refundedRevenue,
      item.netRevenue,
      item.grossRevenue > 0 ? Math.round((item.refundedRevenue / item.grossRevenue) * 1000) / 10 : 0,
    ]);
  }

  if (mode === "audit") {
    rows.push([]);
    rows.push(["KIỂM TRA CHẤT LƯỢNG DỮ LIỆU"]);
    rows.push(["field", "value"]);
    rows.push(["total_orders_loaded", orders.value.length]);
    rows.push(["filtered_orders", filteredOverviewOrders.value.length]);
    rows.push(["bucket_count", sortedOverviewBuckets.value.length]);
    rows.push(["products_count", productsCount.value]);
    rows.push(["customers_count", customersCount.value]);
    rows.push(["group_mode", overviewMode.value]);
    rows.push(["sort_mode", overviewSort.value]);
  }

  return rows;
}

function downloadOverviewCsv(rows, suffix) {
  const csv = createCsvContent(rows);
  const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = `tong-quan-ke-toan-${suffix}-${overviewMode.value}-${startDateInput.value}-den-${endDateInput.value}.csv`;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}

function exportOverviewSummaryCsv() {
  downloadOverviewCsv(buildOverviewCsvRows("summary"), "summary");
}

function exportOverviewCsv() {
  downloadOverviewCsv(buildOverviewCsvRows("detailed"), "detailed");
}

function exportOverviewAuditCsv() {
  downloadOverviewCsv(buildOverviewCsvRows("audit"), "audit");
}

function escapedPrint(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

function printOverview() {
  const chartBars = overviewBarSeries.value
    .map(
      (bar) => `
        <rect x="${bar.x}" y="${bar.y}" width="${bar.width}" height="${bar.height}" rx="6" fill="${bar.fill}" opacity="0.88" />
        <text x="${bar.x + bar.width / 2}" y="220" text-anchor="middle" fill="#64748b" font-size="10">${escapedPrint(bar.label)}</text>
      `,
    )
    .join("");

  const chartPoints = overviewLinePoints.value
    .map((point) => `<circle cx="${point.x}" cy="${point.y}" r="4.5" fill="#fff" stroke="${overviewMetricColor.value}" stroke-width="2.5" />`)
    .join("");

  const bucketRows = sortedOverviewBuckets.value
    .map(
      (row) => `
        <tr>
          <td>${escapedPrint(row.sublabel)}</td>
          <td>${escapedPrint(formatDateCell(row.startDate))}</td>
          <td>${escapedPrint(formatDateCell(row.endDate))}</td>
          <td class="right">${escapedPrint(row.orderCount)}</td>
          <td class="right">${escapedPrint(row.paidCount)}</td>
          <td class="right">${escapedPrint(row.refundedCount)}</td>
          <td class="right">${escapedPrint(formatCurrency(row.grossRevenue))}</td>
          <td class="right">${escapedPrint(formatCurrency(row.refundedRevenue))}</td>
          <td class="right">${escapedPrint(formatCurrency(row.netRevenue))}</td>
        </tr>
      `,
    )
    .join("");

  const recentRows = latestVisibleOrders.value
    .slice(0, 8)
    .map(
      (order) => `
        <tr>
          <td>#${escapedPrint(order.id || "-")}</td>
          <td>${escapedPrint(paymentStatusLabel(order.paymentStatus))}</td>
          <td class="right">${escapedPrint(formatCurrency(getOrderPaymentImpact(order)))}</td>
          <td>${escapedPrint(formatLongDateTime(order.createdAt))}</td>
        </tr>
      `,
    )
    .join("");

  const html = `<!doctype html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <title>In tổng quan kế toán</title>
    <style>
      @page { margin: 10mm; size: A4; }
      * { box-sizing: border-box; }
      body { margin: 0; font-family: Arial, sans-serif; color: #0f172a; background: #fff; }
      .shell { padding: 10mm; }
      h1 { margin: 0 0 6px; font-size: 20px; }
      .meta { margin: 0 0 14px; color: #475569; font-size: 12px; }
      h2 { margin: 14px 0 8px; font-size: 15px; }
      table { width: 100%; border-collapse: collapse; table-layout: fixed; }
      th, td { border: 1px solid #cbd5e1; padding: 6px 8px; font-size: 11px; vertical-align: top; }
      th { background: #f8fafc; text-align: left; }
      .right { text-align: right; }
      .summary td:first-child { width: 48%; }
      .summary td:last-child { width: 52%; }
      .section { margin-top: 10px; }
      .chart-box { border: 1px solid #cbd5e1; border-radius: 8px; padding: 8px; }
      tr { page-break-inside: avoid; break-inside: avoid; }
      thead { display: table-header-group; }
    </style>
  </head>
  <body>
    <div class="shell">
      <h1>Tổng quan kế toán tại thời điểm in</h1>
      <p class="meta">Khoảng: ${escapedPrint(selectedRangeText.value)} | Nhóm: ${escapedPrint(overviewModeLabel.value)} | Sắp xếp: ${escapedPrint(overviewSort.value === "oldest" ? "Cũ nhất ở trên" : "Mới nhất ở trên")} | Chỉ số chart: ${escapedPrint(overviewMetricLegend.value)}</p>

      <div class="section">
        <h2>Tóm tắt KPI</h2>
        <table class="summary">
          <tbody>
            <tr><td>Doanh thu vào</td><td class="right">${escapedPrint(formatCurrency(overviewGrossRevenue.value))}</td></tr>
            <tr><td>Hoàn tiền</td><td class="right">-${escapedPrint(formatCurrency(overviewRefundRevenue.value))}</td></tr>
            <tr><td>Doanh thu ròng</td><td class="right">${escapedPrint(formatCurrency(overviewNetRevenue.value))}</td></tr>
            <tr><td>Tỷ lệ hoàn tiền</td><td class="right">${escapedPrint(overviewRefundRate.value)}%</td></tr>
            <tr><td>Tổng đơn trong kỳ</td><td class="right">${escapedPrint(filteredOverviewOrders.value.length)}</td></tr>
            <tr><td>Mốc gần nhất</td><td class="right">${escapedPrint(currentOverviewBucket.value.sublabel)} (${escapedPrint(formatCurrency(currentOverviewBucket.value.netRevenue))})</td></tr>
          </tbody>
        </table>
      </div>

      <div class="section">
        <h2>Biểu đồ doanh thu ${escapedPrint(overviewModeText.value)}</h2>
        <div class="chart-box">
          <svg viewBox="0 0 640 240" width="100%" height="250" preserveAspectRatio="none">
            <line x1="24" y1="200" x2="616" y2="200" stroke="rgba(148,163,184,0.35)" stroke-width="1" />
            <line x1="24" y1="24" x2="24" y2="200" stroke="rgba(148,163,184,0.2)" stroke-width="1" />
            ${chartBars}
            <path d="${escapedPrint(overviewTrendPath.value)}" fill="none" stroke="${escapedPrint(overviewMetricColor.value)}" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" />
            ${chartPoints}
          </svg>
        </div>
      </div>

      <div class="section">
        <h2>Bảng doanh thu ${escapedPrint(overviewModeText.value)}</h2>
        <table>
          <thead>
            <tr>
              <th>${escapedPrint(overviewColumnLabel.value)}</th>
              <th>Từ ngày</th>
              <th>Đến ngày</th>
              <th class="right">Tổng đơn</th>
              <th class="right">Đơn đã thanh toán</th>
              <th class="right">Đơn hoàn tiền</th>
              <th class="right">Doanh thu vào</th>
              <th class="right">Hoàn tiền</th>
              <th class="right">Doanh thu ròng</th>
            </tr>
          </thead>
          <tbody>${bucketRows || '<tr><td colspan="9">Không có dữ liệu</td></tr>'}</tbody>
        </table>
      </div>

      <div class="section">
        <h2>Đơn gần nhất</h2>
        <table>
          <thead>
            <tr>
              <th>Mã đơn</th>
              <th>Thanh toán</th>
              <th class="right">Ròng</th>
              <th>Thời gian</th>
            </tr>
          </thead>
          <tbody>${recentRows || '<tr><td colspan="4">Không có dữ liệu</td></tr>'}</tbody>
        </table>
      </div>
    </div>
  </body>
</html>`;

  const iframe = document.createElement("iframe");
  iframe.style.position = "fixed";
  iframe.style.width = "0";
  iframe.style.height = "0";
  iframe.style.border = "0";
  iframe.style.right = "0";
  iframe.style.bottom = "0";
  document.body.appendChild(iframe);

  const cleanup = () => {
    setTimeout(() => {
      if (document.body.contains(iframe)) {
        document.body.removeChild(iframe);
      }
    }, 500);
  };

  iframe.onload = () => {
    const w = iframe.contentWindow;
    if (!w) {
      cleanup();
      return;
    }
    w.focus();
    w.print();
    cleanup();
  };

  iframe.srcdoc = html;
}

onMounted(loadDashboard);
</script>

<style scoped>
@media print {
  :deep(header),
  :deep(nav),
  :deep(.topbar),
  :deep(button),
  :deep(details) {
    display: none !important;
  }

  :deep(.retail-card) {
    border: 1px solid #d1d5db !important;
    box-shadow: none !important;
    break-inside: avoid;
    page-break-inside: avoid;
    background: #ffffff !important;
    color: #0f172a !important;
  }

  :deep(table) {
    font-size: 11px !important;
  }

  :deep(th),
  :deep(td) {
    padding-top: 6px !important;
    padding-bottom: 6px !important;
  }

  :deep(svg) {
    max-height: 220px !important;
  }

  :deep(body),
  :deep(html) {
    background: #ffffff !important;
  }
}
</style>