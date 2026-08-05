import { api } from "./client";
import type { ApiResponse, PageResponse, Review, ReviewRequest } from "../types";

export const reviewsApi = {
  create: (payload: ReviewRequest) => api.post<ApiResponse<Review>>("/reviews", payload).then((r) => r.data.data),

  update: (id: number, payload: ReviewRequest) =>
    api.put<ApiResponse<Review>>(`/reviews/${id}`, payload).then((r) => r.data.data),

  remove: (id: number) => api.delete(`/reviews/${id}`),

  uploadImages: (id: number, files: File[]) => {
    const form = new FormData();
    files.forEach((f) => form.append("files", f));
    return api
      .post<ApiResponse<string[]>>(`/reviews/${id}/images`, form, {
        headers: { "Content-Type": "multipart/form-data" },
      })
      .then((r) => r.data.data);
  },

  byProperty: (propertyId: number, page = 0, size = 10) =>
    api
      .get<ApiResponse<PageResponse<Review>>>(`/reviews/property/${propertyId}`, { params: { page, size } })
      .then((r) => r.data.data),

  eligibility: (bookingId: number) =>
    api.get<ApiResponse<boolean>>(`/reviews/eligibility/${bookingId}`).then((r) => r.data.data),
};
