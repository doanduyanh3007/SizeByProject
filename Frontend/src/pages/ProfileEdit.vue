<!-- ProfileEdit.vue -->
<template>
  <div class="bg-background-light dark:bg-background-dark text-slate-900 dark:text-white font-display overflow-x-hidden transition-colors duration-200 min-h-screen flex flex-col">

    <MainTopBar />

    <!-- Loading state -->
    <div v-if="isLoading" class="flex-1 flex items-center justify-center">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary" />
    </div>

    <!-- Main content with sidebar -->
    <div v-else class="flex flex-1 justify-center py-8 px-4 sm:px-8">
      <div class="flex flex-col md:flex-row max-w-[1200px] flex-1 w-full gap-8">
        
        <!-- Left Sidebar Menu -->
        <div class="md:w-64 shrink-0">
          <div class="sticky top-8">
            <!-- User Info Summary -->
            <div class="bg-white dark:bg-[#1a222b] rounded-xl border border-slate-200 dark:border-slate-800 p-6 mb-4">
              <div class="flex items-center gap-4">
                <div class="size-16 rounded-full bg-slate-100 dark:bg-[#283039] flex items-center justify-center overflow-hidden">
                  <img 
                    v-if="resolvedSidebarAvatar" 
                    :src="resolvedSidebarAvatar" 
                    :alt="form.username"
                    class="w-full h-full object-cover"
                  />
                  <span v-else class="material-symbols-outlined text-3xl text-slate-500 dark:text-[#c8d0d8]">account_circle</span>
                </div>
                <div>
                  <h3 class="font-bold text-slate-900 dark:text-white">{{ form.username || 'Người dùng' }}</h3>
                  <p class="text-xs text-slate-500 dark:text-[#9cabba]">{{ form.gmail }}</p>
                </div>
              </div>
            </div>

            <!-- Navigation Menu -->
            <div class="bg-white dark:bg-[#1a222b] rounded-xl border border-slate-200 dark:border-slate-800 overflow-hidden">
              <router-link 
                to="/profile" 
                class="flex items-center gap-3 px-6 py-4 text-slate-600 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-[#283039] transition-colors border-b border-slate-100 dark:border-slate-800"
                exact-active-class="bg-primary/10 text-primary border-l-4 border-primary"
              >
                <span class="material-symbols-outlined">person</span>
                <span class="font-medium">Hồ sơ</span>
              </router-link>
              
              <router-link 
                to="/profile/edit" 
                class="flex items-center gap-3 px-6 py-4 text-slate-600 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-[#283039] transition-colors border-b border-slate-100 dark:border-slate-800"
                exact-active-class="text-primary bg-primary/10 border-l-4 border-primary font-medium"
              >
                <span class="material-symbols-outlined">edit</span>
                <span>Sửa hồ sơ</span>
              </router-link>
              
              <router-link 
                to="/orders" 
                class="flex items-center gap-3 px-6 py-4 text-slate-600 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-[#283039] transition-colors border-b border-slate-100 dark:border-slate-800"
                active-class="bg-primary/10 text-primary border-l-4 border-primary"
              >
                <span class="material-symbols-outlined">shopping_bag</span>
                <span class="font-medium">Lịch sử đơn hàng</span>
              </router-link>
              
              <router-link 
                to="/profile/edit/reviews" 
                class="flex items-center gap-3 px-6 py-4 text-slate-600 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-[#283039] transition-colors border-b border-slate-100 dark:border-slate-800"
                exact-active-class="bg-primary/10 text-primary border-l-4 border-primary"
              >
                <span class="material-symbols-outlined">rate_review</span>
                <span class="font-medium">Đánh giá của tôi</span>
              </router-link>
              
              <router-link 
                to="/vouchers" 
                class="flex items-center gap-3 px-6 py-4 text-slate-600 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-[#283039] transition-colors border-b border-slate-100 dark:border-slate-800"
                active-class="bg-primary/10 text-primary border-l-4 border-primary"
              >
                <span class="material-symbols-outlined">confirmation_number</span>
                <span class="font-medium">Ưu đãi của tôi</span>
              </router-link>
              
              <button 
                @click="handleLogout"
                class="w-full flex items-center gap-3 px-6 py-4 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors"
              >
                <span class="material-symbols-outlined">logout</span>
                <span class="font-medium">Đăng xuất</span>
              </button>
            </div>
          </div>
        </div>

        <!-- Right Content - Edit Form -->
        <div class="flex-1">
          <div class="flex flex-col gap-6">
            <!-- Breadcrumb -->
            <div class="flex items-center gap-2 text-sm">
              <router-link to="/profile" class="text-slate-500 dark:text-[#9cabba] hover:text-primary">Hồ sơ</router-link>
              <span class="material-symbols-outlined text-sm text-slate-400">chevron_right</span>
              <span class="text-slate-900 dark:text-white font-medium">{{ isReviewsTab ? 'Đánh giá của tôi' : 'Chỉnh sửa hồ sơ' }}</span>
            </div>

            <template v-if="!isReviewsTab">
            <!-- Edit Form -->
            <div class="rounded-xl bg-white dark:bg-[#1a222b] shadow-sm border border-slate-200 dark:border-slate-800 overflow-hidden p-6 md:p-8">
              <form @submit.prevent="handleSubmit" class="flex flex-col gap-6">
                <!-- Avatar section with file upload -->
                <div class="flex items-center gap-6 pb-6 border-b border-slate-100 dark:border-slate-800">
                  <div class="relative group">
                    <div class="size-24 shrink-0 rounded-full shadow-inner flex items-center justify-center bg-slate-100 text-slate-500 dark:bg-[#283039] dark:text-[#c8d0d8] overflow-hidden">
                      <img 
                        v-if="avatarPreview" 
                        :src="avatarPreview" 
                        class="w-full h-full object-cover"
                        alt="Avatar preview"
                      >
                      <span v-else class="material-symbols-outlined text-5xl">account_circle</span>
                    </div>
                    <!-- Upload overlay -->
                    <label 
                      for="avatar-upload"
                      class="absolute inset-0 flex items-center justify-center bg-black/50 rounded-full opacity-0 group-hover:opacity-100 cursor-pointer transition-opacity"
                    >
                      <span class="material-symbols-outlined text-white text-2xl">photo_camera</span>
                    </label>
                    <input 
                      id="avatar-upload"
                      type="file"
                      accept="image/*"
                      class="hidden"
                      @change="handleAvatarChange"
                      ref="fileInput"
                    >
                  </div>
                  <div class="flex flex-col gap-2">
                    <p class="text-sm text-slate-500 dark:text-[#9cabba]">Ảnh đại diện</p>
                    <div class="flex gap-2">
                      <button 
                        type="button" 
                        @click="triggerFileUpload"
                        class="text-sm text-primary hover:text-blue-400 font-medium text-left"
                      >
                        Đổi ảnh
                      </button>
                      <button 
                        v-if="avatarPreview"
                        type="button"
                        @click="removeAvatar"
                        class="text-sm text-red-500 hover:text-red-600 font-medium"
                      >
                        Xóa
                      </button>
                    </div>
                    <p v-if="uploadError" class="text-xs text-red-500">{{ uploadError }}</p>
                  </div>
                </div>

                <!-- Hiển thị lỗi -->
                <div v-if="error" class="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-4">
                  <p class="text-red-600 dark:text-red-400 text-sm">{{ error }}</p>
                </div>

                <!-- Form fields -->
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div class="flex flex-col gap-2">
                    <label class="text-sm font-medium text-slate-700 dark:text-slate-300">Tên đăng nhập</label>
                    <input
                      v-model="form.username"
                      type="text"
                      class="w-full px-4 py-2.5 rounded-lg border border-slate-900 dark:border-slate-700 bg-slate-50 dark:bg-[#283039] text-slate-900 dark:text-white focus:ring-2 focus:ring-primary/20 focus:border-primary transition-colors"
                      :class="{ 'border-red-500': errors.username }"
                      required
                    >
                    <p v-if="errors.username" class="text-red-500 text-xs">{{ errors.username }}</p>
                  </div>

                  <div class="flex flex-col gap-2">
                    <label class="text-sm font-medium text-slate-700 dark:text-slate-300">Email liên hệ</label>
                    <input
                      v-model="form.gmail"
                      type="email"
                      class="w-full px-4 py-2.5 rounded-lg border border-slate-900 dark:border-slate-700 bg-slate-50 dark:bg-[#283039] text-slate-900 dark:text-white focus:ring-2 focus:ring-primary/20 focus:border-primary transition-colors"
                      :class="{ 'border-red-500': errors.gmail }"
                      required
                    >
                    <p v-if="errors.gmail" class="text-red-500 text-xs">{{ errors.gmail }}</p>
                  </div>

                  <div class="flex flex-col gap-2">
                    <label class="text-sm font-medium text-slate-700 dark:text-slate-300">Số điện thoại</label>
                    <input
                      v-model="form.phone"
                      type="tel"
                      class="w-full px-4 py-2.5 rounded-lg border border-slate-900 dark:border-slate-700 bg-slate-50 dark:bg-[#283039] text-slate-900 dark:text-white focus:ring-2 focus:ring-primary/20 focus:border-primary transition-colors"
                    >
                  </div>

                  <div class="flex flex-col gap-2">
                    <label class="text-sm font-medium text-slate-700 dark:text-slate-300">Hạng thành viên</label>
                    <input
                      v-model="form.membership"
                      type="text"
                      class="w-full px-4 py-2.5 rounded-lg border border-slate-900 dark:border-slate-700 bg-slate-50 dark:bg-[#1f2937] text-slate-500 dark:text-slate-400 cursor-not-allowed"
                      disabled
                      readonly
                    >
                  </div>
                </div>

                <!-- Address book -->
                <div class="pt-2 border-t border-slate-100 dark:border-slate-800">
                  <AddressManager
                    v-if="form.id"
                    :account-id="form.id"
                    mode="manage"
                    :default-fullname="form.username"
                    :default-phone="form.phone"
                  />
                </div>

                <!-- Buttons -->
                <div class="flex flex-col sm:flex-row gap-3 pt-4">
                  <button
                    type="submit"
                    :disabled="isSubmitting"
                    class="flex-1 flex items-center justify-center gap-2 px-6 py-3 rounded-lg bg-primary hover:bg-primary-hover text-white text-sm font-bold transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    <span v-if="isSubmitting" class="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent" />
                    <span>{{ isSubmitting ? 'Đang lưu...' : 'Lưu thay đổi' }}</span>
                  </button>
                  <button
                    type="button"
                    @click="goBack"
                    class="flex-1 px-6 py-3 rounded-lg border border-slate-200 dark:border-slate-700 hover:bg-slate-100 dark:hover:bg-[#283039] text-slate-900 dark:text-white text-sm font-bold transition-colors"
                  >
                    Hủy
                  </button>
                </div>
              </form>
            </div>

            <!-- Quick Order History Preview -->
            <div class="rounded-xl bg-white dark:bg-[#1a222b] shadow-sm border border-slate-200 dark:border-slate-800 overflow-hidden p-6">
              <div class="flex items-center justify-between mb-4">
                <h3 class="text-lg font-bold text-slate-900 dark:text-white">Đơn hàng gần đây</h3>
                <router-link to="/orders" class="text-sm text-primary hover:text-blue-400 flex items-center gap-1">
                  Xem tất cả
                  <span class="material-symbols-outlined text-sm">arrow_forward</span>
                </router-link>
              </div>

              <!-- Loading orders -->
              <div v-if="isLoadingOrders" class="flex justify-center py-4">
                <div class="animate-spin rounded-full h-6 w-6 border-b-2 border-primary" />
              </div>

              <!-- Orders list -->
              <div v-else-if="recentOrders.length > 0" class="space-y-3">
                <div v-for="(order, index) in recentOrders" :key="order.id || index" class="flex items-center justify-between p-3 bg-slate-50 dark:bg-[#202934] rounded-lg">
                  <div>
                    <p class="text-sm font-medium text-slate-900 dark:text-white">{{ index + 1 }}</p>
                    <p class="text-xs text-slate-500 dark:text-[#9cabba]">{{ formatDate(getOrderDate(order)) }}</p>
                  </div>
                  <div class="text-right">
                    <span class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium" :class="getStatusClass(order.status)">
                      {{ formatOrderStatus(order.status) }}
                    </span>
                    <p class="text-sm font-medium text-slate-900 dark:text-white mt-1">{{ formatPrice(getOrderTotal(order)) }}</p>
                  </div>
                </div>
              </div>

              <!-- No orders -->
              <div v-else class="text-center py-4">
                <p class="text-slate-500 dark:text-[#9cabba] text-sm">Bạn chưa có đơn hàng nào</p>
                <router-link to="/shop" class="inline-block mt-2 text-primary text-sm hover:text-blue-400">
                  Bắt đầu mua sắm
                </router-link>
              </div>
            </div>

            </template>

            <template v-else>
              <div class="rounded-xl bg-white dark:bg-[#1a222b] shadow-sm border border-slate-200 dark:border-slate-800 overflow-hidden p-6 md:p-8">
                <div class="flex flex-wrap items-center justify-between gap-3 mb-6">
                  <h2 class="text-xl font-bold text-slate-900 dark:text-white">Đánh giá của tôi</h2>
                  <span class="text-xs px-2 py-1 rounded-full bg-slate-100 dark:bg-[#283039] text-slate-600 dark:text-[#9cabba]">
                    {{ myReviews.length }} đánh giá
                  </span>
                </div>

                <div v-if="isLoadingReviews" class="flex justify-center py-8">
                  <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary" />
                </div>

                <div v-else-if="myReviews.length === 0" class="text-center py-8">
                  <p class="text-slate-500 dark:text-[#9cabba] text-sm">Bạn chưa viết đánh giá nào.</p>
                  <router-link to="/shop" class="inline-block mt-3 text-primary text-sm hover:text-blue-400 font-medium">
                    Xem sản phẩm để đánh giá
                  </router-link>
                </div>

                <div v-else class="space-y-4">
                  <article
                    v-for="review in myReviews"
                    :key="review.id"
                    class="rounded-lg border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-[#202934] p-4"
                  >
                    <div class="flex flex-wrap items-start justify-between gap-3">
                      <div>
                        <p class="text-sm font-semibold text-slate-900 dark:text-white">Sản phẩm #{{ review.productId }}</p>
                        <p class="text-xs text-slate-500 dark:text-[#9cabba]">{{ formatDate(review.createdAt) }}</p>
                      </div>
                      <router-link
                        :to="`/product/${review.productId}/review`"
                        class="text-xs px-3 py-1.5 rounded-md bg-primary/10 text-primary hover:bg-primary/20 transition-colors font-medium"
                      >
                        Sửa đánh giá
                      </router-link>
                    </div>

                    <div class="mt-3 flex items-center gap-1" aria-label="Rating">
                      <span
                        v-for="n in 5"
                        :key="`${review.id}-${n}`"
                        class="text-lg leading-none"
                        :class="n <= Number(review.rating || 0) ? 'text-yellow-400' : 'text-slate-300 dark:text-slate-600'"
                      >
                        ★
                      </span>
                    </div>

                    <p class="mt-3 text-sm text-slate-700 dark:text-slate-300 whitespace-pre-wrap">
                      {{ review.comment || 'Bạn chưa để lại nội dung.' }}
                    </p>
                  </article>
                </div>
              </div>
            </template>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch, unref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { accountsApi, ordersApi, resolveBackendAssetUrl } from '../services/api'
