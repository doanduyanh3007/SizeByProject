<template>
  <div class="retail-shell flex min-h-screen flex-col overflow-x-hidden">
    <MainTopBar />

    <main class="mx-auto flex w-full max-w-[1240px] flex-1 flex-col gap-16 px-4 py-10 md:px-8 xl:px-10">
      <section class="grid gap-8 lg:grid-cols-[0.95fr_1.05fr] lg:items-center">
        <div class="retail-card overflow-hidden p-0">
          <img
            src="https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=1400&q=80"
            alt="Không gian trưng bày giày SizeBy"
            class="aspect-[4/5] h-full w-full object-cover lg:aspect-auto lg:min-h-[580px]"
          />
        </div>

        <div>
          <span class="retail-kicker">Đồng hành cùng từng bước chân</span>
          <h1 class="retail-heading mt-5 text-[56px] leading-[0.92] text-slate-900 dark:text-white sm:text-[72px]">
            SizeBy chọn giày để bạn mang thoải mái mỗi ngày.
          </h1>
          <p class="mt-6 max-w-2xl text-base leading-8 text-slate-600 dark:text-[#cabdae] sm:text-lg">
            Chúng tôi xây danh mục dựa trên nhu cầu thật: đi làm, đi chơi, tập luyện và sử dụng hằng ngày.
            Mỗi sản phẩm đều có thông tin rõ ràng về form, chất liệu và cảm giác mang để bạn chọn nhanh hơn.
          </p>

          <div class="mt-8 rounded-[24px] border border-slate-200/80 bg-white/70 px-5 py-5 dark:border-[#3c342e] dark:bg-[#241d19]/80">
            <p class="text-[11px] font-semibold uppercase tracking-[0.22em] text-primary">Thông tin người mua cần trước</p>
            <ul class="mt-4 grid gap-3">
              <li v-for="item in userFocusNotes" :key="item" class="flex items-start gap-3 text-sm leading-6 text-slate-700 dark:text-[#d8cabb]">
                <span class="material-symbols-outlined mt-0.5 text-[18px] text-primary">check_circle</span>
                <span>{{ item }}</span>
              </li>
            </ul>
          </div>
        </div>
      </section>

      <section class="grid gap-6 lg:grid-cols-[0.9fr_1.1fr]">
        <article class="retail-card px-6 py-7 sm:px-8">
          <span class="retail-kicker">Câu chuyện hình thành</span>
          <h2 class="retail-heading mt-4 text-[42px] leading-none text-slate-900 dark:text-white">Bắt đầu từ nhu cầu mua thật.</h2>
          <div class="mt-5 space-y-4 text-sm leading-7 text-slate-600 dark:text-[#cabdae] sm:text-base">
            <p>
              SizeBy bắt đầu từ một cửa hàng nhỏ, nơi khách thường hỏi cùng một điều: đôi nào êm,
              dễ phối đồ và đi lâu không mỏi chân.
            </p>
            <p>
              Từ đó, chúng tôi tập trung chọn mẫu theo trải nghiệm sử dụng thực tế thay vì chạy theo xu hướng ngắn hạn.
            </p>
          </div>
        </article>

        <article class="retail-card px-6 py-7 sm:px-8">
          <span class="retail-kicker">Cách SizeBy chọn hàng</span>
          <div class="mt-5 grid gap-5 md:grid-cols-2">
            <div v-for="item in operatingPrinciples" :key="item.title" class="rounded-[24px] border border-slate-200/70 bg-white/70 p-5 dark:border-[#3c342e] dark:bg-[#241d19]/80">
              <span class="material-symbols-outlined text-[28px] text-primary">{{ item.icon }}</span>
              <h3 class="mt-4 text-lg font-semibold text-slate-900 dark:text-white">{{ item.title }}</h3>
              <p class="mt-2 text-sm leading-6 text-slate-600 dark:text-[#cabdae]">{{ item.description }}</p>
            </div>
          </div>
        </article>
      </section>

      <section class="retail-card px-6 py-7 sm:px-8">
        <div class="mb-8 flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
          <div>
            <span class="retail-kicker">Điểm khác biệt</span>
            <h2 class="retail-heading mt-4 text-[42px] leading-none text-slate-900 dark:text-white">Những gì bạn cảm nhận rõ ngay.</h2>
          </div>
          <p class="max-w-xl text-sm leading-6 text-slate-600 dark:text-[#cabdae]">
            Điểm khác biệt nằm ở cách chọn mẫu, kiểm tra chất lượng và hỗ trợ sau mua để khách yên tâm khi đặt hàng online.
          </p>
        </div>

        <div class="grid gap-5 md:grid-cols-2 xl:grid-cols-4">
          <article v-for="item in stats" :key="item.label" class="rounded-[24px] bg-[#f6efe6] px-5 py-6 text-center dark:bg-[#2b241f]">
            <p class="font-headline text-[48px] leading-none text-primary">{{ item.value }}</p>
            <p class="mt-3 text-sm font-semibold uppercase tracking-[0.18em] text-slate-900 dark:text-white">{{ item.label }}</p>
            <p class="mt-2 text-sm leading-6 text-slate-600 dark:text-[#cabdae]">{{ item.description }}</p>
          </article>
        </div>
      </section>

      <section class="grid gap-6 lg:grid-cols-[1.05fr_0.95fr]">
        <article class="retail-card px-6 py-7 sm:px-8">
          <span class="retail-kicker">Trải nghiệm khách hàng</span>
          <h2 class="retail-heading mt-4 text-[42px] leading-none text-slate-900 dark:text-white">Từ lúc xem mẫu đến khi nhận hàng.</h2>
          <div class="mt-6 space-y-5">
            <div v-for="(step, index) in customerJourney" :key="step.title" class="flex gap-4">
              <div class="flex size-11 shrink-0 items-center justify-center rounded-full bg-primary text-sm font-semibold text-white">0{{ index + 1 }}</div>
              <div>
                <h3 class="text-lg font-semibold text-slate-900 dark:text-white">{{ step.title }}</h3>
                <p class="mt-1 text-sm leading-6 text-slate-600 dark:text-[#cabdae]">{{ step.description }}</p>
              </div>
            </div>
          </div>
        </article>

        <article class="retail-card overflow-hidden p-0">
          <div class="flex h-full flex-col justify-between bg-[#1f1712] p-8 text-white">
            <div>
              <p class="text-[11px] uppercase tracking-[0.24em] text-[#f7c2a4]">Cam kết dịch vụ</p>
              <h2 class="font-headline mt-4 text-[44px] uppercase leading-none">Mua online vẫn yên tâm.</h2>
              <p class="mt-5 max-w-md text-sm leading-7 text-[#f0dfd2] sm:text-base">
                Chúng tôi hỗ trợ chọn size, xác nhận đơn nhanh và hướng dẫn đổi size minh bạch,
                để bạn nhận được đôi giày đúng kỳ vọng ngay từ lần mua đầu tiên.
              </p>
            </div>

            <div class="mt-8 rounded-[28px] border border-white/10 bg-white/5 p-5">
              <p class="text-[11px] uppercase tracking-[0.22em] text-[#f7c2a4]">Bước tiếp theo</p>
              <h3 class="mt-2 text-2xl font-semibold">Xem cửa hàng hoặc thương hiệu</h3>
              <div class="mt-5 flex flex-wrap gap-3">
                <router-link to="/shop" class="inline-flex items-center rounded-full bg-primary px-5 py-3 text-sm font-semibold text-white transition-transform hover:-translate-y-0.5">Khám phá sản phẩm</router-link>
                <router-link to="/brands" class="inline-flex items-center rounded-full border border-white/20 px-5 py-3 text-sm font-semibold text-white transition-colors hover:border-[#f7c2a4] hover:text-[#f7c2a4]">Xem thương hiệu</router-link>
              </div>
            </div>
          </div>
        </article>
      </section>
    </main>
  </div>
