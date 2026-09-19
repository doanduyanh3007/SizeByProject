<template>
  <div class="fixed bottom-6 right-6 z-50">
    <!-- Chat Icon -->
    <button
      v-if="!isOpen"
      @click="isOpen = true"
      class="flex h-14 w-14 items-center justify-center rounded-full bg-primary text-white shadow-xl hover:bg-primary/90 transition-transform hover:scale-105"
    >
      <span class="material-symbols-outlined text-[28px]">chat</span>
    </button>

    <!-- Chat Box -->
    <div
      v-if="isOpen"
      class="flex h-[450px] w-[350px] flex-col rounded-2xl bg-white shadow-2xl border border-slate-200 overflow-hidden"
    >
      <div class="flex items-center justify-between bg-primary px-4 py-3 text-white">
        <div class="flex items-center gap-2">
          <span class="material-symbols-outlined">smart_toy</span>
          <h3 class="font-bold">SizeBy Bot</h3>
        </div>
        <button @click="isOpen = false" class="hover:opacity-75 transition-opacity">
          <span class="material-symbols-outlined">close</span>
        </button>
      </div>

      <div class="flex-1 overflow-y-auto p-4 flex flex-col gap-3 bg-slate-50">
        <div v-for="(msg, index) in messages" :key="index" :class="msg.isBot ? 'self-start' : 'self-end'">
          <div
            :class="msg.isBot ? 'bg-white border border-slate-200 text-slate-800' : 'bg-primary text-white'"
            class="max-w-[250px] rounded-2xl px-4 py-2 text-sm shadow-sm whitespace-pre-wrap"
          >
            {{ msg.text }}
          </div>
        </div>
        <div v-if="loading" class="self-start">
          <div class="bg-white border border-slate-200 text-slate-500 max-w-[250px] rounded-2xl px-4 py-2 text-sm shadow-sm italic flex gap-1">
            <span class="animate-bounce">.</span><span class="animate-bounce delay-100">.</span><span class="animate-bounce delay-200">.</span>
          </div>
        </div>
      </div>

      <div class="p-3 border-t border-slate-200 bg-white">
        <form @submit.prevent="sendMessage" class="flex items-center gap-2">
          <input
            v-model="input"
            type="text"
            placeholder="Nhập tin nhắn..."
            class="flex-1 rounded-xl border border-slate-900 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
            :disabled="loading"
          />
          <button
            type="submit"
            class="flex h-9 w-9 items-center justify-center rounded-xl bg-primary text-white hover:bg-primary/90 disabled:opacity-50 transition-colors"
            :disabled="!input.trim() || loading"
          >
            <span class="material-symbols-outlined text-[18px]">send</span>
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { API_BASE_URL } from "@/services/api";

const isOpen = ref(false);
const input = ref("");
const loading = ref(false);
const messages = ref([
  { text: "Chào bạn! Tôi là trợ lý ảo của SizeBy. Tôi có thể giúp gì cho bạn hôm nay?", isBot: true },
]);

async function sendMessage() {
  const text = input.value.trim();
  if (!text) return;

  messages.value.push({ text, isBot: false });
  input.value = "";
  loading.value = true;

  try {
    const res = await fetch(`${API_BASE_URL}/chatbot/ask`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ message: text }),
    });
    
    if (!res.ok) throw new Error("API Error");
    
    const data = await res.json();
    messages.value.push({ text: data.reply || "Xin lỗi, tôi không thể trả lời lúc này.", isBot: true });
  } catch (err) {
    messages.value.push({ text: "Lỗi kết nối đến máy chủ chatbot.", isBot: true });
  } finally {
    loading.value = false;
  }
}
</script>
