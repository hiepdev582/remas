import { authRoles } from "~/features/auth/constants";

export const usePermission = () => {
  const authStore = useAuthStore();

  const hasRole = (role: string | string[]) => {
    const rolesStr = authStore.adminInfo?.roles;
    if (!rolesStr) return false;

    // Phóng tránh trường hợp roles lưu dạng mảng hoặc chuỗi phân tách bởi dấu phẩy
    const rolesList = typeof rolesStr === "string"
      ? rolesStr.split(",").map((r) => r.trim())
      : Array.isArray(rolesStr)
        ? rolesStr
        : [rolesStr];

    if (Array.isArray(role)) {
      return role.some((r) => rolesList.includes(r));
    }
    return rolesList.includes(role);
  };

  const isSuperAdmin = computed(() => hasRole(authRoles.superAdmin));
  const isAdmin = computed(() => hasRole(authRoles.admin));

  return {
    hasRole,
    isSuperAdmin,
    isAdmin,
  };
};
