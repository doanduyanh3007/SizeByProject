const fs = require('fs');
const path = 'C:\\Users\\khanh\\Documents\\SizeByProject\\Frontend\\src\\pages\\ProductDetailView.vue';
let content = fs.readFileSync(path, 'utf8');

const t1 = 'const compareStore = useCompareStore();';
if (content.includes(t1) && !content.includes('const toastMessage = ref(')) {
  content = content.replace(t1, t1 + '\n\nconst toastMessage = ref("");\nconst toastType = ref("success");\nlet toastTimer = null;\n\nfunction showToast(msg, type = "success") {\n  toastMessage.value = msg;\n  toastType.value = type;\n  clearTimeout(toastTimer);\n  toastTimer = setTimeout(() => { toastMessage.value = ""; }, 3000);\n}');
}

const t2 = 'function handleAddToCart() {';
if (content.includes(t2)) {
  const replacement2 = `async function handleAddToCart() {
  if (!canAddToCart.value) return;

  if (!isLoggedIn.value) {
    showAuthRequiredPopup.value = true;
    return;
  }

  const added = await cartStore.addToCart(product.value, selectedVariant.value, 1);
  if (added === false) {
    showAuthRequiredPopup.value = true;
    return;
  }

  showToast("Đã thêm vào giỏ hàng thành công!");
}`;
  const oldBlockRegex = /function handleAddToCart\(\) \{[\s\S]*?alert\(\"Đã thêm vào giỏ hàng thành công!\"\);[\s\S]*?\}/m;
  content = content.replace(oldBlockRegex, replacement2);
}

const t3Regex = /<\/div>\s*<\/template>/;
if (t3Regex.test(content) && !content.includes('Toast Notification')) {
  const replacement3 = `
    <!-- Toast Notification -->
    <transition name="slide-up">
      <div
        v-if="toastMessage"
        :class="[
          'fixed bottom-6 left-1/2 -translate-x-1/2 z-[200] flex items-center gap-3 px-5 py-3 rounded-2xl shadow-2xl text-white font-semibold text-sm transition-all',
          toastType === 'success' ? 'bg-green-600' : 'bg-red-500'
        ]"
      >
        <span class="material-symbols-outlined text-[22px]">
          {{ toastType === 'success' ? 'check_circle' : 'error' }}
        </span>
        {{ toastMessage }}
      </div>
    </transition>
  </div>
</template>`;
  content = content.replace(t3Regex, replacement3);
}

const t4 = '</style>';
if (content.includes(t4) && !content.includes('.slide-up-enter-active')) {
  content = content.replace(t4, `.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}
.slide-up-enter-from,
.slide-up-leave-to {
  opacity: 0;
  transform: translate(-50%, 20px);
}
</style>`);
}

fs.writeFileSync(path, content, 'utf8');
console.log('ProductDetailView updated via script file');
