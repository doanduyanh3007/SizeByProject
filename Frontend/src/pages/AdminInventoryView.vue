<template>
  <AdminShell>
    <template #header>
      <div class="flex flex-wrap items-center justify-between gap-3">
        <div>
          <p class="text-xs uppercase tracking-[0.18em] text-primary">
            Quản lý tồn kho
          </p>
          <h2 class="mt-1 text-2xl font-bold">Quản lý tồn kho</h2>
        </div>

        <div class="flex flex-wrap items-center gap-2">
          <button
            type="button"
            class="inline-flex items-center gap-2 rounded-xl bg-primary px-3 py-2 text-sm font-semibold text-white hover:opacity-90"
            @click="openCreate"
          >
            <span class="material-symbols-outlined text-[18px]">add</span>
            Thêm biến thể
          </button>

          <button
            type="button"
            class="inline-flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm font-medium hover:border-primary hover:text-primary dark:border-[#3c342e] dark:bg-[#1f1a17]"
            @click="resetFilters"
          >
            <span class="material-symbols-outlined text-[18px]"
              >filter_alt_off</span
            >
            Xóa bộ lọc
          </button>

          <button
            type="button"
            class="inline-flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm font-medium hover:border-primary hover:text-primary dark:border-[#3c342e] dark:bg-[#1f1a17]"
            @click="loadInventory"
          >
            <span class="material-symbols-outlined text-[18px]">refresh</span>
            Làm mới
          </button>
        </div>
      </div>
    </template>

    <div class="space-y-5">
      <!-- Search Bar -->
      <section class="retail-card p-4">
        <div class="grid gap-3 md:grid-cols-[1fr_auto]">
          <label
            class="flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2 dark:border-[#3c342e] dark:bg-[#1f1a17]"
          >
            <span class="material-symbols-outlined text-[18px] text-slate-400"
              >search</span
            >
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="Tìm theo ID, sản phẩm, màu, size..."
              class="w-full bg-transparent text-sm outline-none"
              @input="handleSearch"
            />
          </label>

          <button
            type="button"
            class="inline-flex items-center justify-center gap-2 rounded-xl border border-slate-200 bg-white px-4 py-2 text-sm font-medium hover:border-primary hover:text-primary dark:border-[#3c342e] dark:bg-[#1f1a17]"
            @click="showFilters = !showFilters"
          >
            <span class="material-symbols-outlined text-[18px]">tune</span>
            {{ showFilters ? "Ẩn bộ lọc" : "Hiện bộ lọc" }}
          </button>
        </div>
      </section>

      <!-- Advanced Filters -->
      <section v-if="showFilters" class="retail-card p-4">
        <div class="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          <div>
            <label class="mb-1 block text-xs font-medium text-slate-500"
              >Sản phẩm</label
            >
            <select
              v-model="filters.productId"
              class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#1f1a17]"
              @change="applyFilters"
            >
              <option :value="null">Tất cả sản phẩm</option>
              <option
                v-for="product in products"
                :key="product.id"
                :value="product.id"
              >
                {{ product.name }}
              </option>
            </select>
          </div>

          <div>
            <label class="mb-1 block text-xs font-medium text-slate-500"
              >Màu sắc</label
            >
            <select
              v-model="filters.colorId"
              class="w-full rounded-xl border-2 border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#1f1a17]"
              @change="applyFilters"
            >
              <option :value="null">Tất cả màu</option>
              <option v-for="color in colors" :key="color.id" :value="color.id">
                {{ color.name }}
              </option>
            </select>
          </div>

          <div>
            <label class="mb-1 block text-xs font-medium text-slate-500"
              >Size</label
            >
            <select
              v-model="filters.sizeId"
              class="w-full rounded-xl border-2 border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#1f1a17]"
              @change="applyFilters"
            >
              <option :value="null">Tất cả size</option>
              <option v-for="size in sizes" :key="size.id" :value="size.id">
                {{ size.name }}
              </option>
            </select>
          </div>

          <div>
            <label class="mb-1 block text-xs font-medium text-slate-500"
              >Trạng thái tồn kho</label
            >
            <select
              v-model="filters.stockStatus"
              class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#1f1a17]"
              @change="applyFilters"
            >
              <option :value="null">Tất cả</option>
              <option value="selling">Đang bán</option>
              <option value="out_of_stock">Hết hàng (0)</option>
              <option value="hidden">Ngừng bán</option>
              <option value="low_stock">Sắp hết (&lt; 10)</option>
              <option value="in_stock">Còn hàng (≥ 10)</option>
            </select>
          </div>

          <div>
            <label class="mb-1 block text-xs font-medium text-slate-500"
              >Giá từ (VND)</label
            >
            <input
              v-model.number="filters.minPrice"
              type="number"
              min="0"
              step="10000"
              placeholder="0"
              class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#1f1a17]"
              @input="applyFilters"
            />
          </div>

          <div>
            <label class="mb-1 block text-xs font-medium text-slate-500"
              >Giá đến (VND)</label
            >
            <input
              v-model.number="filters.maxPrice"
              type="number"
              min="0"
              step="10000"
              placeholder="Không giới hạn"
              class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#1f1a17]"
              @input="applyFilters"
            />
          </div>

          <div>
            <label class="mb-1 block text-xs font-medium text-slate-500"
              >ID từ</label
            >
            <input
              v-model.number="filters.minId"
              type="number"
              min="1"
              placeholder="1"
              class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#1f1a17]"
              @input="applyFilters"
            />
          </div>

          <div>
            <label class="mb-1 block text-xs font-medium text-slate-500"
              >ID đến</label
            >
            <input
              v-model.number="filters.maxId"
              type="number"
              min="1"
              placeholder="Không giới hạn"
              class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm dark:border-[#3c342e] dark:bg-[#1f1a17]"
              @input="applyFilters"
            />
          </div>
        </div>

        <div
          class="mt-4 flex flex-wrap items-center justify-between gap-2 border-t border-slate-200 pt-4 dark:border-[#3c342e]"
        >
          <div class="text-sm text-slate-500 dark:text-[#b9aa9a]">
            <span class="font-semibold text-slate-700 dark:text-white">{{
              filteredVariants.length
            }}</span>
            / {{ variants.length }} biến thể
          </div>
          <div class="flex gap-2">
            <button
              type="button"
              class="rounded-lg bg-slate-100 px-3 py-1.5 text-xs font-medium text-slate-600 hover:bg-slate-200 dark:bg-[#2b241f] dark:text-[#b9aa9a]"
              @click="applyFilters"
            >
              Áp dụng
            </button>
            <button
              type="button"
              class="rounded-lg border border-slate-200 px-3 py-1.5 text-xs font-medium hover:border-primary hover:text-primary dark:border-[#3c342e]"
              @click="resetFilters"
            >
              Đặt lại
            </button>
          </div>
        </div>
      </section>

      <!-- Statistics Cards -->
      <section class="retail-card p-5">
        <div class="mb-4 grid grid-cols-1 gap-3 text-sm md:grid-cols-4">
          <div
            class="rounded-xl border border-slate-200 px-3 py-2 dark:border-[#3c342e]"
          >
            Tổng biến thể:
            <span class="font-semibold">{{ variants.length }}</span>
          </div>
          <div
            class="rounded-xl border border-slate-200 px-3 py-2 dark:border-[#3c342e]"
          >
            Còn hàng:
            <span class="font-semibold text-green-600">{{ inStockCount }}</span>
          </div>
          <div
            class="rounded-xl border border-slate-200 px-3 py-2 dark:border-[#3c342e]"
          >
            Sắp hết:
            <span class="font-semibold text-yellow-600">{{
              lowStockCount
            }}</span>
          </div>
          <div
            class="rounded-xl border border-slate-200 px-3 py-2 dark:border-[#3c342e]"
          >
            Hết hàng:
            <span class="font-semibold text-red-600">{{
              outOfStockCount
            }}</span>
          </div>
        </div>

        <div
          v-if="error"
          class="mb-4 rounded-xl border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700 dark:border-red-900/40 dark:bg-red-900/20 dark:text-red-300"
        >
          {{ error }}
        </div>

        <div
          v-if="hiddenVariants.length > 0"
          class="mb-4 rounded-xl border border-amber-200 bg-amber-50 px-3 py-3 text-sm text-slate-700 dark:border-amber-900/20 dark:bg-[#3f2e11] dark:text-[#fbe7a1]"
        >
          <div class="flex flex-wrap items-center justify-between gap-3">
            <div>
              <p class="font-semibold">Biến thể ẩn tạm</p>
              <p class="text-xs text-slate-500 dark:text-[#e1c797]">
                Những biến thể đã xóa mềm sẽ tạm ẩn khỏi danh sách được dùng.
                Bạn có thể khôi phục lại khi cần.
              </p>
            </div>
            <button
              type="button"
              class="rounded-xl bg-primary px-3 py-2 text-xs font-semibold text-white hover:opacity-90"
              @click="requestRestoreAll"
            >
              Khôi phục tất cả
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
                <span class="ml-2"
                  >{{ variant.productName || "Sản phẩm" }} -
                  {{ variant.colorName || "" }}
                  {{ variant.sizeName || "" }}</span
                >
              </div>
              <button
                type="button"
                class="rounded-xl bg-emerald-500 px-3 py-2 text-xs font-semibold text-white hover:bg-emerald-600"
                @click="requestRestore(variant)"
              >
                Khôi phục
              </button>
            </div>
          </div>
        </div>

        <div
          v-if="loading"
          class="py-10 text-center text-sm text-slate-500 dark:text-[#b9aa9a]"
        >
          Đang tải dữ liệu tồn kho...
        </div>

        <div
          v-else-if="filteredVariants.length === 0"
          class="rounded-2xl border border-dashed border-slate-300 p-8 text-center text-sm text-slate-500 dark:border-[#3c342e] dark:text-[#b9aa9a]"
        >
          Không có dữ liệu tồn kho phù hợp.
        </div>

        <div v-else class="overflow-x-auto rounded-xl border border-slate-200 dark:border-[#4a4038]">
          <table class="w-full min-w-[980px] text-left text-sm">
            <thead class="border-b border-slate-200 bg-slate-50 text-xs uppercase tracking-wide text-slate-500 dark:border-[#4a4038] dark:bg-[#201a16]">
              <tr>
                <th class="px-4 py-3">STT</th>
                <th class="px-4 py-3">Sản phẩm</th>
                <th class="px-4 py-3">Biến thể</th>
                <th class="px-4 py-3">Màu</th>
                <th class="px-4 py-3">Size</th>
                <th class="px-4 py-3">Tồn kho</th>
                <th class="px-4 py-3">Hình ảnh</th>
                <th class="px-4 py-3 text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-slate-100 dark:divide-[#3a312b]">
              <template v-for="(group, groupIndex) in paginatedGroupedVariants" :key="group.productId">
                <tr class="cursor-pointer bg-white hover:bg-slate-50 dark:bg-[#1a1512] dark:hover:bg-[#201a16]" @click="toggleProductExpand(group.productId)">
                  <td class="px-4 py-4 text-slate-500">{{ inventoryPageStart + groupIndex }}</td>
                  <td class="px-4 py-4">
                    <p class="font-bold">{{ group.productName }}</p>
                    <p class="text-xs text-slate-500">ID: {{ group.productId }}</p>
                  </td>
                  <td class="px-4 py-4">{{ group.variants.length }}</td>
                  <td class="px-4 py-4">{{ group.colorCount }}</td>
                  <td class="px-4 py-4">{{ group.sizeCount }}</td>
                  <td class="px-4 py-4">
                    <span class="font-bold" :class="group.totalStock > 0 ? 'text-emerald-600' : 'text-red-500'">
                      {{ group.totalStock }}
                    </span>
                  </td>
                  <td class="px-4 py-4">
                    <div class="flex items-center gap-2">
                      <div class="flex -space-x-2">
                        <div
                          v-for="(img, idx) in group.previewImages"
                          :key="`${group.productId}-preview-${idx}`"
                          class="size-9 overflow-hidden rounded-full border-2 border-white bg-slate-100"
                        >
                          <img :src="img" alt="" class="h-full w-full object-cover" />
                        </div>
                      </div>
                      <span class="text-xs text-slate-500">+{{ group.previewCount }}</span>
                    </div>
                  </td>
                  <td class="px-4 py-4">
                    <div class="flex justify-end gap-1">
                      <span class="material-symbols-outlined p-1 text-[19px] text-slate-500">
                        {{ isExpandedProduct(group.productId) ? "expand_less" : "expand_more" }}
                      </span>
                    </div>
                  </td>
                </tr>
                <tr v-if="isExpandedProduct(group.productId)" class="bg-slate-50/70 dark:bg-[#201a16]">
                  <td colspan="8" class="px-4 py-3">
                    <div class="grid gap-2 md:grid-cols-2 xl:grid-cols-3">
                      <div
                        v-for="v in group.variants"
                        :key="v.id"
                        class="flex items-center justify-between gap-3 rounded-xl border border-slate-200 bg-white p-3 text-xs dark:border-[#3a312b] dark:bg-[#1a1512]"
                      >
                        <div class="min-w-0">
                          <p class="font-semibold">#{{ v.id }} · {{ v.colorName || "N/A" }} / {{ v.sizeName || "N/A" }}</p>
                          <p class="mt-1 text-slate-500">{{ formatCurrency(v.price) }} · Tồn: {{ v.stockQuantity ?? 0 }}</p>
                          <span class="mt-1 inline-flex rounded-full px-2 py-0.5 text-[11px] font-semibold" :class="variantStatusClass(v.status, v.stockQuantity)">
                            {{ variantStatusLabel(v.status, v.stockQuantity) }}
                          </span>
                        </div>
                        <div class="flex gap-1">
                          <button class="rounded-lg p-1.5 hover:bg-slate-100" title="Chỉnh sửa" @click.stop="openEdit(v)">
                            <span class="material-symbols-outlined text-[18px] text-slate-500">edit</span>
                          </button>
                          <button class="rounded-lg p-1.5 hover:bg-slate-100" :title="String(v.status || '').toUpperCase() === 'HIDDEN' ? 'Bỏ ẩn' : 'Xóa'" @click.stop="String(v.status || '').toUpperCase() === 'HIDDEN' ? requestRestore(v) : requestDelete(v)">
                            <span class="material-symbols-outlined text-[18px]" :class="String(v.status || '').toUpperCase() === 'HIDDEN' ? 'text-emerald-500' : 'text-red-500'">{{ String(v.status || '').toUpperCase() === 'HIDDEN' ? 'restore' : 'delete' }}</span>
                          </button>
                        </div>
                      </div>
                    </div>
                  </td>
                </tr>
              </template>
            </tbody>
          </table>
        </div>

        <PaginationBar
          :current-page="inventoryPage"
          :total-pages="inventoryTotalPages"
          :page-start="inventoryPageStart"
          :page-end="inventoryPageEnd"
          :total-items="inventoryTotalItems"
          label="nhóm sản phẩm"
          @previous="inventoryPreviousPage"
          @next="inventoryNextPage"
        />
      </section>
    </div>

    <!-- Modal Thêm/Sửa -->
    <Teleport to="body">
      <div
        v-if="showModal"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4"
        @click.self="closeModal"
      >
        <div
          class="w-full max-w-lg max-h-[88vh] overflow-y-auto rounded-2xl bg-white p-4 shadow-2xl dark:bg-[#1f1a17]"
        >
          <div class="relative mb-4 flex items-center">
            <h3 class="text-base font-bold">
              {{ editingId ? `Sửa biến thể #${editingId}` : "Thêm biến thể mới" }}
            </h3>
            <button
              type="button"
              class="absolute right-0 top-0 inline-flex size-8 items-center justify-center rounded-lg text-slate-400 hover:bg-slate-100 hover:text-slate-600 dark:hover:bg-slate-800"
              @click="closeModal"
            >
              <span class="material-symbols-outlined text-[20px]">close</span>
            </button>
          </div>

          <div class="space-y-3">
            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Sản phẩm *</label
              >
              <select
                v-model.number="form.productId"
                class="w-full rounded-xl border-2 border-slate-900 bg-gray-50 px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
              >
                <option :value="null">-- Chọn sản phẩm --</option>
                <option v-for="p in products" :key="p.id" :value="Number(p.id)">
                  {{ p.name }}
                </option>
              </select>
            </div>

            <div v-if="!editingId">
              <label class="mb-2 block text-xs font-medium text-slate-500"
                >Màu sắc * <span class="font-normal">(chọn nhiều)</span></label
              >
              <div
                class="max-h-36 overflow-y-auto rounded-xl border-2 border-slate-300 p-2 dark:border-[#3c342e]"
              >
                <div class="grid grid-cols-2 gap-2">
                  <label
                    v-for="c in colors"
                    :key="c.id"
                    class="flex cursor-pointer items-center gap-2 rounded-lg border-2 border-slate-200 px-2 py-1.5 text-sm transition-colors hover:border-primary/50 hover:bg-slate-50 dark:border-[#3c342e] dark:hover:bg-[#2b241f]"
                  >
                    <input
                      v-model="form.colorIds"
                      type="checkbox"
                      :value="Number(c.id)"
                      class="rounded border-slate-900 text-primary focus:ring-primary"
                    />
                    <span>{{ c.name }}</span>
                  </label>
                </div>
              </div>
            </div>

            <div v-else>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Màu sắc *</label
              >
              <select
                v-model.number="form.colorId"
                class="w-full rounded-xl border-2 border-slate-900 bg-gray-50 px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
              >
                <option :value="null">-- Chọn màu --</option>
                <option v-for="c in colors" :key="c.id" :value="Number(c.id)">
                  {{ c.name }}
                </option>
              </select>
            </div>

            <div v-if="!editingId">
              <label class="mb-2 block text-xs font-medium text-slate-500"
                >Size * <span class="font-normal">(chọn nhiều)</span></label
              >
              <div
                class="max-h-36 overflow-y-auto rounded-xl border-2 border-slate-300 p-2 dark:border-[#3c342e]"
              >
                <div class="grid grid-cols-3 gap-2">
                  <label
                    v-for="s in sizes"
                    :key="s.id"
                    class="flex cursor-pointer items-center gap-2 rounded-lg border-2 border-slate-200 px-2 py-1.5 text-sm transition-colors hover:border-primary/50 hover:bg-slate-50 dark:border-[#3c342e] dark:hover:bg-[#2b241f]"
                  >
                    <input
                      v-model="form.sizeIds"
                      type="checkbox"
                      :value="Number(s.id)"
                      class="rounded border-slate-900 text-primary focus:ring-primary"
                    />
                    <span>{{ s.name }}</span>
                  </label>
                </div>
              </div>
              <p
                v-if="pendingVariantCount > 0"
                class="mt-1 text-xs text-slate-500 dark:text-[#b9aa9a]"
              >
                Sẽ tạo {{ pendingVariantCount }} biến thể mới
                <span v-if="skippedVariantCount > 0">
                  (bỏ qua {{ skippedVariantCount }} đã tồn tại)
                </span>
              </p>
            </div>

            <div v-else>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Size *</label
              >
              <select
                v-model.number="form.sizeId"
                class="w-full rounded-xl border-2 border-slate-900 bg-gray-50 px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
              >
                <option :value="null">-- Chọn size --</option>
                <option v-for="s in availableSizesForForm" :key="s.id" :value="Number(s.id)">
                  {{ s.name }}
                </option>
              </select>
            </div>

            <div class="grid grid-cols-2 gap-3">
              <div>
                <label class="mb-1 block text-xs font-medium text-slate-500"
                  >Giá (VND) *</label
                >
                <input
                  v-model.number="form.price"
                  type="number"
                  min="0"
                  class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                />
              </div>
              <div>
                <label class="mb-1 block text-xs font-medium text-slate-500"
                  >Tồn kho *</label
                >
                <input
                  v-model.number="form.stockQuantity"
                  type="number"
                  min="0"
                  class="w-full rounded-xl border border-slate-900 bg-gray-50 px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                />
              </div>
            </div>

            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500"
                >Trạng thái bán *</label
              >
              <select
                v-model="form.status"
                class="w-full rounded-xl border-2 border-slate-900 bg-gray-50 px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
              >
                <option value="SELLING">Đang bán</option>
                <option value="OUT_OF_STOCK">Hết hàng</option>
                <option value="HIDDEN">Ngừng bán</option>
              </select>
            </div>
          </div>

          <p v-if="modalError" class="mt-3 text-xs text-red-500">
            {{ modalError }}
          </p>

          <div class="mt-4 flex justify-end gap-2">

            <button
              type="button"
              :disabled="saving"
              class="rounded-xl bg-primary px-3 py-1.5 text-sm font-medium text-white hover:opacity-90 disabled:opacity-60"
              @click="requestSaveVariant"
            >
              {{ saving ? "Đang lưu..." : editingId ? "Cập nhật" : "Thêm mới" }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>

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
import AdminShell from "@/components/admin/AdminShell.vue";
import ConfirmDialog from "@/components/admin/ConfirmDialog.vue";
import PaginationBar from "@/components/PaginationBar.vue";
import { useConfirmDialog } from "@/composables/useConfirmDialog";
import { usePagination } from "@/composables/usePagination";
import { adminApi, colorsApi, sizesApi } from "@/services/api";
import { resolveImageUrl } from "@/services/api";

const { confirmModal, requestConfirm, executeConfirm, cancelConfirm } =
  useConfirmDialog();

const loading = ref(false);
const error = ref("");
const searchKeyword = ref("");
const showFilters = ref(false);
const variants = ref([]);
const products = ref([]);
const colors = ref([]);
const sizes = ref([]);
const hiddenVariants = ref([]);

// Filter state
const filters = ref({
  productId: null,
  colorId: null,
  sizeId: null,
  stockStatus: null,
  minPrice: null,
  maxPrice: null,
  minId: null,
  maxId: null,
});

const expandedProductIds = ref([]);

const visibleVariants = computed(() => {
  return variants.value.filter(
    (variant) =>
      !hiddenVariants.value.some(
        (hidden) => Number(variant.id) === Number(hidden.id),
      ),
  );
});

const showModal = ref(false);
const editingId = ref(null);
const saving = ref(false);
const modalError = ref("");
const form = ref(emptyForm());

const availableSizesForForm = computed(() => {
  if (!form.value.productId || !form.value.colorId) return sizes.value;
  // Exclude sizes already used by variants of the same product and color
  const existingVariants = visibleVariants.value.filter(v => 
    Number(v.productId) === Number(form.value.productId) && 
    Number(v.colorId) === Number(form.value.colorId) && 
    Number(v.id) !== Number(editingId.value)
  );
  const usedSizeIds = new Set(existingVariants.map(v => Number(v.sizeId)));
  return sizes.value.filter(s => !usedSizeIds.has(Number(s.id)));
});

function variantCombinationExists(productId, colorId, sizeId) {
  return visibleVariants.value.some(
    (variant) =>
      Number(variant.productId) === Number(productId) &&
      Number(variant.colorId) === Number(colorId) &&
      Number(variant.sizeId) === Number(sizeId),
  );
}

const pendingVariantCombinations = computed(() => {
  if (editingId.value || !form.value.productId) return [];

  const colorIds = (form.value.colorIds || []).map(Number).filter(Boolean);
  const sizeIds = (form.value.sizeIds || []).map(Number).filter(Boolean);
  const combinations = [];

  for (const colorId of colorIds) {
    for (const sizeId of sizeIds) {
      combinations.push({ colorId, sizeId });
    }
  }

  return combinations;
});

const pendingVariantCount = computed(() => {
  return pendingVariantCombinations.value.filter(
    ({ colorId, sizeId }) =>
      !variantCombinationExists(form.value.productId, colorId, sizeId),
  ).length;
});

const skippedVariantCount = computed(() => {
  return pendingVariantCombinations.value.length - pendingVariantCount.value;
});

const deleteTarget = ref(null);
const deleting = ref(false);
const saveConfirmOpen = ref(false);
const saveConfirmMessage = ref("");

// Computed: Filtered variants
const filteredVariants = computed(() => {
  let result = [...visibleVariants.value];

  // Search by keyword
  if (searchKeyword.value) {
    const kw = searchKeyword.value.toLowerCase();
    result = result.filter((v) => {
      return (
        String(v.id).includes(kw) ||
        (v.productName && v.productName.toLowerCase().includes(kw)) ||
        (v.colorName && v.colorName.toLowerCase().includes(kw)) ||
        (v.sizeName && v.sizeName.toLowerCase().includes(kw))
      );
    });
  }

  // Filter by product
  if (filters.value.productId) {
    result = result.filter((v) => v.productId === filters.value.productId);
  }

  // Filter by color
  if (filters.value.colorId) {
    result = result.filter((v) => v.colorId === filters.value.colorId);
  }

  // Filter by size
  if (filters.value.sizeId) {
    result = result.filter((v) => v.sizeId === filters.value.sizeId);
  }

  // Filter by stock status
  if (filters.value.stockStatus) {
    result = result.filter((v) => {
      const stock = v.stockQuantity || 0;
      const status = normalizeVariantStatus(v.status, stock);
      if (filters.value.stockStatus === "selling") return status === "SELLING";
      if (filters.value.stockStatus === "out_of_stock") return stock === 0;
      if (filters.value.stockStatus === "hidden") return status === "HIDDEN";
      if (filters.value.stockStatus === "low_stock")
        return stock > 0 && stock < 10;
      if (filters.value.stockStatus === "in_stock") return stock >= 10;
      return true;
    });
  }

  // Filter by price range
  if (
    filters.value.minPrice !== null &&
    filters.value.minPrice !== undefined &&
    filters.value.minPrice > 0
  ) {
    result = result.filter((v) => (v.price || 0) >= filters.value.minPrice);
  }
  if (
    filters.value.maxPrice !== null &&
    filters.value.maxPrice !== undefined &&
    filters.value.maxPrice > 0
  ) {
    result = result.filter((v) => (v.price || 0) <= filters.value.maxPrice);
  }

  // Filter by ID range
  if (
    filters.value.minId !== null &&
    filters.value.minId !== undefined &&
    filters.value.minId > 0
  ) {
    result = result.filter((v) => v.id >= filters.value.minId);
  }
  if (
    filters.value.maxId !== null &&
    filters.value.maxId !== undefined &&
    filters.value.maxId > 0
  ) {
    result = result.filter((v) => v.id <= filters.value.maxId);
  }

  // Sort by ID
  result.sort((a, b) => a.id - b.id);

  return result;
});

const groupedVariants = computed(() => {
  const grouped = new Map();
  for (const v of filteredVariants.value) {
    const key = Number(v.productId);
    if (!grouped.has(key)) {
      grouped.set(key, {
        productId: key,
        productName: v.productName || `Sản phẩm #${key}`,
        variants: [],
      });
    }
    grouped.get(key).variants.push(v);
  }

  return [...grouped.values()]
    .map((group) => ({
      ...group,
      variants: [...group.variants].sort((a, b) => (a.id || 0) - (b.id || 0)),
      totalStock: group.variants.reduce(
        (sum, item) => sum + Number(item.stockQuantity || 0),
        0,
      ),
      colorCount: new Set(group.variants.map((item) => item.colorId)).size,
      sizeCount: new Set(group.variants.map((item) => item.sizeId)).size,
      previewImages: [
        ...new Set(
          group.variants.flatMap((item) => {
            const urls = [];
            if (item.imageUrl) urls.push(item.imageUrl);
            if (Array.isArray(item.extraImages) && item.extraImages.length) {
              urls.push(...item.extraImages.filter(Boolean));
            }
            return urls;
          }),
        ),
      ].slice(0, 3),
      previewCount: group.variants.reduce((sum, item) => {
        const count =
          (item.imageUrl ? 1 : 0) +
          (Array.isArray(item.extraImages) ? item.extraImages.length : 0);
        return sum + count;
      }, 0),
    }))
    .sort((a, b) =>
      a.productName.localeCompare(b.productName, "vi", { sensitivity: "base" }),
    );
});

const {
  currentPage: inventoryPage,
  totalPages: inventoryTotalPages,
  paginatedItems: paginatedGroupedVariants,
  pageStart: inventoryPageStart,
  pageEnd: inventoryPageEnd,
  totalItems: inventoryTotalItems,
  previousPage: inventoryPreviousPage,
  nextPage: inventoryNextPage,
} = usePagination(groupedVariants, 10);

// Stock statistics
const inStockCount = computed(() => {
  return variants.value.filter((v) => normalizeVariantStatus(v.status, v.stockQuantity) === "SELLING").length;
});

const lowStockCount = computed(() => {
  return variants.value.filter((v) => {
    const stock = v.stockQuantity || 0;
    return stock > 0 && stock < 10;
  }).length;
});

const outOfStockCount = computed(() => {
  return variants.value.filter((v) => normalizeVariantStatus(v.status, v.stockQuantity) === "OUT_OF_STOCK").length;
});

// Reset filters
function resetFilters() {
  filters.value = {
    productId: null,
    colorId: null,
    sizeId: null,
    stockStatus: null,
    minPrice: null,
    maxPrice: null,
    minId: null,
    maxId: null,
  };
  searchKeyword.value = "";
  applyFilters();
}

// Apply filters (reset to page 1)
function applyFilters() {
  // no-op: computed filters auto apply
}

// Handle search with debounce
let searchTimeout;
function handleSearch() {
  clearTimeout(searchTimeout);
  searchTimeout = setTimeout(() => {
    // no-op: computed filters auto apply
  }, 300);
}

function isExpandedProduct(productId) {
  return expandedProductIds.value.includes(Number(productId));
}

function toggleProductExpand(productId) {
  const id = Number(productId);
  if (isExpandedProduct(id)) {
    expandedProductIds.value = expandedProductIds.value.filter(
      (item) => item !== id,
    );
    return;
  }
  expandedProductIds.value = [...expandedProductIds.value, id];
}

function emptyForm() {
  return {
    productId: null,
    colorId: null,
    sizeId: null,
    colorIds: [],
    sizeIds: [],
    price: 0,
    stockQuantity: 0,
    status: "SELLING",
  };
}

function toList(data) {
  if (Array.isArray(data)) return data;
  if (data && typeof data === "object") {
    if (Array.isArray(data.content)) return data.content;
    if (Array.isArray(data.data)) return data.data;
    if (Array.isArray(data.list)) return data.list;
  }
  return [];
}

function asFiniteNumber(value, fallback = null) {
  const n = Number(value);
  return Number.isFinite(n) ? n : fallback;
}

function normalizeVariantStatus(value, stockQuantity = null) {
  const normalized = String(value || "").trim().toUpperCase();
  const hasKnownStock = stockQuantity !== null && stockQuantity !== undefined && stockQuantity !== "";
  const stock = Number(stockQuantity);
  const hasStock = hasKnownStock && Number.isFinite(stock) ? stock > 0 : true;

  if (["SELLING", "AVAILABLE", "ACTIVE", "ON_SALE"].includes(normalized)) {
    return hasStock ? "SELLING" : "OUT_OF_STOCK";
  }
  if (["OUT_OF_STOCK", "SOLD_OUT", "OUTOFSTOCK"].includes(normalized)) {
    return "OUT_OF_STOCK";
  }
  if (["HIDDEN", "INACTIVE", "DISCONTINUED", "STOP_SELLING"].includes(normalized)) {
    return "HIDDEN";
  }
  return hasStock ? "SELLING" : "OUT_OF_STOCK";
}

function variantStatusLabel(status, stockQuantity = null) {
  const normalized = normalizeVariantStatus(status, stockQuantity);
  if (normalized === "HIDDEN") return "Ngừng bán";
  if (normalized === "OUT_OF_STOCK") return "Hết hàng";
  return "Đang bán";
}

function variantStatusClass(status, stockQuantity = null) {
  const normalized = normalizeVariantStatus(status, stockQuantity);
  if (normalized === "HIDDEN") {
    return "bg-slate-100 text-slate-700 dark:bg-slate-900/30 dark:text-slate-300";
  }
  if (normalized === "OUT_OF_STOCK") {
    return "bg-red-100 text-red-700 dark:bg-red-900/20 dark:text-red-400";
  }
  return "bg-green-100 text-green-700 dark:bg-green-900/20 dark:text-green-400";
}

function toMap(rows, idKey = "id") {
  const map = new Map();
  for (const row of rows) {
    const id = asFiniteNumber(row && row[idKey], null);
    if (id !== null) {
      map.set(id, row);
    }
  }
  return map;
}

function normalizeImageList(value) {
  if (Array.isArray(value)) {
    return value.filter(Boolean).map((item) => resolveImageUrl(item));
  }

  if (typeof value === "string") {
    const trimmed = value.trim();
    if (!trimmed) return [];

    if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
      try {
        const parsed = JSON.parse(trimmed);
        return Array.isArray(parsed)
          ? parsed.filter(Boolean).map((item) => resolveImageUrl(item))
          : [];
      } catch {
        return [];
      }
    }

    if (trimmed.includes(",")) {
      return trimmed
        .split(",")
        .map((item) => item.trim())
        .filter(Boolean)
        .map((item) => resolveImageUrl(item));
    }

    return [resolveImageUrl(trimmed)];
  }

  return [];
}

function hydrateVariants(rawVariants, rawProducts, rawColors, rawSizes) {
  const productMap = toMap(rawProducts);
  const colorMap = toMap(rawColors);
  const sizeMap = toMap(rawSizes);

  return (Array.isArray(rawVariants) ? rawVariants : []).map((item) => {
    const productId = asFiniteNumber(item.productId ?? item.product_id, null);
    const colorId = asFiniteNumber(item.colorId ?? item.color_id, null);
    const sizeId = asFiniteNumber(item.sizeId ?? item.size_id, null);

    const product = productId !== null ? productMap.get(productId) : null;
    const color = colorId !== null ? colorMap.get(colorId) : null;
    const size = sizeId !== null ? sizeMap.get(sizeId) : null;

    const rawHex =
      item.colorHex ||
      item.color_hex ||
      color?.hexCode ||
      color?.hex_code ||
      color?.hexCode1 ||
      color?.hexcode ||
      null;

    const primaryImage =
      item.imageUrl || item.image_url || item.imgUrl || item.img_url || null;

    const normalizedExtras = normalizeImageList(
      item.extraImages ?? item.extra_images ?? item.images ?? null,
    );

    const stockQuantity =
      asFiniteNumber(item.stockQuantity ?? item.stock_quantity, 0) || 0;

    return {
      ...item,
      productId,
      colorId,
      sizeId,
      productName: item.productName || item.product_name || product?.name || "",
      colorName: item.colorName || item.color_name || color?.name || "",
      sizeName: item.sizeName || item.size_name || size?.name || "",
      colorHex: normalizeHexColor(rawHex),
      price: asFiniteNumber(item.price, 0) || 0,
      stockQuantity,
      status: normalizeVariantStatus(
        item.status ?? item.variantStatus ?? item.variant_status,
        stockQuantity,
      ),
      imageUrl: resolveImageUrl(primaryImage),
      extraImages: normalizedExtras,
    };
  });
}

function normalizeHexColor(value) {
  const hex = String(value || "").trim();
  if (!hex) return null;
  const normalized = hex.startsWith("#") ? hex : `#${hex}`;
  return /^#[0-9A-Fa-f]{6}$/.test(normalized) ? normalized.toUpperCase() : null;
}

function formatCurrency(value) {
  const n = Number(value);
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
    maximumFractionDigits: 0,
  }).format(Number.isFinite(n) ? n : 0);
}

