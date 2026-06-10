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
  <section
    class="select-none relative w-full h-screen overflow-hidden flex items-center justify-center bg-gradient-to-br from-[#0e2450] via-primary to-[#07132c]"
  >
    <!-- Background Effects -->
    <div
      class="absolute top-[-10%] left-[-10%] w-[50%] h-[50%] rounded-full bg-white/10 blur-[120px] pointer-events-none"
    ></div>
    <div
      class="absolute bottom-[-10%] right-[-10%] w-[50%] h-[50%] rounded-full bg-[#17ba98]/10 blur-[120px] pointer-events-none"
    ></div>

    <!-- Login Card -->
    <div
      class="z-10 w-[400px] max-w-[90%] max-h-[90%] bg-white rounded shadow-[0_25px_50px_-12px_rgba(0,0,0,0.3)] border border-gray-100 p-8 transition-all duration-300 hover:shadow-[0_30px_60px_-10px_rgba(0,0,0,0.4)] hover:-translate-y-1"
    >
      <section class="text-center mb-8">
        <div
          class="inline-flex items-center justify-center w-16 h-w-16 rounded-full shadow-md shadow-primary/20 mb-4"
        >
          <NuxtImg
            src="/images/logo_circle.png"
            class="w-full h-full object-cover"
          />
        </div>
        <h1 class="text-2xl font-extrabold tracking-tight">REMAS ADMIN</h1>
        <p class="mt-2 text-xs font-semibold">Rental Item Management System</p>
      </section>

      <!-- Form -->
      <BaseForm
        submit-button-text="Login"
        :fields="loginFields"
        :validation-schema="loginSchema"
        :loading="isLoading"
        @onSubmit="handleLogin"
      />
    </div>
  </section>
</template>