import { useReviewsStore } from '@/stores/reviews'
import AddressManager from '@/components/AddressManager.vue'
import { getSession, saveSession, clearSession } from '@/utils/auth'

const route = useRoute()
const router = useRouter()
const reviewsStore = useReviewsStore()
const isLoading = ref(true)
const isLoadingOrders = ref(false)
const isLoadingReviews = ref(false)
const isSubmitting = ref(false)
const error = ref('')
const uploadError = ref('')
const fileInput = ref(null)
const avatarPreview = ref(null)
const recentOrders = ref([])
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

function normalizeAvatarUrl(rawUrl) {
  if (!rawUrl || typeof rawUrl !== 'string') {
    return ''
  }

  const fixedUrl = rawUrl.replace(
    /^https?:\/\/[^/]+\/?(https?:\/\/.+)$/i,
    '$1',
  )

  return resolveBackendAssetUrl(fixedUrl) || ''
}

const form = ref({
  id: null,
  username: '',
  gmail: '',
  phone: '',
  address: '',
  membership: 'Thành viên thường', // Backend không có field này, tự thêm vào UI
  confirmPassword: '',
  avatar: null,
  imgUrl: null
})

const resolvedSidebarAvatar = computed(() =>
  normalizeAvatarUrl(form.value.imgUrl || avatarPreview.value),
)

