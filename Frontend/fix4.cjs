const fs = require('fs');
let code = fs.readFileSync('src/components/admin/AdminShell.vue', 'utf8');

const bellTarget = \          <!-- BELL NOTIFICATION -->
          <div class="relative cursor-pointer mr-2 flex items-center justify-center" @click="markAsReadAndGoToOrders" title="Thông báo đơn hàng mới">
            <span class="material-symbols-outlined text-zinc-600 hover:text-primary transition-colors text-[24px]">notifications</span>
            <span v-if="newOrderCount > 0" class="absolute -top-1 -right-1 flex h-[16px] w-[16px] items-center justify-center rounded-full bg-red-500 text-[10px] font-bold text-white shadow-sm ring-2 ring-white">
              {{ newOrderCount > 9 ? '9+' : newOrderCount }}
            </span>
          </div>\;

const newBell = \          <!-- BELL NOTIFICATION -->
          <div class="relative mr-2" @click.stop>
            <div class="relative cursor-pointer flex items-center justify-center" @click="toggleNotificationDropdown" title="Thông báo đơn hàng mới">
              <span class="material-symbols-outlined text-zinc-800 transition-colors text-[24px]">notifications</span>
              <span v-if="newOrderCount > 0" class="absolute -top-1 -right-1 flex h-[16px] w-[16px] items-center justify-center rounded-full bg-red-500 text-[10px] font-bold text-white shadow-sm ring-2 ring-white">
                {{ newOrderCount > 9 ? '9+' : newOrderCount }}
              </span>
            </div>
            
            <!-- DROPDOWN -->
            <div v-if="showNotificationDropdown" class="absolute right-0 top-10 w-80 bg-white border border-zinc-200 rounded-xl shadow-lg z-50 overflow-hidden">
              <div class="px-4 py-3 border-b border-zinc-100 flex justify-between items-center bg-zinc-50">
                <h3 class="font-bold text-sm text-zinc-800">Thông báo mới</h3>
                <span v-if="newOrderCount > 0" class="text-xs text-primary font-medium cursor-pointer" @click="markAllRead">Đánh dấu đã đọc</span>
              </div>
              <div class="max-h-[300px] overflow-y-auto">
                <div v-if="recentOrders.length === 0" class="px-4 py-6 text-center text-zinc-500 text-sm">
                  Không có thông báo mới
                </div>
                <div v-else class="divide-y divide-zinc-100">
                  <div 
                    v-for="order in recentOrders" :key="order.id"
                    class="px-4 py-3 hover:bg-zinc-50 cursor-pointer transition-colors flex items-start gap-3"
                    @click="goToOrder(order.id)"
                  >
                    <div class="h-8 w-8 rounded-full bg-blue-50 text-blue-500 flex items-center justify-center flex-shrink-0 mt-0.5">
                      <span class="material-symbols-outlined text-[18px]">local_shipping</span>
                    </div>
                    <div>
                      <p class="text-sm text-zinc-800 font-medium">Đơn hàng online #{{ order.id }}</p>
                      <p class="text-xs text-zinc-500 mt-0.5">{{ order.customerName || order.customerEmail || 'Khách vãng lai' }} vừa đặt hàng</p>
                    </div>
                  </div>
                </div>
              </div>
              <div class="px-4 py-2 bg-zinc-50 border-t border-zinc-100 text-center">
                <router-link to="/admin/orders" class="text-xs text-zinc-600 hover:text-primary font-medium" @click="showNotificationDropdown = false">Xem tất cả đơn hàng</router-link>
              </div>
            </div>
          </div>\;

code = code.replace(bellTarget, newBell);

const scriptTarget = \unction markAsReadAndGoToOrders() {
  if (newOrderCount.value > 0) {
    adminApi.getOrders({ page: 0, size: 1 }).then(data => {
       const ordersList = Array.isArray(data) ? data : (data?.content || []);
       if (ordersList.length > 0) {
          const maxId = Math.max(...ordersList.map(o => o.id));
          localStorage.setItem('lastSeenOrderId', maxId.toString());
          lastSeenOrderId.value = maxId;
          newOrderCount.value = 0;
       }
    });
  }
  router.push('/admin/orders');
}\;

const newScript = \const showNotificationDropdown = ref(false);
const recentOrders = ref([]);

function toggleNotificationDropdown() {
  showNotificationDropdown.value = !showNotificationDropdown.value;
}

function markAllRead() {
  if (recentOrders.value.length > 0) {
    const maxId = Math.max(...recentOrders.value.map(o => o.id));
    localStorage.setItem('lastSeenOrderId', maxId.toString());
    lastSeenOrderId.value = maxId;
    newOrderCount.value = 0;
  }
}

function goToOrder(orderId) {
  showNotificationDropdown.value = false;
  // Mark all as read when clicking an order
  markAllRead();
  router.push('/admin/orders'); 
}

// Close dropdown on click outside
onMounted(() => {
  document.addEventListener('click', () => {
    showNotificationDropdown.value = false;
  });
});\;

code = code.replace(scriptTarget, newScript);
code = code.replace('let count = 0;', 'let count = 0; recentOrders.value = ordersList.filter(o => o.id > latestId).slice(0, 5);');

// Fix active hover text color in sidebar
const oldActive = "                  ? 'border border-zinc-300 bg-white text-zinc-900 shadow-sm'\n                  : 'text-zinc-700 hover:bg-zinc-50 hover:text-zinc-900'";
const newActive = "                  ? 'border border-zinc-300 bg-white text-zinc-900 shadow-sm'\n                  : 'text-zinc-700 hover:bg-zinc-50 text-zinc-900'";
code = code.replace(oldActive, newActive);

fs.writeFileSync('src/components/admin/AdminShell.vue', code);
console.log('Fixed dropdown via script');