</template>

<script setup>
const userFocusNotes = [
  'Mỗi mẫu đều được kiểm tra độ êm, độ bền và độ ôm chân trước khi lên cửa hàng.',
  'Thông tin size, chất liệu, ưu đãi và giá bán hiển thị rõ ngay tại nơi cần xem.',
  'Đội ngũ hỗ trợ chọn giày theo mục đích: đi hằng ngày, chạy bộ, tập luyện hoặc đi làm.',
];

const operatingPrinciples = [
  {
    icon: 'styler',
    title: 'Ưu tiên form giày dễ chọn',
    description: 'Mô tả tập trung vào phom, độ ôm chân, cảm giác đế và tình huống sử dụng thực tế.',
  },
  {
    icon: 'palette',
    title: 'Phối màu hợp thị hiếu số đông',
    description: 'Tông màu dễ phối đồ, ít rủi ro và phù hợp với người mua lần đầu.',
  },
  {
    icon: 'inventory_2',
    title: 'Trưng bày theo nhóm nhu cầu',
    description: 'Bạn không cần biết mã sản phẩm trước, chỉ cần chọn theo mục đích sử dụng.',
  },
  {
    icon: 'verified_user',
    title: 'Thông tin bán hàng minh bạch',
    description: 'Giá, voucher, đổi trả, giao hàng và bảng size luôn đặt đúng nơi bạn cần.',
  },
];

const stats = [
  {
    value: '48h',
    label: 'Xử lý đơn nhanh',
    description: 'Phần lớn đơn hàng được xác nhận và chuẩn bị giao trong 48 giờ làm việc.',
  },
  {
    value: '7 ngày',
    label: 'Đổi size linh hoạt',
    description: 'Hỗ trợ đổi size theo chính sách rõ ràng để bạn yên tâm khi mua online.',
  },
  {
    value: '4.8/5',
    label: 'Đánh giá trung bình',
    description: 'Mức hài lòng thực tế từ khách hàng đã mua và sử dụng sản phẩm.',
  },
  {
    value: '12+',
    label: 'Thương hiệu phổ biến',
    description: 'Nhiều lựa chọn từ các thương hiệu dễ mang, phù hợp nhu cầu hằng ngày.',
  },
];

const customerJourney = [
  {
    title: 'Xem nhanh từ trang chủ',
    description: 'Bạn đi thẳng vào danh mục, thương hiệu hoặc khuyến mãi chỉ với một lần chạm.',
  },
  {
    title: 'Lọc từ trang cửa hàng',
    description: 'Bộ lọc size, thương hiệu và khoảng giá giúp thu gọn lựa chọn theo nhu cầu thật.',
  },
  {
    title: 'Quyết định ở trang chi tiết',
    description: 'Thông tin size, chất liệu, đánh giá và giao nhận hiển thị rõ tại nơi quyết định mua.',
  },
  {
    title: 'Theo dõi sau mua',
    description: 'Bạn theo dõi đơn hàng, lưu địa chỉ và quản lý ưu đãi trong cùng một tài khoản.',
  },
];
</script>


