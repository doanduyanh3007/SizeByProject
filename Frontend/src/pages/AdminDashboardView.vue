<template>
  <div class="flex min-h-screen w-full flex-col bg-background-light dark:bg-background-dark text-slate-900 dark:text-white font-display antialiased overflow-hidden">

    <MainTopBar />

    <div class="flex flex-1 overflow-hidden">

    <aside class="hidden md:flex flex-col w-72 h-full border-r border-slate-200 dark:border-slate-800 bg-surface-light dark:bg-background-dark">
      <div class="flex flex-col h-full p-4">
        <div class="flex items-center gap-3 px-2 py-6 mb-2">
          <div class="size-12 flex items-center justify-center bg-primary text-white rounded-lg">
            <span class="material-symbols-outlined text-2xl">bolt</span>
          </div>
          <div class="flex flex-col">
            <h1 class="text-slate-900 dark:text-white text-lg font-bold leading-tight">SizeBy</h1>
            <p class="text-slate-500 dark:text-slate-400 text-xs font-normal">Bảng điều khiển quản trị</p>
          </div>
        </div>

        <nav class="flex flex-col gap-1 flex-1">
          <router-link class="flex items-center gap-3 px-3 py-3 rounded-lg bg-primary text-white group transition-colors" to="/admin">
            <span class="material-symbols-outlined text-[24px]">dashboard</span>
            <p class="text-sm font-medium">Tổng quan</p>
          </router-link>

          <router-link class="flex items-center gap-3 px-3 py-3 rounded-lg text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-surface-dark transition-colors" to="/admin/products">
            <span class="material-symbols-outlined text-[24px]">inventory_2</span>
            <p class="text-sm font-medium">Sản phẩm</p>
          </router-link>

          <a class="flex items-center gap-3 px-3 py-3 rounded-lg text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-surface-dark transition-colors" href="#">
            <span class="material-symbols-outlined text-[24px]">category</span>
            <p class="text-sm font-medium">Danh mục</p>
          </a>
          <a class="flex items-center gap-3 px-3 py-3 rounded-lg text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-surface-dark transition-colors" href="#">
            <span class="material-symbols-outlined text-[24px]">group</span>
            <p class="text-sm font-medium">Tài khoản</p>
          </a>
          <a class="flex items-center gap-3 px-3 py-3 rounded-lg text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-surface-dark transition-colors" href="#">
            <span class="material-symbols-outlined text-[24px]">shopping_bag</span>
            <div class="flex flex-1 justify-between items-center">
              <p class="text-sm font-medium">Đơn hàng</p>
              <span class="bg-primary/20 text-primary text-xs font-bold px-2 py-0.5 rounded-full">3</span>
            </div>
          </a>
        </nav>

        <div class="mt-auto border-t border-slate-200 dark:border-slate-800 pt-4">
          <router-link class="flex items-center gap-3 px-3 py-2 rounded-lg text-red-500 hover:bg-red-50 dark:hover:bg-red-900/10 transition-colors mt-1" to="/login">
            <span class="material-symbols-outlined text-[24px]">logout</span>
            <p class="text-sm font-medium">Đăng xuất</p>
          </router-link>
        </div>
      </div>
    </aside>

    <main class="flex-1 flex flex-col h-full overflow-hidden bg-background-light dark:bg-[#101922] relative">

      <div class="flex-1 overflow-y-auto p-4 md:p-8">
        <div class="max-w-7xl mx-auto flex flex-col gap-6">
          <div class="flex flex-col md:flex-row md:items-end justify-between gap-4">
            <div>
              <p class="text-sm font-medium text-slate-500 dark:text-slate-400 mb-1">Chào mừng quay lại</p>
              <h1 class="text-slate-900 dark:text-white text-3xl font-bold leading-tight">Tổng quan vận hành</h1>
            </div>
            <div class="flex gap-3">
              <button class="flex items-center gap-2 px-4 py-2 bg-white dark:bg-surface-dark border border-slate-200 dark:border-slate-700 rounded-lg text-sm font-medium hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors">
                <span class="material-symbols-outlined text-[20px]">cloud_download</span>
                Xuất báo cáo
              </button>
              <router-link class="flex items-center gap-2 px-4 py-2 bg-primary text-white rounded-lg text-sm font-medium hover:bg-primary-hover transition-colors shadow-lg shadow-primary/30" to="/admin/products">
                <span class="material-symbols-outlined text-[20px]">add</span>
                Thêm sản phẩm
              </router-link>
            </div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div class="flex flex-col gap-3 p-6 rounded-xl bg-white dark:bg-surface-dark border border-slate-200 dark:border-slate-800 shadow-sm">
              <div class="flex items-center justify-between">
                <p class="text-slate-500 dark:text-slate-400 text-sm font-medium">Tổng doanh thu</p>
                <div class="p-2 bg-green-100 dark:bg-green-900/20 rounded-lg text-green-600 dark:text-green-400">
                  <span class="material-symbols-outlined text-[20px]">attach_money</span>
                </div>
              </div>
              <div class="flex items-end gap-3">
                <p class="text-slate-900 dark:text-white text-3xl font-bold leading-none">1,08 tỷ ₫</p>
                <div class="flex items-center text-[#0bda5b] text-sm font-medium bg-[#0bda5b]/10 px-2 py-0.5 rounded-full">
                  <span class="material-symbols-outlined text-[16px] mr-1">trending_up</span>
                  +12%
                </div>
              </div>
              <p class="text-xs text-slate-400 dark:text-slate-500">So với tháng trước</p>
            </div>
            <div class="flex flex-col gap-3 p-6 rounded-xl bg-white dark:bg-surface-dark border border-slate-200 dark:border-slate-800 shadow-sm">
              <div class="flex items-center justify-between">
                <p class="text-slate-500 dark:text-slate-400 text-sm font-medium">Tổng đơn hàng</p>
                <div class="p-2 bg-primary/10 rounded-lg text-primary">
                  <span class="material-symbols-outlined text-[20px]">shopping_cart</span>
                </div>
              </div>
              <div class="flex items-end gap-3">
                <p class="text-slate-900 dark:text-white text-3xl font-bold leading-none">1.204</p>
                <div class="flex items-center text-[#0bda5b] text-sm font-medium bg-[#0bda5b]/10 px-2 py-0.5 rounded-full">
                  <span class="material-symbols-outlined text-[16px] mr-1">trending_up</span>
                  +5%
                </div>
              </div>
              <p class="text-xs text-slate-400 dark:text-slate-500">So với tháng trước</p>
            </div>
            <div class="flex flex-col gap-3 p-6 rounded-xl bg-white dark:bg-surface-dark border border-slate-200 dark:border-slate-800 shadow-sm">
              <div class="flex items-center justify-between">
                <p class="text-slate-500 dark:text-slate-400 text-sm font-medium">Voucher đang hoạt động</p>
                <div class="p-2 bg-orange-100 dark:bg-orange-900/20 rounded-lg text-orange-500">
                  <span class="material-symbols-outlined text-[20px]">local_activity</span>
                </div>
              </div>
              <div class="flex items-end gap-3">
                <p class="text-slate-900 dark:text-white text-3xl font-bold leading-none">15</p>
                <div class="flex items-center text-[#fa6238] text-sm font-medium bg-[#fa6238]/10 px-2 py-0.5 rounded-full">
                  <span class="material-symbols-outlined text-[16px] mr-1">trending_down</span>
                  -2%
                </div>
              </div>
              <p class="text-xs text-slate-400 dark:text-slate-500">So với tháng trước</p>
            </div>
          </div>

          <div class="flex flex-col gap-4">
            <div class="flex items-center justify-between px-1">
              <h2 class="text-slate-900 dark:text-white text-xl font-bold">Đơn hàng gần đây</h2>
              <a class="text-primary text-sm font-medium hover:underline" href="#">Xem tất cả</a>
            </div>
            <div class="w-full overflow-hidden rounded-xl border border-slate-200 dark:border-slate-800 bg-white dark:bg-surface-dark shadow-sm">
              <div class="overflow-x-auto">
                <table class="w-full text-left text-sm">
                  <thead class="bg-slate-50 dark:bg-[#151c24] border-b border-slate-200 dark:border-slate-800">
                    <tr>
                      <th class="px-6 py-4 font-semibold text-slate-500 dark:text-slate-400 whitespace-nowrap">Mã đơn</th>
                      <th class="px-6 py-4 font-semibold text-slate-500 dark:text-slate-400 whitespace-nowrap">Khách hàng</th>
                      <th class="px-6 py-4 font-semibold text-slate-500 dark:text-slate-400 whitespace-nowrap">Tổng tiền</th>
                      <th class="px-6 py-4 font-semibold text-slate-500 dark:text-slate-400 whitespace-nowrap">Ngày</th>
                      <th class="px-6 py-4 font-semibold text-slate-500 dark:text-slate-400 whitespace-nowrap">Trạng thái</th>
                      <th class="px-6 py-4 font-semibold text-slate-500 dark:text-slate-400 whitespace-nowrap text-right">Thao tác</th>
                    </tr>
                  </thead>
                  <tbody class="divide-y divide-slate-100 dark:divide-slate-800">
                    <tr class="hover:bg-slate-50 dark:hover:bg-slate-800/50 transition-colors">
                      <td class="px-6 py-4 font-medium text-slate-900 dark:text-white">#ORD-001</td>
                      <td class="px-6 py-4 text-slate-600 dark:text-slate-300">
                        <div class="flex items-center gap-3">
                          <div class="size-8 rounded-full bg-slate-200 dark:bg-slate-700 bg-cover bg-center" style="background-image: url('https://lh3.googleusercontent.com/aida-public/AB6AXuBqJTbBfRXT5_L1Qvlv44uCxpt_zDw5hNV9rmszoSFoPCO0kEJu_poB3HrtHev7Po8CZmQSZ6zCjeApq6BMJmQ06OxV-ykV0hDgKgfQiC5PmOE3hRRrN88A2MqnHOrQSPqXOUShb8M4bv3ltOv7FQqf0Ehs3tqWC2Y_K-EUC48kQkjw-w4myP5_OO12zJ_9IFOrDHl7XE469PYD1ZAlnPb996QXSQHxFenPlgEg4elK_l-uFFltRTYruyYjsm9Mtl8RnnNLM5C3vO8');" />
                          Khách lẻ #1
                        </div>
                      </td>
                      <td class="px-6 py-4 text-slate-600 dark:text-slate-300">2.890.000 ₫</td>
                      <td class="px-6 py-4 text-slate-600 dark:text-slate-300">25 thg 10, 2023</td>
                      <td class="px-6 py-4">
                        <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400">Chờ xác nhận</span>
                      </td>
                      <td class="px-6 py-4 text-right">
                        <button class="text-primary hover:text-blue-400 font-medium text-sm transition-colors">Xác nhận</button>
                      </td>
                    </tr>
                    <tr class="hover:bg-slate-50 dark:hover:bg-slate-800/50 transition-colors">
                      <td class="px-6 py-4 font-medium text-slate-900 dark:text-white">#ORD-002</td>
                      <td class="px-6 py-4 text-slate-600 dark:text-slate-300">
                        <div class="flex items-center gap-3">
                          <div class="size-8 rounded-full bg-slate-200 dark:bg-slate-700 flex items-center justify-center text-xs font-bold text-slate-500">JS</div>
                          Khách lẻ #2
                        </div>
                      </td>
                      <td class="px-6 py-4 text-slate-600 dark:text-slate-300">1.950.000 ₫</td>
                      <td class="px-6 py-4 text-slate-600 dark:text-slate-300">24 thg 10, 2023</td>
                      <td class="px-6 py-4">
                        <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400">Đang giao</span>
                      </td>
                      <td class="px-6 py-4 text-right">
                        <button class="text-slate-500 hover:text-white font-medium text-sm transition-colors">Theo dõi</button>
                      </td>
                    </tr>
                    <tr class="hover:bg-slate-50 dark:hover:bg-slate-800/50 transition-colors">
                      <td class="px-6 py-4 font-medium text-slate-900 dark:text-white">#ORD-003</td>
                      <td class="px-6 py-4 text-slate-600 dark:text-slate-300">
                        <div class="flex items-center gap-3">
                          <div class="size-8 rounded-full bg-slate-200 dark:bg-slate-700 bg-cover bg-center" style="background-image: url('https://lh3.googleusercontent.com/aida-public/AB6AXuAGIC28eNiD5IlJxh0d43IAw7UBSPPrHJTfrsqvneUYTa688CaC6DVpz-Ove-7B-wuS1flOSIGloPNKZGQLfu38YkzKyHNT4s9sP-iToUCZol278x-zaIvXxJranrqk54wLGiCMMIGFzdbcdThEOWpzdB3h_l_LwfXvMNOUSxSI4Vg2Cl2GQHwjYrdPYKARfh3hKf3NBdsGONSzSjoFY24mTkHsKtbHh5nSkI_7uSxy6bP_bNLQV1XfAEtr81urtH3Jhu6C16C3oAA');" />
                          Khách lẻ #3
                        </div>
                      </td>
                      <td class="px-6 py-4 text-slate-600 dark:text-slate-300">4.980.000 ₫</td>
                      <td class="px-6 py-4 text-slate-600 dark:text-slate-300">24 thg 10, 2023</td>
                      <td class="px-6 py-4">
                        <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400">Đã giao</span>
                      </td>
                      <td class="px-6 py-4 text-right">
                        <button class="text-slate-500 hover:text-white font-medium text-sm transition-colors">Xem</button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <div class="flex items-center justify-between px-6 py-4 border-t border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-surface-dark">
                <p class="text-sm text-slate-500 dark:text-slate-400">Hiển thị <span class="font-medium">1</span> đến <span class="font-medium">5</span> trên tổng <span class="font-medium">50</span> đơn hàng</p>
                <div class="flex gap-2">
                  <button class="px-3 py-1 text-sm border border-slate-200 dark:border-slate-700 rounded bg-white dark:bg-surface-dark text-slate-600 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800 disabled:opacity-50" disabled="">Trước</button>
                  <button class="px-3 py-1 text-sm border border-slate-200 dark:border-slate-700 rounded bg-white dark:bg-surface-dark text-slate-600 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800">Sau</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
    </div>
  </div>
</template>

<script setup>
</script>
