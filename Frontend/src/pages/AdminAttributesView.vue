<template>
  <AdminShell>
    <template #header>
      <div class="flex flex-wrap items-center justify-between gap-2">
        <div>
          <p class="text-xs uppercase tracking-[0.18em] text-primary">
            Quản lý danh mục
          </p>
          <h2 class="mt-1 text-xl font-bold">Quản lý thuộc tính</h2>
        </div>

        <div class="flex flex-wrap items-center gap-2">
          <button
            type="button"
            class="inline-flex items-center gap-1.5 rounded-xl border border-slate-200 bg-white px-3 py-1.5 text-sm font-medium hover:border-primary hover:text-primary dark:border-[#3c342e] dark:bg-[#1f1a17]"
            @click="fetchAll"
          >
            <span class="material-symbols-outlined text-[18px]">refresh</span>
            Làm mới
          </button>
        </div>
      </div>
    </template>

    <div class="space-y-4">
      <div class="flex flex-wrap gap-2">
        <button
          type="button"
          class="rounded-xl px-4 py-2 text-sm font-semibold transition-colors"
          :class="
            activeTab === 'colors'
              ? 'bg-primary text-white'
              : 'border border-slate-200 bg-white text-slate-700 dark:border-[#3c342e] dark:bg-[#1f1a17]'
          "
          @click="activeTab = 'colors'"
        >
          Màu sắc
        </button>
        <button
          type="button"
          class="rounded-xl px-4 py-2 text-sm font-semibold transition-colors"
          :class="
            activeTab === 'sizes'
              ? 'bg-primary text-white'
              : 'border border-slate-200 bg-white text-slate-700 dark:border-[#3c342e] dark:bg-[#1f1a17]'
          "
          @click="activeTab = 'sizes'"
        >
          Size
        </button>
      </div>

      <div
        v-if="error"
        class="rounded-xl border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700 dark:border-red-900/40 dark:bg-red-900/20 dark:text-red-300"
      >
        {{ error }}
      </div>

      <!-- Màu sắc -->
      <section v-if="activeTab === 'colors'" class="retail-card p-3">
        <div class="mb-3 flex flex-wrap items-center justify-between gap-2">
          <p class="text-sm font-semibold">Danh sách màu sắc</p>
          <button
            type="button"
            class="inline-flex items-center gap-1.5 rounded-xl bg-primary px-3 py-1.5 text-sm font-semibold text-white hover:opacity-90"
            @click="openCreateColor"
          >
            <span class="material-symbols-outlined text-[18px]">add</span>
            Thêm màu
          </button>
        </div>

        <div
          v-if="loading"
          class="py-8 text-center text-sm text-slate-500 dark:text-[#b9aa9a]"
        >
          Đang tải...
        </div>

        <div v-else class="overflow-x-auto">
          <table class="w-full min-w-[560px] text-left text-xs">
            <thead>
              <tr
                class="border-b border-slate-200 text-slate-500 dark:border-[#3c342e] dark:text-[#b9aa9a]"
              >
                <th class="px-2 py-2 font-medium">ID</th>
                <th class="px-2 py-2 font-medium">Màu</th>
                <th class="px-2 py-2 font-medium">Tên</th>
                <th class="px-2 py-2 font-medium">Mã hex</th>
                <th class="px-2 py-2 text-right font-medium">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="color in colors"
                :key="color.id"
                class="border-b border-slate-100 last:border-none dark:border-[#2b241f]"
              >
                <td class="px-2 py-2 font-semibold text-slate-500">#{{ color.id }}</td>
                <td class="px-2 py-2">
                  <span
                    class="inline-block size-6 rounded-full border border-slate-200"
                    :style="{ backgroundColor: color.hexCode || color.hexCode1 || '#ccc' }"
                  ></span>
                </td>
                <td class="px-2 py-2 font-semibold">{{ color.name }}</td>
                <td class="px-2 py-2 text-slate-500">{{ color.hexCode || color.hexCode1 || "—" }}</td>
                <td class="px-2 py-2 text-right">
                  <div class="flex items-center justify-end gap-1">
                    <button
                      class="rounded-lg p-1.5 hover:bg-slate-100 dark:hover:bg-[#2b241f]"
                      title="Chỉnh sửa"
                      @click="openEditColor(color)"
                    >
                      <span class="material-symbols-outlined text-[18px] text-slate-500">edit</span>
                    </button>
                    <button
                      class="rounded-lg p-1.5 hover:bg-red-50 dark:hover:bg-red-900/20"
                      title="Xóa"
                      @click="requestDeleteColor(color)"
                    >
                      <span class="material-symbols-outlined text-[18px] text-red-500">delete</span>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <!-- Size -->
      <section v-if="activeTab === 'sizes'" class="retail-card p-3">
        <div class="mb-3 flex flex-wrap items-center justify-between gap-2">
          <p class="text-sm font-semibold">Danh sách size</p>
          <button
            type="button"
            class="inline-flex items-center gap-1.5 rounded-xl bg-primary px-3 py-1.5 text-sm font-semibold text-white hover:opacity-90"
            @click="openCreateSize"
          >
            <span class="material-symbols-outlined text-[18px]">add</span>
            Thêm size
          </button>
        </div>

        <div
          v-if="loading"
          class="py-8 text-center text-sm text-slate-500 dark:text-[#b9aa9a]"
        >
          Đang tải...
        </div>

        <div v-else class="overflow-x-auto">
          <table class="w-full min-w-[420px] text-left text-xs">
            <thead>
              <tr
                class="border-b border-slate-200 text-slate-500 dark:border-[#3c342e] dark:text-[#b9aa9a]"
              >
                <th class="px-2 py-2 font-medium">ID</th>
                <th class="px-2 py-2 font-medium">Tên size</th>
                <th class="px-2 py-2 text-right font-medium">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="size in sizes"
                :key="size.id"
                class="border-b border-slate-100 last:border-none dark:border-[#2b241f]"
              >
                <td class="px-2 py-2 font-semibold text-slate-500">#{{ size.id }}</td>
                <td class="px-2 py-2 font-semibold">{{ size.name }}</td>
                <td class="px-2 py-2 text-right">
                  <div class="flex items-center justify-end gap-1">
                    <button
                      class="rounded-lg p-1.5 hover:bg-slate-100 dark:hover:bg-[#2b241f]"
                      title="Chỉnh sửa"
                      @click="openEditSize(size)"
                    >
                      <span class="material-symbols-outlined text-[18px] text-slate-500">edit</span>
                    </button>
                    <button
                      class="rounded-lg p-1.5 hover:bg-red-50 dark:hover:bg-red-900/20"
                      title="Xóa"
                      @click="requestDeleteSize(size)"
                    >
                      <span class="material-symbols-outlined text-[18px] text-red-500">delete</span>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
    </div>

    <!-- Modal thêm/sửa -->
    <Teleport to="body">
      <div
        v-if="showModal"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4"
        @click.self="closeModal"
      >
        <div
          class="w-full max-w-md max-h-[90vh] overflow-y-auto rounded-2xl bg-white p-4 shadow-2xl dark:bg-[#1f1a17]"
        >
          <h3 class="mb-4 text-base font-bold">{{ modalTitle }}</h3>

          <div v-if="modalType === 'color'" class="grid gap-3">
            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500">Tên màu *</label>
              <input
                v-model.trim="colorForm.name"
                type="text"
                class="w-full rounded-xl border-2 border-slate-200 bg-white px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                placeholder="VD: Đen, Trắng, Đỏ..."
              />
            </div>
            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500">Mã màu hex</label>
              <input
                v-model.trim="colorForm.hexCode"
                type="text"
                class="w-full rounded-xl border-2 border-slate-200 bg-white px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                placeholder="#000000"
              />
            </div>
          </div>

          <div v-else class="grid gap-3">
            <div>
              <label class="mb-1 block text-xs font-medium text-slate-500">Tên size *</label>
              <input
                v-model.trim="sizeForm.name"
                type="text"
                class="w-full rounded-xl border-2 border-slate-200 bg-white px-3 py-1.5 text-sm dark:border-[#3c342e] dark:bg-[#2b241f]"
                placeholder="VD: 38, 39, 40..."
              />
            </div>
          </div>

          <p v-if="modalError" class="mt-3 text-xs text-red-500">{{ modalError }}</p>

          <div class="mt-4 flex justify-end gap-2">
            <button
              type="button"
              class="rounded-xl border border-slate-200 px-4 py-2 text-sm font-medium dark:border-[#3c342e]"
              @click="closeModal"
            >
              Hủy
            </button>
            <button
              type="button"
              class="rounded-xl bg-primary px-4 py-2 text-sm font-medium text-white hover:opacity-90"
              @click="requestSave"
            >
              {{ editingId ? "Cập nhật" : "Thêm mới" }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- Modal xác nhận -->
    <Teleport to="body">
      <div
        v-if="confirmModal"
        class="fixed inset-0 z-[60] flex items-center justify-center bg-black/50 p-4"
        @click.self="confirmModal = null"
      >
        <div
          class="w-full max-w-sm rounded-2xl bg-white p-6 shadow-2xl dark:bg-[#1f1a17]"
        >
          <h3 class="mb-2 text-base font-bold">{{ confirmModal.title }}</h3>
          <p class="text-sm text-slate-500 dark:text-[#b9aa9a]">
            {{ confirmModal.message }}
          </p>
          <div class="mt-5 flex justify-end gap-3">
            <button
              class="rounded-xl border border-slate-200 px-4 py-2 text-sm font-medium dark:border-[#3c342e]"
              @click="confirmModal = null"
            >
              Hủy
            </button>
            <button
              :disabled="confirmModal.loading"
              class="rounded-xl px-4 py-2 text-sm font-medium text-white disabled:opacity-60"
              :class="confirmModal.danger ? 'bg-red-500 hover:bg-red-600' : 'bg-primary hover:opacity-90'"
              @click="executeConfirm"
            >
              {{ confirmModal.loading ? "Đang xử lý..." : confirmModal.confirmText }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>
  </AdminShell>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import AdminShell from "@/components/admin/AdminShell.vue";
import { colorsApi, sizesApi } from "@/services/api";

const activeTab = ref("colors");
const colors = ref([]);
const sizes = ref([]);
const loading = ref(false);
const error = ref("");

const showModal = ref(false);
const modalType = ref("color");
const editingId = ref(null);
const modalError = ref("");
const colorForm = ref({ name: "", hexCode: "" });
const sizeForm = ref({ name: "" });

const confirmModal = ref(null);

const modalTitle = computed(() => {
  if (modalType.value === "color") {
    return editingId.value ? `Sửa màu #${editingId.value}` : "Thêm màu mới";
  }
  return editingId.value ? `Sửa size #${editingId.value}` : "Thêm size mới";
});

function unwrapList(response) {
  if (Array.isArray(response)) return response;
  if (Array.isArray(response?.data)) return response.data;
  return [];
}

async function fetchAll() {
  loading.value = true;
  error.value = "";
  try {
    const [colorRes, sizeRes] = await Promise.all([
      colorsApi.getAll(),
      sizesApi.getAll(),
    ]);
    colors.value = unwrapList(colorRes);
    sizes.value = unwrapList(sizeRes);
  } catch (err) {
    error.value = err?.message || "Không thể tải dữ liệu thuộc tính.";
  } finally {
    loading.value = false;
  }
}

function resetForms() {
  colorForm.value = { name: "", hexCode: "" };
  sizeForm.value = { name: "" };
  editingId.value = null;
  modalError.value = "";
}

function openCreateColor() {
  resetForms();
  modalType.value = "color";
  showModal.value = true;
}

function openEditColor(color) {
  resetForms();
  modalType.value = "color";
  editingId.value = color.id;
  colorForm.value = {
    name: color.name || "",
    hexCode: color.hexCode || color.hexCode1 || "",
  };
  showModal.value = true;
}

function openCreateSize() {
  resetForms();
  modalType.value = "size";
  showModal.value = true;
}

function openEditSize(size) {
  resetForms();
  modalType.value = "size";
  editingId.value = size.id;
  sizeForm.value = { name: size.name || "" };
  showModal.value = true;
}

function closeModal() {
  showModal.value = false;
  resetForms();
}

function validateForm() {
  if (modalType.value === "color") {
    if (!colorForm.value.name.trim()) {
      modalError.value = "Vui lòng nhập tên màu.";
      return false;
    }
    return true;
  }
  if (!sizeForm.value.name.trim()) {
    modalError.value = "Vui lòng nhập tên size.";
    return false;
  }
  return true;
}

function requestSave() {
  modalError.value = "";
  if (!validateForm()) return;

  const isEdit = Boolean(editingId.value);
  const label = modalType.value === "color" ? "màu" : "size";
  const name =
    modalType.value === "color"
      ? colorForm.value.name.trim()
      : sizeForm.value.name.trim();

  confirmModal.value = {
    title: isEdit ? "Xác nhận cập nhật" : "Xác nhận thêm mới",
    message: isEdit
      ? `Bạn có chắc muốn cập nhật ${label} "${name}"?`
      : `Bạn có chắc muốn thêm ${label} "${name}"?`,
    confirmText: isEdit ? "Cập nhật" : "Thêm mới",
    danger: false,
    loading: false,
    action: saveItem,
  };
}

async function saveItem() {
  if (modalType.value === "color") {
    const payload = {
      name: colorForm.value.name.trim(),
      hexCode: colorForm.value.hexCode.trim() || null,
    };
    if (editingId.value) {
      await colorsApi.update(editingId.value, payload);
    } else {
      await colorsApi.create(payload);
    }
  } else {
    const payload = { name: sizeForm.value.name.trim() };
    if (editingId.value) {
      await sizesApi.update(editingId.value, payload);
    } else {
      await sizesApi.create(payload);
    }
  }
}

async function executeConfirm() {
  if (!confirmModal.value?.action) return;
  confirmModal.value.loading = true;
  try {
    await confirmModal.value.action();
    confirmModal.value = null;
    closeModal();
    await fetchAll();
  } catch (err) {
    if (confirmModal.value) {
      confirmModal.value.loading = false;
    }
    modalError.value = err?.message || "Không thể thực hiện thao tác.";
    if (!showModal.value) {
      error.value = modalError.value;
    }
  }
}

function requestDeleteColor(color) {
  confirmModal.value = {
    title: "Xác nhận xóa",
    message: `Bạn chắc chắn muốn xóa màu "${color.name}" (#${color.id})?`,
    confirmText: "Xóa",
    danger: true,
    loading: false,
    action: async () => {
      await colorsApi.delete(color.id);
    },
  };
}

function requestDeleteSize(size) {
  confirmModal.value = {
    title: "Xác nhận xóa",
    message: `Bạn chắc chắn muốn xóa size "${size.name}" (#${size.id})?`,
    confirmText: "Xóa",
    danger: true,
    loading: false,
    action: async () => {
      await sizesApi.delete(size.id);
    },
  };
}

onMounted(fetchAll);
</script>
