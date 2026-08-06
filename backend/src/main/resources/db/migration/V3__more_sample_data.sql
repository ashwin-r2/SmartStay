-- =====================================================================================
-- StaySmart AI - additional sample data (round 2): more hosts, guests, properties across
-- new cities, bookings covering every status (including PENDING, missing from V2), reviews,
-- AI usage logs and a chat session. Builds on top of V2__seed_sample_data.sql ids.
-- Passwords (same bcrypt hashes reused from V2, matching the same plaintext values):
--   New hosts  : meera.host@staysmart.ai    / Host12345!
--                vikram.host@staysmart.ai   / Host12345!
--   New guests : guest.dev@staysmart.ai     / Password123!
--                guest.fatima@staysmart.ai  / Password123!
--                guest.sanjay@staysmart.ai  / Password123!
--                guest.neha@staysmart.ai    / Password123!
-- =====================================================================================

-- ------------------------------------------------------------------------------------
-- users (ids 8-13)
-- ------------------------------------------------------------------------------------
INSERT INTO users (id, full_name, email, password_hash, phone, role, avatar_url, bio, enabled, created_at, updated_at) VALUES
(8, 'Meera Iyer', 'meera.host@staysmart.ai', '$2a$10$XR0X.An613F2O.cvzXJ/juxKv9cBDWhqZWQLHdDlB2xTNbp6Lv4zy', '+91-9000000008', 'HOST', 'https://i.pravatar.cc/150?u=meera.host@staysmart.ai', 'Backwater houseboats and island stays in Kerala and the Andamans.', true, now(), now()),
(9, 'Vikram Singh', 'vikram.host@staysmart.ai', '$2a$10$XR0X.An613F2O.cvzXJ/juxKv9cBDWhqZWQLHdDlB2xTNbp6Lv4zy', '+91-9000000009', 'HOST', 'https://i.pravatar.cc/150?u=vikram.host@staysmart.ai', 'City apartments in Mumbai and Bangalore, plus a guesthouse in Leh.', true, now(), now()),
(10, 'Dev Patel', 'guest.dev@staysmart.ai', '$2a$10$7ka7V6XzHU6NotbEF1nZmOPxRpRkgDBoWmh0jBfvZgLWpxefXYAke', '+91-9000000010', 'USER', 'https://i.pravatar.cc/150?u=guest.dev@staysmart.ai', 'Foodie, always chasing the next coastal trip.', true, now(), now()),
(11, 'Fatima Khan', 'guest.fatima@staysmart.ai', '$2a$10$7ka7V6XzHU6NotbEF1nZmOPxRpRkgDBoWmh0jBfvZgLWpxefXYAke', '+91-9000000011', 'USER', 'https://i.pravatar.cc/150?u=guest.fatima@staysmart.ai', 'Photographer documenting India''s hill stations.', true, now(), now()),
(12, 'Sanjay Gupta', 'guest.sanjay@staysmart.ai', '$2a$10$7ka7V6XzHU6NotbEF1nZmOPxRpRkgDBoWmh0jBfvZgLWpxefXYAke', '+91-9000000012', 'USER', 'https://i.pravatar.cc/150?u=guest.sanjay@staysmart.ai', 'Corporate traveler turned weekend explorer.', true, now(), now()),
(13, 'Neha Joshi', 'guest.neha@staysmart.ai', '$2a$10$7ka7V6XzHU6NotbEF1nZmOPxRpRkgDBoWmh0jBfvZgLWpxefXYAke', '+91-9000000013', 'USER', 'https://i.pravatar.cc/150?u=guest.neha@staysmart.ai', 'Solo backpacker, heritage sites enthusiast.', true, now(), now());