const errors = ref({
  username: '',
  gmail: '',
  confirmPassword: ''
})

const isReviewsTab = computed(() => route.name === 'profile-edit-reviews')

const myReviews = computed(() => {
  const accountId = Number(form.value.id)
  if (!Number.isFinite(accountId)) {
    return []
  }

  return [...reviewsStore.getByAccountId(accountId)].sort((a, b) => {
    const dateA = new Date(a.createdAt || 0).getTime()
    const dateB = new Date(b.createdAt || 0).getTime()
    return dateB - dateA
  })
})

// Load user data from localStorage and backend
onMounted(async () => {
  await Promise.all([
    loadUserData(),
    fetchRecentOrders(),
    fetchMyReviews()
  ])
})

async function loadUserData() {
  try {
    const userData = getSession('user')
    if (userData) {
      const localUser = JSON.parse(userData)
      
      // Refresh từ backend để lấy dữ liệu mới nhất
      if (localUser.id) {
        try {
          // Gọi API getById để lấy thông tin mới nhất từ backend
          const latestUser = await accountsApi.getById(localUser.id)
          console.log('Latest user from backend:', latestUser)
          
          // Cập nhật form với dữ liệu từ backend
          form.value = {
            id: latestUser.id,
            username: latestUser.username || '',
            gmail: latestUser.gmail || '',
            phone: latestUser.phone || '',
            address: latestUser.address || '',
            membership: form.value.membership, // Giữ membership từ UI
            confirmPassword: '',
            avatar: null,
            imgUrl: normalizeAvatarUrl(latestUser.imgUrl || null)
          }
          
          // Load avatar from backend imgUrl (most important)
          if (latestUser.imgUrl) {
            const imageUrl = normalizeAvatarUrl(latestUser.imgUrl)
            avatarPreview.value = imageUrl
            console.log('Loaded avatar from backend:', imageUrl)
          }
          
          // Cập nhật localStorage với dữ liệu mới từ backend
          const updatedUserData = {
            ...latestUser,
            membership: form.value.membership,
            avatar: avatarPreview.value
          }
          saveSession(updatedUserData)
          
        } catch (refreshError) {
          console.error('Failed to refresh user data:', refreshError)
          // Fallback to local data
          form.value = {
            id: localUser.id,
            username: localUser.username || '',
            gmail: localUser.gmail || '',
            phone: localUser.phone || '',
            address: localUser.address || '',
            membership: localUser.membership || 'Thành viên thường',
            confirmPassword: '',
            avatar: null,
            imgUrl: normalizeAvatarUrl(localUser.imgUrl || null)
          }
        }
      }
    } else {
      router.push('/login')
    }
  } catch (error) {
    console.error('Error loading user data:', error)
    error.value = 'Không tải được thông tin người dùng'
  } finally {
    isLoading.value = false
  }
}

