import axios, { AxiosError, type AxiosInstance, type InternalAxiosRequestConfig } from "axios";
import { API_CONFIG } from "./config";

/**
 * Axios instance with JWT + refresh-token interceptors.
 * All Spring Boot REST calls go through this client.
 */
export const apiClient: AxiosInstance = axios.create({
  baseURL: API_CONFIG.baseURL,
  timeout: API_CONFIG.timeout,
  headers: { "Content-Type": "application/json" },
});

// ---------- Token helpers (browser-only) ----------
export const tokenStore = {
  get access() {
    if (typeof window === "undefined") return null;
    return window.localStorage.getItem(API_CONFIG.tokenStorageKey);
  },
  get refresh() {
    if (typeof window === "undefined") return null;
    return window.localStorage.getItem(API_CONFIG.refreshStorageKey);
  },
  set(access: string, refresh?: string) {
    if (typeof window === "undefined") return;
    window.localStorage.setItem(API_CONFIG.tokenStorageKey, access);
    if (refresh) window.localStorage.setItem(API_CONFIG.refreshStorageKey, refresh);
  },
  clear() {
    if (typeof window === "undefined") return;
    window.localStorage.removeItem(API_CONFIG.tokenStorageKey);
    window.localStorage.removeItem(API_CONFIG.refreshStorageKey);
  },
};

// ---------- Request: attach bearer token ----------
apiClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = tokenStore.access;
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// ---------- Response: global error + refresh flow ----------
let refreshing: Promise<string | null> | null = null;

async function refreshAccessToken(): Promise<string | null> {
  const refresh = tokenStore.refresh;
  if (!refresh) return null;
  try {
    const { data } = await axios.post(`${API_CONFIG.baseURL}/auth/refresh`, {
      refreshToken: refresh,
    });
    tokenStore.set(data.accessToken, data.refreshToken);
    return data.accessToken as string;
  } catch {
    tokenStore.clear();
    return null;
  }
}

apiClient.interceptors.response.use(
  (r) => r,
  async (error: AxiosError) => {
    const original = error.config as InternalAxiosRequestConfig & { _retry?: boolean };
    if (error.response?.status === 401 && !original?._retry) {
      original._retry = true;
      refreshing ??= refreshAccessToken().finally(() => {
        refreshing = null;
      });
      const newToken = await refreshing;
      if (newToken && original.headers) {
        original.headers.Authorization = `Bearer ${newToken}`;
        return apiClient(original);
      }
      if (typeof window !== "undefined") window.location.href = "/auth/login";
    }
    return Promise.reject(error);
  },
);