function stockClass(stock) {
  const q = Number(stock);
  if (!Number.isFinite(q) || q <= 0) {
    return "bg-red-100 text-red-700 dark:bg-red-900/20 dark:text-red-400";
  }
  if (q < 10) {
    return "bg-yellow-100 text-yellow-700 dark:bg-yellow-900/20 dark:text-yellow-400";
  }
  return "bg-green-100 text-green-700 dark:bg-green-900/20 dark:text-green-400";
}

function openCreate() {
  editingId.value = null;
  form.value = emptyForm();
  modalError.value = "";
  showModal.value = true;
}

function openEdit(v) {
  editingId.value = v.id;
  form.value = {
    productId: asFiniteNumber(v.productId, null),
    colorId: asFiniteNumber(v.colorId, null),
    sizeId: asFiniteNumber(v.sizeId, null),
    colorIds: [],
    sizeIds: [],
    price: asFiniteNumber(v.price, 0) || 0,
    stockQuantity: asFiniteNumber(v.stockQuantity, 0) || 0,
    status: normalizeVariantStatus(v.status, v.stockQuantity),
  };
  modalError.value = "";
  showModal.value = true;
}

function closeModal() {
  showModal.value = false;
}

function requestSaveVariant() {
  modalError.value = "";
  const f = form.value;

  if (!f.productId) {
    modalError.value = "Vui lòng chọn sản phẩm.";
    return;
  }

  if (!editingId.value) {
    const colorIds = (f.colorIds || []).map(Number).filter(Boolean);
    const sizeIds = (f.sizeIds || []).map(Number).filter(Boolean);

    if (colorIds.length === 0 || sizeIds.length === 0) {
      modalError.value = "Vui lòng chọn ít nhất một màu và một size.";
      return;
    }

    const combinations = pendingVariantCombinations.value.filter(
      ({ colorId, sizeId }) =>
        !variantCombinationExists(f.productId, colorId, sizeId),
    );

    if (combinations.length === 0) {
      modalError.value = "Tất cả tổ hợp màu/size đã tồn tại trong kho.";
      return;
    }

    requestConfirm({
      title: "Xác nhận thêm biến thể",
      message: `Bạn có chắc muốn tạo ${combinations.length} biến thể mới cho sản phẩm đã chọn?`,
      confirmText: "Thêm mới",
      action: saveVariant,
    });
    return;
  }

  if (!f.colorId || !f.sizeId) {
    modalError.value = "Vui lòng chọn đầy đủ Màu sắc và Size.";
    return;
  }

  requestConfirm({
    title: "Xác nhận cập nhật",
    message: `Bạn có chắc muốn cập nhật biến thể #${editingId.value}?`,
    confirmText: "Cập nhật",
    action: saveVariant,
  });
}

