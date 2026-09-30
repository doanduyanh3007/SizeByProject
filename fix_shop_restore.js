const fs = require('fs');

// Restore original
const original = fs.readFileSync('ShopView_original.vue', 'utf8');
const path = 'Frontend\\src\\pages\\ShopView.vue';

let content = original;

// 1. Import useCartStore
content = content.replace(
    'import { useProductStore } from "@/stores/products";',
    'import { useProductStore } from "@/stores/products";\nimport { useCartStore } from "@/stores/cart";'
);

// 2. Init cartStore
content = content.replace(
    'const productStore = useProductStore();',
    'const productStore = useProductStore();\nconst cartStore = useCartStore();'
);

// 3. Add handleQuickAdd function before toggleCompare
const funcTarget = 'function toggleCompare(productId) {';
const newFunc = `function handleQuickAdd(product) {
  if (!product || !product.variants || product.variants.length === 0) {
    alert("Sản phẩm chưa có thông tin phân loại. Vui lòng xem chi tiết để thêm vào giỏ.");
    return;
  }
  const availableVariant = product.variants.find(v => v.stockQuantity > 0) || product.variants[0];
  if (!availableVariant || availableVariant.stockQuantity <= 0) {
    alert("Sản phẩm đã hết hàng!");
    return;
  }
  if (!currentUser.value) {
    alert("Vui lòng đăng nhập để thêm vào giỏ hàng");
    return;
  }
  cartStore.accountId = currentUser.value.id;
  const added = cartStore.addToCart(product, availableVariant, 1);
  if (added !== false) {
    alert("Đã thêm vào giỏ hàng thành công!");
  }
}

function toggleCompare(productId) {`;

content = content.replace(funcTarget, newFunc);

// 4. Add quick add button next to Xem chi tiết
// Find the exact router-link block
const oldBtn = `<router-link
                      :to="\`/product/\${product.id}\`"
                      class="mt-4 w-full bg-primary hover:bg-primary-hover text-white font-medium py-2 px-4 rounded-lg flex items-center justify-center gap-2 transition-colors"
                    >
                      <span class="material-symbols-outlined text-[20px]"
                        >shopping_cart</span
                      >
                      Xem chi tiết
                    </router-link>`;

const newBtn = `<div class="mt-4 flex gap-2 w-full">
                      <button
                        @click.prevent="handleQuickAdd(product)"
                        class="flex-1 bg-white border-2 border-primary text-primary hover:bg-primary hover:text-white font-medium py-2 px-2 rounded-lg flex items-center justify-center transition-colors"
                        title="Thêm nhanh vào giỏ"
                      >
                        <span class="material-symbols-outlined text-[20px]">add_shopping_cart</span>
                      </button>
                      <router-link
                        :to="\`/product/\${product.id}\`"
                        class="flex-[3] bg-primary hover:bg-primary-hover text-white font-medium py-2 px-2 rounded-lg flex items-center justify-center gap-2 transition-colors"
                      >
                        <span class="material-symbols-outlined text-[20px]">visibility</span>
                        Xem chi tiết
                      </router-link>
                    </div>`;

if (content.includes(oldBtn)) {
    content = content.replace(oldBtn, newBtn);
    console.log('Button replaced successfully');
} else {
    console.log('Button target not found — searching...');
    // Find approximate position
    const idx = content.indexOf('Xem chi tiết');
    console.log('Xem chi tiết at idx:', idx, 'context:', content.substring(Math.max(0, idx-200), idx+100));
}

fs.writeFileSync(path, content, 'utf8');
console.log('ShopView restored and updated');