-- ------------------------------------------------------------------------------------
-- properties (ids 9-18) - new cities: Kerala, Mumbai, Ladakh, Pondicherry, Darjeeling,
-- Coorg, Varanasi, Bangalore, Andaman - and two PropertyType/RoomType values V2 didn't use
-- (FARM_STAY, CONDO, SHARED_ROOM)
-- ------------------------------------------------------------------------------------
INSERT INTO properties (id, host_id, title, description, property_type, room_type, address_line, city, state, country, zip_code, latitude, longitude, price_per_night, cleaning_fee, max_guests, bedrooms, beds, bathrooms, avg_rating, review_count, status, ai_generated_description, created_at, updated_at) VALUES
(9, 8, 'Alleppey Backwater Houseboat', 'A traditional Kerala houseboat with a private sundeck, cruising the Alleppey backwaters with meals cooked on board.', 'HOUSE', 'ENTIRE_PLACE', 'Alleppey Boat Jetty Road', 'Alleppey', 'Kerala', 'India', '688001', 9.4981, 76.3388, 6500.00, 500.00, 4, 2, 2, 1.5, 5.00, 1, 'ACTIVE', false, now(), now()),
(10, 8, 'Fort Kochi Heritage Home', 'A private room in a restored Dutch-colonial home near the Chinese fishing nets, a short walk from Fort Kochi''s cafes.', 'HOUSE', 'PRIVATE_ROOM', 'Fort Kochi Beach Road', 'Kochi', 'Kerala', 'India', '682001', 9.9658, 76.2422, 2600.00, 200.00, 2, 1, 1, 1.0, 0.00, 0, 'ACTIVE', false, now(), now()),
(11, 9, 'Bandra Sea-View Apartment', 'A modern apartment overlooking the Arabian Sea in Bandra, close to Bandstand promenade and Mumbai''s best cafes.', 'APARTMENT', 'ENTIRE_PLACE', 'Carter Road', 'Mumbai', 'Maharashtra', 'India', '400050', 19.0596, 72.8295, 7200.00, 600.00, 4, 2, 2, 2.0, 4.00, 1, 'ACTIVE', false, now(), now()),
(12, 9, 'Leh Ladakh Mountain Guesthouse', 'A simple shared-room guesthouse in the heart of Leh, run by a local family, with home-cooked breakfast and mountain views.', 'COTTAGE', 'SHARED_ROOM', 'Fort Road', 'Leh', 'Ladakh', 'India', '194101', 34.1526, 77.5771, 1500.00, 100.00, 1, 1, 1, 1.0, 0.00, 0, 'ACTIVE', false, now(), now()),
(13, 2, 'Pondicherry French Quarter Villa', 'A pastel-colonial villa in the White Town French Quarter, with a courtyard garden and a five-minute walk to the promenade.', 'VILLA', 'ENTIRE_PLACE', 'Rue Romain Rolland', 'Puducherry', 'Puducherry', 'India', '605001', 11.9416, 79.8083, 6800.00, 500.00, 6, 3, 4, 2.5, 0.00, 0, 'ACTIVE', false, now(), now()),
(14, 3, 'Darjeeling Tea Estate Bungalow', 'A colonial planter''s bungalow set among tea gardens, with panoramic views of Kanchenjunga on clear mornings.', 'COTTAGE', 'ENTIRE_PLACE', 'Happy Valley Tea Estate Road', 'Darjeeling', 'West Bengal', 'India', '734101', 27.0410, 88.2663, 4500.00, 400.00, 5, 3, 3, 2.0, 5.00, 1, 'ACTIVE', false, now(), now()),
(15, 3, 'Coorg Coffee Plantation Cottage', 'A cottage tucked inside a working coffee plantation in Coorg, with guided estate walks and fresh-brewed filter coffee.', 'FARM_STAY', 'ENTIRE_PLACE', 'Madikeri-Siddapur Road', 'Coorg', 'Karnataka', 'India', '571201', 12.4244, 75.7382, 3900.00, 350.00, 4, 2, 2, 1.5, 0.00, 0, 'ACTIVE', false, now(), now()),
(16, 4, 'Varanasi Ganges View Room', 'A simple, clean private room overlooking the ghats, a few minutes'' walk from the evening Ganga Aarti.', 'HOUSE', 'PRIVATE_ROOM', 'Assi Ghat Road', 'Varanasi', 'Uttar Pradesh', 'India', '221001', 25.3176, 82.9739, 1800.00, 150.00, 2, 1, 1, 1.0, 4.00, 1, 'ACTIVE', false, now(), now()),
(17, 9, 'Bangalore Koramangala City Loft', 'A stylish loft in Koramangala''s cafe district, ideal for business travelers and long weekends alike.', 'CONDO', 'ENTIRE_PLACE', '80 Feet Road', 'Bangalore', 'Karnataka', 'India', '560034', 12.9352, 77.6245, 4200.00, 350.00, 3, 1, 2, 1.0, 0.00, 0, 'INACTIVE', false, now(), now()),
(18, 8, 'Havelock Island Beachfront Bungalow', 'A beachfront bungalow steps from Radhanagar Beach in the Andamans, with kayaks available and a private garden.', 'VILLA', 'ENTIRE_PLACE', 'Radhanagar Beach Road', 'Havelock Island', 'Andaman and Nicobar Islands', 'India', '744211', 12.0117, 92.9853, 9500.00, 800.00, 6, 3, 3, 2.0, 0.00, 0, 'ACTIVE', false, now(), now());

