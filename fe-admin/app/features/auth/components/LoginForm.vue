<script setup lang="ts">
import type { BaseInputProps } from "~/components/base/Input.vue";
import type { LoginRequest } from "../types";
import { loginFieldSchema } from "../validation";
import type { BaseInputPasswordProps } from "~/components/base/InputPassword.vue";

//#region Common
const authStore = useAuthStore();
const authService = useAuthService();
const isLoading = ref(false);
//#endregion

//#region State
const loginFields = computed<FormFieldConfig[]>(() => [
  {
    type: FormFieldType.TEXT,
    name: authFieldNames.username,
    label: authFieldLabels.username,
    required: true,
    config: {
      autocomplete: "new-password",
      placeholder: placeholders.enter(authFieldLabels.username),
    } as BaseInputProps,
  },
  {
    type: FormFieldType.PASSWORD,
    name: authFieldNames.password,
    label: authFieldLabels.password,
    required: true,
    config: {
      autocomplete: "new-password",
      placeholder: placeholders.enter(authFieldLabels.password),
    } as BaseInputPasswordProps,
  },
]);

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
      response.roles.includes(authRoles.superAdmin)
    ) {
      toast.success("Welcome back, " + response.username);
      await navigateTo(ROUTES.DASHBOARD);
    } else {
      authStore.clearAuth();
      toast.error("You do not have permission to login to admin page!");
    }
  } catch (error: any) {
    const apiMessage = error.response?._data?.message || "Login failed!";
    toast.error(apiMessage);
  } finally {
    isLoading.value = false;
  }
};
//#endregion

//#region Show error message
const route = useRoute();

onMounted(() => {
  if (route.query.error) {
    toast.error(route.query.error as string);
    navigateTo({ path: route.path, query: {} }, { replace: true });
  }
});
//#endregion
</script>

<template>
  <AuthLayout
    subtitle="Login to Rental Management System"
    redirect-text="Don't have an account?"
    redirect-button-text="Register"
    :redirect-link="ROUTES.AUTH.REGISTER"
  >
    <template #form>
      <BaseForm
        submit-button-text="Login"
        :fields="loginFields"
        :validation-schema="loginSchema"
        :loading="isLoading"
        @onSubmit="handleLogin"
      />
    </template>
  </AuthLayout>
</template>
