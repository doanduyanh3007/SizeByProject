<template>
  <div
    v-if="totalItems > 0"
    class="mt-3 flex flex-wrap items-center justify-between gap-2 border-t border-slate-200 pt-3 dark:border-[#3c342e]"
  >
    <p class="text-xs text-slate-500 dark:text-[#b9aa9a]">
      Hiển thị {{ pageStart }} đến {{ pageEnd }} / {{ totalItems }} {{ label }}
    </p>
    <div class="flex items-center gap-1">
      <button
        type="button"
        class="rounded-lg border border-slate-200 px-2.5 py-1 text-xs transition-colors hover:bg-zinc-50 disabled:cursor-not-allowed disabled:opacity-50 dark:border-[#3c342e] dark:hover:bg-zinc-100"
        :disabled="currentPage === 1"
        @click="$emit('previous')"
      >
        Trước
      </button>
      <span class="px-3 py-1 text-xs">
        Trang {{ currentPage }} / {{ totalPages }}
      </span>
      <button
        type="button"
        class="rounded-lg border border-slate-200 px-2.5 py-1 text-xs transition-colors hover:bg-zinc-50 disabled:cursor-not-allowed disabled:opacity-50 dark:border-[#3c342e] dark:hover:bg-zinc-100"
        :disabled="currentPage === totalPages"
        @click="$emit('next')"
      >
        Sau
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  currentPage: { type: Number, required: true },
  totalPages: { type: Number, required: true },
  pageStart: { type: Number, required: true },
  pageEnd: { type: Number, required: true },
  totalItems: { type: Number, required: true },
  label: { type: String, default: 'bản ghi' },
})

defineEmits(['previous', 'next'])
</script>
