<template>
  <router-view v-slot="{ Component, route }">
    <div class="flex min-h-screen flex-col">
      <component :is="Component" class="flex-1" />
      <AppFooter v-if="!route.path.startsWith('/admin')" />
      <ChatbotWidget v-if="!route.path.startsWith('/admin')" />
    </div>
  </router-view>
</template>

<script setup>
import AppFooter from "@/components/AppFooter.vue";
import ChatbotWidget from "@/components/ChatbotWidget.vue";
</script>
