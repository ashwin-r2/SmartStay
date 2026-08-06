import { api } from "./client";
import type {
  ApiResponse,
  Amenity,
  AvailabilityBlock,
  PageResponse,
  PropertyDetail,
  PropertyRequest,
  PropertySearchParams,
  PropertyStatus,
  PropertySummary,
} from "../types";

export const propertiesApi = {
  search: (params: PropertySearchParams) =>
    api
      .get<ApiResponse<PageResponse<PropertySummary>>>("/properties/search", { params })
      .then((r) => r.data.data),

  getById: (id: number) => api.get<ApiResponse<PropertyDetail>>(`/properties/${id}`).then((r) => r.data.data),

  byHost: (hostId: number, page = 0, size = 20) =>
    api
      .get<ApiResponse<PageResponse<PropertySummary>>>(`/properties/host/${hostId}`, { params: { page, size } })
      .then((r) => r.data.data),

  create: (payload: PropertyRequest) =>
    api.post<ApiResponse<PropertyDetail>>("/properties", payload).then((r) => r.data.data),

  update: (id: number, payload: PropertyRequest) =>
    api.put<ApiResponse<PropertyDetail>>(`/properties/${id}`, payload).then((r) => r.data.data),

  setStatus: (id: number, status: PropertyStatus) =>
    api.patch<ApiResponse<PropertyDetail>>(`/properties/${id}/status`, null, { params: { status } }).then((r) => r.data.data),

  remove: (id: number) => api.delete(`/properties/${id}`),

  uploadImages: (id: number, files: File[]) => {
    const form = new FormData();
    files.forEach((f) => form.append("files", f));
    return api
      .post<ApiResponse<string[]>>(`/properties/${id}/images`, form, {
        headers: { "Content-Type": "multipart/form-data" },
      })
      .then((r) => r.data.data);
  },

  removeImage: (id: number, imageId: number) => api.delete(`/properties/${id}/images/${imageId}`),

  availability: (id: number) =>
    api.get<ApiResponse<AvailabilityBlock[]>>(`/properties/${id}/availability`).then((r) => r.data.data),

  blockDates: (id: number, startDate: string, endDate: string) =>
    api
      .post<ApiResponse<AvailabilityBlock>>(`/properties/${id}/availability`, { startDate, endDate })
      .then((r) => r.data.data),

  unblockDates: (id: number, blockId: number) => api.delete(`/properties/${id}/availability/${blockId}`),
};

export const amenitiesApi = {
  list: () => api.get<ApiResponse<Amenity[]>>("/amenities").then((r) => r.data.data),

  create: (payload: { name: string; icon?: string }) =>
    api.post<ApiResponse<Amenity>>("/amenities", payload).then((r) => r.data.data),

  update: (id: number, payload: { name: string; icon?: string }) =>
    api.put<ApiResponse<Amenity>>(`/amenities/${id}`, payload).then((r) => r.data.data),

  remove: (id: number) => api.delete(`/amenities/${id}`),
};
