/**
 * NovaBank API configuration.
 * Reads Vite env vars — never hardcode URLs.
 */
export const API_CONFIG = {
  baseURL: (import.meta.env.VITE_API_BASE_URL as string) ?? "http://localhost:8080/api/v1",
  timeout: 20_000,
  useMocks: (import.meta.env.VITE_USE_MOCKS as string) !== "false",
  tokenStorageKey: "novabank.access_token",
  refreshStorageKey: "novabank.refresh_token",
} as const;