-- property images
INSERT INTO property_images (property_id, url, display_order, created_at) VALUES
(9, 'https://picsum.photos/seed/staysmart-p9-a/800/600', 0, now()),
(9, 'https://picsum.photos/seed/staysmart-p9-b/800/600', 1, now()),
(10, 'https://picsum.photos/seed/staysmart-p10-a/800/600', 0, now()),
(11, 'https://picsum.photos/seed/staysmart-p11-a/800/600', 0, now()),
(11, 'https://picsum.photos/seed/staysmart-p11-b/800/600', 1, now()),
(12, 'https://picsum.photos/seed/staysmart-p12-a/800/600', 0, now()),
(13, 'https://picsum.photos/seed/staysmart-p13-a/800/600', 0, now()),
(13, 'https://picsum.photos/seed/staysmart-p13-b/800/600', 1, now()),
(14, 'https://picsum.photos/seed/staysmart-p14-a/800/600', 0, now()),
(14, 'https://picsum.photos/seed/staysmart-p14-b/800/600', 1, now()),
(15, 'https://picsum.photos/seed/staysmart-p15-a/800/600', 0, now()),
(16, 'https://picsum.photos/seed/staysmart-p16-a/800/600', 0, now()),
(17, 'https://picsum.photos/seed/staysmart-p17-a/800/600', 0, now()),
(18, 'https://picsum.photos/seed/staysmart-p18-a/800/600', 0, now()),
(18, 'https://picsum.photos/seed/staysmart-p18-b/800/600', 1, now());

-- property amenities (amenity ids reference the fixed list seeded in V2)
INSERT INTO property_amenities (property_id, amenity_id) VALUES
(9, 1), (9, 3), (9, 12),
(10, 1), (10, 3), (10, 12),
(11, 1), (11, 2), (11, 7), (11, 13),
(12, 1), (12, 8), (12, 11), (12, 12),
(13, 1), (13, 2), (13, 4), (13, 5), (13, 15),
(14, 1), (14, 8), (14, 11), (14, 12),
(15, 1), (15, 3), (15, 8), (15, 11),
(16, 1), (16, 12),
(17, 1), (17, 2), (17, 7), (17, 13), (17, 15),
(18, 1), (18, 2), (18, 4), (18, 5), (18, 10);

