<template>
  <Teleport to="body">
    <Transition name="fade">
      <div
        v-if="isDialogVisible"
        class="fixed inset-0 z-[100] flex items-center justify-center bg-black/50 p-4 backdrop-blur-sm"
        @click.self="handleCancel"
      >
        <div
          class="w-full max-w-sm transform overflow-hidden rounded-2xl border border-slate-100 bg-white p-6 shadow-2xl transition-all dark:border-[#3c342e] dark:bg-[#1f1a17]"
        >
          <div class="flex items-start gap-3">
            <div
              class="flex size-10 shrink-0 items-center justify-center rounded-full"
              :class="
                danger
                  ? 'bg-red-100 text-red-600 dark:bg-red-900/30 dark:text-red-400'
                  : 'bg-primary/10 text-primary dark:bg-primary/20'
              "
            >
              <span class="material-symbols-outlined text-[22px]">
                {{ danger ? "warning" : "help" }}
              </span>
            </div>

            <div class="flex-1">
              <h3 class="text-base font-bold text-slate-900 dark:text-white">
                {{ title || "Xác nhận thao tác" }}
              </h3>
              <p class="mt-1 text-sm text-slate-500 dark:text-[#b9aa9a]">
                {{ message || "Bạn có chắc chắn muốn thực hiện thao tác này?" }}
              </p>
            </div>
          </div>

          <div class="mt-6 flex justify-end gap-2.5">
            <button
              type="button"
              :disabled="loading"
              class="rounded-xl border border-slate-200 bg-white px-4 py-2 text-sm font-medium text-slate-700 transition-colors hover:bg-slate-50 disabled:opacity-60 dark:border-[#3c342e] dark:bg-[#2b241f] dark:text-[#d3c5b8] dark:hover:bg-[#362e28]"
              @click="handleCancel"
            >
              {{ cancelText || "Hủy" }}
            </button>

            <button
              type="button"
              :disabled="loading"
              class="inline-flex items-center gap-1.5 rounded-xl px-4 py-2 text-sm font-medium text-white shadow-sm transition-all disabled:opacity-60"
              :class="
                danger
                  ? 'bg-red-500 hover:bg-red-600 active:bg-red-700'
                  : 'bg-primary hover:opacity-95 active:opacity-90'
              "
              @click="handleConfirm"
            >
              <span
                v-if="loading"
                class="size-4 animate-spin rounded-full border-2 border-white border-t-transparent"
              />
              <span>{{
                loading
                  ? "Đang xử lý..."
                  : confirmText || "Xác nhận"
              }}</span>
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  open: {
    type: Boolean,
    default: false,
  },
  isOpen: {
    type: Boolean,
    default: false,
  },
  modelValue: {
    type: Boolean,
    default: false,
  },
  title: {
    type: String,
    default: "",
  },
  message: {
    type: String,
    default: "",
  },
  confirmText: {
    type: String,
    default: "Xác nhận",
  },
  cancelText: {
    type: String,
    default: "Hủy",
  },
  danger: {
    type: Boolean,
    default: false,
  },
  loading: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["confirm", "cancel", "update:modelValue", "update:open"]);

const isDialogVisible = computed(() => {
  return props.open || props.isOpen || props.modelValue;
});

function handleConfirm() {
  if (props.loading) return;
  emit("confirm");
}

function handleCancel() {
  if (props.loading) return;
  emit("cancel");
  emit("update:modelValue", false);
  emit("update:open", false);
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease, transform 0.15s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>