<template>
  <div class="retail-shell min-h-screen">
    <MainTopBar />

    <main class="mx-auto flex w-full max-w-3xl flex-col gap-8 px-4 py-10 md:px-8 xl:px-10">
      <section>
        
        <article class="retail-card px-6 py-7 sm:px-8 lg:px-9 lg:py-9">
          <span class="retail-kicker">THÔNG TIN BẢO HÀNH</span>
          <h1 class="retail-heading mt-4 text-[42px] leading-[0.94] text-slate-900 dark:text-white sm:text-[50px]">
            Nhập mã bảo hành
          </h1>
          <p class="mt-5 text-base leading-8 text-slate-600 dark:text-[#cabdae]">
            Mã bảo hành được cấp khi đơn hàng hoàn thành thành công. Hãy nhập mã để xem thông tin chi tiết.
          </p>

          <form @submit.prevent="checkWarranty" class="mt-8 flex flex-col gap-3 sm:flex-row">
            <input
              v-model="warrantyCode"
              type="text"
              placeholder="Nhập mã bảo hành"
              class="flex-1 rounded-2xl border border-slate-200 bg-white px-5 py-4 text-slate-900 outline-none transition focus:border-primary focus:ring-2 focus:ring-primary/20 dark:border-[#3c342e] dark:bg-[#181310] dark:text-white"
              required
            />
            <button
              type="submit"
              class="rounded-2xl bg-primary px-8 py-4 font-bold text-white transition hover:bg-primary-hover disabled:opacity-50 flex items-center justify-center gap-2"
              :disabled="isLoading"
            >
              <span v-if="isLoading" class="material-symbols-outlined animate-spin text-[20px]">progress_activity</span>
              {{ isLoading ? 'Đang tải...' : 'Tra cứu' }}
            </button>
          </form>

          <div v-if="error" class="mt-6 rounded-2xl bg-red-50 p-4 text-sm font-medium text-red-600 dark:bg-red-900/20 dark:text-red-400">
            {{ error }}
          </div>

          <!-- RESULT -->
          <div v-if="warrantyInfo" class="mt-8 rounded-[24px] border border-green-200 bg-green-50 p-6 dark:border-green-900/30 dark:bg-[#241d19]/80">
            <div class="flex items-center gap-3 mb-6 border-b border-green-200/50 pb-4 dark:border-[#3c342e]">
              <div class="flex size-12 items-center justify-center rounded-full bg-green-100 text-green-600 dark:bg-green-900/40 dark:text-green-400">
                <span class="material-symbols-outlined text-2xl">verified</span>
              </div>
              <div>
                <h3 class="text-lg font-bold text-green-900 dark:text-green-400">Chứng nhận bảo hành hợp lệ</h3>
                <p class="text-sm font-medium text-green-700 dark:text-green-500">Mã: <span class="font-mono font-bold">{{ warrantyInfo.code }}</span></p>
              </div>
            </div>

            <div class="grid gap-4 sm:grid-cols-2">
              <div>
                <p class="text-[11px] font-semibold uppercase tracking-[0.2em] text-green-600/70 dark:text-green-500/70">Đơn hàng</p>
                <p class="mt-1 font-bold text-green-900 dark:text-green-400">#{{ warrantyInfo.orderId }}</p>
              </div>
              <div>
                <p class="text-[11px] font-semibold uppercase tracking-[0.2em] text-green-600/70 dark:text-green-500/70">Thời hạn bảo hành</p>
                <p class="mt-1 font-bold text-green-900 dark:text-green-400">12 tháng</p>
              </div>
              <div>
                <p class="text-[11px] font-semibold uppercase tracking-[0.2em] text-green-600/70 dark:text-green-500/70">Ngày bắt đầu</p>
                <p class="mt-1 font-medium text-green-800 dark:text-green-500">{{ formatDate(warrantyInfo.startDate) }}</p>
              </div>
              <div>
                <p class="text-[11px] font-semibold uppercase tracking-[0.2em] text-green-600/70 dark:text-green-500/70">Ngày kết thúc</p>
                <p class="mt-1 font-medium text-green-800 dark:text-green-500">{{ formatDate(warrantyInfo.endDate) }}</p>
              </div>
            </div>
            
            <div class="mt-6 pt-5 border-t border-green-200/50 dark:border-[#3c342e]">
               <p class="text-[11px] font-semibold uppercase tracking-[0.2em] text-green-600/70 dark:text-green-500/70 mb-3">Sản phẩm được bảo hành</p>
               <ul class="space-y-3">
                 <li v-for="item in warrantyInfo.items" :key="item.id" class="flex items-start gap-4 p-3 rounded-xl border border-green-200/50 bg-white dark:border-[#3c342e] dark:bg-[#1f1a17]">
                   <img :src="item.imageUrl" alt="Product Image" class="size-16 object-cover rounded-lg border border-slate-100 dark:border-[#3c342e]" />
                   <div class="flex-1 min-w-0">
                     <p class="text-sm font-bold text-slate-900 dark:text-white truncate" :title="item.name">{{ item.name }}</p>
                     <p class="text-xs text-slate-500 dark:text-[#b9aa9a] mt-1">
                       <span v-if="item.color">{{ item.color }}</span>
                       <span v-if="item.color && item.size"> · </span>
                       <span v-if="item.size">Size {{ item.size }}</span>
                     </p>
                   </div>
                   <div class="text-right shrink-0">
                     <p class="text-sm font-black text-primary">{{ formatCurrency(item.price) }}</p>
                     <p class="text-xs font-semibold text-slate-400 mt-1">SL: {{ item.quantity }}</p>
                   </div>
                 </li>
               </ul>
            </div>
          </div>
        </article>

      </section>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ordersApi } from '@/services/api'
