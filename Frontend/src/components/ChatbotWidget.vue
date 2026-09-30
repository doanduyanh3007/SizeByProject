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
            <div v-if="msg.products && msg.products.length > 0" class="mt-3 flex flex-col gap-2">
               <div v-for="pid in msg.products" :key="pid" @click="goToProduct(pid)" class="flex items-center gap-2 p-1.5 rounded-lg border border-slate-200 bg-white hover:bg-slate-50 cursor-pointer transition-colors shadow-sm">
                  <img :src="getProductImg(pid)" class="w-12 h-12 object-cover rounded-md border border-slate-100" />
                  <span class="text-xs font-semibold text-slate-700 line-clamp-2 leading-tight flex-1">{{ getProductName(pid) }}</span>
               </div>
            </div>
          </div>
        </div>
        <div v-if="loading" class="self-start">
          <div class="bg-white border border-slate-200 text-slate-500 max-w-[250px] rounded-2xl px-4 py-2 text-sm shadow-sm italic flex gap-1">
            <span class="animate-bounce">.</span><span class="animate-bounce delay-100">.</span><span class="animate-bounce delay-200">.</span>
          </div>
        </div>
      </div>

      <div v-if="messages.length === 1 && !loading" class="px-4 pb-2 flex flex-wrap gap-2 bg-slate-50">
        <button 
          v-for="q in quickQuestions" 
          :key="q" 
          @click="sendQuickMessage(q)" 
          class="bg-blue-50 text-blue-600 text-xs px-3 py-1.5 rounded-full hover:bg-blue-100 transition-colors border border-blue-200 text-left"
        >
          {{ q }}
        </button>
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
import { ref, onMounted } from "vue";
import { API_BASE_URL } from "@/services/api";
import { useProductStore } from "@/stores/products";
import { useRouter } from "vue-router";

const isOpen = ref(false);
const input = ref("");
const loading = ref(false);
const messages = ref([
  { text: "Chào bạn! Tôi là trợ lý ảo của SizeBy. Tôi có thể giúp gì cho bạn hôm nay?", isBot: true },
]);

const productStore = useProductStore();
const router = useRouter();

onMounted(() => {
  if (productStore.products.length === 0) {
    productStore.fetchProducts();
  }
});

function getProductData(id) {
  return productStore.products.find(p => p.id === id) || {};
}

function getProductImg(id) {
  const p = getProductData(id);
  if (p.imageUrl) {
    return p.imageUrl;
  }
  if (p.images && p.images.length > 0) {
     const img = p.images.find(img => img.isPrimary) || p.images[0];
     return img.imageUrl;
  }
  return 'https://placehold.co/100x100?text=No+Image'; 
}

function getProductName(id) {
  return getProductData(id).name || "Sản phẩm";
}

function goToProduct(id) {
  isOpen.value = false;
  router.push(`/product/${id}`);
}

const quickQuestions = [
  "Sản phẩm nào đang bán chạy nhất?",
  "Sản phẩm nào được đánh giá tốt nhất?"
];

function sendQuickMessage(text) {
  input.value = text;
  sendMessage();
}

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
    const productIds = [];
    let cleanText = (data.reply || "Xin lỗi, tôi không thể trả lời lúc này.").replace(/\[PRODUCT:(\d+)\]/g, (match, id) => {
      if (!productIds.includes(Number(id))) productIds.push(Number(id));
      return "";
    }).trim();

    messages.value.push({ text: cleanText, isBot: true, products: productIds });
  } catch (err) {
    messages.value.push({ text: "Lỗi kết nối đến máy chủ chatbot.", isBot: true });
  } finally {
    loading.value = false;
  }
}
</script>
