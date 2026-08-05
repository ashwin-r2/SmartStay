# Entity-Relationship Diagram

This matches the Flyway schema in [`backend/src/main/resources/db/migration/V1__init_schema.sql`](../backend/src/main/resources/db/migration/V1__init_schema.sql).
See [`DATABASE-SCHEMA.md`](DATABASE-SCHEMA.md) for column-level detail.

```mermaid
erDiagram
    USERS ||--o{ PROPERTIES : hosts
    USERS ||--o{ BOOKINGS : books
    USERS ||--o{ REVIEWS : writes
    USERS ||--o{ REFRESH_TOKENS : owns
    USERS ||--o{ AI_USAGE_LOGS : triggers
    USERS ||--o{ AI_CHAT_MESSAGES : sends

    PROPERTIES ||--o{ PROPERTY_IMAGES : has
    PROPERTIES ||--o{ AVAILABILITY_BLOCKS : has
    PROPERTIES ||--o{ BOOKINGS : "is booked in"
    PROPERTIES ||--o{ REVIEWS : receives
    PROPERTIES }o--o{ AMENITIES : offers

    BOOKINGS ||--|| REVIEWS : "reviewed by"
    BOOKINGS ||--o| AVAILABILITY_BLOCKS : "blocks dates via"

    REVIEWS ||--o{ REVIEW_IMAGES : has

    USERS {
        bigint id PK
        varchar full_name
        varchar email UK
        varchar password_hash
        varchar phone
        varchar role "USER | HOST | ADMIN"
        varchar avatar_url
        varchar bio
        boolean enabled
        timestamp created_at
        timestamp updated_at
    }

    REFRESH_TOKENS {
        bigint id PK
        bigint user_id FK
        varchar token_hash UK
        timestamp expires_at
        boolean revoked
    }

    PROPERTIES {
        bigint id PK
        bigint host_id FK
        varchar title
        text description
        varchar property_type
        varchar room_type
        varchar city
        varchar country
        numeric price_per_night
        numeric cleaning_fee
        int max_guests
        int bedrooms
        int beds
        numeric bathrooms
        numeric avg_rating
        int review_count
        varchar status "ACTIVE | INACTIVE"
        boolean ai_generated_description
    }

    PROPERTY_IMAGES {
        bigint id PK
        bigint property_id FK
        varchar url
        int display_order
    }

    AMENITIES {
        bigint id PK
        varchar name UK
        varchar icon
    }

    PROPERTY_AMENITIES {
        bigint property_id FK
        bigint amenity_id FK
    }

    AVAILABILITY_BLOCKS {
        bigint id PK
        bigint property_id FK
        date start_date
        date end_date
        varchar reason "BOOKED | BLOCKED"
        bigint booking_id FK
    }

    BOOKINGS {
        bigint id PK
        bigint property_id FK
        bigint guest_id FK
        date check_in
        date check_out
        int guests_count
        int nights
        numeric price_per_night
        numeric total_price
        varchar status "PENDING | CONFIRMED | CANCELLED | COMPLETED"
        varchar cancellation_reason
    }

    REVIEWS {
        bigint id PK
        bigint booking_id FK, UK
        bigint property_id FK
        bigint user_id FK
        smallint rating
        text comment
    }

    REVIEW_IMAGES {
        bigint id PK
        bigint review_id FK
        varchar url
        int display_order
    }

    AI_USAGE_LOGS {
        bigint id PK
        bigint user_id FK
        varchar feature
        int prompt_tokens
        int completion_tokens
        bigint latency_ms
        boolean success
        varchar error_message
    }

    AI_CHAT_MESSAGES {
        bigint id PK
        bigint user_id FK
        varchar session_id
        varchar role "USER | ASSISTANT"
        text content
    }
```

## Notes

- **`property_amenities`** is a pure join table (composite PK `property_id, amenity_id`) —
  no surrogate id, so it's shown here as a relationship rather than a boxed entity.
- **`availability_blocks.booking_id`** is a plain nullable FK: rows created from a
  confirmed booking carry `reason = 'BOOKED'` and reference it; rows a host creates
  manually (maintenance/personal use) carry `reason = 'BLOCKED'` and a null `booking_id`.
- **`reviews.booking_id`** is `UNIQUE` — enforcing "one review per completed stay" at
  the database level, not just in application code.
- **`properties.avg_rating` / `review_count`** are denormalized caches, recomputed by
  `ReviewService` on every create/update/delete so property listings can sort/filter by
  rating without aggregating `reviews` on every request.
