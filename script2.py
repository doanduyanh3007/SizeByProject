import re

file_path = r'C:\Users\Admin\Documents\SizeByProject\Frontend\src\pages\OrderHistoryView.vue'

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Update canReturnActiveOrder
search_1 = r"if \(!activeDetailOrder\.value \|\| isDetailLoading\.value\) \{\s*return false;\s*\}"
replace_1 = r"if (!activeDetailOrder.value || isDetailLoading.value) {\n    return false;\n  }\n  if (activeDetailOrder.value.returnRejected) return false;"
content = re.sub(search_1, replace_1, content)

# Update template
search_2 = r"</button>\s*<span\s*v-if=\"!hasAvailableOrderAction\""
replace_2 = r'''</button>
                    <div
                      v-else-if="activeDetailOrder.returnRejected"
                      class="mt-3 flex items-center gap-2 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700 dark:border-red-800/40 dark:bg-red-900/20 dark:text-red-400"
                    >
                      <span class="material-symbols-outlined text-[18px]">block</span>
                      Yêu cầu hoàn trả đã bị từ chối. Đơn hàng không thể hoàn trả.
                    </div>
                    <span
                      v-if="!hasAvailableOrderAction"'''
content = re.sub(search_2, replace_2, content)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print('Done!')
