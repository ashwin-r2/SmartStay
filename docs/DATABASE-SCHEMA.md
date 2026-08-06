# Database Schema Reference

PostgreSQL schema managed by Flyway. Source of truth:
[`backend/src/main/resources/db/migration/`](../backend/src/main/resources/db/migration/)

| Migration | Purpose |
|---|---|
| `V1__init_schema.sql` | All 12 tables, constraints, indexes |
| `V2__seed_sample_data.sql` | Demo users/properties/bookings/reviews/AI logs (see [sample-data.sql](sample-data.sql)) |
| `V3__more_sample_data.sql` | More hosts/guests/properties (new cities) + bookings covering every status incl. `PENDING`, reviews, AI logs |
| `V4__ooty_coorg_hyderabad_properties.sql` | 24 more properties: 6 in Ooty, 10 in Coorg, 8 in Hyderabad |

See [`ER-DIAGRAM.md`](ER-DIAGRAM.md) for the visual relationship diagram.

## `users`

| Column | Type | Notes |
|---|---|---|
| id | BIGSERIAL PK | |
| full_name | VARCHAR(150) NOT NULL | |
| email | VARCHAR(180) NOT NULL UNIQUE | login identifier |
| password_hash | VARCHAR(255) NOT NULL | BCrypt |
| phone | VARCHAR(30) | |
| role | VARCHAR(20) NOT NULL DEFAULT 'USER' | CHECK IN (USER, HOST, ADMIN) |
| avatar_url | VARCHAR(500) | |
| bio | VARCHAR(1000) | |
| enabled | BOOLEAN NOT NULL DEFAULT TRUE | admin can disable an account |
| created_at / updated_at | TIMESTAMP NOT NULL | audited by JPA |

Indexes: `idx_users_role`.

## `refresh_tokens`

| Column | Type | Notes |
|---|---|---|
| id | BIGSERIAL PK | |
| user_id | BIGINT FK → users, ON DELETE CASCADE | |
| token_hash | VARCHAR(255) NOT NULL UNIQUE | SHA-256 of the JWT refresh token (never store the raw token) |
| expires_at | TIMESTAMP NOT NULL | |
| revoked | BOOLEAN NOT NULL DEFAULT FALSE | set on logout / rotation |

Indexes: `idx_refresh_tokens_user`.

## `properties`

| Column | Type | Notes |
|---|---|---|
| id | BIGSERIAL PK | |
| host_id | BIGINT FK → users, ON DELETE CASCADE | |
| title / description | VARCHAR(150) / TEXT | |
| property_type | VARCHAR(40) DEFAULT 'APARTMENT' | APARTMENT, HOUSE, VILLA, CABIN, CONDO, STUDIO, COTTAGE, FARM_STAY |
| room_type | VARCHAR(40) DEFAULT 'ENTIRE_PLACE' | ENTIRE_PLACE, PRIVATE_ROOM, SHARED_ROOM |
| address_line / city / state / country / zip_code | VARCHAR | `city` and `country` required |
| latitude / longitude | DOUBLE PRECISION | |
| price_per_night | NUMERIC(10,2) NOT NULL, CHECK ≥ 0 | |
| cleaning_fee | NUMERIC(10,2) DEFAULT 0 | |
| max_guests / bedrooms / beds | INT | |
| bathrooms | NUMERIC(3,1) DEFAULT 1 | |
| avg_rating | NUMERIC(3,2) DEFAULT 0 | denormalized cache, see `ReviewService` |
| review_count | INT DEFAULT 0 | denormalized cache |
| status | VARCHAR(20) DEFAULT 'ACTIVE' | CHECK IN (ACTIVE, INACTIVE) |
| ai_generated_description | BOOLEAN DEFAULT FALSE | true once the AI Description Generator's text has been applied |

Indexes: `idx_properties_host`, `idx_properties_city`, `idx_properties_status`, `idx_properties_price`.

## `property_images`

