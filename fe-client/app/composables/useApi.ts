export const useApi = () => {
  const config = useRuntimeConfig();
  const baseURL = config.public.apiBase as string;

  return $fetch.create({
    baseURL,
  });
};