// Fetch recent orders
async function fetchRecentOrders() {
  try {
    const userData = JSON.parse(getSession('user'))
    if (!userData?.id) return

    isLoadingOrders.value = true
    const response = await ordersApi.getUserOrders(userData.id)

    const list = response && response.content ? response.content : Array.isArray(response) ? response : []
    recentOrders.value = sortOrdersByDateDesc(list).slice(0, 3)
  } catch (error) {
    console.error('Error fetching recent orders:', error)
  } finally {
    isLoadingOrders.value = false
  }
}

function sortOrdersByDateDesc(list) {
  return [...(Array.isArray(list) ? list : [])].sort((a, b) => {
    const timeA = new Date(getOrderDate(a) || 0).getTime()
    const timeB = new Date(getOrderDate(b) || 0).getTime()
    if (timeA !== timeB) {
      return timeB - timeA
    }

    const idA = Number(a && a.id)
    const idB = Number(b && b.id)
    return (Number.isFinite(idB) ? idB : 0) - (Number.isFinite(idA) ? idA : 0)
  })
}

async function fetchMyReviews() {
  isLoadingReviews.value = true
  try {
    await reviewsStore.fetchReviews({ force: true })
  } catch (err) {
    console.error('Error fetching reviews:', err)
  } finally {
    isLoadingReviews.value = false
  }
}

