<template>
  <div class="flex min-h-screen w-full flex-col bg-background-light dark:bg-background-dark text-slate-900 dark:text-white font-display antialiased overflow-hidden">

    <MainTopBar />

    <main class="layout-container flex flex-1 grow flex-col">
      <div class="px-4 md:px-10 lg:px-40 flex flex-1 justify-center py-8">
        <div class="layout-content-container flex flex-col max-w-[1200px] flex-1">
          <div class="flex flex-col md:flex-row md:items-end justify-between gap-6 mb-8">
            <div class="flex flex-col gap-3">
              <h1 class="text-slate-900 dark:text-white text-4xl font-black leading-tight tracking-[-0.033em]">Quản lý kho sản phẩm</h1>
              <p class="text-slate-500 dark:text-[#9cabba] text-base font-normal leading-normal max-w-2xl">Theo dõi danh sách giày, tồn kho, danh mục và biến thể để việc bán hàng ổn định hơn.</p>
            </div>
            <div class="flex gap-3 flex-wrap">
              <button class="flex min-w-[84px] cursor-pointer items-center justify-center overflow-hidden rounded-lg h-10 px-4 bg-slate-200 dark:bg-surface-dark hover:bg-slate-300 dark:hover:bg-[#283039] text-slate-900 dark:text-white text-sm font-bold leading-normal tracking-[0.015em] transition-colors gap-2">
                <span class="material-symbols-outlined text-[20px]">tune</span>
                <span class="truncate">Quản lý biến thể</span>
              </button>
              <button class="flex min-w-[84px] cursor-pointer items-center justify-center overflow-hidden rounded-lg h-10 px-4 bg-primary hover:bg-primary-hover text-white text-sm font-bold leading-normal tracking-[0.015em] shadow-lg shadow-zinc-900/10 transition-all gap-2">
                <span class="material-symbols-outlined text-[20px]">add</span>
                <span class="truncate">Thêm sản phẩm</span>
              </button>
            </div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
            <div class="p-4 rounded-xl bg-white dark:bg-surface-dark border border-slate-200 dark:border-border-dark flex items-center gap-4">
              <div class="bg-blue-100 dark:bg-blue-900/30 p-3 rounded-lg text-primary">
                <span class="material-symbols-outlined">inventory_2</span>
              </div>
              <div>
                <p class="text-slate-500 dark:text-[#9cabba] text-sm">Tổng sản phẩm</p>
                <p class="text-slate-900 dark:text-white text-xl font-bold">1,248</p>
              </div>
            </div>
            <div class="p-4 rounded-xl bg-white dark:bg-surface-dark border border-slate-200 dark:border-border-dark flex items-center gap-4">
              <div class="bg-green-100 dark:bg-green-900/30 p-3 rounded-lg text-green-600 dark:text-green-400">
                <span class="material-symbols-outlined">check_circle</span>
              </div>
              <div>
                <p class="text-slate-500 dark:text-[#9cabba] text-sm">Còn hàng</p>
                <p class="text-slate-900 dark:text-white text-xl font-bold">1,180</p>
              </div>
            </div>
            <div class="p-4 rounded-xl bg-white dark:bg-surface-dark border border-slate-200 dark:border-border-dark flex items-center gap-4">
              <div class="bg-red-100 dark:bg-red-900/30 p-3 rounded-lg text-red-600 dark:text-red-400">
                <span class="material-symbols-outlined">warning</span>
              </div>
              <div>
                <p class="text-slate-500 dark:text-[#9cabba] text-sm">Sắp hết hàng</p>
                <p class="text-slate-900 dark:text-white text-xl font-bold">45</p>
              </div>
            </div>
            <div class="p-4 rounded-xl bg-white dark:bg-surface-dark border border-slate-200 dark:border-border-dark flex items-center gap-4">
              <div class="bg-purple-100 dark:bg-purple-900/30 p-3 rounded-lg text-purple-600 dark:text-purple-400">
                <span class="material-symbols-outlined">category</span>
              </div>
              <div>
                <p class="text-slate-500 dark:text-[#9cabba] text-sm">Danh mục</p>
                <p class="text-slate-900 dark:text-white text-xl font-bold">12</p>
              </div>
            </div>
          </div>

          <div class="flex flex-col rounded-xl border border-slate-200 dark:border-border-dark bg-white dark:bg-surface-dark overflow-hidden shadow-sm">
            <div class="flex flex-col sm:flex-row justify-between items-center p-4 border-b border-slate-200 dark:border-border-dark gap-4">
              <div class="flex items-center gap-2 w-full sm:w-auto">
                <div class="relative w-full sm:w-64">
                  <span class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 material-symbols-outlined text-[20px]">search</span>
                  <input class="w-full pl-10 pr-4 py-2 bg-slate-50 dark:bg-[#111418] border border-slate-900 dark:border-border-dark rounded-lg text-sm text-slate-900 dark:text-white focus:ring-primary focus:border-primary" placeholder="Tìm theo tên hoặc mã..." type="text">
                </div>
              </div>
              <div class="flex items-center gap-2 w-full sm:w-auto">
                <button class="flex items-center gap-2 px-3 py-2 text-sm font-medium text-slate-600 dark:text-[#9cabba] hover:text-slate-900 dark:hover:text-white transition-colors">
                  <span class="material-symbols-outlined text-[18px]">filter_list</span>
                  Lọc
                </button>
                <button class="flex items-center gap-2 px-3 py-2 text-sm font-medium text-slate-600 dark:text-[#9cabba] hover:text-slate-900 dark:hover:text-white transition-colors">
                  <span class="material-symbols-outlined text-[18px]">download</span>
                  Xuất file
                </button>
              </div>
            </div>

            <div class="overflow-x-auto w-full">
              <table class="w-full min-w-[800px]">
                <thead class="bg-slate-50 dark:bg-[#111418]">
                  <tr>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-500 dark:text-[#9cabba] uppercase tracking-wider w-[140px]">Mã SP</th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-500 dark:text-[#9cabba] uppercase tracking-wider">Tên sản phẩm</th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-500 dark:text-[#9cabba] uppercase tracking-wider w-[140px]">Danh mục</th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-500 dark:text-[#9cabba] uppercase tracking-wider w-[120px]">Giá</th>
                    <th class="px-6 py-4 text-left text-xs font-semibold text-slate-500 dark:text-[#9cabba] uppercase tracking-wider w-[140px]">Trạng thái</th>
                    <th class="px-6 py-4 text-right text-xs font-semibold text-slate-500 dark:text-[#9cabba] uppercase tracking-wider w-[100px]">Tác vụ</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-slate-200 dark:divide-border-dark">
                  <tr class="hover:bg-slate-50 dark:hover:bg-[#283039]/30 transition-colors group">
                    <td class="px-6 py-4 text-sm text-slate-500 dark:text-[#9cabba] font-mono">P7-RUN-001</td>
                    <td class="px-6 py-4">
                      <div class="flex items-center gap-3">
                        <div class="size-10 rounded bg-slate-100 dark:bg-[#283039] bg-cover bg-center shrink-0" data-alt="Red running shoe side view" style="background-image: url(&quot;https://lh3.googleusercontent.com/aida-public/AB6AXuCEK6tYJzsFY41eajqO1X1msmmT-dZOJWU1jZfcNfLwfBKLunJLIgxFHjTlKtP5-7dfNHbyQFnc4EyPvLiQkJbZ9u9nrELT02Gw_WV6BO-0saPL6jHz_tza_h2CDt-mv65CjcyNpEfJ9l5VimV-VbeECvyQ6OdxCVhIbXo6hPOR7U55b8EgOdG7Tn-f5p3eNKyguoJAlEhNIINpa3gQb533oGA_gxGtMYu1rJIUAOUffTxU-eX_YNSKvk7fhF7w79tS2jXenyYMzH0&quot;);" />
                        <span class="text-sm font-medium text-slate-900 dark:text-white">Velocity Runner</span>
                      </div>
                    </td>
                    <td class="px-6 py-4">
                      <span class="inline-flex items-center rounded-md bg-blue-50 dark:bg-blue-900/30 px-2 py-1 text-xs font-medium text-blue-700 dark:text-blue-300 ring-1 ring-inset ring-blue-700/10 dark:ring-blue-700/30">Chạy bộ</span>
                    </td>
                    <td class="px-6 py-4 text-sm text-slate-600 dark:text-slate-300 font-medium">2.890.000 ₫</td>
                    <td class="px-6 py-4">
                      <span class="inline-flex items-center gap-1.5 rounded-full bg-green-100 dark:bg-green-900/30 px-2 py-1 text-xs font-medium text-green-700 dark:text-green-400">
                        <span class="size-1.5 rounded-full bg-green-600 dark:bg-green-400" /> Còn hàng
                      </span>
                    </td>
                    <td class="px-6 py-4 text-right">
                      <button class="text-slate-400 hover:text-primary transition-colors p-2 rounded-full hover:bg-slate-100 dark:hover:bg-[#283039]">
                        <span class="material-symbols-outlined text-[20px]">more_vert</span>
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div class="border-t border-slate-200 dark:border-border-dark px-4 py-3 flex items-center justify-between">
              <div class="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
                <div>
                  <p class="text-sm text-slate-500 dark:text-[#9cabba]">Đang hiển thị <span class="font-medium text-slate-900 dark:text-white">1</span> đến <span class="font-medium text-slate-900 dark:text-white">6</span> trên tổng <span class="font-medium text-slate-900 dark:text-white">97</span> kết quả</p>
                </div>
                <div>
                  <nav aria-label="Pagination" class="isolate inline-flex -space-x-px rounded-md shadow-sm">
                    <a class="relative inline-flex items-center rounded-l-md px-2 py-2 text-slate-400 ring-1 ring-inset ring-slate-300 dark:ring-border-dark hover:bg-slate-50 dark:hover:bg-[#283039] focus:z-20 focus:outline-offset-0" href="#">
                      <span class="sr-only">Trước</span>
                      <span class="material-symbols-outlined text-[20px]">chevron_left</span>
                    </a>
                    <a aria-current="page" class="relative z-10 inline-flex items-center bg-primary px-4 py-2 text-sm font-semibold text-white focus:z-20 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600" href="#">1</a>
                    <a class="relative inline-flex items-center px-4 py-2 text-sm font-semibold text-slate-900 dark:text-white ring-1 ring-inset ring-slate-300 dark:ring-border-dark hover:bg-slate-50 dark:hover:bg-[#283039] focus:z-20 focus:outline-offset-0" href="#">2</a>
                    <a class="relative inline-flex items-center rounded-r-md px-2 py-2 text-slate-400 ring-1 ring-inset ring-slate-300 dark:ring-border-dark hover:bg-slate-50 dark:hover:bg-[#283039] focus:z-20 focus:outline-offset-0" href="#">
                      <span class="sr-only">Sau</span>
                      <span class="material-symbols-outlined text-[20px]">chevron_right</span>
                    </a>
                  </nav>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
</script>
