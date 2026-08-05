# REST API Documentation

Base URL: `http://localhost:8080/api` (see [`DEPLOYMENT-GUIDE.md`](DEPLOYMENT-GUIDE.md) for
other environments). **Interactive Swagger UI is generated automatically** at
`/api/swagger-ui.html` (raw OpenAPI JSON at `/api/v3/api-docs`) — this document is a quick
static reference of the same surface.

## Conventions

- **Auth**: send `Authorization: Bearer <accessToken>` on any endpoint not marked *Public*.
- **Response envelope**: every success response is `{ success, message, data, timestamp }`
  ([`ApiResponse<T>`](../backend/src/main/java/com/staysmart/common/dto/ApiResponse.java)).
- **Paged responses**: `data` is `{ content, page, size, totalElements, totalPages, last }`
  ([`PageResponse<T>`](../backend/src/main/java/com/staysmart/common/dto/PageResponse.java)).
- **Errors**: `{ timestamp, status, error, message, path, fieldErrors? }`
  ([`ErrorResponse`](../backend/src/main/java/com/staysmart/exception/ErrorResponse.java)),
  with `fieldErrors` present only for `400` validation failures.
- **Roles**: `USER` (guest), `HOST` (can list properties), `ADMIN` (platform admin).

## Authentication — `/auth`

| Method | Path | Access | Description |
|---|---|---|---|
| POST | `/auth/register` | Public | Register as USER or HOST |
| POST | `/auth/login` | Public | Log in, returns access + refresh token |
| POST | `/auth/refresh` | Public | Exchange a refresh token for a new pair |
| POST | `/auth/logout` | Public | Revoke a refresh token |
| GET | `/auth/me` | Authenticated | Current user's profile |

## Users — `/users`

| Method | Path | Access | Description |
|---|---|---|---|
| GET | `/users/{id}` | Public | A user's public profile |
| PUT | `/users/me` | Authenticated | Update own profile |
| PUT | `/users/me/password` | Authenticated | Change own password |

## Properties — `/properties`

| Method | Path | Access | Description |
|---|---|---|---|
| GET | `/properties/search` | Public | Filter by city/country/dates/guests/price/type/room type/amenities/keyword, paged |
| GET | `/properties/{id}` | Public | Full property detail |
| GET | `/properties/host/{hostId}` | Public | A host's listings, paged |
| POST | `/properties` | HOST, ADMIN | Create a listing |
| PUT | `/properties/{id}` | Owner HOST, ADMIN | Update a listing |
| PATCH | `/properties/{id}/status?status=` | Owner HOST, ADMIN | Activate/deactivate |
| DELETE | `/properties/{id}` | Owner HOST, ADMIN | Delete a listing |
| POST | `/properties/{id}/images` (multipart `files`) | Owner HOST, ADMIN | Upload images |
| DELETE | `/properties/{id}/images/{imageId}` | Owner HOST, ADMIN | Remove an image |
| GET | `/properties/{id}/availability` | Public | Booked/blocked date ranges |
| POST | `/properties/{id}/availability` | Owner HOST, ADMIN | Block a date range |
| DELETE | `/properties/{id}/availability/{blockId}` | Owner HOST, ADMIN | Remove a manual block |

## Amenities — `/amenities`

| Method | Path | Access | Description |
|---|---|---|---|
| GET | `/amenities` | Public | List the amenity catalog |
| POST | `/amenities` | ADMIN | Add an amenity |
| DELETE | `/amenities/{id}` | ADMIN | Remove an amenity |

## Bookings — `/bookings`

| Method | Path | Access | Description |
|---|---|---|---|
| POST | `/bookings` | Authenticated | Book a property (instant confirmation) |
| GET | `/bookings/{id}` | Guest, property host, or ADMIN | Booking detail |
| PUT | `/bookings/{id}/cancel` | Guest, property host, or ADMIN | Cancel a booking |
| GET | `/bookings/me` | Authenticated | Own booking history, paged |
| GET | `/bookings/property/{propertyId}` | Owner HOST, ADMIN | Bookings for one property, paged |
| GET | `/bookings/host/me` | HOST, ADMIN | Bookings across all owned properties, paged |

## Reviews — `/reviews`

| Method | Path | Access | Description |
|---|---|---|---|
| POST | `/reviews` | Authenticated | Review a completed stay (one per booking) |
| PUT | `/reviews/{id}` | Review owner | Edit own review |
| DELETE | `/reviews/{id}` | Review owner, ADMIN | Delete a review |
| POST | `/reviews/{id}/images` (multipart `files`) | Review owner | Add photos |
| GET | `/reviews/property/{propertyId}` | Public | A property's reviews, paged |
| GET | `/reviews/eligibility/{bookingId}` | Authenticated | Whether the caller may still review this booking |

## AI Features — `/ai`

All authenticated; every call is logged to `ai_usage_logs`.

| Method | Path | Description |
|---|---|---|
| GET | `/ai/recommendations?city=` | AI-curated property picks + explanation |
| GET | `/ai/reviews/{propertyId}/summary` | AI summary of a property's reviews (pros/cons/verdict) |
| POST | `/ai/chat` `{ sessionId?, message }` | Multi-turn chat assistant |
| GET | `/ai/chat/{sessionId}/history` | Replay a chat session's messages |
| POST | `/ai/trip-planner` `{ destination, startDate, endDate, travelers, interests?, budgetLevel? }` | Day-by-day itinerary |
| POST | `/ai/description-generator` `{ title, propertyType, roomType, city, country, bedrooms, beds, bathrooms?, maxGuests, amenities?, pricePerNight?, applyToPropertyId? }` | Draft (and optionally save) a listing description |
| POST | `/ai/budget-planner` `{ destination, travelers, nights, totalBudget, currency? }` | Category-by-category trip budget |
| POST | `/ai/smart-search` `{ query }` | Natural language → structured filters → property search results |

If `OPENAI_API_KEY` is not configured on the server, every `/ai/**` endpoint returns
`503 Service Unavailable` with a clear message instead of crashing — the rest of the
platform stays fully usable for grading/demo without a paid API key.

## Admin — `/admin` (ADMIN only)

| Method | Path | Description |
|---|---|---|
| GET | `/admin/dashboard` | Platform-wide totals (users/properties/bookings/revenue/reviews) |
| GET | `/admin/users?role=` | List users, optional role filter, paged |
| PATCH | `/admin/users/{id}/enabled?enabled=` | Enable/disable an account |
| PATCH | `/admin/users/{id}/role?role=` | Change a user's role |
| DELETE | `/admin/users/{id}` | Delete a user |
| GET | `/admin/properties` | List every property, including inactive, paged |
| DELETE | `/admin/properties/{id}` | Delete any property |
| GET | `/admin/bookings/analytics` | Status breakdown, revenue, monthly trend |
| GET | `/admin/ai/usage` | Per-feature call counts, success rate, average latency |

## Example: booking flow (curl)

```bash
# 1. Log in
curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"guest.alice@staysmart.ai","password":"Password123!"}' | tee /tmp/login.json

TOKEN=$(jq -r '.data.accessToken' /tmp/login.json)

# 2. Search properties in Goa
curl -s "http://localhost:8080/api/properties/search?city=Goa&guests=2" \
  -H "Authorization: Bearer $TOKEN"

# 3. Book property id=1
curl -s -X POST http://localhost:8080/api/bookings \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"propertyId":1,"checkIn":"2026-04-01","checkOut":"2026-04-04","guestsCount":2}'
```
