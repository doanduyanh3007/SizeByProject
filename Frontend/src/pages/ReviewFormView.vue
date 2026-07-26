<template>
  <div class="bg-background-light dark:bg-background-dark text-slate-900 dark:text-white font-display min-h-screen flex flex-col">
    <MainTopBar />

    <main class="flex-1 max-w-[800px] mx-auto w-full px-4 md:px-8 py-10">
      <nav class="text-xs text-slate-500 dark:text-[#9cabba] mb-4 flex items-center gap-1">
        <router-link class="hover:text-primary" to="/">Trang chủ</router-link>
        <span>/</span>
        <router-link class="hover:text-primary" to="/shop">Cửa hàng</router-link>
        <span>/</span>
        <router-link class="hover:text-primary" :to="`/product/${productId}`">{{ productName || 'Sản phẩm' }}</router-link>
        <span>/</span>
        <span class="text-slate-700 dark:text-white">{{ existingReview ? 'Sửa đánh giá' : 'Viết đánh giá' }}</span>
      </nav>

      <div class="bg-white dark:bg-[#18212b] rounded-xl border border-slate-200 dark:border-[#283039] p-6 md:p-8 flex flex-col gap-6">
        <div class="flex flex-col gap-1">
          <h1 class="text-2xl md:text-3xl font-black tracking-tight">
            {{ existingReview ? 'Sửa đánh giá của bạn' : 'Viết đánh giá' }}
          </h1>
          <p class="text-slate-500 dark:text-[#9cabba] text-sm">
            {{ existingReview ? 'Bạn có thể sửa hoặc xóa đánh giá cũ.' : 'Chia sẻ cảm nhận để người mua sau dễ chọn hơn.' }}
          </p>
        </div>

        <!-- Warning for existing review -->
        <div v-if="existingReview" class="rounded-lg border border-yellow-300 dark:border-yellow-600 bg-yellow-50 dark:bg-yellow-900/20 px-4 py-3 flex items-start gap-3">
          <svg class="w-5 h-5 text-yellow-600 dark:text-yellow-400 flex-shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
          <div class="flex-1 text-sm">
            <p class="font-semibold text-yellow-800 dark:text-yellow-200">Bạn đã đánh giá sản phẩm này rồi</p>
            <p class="text-yellow-700 dark:text-yellow-300 mt-1">Nếu cần, bạn có thể sửa hoặc xóa ở bên dưới.</p>
          </div>
        </div>

        <div v-if="productName" class="flex items-center gap-4 p-4 rounded-lg bg-slate-50 dark:bg-[#111418] border border-slate-200 dark:border-[#283039]">
          <div class="w-16 h-16 rounded-lg overflow-hidden bg-slate-100 dark:bg-[#283039]">
            <img :alt="productName" class="w-full h-full object-cover" :src="productImage">
          </div>
          <div class="flex flex-col">
            <span class="text-xs uppercase tracking-wide text-slate-500 dark:text-[#9cabba]">Đang đánh giá</span>
            <span class="font-semibold text-slate-900 dark:text-white">{{ productName }}</span>
          </div>
        </div>

        <div v-if="reviewerName" class="rounded-lg border border-slate-200 dark:border-[#283039] bg-slate-50 dark:bg-[#111418] px-4 py-3 text-sm text-slate-600 dark:text-[#c8d0d8]">
          Đang đánh giá với tài khoản <span class="font-semibold text-slate-900 dark:text-white">{{ reviewerName }}</span>
        </div>

        <form class="flex flex-col gap-5" @submit.prevent="handleSubmit">
          <div class="flex flex-col gap-2">
            <label class="text-sm font-medium text-slate-900 dark:text-white">Số sao</label>
            <div class="flex items-center gap-2">
              <button
                v-for="n in 5"
                :key="n"
                class="text-2xl"
                :class="n <= rating ? 'text-yellow-400' : 'text-slate-300 dark:text-[#3b4754]'"
                type="button"
                @click="rating = n"
              >
                ★
              </button>
              <span class="text-sm text-slate-500 dark:text-[#9cabba]">
                {{ rating ? `${rating} / 5` : 'Bấm để chọn sao' }}
              </span>
            </div>
          </div>

          <div class="flex flex-col gap-2">
            <label class="text-sm font-medium text-slate-900 dark:text-white">Nội dung đánh giá</label>
            <textarea
              v-model="comment"
              class="w-full rounded-lg border border-slate-200 dark:border-[#3b4754] bg-white dark:bg-[#1b2127] px-3 py-2.5 text-sm text-slate-900 dark:text-white placeholder:text-slate-400 dark:placeholder:text-[#9cabba] focus:outline-none focus:ring-2 focus:ring-primary/60 min-h-[120px] resize-y"
              placeholder="Bạn thấy form giày, độ êm và trải nghiệm khi mang như thế nào?"
              required
            />
          </div>

          <p v-if="submitError" class="text-sm text-red-600 dark:text-red-400 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg px-3 py-2">
            {{ submitError }}
          </p>

          <p v-if="successMessage" class="text-sm text-green-600 dark:text-green-400 bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800 rounded-lg px-3 py-2">
            {{ successMessage }}
          </p>

          <div class="flex gap-3 mt-2">
            <button
              class="flex-1 inline-flex items-center justify-center rounded-lg bg-primary hover:bg-primary-hover text-white text-sm font-semibold px-5 py-2.5 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              :disabled="!rating || !comment || isSubmitting"
              type="submit"
            >
              <span v-if="isSubmitting" class="flex items-center gap-2">
                <svg class="animate-spin h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                {{ existingReview ? 'Đang cập nhật...' : 'Đang gửi...' }}
              </span>
              <span v-else>{{ existingReview ? 'Cập nhật đánh giá' : 'Gửi đánh giá' }}</span>
            </button>

            <button
              v-if="existingReview"
              class="inline-flex items-center justify-center rounded-lg bg-red-600 hover:bg-red-700 text-white text-sm font-semibold px-5 py-2.5 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              :disabled="isSubmitting"
              type="button"
              @click="handleDelete"
            >
              <span v-if="isDeleting" class="flex items-center gap-2">
                <svg class="animate-spin h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Đang xóa...
              </span>
              <span v-else>Xóa đánh giá</span>
            </button>
          </div>
        </form>
      </div>
    </main>
  </div>
