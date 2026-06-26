<script setup lang="ts">
import type { BaseInputProps } from "~/components/base/Input.vue";
import { upsertUserFieldSchema } from "../validation";
import type { UserRequest } from "../types";

const props = defineProps<{
  id?: number;
  state: FormState;
}>();

const emit = defineEmits<{ (e: "onSuccess"): void }>();

const isLoading = ref(false);
const userService = useUserService();

const isOpen = defineModel<boolean>();
const formRef = ref<any>(null);

const handleConfirm = () => {
  formRef.value?.onSubmit();
};

//#region State
const upsertUserFields = computed<any[]>(() => [
  {
    type: FormFieldType.TEXT,
    name: userFieldNames.fullName,
    label: userFieldLabels.fullName,
    config: {
      autocomplete: "new-password",
      placeholder: placeholders.enter(userFieldLabels.fullName),
      disabled: props.state === FormState.VIEW,
    } as BaseInputProps,
  },
  {
    type: FormFieldType.TEXT,
    name: userFieldNames.email,
    label: userFieldLabels.email,
    config: {
      autocomplete: "new-password",
      placeholder: placeholders.enter(userFieldLabels.email),
      disabled: props.state === FormState.VIEW,
    } as BaseInputProps,
  },
  {
    type: FormFieldType.TEXT,
    name: userFieldNames.username,
    label: userFieldLabels.username,
    required: true,
    config: {
      autocomplete: "new-password",
      placeholder: placeholders.enter(userFieldLabels.username),
      disabled:
        props.state === FormState.VIEW || props.state === FormState.EDIT,
    } as BaseInputProps,
  },
  {
    type: FormFieldType.PASSWORD,
    name: userFieldNames.password,
    label: userFieldLabels.password,
    required: props.state === FormState.ADD,
    config: {
      autocomplete: "new-password",
      placeholder:
        props.state === FormState.ADD
          ? placeholders.enter(userFieldLabels.password)
          : "Leave blank to keep current password",
      disabled: props.state === FormState.VIEW,
    } as any,
  },
  {
    type: FormFieldType.SELECT,
    name: userFieldNames.isActive,
    label: userFieldLabels.isActive,
    required: true,
    config: {
      placeholder: placeholders.select(userFieldLabels.isActive),
      options: [
        { label: "Active", value: true },
        { label: "Inactive", value: false },
      ],
      disabled: props.state === FormState.VIEW,
    } as any,
  },
  {
    type: FormFieldType.SELECT,
    name: userFieldNames.roles,
    label: userFieldLabels.roles,
    required: true,
    config: {
      mode: "multiple",
      placeholder: placeholders.select(userFieldLabels.roles),
      options: [
        { label: "Admin", value: authRoles.admin },
        { label: "Customer", value: authRoles.customer },
      ],
      disabled: props.state === FormState.VIEW,
    } as any,
  },
]);

const upsertUserSchema = computed(() =>
  toTypedSchema(upsertUserFieldSchema.getSchema(props.state === FormState.EDIT)),
);
//#endregion

//#region Services
const formInfo = computed(() => {
  return {
    modalTitle: props.state === FormState.ADD ? "Add User" : "Edit User",
    successMessage:
      props.state === FormState.ADD
        ? "Add user successfully!"
        : "Edit user successfully!",
    errorMessage:
      props.state === FormState.ADD ? "Add user failed!" : "Edit user failed!",
  };
});

const handleConfirmUpsertUser = async (values: any) => {
  isLoading.value = true;

  try {
    const data: UserRequest = {
      username: values.username,
      password: values.password || undefined,
      email: values.email || undefined,
      fullName: values.fullName || undefined,
      isActive: values.isActive ?? false,
      roles: values.roles || [],
    };

    if (props.state === FormState.ADD) {
      await userService.add(data);
      toast.success(formInfo.value.successMessage);
      emit("onSuccess");
    } else if (props.id) {
      await userService.edit(props.id, data);
      toast.success(formInfo.value.successMessage);
      emit("onSuccess");
    } else {
      toast.errorOccured();
    }
    isOpen.value = false;
  } catch (error: any) {
    const apiMessage =
      error.response?._data?.message || formInfo.value.errorMessage;
    toast.error(apiMessage);
  } finally {
    isLoading.value = false;
  }
};

watch(
  () => isOpen.value,
  async () => {
    if (!isOpen.value) return;

    formRef.value?.resetForm();

    if (props.state !== FormState.ADD && props.id) {
      const user = await userService.get(props.id);
      formRef.value?.setValues({
        ...user,
        password: "",
      });
    } else {
      formRef.value?.setValues({
        isActive: true,
        roles: [authRoles.customer],
      });
    }
  },
);
//#endregion
</script>

<template>
  <BaseModal
    v-model="isOpen"
    wrapClassName="upsert-user-wrapper"
    confirmText="Save"
    :confirmLoading="isLoading"
    :title="formInfo.modalTitle"
    @onConfirm="handleConfirm"
  >
    <BaseForm
      ref="formRef"
      hide-submit-button
      :fields="upsertUserFields"
      :validation-schema="upsertUserSchema"
      @onSubmit="handleConfirmUpsertUser"
    />
  </BaseModal>
</template>
