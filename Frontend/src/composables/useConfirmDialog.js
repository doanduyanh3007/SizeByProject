import { ref } from "vue";

export function useConfirmDialog() {
  const confirmModal = ref(null);

  /**
   * Mở hộp thoại xác nhận
   * @param {Object} options
   * @param {string} [options.title] Tiêu đề modal
   * @param {string} [options.message] Nội dung xác nhận
   * @param {string} [options.confirmText] Nhãn nút xác nhận
   * @param {string} [options.cancelText] Nhãn nút hủy
   * @param {boolean} [options.danger] Thao tác nguy hiểm (nút màu đỏ)
   * @param {Function} [options.action] Hàm async hoặc sync được gọi khi xác nhận
   * @param {Function} [options.onConfirm] Tương đương action
   */
  function requestConfirm(options = {}) {
    confirmModal.value = {
      title: options.title || "Xác nhận thao tác",
      message: options.message || "Bạn có chắc chắn muốn thực hiện thao tác này?",
      confirmText: options.confirmText || "Xác nhận",
      cancelText: options.cancelText || "Hủy",
      danger: Boolean(options.danger),
      loading: false,
      action: options.action || options.onConfirm || null,
    };
  }

  /**
   * Thực thi hàm hành động đã đăng ký và tự động cập nhật trạng thái loading
   */
  async function executeConfirm() {
    if (!confirmModal.value) return;

    const action = confirmModal.value.action;
    if (typeof action === "function") {
      confirmModal.value.loading = true;
      try {
        await action();
        confirmModal.value = null;
      } catch (err) {
        if (confirmModal.value) {
          confirmModal.value.loading = false;
        }
        throw err;
      }
    } else {
      confirmModal.value = null;
    }
  }

  /**
   * Hủy bỏ / đóng modal
   */
  function cancelConfirm() {
    confirmModal.value = null;
  }

  return {
    confirmModal,
    requestConfirm,
    executeConfirm,
    cancelConfirm,
  };
}