// Handle avatar change
function triggerFileUpload() {
  fileInput.value?.click()
}

function handleAvatarChange(event) {
  const file = event.target.files[0]
  if (!file) return

  // Validate file type
  if (!file.type.startsWith('image/')) {
    uploadError.value = 'Vui lòng chọn tệp ảnh hợp lệ'
    return
  }

  // Validate file size (max 2MB)
  if (file.size > 2 * 1024 * 1024) {
    uploadError.value = 'Ảnh phải nhỏ hơn 2MB'
    return
  }

  uploadError.value = ''

  // Create preview
  const reader = new FileReader()
  reader.onload = (e) => {
    avatarPreview.value = e.target.result
    form.value.avatar = file
  }
  reader.readAsDataURL(file)
}

function removeAvatar() {
  avatarPreview.value = null
  form.value.avatar = null
  form.value.imgUrl = null
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

// Format date
function formatDate(dateString) {
  if (!dateString) return 'Không rõ'
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('vi-VN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  }).format(date)
}

function getOrderDate(order) {
  const source = order && typeof order === 'object' ? order : {}
  return source.orderDate || source.order_date || source.createdAt || source.created_at || null
}

function getOrderTotal(order) {
  const source = order && typeof order === 'object' ? order : {}
  const raw =
    source.totalAmount !== undefined && source.totalAmount !== null ? source.totalAmount :
    source.total_amount !== undefined && source.total_amount !== null ? source.total_amount :
    source.finalAmount !== undefined && source.finalAmount !== null ? source.finalAmount :
    source.final_amount !== undefined && source.final_amount !== null ? source.final_amount :
    source.totalMoney !== undefined && source.totalMoney !== null ? source.totalMoney :
    source.total_money

  const n = Number(raw)
  return Number.isFinite(n) ? n : 0
}

// Format price
function formatPrice(price) {
  if (!price) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(Number(price))
}

// Get status class
function getStatusClass(status) {
  switch (status?.toLowerCase()) {
    case 'shipping':
    case 'shipped':
      return 'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-300'
    case 'delivered':
      return 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-300'
    case 'returned':
      return 'bg-slate-100 text-slate-800 dark:bg-slate-700/50 dark:text-slate-300'
    case 'cancelled':
      return 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-300'
    default:
      return 'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-300'
  }
}

// Validate form
const validateForm = () => {
  let isValid = true
  errors.value = { username: '', gmail: '' }

  if (!form.value.username?.trim()) {
    errors.value.username = 'Bạn cần nhập tên đăng nhập'
    isValid = false
  }

  if (!form.value.gmail?.trim()) {
    errors.value.gmail = 'Bạn cần nhập email'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.value.gmail)) {
    errors.value.gmail = 'Email không đúng định dạng'
    isValid = false
  }

  return isValid
}

// Upload avatar to backend
async function uploadAvatar(accountId) {
  if (!form.value.avatar) {
    return form.value.imgUrl // Return existing URL if no new file
  }

  try {
    const formData = new FormData()
    formData.append('file', form.value.avatar)

    const response = await axios.post(
      `${API_BASE_URL}/accounts/${accountId}/upload-avatar`,
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }
    )

    if (response.data && response.data.imageUrl) {
      return normalizeAvatarUrl(response.data.imageUrl)
    } else {
      throw new Error('Máy chủ không trả về đường dẫn ảnh')
    }
  } catch (err) {
    console.error('Error uploading avatar:', err)
    throw new Error('Tải ảnh lên thất bại: ' + (err.response?.data?.error || err.message))
  }
}

