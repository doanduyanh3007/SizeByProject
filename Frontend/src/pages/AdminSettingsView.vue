<template>
  <AdminShell>
    <template #header>
      <div class="flex flex-wrap items-center justify-between gap-3">
        <div>
          <p class="text-xs uppercase tracking-[0.18em] text-primary">Hồ sơ admin</p>
          <h2 class="mt-1 text-2xl font-bold">Cài đặt tài khoản admin</h2>
        </div>
      </div>
    </template>

    <section class="retail-card p-6">
      <div v-if="message" class="mb-4 rounded-xl px-3 py-2 text-sm" :class="messageType === 'error' ? 'border border-red-200 bg-red-50 text-red-700 dark:border-red-900/40 dark:bg-red-900/20 dark:text-red-300' : 'border border-green-200 bg-green-50 text-green-700 dark:border-green-900/40 dark:bg-green-900/20 dark:text-green-300'">
        {{ message }}
      </div>

      <form class="grid gap-4 md:grid-cols-2" @submit.prevent="saveSettings">
        <div class="md:col-span-2 flex items-center gap-4 rounded-2xl border border-slate-200 p-4 dark:border-[#3c342e]">
          <div class="size-20 overflow-hidden rounded-full bg-slate-100 dark:bg-[#2b241f]">
            <img v-if="resolvedAvatar" :src="resolvedAvatar" alt="Admin avatar" class="h-full w-full object-cover" />
            <span v-else class="flex h-full items-center justify-center text-xs text-slate-500 dark:text-[#b9aa9a]">ADMIN</span>
          </div>

          <div>
            <p class="text-sm font-medium">Ảnh đại diện</p>
            <div class="mt-1 flex flex-wrap items-center gap-2">
              <button
                type="button"
                class="rounded-xl border border-slate-200 px-3 py-1.5 text-xs font-medium hover:border-primary hover:text-primary dark:border-[#3c342e]"
                @click="triggerAvatarUpload"
              >
                Chọn ảnh
              </button>

              <button
                v-if="resolvedAvatar"
                type="button"
                class="rounded-xl border border-red-200 px-3 py-1.5 text-xs font-medium text-red-600 hover:bg-red-50 dark:border-red-900/40 dark:text-red-400"
                @click="removeAvatar"
              >
                Xóa ảnh
              </button>
            </div>
            <p v-if="uploadError" class="mt-1 text-xs text-red-500">{{ uploadError }}</p>

            <input
              ref="fileInput"
              type="file"
              accept="image/*"
              class="hidden"
              @change="handleAvatarChange"
            />
          </div>
        </div>

        <label class="flex flex-col gap-2">
          <span class="text-sm font-medium">Tên hiển thị</span>
          <input v-model="form.username" type="text" class="rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm outline-none focus:border-primary dark:border-[#3c342e] dark:bg-[#1f1a17]" />
        </label>

        <label class="flex flex-col gap-2">
          <span class="text-sm font-medium">Email</span>
          <input v-model="form.gmail" type="email" class="rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm outline-none focus:border-primary dark:border-[#3c342e] dark:bg-[#1f1a17]" />
        </label>

        <label class="flex flex-col gap-2">
          <span class="text-sm font-medium">Số điện thoại</span>
          <input v-model="form.phone" type="text" class="rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm outline-none focus:border-primary dark:border-[#3c342e] dark:bg-[#1f1a17]" />
        </label>

        <label class="md:col-span-2 flex flex-col gap-2">
          <span class="text-sm font-medium">Địa chỉ</span>
          <textarea v-model="form.address" rows="3" class="rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm outline-none focus:border-primary dark:border-[#3c342e] dark:bg-[#1f1a17]" />
        </label>

        <div class="md:col-span-2 rounded-2xl border border-slate-200 p-4 dark:border-[#3c342e]">
          <p class="text-sm font-medium">Đổi mật khẩu</p>

          <div class="mt-3 grid gap-4 md:grid-cols-2">
            <label class="flex flex-col gap-2">
              <span class="text-sm font-medium">Mật khẩu mới</span>
              <input
                v-model="form.newPassword"
                type="password"
                autocomplete="new-password"
                class="rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm outline-none focus:border-primary dark:border-[#3c342e] dark:bg-[#1f1a17]"
              />
            </label>

            <label class="flex flex-col gap-2">
              <span class="text-sm font-medium">Xác nhận mật khẩu mới</span>
              <input
                v-model="form.confirmPassword"
                type="password"
                autocomplete="new-password"
                class="rounded-xl border border-slate-900 bg-gray-50 px-3 py-2 text-sm outline-none focus:border-primary dark:border-[#3c342e] dark:bg-[#1f1a17]"
              />
            </label>
          </div>
        </div>

        <div class="md:col-span-2 flex justify-end">
          <button
            type="submit"
            :disabled="saving"
            class="inline-flex items-center gap-2 rounded-xl bg-primary px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
          >
            <span class="material-symbols-outlined text-[18px]">save</span>
            {{ saving ? 'Đang lưu...' : 'Lưu thay đổi' }}
          </button>
        </div>
      </form>
    </section>
  </AdminShell>
</template>

<script setup>
import { getSession } from "@/utils/auth";
import { computed, onMounted, ref } from "vue";

import AdminShell from "@/components/admin/AdminShell.vue";
import { accountsApi, resolveBackendAssetUrl } from "@/services/api";

const API_BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080/api";

const saving = ref(false);
const message = ref("");
const messageType = ref("success");
const accountId = ref(null);

