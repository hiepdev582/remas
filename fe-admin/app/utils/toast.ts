import {
  message as antdMessage,
  notification as antdNotification,
} from "ant-design-vue";

export const toast = {
  success(content: string, duration = 3) {
    antdMessage.success(content, duration);
  },

  error(content: string, duration = 4) {
    antdMessage.error(content, duration);
  },

  errorOccured(duration = 4) {
    antdMessage.error("An error occurred! Please try again.", duration);
  },

  warning(content: string, duration = 3) {
    antdMessage.warning(content, duration);
  },

  info(content: string, duration = 3) {
    antdMessage.info(content, duration);
  },

  notify(
    title: string,
    description: string,
    type: ToastType = ToastType.INFO,
    duration = 4.5,
  ) {
    antdNotification[type]({
      message: title,
      description: description,
      duration: duration,
      placement: "topRight",
    });
  },
};
