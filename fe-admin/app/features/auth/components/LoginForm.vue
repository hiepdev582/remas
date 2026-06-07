<script setup lang="ts">
import { useAuthStore } from "../store";
import type { AuthResponse } from "../types";
import * as zod from "zod";

const authStore = useAuthStore();
const isLoading = ref(false);

const loginSchema = zod.object({
  username: zod
    .string()
    .min(1, "Username is required")
    .min(3, "Username must be at least 3 characters long"),
  password: zod
    .string()
    .min(1, "Password is required")
    .min(6, "Password must be at least 6 characters long"),
});

const { value: username } = useField<string>("username");
const { value: password } = useField<string>("password");

const { handleSubmit, errors } = useForm({
  validationSchema: toTypedSchema(loginSchema),
});

const handleLogin = async (values) => {
  isLoading.value = true;

  try {
    const response = await $fetch<AuthResponse>(
      "http://localhost:8080/api/v1/auth/login",
      {
        method: "POST",
        body: values,
      },
    );

    authStore.setAuth(response);

    if (response.roles.includes("ADMIN") || response.roles.includes("STAFF")) {
      message.success("Welcome back " + response.username);
      await navigateTo("/");
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
</script>

<template></template>