-- ------------------------------------------------------------------------------------
-- bookings (ids 8-15) - adds PENDING, which V2's data didn't cover
-- ------------------------------------------------------------------------------------
INSERT INTO bookings (id, property_id, guest_id, check_in, check_out, guests_count, nights, price_per_night, cleaning_fee, total_price, status, cancellation_reason, cancelled_at, created_at, updated_at) VALUES
(8, 9, 10, CURRENT_DATE - INTERVAL '30 days', CURRENT_DATE - INTERVAL '25 days', 4, 5, 6500.00, 500.00, 33000.00, 'COMPLETED', NULL, NULL, now() - INTERVAL '35 days', now() - INTERVAL '25 days'),
(9, 11, 11, CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE - INTERVAL '16 days', 3, 4, 7200.00, 600.00, 29400.00, 'COMPLETED', NULL, NULL, now() - INTERVAL '24 days', now() - INTERVAL '16 days'),
(10, 14, 12, CURRENT_DATE - INTERVAL '15 days', CURRENT_DATE - INTERVAL '12 days', 4, 3, 4500.00, 400.00, 13900.00, 'COMPLETED', NULL, NULL, now() - INTERVAL '19 days', now() - INTERVAL '12 days'),
(11, 16, 13, CURRENT_DATE - INTERVAL '8 days', CURRENT_DATE - INTERVAL '6 days', 1, 2, 1800.00, 150.00, 3750.00, 'COMPLETED', NULL, NULL, now() - INTERVAL '12 days', now() - INTERVAL '6 days'),
(12, 13, 10, CURRENT_DATE + INTERVAL '15 days', CURRENT_DATE + INTERVAL '19 days', 5, 4, 6800.00, 500.00, 27700.00, 'CONFIRMED', NULL, NULL, now() - INTERVAL '3 days', now() - INTERVAL '3 days'),
(13, 15, 11, CURRENT_DATE + INTERVAL '30 days', CURRENT_DATE + INTERVAL '33 days', 3, 3, 3900.00, 350.00, 12050.00, 'CONFIRMED', NULL, NULL, now() - INTERVAL '2 days', now() - INTERVAL '2 days'),
(14, 10, 12, CURRENT_DATE + INTERVAL '40 days', CURRENT_DATE + INTERVAL '42 days', 2, 2, 2600.00, 200.00, 5400.00, 'PENDING', NULL, NULL, now() - INTERVAL '1 days', now() - INTERVAL '1 days'),
(15, 12, 13, CURRENT_DATE + INTERVAL '5 days', CURRENT_DATE + INTERVAL '7 days', 1, 2, 1500.00, 100.00, 3100.00, 'CANCELLED', 'Flight got cancelled', now() - INTERVAL '1 days', now() - INTERVAL '6 days', now() - INTERVAL '1 days');

-- availability blocks for the new CONFIRMED bookings, plus one manual maintenance block
INSERT INTO availability_blocks (property_id, start_date, end_date, reason, booking_id, created_at) VALUES
(13, CURRENT_DATE + INTERVAL '15 days', CURRENT_DATE + INTERVAL '19 days', 'BOOKED', 12, now()),
(15, CURRENT_DATE + INTERVAL '30 days', CURRENT_DATE + INTERVAL '33 days', 'BOOKED', 13, now()),
(18, CURRENT_DATE + INTERVAL '60 days', CURRENT_DATE + INTERVAL '65 days', 'BLOCKED', NULL, now());

-- ------------------------------------------------------------------------------------
-- reviews (ids 5-8), matching the avg_rating/review_count seeded on properties above
-- ------------------------------------------------------------------------------------
INSERT INTO reviews (id, booking_id, property_id, user_id, rating, comment, created_at, updated_at) VALUES
(5, 8, 9, 10, 5, 'The backwater cruise was magical - waking up to the water lapping against the houseboat and fresh Kerala food cooked on board. Unforgettable.', now() - INTERVAL '24 days', now() - INTERVAL '24 days'),
(6, 9, 11, 11, 4, 'Gorgeous sea view from the balcony and a great Bandra location, though street noise picked up at night. Would stay again.', now() - INTERVAL '15 days', now() - INTERVAL '15 days'),
(7, 10, 14, 12, 5, 'Waking up amid the tea gardens with Kanchenjunga peeking through the clouds was worth the whole trip. Wonderfully quiet.', now() - INTERVAL '11 days', now() - INTERVAL '11 days'),
(8, 11, 16, 13, 4, 'Simple room but an unbeatable location for the Ganga Aarti - just a short walk down the ghats. Great value.', now() - INTERVAL '5 days', now() - INTERVAL '5 days');

INSERT INTO review_images (review_id, url, display_order) VALUES
(5, 'https://picsum.photos/seed/staysmart-review5-a/600/400', 0),
(7, 'https://picsum.photos/seed/staysmart-review7-a/600/400', 0);

