<script setup lang="ts">
import type { BaseInputProps } from "~/components/base/Input.vue";
import type { RegisterRequest } from "../types";
import { registerFieldSchema } from "../validation";
import type { BaseInputPasswordProps } from "~/components/base/InputPassword.vue";

//#region Common
const authService = useAuthService();
const isLoading = ref(false);
//#endregion

//#region State
const registerFields = computed<FormFieldConfig[]>(() => [
  {
    type: FormFieldType.TEXT,
    name: authFieldNames.fullName,
    label: authFieldLabels.fullName,
    config: {
      autocomplete: "new-password",
      placeholder: placeholders.enter(authFieldLabels.fullName),
    } as BaseInputProps,
  },
  {
    type: FormFieldType.TEXT,
    name: authFieldNames.email,
    label: authFieldLabels.email,
    config: {
      autocomplete: "new-password",
      placeholder: placeholders.enter(authFieldLabels.email),
    } as BaseInputProps,
  },
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

const registerSchema = toTypedSchema(registerFieldSchema.getSchema());
//#endregion

//#region Services
const handleRegister = async (values: RegisterRequest) => {
  isLoading.value = true;

  try {
    const response = await authService.register(values);
    toast.success(
      response ||
        "Registered successfully! Plase contact admin to activate your account.",
    );
    await navigateTo(ROUTES.AUTH.LOGIN);
  } catch (error: any) {
    const apiMessage = error.response?._data?.message || "Registration failed!";
    toast.error(apiMessage);
  } finally {
    isLoading.value = false;
  }
};
//#endregion
</script>

<template>
  <AuthLayout
    subtitle="Create a new account for REMAS"
    redirect-text="Already have an account?"
    redirect-button-text="Login"
    :redirect-link="ROUTES.AUTH.LOGIN"
  >
    <template #form>
      <BaseForm
        submit-button-text="Register"
        :fields="registerFields"
        :validation-schema="registerSchema"
        :loading="isLoading"
        @onSubmit="handleRegister"
      />
    </template>
  </AuthLayout>
</template>
