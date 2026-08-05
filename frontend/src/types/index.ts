// Shared TypeScript types mirroring the backend DTOs (see backend `dto` packages).
// Keeping these in one place makes it easy to see the full API surface at a glance.

export type Role = "USER" | "HOST" | "ADMIN";

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  fieldErrors?: Record<string, string>;
}

// ---------------------------------------------------------------------------
// Auth / users
// ---------------------------------------------------------------------------

export interface UserResponse {
  id: number;
  fullName: string;
  email: string;
  phone?: string;
  role: Role;
  avatarUrl?: string;
  bio?: string;
  enabled: boolean;
  createdAt: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresInMs: number;
  user: UserResponse;
}

export interface RegisterRequest {
  fullName: string;
  email: string;
  password: string;
  phone?: string;
  role?: "USER" | "HOST";
}

export interface LoginRequest {
  email: string;
  password: string;
}

// ---------------------------------------------------------------------------
// Properties
// ---------------------------------------------------------------------------

export type PropertyType =
  | "APARTMENT"
  | "HOUSE"
  | "VILLA"
  | "CABIN"
  | "CONDO"
  | "STUDIO"
  | "COTTAGE"
  | "FARM_STAY";

export type RoomType = "ENTIRE_PLACE" | "PRIVATE_ROOM" | "SHARED_ROOM";
export type PropertyStatus = "ACTIVE" | "INACTIVE";

export interface Amenity {
  id: number;
  name: string;
  icon?: string;
}

export interface PropertyImage {
  id: number;
  url: string;
  displayOrder: number;
}

export interface PropertySummary {
  id: number;
  title: string;
  propertyType: PropertyType;
  city: string;
  country: string;
  pricePerNight: number;
  maxGuests: number;
  avgRating: number;
  reviewCount: number;
  status: PropertyStatus;
  coverImageUrl?: string;
}

export interface PropertyDetail {
  id: number;
  host: { id: number; fullName: string; avatarUrl?: string };
  title: string;
  description?: string;
  propertyType: PropertyType;
  roomType: RoomType;
  addressLine?: string;
  city: string;
  state?: string;
  country: string;
  zipCode?: string;
  latitude?: number;
  longitude?: number;
  pricePerNight: number;
  cleaningFee: number;
  maxGuests: number;
  bedrooms: number;
  beds: number;
  bathrooms: number;
  avgRating: number;
  reviewCount: number;
  status: PropertyStatus;
  aiGeneratedDescription: boolean;
  images: PropertyImage[];
  amenities: Amenity[];
  createdAt: string;
}

export interface PropertyRequest {
  title: string;
  description?: string;
  propertyType: PropertyType;
  roomType: RoomType;
  addressLine?: string;
  city: string;
  state?: string;
  country: string;
  zipCode?: string;
  latitude?: number;
  longitude?: number;
  pricePerNight: number;
  cleaningFee?: number;
  maxGuests: number;
  bedrooms?: number;
  beds?: number;
  bathrooms?: number;
  amenityIds?: number[];
}

export interface AvailabilityBlock {
  id: number;
  startDate: string;
  endDate: string;
  reason: "BOOKED" | "BLOCKED";
  bookingId?: number;
}

export interface PropertySearchParams {
  city?: string;
  country?: string;
  checkIn?: string;
  checkOut?: string;
  guests?: number;
  minPrice?: number;
  maxPrice?: number;
  propertyType?: string;
  roomType?: string;
  amenityIds?: number[];
  keyword?: string;
  page?: number;
  size?: number;
}

// ---------------------------------------------------------------------------
// Bookings
// ---------------------------------------------------------------------------

export type BookingStatus = "PENDING" | "CONFIRMED" | "CANCELLED" | "COMPLETED";

export interface Booking {
  id: number;
  propertyId: number;
  propertyTitle: string;
  propertyCity: string;
  propertyCoverImageUrl?: string;
  guestId: number;
  guestName: string;
  checkIn: string;
  checkOut: string;
  guestsCount: number;
  nights: number;
  pricePerNight: number;
  cleaningFee: number;
  totalPrice: number;
  status: BookingStatus;
  cancellationReason?: string;
  cancelledAt?: string;
  createdAt: string;
}

export interface BookingRequest {
  propertyId: number;
  checkIn: string;
  checkOut: string;
  guestsCount: number;
}

// ---------------------------------------------------------------------------
// Reviews
// ---------------------------------------------------------------------------

export interface Review {
  id: number;
  bookingId: number;
  propertyId: number;
  userId: number;
  userName: string;
  userAvatarUrl?: string;
  rating: number;
  comment?: string;
  imageUrls: string[];
  createdAt: string;
}

export interface ReviewRequest {
  bookingId: number;
  rating: number;
  comment?: string;
}

// ---------------------------------------------------------------------------
// AI features
// ---------------------------------------------------------------------------

export interface RecommendationResponse {
  summary: string;
  properties: PropertySummary[];
}

export interface ReviewSummaryResponse {
  propertyId: number;
  reviewCount: number;
  summary: string;
}

export interface ChatMessageDto {
  role: "USER" | "ASSISTANT";
  content: string;
  createdAt: string;
}

export interface ChatResponse {
  sessionId: string;
  reply: string;
}

export interface TripPlanRequest {
  destination: string;
  startDate: string;
  endDate: string;
  travelers: number;
  interests?: string;
  budgetLevel?: string;
}

export interface TripPlanResponse {
  destination: string;
  itinerary: string;
}

export interface DescriptionGenerateRequest {
  title: string;
  propertyType: PropertyType;
  roomType: RoomType;
  city: string;
  country: string;
  bedrooms: number;
  beds: number;
  bathrooms?: number;
  maxGuests: number;
  amenities?: string[];
  pricePerNight?: number;
  applyToPropertyId?: number;
}

export interface DescriptionGenerateResponse {
  description: string;
  appliedToProperty: boolean;
}

export interface BudgetPlanRequest {
  destination: string;
  travelers: number;
  nights: number;
  totalBudget: number;
  currency?: string;
}

export interface BudgetPlanResponse {
  destination: string;
  avgNightlyPriceReference?: number;
  plan: string;
}

export interface SmartSearchResponse {
  interpretedFilters: PropertySearchParams;
  results: PageResponse<PropertySummary>;
}

// ---------------------------------------------------------------------------
// Admin
// ---------------------------------------------------------------------------

export interface DashboardStats {
  totalUsers: number;
  totalGuests: number;
  totalHosts: number;
  totalAdmins: number;
  totalProperties: number;
  activeProperties: number;
  inactiveProperties: number;
  totalBookings: number;
  confirmedBookings: number;
  cancelledBookings: number;
  completedBookings: number;
  totalRevenue: number;
  totalReviews: number;
}

export interface BookingAnalytics {
  totalBookings: number;
  pending: number;
  confirmed: number;
  cancelled: number;
  completed: number;
  totalRevenue: number;
  monthlyStats: { month: string; bookingCount: number; revenue: number }[];
}

export interface AiFeatureStat {
  feature: string;
  totalCalls: number;
  successCount: number;
  failureCount: number;
  avgLatencyMs: number;
}

export interface AiUsageStats {
  totalCalls: number;
  successCount: number;
  failureCount: number;
  perFeature: AiFeatureStat[];
}
