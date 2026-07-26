<template>
  <div class="retail-shell min-h-screen">
    <MainTopBar />

    <main class="mx-auto flex w-full max-w-[1240px] flex-col gap-10 px-4 py-10 md:px-8 xl:px-10">
      <section class="grid gap-6 lg:grid-cols-[1.05fr_0.95fr]">
        <article class="retail-card px-6 py-7 sm:px-8 lg:px-9 lg:py-9">
          <span class="retail-kicker">Hướng dẫn đo chân</span>
          <h1 class="retail-heading mt-4 text-[54px] leading-[0.92] text-slate-900 dark:text-white sm:text-[72px]">Chọn đúng size ngay từ lần đầu.</h1>
          <p class="mt-6 max-w-2xl text-base leading-8 text-slate-600 dark:text-[#cabdae] sm:text-lg">
            Đo đúng chiều dài bàn chân theo cm rồi đối chiếu bảng US/EU bên dưới.
            Cách này giúp bạn giảm rủi ro lệch size khi đặt hàng online.
          </p>

          <div class="mt-8 grid gap-4 sm:grid-cols-3">
            <article v-for="tip in quickTips" :key="tip.title" class="rounded-[24px] border border-slate-200/70 bg-white/75 p-5 dark:border-[#3c342e] dark:bg-[#241d19]/80">
              <span class="material-symbols-outlined text-[28px] text-primary">{{ tip.icon }}</span>
              <h3 class="mt-3 text-lg font-semibold text-slate-900 dark:text-white">{{ tip.title }}</h3>
              <p class="mt-2 text-sm leading-6 text-slate-600 dark:text-[#cabdae]">{{ tip.description }}</p>
            </article>
          </div>
        </article>

        <article class="retail-card overflow-hidden p-0">
          <img
            src="https://images.unsplash.com/photo-1543508282-6319a3e2621f?auto=format&fit=crop&w=1400&q=80"
            alt="Đo size giày bằng thước"
            class="aspect-[4/5] h-full w-full object-cover lg:min-h-[430px]"
          />
        </article>
      </section>

      <section class="grid grid-cols-1 gap-6 lg:grid-cols-2">
        <article class="retail-card overflow-hidden">
          <div class="border-b border-slate-200/70 px-6 py-5 dark:border-[#3c342e]">
            <h2 class="text-2xl font-semibold text-slate-900 dark:text-white">Nam / Unisex (US)</h2>
          </div>
          <div class="overflow-x-auto px-6 py-5">
            <table class="w-full min-w-[320px] text-sm">
              <thead>
                <tr class="text-left text-slate-500 dark:text-[#cabdae]">
                  <th class="py-2">US</th>
                  <th class="py-2">EU</th>
                  <th class="py-2">CM</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-200 dark:divide-[#3c342e]">
                <tr v-for="row in menRows" :key="row.us" class="text-slate-900 dark:text-white">
                  <td class="py-3 font-semibold">{{ row.us }}</td>
                  <td class="py-3">{{ row.eu }}</td>
                  <td class="py-3">{{ row.cm }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </article>

        <article class="retail-card overflow-hidden">
          <div class="border-b border-slate-200/70 px-6 py-5 dark:border-[#3c342e]">
            <h2 class="text-2xl font-semibold text-slate-900 dark:text-white">Nữ (US)</h2>
          </div>
          <div class="overflow-x-auto px-6 py-5">
            <table class="w-full min-w-[320px] text-sm">
              <thead>
                <tr class="text-left text-slate-500 dark:text-[#cabdae]">
                  <th class="py-2">US</th>
                  <th class="py-2">EU</th>
                  <th class="py-2">CM</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-200 dark:divide-[#3c342e]">
                <tr v-for="row in womenRows" :key="row.us" class="text-slate-900 dark:text-white">
                  <td class="py-3 font-semibold">{{ row.us }}</td>
                  <td class="py-3">{{ row.eu }}</td>
                  <td class="py-3">{{ row.cm }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </article>
      </section>

      <section class="retail-card px-6 py-7 sm:px-8">
        <span class="retail-kicker">Mẹo nhỏ trước khi mua</span>
        <div class="mt-5 grid gap-5 md:grid-cols-3">
          <article v-for="tip in fittingNotes" :key="tip.title" class="rounded-[24px] bg-[#f6efe6] p-5 dark:bg-[#2b241f]">
            <h3 class="text-lg font-semibold text-slate-900 dark:text-white">{{ tip.title }}</h3>
            <p class="mt-2 text-sm leading-6 text-slate-600 dark:text-[#cabdae]">{{ tip.description }}</p>
          </article>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
const quickTips = [
  {
    icon: 'straighten',
    title: 'Đo chiều dài bàn chân',
    description: 'Đặt gót sát tường, đo từ gót đến đầu ngón dài nhất rồi đối chiếu theo cm.',
  },
  {
    icon: 'schedule',
    title: 'Thử vào cuối ngày',
    description: 'Cuối ngày chân nở nhẹ, giúp bạn chọn size sát cảm giác mang thực tế hơn.',
  },
  {
    icon: 'socks',
    title: 'Mang đúng loại tất',
    description: 'Nếu bạn thường đi tất thể thao dày, hãy đo và thử theo đúng loại tất đó.',
  },
]

const fittingNotes = [
  {
    title: 'Nếu nằm giữa hai size',
    description: 'Với giày chạy bộ hoặc upper ôm, bạn nên tăng nửa size hoặc 1 size để thoải mái hơn.',
  },
  {
    title: 'Mẫu mang hằng ngày',
    description: 'Với chất liệu mềm và dáng regular, bạn có thể chọn đúng size thường đi để giữ form gọn.',
  },
  {
    title: 'Khi còn phân vân',
    description: 'Bạn có thể xem thêm đánh giá tại trang chi tiết hoặc liên hệ hỗ trợ trước khi chốt đơn.',
  },
]

const menRows = [
  { us: 7, eu: 40, cm: 25 },
  { us: 8, eu: 41, cm: 26 },
  { us: 9, eu: 42, cm: 27 },
  { us: 10, eu: 43, cm: 28 },
  { us: 11, eu: 44, cm: 29 },
  { us: 12, eu: 45, cm: 30 },
]

const womenRows = [
  { us: 6, eu: 37, cm: 23 },
  { us: 7, eu: 38, cm: 24 },
  { us: 8, eu: 39, cm: 25 },
  { us: 9, eu: 40, cm: 26 },
  { us: 10, eu: 41, cm: 27 },
  { us: 11, eu: 42, cm: 28 },
]
</script>