async function saveVariant() {
  modalError.value = "";
  const f = form.value;

  if (!f.productId) {
    modalError.value = "Vui lòng chọn sản phẩm.";
    return;
  }

  if (!editingId.value) {
    const colorIds = (f.colorIds || []).map(Number).filter(Boolean);
    const sizeIds = (f.sizeIds || []).map(Number).filter(Boolean);

    if (colorIds.length === 0 || sizeIds.length === 0) {
      modalError.value = "Vui lòng chọn ít nhất một màu và một size.";
      return;
    }

    const combinations = pendingVariantCombinations.value.filter(
      ({ colorId, sizeId }) =>
        !variantCombinationExists(f.productId, colorId, sizeId),
    );

    if (combinations.length === 0) {
      modalError.value = "Tất cả tổ hợp màu/size đã tồn tại trong kho.";
      return;
    }

    saving.value = true;
    try {
      const requestedStatus = String(f.status || "").trim().toUpperCase();
      const normalizedStock =
        requestedStatus === "OUT_OF_STOCK"
          ? 0
          : Math.max(0, Number(f.stockQuantity || 0));
      const normalizedStatus = normalizeVariantStatus(f.status, normalizedStock);

      for (const { colorId, sizeId } of combinations) {
        const payload = {
          productId: Number(f.productId),
          colorId: Number(colorId),
          sizeId: Number(sizeId),
          price: Number(f.price || 0),
          stockQuantity: normalizedStock,
          status: normalizedStatus,
        };
        await adminApi.createVariant(payload);
      }

      if (!expandedProductIds.value.includes(Number(f.productId))) {
        expandedProductIds.value = [...expandedProductIds.value, Number(f.productId)];
      }

      closeModal();
      await loadInventory();
    } catch (err) {
      modalError.value = err?.message || "Không thể tạo biến thể mới.";
    } finally {
      saving.value = false;
    }
    return;
  }

  if (!f.colorId || !f.sizeId) {
    modalError.value = "Vui lòng chọn đầy đủ Màu sắc và Size.";
    return;
  }

  saving.value = true;
  try {
    let variantIdForUpload = editingId.value;
    const requestedStatus = String(f.status || "").trim().toUpperCase();
    const normalizedStock =
      requestedStatus === "OUT_OF_STOCK"
        ? 0
        : Math.max(0, Number(f.stockQuantity || 0));
    const normalizedStatus = normalizeVariantStatus(f.status, normalizedStock);

    if (!editingId.value) {
      const payload = {
        productId: Number(f.productId),
        colorId: Number(f.colorId),
        sizeId: Number(f.sizeId),
        price: Number(f.price || 0),
        stockQuantity: normalizedStock,
        status: normalizedStatus,
      };

      const createdVariant = await adminApi.createVariant(payload);
      variantIdForUpload = createdVariant.id || createdVariant.data?.id;
    } else {
      await adminApi.updateVariant(variantIdForUpload, {
        productId: Number(f.productId),
        colorId: Number(f.colorId),
        sizeId: Number(f.sizeId),
        price: Number(f.price || 0),
        stockQuantity: normalizedStock,
        status: normalizedStatus,
      });
    }

    const finalPayload = {
      productId: Number(f.productId),
      colorId: Number(f.colorId),
      sizeId: Number(f.sizeId),
      price: Number(f.price || 0),
      stockQuantity: normalizedStock,
      status: normalizedStatus,
    };

    await adminApi.updateVariant(variantIdForUpload, finalPayload);

    if (!expandedProductIds.value.includes(Number(f.productId))) {
      expandedProductIds.value = [
        ...expandedProductIds.value,
        Number(f.productId),
      ];
    }

    closeModal();
    await loadInventory();
  } catch (err) {
    modalError.value = err?.message || "Không thể lưu biến thể.";
  } finally {
    saving.value = false;
  }
}