const fileInput = ref(null);
const avatarFile = ref(null);
const avatarPreview = ref("");
const uploadError = ref("");

const form = ref({
  username: "",
  gmail: "",
  phone: "",
  address: "",
  imgUrl: null,
  newPassword: "",
  confirmPassword: "",
});

const resolvedAvatar = computed(() => {
  return normalizeAvatarUrl(avatarPreview.value || form.value.imgUrl || "");
});

function normalizeAvatarUrl(rawUrl) {
  if (!rawUrl || typeof rawUrl !== "string") {
    return "";
  }

  const fixedUrl = rawUrl.replace(
    /^https?:\/\/[^/]+\/?(https?:\/\/.+)$/i,
    "$1",
  );

  return resolveBackendAssetUrl(fixedUrl) || "";
}

function getLoggedInUser() {
  try {
    return JSON.parse(getSession("user") || "{}");
  } catch {
    return {};
  }
}

function triggerAvatarUpload() {
  fileInput.value?.click();
}

function handleAvatarChange(event) {
  const file = event.target.files && event.target.files[0] ? event.target.files[0] : null;
  if (!file) {
    return;
  }

  if (!file.type.startsWith("image/")) {
    uploadError.value = "Vui lòng chọn tệp ảnh hợp lệ.";
    return;
  }

  if (file.size > 2 * 1024 * 1024) {
    uploadError.value = "Ảnh phải nhỏ hơn 2MB.";
    return;
  }

  uploadError.value = "";
  avatarFile.value = file;

  const reader = new FileReader();
  reader.onload = (e) => {
    avatarPreview.value = e.target && e.target.result ? String(e.target.result) : "";
  };
  reader.readAsDataURL(file);
}

function removeAvatar() {
  avatarFile.value = null;
  avatarPreview.value = "";
  form.value.imgUrl = null;
  uploadError.value = "";

  if (fileInput.value) {
    fileInput.value.value = "";
  }
}

async function uploadAvatar(currentAccountId) {
  if (!avatarFile.value) {
    return form.value.imgUrl || null;
  }

  const formData = new FormData();
  formData.append("file", avatarFile.value);

  const response = await fetch(`${API_BASE_URL}/accounts/${currentAccountId}/upload-avatar`, {
    method: "POST",
    body: formData,
  });

  let data = null;
  try {
    data = await response.json();
  } catch {
    data = null;
  }

  if (!response.ok) {
    const messageText =
      data && typeof data === "object" && (data.message || data.error)
        ? data.message || data.error
        : `Upload avatar thất bại (HTTP ${response.status})`;
    throw new Error(messageText);
  }

  const nextImageUrl =
    data && typeof data === "object"
      ? data.imageUrl || data.imgUrl || data.url || null
      : null;

  if (!nextImageUrl) {
    throw new Error("Máy chủ không trả về đường dẫn ảnh.");
  }

  return nextImageUrl;
}

async function loadSettings() {
  const user = getLoggedInUser();
  const id = Number(user.id);
  if (!Number.isFinite(id) || id <= 0) {
    message.value = "Không tìm thấy tài khoản đăng nhập.";
    messageType.value = "error";
    return;
  }

  accountId.value = id;

  try {
    const account = await accountsApi.getById(id);
    form.value = {
      username: account.username || "",
      gmail: account.gmail || "",
      phone: account.phone || "",
      address: account.address || "",
      imgUrl: account.imgUrl || null,
      newPassword: "",
      confirmPassword: "",
    };
  } catch (err) {
    console.error("Failed to load admin settings:", err);
    message.value = "Không thể tải thông tin tài khoản.";
    messageType.value = "error";
  }
}

async function saveSettings() {
  if (!accountId.value) {
    message.value = "Không tìm thấy tài khoản hợp lệ để cập nhật.";
    messageType.value = "error";
    return;
  }

  saving.value = true;
  message.value = "";

  try {
    const uploadedAvatarUrl = await uploadAvatar(accountId.value);
    const newPassword = String(form.value.newPassword || "");
    const confirmPassword = String(form.value.confirmPassword || "");

    if (newPassword || confirmPassword) {
      if (newPassword.length < 6) {
        throw new Error("Mật khẩu mới cần tối thiểu 6 ký tự.");
      }

      if (newPassword !== confirmPassword) {
        throw new Error("Xác nhận mật khẩu không khớp.");
      }
    }

    const payload = {
      username: form.value.username,
      gmail: form.value.gmail,
      phone: form.value.phone,
      address: form.value.address,
      imgUrl: uploadedAvatarUrl || null,
    };

    if (newPassword) {
      payload.password = newPassword;
    }

    const updated = await accountsApi.update(accountId.value, payload);

    const localUser = getLoggedInUser();
    const merged = {
      ...localUser,
      ...updated,
      imgUrl: updated.imgUrl || payload.imgUrl || null,
    };
    saveSession(merged);

    form.value.imgUrl = merged.imgUrl || null;
    form.value.newPassword = "";
    form.value.confirmPassword = "";
    avatarFile.value = null;
    avatarPreview.value = "";
    uploadError.value = "";

    if (fileInput.value) {
      fileInput.value.value = "";
    }

    message.value = "Cập nhật tài khoản thành công.";
    messageType.value = "success";
  } catch (err) {
    console.error("Failed to update admin settings:", err);
    message.value =
      err && typeof err === "object" && err.message
        ? err.message
        : "Không thể cập nhật tài khoản. Vui lòng thử lại.";
    messageType.value = "error";
  } finally {
    saving.value = false;
  }
}

onMounted(loadSettings);
</script>
