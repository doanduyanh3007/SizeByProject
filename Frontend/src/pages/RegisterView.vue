<template>
  <div class="retail-shell text-slate-900 dark:text-white overflow-x-hidden antialiased flex flex-col min-h-screen">
    <MainTopBar />

    <main class="mx-auto flex w-full max-w-[1240px] flex-1 flex-col gap-6 px-4 py-8 md:px-8 lg:flex-row lg:items-stretch lg:gap-8 xl:px-10">
      <section class="hidden lg:flex lg:w-[52%] retail-card overflow-hidden p-0">
        <div class="relative flex w-full items-stretch">
          <img
            alt="Customer opening a Peak Seven shoe box in a warm retail setting"
            class="absolute inset-0 h-full w-full object-cover"
            src="https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?auto=format&fit=crop&w=1400&q=80"
          >
          <div class="absolute inset-0 bg-gradient-to-t from-[#16110d]/95 via-[#16110d]/55 to-[#16110d]/10" />
          <div class="absolute inset-0 bg-[radial-gradient(circle_at_top_right,rgba(255,255,255,0.16),transparent_32%)]" />

          <div class="relative z-10 flex w-full flex-col justify-between gap-8 p-10 text-white">
            <div class="space-y-4">
              <span class="retail-kicker w-fit border-white/30 bg-white/15 text-white">Thành viên SizeBy</span>
              <div>
                <h1 class="text-[42px] font-black leading-[1.02] tracking-tight">Tạo tài khoản để giữ mọi lần mua sắm gọn gàng trong một nơi.</h1>
                <p class="mt-4 max-w-xl text-sm leading-7 text-[#f1dfd3]">Đăng ký một lần để lưu địa chỉ nhận hàng, theo dõi đơn rõ ràng và giữ sẵn ưu đãi cho những lần mua tiếp theo.</p>
              </div>
            </div>

            <div class="grid grid-cols-2 gap-3 xl:grid-cols-3">
              <article class="rounded-2xl border border-white/20 bg-white/10 px-4 py-4 backdrop-blur-sm">
                <p class="text-[11px] uppercase tracking-[0.18em] text-[#f7c2a4]">Tạo hồ sơ</p>
                <p class="mt-2 text-xl font-bold leading-tight">Lưu địa chỉ và thông tin nhận hàng</p>
              </article>
              <article class="rounded-2xl border border-white/20 bg-white/10 px-4 py-4 backdrop-blur-sm">
                <p class="text-[11px] uppercase tracking-[0.18em] text-[#f7c2a4]">Ưu đãi</p>
                <p class="mt-2 text-xl font-bold leading-tight">Giữ voucher thành viên sẵn để dùng</p>
              </article>
              <article class="rounded-2xl border border-white/20 bg-white/10 px-4 py-4 backdrop-blur-sm col-span-2 xl:col-span-1">
                <p class="text-[11px] uppercase tracking-[0.18em] text-[#f7c2a4]">Theo dõi</p>
                <p class="mt-2 text-xl font-bold leading-tight">Xem lịch sử đơn và đánh giá nhanh hơn</p>
              </article>
            </div>

            <div class="rounded-[26px] border border-white/15 bg-black/20 p-5 backdrop-blur-sm">
              <p class="text-[11px] uppercase tracking-[0.22em] text-[#f7c2a4]">Bắt đầu nhanh</p>
              <div class="mt-3 space-y-3 text-sm text-[#f6e9de]">
                <div class="flex items-start gap-3">
                  <span class="material-symbols-outlined text-[18px] text-[#f7c2a4]">check_circle</span>
                  <p>Chỉ cần tên đăng nhập, email, số điện thoại và mật khẩu để hoàn tất.</p>
                </div>
                <div class="flex items-start gap-3">
                  <span class="material-symbols-outlined text-[18px] text-[#f7c2a4]">local_shipping</span>
                  <p>Đơn hàng sau này điền thông tin giao nhận nhanh hơn vì dữ liệu đã được lưu.</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="w-full lg:w-[48%] flex items-center justify-center">
        <div class="retail-card w-full max-w-[560px] px-6 py-7 sm:px-8">
          <div class="text-center lg:text-left">
            <h1 class="pb-2 text-[32px] font-bold leading-tight tracking-tight text-slate-900 dark:text-white">Tạo tài khoản mới</h1>
            <p class="text-base text-slate-500 dark:text-[#b7a999]">Hoàn tất vài thông tin cơ bản để bắt đầu mua sắm, lưu ưu đãi và quản lý đơn dễ hơn.</p>
          </div>

          <div class="w-full py-3">
            <div class="flex h-12 w-full items-center justify-center rounded-xl bg-[#f2e9dd] p-1 dark:bg-[#2b241f]">
              <router-link class="flex h-full flex-1 cursor-pointer items-center justify-center overflow-hidden rounded-lg px-2 text-sm font-medium text-slate-500 transition-all hover:bg-black/5 hover:text-slate-800 dark:text-[#b7a999] dark:hover:bg-white/5 dark:hover:text-white" to="/login">
                <span class="truncate">Đăng nhập</span>
              </router-link>
              <div class="flex h-full flex-1 cursor-default items-center justify-center overflow-hidden rounded-lg bg-white px-2 text-sm font-medium text-slate-900 shadow-sm transition-all dark:bg-[#111418] dark:text-white">
                <span class="truncate">Đăng ký</span>
              </div>
            </div>
          </div>

          <div v-if="errorMessage" class="rounded-lg border border-red-400 bg-red-100 px-4 py-3 text-red-700 dark:border-red-800 dark:bg-red-900/20 dark:text-red-300">
            {{ errorMessage }}
          </div>

          <div v-if="successMessage" class="mt-3 rounded-lg border border-green-400 bg-green-100 px-4 py-3 text-green-700 dark:border-green-800 dark:bg-green-900/20 dark:text-green-300">
            {{ successMessage }}
          </div>

          <form class="mt-4 flex flex-col gap-4" @submit.prevent="handleRegister">
            <div class="grid gap-4 sm:grid-cols-2">
              <label class="flex flex-col gap-1.5">
                <span class="text-sm font-medium leading-normal text-slate-900 dark:text-white">Tên đăng nhập <span class="text-red-500">*</span></span>
                <div class="flex items-center overflow-hidden rounded-xl border border-slate-300 bg-white transition-all focus-within:border-primary focus-within:ring-1 focus-within:ring-primary dark:border-[#3b4754] dark:bg-[#1b2127]">
                  <span class="pl-4 text-slate-400 dark:text-[#9cabba]"><span class="material-symbols-outlined text-[20px]">person</span></span>
                  <input
                    v-model="formData.username"
                    class="w-full border-none bg-transparent p-3.5 text-base text-slate-900 placeholder:text-slate-400 focus:ring-0 dark:text-white dark:placeholder:text-[#9cabba]"
                    placeholder="peakseven.member"
                    required
                    type="text"
                  >
                </div>
              </label>

              <label class="flex flex-col gap-1.5">
                <span class="text-sm font-medium leading-normal text-slate-900 dark:text-white">Số điện thoại <span class="text-red-500">*</span></span>
                <div class="flex items-center overflow-hidden rounded-xl border border-slate-300 bg-white transition-all focus-within:border-primary focus-within:ring-1 focus-within:ring-primary dark:border-[#3b4754] dark:bg-[#1b2127]">
                  <span class="pl-4 text-slate-400 dark:text-[#9cabba]"><span class="material-symbols-outlined text-[20px]">call</span></span>
                  <input
                    v-model="formData.phone"
                    class="w-full border-none bg-transparent p-3.5 text-base text-slate-900 placeholder:text-slate-400 focus:ring-0 dark:text-white dark:placeholder:text-[#9cabba]"
                    placeholder="09xxxxxxxx"
                    required
                    type="tel"
                  >
                </div>
              </label>
            </div>

            <label class="flex flex-col gap-1.5">
              <span class="text-sm font-medium leading-normal text-slate-900 dark:text-white">Email liên hệ <span class="text-red-500">*</span></span>
              <div class="flex items-center overflow-hidden rounded-xl border border-slate-300 bg-white transition-all focus-within:border-primary focus-within:ring-1 focus-within:ring-primary dark:border-[#3b4754] dark:bg-[#1b2127]">
                <span class="pl-4 text-slate-400 dark:text-[#9cabba]"><span class="material-symbols-outlined text-[20px]">mail</span></span>
                <input
                  v-model="formData.gmail"
                  class="w-full border-none bg-transparent p-3.5 text-base text-slate-900 placeholder:text-slate-400 focus:ring-0 dark:text-white dark:placeholder:text-[#9cabba]"
                  placeholder="you@gmail.com"
                  required
                  type="email"
                >
              </div>
            </label>

            <label class="flex flex-col gap-1.5">
              <span class="text-sm font-medium leading-normal text-slate-900 dark:text-white">Địa chỉ giao nhận</span>
              <div class="flex items-center overflow-hidden rounded-xl border border-slate-300 bg-white transition-all focus-within:border-primary focus-within:ring-1 focus-within:ring-primary dark:border-[#3b4754] dark:bg-[#1b2127]">
                <span class="pl-4 text-slate-400 dark:text-[#9cabba]"><span class="material-symbols-outlined text-[20px]">home</span></span>
                <input
                  v-model="formData.address"
                  class="w-full border-none bg-transparent p-3.5 text-base text-slate-900 placeholder:text-slate-400 focus:ring-0 dark:text-white dark:placeholder:text-[#9cabba]"
                  placeholder="Số nhà, đường, quận hoặc huyện"
                  type="text"
                >
              </div>
            </label>

            <div class="grid gap-4 sm:grid-cols-2">
              <label class="flex flex-col gap-1.5">
                <span class="text-sm font-medium leading-normal text-slate-900 dark:text-white">Mật khẩu <span class="text-red-500">*</span></span>
                <div class="flex items-center overflow-hidden rounded-xl border border-slate-300 bg-white transition-all focus-within:border-primary focus-within:ring-1 focus-within:ring-primary dark:border-[#3b4754] dark:bg-[#1b2127]">
                  <span class="pl-4 text-slate-400 dark:text-[#9cabba]"><span class="material-symbols-outlined text-[20px]">lock</span></span>
                  <input
                    v-model="formData.password"
                    class="w-full border-none bg-transparent p-3.5 text-base text-slate-900 placeholder:text-slate-400 focus:ring-0 dark:text-white dark:placeholder:text-[#9cabba]"
                    minlength="6"
                    placeholder="Tối thiểu 6 ký tự"
                    required
                    :type="showPassword ? 'text' : 'password'"
                  >
                  <button class="cursor-pointer pr-4 text-slate-400 transition-colors hover:text-primary dark:text-[#9cabba]" type="button" @click="showPassword = !showPassword">
                    <span class="material-symbols-outlined text-[20px]">{{ showPassword ? 'visibility' : 'visibility_off' }}</span>
                  </button>
                </div>
              </label>

              <label class="flex flex-col gap-1.5">
                <span class="text-sm font-medium leading-normal text-slate-900 dark:text-white">Nhập lại mật khẩu <span class="text-red-500">*</span></span>
                <div class="flex items-center overflow-hidden rounded-xl border border-slate-300 bg-white transition-all focus-within:border-primary focus-within:ring-1 focus-within:ring-primary dark:border-[#3b4754] dark:bg-[#1b2127]">
                  <span class="pl-4 text-slate-400 dark:text-[#9cabba]"><span class="material-symbols-outlined text-[20px]">verified_user</span></span>
                  <input
                    v-model="formData.confirmPassword"
                    class="w-full border-none bg-transparent p-3.5 text-base text-slate-900 placeholder:text-slate-400 focus:ring-0 dark:text-white dark:placeholder:text-[#9cabba]"
                    minlength="6"
                    placeholder="Nhập lại để xác nhận"
                    required
                    :type="showConfirmPassword ? 'text' : 'password'"
                  >
                  <button class="cursor-pointer pr-4 text-slate-400 transition-colors hover:text-primary dark:text-[#9cabba]" type="button" @click="showConfirmPassword = !showConfirmPassword">
                    <span class="material-symbols-outlined text-[20px]">{{ showConfirmPassword ? 'visibility' : 'visibility_off' }}</span>
                  </button>
                </div>
              </label>
            </div>

            <div class="rounded-2xl border border-[#eadbca] bg-[#faf5ef] px-4 py-4 dark:border-[#3c342e] dark:bg-[#211b17]">
              <p class="text-[11px] font-semibold uppercase tracking-[0.18em] text-primary">Quyền lợi khi đăng ký</p>
              <div class="mt-3 grid gap-2 text-sm leading-6 text-slate-600 dark:text-[#d7c7b8]">
                <div class="flex items-start gap-3">
                  <span class="material-symbols-outlined text-[18px] text-primary">check_circle</span>
                  <p>Lưu thông tin giao nhận để thanh toán nhanh hơn ở các đơn tiếp theo.</p>
                </div>
                <div class="flex items-start gap-3">
                  <span class="material-symbols-outlined text-[18px] text-primary">sell</span>
                  <p>Nhận và quản lý voucher thành viên trong tài khoản cá nhân.</p>
                </div>
                <div class="flex items-start gap-3">
                  <span class="material-symbols-outlined text-[18px] text-primary">history</span>
                  <p>Theo dõi lịch sử đơn hàng và để lại đánh giá thuận tiện hơn.</p>
                </div>
              </div>
            </div>

            <div class="flex items-start gap-3 rounded-2xl border border-[#eadbca] bg-white/70 px-4 py-3 dark:border-[#3c342e] dark:bg-[#181411]">
              <div class="flex h-5 items-center">
                <input
                  id="terms"
                  v-model="formData.agreeTerms"
                  class="h-4 w-4 rounded border-gray-300 bg-white text-primary focus:ring-primary dark:border-[#3b4754] dark:bg-[#1b2127]"
                  required
                  type="checkbox"
                >
              </div>
              <label class="text-sm leading-6 text-slate-600 dark:text-[#cbbcad]" for="terms">
                Tôi đồng ý với <a class="font-medium text-slate-900 underline decoration-slate-400 underline-offset-2 hover:decoration-primary dark:text-white" href="#">Điều khoản dịch vụ</a> và <a class="font-medium text-slate-900 underline decoration-slate-400 underline-offset-2 hover:decoration-primary dark:text-white" href="#">Chính sách bảo mật</a>.
              </label>
            </div>

            <div class="flex flex-col gap-4">
              <button
                class="flex h-12 w-full cursor-pointer items-center justify-center overflow-hidden rounded-xl bg-primary text-base font-bold leading-normal tracking-[0.015em] text-white shadow-lg shadow-zinc-900/10 transition-all hover:bg-primary-hover disabled:cursor-not-allowed disabled:opacity-50"
                :disabled="isLoading"
                type="submit"
              >
                <span v-if="isLoading" class="mr-2">
                  <svg class="h-5 w-5 animate-spin text-white" fill="none" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
                    <path class="opacity-75" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" fill="currentColor" />
                  </svg>
                </span>
                {{ isLoading ? 'Đang tạo tài khoản...' : 'Tạo tài khoản' }}
              </button>

              <p class="text-center text-sm text-slate-500 dark:text-[#b7a999]">
                Đã có tài khoản?
                <router-link class="font-semibold text-primary transition-colors hover:text-[#a84f24]" to="/login">Đăng nhập ngay</router-link>
              </p>
            </div>
          </form>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
  import { reactive, ref } from 'vue'
  import { useRouter } from 'vue-router'
  import { authApi } from '../services/api'

  const router = useRouter()

  const showPassword = ref(false)
  const showConfirmPassword = ref(false)
  const isLoading = ref(false)
  const errorMessage = ref('')
  const successMessage = ref('')

  const formData = reactive({
    username: '',
    gmail: '',
    phone: '',
    address: '',
    password: '',
    confirmPassword: '',
    agreeTerms: false,
  })

  async function handleRegister () {
    errorMessage.value = ''
    successMessage.value = ''

    if (!formData.username || !formData.gmail || !formData.phone || !formData.password || !formData.confirmPassword) {
      errorMessage.value = 'Vui lòng điền đầy đủ thông tin bắt buộc'
      return
    }

    if (formData.password !== formData.confirmPassword) {
      errorMessage.value = 'Mật khẩu xác nhận không khớp'
      return
    }

    if (formData.password.length < 6) {
      errorMessage.value = 'Mật khẩu phải có ít nhất 6 ký tự'
      return
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailRegex.test(formData.gmail)) {
      errorMessage.value = 'Email không hợp lệ'
      return
    }

    const phoneRegex = /^[0-9]{10,11}$/
    if (!phoneRegex.test(formData.phone)) {
      errorMessage.value = 'Số điện thoại phải có 10-11 chữ số'
      return
    }

    if (!formData.agreeTerms) {
      errorMessage.value = 'Vui lòng đồng ý với điều khoản dịch vụ'
      return
    }

    isLoading.value = true

    try {
      console.log('Sending data:', {
        username: formData.username,
        gmail: formData.gmail,
        phone: formData.phone,
        address: formData.address,
        password: formData.password,
      })

      const response = await authApi.register({
        username: formData.username,
        gmail: formData.gmail,
        phone: formData.phone,
        address: formData.address || '',
        password: formData.password,
      })

      console.log('Response from server:', response)

      if (response && response.id) {
        successMessage.value = 'Đăng ký thành công! Đang chuyển hướng đến trang đăng nhập...'

        formData.username = ''
        formData.gmail = ''
        formData.phone = ''
        formData.address = ''
        formData.password = ''
        formData.confirmPassword = ''
        formData.agreeTerms = false

        setTimeout(() => {
          router.push('/login')
        }, 2000)
      } else {
        errorMessage.value = response?.message || 'Đăng ký thất bại. Vui lòng thử lại.'
      }
    } catch (error) {
      console.error('Registration error:', error)

      if (error.status === 400) {
        if (error.errors) {
          const errorMessages = Object.values(error.errors).join(', ')
          errorMessage.value = errorMessages || 'Dữ liệu không hợp lệ'
        } else if (error.data && error.data.message) {
          errorMessage.value = error.data.message
        } else {
          errorMessage.value = 'Dữ liệu không hợp lệ. Vui lòng kiểm tra:'
          errorMessage.value += '\n- Username không được để trống'
          errorMessage.value += '\n- Email phải đúng định dạng'
          errorMessage.value += '\n- Số điện thoại phải 10-11 số'
          errorMessage.value += '\n- Mật khẩu tối thiểu 6 ký tự'
        }
      } else if (error.status === 409) {
        errorMessage.value = 'Email này đã được đăng ký. Vui lòng sử dụng email khác.'
      } else {
        errorMessage.value = error.message || 'Có lỗi xảy ra. Vui lòng thử lại sau.'
      }
    } finally {
      isLoading.value = false
    }
  }
</script>