| Column | Type | Notes |
|---|---|---|
| id | BIGSERIAL PK | |
| property_id | BIGINT FK → properties, ON DELETE CASCADE | |
| url | VARCHAR(500) NOT NULL | |
| display_order | INT DEFAULT 0 | |

Indexes: `idx_property_images_property`.

## `amenities` / `property_amenities`

`amenities`: `id`, `name` (UNIQUE), `icon`. No audit timestamps — this is a small reference table.

`property_amenities`: pure join table, composite PK `(property_id, amenity_id)`, both `ON DELETE CASCADE`.

## `availability_blocks`

| Column | Type | Notes |
|---|---|---|
| id | BIGSERIAL PK | |
| property_id | BIGINT FK → properties, ON DELETE CASCADE | |
| start_date / end_date | DATE NOT NULL | half-open range `[start, end)`, CHECK `end_date > start_date` |
| reason | VARCHAR(20) DEFAULT 'BLOCKED' | CHECK IN (BOOKED, BLOCKED) |
| booking_id | BIGINT FK → bookings, ON DELETE CASCADE, nullable | set only when `reason = BOOKED` |

Indexes: `idx_availability_property_dates` (property_id, start_date, end_date) — backs the
overlap-check query used by both search and booking creation.

## `bookings`

| Column | Type | Notes |
|---|---|---|
| id | BIGSERIAL PK | |
| property_id | BIGINT FK → properties, ON DELETE CASCADE | |
| guest_id | BIGINT FK → users, ON DELETE CASCADE | |
| check_in / check_out | DATE NOT NULL | CHECK `check_out > check_in` |
| guests_count / nights | INT | |
| price_per_night / cleaning_fee / total_price | NUMERIC(10,2) | snapshotted at booking time, independent of later property price changes |
| status | VARCHAR(20) DEFAULT 'PENDING' | CHECK IN (PENDING, CONFIRMED, CANCELLED, COMPLETED) |
| cancellation_reason | VARCHAR(500) | |
| cancelled_at | TIMESTAMP | |

Indexes: `idx_bookings_property`, `idx_bookings_guest`, `idx_bookings_status`, `idx_bookings_dates`.

## `reviews` / `review_images`

`reviews`: `id`, `booking_id` (FK → bookings, **UNIQUE** — one review per stay), `property_id`,
`user_id`, `rating` SMALLINT CHECK 1–5, `comment` TEXT, audited timestamps.

`review_images`: `id`, `review_id` (FK → reviews, ON DELETE CASCADE), `url`, `display_order`.

Indexes: `idx_reviews_property`, `idx_reviews_user`, `idx_review_images_review`.

## `ai_usage_logs`

| Column | Type | Notes |
|---|---|---|
| id | BIGSERIAL PK | |
| user_id | BIGINT FK → users, ON DELETE SET NULL | |
| feature | VARCHAR(40) NOT NULL | CHECK IN (RECOMMENDATION, REVIEW_SUMMARY, CHAT_ASSISTANT, TRIP_PLANNER, DESCRIPTION_GENERATOR, BUDGET_PLANNER, SMART_SEARCH) |
| prompt_tokens / completion_tokens | INT, nullable | populated when the model exposes token usage (chat assistant only, currently) |
| latency_ms | BIGINT | |
| success | BOOLEAN DEFAULT TRUE | |
| error_message | VARCHAR(500) | |

Indexes: `idx_ai_usage_feature`, `idx_ai_usage_user`, `idx_ai_usage_created`. This table
powers the admin "AI usage statistics" dashboard.

## `ai_chat_messages`

| Column | Type | Notes |
|---|---|---|
| id | BIGSERIAL PK | |
| user_id | BIGINT FK → users, ON DELETE CASCADE | |
| session_id | VARCHAR(100) NOT NULL | groups a conversation's turns |
| role | VARCHAR(20) NOT NULL | CHECK IN (USER, ASSISTANT) |
| content | TEXT NOT NULL | |

Indexes: `idx_ai_chat_session`, `idx_ai_chat_user`.
