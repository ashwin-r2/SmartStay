-- =====================================================================================
-- StaySmart AI - initial schema
-- =====================================================================================

-- ------------------------------------------------------------------------------------
-- users & auth
-- ------------------------------------------------------------------------------------
CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    full_name       VARCHAR(150)  NOT NULL,
    email           VARCHAR(180)  NOT NULL UNIQUE,
    password_hash   VARCHAR(255)  NOT NULL,
    phone           VARCHAR(30),
    role            VARCHAR(20)   NOT NULL DEFAULT 'USER' CHECK (role IN ('USER', 'HOST', 'ADMIN')),
    avatar_url      VARCHAR(500),
    bio             VARCHAR(1000),
    enabled         BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP     NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP     NOT NULL DEFAULT now()
);
CREATE INDEX idx_users_role ON users(role);

CREATE TABLE refresh_tokens (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash      VARCHAR(255)  NOT NULL UNIQUE,
    expires_at      TIMESTAMP     NOT NULL,
    revoked         BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP     NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP     NOT NULL DEFAULT now()
);
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);

-- ------------------------------------------------------------------------------------
-- properties
-- ------------------------------------------------------------------------------------
CREATE TABLE properties (
    id                  BIGSERIAL PRIMARY KEY,
    host_id             BIGINT        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title               VARCHAR(150)  NOT NULL,
    description         TEXT,
    property_type       VARCHAR(40)   NOT NULL DEFAULT 'APARTMENT',
    room_type           VARCHAR(40)   NOT NULL DEFAULT 'ENTIRE_PLACE',
    address_line        VARCHAR(255),
    city                VARCHAR(100)  NOT NULL,
    state               VARCHAR(100),
    country             VARCHAR(100)  NOT NULL,
    zip_code            VARCHAR(20),
    latitude            DOUBLE PRECISION,
    longitude           DOUBLE PRECISION,
    price_per_night     NUMERIC(10,2) NOT NULL CHECK (price_per_night >= 0),
    cleaning_fee        NUMERIC(10,2) NOT NULL DEFAULT 0,
    max_guests          INT           NOT NULL DEFAULT 1 CHECK (max_guests > 0),
    bedrooms            INT           NOT NULL DEFAULT 1,
    beds                INT           NOT NULL DEFAULT 1,
    bathrooms           NUMERIC(3,1)  NOT NULL DEFAULT 1,
    avg_rating          NUMERIC(3,2)  NOT NULL DEFAULT 0,
    review_count        INT           NOT NULL DEFAULT 0,
    status              VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE')),
    ai_generated_description BOOLEAN  NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP     NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP     NOT NULL DEFAULT now()
);
CREATE INDEX idx_properties_host ON properties(host_id);
CREATE INDEX idx_properties_city ON properties(city);
CREATE INDEX idx_properties_status ON properties(status);
CREATE INDEX idx_properties_price ON properties(price_per_night);

CREATE TABLE property_images (
    id              BIGSERIAL PRIMARY KEY,
    property_id     BIGINT        NOT NULL REFERENCES properties(id) ON DELETE CASCADE,
    url             VARCHAR(500)  NOT NULL,
    display_order   INT           NOT NULL DEFAULT 0,
    created_at      TIMESTAMP     NOT NULL DEFAULT now()
);
CREATE INDEX idx_property_images_property ON property_images(property_id);

CREATE TABLE amenities (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(80) NOT NULL UNIQUE,
    icon    VARCHAR(80)
);

CREATE TABLE property_amenities (
    property_id     BIGINT NOT NULL REFERENCES properties(id) ON DELETE CASCADE,
    amenity_id      BIGINT NOT NULL REFERENCES amenities(id) ON DELETE CASCADE,
    PRIMARY KEY (property_id, amenity_id)
);

-- availability: explicit blocked date ranges (host maintenance blocks and booking-driven blocks)
CREATE TABLE availability_blocks (
    id              BIGSERIAL PRIMARY KEY,
    property_id     BIGINT        NOT NULL REFERENCES properties(id) ON DELETE CASCADE,
    start_date      DATE          NOT NULL,
    end_date        DATE          NOT NULL,
    reason          VARCHAR(20)   NOT NULL DEFAULT 'BLOCKED' CHECK (reason IN ('BOOKED', 'BLOCKED')),
    booking_id      BIGINT,
    created_at      TIMESTAMP     NOT NULL DEFAULT now(),
    CHECK (end_date > start_date)
);
CREATE INDEX idx_availability_property_dates ON availability_blocks(property_id, start_date, end_date);

