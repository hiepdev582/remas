<script setup lang="ts">
import type { RegisterRequest } from "../types";
import { registerFieldSchema } from "../validation";

//#region Common
const authService = useAuthService();
const isLoading = ref(false);
//#endregion

//#region State
const registerFields: FormFieldConfig[] = [
  {
    name: authFieldNames.fullName,
    label: authFieldLabels.fullName,
    type: FormFieldType.TEXT,
    placeholder: placeholders.enter(authFieldLabels.fullName),
  },
  {
    name: authFieldNames.email,
    label: authFieldLabels.email,
    type: FormFieldType.TEXT,
    placeholder: placeholders.enter(authFieldLabels.email),
  },
  {
    name: authFieldNames.username,
    label: authFieldLabels.username,
    type: FormFieldType.TEXT,
    placeholder: placeholders.enter(authFieldLabels.username),
    required: true,
  },
  {
    name: authFieldNames.password,
    label: authFieldLabels.password,
    type: FormFieldType.PASSWORD,
    placeholder: placeholders.enter(authFieldLabels.password),
    required: true,
  },
];

const registerSchema = toTypedSchema(registerFieldSchema.getSchema());
//#endregion

//#region Services
const handleRegister = async (values: RegisterRequest) => {
  isLoading.value = true;

  try {
    const response = await authService.register(values);
    message.success(
      response ||
        "Registered successfully! Plase contact admin to activate your account.",
    );
    await navigateTo(ROUTES.AUTH.LOGIN);
  } catch (error: any) {
    const apiMessage = error.response?._data?.message || "Registration failed";
    message.error(apiMessage);
  } finally {
    isLoading.value = false;
  }
};
//#endregion
</script>

<template>
  <div
    class="select-none relative w-full h-screen overflow-hidden flex items-center justify-center bg-gradient-to-br from-[#0e2450] via-primary to-[#07132c]"
  >
    <!-- Background Effects -->
    <div
      class="absolute top-[-10%] left-[-10%] w-[50%] h-[50%] rounded-full bg-white/10 blur-[120px] pointer-events-none"
    ></div>
    <div
      class="absolute bottom-[-10%] right-[-10%] w-[50%] h-[50%] rounded-full bg-[#17ba98]/10 blur-[120px] pointer-events-none"
    ></div>

    <!-- Register Card -->
    <div
      class="relative z-10 w-[420px] max-w-[90%] bg-white rounded shadow-[0_25px_50px_-12px_rgba(0,0,0,0.3)] border border-gray-100 py-6 px-8 transition-all duration-300 hover:shadow-[0_30px_60px_-10px_rgba(0,0,0,0.4)] hover:-translate-y-1"
    >
      <div class="text-center mb-5">
        <div
          class="inline-flex items-center justify-center w-16 h-16 rounded-full shadow-md shadow-primary/20 mb-3"
        >
          <NuxtImg
            src="/images/logo_circle.png"
            class="w-full h-full object-cover"
          />
        </div>
        <h1 class="text-2xl font-extrabold tracking-tight">REMAS ADMIN</h1>
        <p class="mt-1 text-xs font-medium">Create a new account for REMAS</p>
      </div>

      <!-- Form -->
      <BaseForm
        submit-button-text="Register"
        :fields="registerFields"
        :validation-schema="registerSchema"
        :loading="isLoading"
        @onSubmit="handleRegister"
      />

      <!-- Redirect Link -->
      <div class="mt-4 text-center text-sm font-medium">
        Already have an account?
        <NuxtLink
          :to="ROUTES.AUTH.LOGIN"
          class="text-primary hover:text-primary-hover font-semibold transition-colors ml-[2px]"
        >
          Login
        </NuxtLink>
      </div>
    </div>
  </div>
</template>

<style lang="css" scoped>
:deep(.ant-form-item) {
  margin-bottom: 8px;
}
</style>
