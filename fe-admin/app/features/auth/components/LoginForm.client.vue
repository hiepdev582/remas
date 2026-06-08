<script setup lang="ts">
import { toTypedSchema } from "@vee-validate/zod";
import type { LoginRequest } from "../types";
import * as zod from "zod";
import { authFieldLabels, authFieldNames, authRoles } from "../constants";

//#region Common
const authStore = useAuthStore();
const authService = useAuthService();
const isLoading = ref(false);
//#endregion

//#region Validation
const loginSchema = zod.object({
  username: zod
    .string()
    .min(1, errorMessages.required(authFieldLabels.username))
    .min(3, errorMessages.minLength(authFieldLabels.username, 3)),
  password: zod
    .string()
    .min(1, errorMessages.required(authFieldLabels.password))
    .min(6, errorMessages.minLength(authFieldLabels.password, 6)),
});

const { value: username } = useField<string>(authFieldNames.username);
const { value: password } = useField<string>(authFieldNames.password);

const { handleSubmit, errors } = useForm({
  validationSchema: toTypedSchema(loginSchema),
});
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
  <div>
    <BaseInputPassword></BaseInputPassword>
  </div>
</template>
