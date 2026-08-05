import { api } from "./client";
import type { ApiResponse, AuthResponse, LoginRequest, RegisterRequest, UserResponse } from "../types";

export const authApi = {
  register: (payload: RegisterRequest) =>
    api.post<ApiResponse<AuthResponse>>("/auth/register", payload).then((r) => r.data.data),

  login: (payload: LoginRequest) =>
    api.post<ApiResponse<AuthResponse>>("/auth/login", payload).then((r) => r.data.data),

  logout: (refreshToken: string) => api.post("/auth/logout", { refreshToken }),

  me: () => api.get<ApiResponse<UserResponse>>("/auth/me").then((r) => r.data.data),

  updateProfile: (payload: { fullName?: string; phone?: string; avatarUrl?: string; bio?: string }) =>
    api.put<ApiResponse<UserResponse>>("/users/me", payload).then((r) => r.data.data),

  changePassword: (payload: { currentPassword: string; newPassword: string }) =>
    api.put<ApiResponse<void>>("/users/me/password", payload).then((r) => r.data),
};