import MainTopBar from '@/components/MainTopBar.vue'

const warrantyCode = ref('')
const isLoading = ref(false)
const error = ref('')
const warrantyInfo = ref(null)

async function checkWarranty() {
  const code = warrantyCode.value.trim().toUpperCase()
  if (!code) return

  error.value = ''
  warrantyInfo.value = null
  isLoading.value = true

  try {
    const match = code.match(/^BH-(\d+)-12T$/)
    if (!match) {
      throw new Error('Mã bảo hành không hợp lệ. Vui lòng kiểm tra lại.')
    }

    const orderId = match[1]
    
    const order = await ordersApi.getOrderById(orderId)
    
    if (!order) {
      throw new Error('Không tìm thấy thông tin đơn hàng cho mã bảo hành này.')
    }

    const status = String(order.status || '').toUpperCase()
    if (status !== 'SUCCESS' && status !== 'THÀNH CÔNG' && status !== 'RETURNED' && status !== 'DELIVERED') {
      throw new Error('Đơn hàng chưa hoàn thành, chưa đủ điều kiện kích hoạt bảo hành.')
    }

    const startDate = new Date(order.createdAt || order.created_at)
    const endDate = new Date(startDate)
    endDate.setMonth(endDate.getMonth() + 12)

    warrantyInfo.value = {
      code,
      orderId,
      startDate,
      endDate,
      items: (order.items || order.orderDetails || order.orderItems || []).map(item => ({
        id: item.id || item.productId || item.product_id || Math.random(),
        name: item.productName || item.product_name || item.name || item.variant?.product?.name || item.variant?.productName || 'Sản phẩm',
        color: item.color || item.colorName || item.color_name || item.variant?.color?.name || item.variant?.colorName || '',
        size: item.size || item.sizeName || item.size_name || item.variant?.size?.name || item.variant?.sizeName || '',
        price: Number(item.price ?? item.unitPrice ?? item.variant?.price ?? 0),
        quantity: Number(item.quantity ?? item.qty ?? 1) || 1,
        imageUrl: item.imageUrl || item.image_url || item.variant?.imageUrl || item.variant?.product?.imageUrl || item.product?.imageUrl || 'https://placehold.co/100x100?text=No+Image'
      }))
    }
  } catch (err) {
    error.value = err.message || 'Đã xảy ra lỗi khi kiểm tra bảo hành.'
  } finally {
    isLoading.value = false
  }
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  return new Intl.DateTimeFormat('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  }).format(new Date(dateStr))
}

function formatCurrency(value) {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(value);
}
</script>
