<template>
  <div class="bg-background-light dark:bg-background-dark text-black dark:text-white font-display antialiased selection:bg-primary">
    <div class="relative flex h-auto min-h-screen w-full flex-col overflow-x-hidden">

      <MainTopBar />

      <main class="flex flex-1 justify-center py-5 px-4 md:px-8 lg:px-12 xl:px-40">
        <div class="flex flex-col max-w-[1280px] flex-1 gap-8">
          
          <div class="flex flex-col">
            <!-- Breadcrumbs -->
            <div class="flex flex-wrap gap-2 px-4 py-2">
              <router-link class="text-slate-500 dark:text-[#b9aa9a] text-sm font-medium leading-normal hover:text-primary transition-colors" to="/">Trang chủ</router-link>
              <span class="text-slate-500 dark:text-[#b9aa9a] text-sm font-medium leading-normal">/</span>
              <router-link class="text-slate-500 dark:text-[#b9aa9a] text-sm font-medium leading-normal hover:text-primary transition-colors" to="/shop">Cửa hàng</router-link>
              <span class="text-slate-500 dark:text-[#b9aa9a] text-sm font-medium leading-normal">/</span>
              <span class="text-slate-900 dark:text-white text-sm font-bold leading-normal">So sánh sản phẩm</span>
            </div>

            <!-- Header -->
            <div class="flex flex-col gap-4 text-left md:flex-row md:justify-between md:items-end px-4 py-6">
              <div class="flex flex-col gap-2">
                <h1 class="text-slate-900 dark:text-white text-4xl md:text-5xl font-black leading-tight tracking-[-0.033em]">So sánh sản phẩm</h1>
                <p class="text-slate-500 dark:text-[#b9aa9a] text-base md:text-lg font-normal leading-normal max-w-2xl">Đặt các mẫu cạnh nhau để xem nhanh mô tả, thương hiệu, khoảng giá và các thông tin cơ bản trước khi quyết định.</p>
              </div>
              <router-link class="text-primary hover:text-primary/80 font-medium text-sm flex items-center gap-1 self-start md:self-end" to="/shop">
                <span class="material-symbols-outlined text-lg">arrow_back</span>
                Quay lại cửa hàng
              </router-link>
            </div>
          </div>

          <div class="w-full overflow-hidden rounded-xl border border-slate-200 bg-white shadow-xl @container">
            <div class="overflow-x-auto">
              <table class="w-full min-w-[800px] border-collapse">
                <thead>
                  <tr class="bg-slate-50">
                    <th class="p-6 text-left w-1/4 min-w-[200px] border-b border-slate-200">
                      <span class="text-slate-600 text-sm font-bold uppercase tracking-wider">Tiêu chí</span>
                    </th>
                    <th
                      v-for="(p, idx) in comparedProducts"
                      :key="p.id"
                      class="p-6 text-left w-1/4 min-w-[240px] border-b border-slate-200 border-l border-l-slate-200"
                      :class="idx === 1 ? 'bg-slate-100' : ''"
                    >
                      <div class="flex flex-col gap-4">
                        <div class="aspect-[4/3] w-full overflow-hidden rounded-lg bg-slate-100 relative group">
                          <div class="absolute inset-0 bg-gradient-to-tr from-primary/10 to-transparent" />
                          <img :alt="p.name" class="h-full w-full object-cover object-center transition-transform duration-500 group-hover:scale-110" :src="p.imageUrl || 'https://via.placeholder.com/400x300'">
                        </div>
                        <div class="flex items-start justify-between gap-3">
                          <div>
                            <h3 class="text-slate-900 text-lg font-bold">{{ p.name }}</h3>
                            <div class="flex items-center gap-1 mt-1">
                              <span class="material-symbols-outlined text-yellow-500 text-sm">star</span>
                              <span class="text-slate-900 text-sm font-medium">4.8</span>
                              <span class="text-slate-500 text-xs">(Chưa có đánh giá)</span>
                            </div>
                          </div>
                          <button
                            class="text-slate-400 hover:text-slate-700 transition-colors"
                            title="Bỏ khỏi bảng so sánh"
                            type="button"
                            @click="compareStore.remove(p.id)"
                          >
                            <span class="material-symbols-outlined">close</span>
                          </button>
                        </div>
                      </div>
                    </th>
                    <th v-if="comparedProducts.length < 4" class="p-6 text-left w-1/4 min-w-[240px] border-b border-slate-200 border-l border-l-slate-200 align-middle">
                      <div class="flex flex-col items-center justify-center gap-4 h-full min-h-[200px] border-2 border-dashed border-slate-300 rounded-xl cursor-pointer hover:border-primary hover:bg-slate-50 transition-colors" @click="showAddProductModal = true">
                        <div class="bg-primary/10 text-primary p-3 rounded-full">
                          <span class="material-symbols-outlined text-3xl">add</span>
                        </div>
                        <span class="text-slate-600 font-medium">Thêm sản phẩm</span>
                      </div>
                    </th>
                  </tr>
                </thead>
                <tbody class="text-sm">
                  <tr class="hover:bg-slate-50 transition-colors">
                    <td class="p-4 px-6 text-slate-600 font-medium border-b border-slate-200 bg-slate-50">Danh mục</td>
                    <td
                      v-for="(p, idx) in comparedProducts"
                      :key="`cat-${p.id}`"
                      class="p-4 px-6 text-slate-900 font-medium border-b border-slate-200 border-l border-slate-200"
                      :class="idx === 1 ? 'bg-slate-50' : ''"
                    >
                      {{ p.category?.name || '--' }}
                    </td>
                    <td v-if="comparedProducts.length < 4" class="p-4 px-6 border-b border-slate-200 border-l border-slate-200"></td>
                  </tr>
                  <tr class="hover:bg-slate-50 transition-colors">
                    <td class="p-4 px-6 text-slate-600 font-medium border-b border-slate-200 bg-slate-50">Thương hiệu</td>
                    <td
                      v-for="(p, idx) in comparedProducts"
                      :key="`brand-${p.id}`"
                      class="p-4 px-6 text-slate-900 border-b border-slate-200 border-l border-slate-200"
                      :class="idx === 1 ? 'bg-slate-50' : ''"
                    >
                      {{ p.brand?.name || '--' }}
                    </td>
                    <td v-if="comparedProducts.length < 4" class="p-4 px-6 border-b border-slate-200 border-l border-slate-200"></td>
                  </tr>
                  <tr class="hover:bg-slate-50 transition-colors">
                    <td class="p-4 px-6 text-slate-600 font-medium border-b border-slate-200 bg-slate-50">Giá</td>
                    <td
                      v-for="(p, idx) in comparedProducts"
                      :key="`price-${p.id}`"
                      class="p-4 px-6 text-slate-900 text-base font-bold border-b border-slate-200 border-l border-slate-200"
                      :class="idx === 1 ? 'bg-slate-50' : ''"
                    >
                      {{ getDisplayPriceLabel(p) }}
                    </td>
                    <td v-if="comparedProducts.length < 4" class="p-4 px-6 border-b border-slate-200 border-l border-slate-200"></td>
                  </tr>
                  <tr class="hover:bg-slate-50 transition-colors">
                    <td class="p-4 px-6 text-slate-600 font-medium border-b border-slate-200 bg-slate-50">Màu sắc</td>
                    <td
                      v-for="(p, idx) in comparedProducts"
                      :key="`color-${p.id}`"
                      class="p-4 px-6 border-b border-slate-200 border-l border-slate-200"
                      :class="idx === 1 ? 'bg-slate-50' : ''"
                    >
                      <div v-if="getProductColors(p).length > 0" class="flex flex-wrap gap-2">
                        <div
                          v-for="color in getProductColors(p)"
                          :key="`color-token-${p.id}-${color.id ?? color.name}`"
                          class="inline-flex items-center gap-2 rounded-full border border-slate-200 bg-white px-2 py-1"
                          :title="color.name"
                        >
                          <span
                            class="size-4 rounded-full border border-slate-200"
                            :class="!getColorSwatchStyle(color) ? 'bg-slate-300' : ''"
                            :style="getColorSwatchStyle(color) || undefined"
                          />
                          <span class="text-slate-700 text-xs">{{ color.name }}</span>
                        </div>
                      </div>
                      <span v-else class="text-slate-500">--</span>
                    </td>
                    <td v-if="comparedProducts.length < 4" class="p-4 px-6 border-b border-slate-200 border-l border-slate-200"></td>
                  </tr>
                  <tr class="hover:bg-slate-50 transition-colors">
                    <td class="p-4 px-6 text-slate-600 font-medium border-b border-slate-200 bg-slate-50">Size có sẵn</td>
                    <td
                      v-for="(p, idx) in comparedProducts"
                      :key="`size-${p.id}`"
                      class="p-4 px-6 text-slate-900 border-b border-slate-200 border-l border-slate-200"
                      :class="idx === 1 ? 'bg-slate-50' : ''"
                    >
                      <div v-if="getProductSizes(p).length > 0" class="flex flex-wrap gap-2">
                        <span
                          v-for="sizeName in getProductSizes(p)"
                          :key="`size-token-${p.id}-${sizeName}`"
                          class="inline-flex items-center rounded-md border border-slate-200 bg-white px-2 py-1 text-xs text-slate-700"
                        >
                          {{ sizeName }}
                        </span>
                      </div>
                      <span v-else class="text-slate-500">--</span>
                    </td>
                    <td v-if="comparedProducts.length < 4" class="p-4 px-6 border-b border-slate-200 border-l border-slate-200"></td>
                  </tr>
                  <tr class="hover:bg-slate-50 transition-colors">
                    <td class="p-4 px-6 text-slate-600 font-medium border-b border-slate-200 bg-slate-50">Mô tả</td>
                    <td
                      v-for="(p, idx) in comparedProducts"
                      :key="`description-${p.id}`"
                      class="p-4 px-6 text-slate-700 border-b border-slate-200 border-l border-slate-200 leading-relaxed"
                      :class="idx === 1 ? 'bg-slate-50' : ''"
                    >
                      {{ getProductDescription(p) }}
                    </td>
                    <td v-if="comparedProducts.length < 4" class="p-4 px-6 border-b border-slate-200 border-l border-slate-200"></td>
                  </tr>
                  <tr>
                    <td class="p-4 px-6 bg-slate-50 border-t border-slate-200" />
                    <td
                      v-for="(p, idx) in comparedProducts"
                      :key="`cta-${p.id}`"
                      class="p-6 border-t border-slate-200 border-l border-slate-200"
                      :class="idx === 1 ? 'bg-slate-50' : ''"
                    >
                      <router-link
                        class="w-full flex cursor-pointer items-center justify-center rounded-lg h-12 px-6 bg-primary hover:bg-primary/90 text-white text-sm font-bold tracking-[0.015em] transition-all transform active:scale-95 shadow-lg shadow-primary/20"
                        :to="`/product/${p.id}`"
                      >
                        Xem chi tiết
                      </router-link>
                    </td>
                    <td v-if="comparedProducts.length < 4" class="p-6 border-t border-slate-200 border-l border-slate-200"></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mt-4">
            <div class="bg-surface-dark rounded-xl p-6 border border-border-dark flex items-start gap-4">
              <div class="bg-surface-dark-highlight p-3 rounded-lg text-primary">
                <span class="material-symbols-outlined">local_shipping</span>
              </div>
              <div>
                <h4 class="text-slate-900 font-bold mb-1">Miễn phí vận chuyển</h4>
                <p class="text-text-secondary text-sm">Áp dụng với đơn đủ điều kiện. Hỗ trợ giao hàng nhiều khu vực.</p>
              </div>
            </div>
            <div class="bg-surface-dark rounded-xl p-6 border border-border-dark flex items-start gap-4">
              <div class="bg-surface-dark-highlight p-3 rounded-lg text-primary">
                <span class="material-symbols-outlined">verified_user</span>
              </div>
              <div>
                <h4 class="text-slate-900 font-bold mb-1">Bảo hành 2 năm</h4>
                <p class="text-text-secondary text-sm">Cam kết chất lượng rõ ràng để bạn yên tâm sử dụng.</p>
              </div>
            </div>
            <div class="bg-surface-dark rounded-xl p-6 border border-border-dark flex items-start gap-4">
              <div class="bg-surface-dark-highlight p-3 rounded-lg text-primary">
                <span class="material-symbols-outlined">sync_alt</span>
              </div>
              <div>
                <h4 class="text-slate-900 font-bold mb-1">Đổi trả dễ dàng</h4>
                <p class="text-text-secondary text-sm">Hỗ trợ đổi trả trong 30 ngày nếu sản phẩm còn nguyên trạng.</p>
              </div>
            </div>
          </div>
        </div>
      </main>

      <!-- Add Product Modal -->
      <div v-if="showAddProductModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4" @click.self="showAddProductModal = false">
        <div class="w-full max-w-lg rounded-2xl bg-white shadow-2xl flex flex-col max-h-[80vh]">
          <div class="flex items-center justify-between border-b border-slate-200 p-4">
            <h3 class="text-lg font-bold text-slate-900">Thêm sản phẩm so sánh</h3>
            <button class="text-slate-400 hover:text-slate-700 transition-colors" @click="showAddProductModal = false">
              <span class="material-symbols-outlined">close</span>
            </button>
          </div>
          <div class="p-4 border-b border-slate-100">
            <div class="relative">
              <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400">search</span>
              <input v-model="searchQuery" type="text" placeholder="Tìm kiếm sản phẩm..." class="w-full rounded-xl border border-slate-900 pl-10 pr-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20" />
            </div>
          </div>
          <div class="overflow-y-auto p-2 flex-1">
            <div v-if="availableProducts.length === 0" class="p-8 text-center text-slate-500">
              Không tìm thấy sản phẩm nào.
            </div>
            <div
              v-for="p in availableProducts"
              :key="p.id"
              class="flex items-center gap-4 p-3 hover:bg-slate-50 rounded-xl cursor-pointer transition-colors"
              @click="addProductToCompare(p)"
            >
              <img :src="p.imageUrl || 'https://via.placeholder.com/100'" :alt="p.name" class="w-16 h-16 object-cover rounded-lg bg-slate-100" />
              <div class="flex-1">
                <h4 class="font-bold text-slate-900 line-clamp-1">{{ p.name }}</h4>
                <p class="text-sm text-slate-500">{{ p.brand?.name || 'Không rõ' }}</p>
              </div>
              <button class="text-primary hover:bg-primary/10 p-2 rounded-full transition-colors flex items-center justify-center">
                <span class="material-symbols-outlined">add</span>
              </button>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
  import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { useCompareStore } from '@/stores/compare'
  import { useProductStore } from '@/stores/products'
  import {
    normalizeHexColor,
    resolveColorFromVariant,
    resolveSizeFromVariant,
    toVariantId,
  } from '@/utils/variantValues'

  const compareStore = useCompareStore()
  const productStore = useProductStore()
  const router = useRouter()
  const route = useRoute()
  const isLoggedIn = ref(false)
  const showUserMenu = ref(false)
  const showAddProductModal = ref(false)

  const comparedProducts = computed(() => {
    return compareStore.compareIds
      .map(id => productStore.getProductById(id))
      .filter(Boolean)
  })

  const searchQuery = ref('')
  
  const availableProducts = computed(() => {
    return productStore.products.filter(p => !compareStore.compareIds.includes(p.id) && p.name.toLowerCase().includes(searchQuery.value.toLowerCase()))
  })

  function addProductToCompare(product) {
    if (compareStore.compareIds.length < 4) {
      compareStore.add(product.id)
    }
    showAddProductModal.value = false
    searchQuery.value = ''
  }

  function getProductVariants (productId) {
    const id = toVariantId(productId)
    if (id === null) return []
    return productStore.variants.filter(v => toVariantId(v.productId) === id)
  }

  function getDisplayPrice (product) {
    const prices = getProductVariants(product?.id)
      .map(v => Number(v?.price))
      .filter(Number.isFinite)

    if (prices.length > 0) {
      return Math.min(...prices)
    }

    const fallback = Number(product?.price)
    return Number.isFinite(fallback) ? fallback : null
  }

  function getDisplayPriceLabel (product) {
    const prices = getProductVariants(product?.id)
      .map(v => Number(v?.price))
      .filter(Number.isFinite)

    if (prices.length === 0) {
      return formatMoney(getDisplayPrice(product))
    }

    const min = Math.min(...prices)
    const max = Math.max(...prices)
    if (min === max) {
      return formatMoney(min)
    }
    return `${formatMoney(min)} - ${formatMoney(max)}`
  }

  function formatMoney (value) {
    const n = Number(value)
    if (!Number.isFinite(n)) return '--'
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(n)
  }

  function getProductColors (product) {
    const deduped = new Map()

    for (const variant of getProductVariants(product?.id)) {
      const colorMeta = resolveColorFromVariant(variant)
      if (!colorMeta.name) continue

      const key = colorMeta.id !== null ? `id-${colorMeta.id}` : `name-${colorMeta.name}`
      if (deduped.has(key)) continue

      deduped.set(key, {
        id: colorMeta.id,
        name: colorMeta.name,
        hexCode: colorMeta.hexCode,
      })
    }

    return [...deduped.values()]
  }

  function getColorSwatchStyle (color) {
    const hex = normalizeHexColor(color?.hexCode)
    return hex ? { backgroundColor: hex } : null
  }

  function getProductSizes (product) {
    const deduped = new Map()

    for (const variant of getProductVariants(product?.id)) {
      const sizeMeta = resolveSizeFromVariant(variant)
      if (!sizeMeta.name) continue

      const key = sizeMeta.id !== null ? `id-${sizeMeta.id}` : `name-${sizeMeta.name}`
      if (deduped.has(key)) continue
      deduped.set(key, sizeMeta.name)
    }

    return [...deduped.values()]
  }

  function getProductDescription (product) {
    const raw = product?.description
    if (!raw) return '--'
    const text = String(raw).trim()
    return text || '--'
  }

  function checkLoginStatus () {
    const user = localStorage.getItem('user')
    const token = localStorage.getItem('token')
    isLoggedIn.value = Boolean(user || token)
    if (!isLoggedIn.value) showUserMenu.value = false
  }

  function toggleUserMenu (event) {
    event.stopPropagation()
    showUserMenu.value = !showUserMenu.value
  }

  function handleLogout () {
    localStorage.removeItem('user')
    localStorage.removeItem('token')
    isLoggedIn.value = false
    showUserMenu.value = false
    router.push('/')
  }

  function handleClickOutside (event) {
    const target = event.target
    if (!(target instanceof Element) || !target.closest('.account-menu')) {
      showUserMenu.value = false
    }
  }

  onMounted(async () => {
    checkLoginStatus()
    document.addEventListener('click', handleClickOutside)
    window.addEventListener('storage', checkLoginStatus)

    if (productStore.products.length === 0) await productStore.fetchProducts()
    if (productStore.variants.length === 0) await productStore.fetchVariants()
    if (productStore.brands.length === 0) await productStore.fetchBrands()
    if (productStore.categories.length === 0) await productStore.fetchCategories()
  })

  onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside)
    window.removeEventListener('storage', checkLoginStatus)
  })

  watch(() => route.fullPath, () => {
    checkLoginStatus()
    showUserMenu.value = false
  })
</script>

<style scoped>
/* Custom scrollbar for dark theme */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}
::-webkit-scrollbar-track {
  background: #111418;
}
::-webkit-scrollbar-thumb {
  background: #3b4754;
  border-radius: 4px;
}
::-webkit-scrollbar-thumb:hover {
  background: #4b5866;
}
</style>


