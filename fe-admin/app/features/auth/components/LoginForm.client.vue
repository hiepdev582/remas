<script setup lang="ts">
import type { LoginRequest } from "../types";
import { authFieldLabels, authFieldNames, authRoles } from "../constants";
import { loginFieldSchema } from "../validation";

//#region Common
const authStore = useAuthStore();
const authService = useAuthService();
const isLoading = ref(false);
//#endregion

//#region State
const loginFields: FormFieldConfig[] = [
  {
    name: authFieldNames.username,
    label: authFieldLabels.username,
    type: FormFieldType.TEXT,
    placeholder: placeholders.enter(authFieldLabels.username),
  },
  {
    name: authFieldNames.password,
    label: authFieldLabels.password,
    type: FormFieldType.PASSWORD,
    placeholder: placeholders.enter(authFieldLabels.password),
  },
];

const loginSchema = toTypedSchema(loginFieldSchema.getSchema());
//#endregion

//#region Services
const handleLogin = async (values: LoginRequest) => {
  isLoading.value = true;

  try {
    const response = await authService.login(values);
    authStore.setAuth(response);

    if (
      response.roles.includes(authRoles.admin) ||
      response.roles.includes(authRoles.staff)
    ) {
      message.success("Welcome back " + response.username);
      await navigateTo(ROUTES.DASHBOARD);
    } else {
      authStore.clearAuth();
      message.error("You do not have permission to login to admin page");
    }
  } catch (error: any) {
    const apiMessage = error.response?._data?.message || "Login failed";
    message.error(apiMessage);
  } finally {
    isLoading.value = false;
  }
};
//#endregion
</script>

<template>
  <div
    class="w-full max-w-md p-8 bg-white rounded-xl shadow-lg border border-gray-100"
  >
    <div class="text-center mb-8">
      <h2 class="text-3xl font-bold text-gray-900 tracking-tight">
        REMAS ADMIN
      </h2>
      <p class="mt-2 text-sm text-gray-500">REntal MAnagement System</p>
    </div>

    <BaseForm
      submit-button-text="Login"
      :fields="loginFields"
      :validation-schema="loginSchema"
      :loading="isLoading"
      @onSubmit="handleLogin"
    />
  </div>
</template>
