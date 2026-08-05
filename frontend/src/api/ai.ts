import { api } from "./client";
import type {
  ApiResponse,
  BudgetPlanRequest,
  BudgetPlanResponse,
  ChatMessageDto,
  ChatResponse,
  DescriptionGenerateRequest,
  DescriptionGenerateResponse,
  RecommendationResponse,
  ReviewSummaryResponse,
  SmartSearchResponse,
  TripPlanRequest,
  TripPlanResponse,
} from "../types";

export const aiApi = {
  recommendations: (city?: string) =>
    api
      .get<ApiResponse<RecommendationResponse>>("/ai/recommendations", { params: { city } })
      .then((r) => r.data.data),

  reviewSummary: (propertyId: number) =>
    api.get<ApiResponse<ReviewSummaryResponse>>(`/ai/reviews/${propertyId}/summary`).then((r) => r.data.data),

  chat: (message: string, sessionId?: string) =>
    api.post<ApiResponse<ChatResponse>>("/ai/chat", { message, sessionId }).then((r) => r.data.data),

  chatHistory: (sessionId: string) =>
    api.get<ApiResponse<ChatMessageDto[]>>(`/ai/chat/${sessionId}/history`).then((r) => r.data.data),

  tripPlanner: (payload: TripPlanRequest) =>
    api.post<ApiResponse<TripPlanResponse>>("/ai/trip-planner", payload).then((r) => r.data.data),

  descriptionGenerator: (payload: DescriptionGenerateRequest) =>
    api.post<ApiResponse<DescriptionGenerateResponse>>("/ai/description-generator", payload).then((r) => r.data.data),

  budgetPlanner: (payload: BudgetPlanRequest) =>
    api.post<ApiResponse<BudgetPlanResponse>>("/ai/budget-planner", payload).then((r) => r.data.data),

  smartSearch: (query: string, page = 0, size = 20) =>
    api
      .post<ApiResponse<SmartSearchResponse>>("/ai/smart-search", { query }, { params: { page, size } })
      .then((r) => r.data.data),
};
