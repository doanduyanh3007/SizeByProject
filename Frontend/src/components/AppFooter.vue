<template>
  <footer class="footer-shell">
    <div class="footer-grid">
      <div>
        <p class="footer-kicker">SizeBy</p>
        <h3 class="footer-title">Giày thể thao chọn kỹ cho nhịp sống thật.</h3>
        <p class="footer-copy">
          Từ giày chạy bộ, giày tập gym đến mẫu mang hằng ngày, SizeBy tập trung
          vào form vừa chân, phối màu dễ mặc và trải nghiệm mua sắm rõ ràng.
        </p>
      </div>

      <div>
        <h4 class="footer-heading">Mua sắm</h4>
        <div class="footer-links">
          <a href="/shop">Cửa hàng</a>
          <a href="/#brands">Thương hiệu</a>
          <a href="/vouchers">Ưu đãi</a>
          <a href="/size-guide">Bảng size</a>
        </div>
      </div>

      <div>
        <h4 class="footer-heading">Chăm sóc khách hàng</h4>
        <div class="footer-links">
          <a href="/about">Về SizeBy</a>
          <a href="/profile">Tài khoản của tôi</a>
          <a href="/cart">Giỏ hàng</a>
          <a href="mailto:support@sizeby.vn">support@sizeby.vn</a>
        </div>
      </div>

      <div>
        <h4 class="footer-heading">Kết nối</h4>
        <div class="social-links">
          <a
            v-for="item in items"
            :key="item.title"
            class="social-link"
            :href="item.href"
            rel="noopener noreferrer"
            target="_blank"
            :title="item.title"
          >
            <span class="material-symbols-outlined text-[18px]">{{ item.symbol }}</span>
          </a>
        </div>
      </div>
    </div>

    <div class="footer-meta">
      <span>&copy; {{ new Date().getFullYear() }} SizeBy. Mọi quyền được bảo lưu.</span>
      <span>Giao diện tối giản, tập trung vào trải nghiệm mua sắm.</span>
    </div>
  </footer>
</template>

<script setup>
  const items = [
    {
      title: 'Instagram',
      symbol: 'photo_camera',
      href: 'https://instagram.com',
    },
    {
      title: 'Facebook',
      symbol: 'groups',
      href: 'https://facebook.com',
    },
    {
      title: 'TikTok',
      symbol: 'music_note',
      href: 'https://tiktok.com',
    },
    {
      title: 'GitHub',
      symbol: 'code',
      href: 'https://github.com',
    },
  ]
</script>

<style scoped lang="sass">
  .footer-shell
    background: #ffffff
    border-top: 1px solid #e5e7eb
    padding: 32px 24px 18px

  .footer-grid
    max-width: 1200px
    margin: 0 auto
    display: grid
    grid-template-columns: 1.5fr repeat(3, 1fr)
    gap: 28px

  .footer-kicker
    color: #6b7280
    font-size: 11px
    font-weight: 700
    letter-spacing: .22em
    text-transform: uppercase
    margin-bottom: 12px

  .footer-title
    color: #111827
    font-size: 32px
    line-height: 1
    font-weight: 800
    margin-bottom: 12px

  .footer-copy
    color: #6b7280
    font-size: 14px
    line-height: 1.7

  .footer-heading
    color: #111827
    font-size: 13px
    font-weight: 700
    letter-spacing: .12em
    text-transform: uppercase
    margin-bottom: 14px

  .footer-links
    display: grid
    gap: 10px

    a
      color: #6b7280
      text-decoration: none
      transition: color .2s ease

      &:hover
        color: #374151

  .social-links
    display: flex
    gap: 10px
    flex-wrap: wrap

  .social-link
    width: 38px
    height: 38px
    display: inline-flex
    align-items: center
    justify-content: center
    border-radius: 999px
    border: 1px solid #e5e7eb
    background: #ffffff
    transition: background .2s ease, transform .2s ease, border-color .2s ease

    &:hover
      background: #f9fafb
      border-color: #d1d5db
      transform: translateY(-1px)

    :deep(.v-icon)
      color: #374151

  .footer-meta
    max-width: 1200px
    margin: 24px auto 0
    padding-top: 16px
    border-top: 1px solid #e5e7eb
    display: flex
    justify-content: space-between
    gap: 12px
    color: #9ca3af
    font-size: 12px

  @media (max-width: 960px)
    .footer-grid
      grid-template-columns: 1fr 1fr

    .footer-title
      font-size: 28px

    .footer-meta
      flex-direction: column

  @media (max-width: 640px)
    .footer-grid
      grid-template-columns: 1fr
</style>