-- ------------------------------------------------------------------------------------
-- bookings
-- ------------------------------------------------------------------------------------
CREATE TABLE bookings (
    id                  BIGSERIAL PRIMARY KEY,
    property_id         BIGINT        NOT NULL REFERENCES properties(id) ON DELETE CASCADE,
    guest_id             BIGINT        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    check_in            DATE          NOT NULL,
    check_out           DATE          NOT NULL,
    guests_count         INT           NOT NULL DEFAULT 1,
    nights              INT           NOT NULL,
    price_per_night      NUMERIC(10,2) NOT NULL,
    cleaning_fee         NUMERIC(10,2) NOT NULL DEFAULT 0,
    total_price          NUMERIC(10,2) NOT NULL,
    status               VARCHAR(20)   NOT NULL DEFAULT 'PENDING'
                          CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED')),
    cancellation_reason  VARCHAR(500),
    cancelled_at         TIMESTAMP,
    created_at           TIMESTAMP     NOT NULL DEFAULT now(),
    updated_at           TIMESTAMP     NOT NULL DEFAULT now(),
    CHECK (check_out > check_in)
);
CREATE INDEX idx_bookings_property ON bookings(property_id);
CREATE INDEX idx_bookings_guest ON bookings(guest_id);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_dates ON bookings(check_in, check_out);

ALTER TABLE availability_blocks
    ADD CONSTRAINT fk_availability_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE;

-- ------------------------------------------------------------------------------------
-- reviews
-- ------------------------------------------------------------------------------------
CREATE TABLE reviews (
    id              BIGSERIAL PRIMARY KEY,
    booking_id      BIGINT        NOT NULL UNIQUE REFERENCES bookings(id) ON DELETE CASCADE,
    property_id     BIGINT        NOT NULL REFERENCES properties(id) ON DELETE CASCADE,
    user_id         BIGINT        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    rating          SMALLINT      NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment         TEXT,
    created_at      TIMESTAMP     NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP     NOT NULL DEFAULT now()
);
CREATE INDEX idx_reviews_property ON reviews(property_id);
CREATE INDEX idx_reviews_user ON reviews(user_id);

CREATE TABLE review_images (
    id              BIGSERIAL PRIMARY KEY,
    review_id       BIGINT        NOT NULL REFERENCES reviews(id) ON DELETE CASCADE,
    url             VARCHAR(500)  NOT NULL,
    display_order   INT           NOT NULL DEFAULT 0
);
CREATE INDEX idx_review_images_review ON review_images(review_id);

-- ------------------------------------------------------------------------------------
-- AI features: usage logging + chat history
-- ------------------------------------------------------------------------------------
CREATE TABLE ai_usage_logs (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT        REFERENCES users(id) ON DELETE SET NULL,
    feature             VARCHAR(40)   NOT NULL
                         CHECK (feature IN ('RECOMMENDATION', 'REVIEW_SUMMARY', 'CHAT_ASSISTANT',
                                             'TRIP_PLANNER', 'DESCRIPTION_GENERATOR', 'BUDGET_PLANNER',
                                             'SMART_SEARCH')),
    prompt_tokens       INT,
    completion_tokens   INT,
    latency_ms          BIGINT,
    success             BOOLEAN       NOT NULL DEFAULT TRUE,
    error_message       VARCHAR(500),
    created_at          TIMESTAMP     NOT NULL DEFAULT now()
);
CREATE INDEX idx_ai_usage_feature ON ai_usage_logs(feature);
CREATE INDEX idx_ai_usage_user ON ai_usage_logs(user_id);
CREATE INDEX idx_ai_usage_created ON ai_usage_logs(created_at);

CREATE TABLE ai_chat_messages (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    session_id      VARCHAR(100)  NOT NULL,
    role            VARCHAR(20)   NOT NULL CHECK (role IN ('USER', 'ASSISTANT')),
    content         TEXT          NOT NULL,
    created_at      TIMESTAMP     NOT NULL DEFAULT now()
);
CREATE INDEX idx_ai_chat_session ON ai_chat_messages(session_id);
CREATE INDEX idx_ai_chat_user ON ai_chat_messages(user_id);