// Handle form submit
const handleSubmit = async () => {
  if (!validateForm()) return

  isSubmitting.value = true
  error.value = ''

  try {
    const userData = JSON.parse(getSession('user'))
    if (!userData?.id) {
      throw new Error('Không tìm thấy thông tin tài khoản')
    }

    // Upload avatar first if a new file was selected
    let imageUrl = form.value.imgUrl
    if (form.value.avatar) {
      imageUrl = await uploadAvatar(userData.id)
    }

    // Prepare update data theo đúng format backend yêu cầu
    const updateData = {
      username: form.value.username,
      gmail: form.value.gmail,
      phone: form.value.phone || '',
      imgUrl: imageUrl
    }

    console.log('Updating user with data:', updateData)

    // Call API to update user
    const updatedUser = await accountsApi.update(userData.id, updateData)
    console.log('Updated user response:', updatedUser)
    
    // Update localStorage with new data from backend
    const newUserData = {
      ...updatedUser,
      membership: form.value.membership // Keep membership from UI
    }
    saveSession(newUserData)

    // Refresh current page state and keep user on profile edit view.
    alert('Hồ sơ đã cập nhật thành công!')
    window.location.reload()

  } catch (err) {
    console.error('Error updating profile:', err)
    
    // Xử lý lỗi từ backend
    if (err.message?.includes('Email already exists')) {
      error.value = 'Email này đã tồn tại. Bạn thử email khác nhé.'
    } else if (err.message?.includes('Failed to upload')) {
      error.value = err.message
    } else if (err.status === 400) {
      error.value = 'Dữ liệu không hợp lệ. Bạn kiểm tra lại giúp mình nhé.'
    } else {
      error.value = err.message || 'Cập nhật hồ sơ thất bại. Bạn thử lại nhé.'
    }
  } finally {
    isSubmitting.value = false
  }
}

// Go back to user profile
const goBack = () => {
  router.push('/profile')
}

function formatOrderStatus(status) {
  switch (status?.toLowerCase()) {
    case 'pending':
      return 'Chờ xác nhận'
    case 'shipping':
    case 'shipped':
      return 'Đang giao'
    case 'delivered':
      return 'Đã giao'
    case 'returned':
      return 'Đã hoàn trả'
    case 'cancelled':
      return 'Đã hủy'
    default:
      return status || 'Đang xử lý'
  }
}

// Handle logout
const handleLogout = () => {
  clearSession()
  router.push('/login')
}
</script>
