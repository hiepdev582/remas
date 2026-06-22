import { createVNode } from "vue";
import { Modal } from "ant-design-vue";

export const confirmModal = {
  showDeleteConfirm: (
    title: string,
    content: string,
    onOk: () => void,
    icon?: string,
    onCancel?: () => void,
  ) => {
    Modal.confirm({
      icon: icon ? createVNode(icon) : undefined,
      title: title,
      content: content,
      okType: "danger",
      okText: "Yes",
      cancelText: "No",
      onOk() {
        onOk();
      },
      onCancel() {
        if (onCancel) {
          onCancel();
        }
      },
    });
  },
};