function requestDelete(variant) {
  requestConfirm({
    title: "Xác nhận ẩn biến thể",
    message: `Bạn chắc chắn muốn ẩn (ngừng bán) biến thể #${variant.id} (${variant.productName || "Sản phẩm"} - ${variant.colorName || ""} / ${variant.sizeName || ""})?`,
    confirmText: "Xóa",
    danger: true,
    action: () => doDelete(variant),
  });
}

async function doDelete(variant) {
  if (!variant) return;

  try {
    const result = await adminApi.deleteVariant(variant.id);

    if (result && result.softDeleted) {
      if (
        !hiddenVariants.value.some(
          (item) => Number(item.id) === Number(variant.id),
        )
      ) {
        hiddenVariants.value.push(variant);
      }
      return;
    }

    await loadInventory();
  } catch (err) {
    error.value = err?.message || "Không thể xóa biến thể.";
  }
}

function requestRestore(variant) {
  requestConfirm({
    title: "Xác nhận khôi phục",
    message: `Bạn chắc chắn muốn khôi phục biến thể "${variant.productName || "Sản phẩm"} - ${variant.colorName || ""} ${variant.sizeName || ""}"?`,
    confirmText: "Khôi phục",
    action: () => restoreVariant(variant),
  });
}

function requestRestoreAll() {
  requestConfirm({
    title: "Xác nhận khôi phục",
    message: "Bạn chắc chắn muốn khôi phục tất cả biến thể đang ẩn?",
    confirmText: "Khôi phục tất cả",
    action: restoreAllHiddenVariants,
  });
}

