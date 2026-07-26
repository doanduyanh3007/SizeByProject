<template>
  <div class="flex text-yellow-500" :class="containerClass">
    <span
      v-for="n in 5"
      :key="n"
      class="material-symbols-outlined leading-none"
      :class="sizeClass"
      :style="starStyle(rating, n)"
    >
      {{ getStarIconName(rating, n) }}
    </span>
  </div>
</template>

<script setup>
import { getStarIconName, getStarType } from "@/utils/starRating";

defineProps({
  rating: {
    type: Number,
    default: 0,
  },
  sizeClass: {
    type: String,
    default: "text-[16px]",
  },
  containerClass: {
    type: String,
    default: "my-2",
  },
});

function starStyle(rating, index) {
  const type = getStarType(rating, index);
  if (type === "full" || type === "half") {
    return { fontVariationSettings: "'FILL' 1, 'wght' 400, 'GRAD' 0, 'opsz' 24" };
  }
  return {
    fontVariationSettings: "'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24",
    color: "#d1d5db",
  };
}
</script>