-- ------------------------------------------------------------------------------------
-- more AI usage logs, spread across the new users and all features (incl. a couple of
-- failures so the admin "AI usage statistics" success-rate chart has both colors)
-- ------------------------------------------------------------------------------------
INSERT INTO ai_usage_logs (user_id, feature, prompt_tokens, completion_tokens, latency_ms, success, error_message, created_at) VALUES
(10, 'RECOMMENDATION', 190, 125, 990, true, NULL, now() - INTERVAL '9 days'),
(11, 'RECOMMENDATION', 200, 140, 1080, true, NULL, now() - INTERVAL '8 days'),
(12, 'REVIEW_SUMMARY', 300, 85, 860, true, NULL, now() - INTERVAL '8 days'),
(13, 'REVIEW_SUMMARY', 315, 100, 900, true, NULL, now() - INTERVAL '7 days'),
(10, 'CHAT_ASSISTANT', 65, 95, 710, true, NULL, now() - INTERVAL '7 days'),
(10, 'CHAT_ASSISTANT', 80, 120, 760, true, NULL, now() - INTERVAL '7 days'),
(11, 'TRIP_PLANNER', 220, 510, 2250, true, NULL, now() - INTERVAL '6 days'),
(12, 'BUDGET_PLANNER', 160, 220, 1400, true, NULL, now() - INTERVAL '5 days'),
(8, 'DESCRIPTION_GENERATOR', 140, 270, 1550, true, NULL, now() - INTERVAL '5 days'),
(9, 'DESCRIPTION_GENERATOR', 135, 250, 1450, true, NULL, now() - INTERVAL '4 days'),
(9, 'DESCRIPTION_GENERATOR', 128, 0, 400, false, 'Upstream Gemini request timed out', now() - INTERVAL '4 days'),
(13, 'SMART_SEARCH', 100, 65, 800, true, NULL, now() - INTERVAL '3 days'),
(12, 'SMART_SEARCH', 95, 58, 790, false, 'Upstream OpenAI request timed out', now() - INTERVAL '2 days'),
(11, 'RECOMMENDATION', 185, 118, 970, true, NULL, now() - INTERVAL '1 days');

-- a short demo chat session for guest.dev planning a Kerala trip
INSERT INTO ai_chat_messages (user_id, session_id, role, content, created_at) VALUES
(10, 'demo-session-dev-1', 'USER', 'Planning a 3-day Kerala backwaters trip - any tips on the best route from Alleppey?', now() - INTERVAL '7 days'),
(10, 'demo-session-dev-1', 'ASSISTANT', 'Start with a Alleppey to Kumarakom houseboat cruise on day 1, spend day 2 exploring Kumarakom Bird Sanctuary and a spice plantation, then head back via Kottayam on day 3. Book the houseboat a few days ahead in peak season.', now() - INTERVAL '7 days'),
(10, 'demo-session-dev-1', 'USER', 'Good food stops along the way?', now() - INTERVAL '7 days'),
(10, 'demo-session-dev-1', 'ASSISTANT', 'Try Kumarakom''s karimeen (pearl spot fish) curry, and stop for appam with stew in Kottayam. Most houseboats also serve a full Kerala sadya-style lunch on board.', now() - INTERVAL '7 days');

-- ------------------------------------------------------------------------------------
-- keep sequences in sync after explicit-id inserts
-- ------------------------------------------------------------------------------------
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('properties_id_seq', (SELECT MAX(id) FROM properties));
SELECT setval('property_images_id_seq', (SELECT MAX(id) FROM property_images));
SELECT setval('availability_blocks_id_seq', (SELECT MAX(id) FROM availability_blocks));
SELECT setval('bookings_id_seq', (SELECT MAX(id) FROM bookings));
SELECT setval('reviews_id_seq', (SELECT MAX(id) FROM reviews));
SELECT setval('review_images_id_seq', (SELECT MAX(id) FROM review_images));
SELECT setval('ai_usage_logs_id_seq', (SELECT MAX(id) FROM ai_usage_logs));
SELECT setval('ai_chat_messages_id_seq', (SELECT MAX(id) FROM ai_chat_messages));
