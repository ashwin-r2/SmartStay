import { api } from "./client";
import type { ApiResponse, Booking, BookingRequest, PageResponse } from "../types";

export const bookingsApi = {
  create: (payload: BookingRequest) =>
    api.post<ApiResponse<Booking>>("/bookings", payload).then((r) => r.data.data),

  getById: (id: number) => api.get<ApiResponse<Booking>>(`/bookings/${id}`).then((r) => r.data.data),

  cancel: (id: number, reason: string) =>
    api.put<ApiResponse<Booking>>(`/bookings/${id}/cancel`, { reason }).then((r) => r.data.data),

  myBookings: (page = 0, size = 10) =>
    api.get<ApiResponse<PageResponse<Booking>>>("/bookings/me", { params: { page, size } }).then((r) => r.data.data),

  byProperty: (propertyId: number, page = 0, size = 10) =>
    api
      .get<ApiResponse<PageResponse<Booking>>>(`/bookings/property/${propertyId}`, { params: { page, size } })
      .then((r) => r.data.data),

  myHostBookings: (page = 0, size = 10) =>
    api.get<ApiResponse<PageResponse<Booking>>>("/bookings/host/me", { params: { page, size } }).then((r) => r.data.data),
};