async function restoreVariant(variant) {
  try {
    await adminApi.restoreVariant(variant.id, variant.stockQuantity);
    const restoredId = Number(variant?.id);
    hiddenVariants.value = hiddenVariants.value.filter(
      (hidden) => Number(hidden.id) !== restoredId,
    );
    await loadInventory();
  } catch (err) {
    error.value = "Không thể khôi phục biến thể.";
  }
}

async function restoreAllHiddenVariants() {
  try {
    for (const variant of hiddenVariants.value) {
      await adminApi.restoreVariant(variant.id, variant.stockQuantity);
    }
    hiddenVariants.value = [];
    await loadInventory();
  } catch (err) {
    error.value = "Có lỗi xảy ra khi khôi phục.";
  }
}

async function loadInventory() {
  loading.value = true;
  error.value = "";

  try {
    const [variantData, productData, colorData, sizeData] = await Promise.all([
      adminApi.getVariants({ page: 0, size: 500 }),
      adminApi.getProducts({ page: 0, size: 500 }),
      colorsApi.getAll().catch(() => []),
      sizesApi.getAll().catch(() => []),
    ]);

    const normalizedProducts = Array.isArray(productData) ? productData : [];
    const normalizedColors = toList(colorData);
    const normalizedSizes = toList(sizeData);

    products.value = normalizedProducts;
    colors.value = normalizedColors;
    sizes.value = normalizedSizes;
    variants.value = hydrateVariants(
      Array.isArray(variantData) ? variantData : [],
      normalizedProducts,
      normalizedColors,
      normalizedSizes,
    );
  } catch (err) {
    console.error("Failed to load inventory:", err);
    error.value =
      "Không thể tải dữ liệu tồn kho. Vui lòng kiểm tra backend API.";
  } finally {
    loading.value = false;
  }
}

onMounted(loadInventory);
</script>
