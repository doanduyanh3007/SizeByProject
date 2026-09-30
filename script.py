import re

file_path = r'C:\Users\Admin\Documents\SizeByProject\Frontend\src\pages\OrderHistoryView.vue'

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Update canReturnActiveOrder
old_computed = '''const canReturnActiveOrder = computed(() => {
  if (!activeDetailOrder.value || isDetailLoading.value) {
    return false;
  }
  const category = getOrderStatusCategory(activeDetailOrder.value.status);'''
new_computed = '''const canReturnActiveOrder = computed(() => {
  if (!activeDetailOrder.value || isDetailLoading.value) {
    return false;
  }
  if (activeDetailOrder.value.returnRejected) return false;
  const category = getOrderStatusCategory(activeDetailOrder.value.status);'''

content = content.replace(old_computed, new_computed)

# Update template
old_template = '''                      }}
                    </button>
                    <span'''
new_template = '''                      }}
                    </button>
                    <div
                      v-else-if="activeDetailOrder.returnRejected"
                      class="mt-3 flex items-center gap-2 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700 dark:border-red-800/40 dark:bg-red-900/20 dark:text-red-400"
                    >
                      <span class="material-symbols-outlined text-[18px]">block</span>
                      Yêu c?u hoàn tr? dã b? t? ch?i. Ðon hàng không th? hoàn tr?.
                    </div>
                    <span'''

content = content.replace(old_template, new_template)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print('Done!')
