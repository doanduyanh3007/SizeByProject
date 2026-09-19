<template>
  <div class="retail-shell text-slate-900 dark:text-white overflow-x-hidden antialiased min-h-screen flex flex-col">
    <MainTopBar />

    <main class="mx-auto w-full max-w-[980px] flex-1 px-4 py-8 sm:px-8">
      <div class="rounded-3xl border border-slate-200 bg-white shadow-sm dark:border-[#3a2f29] dark:bg-[#1f1916] overflow-hidden">
        <div class="bg-[linear-gradient(120deg,#2a1a12_0%,#b85c2c_58%,#e7a781_100%)] px-6 py-7 sm:px-8">
          <p class="text-[11px] uppercase tracking-[0.2em] text-[#ffe2cf] font-semibold">Bảo mật tài khoản</p>
          <h1 class="mt-2 text-3xl font-black leading-tight text-white">Thay đổi mật khẩu</h1>
          <p class="mt-2 text-sm leading-7 text-[#fce6d6]">
            Xác thực email trước khi đặt mật khẩu mới để bảo vệ tài khoản của bạn.
          </p>
        </div>

        <div class="p-6 sm:p-8">
          <div class="grid grid-cols-3 gap-2">
            <div
              v-for="(item, idx) in steps"
              :key="item.key"
              class="rounded-xl border px-2 py-2 text-center text-xs font-semibold"
              :class="idx + 1 <= step ? 'border-primary bg-[#fff3ea] text-primary dark:bg-[#35241b]' : 'border-slate-300 text-slate-500 dark:border-[#4a3a31] dark:text-[#bdaea2]'"
            >
              {{ item.label }}
            </div>
          </div>

          <div v-if="errorMessage" class="mt-4 rounded-lg border border-red-300 bg-red-50 px-4 py-3 text-sm text-red-700 dark:border-red-800 dark:bg-red-900/20 dark:text-red-300">
            {{ errorMessage }}
          </div>

          <div v-if="successMessage" class="mt-4 rounded-lg border border-green-300 bg-green-50 px-4 py-3 text-sm text-green-700 dark:border-green-800 dark:bg-green-900/20 dark:text-green-300">
            {{ successMessage }}
          </div>

          <form v-if="step === 1" class="mt-5 space-y-4" @submit.prevent="handleRequestCode">
            <label class="block">
              <span class="mb-1 block text-sm font-semibold">Email tài khoản</span>
              <input
                v-model.trim="email"
                type="email"
                required
                class="w-full rounded-xl border border-slate-900 bg-white px-4 py-3 text-slate-900 focus:border-primary focus:outline-none dark:border-[#4a3a31] dark:bg-[#201915] dark:text-white"
                placeholder="you@gmail.com"
              >
            </label>

            <button
              type="submit"
              :disabled="isLoading"
              class="w-full rounded-xl bg-primary px-4 py-3 text-sm font-bold text-white transition-colors hover:bg-primary-hover disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ isLoading ? 'Đang gửi mã...' : 'Gửi mã xác minh' }}
            </button>
          </form>

          <form v-else-if="step === 2" class="mt-5 space-y-4" @submit.prevent="handleVerifyCode">
            <label class="block">
              <span class="mb-1 block text-sm font-semibold">Mã xác minh</span>
              <input
                v-model.trim="code"
                type="text"
                maxlength="6"
                required
                class="w-full rounded-xl border border-slate-900 bg-white px-4 py-3 text-slate-900 focus:border-primary focus:outline-none dark:border-[#4a3a31] dark:bg-[#201915] dark:text-white"
                placeholder="Nhập mã 6 số"
              >
            </label>

            <div class="grid grid-cols-2 gap-3">
              <button
                type="button"
                class="rounded-xl border border-slate-300 px-4 py-3 text-sm font-semibold text-slate-700 transition-colors hover:bg-slate-50 dark:border-[#4a3a31] dark:text-white dark:hover:bg-[#2b221d]"
                :disabled="isLoading"
                @click="step = 1"
              >
                Quay lại
              </button>
              <button
                type="submit"
                class="rounded-xl bg-primary px-4 py-3 text-sm font-bold text-white transition-colors hover:bg-primary-hover disabled:opacity-50 disabled:cursor-not-allowed"
                :disabled="isLoading"
              >
                {{ isLoading ? 'Đang xác minh...' : 'Xác minh mã' }}
              </button>
            </div>
          </form>

          <form v-else class="mt-5 space-y-4" @submit.prevent="handleResetPassword">
            <label class="block">
              <span class="mb-1 block text-sm font-semibold">Mật khẩu mới</span>
              <input
                v-model="newPassword"
                :type="showPassword ? 'text' : 'password'"
                minlength="6"
                required
                class="w-full rounded-xl border border-slate-900 bg-white px-4 py-3 text-slate-900 focus:border-primary focus:outline-none dark:border-[#4a3a31] dark:bg-[#201915] dark:text-white"
                placeholder="Tối thiểu 6 ký tự"
              >
            </label>

            <label class="block">
              <span class="mb-1 block text-sm font-semibold">Xác nhận mật khẩu mới</span>
              <input
                v-model="confirmPassword"
                :type="showConfirmPassword ? 'text' : 'password'"
                minlength="6"
                required
                class="w-full rounded-xl border border-slate-900 bg-white px-4 py-3 text-slate-900 focus:border-primary focus:outline-none dark:border-[#4a3a31] dark:bg-[#201915] dark:text-white"
                placeholder="Nhập lại mật khẩu"
              >
            </label>

            <div class="grid grid-cols-2 gap-3">
              <button
                type="button"
                class="rounded-xl border border-slate-300 px-4 py-3 text-sm font-semibold text-slate-700 transition-colors hover:bg-slate-50 dark:border-[#4a3a31] dark:text-white dark:hover:bg-[#2b221d]"
                :disabled="isLoading"
                @click="step = 2"
              >
                Quay lại
              </button>
              <button
                type="submit"
                class="rounded-xl bg-primary px-4 py-3 text-sm font-bold text-white transition-colors hover:bg-primary-hover disabled:opacity-50 disabled:cursor-not-allowed"
                :disabled="isLoading"
              >
                {{ isLoading ? 'Đang cập nhật...' : 'Cập nhật mật khẩu' }}
              </button>
            </div>
          </form>

          <div class="mt-6 text-center">
            <router-link to="/profile/edit" class="text-sm font-semibold text-primary hover:text-[#a84f24]">
              Quay lại quản lý tài khoản
            </router-link>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '../services/api'

