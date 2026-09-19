<template>
  <div class="relative w-full" ref="dropdownRef">
    <div
      class="flex items-center justify-between rounded-xl border bg-white px-4 py-3 text-sm cursor-pointer outline-none focus:ring-2 focus:border-primary dark:bg-[#1b2127] dark:text-white dark:border-[#3b4754]"
      :class="{'ring-2 border-primary': isOpen, 'opacity-50 cursor-not-allowed': disabled}"
      @click="toggleDropdown"
    >
      <span v-if="modelValue" class="truncate">{{ modelValue[labelKey] }}</span>
      <span v-else class="text-slate-400 truncate">{{ placeholder }}</span>
      <span class="material-symbols-outlined text-slate-400 text-lg">expand_more</span>
    </div>

    <div
      v-if="isOpen"
      class="absolute z-[99] mt-1 w-full rounded-xl border border-slate-200 bg-white shadow-lg dark:border-[#3b4754] dark:bg-[#1b2127]"
    >
      <div class="p-2 border-b dark:border-[#3b4754]">
        <input
          ref="searchInputRef"
          v-model="searchQuery"
          type="text"
          class="w-full rounded-lg bg-slate-100 px-3 py-2 text-sm outline-none dark:bg-[#252c34] dark:text-white"
          placeholder="Tìm kiếm..."
          @click.stop
        />
      </div>
      <ul class="max-h-60 overflow-y-auto py-1">
        <li
          v-for="option in filteredOptions"
          :key="option.code || option.id || option[labelKey]"
          class="cursor-pointer px-4 py-2 text-sm hover:bg-slate-100 dark:hover:bg-[#252c34]"
          :class="{'bg-primary/10 text-primary font-medium': modelValue && modelValue[labelKey] === option[labelKey]}"
          @click.stop="selectOption(option)"
        >
          {{ option[labelKey] }}
        </li>
        <li v-if="filteredOptions.length === 0" class="px-4 py-3 text-sm text-center text-slate-500">
          Không tìm thấy kết quả
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, onUnmounted } from "vue";

const props = defineProps({
  modelValue: {
    type: Object,
    default: null
  },
  options: {
    type: Array,
    default: () => []
  },
  placeholder: {
    type: String,
    default: "Chọn một mục"
  },
  labelKey: {
    type: String,
    default: "name"
  },
  disabled: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(["update:modelValue", "change"]);

const isOpen = ref(false);
const searchQuery = ref("");
const dropdownRef = ref(null);
const searchInputRef = ref(null);

const filteredOptions = computed(() => {
  if (!searchQuery.value) return props.options;
  const query = searchQuery.value.toLowerCase().trim();
  return props.options.filter(opt => 
    String(opt[props.labelKey] || "").toLowerCase().includes(query)
  );
});

function toggleDropdown() {
  if (props.disabled) return;
  isOpen.value = !isOpen.value;
  if (isOpen.value) {
    searchQuery.value = "";
    nextTick(() => {
      searchInputRef.value?.focus();
    });
  }
}

function selectOption(option) {
  emit("update:modelValue", option);
  emit("change", option);
  isOpen.value = false;
}

function handleClickOutside(event) {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target)) {
    isOpen.value = false;
  }
}

onMounted(() => {
  document.addEventListener("click", handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener("click", handleClickOutside);
});
</script>
