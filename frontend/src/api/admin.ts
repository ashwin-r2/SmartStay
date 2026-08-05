import { api } from "./client";
import type {
  AiUsageStats,
  ApiResponse,
  BookingAnalytics,
  DashboardStats,
  PageResponse,
  PropertySummary,
  Role,
  UserResponse,
} from "../types";

export const adminApi = {
  dashboard: () => api.get<ApiResponse<DashboardStats>>("/admin/dashboard").then((r) => r.data.data),

  users: (role?: Role, page = 0, size = 20) =>
    api
      .get<ApiResponse<PageResponse<UserResponse>>>("/admin/users", { params: { role, page, size } })
      .then((r) => r.data.data),

  setUserEnabled: (id: number, enabled: boolean) =>
    api.patch<ApiResponse<UserResponse>>(`/admin/users/${id}/enabled`, null, { params: { enabled } }).then((r) => r.data.data),

  changeUserRole: (id: number, role: Role) =>
    api.patch<ApiResponse<UserResponse>>(`/admin/users/${id}/role`, null, { params: { role } }).then((r) => r.data.data),

  deleteUser: (id: number) => api.delete(`/admin/users/${id}`),

  properties: (page = 0, size = 20) =>
    api
      .get<ApiResponse<PageResponse<PropertySummary>>>("/admin/properties", { params: { page, size } })
      .then((r) => r.data.data),

  deleteProperty: (id: number) => api.delete(`/admin/properties/${id}`),

  bookingAnalytics: () => api.get<ApiResponse<BookingAnalytics>>("/admin/bookings/analytics").then((r) => r.data.data),

  aiUsage: () => api.get<ApiResponse<AiUsageStats>>("/admin/ai/usage").then((r) => r.data.data),
};