const router = useRouter()

const step = ref(1)
const isLoading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

const email = ref('')
const code = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

const showPassword = ref(false)
const showConfirmPassword = ref(false)

const steps = [
  { key: 'request', label: '1. Gửi mã' },
  { key: 'verify', label: '2. Xác thực' },
  { key: 'reset', label: '3. Cập nhật' },
]

onMounted(() => {
  try {
    const rawUser = localStorage.getItem('user')
    if (rawUser) {
      const parsed = JSON.parse(rawUser)
      if (parsed?.gmail) {
        email.value = parsed.gmail
      }
    }
  } catch {
    // Ignore local storage parse errors.
  }
})

function resetMessages () {
  errorMessage.value = ''
  successMessage.value = ''
}

function validateEmail () {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!email.value || !emailRegex.test(email.value)) {
    errorMessage.value = 'Vui lòng nhập đúng định dạng email.'
    return false
  }
  return true
}

async function handleRequestCode () {
  resetMessages()
  if (!validateEmail()) return

  isLoading.value = true
  try {
    await authApi.requestPasswordResetCode({ gmail: email.value })
    successMessage.value = 'Mã xác minh đã được gửi. Vui lòng kiểm tra email của bạn.'
    step.value = 2
  } catch (error) {
    errorMessage.value = error?.message || 'Không thể gửi mã xác minh. Vui lòng thử lại.'
  } finally {
    isLoading.value = false
  }
}

async function handleVerifyCode () {
  resetMessages()
  if (!validateEmail()) return
  if (!code.value || code.value.length < 4) {
    errorMessage.value = 'Vui lòng nhập mã xác minh hợp lệ.'
    return
  }

  isLoading.value = true
  try {
    await authApi.verifyPasswordResetCode({ gmail: email.value, code: code.value })
    successMessage.value = 'Xác thực thành công. Hãy đặt mật khẩu mới.'
    step.value = 3
  } catch (error) {
    errorMessage.value = error?.message || 'Mã xác minh không hợp lệ hoặc đã hết hạn.'
  } finally {
    isLoading.value = false
  }
}

async function handleResetPassword () {
  resetMessages()
  if (!validateEmail()) return

  if (!newPassword.value || newPassword.value.length < 6) {
    errorMessage.value = 'Mật khẩu mới phải có ít nhất 6 ký tự.'
    return
  }

  if (newPassword.value !== confirmPassword.value) {
    errorMessage.value = 'Mật khẩu xác nhận không khớp.'
    return
  }

  isLoading.value = true
  try {
    await authApi.resetPassword({
      gmail: email.value,
      code: code.value,
      newPassword: newPassword.value,
    })

    successMessage.value = 'Cập nhật mật khẩu thành công. Đang chuyển về trang đăng nhập...'
    setTimeout(() => {
      router.push('/login')
    }, 1200)
  } catch (error) {
    errorMessage.value = error?.message || 'Không thể cập nhật mật khẩu. Vui lòng thử lại.'
  } finally {
    isLoading.value = false
  }
}
</script>
