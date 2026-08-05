import axios, { AxiosError, type InternalAxiosRequestConfig } from "axios";
import type { ApiErrorResponse, ApiResponse, AuthResponse } from "../types";

const ACCESS_TOKEN_KEY = "staysmart_access_token";
const REFRESH_TOKEN_KEY = "staysmart_refresh_token";

export const tokenStorage = {
  getAccessToken: () => localStorage.getItem(ACCESS_TOKEN_KEY),
  getRefreshToken: () => localStorage.getItem(REFRESH_TOKEN_KEY),
  setTokens: (accessToken: string, refreshToken: string) => {
    localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
  },
  clear: () => {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
  },
};

const baseURL = import.meta.env.VITE_API_BASE_URL ?? "/api";

export const api = axios.create({ baseURL });

api.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = tokenStorage.getAccessToken();
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// A plain axios instance (no interceptors) used only for the refresh call itself, so a
// failing refresh never recurses back into the 401-handling logic below.
const refreshClient = axios.create({ baseURL });

let refreshPromise: Promise<string | null> | null = null;

async function refreshAccessToken(): Promise<string | null> {
  const refreshToken = tokenStorage.getRefreshToken();
  if (!refreshToken) return null;

  if (!refreshPromise) {
    refreshPromise = refreshClient
      .post<ApiResponse<AuthResponse>>("/auth/refresh", { refreshToken })
      .then((res) => {
        const { accessToken, refreshToken: newRefreshToken } = res.data.data;
        tokenStorage.setTokens(accessToken, newRefreshToken);
        return accessToken;
      })
      .catch(() => {
        tokenStorage.clear();
        return null;
      })
      .finally(() => {
        refreshPromise = null;
      });
  }
  return refreshPromise;
}

api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError<ApiErrorResponse>) => {
    const originalRequest = error.config as (InternalAxiosRequestConfig & { _retry?: boolean }) | undefined;

    if (error.response?.status === 401 && originalRequest && !originalRequest._retry
      && !originalRequest.url?.includes("/auth/")) {
      originalRequest._retry = true;
      const newToken = await refreshAccessToken();
      if (newToken) {
        originalRequest.headers = originalRequest.headers ?? {};
        originalRequest.headers.Authorization = `Bearer ${newToken}`;
        return api(originalRequest);
      }
      window.dispatchEvent(new CustomEvent("staysmart:unauthorized"));
    }
    return Promise.reject(error);
  }
);

/** Extracts a human-readable message from any Axios/API error, for toast/inline display. */
export function extractErrorMessage(error: unknown, fallback = "Something went wrong. Please try again."): string {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data as ApiErrorResponse | undefined;
    if (data?.fieldErrors && Object.keys(data.fieldErrors).length > 0) {
      return Object.values(data.fieldErrors).join(", ");
    }
    if (data?.message) return data.message;
  }
  if (error instanceof Error) return error.message;
  return fallback;
}
