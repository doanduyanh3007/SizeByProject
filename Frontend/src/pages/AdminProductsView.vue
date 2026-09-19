<template>
  <!-- Toast notification -->
  <Teleport to="body">
    <Transition name="slide-fade">
      <div v-if="successToast" class="fixed top-6 right-6 z-[9999] flex items-center gap-2 rounded-xl bg-emerald-600 px-5 py-3 text-sm font-semibold text-white shadow-lg">
        <span class="material-symbols-outlined text-[20px]">check_circle</span>
        {{ successToast }}
      </div>
    </Transition>
  </Teleport>
  <AdminShell>
    <template #header>
      <div class="flex flex-wrap items-center justify-between gap-3">
        <div>
          <p class="text-xs uppercase tracking-[0.18em] text-primary">
            Quản lý danh mục & biến thể
          </p>
          <h2 class="mt-1 text-2xl font-bold">Quản lý sản phẩm & tồn kho</h2>
        </div>

        <div class="flex flex-wrap items-center gap-2">
          <button
            type="button"
            class="inline-flex items-center gap-1.5 rounded-xl bg-primary px-3 py-2 text-sm font-semibold text-white hover:opacity-90 shadow-sm"
            @click="openCreate"
          >
            <span class="material-symbols-outlined text-[18px]">add</span>
            Thêm sản phẩm
          </button>

          <button
            type="button"
            class="inline-flex items-center gap-1.5 rounded-xl border border-primary/40 bg-primary/10 px-3 py-2 text-sm font-semibold text-primary hover:bg-primary/20 dark:border-primary/40 dark:bg-primary/20"
            @click="openCreateVariant(null)"
          >
            <span class="material-symbols-outlined text-[18px]">add_box</span>
            Thêm biến thể
          </button>

          <button
            type="button"
            class="inline-flex items-center gap-1.5 rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm font-medium hover:border-primary hover:text-primary dark:border-[#3c342e] dark:bg-[#1f1a17]"
            @click="loadData"
          >
            <span class="material-symbols-outlined text-[18px]">refresh</span>
            Làm mới
          </button>
        </div>
      </div>
    </template>

    <div class="space-y-4">
      <!-- Search & Filters -->
      <section class="retail-card p-4">
        <div class="grid gap-3 md:grid-cols-[1fr_200px_200px_160px]">
          <label
            class="flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2 dark:border-[#3c342e] dark:bg-[#1f1a17]"
          >
            <span class="material-symbols-outlined text-[18px] text-slate-400"
              >search</span
            >
            <input
              v-model="search"
              type="text"
              placeholder="Tìm theo tên, mã SP, màu, size, ID..."
              class="w-full bg-transparent text-sm outline-none"
            />
          </label>

          <select
            v-model.number="filterBrandId"
            class="rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#1f1a17]"
          >
            <option :value="null">Tất cả thương hiệu</option>
            <option
              v-for="brand in brands"
              :key="brand.id"
              :value="Number(brand.id)"
            >
              {{ brand.name }}
            </option>
          </select>

          <select
            v-model.number="filterCategoryId"
            class="rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#1f1a17]"
          >
            <option :value="null">Tất cả danh mục</option>
            <option
              v-for="category in categories"
              :key="category.id"
              :value="Number(category.id)"
            >
              {{ category.name }}
            </option>
          </select>

          <select
            v-model="filterStockStatus"
            class="rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#1f1a17]"
          >
            <option value="ALL">Tất cả trạng thái</option>
            <option value="IN_STOCK">Còn hàng</option>
            <option value="OUT_OF_STOCK">Hết hàng (0)</option>
            <option value="LOW_STOCK">Sắp hết (&lt; 10)</option>
          </select>
        </div>
      </section>

        <div
          v-if="error"
          class="mb-4 rounded-xl border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700 dark:border-red-900/40 dark:bg-red-900/20 dark:text-red-300"
        >
          {{ error }}
        </div>

        <!-- Hidden Products Banner -->
        <div
          v-if="hiddenProducts.length > 0"
          class="mb-4 rounded-xl border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-slate-700 dark:border-amber-900/20 dark:bg-[#3f2e11] dark:text-[#fbe7a1]"
        >
          <div class="flex flex-wrap items-center justify-between gap-3">
            <div>
              <p class="font-semibold">Sản phẩm ẩn tạm ({{ hiddenProducts.length }})</p>
              <p class="text-xs text-slate-500 dark:text-[#e1c797]">
                Những sản phẩm đã xóa mềm sẽ tạm ẩn khỏi danh sách. Bạn có thể khôi phục lại khi cần.
              </p>
            </div>
            <button
              type="button"
              class="rounded-xl bg-primary px-3 py-1.5 text-xs font-semibold text-white hover:opacity-90"
              @click="requestRestoreAllProducts"
            >
              Khôi phục tất cả SP
            </button>
          </div>
          <div class="mt-3 space-y-2">
            <div
              v-for="product in hiddenProducts"
              :key="product.id"
              class="flex flex-wrap items-center justify-between gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2 dark:border-[#4c4138] dark:bg-[#1e1812]"
            >
              <div class="text-sm">
                <span class="font-semibold">#{{ product.id }}</span>
                <span class="ml-2">{{ product.name || "Sản phẩm" }}</span>
              </div>
              <button
                type="button"
                class="rounded-xl bg-emerald-500 px-3 py-1.5 text-xs font-semibold text-white hover:bg-emerald-600"
                @click="requestRestoreProduct(product)"
              >
                Khôi phục
              </button>
            </div>
          </div>
        </div>

        <!-- Hidden Variants Banner -->
        <div
          v-if="hiddenVariants.length > 0"
          class="mb-4 rounded-xl border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-slate-700 dark:border-amber-900/20 dark:bg-[#3f2e11] dark:text-[#fbe7a1]"
        >
          <div class="flex flex-wrap items-center justify-between gap-3">
            <div>
              <p class="font-semibold">Biến thể ẩn tạm ({{ hiddenVariants.length }})</p>
              <p class="text-xs text-slate-500 dark:text-[#e1c797]">
                Những biến thể đã xóa mềm sẽ tạm ẩn khỏi danh sách. Bạn có thể khôi phục lại khi cần.
              </p>
            </div>
            <button
              type="button"
              class="rounded-xl bg-primary px-3 py-1.5 text-xs font-semibold text-white hover:opacity-90"
              @click="requestRestoreAllVariants"
            >
              Khôi phục tất cả biến thể
            </button>
          </div>
          <div class="mt-3 space-y-2">
            <div
              v-for="variant in hiddenVariants"
              :key="variant.id"
              class="flex flex-wrap items-center justify-between gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2 dark:border-[#4c4138] dark:bg-[#1e1812]"
            >
              <div class="text-sm">
                <span class="font-semibold">#{{ variant.id }}</span>
                <span class="ml-2">{{ variant.productName }} ({{ variant.colorName }} / {{ variant.sizeName }})</span>
              </div>
              <button
                type="button"
                class="rounded-xl bg-emerald-500 px-3 py-1.5 text-xs font-semibold text-white hover:bg-emerald-600"
                @click="requestRestoreVariant(variant)"
              >
                Khôi phục
              </button>
            </div>
          </div>
        </div>

      <!-- Master - Detail Split Layout (Image 1) -->
      <div class="grid grid-cols-1 gap-4 xl:grid-cols-12">
        <!-- LEFT COLUMN: Quản lý Sản phẩm (7 cols) -->
        <section class="retail-card p-4 xl:col-span-7 flex flex-col min-w-0">
          <div class="mb-4 flex items-center justify-between">
            <h3 class="font-bold text-slate-800 dark:text-white">
              Danh sách sản phẩm
            </h3>
            <span class="rounded-full bg-slate-100 px-2.5 py-0.5 text-xs font-bold text-slate-600 dark:bg-[#2b241f] dark:text-slate-300">
              {{ filteredProducts.length }} mục
            </span>
          </div>

          <div v-if="loading" class="py-12 text-center text-sm text-slate-500 dark:text-[#b9aa9a]">
            Đang tải dữ liệu sản phẩm...
          </div>

          <div v-else-if="filteredProducts.length === 0" class="rounded-2xl border border-dashed border-slate-300 p-8 text-center text-sm text-slate-500 dark:border-[#3c342e] dark:text-[#b9aa9a]">
            Không tìm thấy sản phẩm nào phù hợp.
          </div>

          <div v-else class="overflow-x-auto flex-1">
            <table class="w-full text-left text-xs">
              <thead>
                <tr class="border-b border-slate-200 text-slate-500 dark:border-[#3c342e] dark:text-[#b9aa9a]">
                  <th class="w-10 px-2 py-2.5 font-medium">STT</th>
                  <th class="px-2 py-2.5 font-medium">Sản phẩm</th>
                  <th class="px-2 py-2.5 font-medium">Danh mục</th>
                  <th class="px-2 py-2.5 font-medium text-center">Tồn</th>
                  <th class="px-2 py-2.5 text-right font-medium">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="(product, index) in paginatedProducts"
                  :key="product.id"
                  class="cursor-pointer border-b border-slate-100 transition-all hover:bg-slate-50 dark:border-[#2b241f] dark:hover:bg-[#2b241f]/40"
                  :class="selectedProduct?.id === product.id ? 'bg-primary/10 border-l-4 border-l-primary dark:bg-primary/20' : ''"
                  @click="selectProduct(product)"
                >
                  <td class="px-2 py-3 font-semibold text-slate-500 dark:text-[#b9aa9a]">
                    {{ (productsPage - 1) * 10 + index + 1 }}
                  </td>

                  <td class="px-2 py-3">
                    <div class="flex items-center gap-2.5">
                      <div class="size-10 shrink-0 overflow-hidden rounded-lg bg-slate-100 dark:bg-[#2b241f]">
                        <img
                          v-if="product.imageUrl"
                          :src="product.imageUrl"
                          alt="Product"
                          class="h-full w-full object-cover"
                        />
                        <span v-else class="material-symbols-outlined flex h-full items-center justify-center text-slate-400 text-sm">inventory_2</span>
                      </div>
                      <div class="min-w-0">
                        <p class="font-bold text-slate-900 dark:text-white truncate max-w-[180px]" :title="product.name">
                          {{ product.name || "Sản phẩm" }}
                        </p>
                        <p class="text-[10px] text-slate-400 dark:text-[#9cabba]">
                          {{ product.productCode || 'N/A' }} • {{ brandName(product.brandId) }}
                        </p>
                      </div>
                    </div>
                  </td>

                  <td class="px-2 py-3 text-slate-600 dark:text-[#c4b5a5]">
                    {{ categoryName(product.categoryId) }}
                  </td>

                  <td class="px-2 py-3 text-center font-bold" :class="getProductTotalStock(product.id) > 0 ? 'text-slate-800 dark:text-slate-200' : 'text-red-500'">
                    {{ getProductTotalStock(product.id) }}
                  </td>

                  <td class="px-2 py-3 text-right" @click.stop>
                    <div class="flex items-center justify-end gap-1">
                      <button
                        type="button"
                        class="rounded p-1 hover:bg-slate-100 dark:hover:bg-[#2b241f]"
                        title="Chỉnh sửa sản phẩm"
                        @click="openEdit(product)"
                      >
                        <span class="material-symbols-outlined text-[16px] text-slate-500">edit</span>
                      </button>
                      <button
                        type="button"
                        class="rounded p-1 hover:bg-red-50 dark:hover:bg-red-900/20"
                        title="Xóa sản phẩm"
                        @click="requestDelete(product)"
                      >
                        <span class="material-symbols-outlined text-[16px] text-red-500">delete</span>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="mt-auto pt-3">
            <PaginationBar
              :current-page="productsPage"
              :total-pages="productsTotalPages"
              :page-start="productsPageStart"
              :page-end="productsPageEnd"
              :total-items="productsTotalItems"
              label="sản phẩm"
              @previous="productsPreviousPage"
              @next="productsNextPage"
            />
          </div>
        </section>

        <!-- RIGHT COLUMN: Quản lý Biến thể của SP đang chọn (5 cols) -->
        <section class="retail-card p-4 xl:col-span-5 flex flex-col min-w-0">
          <div v-if="!selectedProduct" class="flex flex-col items-center justify-center py-20 text-center text-slate-400">
            <span class="material-symbols-outlined text-4xl mb-2">touch_app</span>
            <p class="text-sm">Vui lòng chọn một sản phẩm ở bảng bên trái</p>
          </div>

          <div v-else class="space-y-4">
            <!-- Chips summary bar (Image 1 top right) -->
            <div class="grid grid-cols-2 gap-2 text-xs sm:grid-cols-4">
              <div class="rounded-xl border border-slate-200 bg-slate-50 p-2 text-center dark:border-[#3c342e] dark:bg-[#1f1a17]">
                <p class="text-[10px] text-slate-500 dark:text-[#b9aa9a]">Tổng biến thể</p>
                <p class="font-bold text-slate-900 dark:text-white mt-0.5">{{ selectedProductVariants.length }}</p>
              </div>
              <div class="rounded-xl border border-slate-200 bg-slate-50 p-2 text-center dark:border-[#3c342e] dark:bg-[#1f1a17]">
                <p class="text-[10px] text-emerald-600 dark:text-emerald-400">Còn hàng</p>
                <p class="font-bold text-emerald-600 dark:text-emerald-400 mt-0.5">{{ selectedProductInStockCount }}</p>
              </div>
              <div class="rounded-xl border border-slate-200 bg-slate-50 p-2 text-center dark:border-[#3c342e] dark:bg-[#1f1a17]">
                <p class="text-[10px] text-amber-600 dark:text-amber-400">Sắp hết</p>
                <p class="font-bold text-amber-600 dark:text-amber-400 mt-0.5">{{ selectedProductLowStockCount }}</p>
              </div>
              <div class="rounded-xl border border-slate-200 bg-slate-50 p-2 text-center dark:border-[#3c342e] dark:bg-[#1f1a17]">
                <p class="text-[10px] text-red-500">Hết hàng</p>
                <p class="font-bold text-red-500 mt-0.5">{{ selectedProductOutOfStockCount }}</p>
              </div>
            </div>

            <!-- Product Header Info Box (Image 1) -->
            <div class="rounded-xl border border-slate-200 bg-slate-50/70 p-3.5 dark:border-[#3c342e] dark:bg-[#181310] flex items-center justify-between gap-3">
              <div class="min-w-0">
                <h4 class="font-bold text-sm text-slate-900 dark:text-white truncate">
                  {{ selectedProduct.name }}
                </h4>
                <p class="text-xs text-slate-500 dark:text-[#b9aa9a] mt-0.5">
                  {{ selectedProductVariants.length }} biến thể • Tồn kho {{ getProductTotalStock(selectedProduct.id) }}
                </p>
              </div>
              <button
                type="button"
                class="inline-flex shrink-0 items-center gap-1 rounded-lg bg-primary px-3 py-1.5 text-xs font-semibold text-white hover:opacity-90 shadow-sm"
                @click="openCreateVariant(selectedProduct)"
              >
                <span class="material-symbols-outlined text-[16px]">add</span>
                Thêm biến thể
              </button>
            </div>

            <!-- Variant List / Table -->
            <div v-if="selectedProductVariants.length === 0" class="py-12 text-center text-xs text-slate-400 rounded-xl border border-dashed border-slate-200 dark:border-[#3c342e]">
              Sản phẩm này chưa có biến thể nào.<br>
              <button
                type="button"
                class="mt-3 inline-flex items-center gap-1 rounded-lg bg-primary px-3 py-1.5 text-xs font-semibold text-white hover:opacity-90"
                @click="openCreateVariant(selectedProduct)"
              >
                <span class="material-symbols-outlined text-[14px]">add</span>
                Tạo biến thể đầu tiên
              </button>
            </div>

            <div v-else class="space-y-2 max-h-[500px] overflow-y-auto pr-1">
              <div
                v-for="variant in selectedProductVariants"
                :key="variant.id"
                class="flex items-center justify-between gap-3 rounded-xl border border-slate-200 bg-white p-3 transition hover:border-primary/40 dark:border-[#3c342e] dark:bg-[#1f1a17]"
              >
                <div class="flex items-center gap-3 min-w-0">
                  <div class="size-10 shrink-0 overflow-hidden rounded-lg bg-slate-100 dark:bg-[#2b241f]">
                    <img
                      v-if="variant.imageUrl"
                      :src="variant.imageUrl"
                      alt="Variant"
                      class="h-full w-full object-cover"
                    />
                    <span v-else class="material-symbols-outlined flex h-full items-center justify-center text-slate-400 text-sm">image</span>
                  </div>
                  <div class="min-w-0">
                    <div class="flex items-center gap-2">
                      <span
                        class="size-3 shrink-0 rounded-full border border-slate-300"
                        :style="{ backgroundColor: variant.colorHex || '#ccc' }"
                      ></span>
                      <span class="font-bold text-xs text-slate-900 dark:text-white">
                        {{ variant.colorName || "Màu mặc định" }} / {{ variant.sizeName || "Size mặc định" }}
                      </span>
                    </div>
                    <div class="flex items-center gap-2 text-[11px] text-slate-500 dark:text-[#b9aa9a] mt-0.5">
                      <span class="font-semibold text-primary">{{ formatMoney(variant.price) }}</span>
                      <span>•</span>
                      <span>Tồn: <b :class="variant.stockQuantity > 0 ? 'text-slate-700 dark:text-slate-300' : 'text-red-500'">{{ variant.stockQuantity }}</b></span>
                    </div>
                  </div>
                </div>

                <div class="flex items-center gap-1.5 shrink-0">
                  <span
                    class="rounded-full px-2 py-0.5 text-[9px] font-bold"
                    :class="variantStatusClass(variant)"
                  >
                    {{ variantStatusLabel(variant) }}
                  </span>
                  <button
                    type="button"
                    class="rounded-lg p-1.5 hover:bg-slate-100 dark:hover:bg-[#2b241f]"
                    title="Sửa biến thể"
                    @click="openEditVariant(variant)"
                  >
                    <span class="material-symbols-outlined text-[16px] text-slate-500">edit</span>
                  </button>
                  <button
                    type="button"
                    class="rounded-lg p-1.5 hover:bg-red-50 dark:hover:bg-red-900/20"
                    title="Xóa biến thể"
                    @click="requestDeleteVariant(variant)"
                  >
                    <span class="material-symbols-outlined text-[16px] text-red-500">delete</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>

    <!-- MODAL THÊM / SỬA SẢN PHẨM -->
    <Teleport to="body">
      <div
        v-if="showModal"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4"
        @click.self="closeModal"
      >
        <div
          class="w-full max-w-lg rounded-2xl bg-white p-6 shadow-2xl dark:bg-[#1f1a17]"
        >
          <div class="mb-4 flex items-center justify-between">
            <h3 class="text-lg font-bold">
              {{ editingProductId ? "Chỉnh sửa sản phẩm" : "Thêm sản phẩm mới" }}
            </h3>
            <button
              class="rounded-lg p-1 text-slate-400 hover:text-slate-600"
              @click="closeModal"
            >
              <span class="material-symbols-outlined">close</span>
            </button>
          </div>

          <div
            v-if="modalError"
            class="mb-4 rounded-xl border border-red-200 bg-red-50 px-3 py-2 text-xs text-red-700 dark:border-red-900/40 dark:bg-red-900/20 dark:text-red-300"
          >
            {{ modalError }}
          </div>

          <div class="mb-4 flex gap-4 border-b border-slate-200 dark:border-[#3c342e]">
            <button
              type="button"
              class="pb-2 text-sm font-semibold transition-colors"
              :class="productModalTab === 'info' ? 'border-b-2 border-primary text-primary' : 'text-slate-500 hover:text-slate-700'"
              @click="productModalTab = 'info'"
            >
              Thông tin chung
            </button>
            <button
              type="button"
              class="pb-2 text-sm font-semibold transition-colors"
              :class="productModalTab === 'image' ? 'border-b-2 border-primary text-primary' : 'text-slate-500 hover:text-slate-700'"
              @click="productModalTab = 'image'"
            >
              Hình ảnh
            </button>
          </div>

          <div v-if="productModalTab === 'info'" class="space-y-3">
            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500">Tên sản phẩm *</label>
              <input
                v-model.trim="form.name"
                type="text"
                class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                placeholder="VD: Giày thể thao nam Nike Air Max..."
              />
            </div>

            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500">Mã sản phẩm *</label>
              <input
                v-model.trim="form.productCode"
                type="text"
                class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                placeholder="VD: SP-001"
              />
            </div>

            <div class="grid grid-cols-2 gap-3">
              <div>
                <label class="mb-1 block text-xs font-medium text-slate-500">Thương hiệu *</label>
                <select
                  v-model.number="form.brandId"
                  class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                >
                  <option :value="null">-- Chọn thương hiệu --</option>
                  <option v-for="b in brands" :key="b.id" :value="Number(b.id)">{{ b.name }}</option>
                </select>
              </div>

              <div>
                <label class="mb-1 block text-xs font-medium text-slate-500">Danh mục *</label>
                <select
                  v-model.number="form.categoryId"
                  class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                >
                  <option :value="null">-- Chọn danh mục --</option>
                  <option v-for="c in categories" :key="c.id" :value="Number(c.id)">{{ c.name }}</option>
                </select>
              </div>
            </div>

            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500">Mô tả sản phẩm</label>
              <textarea
                v-model.trim="form.description"
                rows="3"
                class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm outline-none focus:border-primary focus:ring-1 focus:ring-primary dark:border-[#3c342e] dark:bg-[#2b241f]"
                placeholder="Nhập mô tả sản phẩm..."
              ></textarea>
            </div>
          </div>

          <div v-else-if="productModalTab === 'image'" class="space-y-4">
            <div>
              <label class="mb-2 block text-sm font-semibold text-slate-700 dark:text-slate-200">Ảnh chính sản phẩm</label>
              <div class="flex items-center gap-3">
                <div class="size-20 shrink-0 overflow-hidden rounded-xl border border-slate-200 bg-slate-50 dark:border-[#3c342e] dark:bg-[#2b241f]">
                  <img v-if="form.imageUrl" :src="form.imageUrl" alt="Preview" class="h-full w-full object-cover" />
                  <span v-else class="flex h-full items-center justify-center text-xs text-slate-400">Không ảnh</span>
                </div>
                <div class="flex-1 space-y-2">
                  <input
                    type="file"
                    accept="image/*"
                    class="block w-full text-sm text-slate-500 file:mr-3 file:rounded-lg file:border-0 file:bg-slate-100 file:px-4 file:py-2 file:text-sm file:font-semibold hover:file:bg-slate-200 dark:file:bg-[#3c342e] dark:file:text-slate-200"
                    @change="handleProductImageUpload"
                  />
                  <input
                    v-model.trim="form.imageUrl"
                    type="text"
                    placeholder="Hoặc nhập URL ảnh..."
                    class="w-full rounded-lg border border-slate-900 bg-gray-50 px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                  />
                </div>
              </div>
            </div>

            <div class="pt-2 border-t border-slate-100 dark:border-[#3c342e]">
              <label class="mb-2 block text-sm font-semibold text-slate-700 dark:text-slate-200">Ảnh phụ (Gallery)</label>
              <input
                type="file"
                multiple
                accept="image/*"
                class="block w-full mb-2 text-sm text-slate-500 file:mr-3 file:rounded-lg file:border-0 file:bg-slate-100 file:px-4 file:py-2 file:text-sm file:font-semibold hover:file:bg-slate-200 dark:file:bg-[#3c342e] dark:file:text-slate-200"
                @change="handleProductGalleryUpload"
              />
              <div class="flex flex-wrap gap-2 mt-2">
                <div v-for="(img, idx) in form.galleryImages" :key="idx" class="relative size-16 rounded-xl border border-slate-200 bg-slate-50 overflow-hidden dark:border-[#3c342e]">
                  <img :src="img.url || img" class="h-full w-full object-cover" />
                  <button type="button" class="absolute top-0 right-0 bg-red-500 text-white rounded-bl-lg p-0.5 opacity-80 hover:opacity-100" @click="removeGalleryImage(idx)">
                    <span class="material-symbols-outlined text-[14px]">close</span>
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div class="mt-6 flex justify-end gap-3">

            <button
              type="button"
              :disabled="saving"
              class="rounded-xl bg-primary px-4 py-2 text-sm font-medium text-white hover:opacity-90 disabled:opacity-50"
              @click="requestSave"
            >
              {{ saving ? "Đang lưu..." : editingProductId ? "Cập nhật" : "Thêm mới" }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- MODAL THÊM / SỬA BIẾN THỂ -->
    <Teleport to="body">
      <div
        v-if="showVariantModal"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4"
        @click.self="closeVariantModal"
      >
        <div
          class="w-full max-w-lg rounded-2xl bg-white p-6 shadow-2xl dark:bg-[#1f1a17]"
        >
          <div class="mb-4 flex items-center justify-between">
            <h3 class="text-lg font-bold">
              {{ editingVariantId ? `Chỉnh sửa biến thể #${editingVariantId}` : "Thêm biến thể sản phẩm" }}
            </h3>
            <button
              class="rounded-lg p-1 text-slate-400 hover:text-slate-600"
              @click="closeVariantModal"
            >
              <span class="material-symbols-outlined">close</span>
            </button>
          </div>

          <div
            v-if="variantModalError"
            class="mb-4 rounded-xl border border-red-200 bg-red-50 px-3 py-2 text-xs text-red-700 dark:border-red-900/40 dark:bg-red-900/20 dark:text-red-300"
          >
            {{ variantModalError }}
          </div>

          <div class="space-y-3">
            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500">Sản phẩm *</label>
              <select
                v-model.number="variantForm.productId"
                :disabled="!!editingVariantId"
                class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f] disabled:opacity-60"
              >
                <option :value="null">-- Chọn sản phẩm --</option>
                <option v-for="p in products" :key="p.id" :value="Number(p.id)">{{ p.name }} (#{{ p.id }})</option>
              </select>
            </div>

            <!-- Single or Batch creation switch (Only when creating) -->
            <div v-if="!editingVariantId" class="flex gap-2 rounded-xl bg-slate-100 p-1 dark:bg-[#2b241f]">
              <button
                type="button"
                class="flex-1 rounded-lg py-1 text-xs font-semibold transition-colors"
                :class="!batchMode ? 'bg-white text-primary shadow-sm dark:bg-[#1f1a17]' : 'text-slate-500'"
                @click="batchMode = false"
              >
                Thêm 1 biến thể
              </button>
              <button
                type="button"
                class="flex-1 rounded-lg py-1 text-xs font-semibold transition-colors"
                :class="batchMode ? 'bg-white text-primary shadow-sm dark:bg-[#1f1a17]' : 'text-slate-500'"
                @click="batchMode = true"
              >
                Tạo hàng loạt (Màu x Size)
              </button>
            </div>

            <!-- Single Variant Colors & Sizes -->
            <div v-if="!batchMode" class="grid grid-cols-2 gap-3">
              <div>
                <label class="mb-1 block text-xs font-medium text-slate-500">Màu sắc *</label>
                <select
                  v-model.number="variantForm.colorId"
                  class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                >
                  <option :value="null">-- Chọn màu --</option>
                  <option v-for="c in colors" :key="c.id" :value="Number(c.id)">{{ c.name }}</option>
                </select>
              </div>

              <div>
                <label class="mb-1 block text-xs font-medium text-slate-500">Kích thước (Size) *</label>
                <select
                  v-model.number="variantForm.sizeId"
                  class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                >
                  <option :value="null">-- Chọn size --</option>
                  <option v-for="s in sizes" :key="s.id" :value="Number(s.id)">{{ s.name }}</option>
                </select>
              </div>
            </div>

            <!-- Batch Creation Multi-Select -->
            <div v-else class="space-y-3">
              <div>
                <label class="mb-1 block text-xs font-medium text-slate-500">Chọn các màu sắc *</label>
                <div class="flex flex-wrap gap-2 max-h-28 overflow-y-auto rounded-xl border border-slate-200 p-2 dark:border-[#3c342e]">
                  <label
                    v-for="c in colors"
                    :key="c.id"
                    class="flex items-center gap-1.5 rounded-lg border px-2 py-1 text-xs cursor-pointer"
                    :class="variantForm.colorIds.includes(Number(c.id)) ? 'border-primary bg-primary/10 text-primary' : 'border-slate-200'"
                  >
                    <input
                      type="checkbox"
                      :value="Number(c.id)"
                      v-model="variantForm.colorIds"
                      class="hidden"
                    />
                    <span class="size-3 rounded-full" :style="{ backgroundColor: c.hexCode || '#ccc' }"></span>
                    <span>{{ c.name }}</span>
                  </label>
                </div>
              </div>

              <div>
                <label class="mb-1 block text-xs font-medium text-slate-500">Chọn các kích thước (Size) *</label>
                <div class="flex flex-wrap gap-2 max-h-28 overflow-y-auto rounded-xl border border-slate-200 p-2 dark:border-[#3c342e]">
                  <label
                    v-for="s in sizes"
                    :key="s.id"
                    class="flex items-center gap-1.5 rounded-lg border px-2.5 py-1 text-xs cursor-pointer"
                    :class="variantForm.sizeIds.includes(Number(s.id)) ? 'border-primary bg-primary/10 text-primary' : 'border-slate-200'"
                  >
                    <input
                      type="checkbox"
                      :value="Number(s.id)"
                      v-model="variantForm.sizeIds"
                      class="hidden"
                    />
                    <span>{{ s.name }}</span>
                  </label>
                </div>
              </div>

              <p class="text-xs text-primary font-semibold">
                Sẽ tạo tổng cộng: {{ variantForm.colorIds.length * variantForm.sizeIds.length }} biến thể
              </p>
            </div>

            <!-- Price & Stock -->
            <div class="grid grid-cols-2 gap-3">
              <div>
                <label class="mb-1 block text-xs font-medium text-slate-500">Giá bán (VND) *</label>
                <input
                  v-model.number="variantForm.price"
                  type="number"
                  min="0"
                  step="10000"
                  class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm font-semibold text-primary dark:border-[#3c342e] dark:bg-[#2b241f]"
                  placeholder="VD: 500000"
                />
              </div>

              <div>
                <label class="mb-1 block text-xs font-medium text-slate-500">Số lượng tồn kho *</label>
                <input
                  v-model.number="variantForm.stockQuantity"
                  type="number"
                  min="0"
                  class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                  placeholder="VD: 50"
                />
              </div>
            </div>

            <div class="grid grid-cols-2 gap-3">
              <div>
                <label class="mb-1 block text-xs font-medium text-slate-500">Trạng thái</label>
                <select
                  v-model="variantForm.status"
                  class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                >
                  <option value="SELLING">Đang bán</option>
                  <option value="OUT_OF_STOCK">Hết hàng</option>
                  <option value="HIDDEN">Ngừng bán</option>
                </select>
              </div>

              <div>
                <label class="mb-1 block text-xs font-medium text-slate-500">Ảnh biến thể (tùy chọn)</label>
                <div class="flex items-center gap-3">
                  <div class="size-16 shrink-0 overflow-hidden rounded-xl border border-slate-200 bg-slate-50 dark:border-[#3c342e] dark:bg-[#2b241f]">
                    <img v-if="variantForm.imageUrl" :src="variantForm.imageUrl" alt="Preview" class="h-full w-full object-cover" />
                    <span v-else class="flex h-full items-center justify-center text-[10px] text-slate-400 text-center">Trống</span>
                  </div>
                  <div class="flex-1 space-y-2">
                    <input
                      type="file"
                      accept="image/*"
                      class="block w-full text-xs text-slate-500 file:mr-2 file:rounded-md file:border-0 file:bg-slate-100 file:px-2 file:py-1 file:text-xs file:font-semibold hover:file:bg-slate-200 dark:file:bg-[#3c342e] dark:file:text-slate-200"
                      @change="handleVariantImageUpload"
                    />
                    <input
                      v-model.trim="variantForm.imageUrl"
                      type="text"
                      placeholder="Hoặc nhập URL..."
                      class="w-full rounded-lg border border-slate-900 bg-gray-50 px-2 py-1.5 text-xs dark:border-[#3c342e] dark:bg-[#2b241f]"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="mt-6 flex justify-end gap-3">

            <button
              type="button"
              :disabled="variantSaving"
              class="rounded-xl bg-primary px-4 py-2 text-sm font-medium text-white hover:opacity-90 disabled:opacity-50"
              @click="requestSaveVariant"
            >
              {{ variantSaving ? "Đang lưu..." : editingVariantId ? "Cập nhật" : "Tạo biến thể" }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- Global Confirm Dialog -->
    <ConfirmDialog
      :open="!!confirmModal"
      :title="confirmModal?.title"
      :message="confirmModal?.message"
      :confirm-text="confirmModal?.confirmText"
      :danger="!!confirmModal?.danger"
      :loading="!!confirmModal?.loading"
      @confirm="executeConfirm"
      @cancel="cancelConfirm"
    />
  </AdminShell>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import axios from "axios";
import AdminShell from "@/components/admin/AdminShell.vue";
import ConfirmDialog from "@/components/admin/ConfirmDialog.vue";
import PaginationBar from "@/components/PaginationBar.vue";
import { useConfirmDialog } from "@/composables/useConfirmDialog";
import { usePagination } from "@/composables/usePagination";
import {
  adminApi,
  brandsApi,
  categoriesApi,
  colorsApi,
  sizesApi,
} from "@/services/api";

const { confirmModal, requestConfirm, executeConfirm, cancelConfirm } =
  useConfirmDialog();

const loading = ref(false);
const error = ref("");
const products = ref([]);
const brands = ref([]);
const categories = ref([]);
const colors = ref([]);
const sizes = ref([]);
const variants = ref([]);

const search = ref("");
const filterBrandId = ref(null);
const filterCategoryId = ref(null);
const filterStockStatus = ref("ALL");

const hiddenProducts = ref([]);
const hiddenVariants = ref([]);
const expandedProductIds = ref([]);

// Product Modal State
const showModal = ref(false);
const productModalTab = ref("info");
const editingProductId = ref(null);
const saving = ref(false);
const modalError = ref("");
const form = ref(emptyProductForm());
const productImageFile = ref(null);
const productGalleryFiles = ref([]);

// Toast notification
const successToast = ref("");
function showToast(msg) {
  successToast.value = msg;
  setTimeout(() => { successToast.value = ""; }, 3000);
}

// Variant Modal State
const showVariantModal = ref(false);
const editingVariantId = ref(null);
const variantSaving = ref(false);
const variantModalError = ref("");
const batchMode = ref(false);
const variantForm = ref(emptyVariantForm());

const API_BASE_URL =
  import.meta.env.VITE_API_URL || "http://localhost:8080/api";

function emptyProductForm() {
  return {
    name: "",
    productCode: "",
    brandId: null,
    categoryId: null,
    description: "",
    imageUrl: "",
    galleryImages: [],
  };
}

function emptyVariantForm() {
  return {
    productId: null,
    colorId: null,
    sizeId: null,
    colorIds: [],
    sizeIds: [],
    price: 0,
    stockQuantity: 0,
    status: "SELLING",
    imageUrl: "",
  };
}

function toList(payload) {
  if (Array.isArray(payload)) return payload;
  if (payload && typeof payload === "object") {
    if (Array.isArray(payload.content)) return payload.content;
    if (Array.isArray(payload.list)) return payload.list;
    if (Array.isArray(payload.data)) return payload.data;
  }
  return [];
}

const brandMap = computed(() => {
  return brands.value.reduce((acc, item) => {
    const id = Number(item.id);
    if (Number.isFinite(id)) acc[id] = item.name;
    return acc;
  }, {});
});

const categoryMap = computed(() => {
  return categories.value.reduce((acc, item) => {
    const id = Number(item.id);
    if (Number.isFinite(id)) acc[id] = item.name;
    return acc;
  }, {});
});

const colorMap = computed(() => {
  return colors.value.reduce((acc, item) => {
    const id = Number(item.id);
    if (Number.isFinite(id)) acc[id] = item;
    return acc;
  }, {});
});

const sizeMap = computed(() => {
  return sizes.value.reduce((acc, item) => {
    const id = Number(item.id);
    if (Number.isFinite(id)) acc[id] = item;
    return acc;
  }, {});
});

function normalizeHexColor(raw) {
  if (!raw) return null;
  const s = String(raw).trim();
  if (s.startsWith("#")) return s;
  if (/^[0-9A-Fa-f]{6}$/.test(s) || /^[0-9A-Fa-f]{3}$/.test(s)) return "#" + s;
  return s;
}

function brandName(brandId) {
  const id = Number(brandId);
  return Number.isFinite(id) ? brandMap.value[id] || "-" : "-";
}

function categoryName(categoryId) {
  const id = Number(categoryId);
  return Number.isFinite(id) ? categoryMap.value[id] || "-" : "-";
}

function formatMoney(amount) {
  const n = Number(amount || 0);
  return n.toLocaleString("vi-VN") + " đ";
}

function isExpandedProduct(productId) {
  return expandedProductIds.value.includes(Number(productId));
}

function toggleProductExpand(productId) {
  const id = Number(productId);
  if (isExpandedProduct(id)) {
    expandedProductIds.value = expandedProductIds.value.filter((item) => item !== id);
  } else {
    expandedProductIds.value = [...expandedProductIds.value, id];
  }
}

// Map variants to products with hydrated color & size info
const visibleVariants = computed(() => {
  return variants.value
    .filter((v) => !hiddenVariants.value.some((h) => Number(h.id) === Number(v.id)))
    .map((v) => {
      const colorId = Number(v.colorId ?? v.color_id);
      const sizeId = Number(v.sizeId ?? v.size_id);
      const colorObj = Number.isFinite(colorId) ? colorMap.value[colorId] : null;
      const sizeObj = Number.isFinite(sizeId) ? sizeMap.value[sizeId] : null;

      const rawHex =
        v.colorHex ||
        v.color_hex ||
        colorObj?.hexCode ||
        colorObj?.hex_code ||
        colorObj?.hexCode1 ||
        null;

      const colorName =
        v.colorName ||
        v.color_name ||
        colorObj?.name ||
        (Number.isFinite(colorId) ? `Màu #${colorId}` : "Màu mặc định");

      const sizeName =
        v.sizeName ||
        v.size_name ||
        sizeObj?.name ||
        (Number.isFinite(sizeId) ? `${sizeObj?.name || sizeId}` : "Size mặc định");

      return {
        ...v,
        colorId: Number.isFinite(colorId) ? colorId : null,
        sizeId: Number.isFinite(sizeId) ? sizeId : null,
        colorName,
        sizeName,
        colorHex: normalizeHexColor(rawHex) || "#cccccc",
      };
    });
});

function getProductVariants(productId) {
  const id = Number(productId);
  return visibleVariants.value.filter(
    (v) => Number(v.productId) === id || Number(v.product_id) === id || (v.product && Number(v.product.id) === id)
  );
}

function getProductTotalStock(productId) {
  return getProductVariants(productId).reduce(
    (sum, v) => sum + (Number(v.stockQuantity ?? v.stock_quantity) || 0),
    0,
  );
}

function variantStatusLabel(variant) {
  const s = String(variant.status || "").toUpperCase();
  const stock = Number(variant.stockQuantity ?? 0);
  if (s === "HIDDEN" || s === "NGUNG_BAN") return "Ngừng bán";
  if (s === "OUT_OF_STOCK" || stock <= 0) return "Hết hàng";
  return "Đang bán";
}

function variantStatusClass(variant) {
  const s = String(variant.status || "").toUpperCase();
  const stock = Number(variant.stockQuantity ?? 0);
  if (s === "HIDDEN" || s === "NGUNG_BAN") {
    return "bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-400";
  }
  if (s === "OUT_OF_STOCK" || stock <= 0) {
    return "bg-red-100 text-red-700 dark:bg-red-950/50 dark:text-red-300";
  }
  return "bg-emerald-100 text-emerald-700 dark:bg-emerald-950/50 dark:text-emerald-300";
}

// Visible & Filtered Products
const visibleProducts = computed(() => {
  return products.value.filter(
    (product) =>
      !hiddenProducts.value.some((h) => Number(h.id) === Number(product.id)),
  );
});

const filteredProducts = computed(() => {
  const keyword = search.value.trim().toLowerCase();
  const brandId = filterBrandId.value ? Number(filterBrandId.value) : null;
  const categoryId = filterCategoryId.value ? Number(filterCategoryId.value) : null;

  return visibleProducts.value.filter((product) => {
    if (brandId !== null && Number(product.brandId) !== brandId) return false;
    if (categoryId !== null && Number(product.categoryId) !== categoryId) return false;

    const prodVariants = getProductVariants(product.id);
    const totalStock = getProductTotalStock(product.id);

    if (filterStockStatus.value === "IN_STOCK" && totalStock <= 0) return false;
    if (filterStockStatus.value === "OUT_OF_STOCK" && totalStock > 0) return false;
    if (filterStockStatus.value === "LOW_STOCK" && (totalStock <= 0 || totalStock >= 10)) return false;

    if (!keyword) return true;

    const variantText = prodVariants
      .map((v) => `${v.colorName || ""} ${v.sizeName || ""} ${v.id}`)
      .join(" ");

    const haystack = [
      product.name,
      product.productCode,
      brandName(product.brandId),
      categoryName(product.categoryId),
      product.id,
      variantText,
    ]
      .filter(Boolean)
      .join(" ")
      .toLowerCase();

    return haystack.includes(keyword);
  });
});

const {
  currentPage: productsPage,
  totalPages: productsTotalPages,
  paginatedItems: paginatedProducts,
  pageStart: productsPageStart,
  pageEnd: productsPageEnd,
  totalItems: productsTotalItems,
  previousPage: productsPreviousPage,
  nextPage: productsNextPage,
} = usePagination(filteredProducts, 10);

const selectedProductId = ref(null);

const selectedProduct = computed(() => {
  if (selectedProductId.value !== null) {
    const found = products.value.find((p) => Number(p.id) === Number(selectedProductId.value));
    if (found) return found;
  }
  return paginatedProducts.value[0] || null;
});

const selectedProductVariants = computed(() => {
  if (!selectedProduct.value) return [];
  return getProductVariants(selectedProduct.value.id);
});

const selectedProductInStockCount = computed(() => {
  return selectedProductVariants.value.filter(
    (v) => Number(v.stockQuantity || 0) > 0 && String(v.status || "").toUpperCase() !== "HIDDEN",
  ).length;
});

const selectedProductLowStockCount = computed(() => {
  return selectedProductVariants.value.filter(
    (v) => Number(v.stockQuantity || 0) > 0 && Number(v.stockQuantity || 0) < 10,
  ).length;
});

const selectedProductOutOfStockCount = computed(() => {
  return selectedProductVariants.value.filter(
    (v) => Number(v.stockQuantity || 0) <= 0 || String(v.status || "").toUpperCase() === "OUT_OF_STOCK",
  ).length;
});

function selectProduct(product) {
  selectedProductId.value = Number(product.id);
}

// --- LOAD ALL DATA ---
async function loadData() {
  loading.value = true;
  error.value = "";

  try {
    const [pList, bList, cList, colList, szList, vList] = await Promise.all([
      adminApi.getProducts({ page: 0, size: 300 }),
      brandsApi.getAll().catch(() => []),
      categoriesApi.getAll().catch(() => []),
      colorsApi.getAll().catch(() => []),
      sizesApi.getAll().catch(() => []),
      adminApi.getVariants().catch(() => []),
    ]);

    products.value = toList(pList);
    brands.value = toList(bList);
    categories.value = toList(cList);
    colors.value = toList(colList);
    sizes.value = toList(szList);
    variants.value = toList(vList);
  } catch (err) {
    console.error("Failed to load products/inventory:", err);
    error.value = "Không thể tải dữ liệu sản phẩm. Vui lòng kiểm tra backend API.";
  } finally {
    loading.value = false;
  }
}

// --- PRODUCT CRUD ---
function openCreate() {
  editingProductId.value = null;
  form.value = emptyProductForm();
  productImageFile.value = null;
  productGalleryFiles.value = [];
  productModalTab.value = "info";
  modalError.value = "";
  showModal.value = true;
}

function openEdit(product) {
  editingProductId.value = product.id;
  form.value = {
    name: product.name || "",
    productCode: product.productCode || "",
    brandId: Number(product.brandId) || null,
    categoryId: Number(product.categoryId) || null,
    description: product.description || "",
    imageUrl: product.imageUrl || "",
    galleryImages: product.galleryImages ? String(product.galleryImages).split(',').filter(Boolean) : [],
  };
  productImageFile.value = null;
  productGalleryFiles.value = [];
  productModalTab.value = "info";
  modalError.value = "";
  showModal.value = true;
}

function closeModal() {
  showModal.value = false;
  editingProductId.value = null;
}

function requestSave() {
  const f = form.value;
  modalError.value = "";

  if (!f.name || !f.productCode) {
    modalError.value = "Vui lòng nhập tên và mã sản phẩm.";
    return;
  }
  if (f.name.length > 100) {
    modalError.value = "Tên sản phẩm không được quá 100 ký tự.";
    return;
  }
  if (f.productCode.length > 50) {
    modalError.value = "Mã sản phẩm không được quá 50 ký tự.";
    return;
  }
  if (f.description && f.description.length > 100) {
    modalError.value = "Mô tả sản phẩm không được quá 100 ký tự.";
    return;
  }
  if (!f.brandId || !f.categoryId) {
    modalError.value = "Vui lòng chọn thương hiệu và danh mục.";
    return;
  }

  const isEdit = !!editingProductId.value;
  requestConfirm({
    title: isEdit ? "Xác nhận cập nhật sản phẩm" : "Xác nhận thêm sản phẩm mới",
    message: isEdit
      ? `Bạn có chắc muốn cập nhật sản phẩm "${f.name}"?`
      : `Bạn có chắc muốn thêm sản phẩm mới "${f.name}"?`,
    confirmText: isEdit ? "Cập nhật" : "Thêm mới",
    action: saveProduct,
  });
}

async function saveProduct() {
  saving.value = true;
  modalError.value = "";
  try {
    let uploadedUrl = form.value.imageUrl;
    if (productImageFile.value) {
      uploadedUrl = await uploadProductImageDirect(productImageFile.value);
    }

    let finalGalleryUrls = [...(form.value.galleryImages || [])].filter(u => u && !u.startsWith('data:'));
    if (productGalleryFiles.value && productGalleryFiles.value.length > 0) {
      for (const file of productGalleryFiles.value) {
        const u = await uploadProductImageDirect(file);
        if (u) finalGalleryUrls.push(u);
      }
    }

    const payload = {
      name: form.value.name.trim(),
      productCode: form.value.productCode.trim(),
      brandId: Number(form.value.brandId),
      categoryId: Number(form.value.categoryId),
      description: form.value.description ? form.value.description.trim() : "",
      imageUrl: uploadedUrl || null,
      galleryImages: finalGalleryUrls.length > 0 ? finalGalleryUrls.join(',') : "",
    };

    if (editingProductId.value) {
      await adminApi.updateProduct(editingProductId.value, payload);
    } else {
      await adminApi.createProduct(payload);
    }

    const isEdit = !!editingProductId.value;
    closeModal();
    await loadData();
    showToast(isEdit ? "Cập nhật sản phẩm thành công!" : "Thêm sản phẩm thành công!");
  } catch (err) {
    modalError.value = err?.message || "Không thể lưu sản phẩm.";
    throw err;
  } finally {
    saving.value = false;
  }
}

function requestDelete(product) {
  requestConfirm({
    title: "Xác nhận xóa sản phẩm",
    message: `Bạn chắc chắn muốn xóa sản phẩm "${product.name || "Sản phẩm"}" (#${product.id})?`,
    confirmText: "Xóa sản phẩm",
    danger: true,
    action: async () => {
      await adminApi.deleteProduct(product.id);
      hiddenProducts.value.push(product);
      await loadData();
    },
  });
}

function requestRestoreProduct(product) {
  requestConfirm({
    title: "Xác nhận khôi phục",
    message: `Bạn chắc chắn muốn khôi phục sản phẩm "${product.name}"?`,
    confirmText: "Khôi phục",
    action: async () => {
      await adminApi.restoreProduct(product.id);
      hiddenProducts.value = hiddenProducts.value.filter((p) => p.id !== product.id);
      await loadData();
    },
  });
}

function requestRestoreAllProducts() {
  requestConfirm({
    title: "Xác nhận khôi phục tất cả",
    message: "Bạn có chắc muốn khôi phục tất cả sản phẩm đang ẩn?",
    confirmText: "Khôi phục tất cả",
    action: async () => {
      for (const p of hiddenProducts.value) {
        await adminApi.restoreProduct(p.id).catch(() => {});
      }
      hiddenProducts.value = [];
      await loadData();
    },
  });
}

// --- VARIANT CRUD ---
function openCreateVariant(product = null) {
  editingVariantId.value = null;
  batchMode.value = false;
  variantForm.value = emptyVariantForm();
  if (product) {
    variantForm.value.productId = Number(product.id);
  }
  variantModalError.value = "";
  showVariantModal.value = true;
}

function openEditVariant(variant) {
  editingVariantId.value = variant.id;
  batchMode.value = false;
  variantForm.value = {
    productId: Number(variant.productId ?? variant.product_id) || null,
    colorId: Number(variant.colorId ?? variant.color_id) || null,
    sizeId: Number(variant.sizeId ?? variant.size_id) || null,
    colorIds: [],
    sizeIds: [],
    price: Number(variant.price) || 0,
    stockQuantity: Number(variant.stockQuantity ?? variant.stock_quantity) || 0,
    status: variant.status || "SELLING",
    imageUrl: variant.imageUrl || "",
  };
  variantModalError.value = "";
  showVariantModal.value = true;
}

function closeVariantModal() {
  showVariantModal.value = false;
  editingVariantId.value = null;
}

function requestSaveVariant() {
  const f = variantForm.value;
  variantModalError.value = "";

  if (!f.productId) {
    variantModalError.value = "Vui lòng chọn sản phẩm.";
    return;
  }

  if (batchMode.value && !editingVariantId.value) {
    if (f.colorIds.length === 0 || f.sizeIds.length === 0) {
      variantModalError.value = "Vui lòng chọn ít nhất 1 màu và 1 size.";
      return;
    }
  } else {
    if (!f.colorId || !f.sizeId) {
      variantModalError.value = "Vui lòng chọn màu sắc và kích thước.";
      return;
    }
  }

  if (f.price < 0 || f.stockQuantity < 0) {
    variantModalError.value = "Giá và số lượng tồn không được âm.";
    return;
  }
  if (isNaN(Number(f.price)) || !Number.isFinite(Number(f.price))) {
    variantModalError.value = "Giá bán phải là số hợp lệ.";
    return;
  }
  if (Number(f.price) > 1_000_000_000) {
    variantModalError.value = "Giá bán không được vượt quá 1 tỷ đồng.";
    return;
  }
  if (isNaN(Number(f.stockQuantity)) || !Number.isFinite(Number(f.stockQuantity))) {
    variantModalError.value = "Số lượng phải là số hợp lệ.";
    return;
  }
  if (Number(f.stockQuantity) > 1000) {
    variantModalError.value = "Số lượng không được vượt quá 1000.";
    return;
  }

  const isEdit = !!editingVariantId.value;
  requestConfirm({
    title: isEdit ? "Xác nhận cập nhật biến thể" : "Xác nhận tạo biến thể",
    message: isEdit
      ? `Bạn có chắc muốn cập nhật biến thể #${editingVariantId.value}?`
      : batchMode.value
        ? `Bạn có chắc muốn tạo ${f.colorIds.length * f.sizeIds.length} biến thể tự động?`
        : `Bạn có chắc muốn tạo biến thể mới?`,
    confirmText: isEdit ? "Cập nhật" : "Tạo biến thể",
    action: saveVariant,
  });
}

async function saveVariant() {
  variantSaving.value = true;
  variantModalError.value = "";

  try {
    const f = variantForm.value;
    if (batchMode.value && !editingVariantId.value) {
      for (const cId of f.colorIds) {
        for (const sId of f.sizeIds) {
          await adminApi.createVariant({
            productId: Number(f.productId),
            colorId: Number(cId),
            sizeId: Number(sId),
            price: Number(f.price),
            stockQuantity: Number(f.stockQuantity),
            status: f.status || "SELLING",
            imageUrl: f.imageUrl || null,
          });
        }
      }
    } else if (editingVariantId.value) {
      await adminApi.updateVariant(editingVariantId.value, {
        productId: Number(f.productId),
        colorId: Number(f.colorId),
        sizeId: Number(f.sizeId),
        price: Number(f.price),
        stockQuantity: Number(f.stockQuantity),
        status: f.status || "SELLING",
        imageUrl: f.imageUrl || null,
      });
    } else {
      await adminApi.createVariant({
        productId: Number(f.productId),
        colorId: Number(f.colorId),
        sizeId: Number(f.sizeId),
        price: Number(f.price),
        stockQuantity: Number(f.stockQuantity),
        status: f.status || "SELLING",
        imageUrl: f.imageUrl || null,
      });
    }

    if (f.productId && !isExpandedProduct(f.productId)) {
      expandedProductIds.value.push(Number(f.productId));
    }

    closeVariantModal();
    await loadData();
    showToast("Lưu biến thể thành công!");
  } catch (err) {
    variantModalError.value = err?.message || "Không thể lưu biến thể.";
    throw err;
  } finally {
    variantSaving.value = false;
  }
}

function requestDeleteVariant(variant) {
  requestConfirm({
    title: "Xác nhận xóa biến thể",
    message: `Bạn có chắc muốn xóa biến thể #${variant.id} (${variant.colorName} / ${variant.sizeName})?`,
    confirmText: "Xóa biến thể",
    danger: true,
    action: async () => {
      await adminApi.deleteVariant(variant.id);
      hiddenVariants.value.push(variant);
      await loadData();
    },
  });
}

function requestRestoreVariant(variant) {
  requestConfirm({
    title: "Xác nhận khôi phục biến thể",
    message: `Bạn chắc chắn muốn khôi phục biến thể #${variant.id}?`,
    confirmText: "Khôi phục",
    action: async () => {
      await adminApi.restoreVariant(variant.id);
      hiddenVariants.value = hiddenVariants.value.filter((v) => v.id !== variant.id);
      await loadData();
    },
  });
}

function requestRestoreAllVariants() {
  requestConfirm({
    title: "Xác nhận khôi phục tất cả",
    message: "Bạn có chắc muốn khôi phục tất cả biến thể đang ẩn?",
    confirmText: "Khôi phục tất cả",
    action: async () => {
      for (const v of hiddenVariants.value) {
        await adminApi.restoreVariant(v.id).catch(() => {});
      }
      hiddenVariants.value = [];
      await loadData();
    },
  });
}

// Image upload helpers
function handleProductImageUpload(event) {
  const file = event.target?.files?.[0];
  if (!file) return;
  productImageFile.value = file;
  const reader = new FileReader();
  reader.onload = (e) => {
    form.value.imageUrl = e.target?.result || "";
  };
  reader.readAsDataURL(file);
}

function removeGalleryImage(idx) {
  const isDataUrl = form.value.galleryImages[idx].startsWith('data:');
  form.value.galleryImages.splice(idx, 1);
  if (isDataUrl && productGalleryFiles.value.length > 0) {
    // Just a rough estimation for now: if it's a data URL, remove the first matching file
    // Ideally we should track the file index, but removing the last added file is acceptable for this simple UI
    productGalleryFiles.value.pop();
  }
}

function handleProductGalleryUpload(event) {
  const files = event.target?.files;
  if (!files || files.length === 0) return;
  
  for (let i = 0; i < files.length; i++) {
    const file = files[i];
    productGalleryFiles.value.push(file);
    const reader = new FileReader();
    reader.onload = (e) => {
      form.value.galleryImages.push(e.target?.result || "");
    };
    reader.readAsDataURL(file);
  }
  
  // Reset input
  event.target.value = '';
}

async function uploadProductImageDirect(file) {
  const formData = new FormData();
  formData.append("file", file);
  const res = await axios.post(`${API_BASE_URL}/upload/image`, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  }).catch(() => null);
  return res?.data?.imageUrl || res?.data?.url || form.value.imageUrl;
}

async function handleVariantImageUpload(event) {
  const file = event.target?.files?.[0];
  if (!file) return;
  variantSaving.value = true;
  try {
    const url = await uploadProductImageDirect(file);
    if (url) {
      variantForm.value.imageUrl = url;
    }
  } catch (err) {
    variantModalError.value = "Không thể tải ảnh lên: " + err.message;
  } finally {
    variantSaving.value = false;
    event.target.value = '';
  }
}

onMounted(loadData);
</script>
