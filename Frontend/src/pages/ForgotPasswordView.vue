<template>
  <div class="retail-shell text-slate-900 dark:text-white overflow-x-hidden antialiased flex flex-col min-h-screen">
    <MainTopBar />

    <main class="mx-auto flex w-full max-w-[1240px] flex-1 flex-col gap-6 px-4 py-8 md:px-8 lg:flex-row lg:items-stretch lg:gap-8 xl:px-10">
      <section class="hidden lg:flex lg:w-[52%] retail-card overflow-hidden p-0">
        <div class="relative flex w-full items-end">
          <img
            class="absolute inset-0 h-full w-full object-cover"
            alt="Customer checking reset password email on mobile"
            src="https://images.unsplash.com/photo-1520607162513-77705c0f0d4a?auto=format&fit=crop&w=1400&q=80"
          >
          <div class="absolute inset-0 bg-gradient-to-t from-[#14100d]/90 via-[#14100d]/45 to-transparent" />

          <div class="relative z-10 flex w-full flex-col gap-6 p-10 text-white">
            <span class="retail-kicker w-fit border-white/30 bg-white/15 text-white">Khôi phục tài khoản</span>
            <div>
              <h1 class="text-[42px] font-black leading-[1.02] tracking-tight">Đặt lại mật khẩu bằng mã xác minh gửi qua email.</h1>
              <p class="mt-4 max-w-xl text-sm leading-7 text-[#f0dfd2]">Nhập email, nhận mã, xác minh và tạo mật khẩu mới trong vài bước ngắn gọn.</p>
            </div>
          </div>
        </div>
      </section>

      <section class="w-full lg:w-[48%] flex items-center justify-center">
        <div class="retail-card w-full max-w-[500px] px-6 py-7 sm:px-8">
          <div class="text-center lg:text-left">
            <h1 class="text-slate-900 dark:text-white tracking-tight text-[32px] font-bold leading-tight pb-2">Quên mật khẩu</h1>
            <p class="text-slate-500 dark:text-[#b7a999] text-base">Làm theo các bước bên dưới để đổi mật khẩu mới.</p>
          </div>

          <div class="mt-4 grid grid-cols-3 gap-2">
            <div
              v-for="(item, idx) in steps"
              :key="item.key"
              class="rounded-xl border px-3 py-2 text-center text-xs font-semibold"
              :class="idx + 1 <= step ? 'border-primary bg-[#fff2e9] text-primary dark:bg-[#2d221a]' : 'border-slate-300 text-slate-500 dark:border-[#3b4754] dark:text-[#b7a999]'"
            >
              {{ item.label }}
            </div>
          </div>

          <div v-if="errorMessage" class="mt-4 bg-red-100 dark:bg-red-900/20 border border-red-400 dark:border-red-800 text-red-700 dark:text-red-300 px-4 py-3 rounded-lg whitespace-pre-line">
            {{ errorMessage }}
          </div>

          <div v-if="successMessage" class="mt-4 bg-green-100 dark:bg-green-900/20 border border-green-400 dark:border-green-800 text-green-700 dark:text-green-300 px-4 py-3 rounded-lg whitespace-pre-line">
            {{ successMessage }}
          </div>

          <form v-if="step === 1" class="mt-4 flex flex-col gap-4" @submit.prevent="handleRequestCode">
            <label class="flex flex-col gap-1.5">
              <span class="text-slate-900 dark:text-white text-sm font-medium leading-normal">Email tài khoản <span class="text-red-500">*</span></span>
              <div class="flex items-center rounded-xl border border-slate-300 dark:border-[#3b4754] bg-white dark:bg-[#1b2127] overflow-hidden focus-within:border-primary focus-within:ring-1 focus-within:ring-primary transition-all">
                <span class="pl-4 text-slate-400 dark:text-[#9cabba]"><span class="material-symbols-outlined text-[20px]">mail</span></span>
                <input
                  v-model.trim="email"
                  class="w-full border-none bg-transparent p-3.5 text-base text-slate-900 dark:text-white placeholder:text-slate-400 dark:placeholder:text-[#9cabba] focus:ring-0"
                  placeholder="you@gmail.com"
                  required
                  type="email"
                >
              </div>
            </label>

            <button
              class="flex w-full cursor-pointer items-center justify-center overflow-hidden rounded-xl h-12 bg-primary hover:bg-primary-hover transition-all text-white text-base font-bold leading-normal tracking-[0.015em] shadow-lg shadow-zinc-900/10 disabled:opacity-50 disabled:cursor-not-allowed"
              :disabled="isLoading"
              type="submit"
            >
              {{ isLoading ? 'Đang gửi mã...' : 'Gửi mã xác minh' }}
            </button>
          </form>

          <form v-else-if="step === 2" class="mt-4 flex flex-col gap-4" @submit.prevent="handleVerifyCode">
            <label class="flex flex-col gap-1.5">
              <span class="text-slate-900 dark:text-white text-sm font-medium leading-normal">Mã xác minh <span class="text-red-500">*</span></span>
              <div class="flex items-center rounded-xl border border-slate-300 dark:border-[#3b4754] bg-white dark:bg-[#1b2127] overflow-hidden focus-within:border-primary focus-within:ring-1 focus-within:ring-primary transition-all">
                <span class="pl-4 text-slate-400 dark:text-[#9cabba]"><span class="material-symbols-outlined text-[20px]">pin</span></span>
                <input
                  v-model.trim="code"
                  class="w-full border-none bg-transparent p-3.5 text-base text-slate-900 dark:text-white placeholder:text-slate-400 dark:placeholder:text-[#9cabba] focus:ring-0"
                  maxlength="6"
                  placeholder="Nhập mã 6 chữ số"
                  required
                  type="text"
                >
              </div>
            </label>

            <div class="flex gap-3">
              <button
                class="flex-1 h-12 rounded-xl border border-slate-300 dark:border-[#3b4754] text-slate-700 dark:text-white hover:bg-slate-50 dark:hover:bg-[#232b33] transition-all"
                :disabled="isLoading"
                type="button"
                @click="step = 1"
              >
                Quay lại
              </button>
              <button
                class="flex-1 h-12 rounded-xl bg-primary text-white font-bold hover:bg-primary-hover transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                :disabled="isLoading"
                type="submit"
              >
                {{ isLoading ? 'Đang xác minh...' : 'Xác minh mã' }}
              </button>
            </div>
          </form>

          <form v-else class="mt-4 flex flex-col gap-4" @submit.prevent="handleResetPassword">
            <label class="flex flex-col gap-1.5">
              <span class="text-slate-900 dark:text-white text-sm font-medium leading-normal">Mật khẩu mới <span class="text-red-500">*</span></span>
              <div class="flex items-center rounded-xl border border-slate-300 dark:border-[#3b4754] bg-white dark:bg-[#1b2127] overflow-hidden focus-within:border-primary focus-within:ring-1 focus-within:ring-primary transition-all">
                <span class="pl-4 text-slate-400 dark:text-[#9cabba]"><span class="material-symbols-outlined text-[20px]">lock</span></span>
                <input
                  v-model="newPassword"
                  class="w-full border-none bg-transparent p-3.5 text-base text-slate-900 dark:text-white placeholder:text-slate-400 dark:placeholder:text-[#9cabba] focus:ring-0"
                  minlength="6"
                  placeholder="Tối thiểu 6 ký tự"
                  required
                  :type="showPassword ? 'text' : 'password'"
                >
                <button class="pr-4 text-slate-400 dark:text-[#9cabba] hover:text-primary transition-colors cursor-pointer" type="button" @click="showPassword = !showPassword">
                  <span class="material-symbols-outlined text-[20px]">{{ showPassword ? 'visibility' : 'visibility_off' }}</span>
                </button>
              </div>
            </label>

            <label class="flex flex-col gap-1.5">
              <span class="text-slate-900 dark:text-white text-sm font-medium leading-normal">Xác nhận mật khẩu mới <span class="text-red-500">*</span></span>
              <div class="flex items-center rounded-xl border border-slate-300 dark:border-[#3b4754] bg-white dark:bg-[#1b2127] overflow-hidden focus-within:border-primary focus-within:ring-1 focus-within:ring-primary transition-all">
                <span class="pl-4 text-slate-400 dark:text-[#9cabba]"><span class="material-symbols-outlined text-[20px]">verified_user</span></span>
                <input
                  v-model="confirmPassword"
                  class="w-full border-none bg-transparent p-3.5 text-base text-slate-900 dark:text-white placeholder:text-slate-400 dark:placeholder:text-[#9cabba] focus:ring-0"
                  minlength="6"
                  placeholder="Nhập lại mật khẩu mới"
                  required
                  :type="showConfirmPassword ? 'text' : 'password'"
                >
                <button class="pr-4 text-slate-400 dark:text-[#9cabba] hover:text-primary transition-colors cursor-pointer" type="button" @click="showConfirmPassword = !showConfirmPassword">
                  <span class="material-symbols-outlined text-[20px]">{{ showConfirmPassword ? 'visibility' : 'visibility_off' }}</span>
                </button>
              </div>
            </label>

            <div class="flex gap-3">
              <button
                class="flex-1 h-12 rounded-xl border border-slate-300 dark:border-[#3b4754] text-slate-700 dark:text-white hover:bg-slate-50 dark:hover:bg-[#232b33] transition-all"
                :disabled="isLoading"
                type="button"
                @click="step = 2"
              >
                Quay lại
              </button>
              <button
                class="flex-1 h-12 rounded-xl bg-primary text-white font-bold hover:bg-primary-hover transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                :disabled="isLoading"
                type="submit"
              >
                {{ isLoading ? 'Đang cập nhật...' : 'Đổi mật khẩu' }}
              </button>
            </div>
          </form>

          <p class="mt-5 text-center text-sm text-slate-500 dark:text-[#b7a999]">
            Quay lại
            <router-link class="font-semibold text-primary transition-colors hover:text-[#a84f24]" to="/login">Đăng nhập</router-link>
          </p>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
  import { ref } from 'vue'
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
    { key: 'verify', label: '2. Xác minh' },
    { key: 'reset', label: '3. Đổi mật khẩu' },
  ]

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
      const response = await authApi.requestPasswordResetCode({ gmail: email.value })
      const debugCode = response?.debugCode || response?.code

      successMessage.value = 'Mã xác minh đã được gửi về email của bạn.'
      if (debugCode) {
        successMessage.value += `\nMã thử nghiệm (môi trường dev): ${debugCode}`
      }
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
      successMessage.value = 'Xác minh thành công. Bạn có thể đặt mật khẩu mới.'
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

    if (!code.value || code.value.length < 4) {
      errorMessage.value = 'Vui lòng nhập lại mã xác minh hợp lệ.'
      return
    }

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

      successMessage.value = 'Đổi mật khẩu thành công. Đang chuyển về trang đăng nhập...'
      setTimeout(() => {
        router.push('/login')
      }, 1200)
    } catch (error) {
      errorMessage.value = error?.message || 'Không thể đổi mật khẩu. Vui lòng thử lại.'
    } finally {
      isLoading.value = false
    }
  }
</script>