</template>

<script setup>
  import { computed, onMounted, ref } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { useProductStore } from '@/stores/products'
  import { useReviewsStore } from '@/stores/reviews'
  import { getProductImageUrl } from '@/utils/productImages'

  const route = useRoute()
  const router = useRouter()
  const productStore = useProductStore()
  const reviewsStore = useReviewsStore()

  const productId = computed(() => Number(route.params.id))
  const currentUser = ref(null)
  const submitError = ref('')
  const successMessage = ref('')
  const isSubmitting = ref(false)
  const isDeleting = ref(false)
  const existingReview = ref(null)

  const product = computed(() => {
    if (!Number.isFinite(productId.value)) return null
    return productStore.getProductById(productId.value) || null
  })

  const productName = computed(() => product.value?.name || '')
  const productImage = computed(() => {
    if (!product.value) return 'https://via.placeholder.com/120x120?text=Gi%C3%A0y'
    return getProductImageUrl({ product: product.value, variants: productStore.variants, width: 120, height: 120 })
  })

  const reviewerName = computed(() => currentUser.value?.username || currentUser.value?.gmail || '')
  const reviewerAccountId = computed(() => Number(currentUser.value?.id))

  const rating = ref(0)
  const comment = ref('')

  onMounted(async () => {
    if (productStore.products.length === 0) {
      await productStore.fetchProducts()
    }
    if (productStore.variants.length === 0) {
      await productStore.fetchVariants()
    }

    const rawUser = localStorage.getItem('user')
    if (rawUser) {
      try {
        currentUser.value = JSON.parse(rawUser)
      } catch {
        currentUser.value = null
      }
    }

    if (!Number.isFinite(reviewerAccountId.value)) {
      router.push('/login')
      return
    }

    // Check if user already has a review for this product
    await reviewsStore.fetchReviews()
    const existing = reviewsStore.getExistingReview(productId.value, reviewerAccountId.value)
    if (existing) {
      existingReview.value = existing
      rating.value = existing.rating || 0
      comment.value = existing.comment || ''
    }
  })

  async function handleSubmit () {
    submitError.value = ''
    successMessage.value = ''
    
    if (!Number.isFinite(productId.value)) return
    if (!Number.isFinite(reviewerAccountId.value)) {
      submitError.value = 'Bạn cần đăng nhập để gửi đánh giá.'
      return
    }

    isSubmitting.value = true

    try {
      let result

      if (existingReview.value) {
        // Update existing review
        result = await reviewsStore.updateReview(existingReview.value.id, {
          rating: rating.value,
          comment: comment.value,
        })
      } else {
        // Create new review
        result = await reviewsStore.addReview({
          productId: productId.value,
          accountId: reviewerAccountId.value,
          rating: rating.value,
          comment: comment.value,
        })
      }

      if (result.success) {
        successMessage.value = existingReview.value ? 'Đã cập nhật đánh giá thành công!' : 'Đã gửi đánh giá thành công!'
        setTimeout(() => {
          router.push(`/product/${productId.value}`)
        }, 1500)
      } else if (result.error === 'DUPLICATE_REVIEW') {
        // This shouldn't happen now, but handle it just in case
        existingReview.value = result.existingReview
        rating.value = result.existingReview.rating || 0
        comment.value = result.existingReview.comment || ''
        submitError.value = 'Bạn đã có đánh giá cho sản phẩm này rồi. Bạn có thể sửa hoặc xóa đánh giá ở trên.'
      } else {
        submitError.value = result.message || 'Gửi đánh giá thất bại. Bạn thử lại giúp mình nhé.'
      }
    } catch (error) {
      console.error('Error submitting review:', error)
      submitError.value = 'Có lỗi xảy ra ngoài dự kiến. Bạn thử lại nhé.'
    } finally {
      isSubmitting.value = false
    }
  }

  async function handleDelete () {
    if (!existingReview.value) return

    const confirmed = confirm('Bạn chắc chắn muốn xóa đánh giá này chứ? Thao tác này không thể hoàn tác.')
    if (!confirmed) return

    isDeleting.value = true
    submitError.value = ''
    successMessage.value = ''

    try {
      const result = await reviewsStore.deleteReview(existingReview.value.id)
      
      if (result.success) {
        successMessage.value = 'Đã xóa đánh giá thành công!'
        setTimeout(() => {
          router.push(`/product/${productId.value}`)
        }, 1500)
      } else {
        submitError.value = 'Xóa đánh giá thất bại. Bạn thử lại nhé.'
      }
    } catch (error) {
      console.error('Error deleting review:', error)
      submitError.value = 'Có lỗi xảy ra ngoài dự kiến. Bạn thử lại nhé.'
    } finally {
      isDeleting.value = false
    }
  }
</script>


