-- =====================================================================================
-- This is a copy of backend/src/main/resources/db/migration/V2__seed_sample_data.sql,
-- V3__more_sample_data.sql and V4__ooty_coorg_hyderabad_properties.sql concatenated,
-- kept here for easy reference/reuse. Flyway runs the originals under db/migration
-- automatically on backend startup - you do not need to run this file manually unless
-- you want to re-seed a database outside of Flyway.
-- =====================================================================================

-- =====================================================================================
-- StaySmart AI - sample data for local development / demo / grading
-- Passwords (bcrypt hashes below correspond to these plaintext values):
--   Admin account   : admin@staysmart.ai        / Admin12345!
--   Host accounts   : aarav.host@staysmart.ai    / Host12345!
--                     priya.host@staysmart.ai    / Host12345!
--                     rohan.host@staysmart.ai    / Host12345!
--   Guest accounts  : guest.alice@staysmart.ai   / Password123!
--                     guest.bob@staysmart.ai     / Password123!
--                     guest.carol@staysmart.ai   / Password123!
-- =====================================================================================

-- ------------------------------------------------------------------------------------
-- users
-- ------------------------------------------------------------------------------------
INSERT INTO users (id, full_name, email, password_hash, phone, role, avatar_url, bio, enabled, created_at, updated_at) VALUES
(1, 'StaySmart Admin', 'admin@staysmart.ai', '$2a$10$Y4VY.dbYWGg6dKcZDzjXeu5eF1n.w3HhUaPUnpBP19rgYANAGlVu.', '+91-9000000001', 'ADMIN', 'https://i.pravatar.cc/150?u=admin@staysmart.ai', 'Platform administrator.', true, now(), now()),
(2, 'Aarav Sharma', 'aarav.host@staysmart.ai', '$2a$10$XR0X.An613F2O.cvzXJ/juxKv9cBDWhqZWQLHdDlB2xTNbp6Lv4zy', '+91-9000000002', 'HOST', 'https://i.pravatar.cc/150?u=aarav.host@staysmart.ai', 'Hosting cozy beach stays in Goa for 5 years.', true, now(), now()),
(3, 'Priya Verma', 'priya.host@staysmart.ai', '$2a$10$XR0X.An613F2O.cvzXJ/juxKv9cBDWhqZWQLHdDlB2xTNbp6Lv4zy', '+91-9000000003', 'HOST', 'https://i.pravatar.cc/150?u=priya.host@staysmart.ai', 'Mountain retreats in Himachal Pradesh.', true, now(), now()),
(4, 'Rohan Mehta', 'rohan.host@staysmart.ai', '$2a$10$XR0X.An613F2O.cvzXJ/juxKv9cBDWhqZWQLHdDlB2xTNbp6Lv4zy', '+91-9000000004', 'HOST', 'https://i.pravatar.cc/150?u=rohan.host@staysmart.ai', 'Heritage homes in Rajasthan.', true, now(), now()),
(5, 'Alice Fernandes', 'guest.alice@staysmart.ai', '$2a$10$7ka7V6XzHU6NotbEF1nZmOPxRpRkgDBoWmh0jBfvZgLWpxefXYAke', '+91-9000000005', 'USER', 'https://i.pravatar.cc/150?u=guest.alice@staysmart.ai', 'Loves the beach and good coffee.', true, now(), now()),
(6, 'Bob Nair', 'guest.bob@staysmart.ai', '$2a$10$7ka7V6XzHU6NotbEF1nZmOPxRpRkgDBoWmh0jBfvZgLWpxefXYAke', '+91-9000000006', 'USER', 'https://i.pravatar.cc/150?u=guest.bob@staysmart.ai', 'Weekend trekker.', true, now(), now()),
(7, 'Carol D''Souza', 'guest.carol@staysmart.ai', '$2a$10$7ka7V6XzHU6NotbEF1nZmOPxRpRkgDBoWmh0jBfvZgLWpxefXYAke', '+91-9000000007', 'USER', 'https://i.pravatar.cc/150?u=guest.carol@staysmart.ai', 'Exploring India one weekend at a time.', true, now(), now());

-- ------------------------------------------------------------------------------------
-- amenities
-- ------------------------------------------------------------------------------------
INSERT INTO amenities (id, name, icon) VALUES
(1, 'WiFi', 'wifi'),
(2, 'Air Conditioning', 'snowflake'),
(3, 'Kitchen', 'cooking-pot'),
(4, 'Free Parking', 'car'),
(5, 'Swimming Pool', 'pool'),
(6, 'Washing Machine', 'washing-machine'),
(7, 'TV', 'tv'),
(8, 'Heating', 'flame'),
(9, 'Pet Friendly', 'paw-print'),
(10, 'Beach Access', 'waves'),
(11, 'Mountain View', 'mountain'),
(12, 'Breakfast Included', 'coffee'),
(13, 'Gym', 'dumbbell'),
(14, 'Hot Tub', 'bath'),
(15, 'Balcony', 'door-open');

-- ------------------------------------------------------------------------------------
-- properties
-- ------------------------------------------------------------------------------------
INSERT INTO properties (id, host_id, title, description, property_type, room_type, address_line, city, state, country, zip_code, latitude, longitude, price_per_night, cleaning_fee, max_guests, bedrooms, beds, bathrooms, avg_rating, review_count, status, ai_generated_description, created_at, updated_at) VALUES
(1, 2, 'Sunset Beach Villa', 'A breezy villa steps from Baga Beach with a private pool and open-air living area, perfect for groups chasing Goa sunsets.', 'VILLA', 'ENTIRE_PLACE', '12 Baga Beach Road', 'Goa', 'Goa', 'India', '403516', 15.5553, 73.7517, 8500.00, 800.00, 6, 3, 4, 3.0, 5.00, 1, 'ACTIVE', false, now(), now()),
(2, 2, 'Cozy Palolem Studio', 'A compact, well-lit studio a short walk from Palolem Beach, ideal for couples or solo travelers.', 'STUDIO', 'ENTIRE_PLACE', '4 Palolem Lane', 'Goa', 'Goa', 'India', '403702', 15.0100, 74.0233, 2200.00, 200.00, 2, 1, 1, 1.0, 0.00, 0, 'ACTIVE', false, now(), now()),
(3, 3, 'Manali Mountain Cabin', 'A wooden cabin overlooking the Beas river valley, with a wood-fired heater and mountain views from every window.', 'CABIN', 'ENTIRE_PLACE', 'Old Manali Road', 'Manali', 'Himachal Pradesh', 'India', '175131', 32.2432, 77.1892, 4200.00, 400.00, 4, 2, 2, 1.5, 5.00, 1, 'ACTIVE', false, now(), now()),
(4, 3, 'Shimla Pine Cottage', 'A quiet colonial-era cottage surrounded by pine forest, a 10-minute walk from Shimla Ridge.', 'COTTAGE', 'ENTIRE_PLACE', 'Chotta Shimla', 'Shimla', 'Himachal Pradesh', 'India', '171002', 31.1048, 77.1734, 3800.00, 350.00, 5, 2, 3, 2.0, 0.00, 0, 'ACTIVE', false, now(), now()),
(5, 4, 'Jaipur Heritage Haveli Room', 'A private room inside a restored 19th-century haveli in the heart of the Pink City, with rooftop breakfast included.', 'HOUSE', 'PRIVATE_ROOM', 'Johari Bazaar', 'Jaipur', 'Rajasthan', 'India', '302003', 26.9239, 75.8267, 3200.00, 250.00, 2, 1, 1, 1.0, 4.00, 1, 'ACTIVE', false, now(), now()),
(6, 4, 'Udaipur Lake View Suite', 'A serene suite with a private balcony overlooking Lake Pichola, moments from the City Palace.', 'APARTMENT', 'ENTIRE_PLACE', 'Lake Pichola Road', 'Udaipur', 'Rajasthan', 'India', '313001', 24.5854, 73.6835, 5600.00, 500.00, 3, 1, 2, 1.5, 0.00, 0, 'ACTIVE', false, now(), now()),
(7, 2, 'Anjuna Rooftop Loft', 'An airy loft above a working ceramics studio near Anjuna flea market, with a shared rooftop lounge.', 'APARTMENT', 'ENTIRE_PLACE', 'Anjuna Market Road', 'Goa', 'Goa', 'India', '403509', 15.5745, 73.7418, 3100.00, 300.00, 3, 1, 2, 1.0, 0.00, 0, 'INACTIVE', false, now(), now()),
(8, 3, 'Rishikesh Riverside Cottage', 'A peaceful riverside cottage near Laxman Jhula, popular with yoga retreat guests.', 'COTTAGE', 'ENTIRE_PLACE', 'Laxman Jhula Road', 'Rishikesh', 'Uttarakhand', 'India', '249304', 30.1215, 78.3252, 2800.00, 250.00, 4, 2, 2, 1.5, 5.00, 1, 'ACTIVE', false, now(), now());

-- property images (placeholder images, deterministic per property)
INSERT INTO property_images (property_id, url, display_order, created_at) VALUES
(1, 'https://picsum.photos/seed/staysmart-p1-a/800/600', 0, now()),
(1, 'https://picsum.photos/seed/staysmart-p1-b/800/600', 1, now()),
(1, 'https://picsum.photos/seed/staysmart-p1-c/800/600', 2, now()),
(2, 'https://picsum.photos/seed/staysmart-p2-a/800/600', 0, now()),
(2, 'https://picsum.photos/seed/staysmart-p2-b/800/600', 1, now()),
(3, 'https://picsum.photos/seed/staysmart-p3-a/800/600', 0, now()),
(3, 'https://picsum.photos/seed/staysmart-p3-b/800/600', 1, now()),
(4, 'https://picsum.photos/seed/staysmart-p4-a/800/600', 0, now()),
(5, 'https://picsum.photos/seed/staysmart-p5-a/800/600', 0, now()),
(5, 'https://picsum.photos/seed/staysmart-p5-b/800/600', 1, now()),
(6, 'https://picsum.photos/seed/staysmart-p6-a/800/600', 0, now()),
(7, 'https://picsum.photos/seed/staysmart-p7-a/800/600', 0, now()),
(8, 'https://picsum.photos/seed/staysmart-p8-a/800/600', 0, now()),
(8, 'https://picsum.photos/seed/staysmart-p8-b/800/600', 1, now());

-- property amenities
INSERT INTO property_amenities (property_id, amenity_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 5), (1, 10), (1, 4),
(2, 1), (2, 3), (2, 10),
(3, 1), (3, 8), (3, 11), (3, 3),
(4, 8), (4, 11), (4, 12), (4, 1),
(5, 1), (5, 12), (5, 9),
(6, 1), (6, 15), (6, 2),
(7, 1), (7, 15),
(8, 1), (8, 3), (8, 9), (8, 11);

-- ------------------------------------------------------------------------------------
-- bookings (mix of completed / confirmed / cancelled to exercise every status)
-- ------------------------------------------------------------------------------------
INSERT INTO bookings (id, property_id, guest_id, check_in, check_out, guests_count, nights, price_per_night, cleaning_fee, total_price, status, cancellation_reason, cancelled_at, created_at, updated_at) VALUES
(1, 1, 5, CURRENT_DATE - INTERVAL '30 days', CURRENT_DATE - INTERVAL '25 days', 4, 5, 8500.00, 800.00, 43300.00, 'COMPLETED', NULL, NULL, now() - INTERVAL '35 days', now() - INTERVAL '25 days'),
(2, 3, 6, CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE - INTERVAL '17 days', 2, 3, 4200.00, 400.00, 13000.00, 'COMPLETED', NULL, NULL, now() - INTERVAL '25 days', now() - INTERVAL '17 days'),
(3, 8, 7, CURRENT_DATE - INTERVAL '10 days', CURRENT_DATE - INTERVAL '7 days', 2, 3, 2800.00, 250.00, 8650.00, 'COMPLETED', NULL, NULL, now() - INTERVAL '15 days', now() - INTERVAL '7 days'),
(4, 5, 5, CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE - INTERVAL '2 days', 2, 3, 3200.00, 250.00, 9850.00, 'COMPLETED', NULL, NULL, now() - INTERVAL '10 days', now() - INTERVAL '2 days'),
(5, 2, 6, CURRENT_DATE + INTERVAL '10 days', CURRENT_DATE + INTERVAL '14 days', 2, 4, 2200.00, 200.00, 9000.00, 'CONFIRMED', NULL, NULL, now() - INTERVAL '2 days', now() - INTERVAL '2 days'),
(6, 1, 7, CURRENT_DATE + INTERVAL '20 days', CURRENT_DATE + INTERVAL '25 days', 5, 5, 8500.00, 800.00, 43300.00, 'CONFIRMED', NULL, NULL, now() - INTERVAL '1 days', now() - INTERVAL '1 days'),
(7, 6, 5, CURRENT_DATE + INTERVAL '3 days', CURRENT_DATE + INTERVAL '6 days', 2, 3, 5600.00, 500.00, 17300.00, 'CANCELLED', 'Change of travel plans', now() - INTERVAL '1 days', now() - INTERVAL '4 days', now() - INTERVAL '1 days');

-- availability blocks for the CONFIRMED bookings (COMPLETED/CANCELLED stays no longer hold dates)
INSERT INTO availability_blocks (property_id, start_date, end_date, reason, booking_id, created_at) VALUES
(2, CURRENT_DATE + INTERVAL '10 days', CURRENT_DATE + INTERVAL '14 days', 'BOOKED', 5, now()),
(1, CURRENT_DATE + INTERVAL '20 days', CURRENT_DATE + INTERVAL '25 days', 'BOOKED', 6, now()),
(4, CURRENT_DATE + INTERVAL '40 days', CURRENT_DATE + INTERVAL '45 days', 'BLOCKED', NULL, now());

-- ------------------------------------------------------------------------------------
-- reviews (only for COMPLETED bookings, matching the properties' seeded avg_rating/review_count above)
-- ------------------------------------------------------------------------------------
INSERT INTO reviews (id, booking_id, property_id, user_id, rating, comment, created_at, updated_at) VALUES
(1, 1, 1, 5, 5, 'Absolutely stunning villa, the pool and the beach access made our trip unforgettable. Aarav was a fantastic host!', now() - INTERVAL '24 days', now() - INTERVAL '24 days'),
(2, 2, 3, 6, 5, 'The cabin exceeded expectations - waking up to that mountain view every morning was priceless. Very clean and cozy.', now() - INTERVAL '16 days', now() - INTERVAL '16 days'),
(3, 3, 8, 7, 5, 'Peaceful riverside spot, perfect after a long trek. Would book again for a yoga retreat.', now() - INTERVAL '6 days', now() - INTERVAL '6 days'),
(4, 4, 5, 5, 4, 'Charming haveli room full of character, though the street noise picked up in the evenings. Breakfast was excellent.', now() - INTERVAL '1 days', now() - INTERVAL '1 days');

INSERT INTO review_images (review_id, url, display_order) VALUES
(1, 'https://picsum.photos/seed/staysmart-review1-a/600/400', 0),
(2, 'https://picsum.photos/seed/staysmart-review2-a/600/400', 0);

-- ------------------------------------------------------------------------------------
-- AI usage logs (seeded so the admin "AI usage statistics" dashboard has data to show
-- without requiring a live OpenAI key during grading/demo)
-- ------------------------------------------------------------------------------------
INSERT INTO ai_usage_logs (user_id, feature, prompt_tokens, completion_tokens, latency_ms, success, error_message, created_at) VALUES
(5, 'RECOMMENDATION', 180, 120, 950, true, NULL, now() - INTERVAL '6 days'),
(6, 'RECOMMENDATION', 175, 130, 1020, true, NULL, now() - INTERVAL '5 days'),
(5, 'REVIEW_SUMMARY', 320, 90, 870, true, NULL, now() - INTERVAL '5 days'),
(7, 'REVIEW_SUMMARY', 340, 95, 910, true, NULL, now() - INTERVAL '4 days'),
(6, 'CHAT_ASSISTANT', 60, 80, 640, true, NULL, now() - INTERVAL '4 days'),
(6, 'CHAT_ASSISTANT', 75, 110, 700, true, NULL, now() - INTERVAL '4 days'),
(7, 'TRIP_PLANNER', 210, 480, 2100, true, NULL, now() - INTERVAL '3 days'),
(5, 'BUDGET_PLANNER', 150, 210, 1350, true, NULL, now() - INTERVAL '3 days'),
(2, 'DESCRIPTION_GENERATOR', 130, 260, 1500, true, NULL, now() - INTERVAL '2 days'),
(3, 'DESCRIPTION_GENERATOR', 125, 240, 1400, true, NULL, now() - INTERVAL '2 days'),
(6, 'SMART_SEARCH', 90, 60, 780, true, NULL, now() - INTERVAL '1 days'),
(7, 'SMART_SEARCH', 95, 55, 810, false, 'Upstream OpenAI request timed out', now() - INTERVAL '1 days');

-- a short demo chat session for guest.bob
INSERT INTO ai_chat_messages (user_id, session_id, role, content, created_at) VALUES
(6, 'demo-session-bob-1', 'USER', 'Hi! Can you suggest a good weekend trip near Manali?', now() - INTERVAL '4 days'),
(6, 'demo-session-bob-1', 'ASSISTANT', 'Manali is great for a weekend - consider a day trip to Solang Valley for adventure sports, and Old Manali for cafes and river walks. Want a full itinerary?', now() - INTERVAL '4 days'),
(6, 'demo-session-bob-1', 'USER', 'Yes please, for 2 days.', now() - INTERVAL '4 days'),
(6, 'demo-session-bob-1', 'ASSISTANT', 'Day 1: Old Manali cafes + Hadimba Temple + riverside walk. Day 2: Solang Valley adventure sports + Vashisht hot springs in the evening. Pack warm layers, evenings get cold!', now() - INTERVAL '4 days');

-- ------------------------------------------------------------------------------------
-- keep sequences in sync after explicit-id inserts
-- ------------------------------------------------------------------------------------
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('amenities_id_seq', (SELECT MAX(id) FROM amenities));
SELECT setval('properties_id_seq', (SELECT MAX(id) FROM properties));
SELECT setval('property_images_id_seq', (SELECT MAX(id) FROM property_images));
SELECT setval('availability_blocks_id_seq', (SELECT MAX(id) FROM availability_blocks));
SELECT setval('bookings_id_seq', (SELECT MAX(id) FROM bookings));
SELECT setval('reviews_id_seq', (SELECT MAX(id) FROM reviews));
SELECT setval('review_images_id_seq', (SELECT MAX(id) FROM review_images));
SELECT setval('ai_usage_logs_id_seq', (SELECT MAX(id) FROM ai_usage_logs));
SELECT setval('ai_chat_messages_id_seq', (SELECT MAX(id) FROM ai_chat_messages));


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


-- =====================================================================================
-- StaySmart AI - additional sample data (round 3): 6 properties in Ooty, 10 in Coorg,
-- 8 in Hyderabad. Listed under the existing hosts seeded in V2/V3 (no new users this
-- round). Properties only - no bookings/reviews for this batch, so avg_rating/review_count
-- stay at 0.00/0 until real bookings are made against them.
-- =====================================================================================

-- ------------------------------------------------------------------------------------
-- properties (ids 19-42)
-- ------------------------------------------------------------------------------------
INSERT INTO properties (id, host_id, title, description, property_type, room_type, address_line, city, state, country, zip_code, latitude, longitude, price_per_night, cleaning_fee, max_guests, bedrooms, beds, bathrooms, avg_rating, review_count, status, ai_generated_description, created_at, updated_at) VALUES
-- Ooty, Tamil Nadu (19-24)
(19, 3, 'Ooty Botanical Garden Cottage', 'A flower-lined cottage a five-minute walk from the Government Botanical Garden, with a wood-burning fireplace for chilly Nilgiri evenings.', 'COTTAGE', 'ENTIRE_PLACE', 'Botanical Garden Road', 'Ooty', 'Tamil Nadu', 'India', '643001', 11.4102, 76.7057, 4200.00, 350.00, 4, 2, 2, 1.5, 0.00, 0, 'ACTIVE', false, now(), now()),
(20, 3, 'Fernhill Colonial Bungalow', 'A restored British-era bungalow near Fernhill Palace, with high ceilings, a private garden and views over the Nilgiri hills.', 'HOUSE', 'ENTIRE_PLACE', 'Fernhill Road', 'Ooty', 'Tamil Nadu', 'India', '643004', 11.3967, 76.7124, 5800.00, 450.00, 6, 3, 4, 2.5, 0.00, 0, 'ACTIVE', false, now(), now()),
(21, 3, 'Ooty Lake View Villa', 'A spacious villa overlooking Ooty Lake with a private balcony, a short stroll from the boat house and the toy train station.', 'VILLA', 'ENTIRE_PLACE', 'Lake Road', 'Ooty', 'Tamil Nadu', 'India', '643001', 11.4046, 76.6934, 6500.00, 500.00, 6, 3, 3, 2.0, 0.00, 0, 'ACTIVE', false, now(), now()),
(22, 2, 'Wenlock Downs Farm Stay', 'A working farm stay on the rolling grasslands of Wenlock Downs, with home-grown produce and horse-riding trails nearby.', 'FARM_STAY', 'ENTIRE_PLACE', 'Wenlock Downs', 'Ooty', 'Tamil Nadu', 'India', '643005', 11.4200, 76.6700, 3600.00, 300.00, 4, 2, 2, 1.5, 0.00, 0, 'ACTIVE', false, now(), now()),
(23, 3, 'Coonoor Road Tea Cottage', 'A private room in a working tea-estate cottage on the Ooty-Coonoor road, with estate walks and fresh tea tastings.', 'COTTAGE', 'PRIVATE_ROOM', 'Coonoor Road', 'Ooty', 'Tamil Nadu', 'India', '643006', 11.3500, 76.7900, 2400.00, 200.00, 2, 1, 1, 1.0, 0.00, 0, 'ACTIVE', false, now(), now()),
(24, 9, 'Doddabetta Hillside Cabin', 'A wooden cabin on the slopes near Doddabetta Peak, the highest point in the Nilgiris, with panoramic sunrise views.', 'CABIN', 'ENTIRE_PLACE', 'Doddabetta Road', 'Ooty', 'Tamil Nadu', 'India', '643007', 11.4133, 76.7377, 4000.00, 350.00, 3, 2, 2, 1.0, 0.00, 0, 'INACTIVE', false, now(), now()),

-- Coorg, Karnataka (25-34)
(25, 3, 'Madikeri Fort View Homestay', 'A private room in a family homestay overlooking Madikeri Fort, with home-cooked Kodava meals available on request.', 'HOUSE', 'PRIVATE_ROOM', 'Fort Road, Madikeri', 'Coorg', 'Karnataka', 'India', '571201', 12.4218, 75.7402, 2800.00, 250.00, 2, 1, 1, 1.0, 0.00, 0, 'ACTIVE', false, now(), now()),
(26, 3, 'Abbey Falls Plantation Villa', 'A grand villa on a working coffee-and-cardamom plantation near Abbey Falls, with a private pool and estate-guided walks.', 'VILLA', 'ENTIRE_PLACE', 'Abbey Falls Road', 'Coorg', 'Karnataka', 'India', '571201', 12.4478, 75.7332, 7200.00, 550.00, 6, 3, 4, 2.5, 0.00, 0, 'ACTIVE', false, now(), now()),
(27, 9, 'Kushalnagar Riverside Cottage', 'A cottage on the banks of the Kaveri near Kushalnagar, close to the Golden Temple (Namdroling Monastery).', 'COTTAGE', 'ENTIRE_PLACE', 'Kaveri Riverside Road, Kushalnagar', 'Coorg', 'Karnataka', 'India', '571234', 12.5564, 75.9581, 3800.00, 300.00, 4, 2, 2, 1.5, 0.00, 0, 'ACTIVE', false, now(), now()),
(28, 3, 'Virajpet Coffee Estate Bungalow', 'A planter''s bungalow deep in a Virajpet coffee estate, with mist-covered mornings and estate-fresh brews.', 'HOUSE', 'ENTIRE_PLACE', 'Estate Road, Virajpet', 'Coorg', 'Karnataka', 'India', '571218', 12.1959, 75.8064, 4600.00, 400.00, 5, 3, 3, 2.0, 0.00, 0, 'ACTIVE', false, now(), now()),
(29, 2, 'Napoklu Treehouse Retreat', 'An elevated treehouse cabin in Napoklu village, surrounded by areca and coffee groves, with a private deck.', 'CABIN', 'ENTIRE_PLACE', 'Napoklu Village Road', 'Coorg', 'Karnataka', 'India', '571211', 12.3833, 75.8167, 5200.00, 400.00, 2, 1, 1, 1.0, 0.00, 0, 'ACTIVE', false, now(), now()),
(30, 3, 'Kakkabe Tadiandamol Base Cottage', 'A cottage at the base of Tadiandamol, Coorg''s highest peak, popular with trekkers heading up at dawn.', 'COTTAGE', 'ENTIRE_PLACE', 'Kakkabe Village', 'Coorg', 'Karnataka', 'India', '571213', 12.3167, 75.8500, 3400.00, 300.00, 4, 2, 2, 1.0, 0.00, 0, 'ACTIVE', false, now(), now()),
(31, 3, 'Suntikoppa Farm Stay', 'A pepper-and-coffee farm stay near Suntikoppa with a pet-friendly policy and farm-to-table breakfasts.', 'FARM_STAY', 'ENTIRE_PLACE', 'Suntikoppa Road', 'Coorg', 'Karnataka', 'India', '571237', 12.5167, 75.7833, 3200.00, 280.00, 4, 2, 2, 1.5, 0.00, 0, 'ACTIVE', false, now(), now()),
(32, 9, 'Ponnampet Spice Garden Home', 'A private room in a spice-garden home near Ponnampet, with guided walks through pepper, cardamom and vanilla vines.', 'HOUSE', 'PRIVATE_ROOM', 'Ponnampet Road', 'Coorg', 'Karnataka', 'India', '571216', 12.1667, 75.9333, 2600.00, 220.00, 2, 1, 1, 1.0, 0.00, 0, 'ACTIVE', false, now(), now()),
(33, 3, 'Gonikoppal Misty Hills Cottage', 'A quiet cottage in the misty hills near Gonikoppal, currently paused while the host completes renovations.', 'COTTAGE', 'ENTIRE_PLACE', 'Gonikoppal Road', 'Coorg', 'Karnataka', 'India', '571213', 12.2000, 75.8500, 3900.00, 320.00, 4, 2, 2, 1.5, 0.00, 0, 'INACTIVE', false, now(), now()),
(34, 3, 'Ammathi Coffee Blossom Villa', 'A villa timed for coffee-blossom season near Ammathi, with a private pool and a wraparound plantation view.', 'VILLA', 'ENTIRE_PLACE', 'Ammathi Road', 'Coorg', 'Karnataka', 'India', '571212', 12.2500, 75.7833, 6800.00, 500.00, 6, 3, 4, 2.5, 0.00, 0, 'ACTIVE', false, now(), now()),

-- Hyderabad, Telangana (35-42)
(35, 9, 'Banjara Hills Sky Apartment', 'A high-floor apartment in upscale Banjara Hills with skyline views, close to Road No. 12''s restaurants and boutiques.', 'APARTMENT', 'ENTIRE_PLACE', 'Road No. 12, Banjara Hills', 'Hyderabad', 'Telangana', 'India', '500034', 17.4126, 78.4344, 5200.00, 400.00, 4, 2, 2, 2.0, 0.00, 0, 'ACTIVE', false, now(), now()),
(36, 9, 'Jubilee Hills Designer Condo', 'A designer-furnished condo in Jubilee Hills with a rooftop pool and gym, minutes from Film Nagar.', 'CONDO', 'ENTIRE_PLACE', 'Road No. 45, Jubilee Hills', 'Hyderabad', 'Telangana', 'India', '500033', 17.4293, 78.4073, 6200.00, 500.00, 4, 2, 2, 2.0, 0.00, 0, 'ACTIVE', false, now(), now()),
(37, 9, 'Gachibowli Tech Park Studio', 'A compact studio near the Gachibowli IT corridor, built for short business stays close to the tech parks.', 'STUDIO', 'ENTIRE_PLACE', 'Nanakramguda Road, Gachibowli', 'Hyderabad', 'Telangana', 'India', '500032', 17.4401, 78.3489, 2800.00, 250.00, 2, 1, 1, 1.0, 0.00, 0, 'ACTIVE', false, now(), now()),
(38, 9, 'Hitech City Skyline Apartment', 'A modern apartment overlooking the Hitech City skyline, walking distance from Cyber Towers and the metro.', 'APARTMENT', 'ENTIRE_PLACE', 'Cyber Towers Road, Hitech City', 'Hyderabad', 'Telangana', 'India', '500081', 17.4483, 78.3915, 4800.00, 400.00, 3, 2, 2, 1.5, 0.00, 0, 'ACTIVE', false, now(), now()),
(39, 4, 'Charminar Old City Heritage Room', 'A private room in a Nizami-era haveli in the Old City, footsteps from Charminar and Laad Bazaar''s bangle shops.', 'HOUSE', 'PRIVATE_ROOM', 'Laad Bazaar, Old City', 'Hyderabad', 'Telangana', 'India', '500002', 17.3616, 78.4747, 2200.00, 200.00, 2, 1, 1, 1.0, 0.00, 0, 'ACTIVE', false, now(), now()),
(40, 4, 'Secunderabad Cantonment Bungalow', 'A colonial cantonment-era bungalow near Sarojini Devi Road, with a wraparound veranda and a large private garden.', 'HOUSE', 'ENTIRE_PLACE', 'Sarojini Devi Road, Secunderabad', 'Hyderabad', 'Telangana', 'India', '500003', 17.4399, 78.4983, 4400.00, 380.00, 5, 3, 3, 2.0, 0.00, 0, 'ACTIVE', false, now(), now()),
(41, 9, 'Kondapur Corporate Condo', 'A well-connected condo near Kondapur''s Botanical Garden Road, currently off-market while the host relocates.', 'CONDO', 'ENTIRE_PLACE', 'Botanical Garden Road, Kondapur', 'Hyderabad', 'Telangana', 'India', '500084', 17.4614, 78.3606, 3600.00, 300.00, 3, 1, 2, 1.0, 0.00, 0, 'INACTIVE', false, now(), now()),
(42, 9, 'Madhapur Metro View Apartment', 'An apartment right by the Madhapur metro station, with easy access to Hitech City and Jubilee Hills alike.', 'APARTMENT', 'ENTIRE_PLACE', 'Ayyappa Society Road, Madhapur', 'Hyderabad', 'Telangana', 'India', '500081', 17.4482, 78.3915, 4000.00, 350.00, 3, 2, 2, 1.5, 0.00, 0, 'ACTIVE', false, now(), now());

-- ------------------------------------------------------------------------------------
-- property images (auto-increment ids, continues on from the existing rows)
-- ------------------------------------------------------------------------------------
INSERT INTO property_images (property_id, url, display_order, created_at) VALUES
(19, 'https://picsum.photos/seed/staysmart-p19-a/800/600', 0, now()),
(19, 'https://picsum.photos/seed/staysmart-p19-b/800/600', 1, now()),
(20, 'https://picsum.photos/seed/staysmart-p20-a/800/600', 0, now()),
(20, 'https://picsum.photos/seed/staysmart-p20-b/800/600', 1, now()),
(21, 'https://picsum.photos/seed/staysmart-p21-a/800/600', 0, now()),
(22, 'https://picsum.photos/seed/staysmart-p22-a/800/600', 0, now()),
(23, 'https://picsum.photos/seed/staysmart-p23-a/800/600', 0, now()),
(24, 'https://picsum.photos/seed/staysmart-p24-a/800/600', 0, now()),
(25, 'https://picsum.photos/seed/staysmart-p25-a/800/600', 0, now()),
(26, 'https://picsum.photos/seed/staysmart-p26-a/800/600', 0, now()),
(26, 'https://picsum.photos/seed/staysmart-p26-b/800/600', 1, now()),
(27, 'https://picsum.photos/seed/staysmart-p27-a/800/600', 0, now()),
(28, 'https://picsum.photos/seed/staysmart-p28-a/800/600', 0, now()),
(29, 'https://picsum.photos/seed/staysmart-p29-a/800/600', 0, now()),
(30, 'https://picsum.photos/seed/staysmart-p30-a/800/600', 0, now()),
(31, 'https://picsum.photos/seed/staysmart-p31-a/800/600', 0, now()),
(32, 'https://picsum.photos/seed/staysmart-p32-a/800/600', 0, now()),
(33, 'https://picsum.photos/seed/staysmart-p33-a/800/600', 0, now()),
(34, 'https://picsum.photos/seed/staysmart-p34-a/800/600', 0, now()),
(34, 'https://picsum.photos/seed/staysmart-p34-b/800/600', 1, now()),
(35, 'https://picsum.photos/seed/staysmart-p35-a/800/600', 0, now()),
(36, 'https://picsum.photos/seed/staysmart-p36-a/800/600', 0, now()),
(36, 'https://picsum.photos/seed/staysmart-p36-b/800/600', 1, now()),
(37, 'https://picsum.photos/seed/staysmart-p37-a/800/600', 0, now()),
(38, 'https://picsum.photos/seed/staysmart-p38-a/800/600', 0, now()),
(39, 'https://picsum.photos/seed/staysmart-p39-a/800/600', 0, now()),
(40, 'https://picsum.photos/seed/staysmart-p40-a/800/600', 0, now()),
(41, 'https://picsum.photos/seed/staysmart-p41-a/800/600', 0, now()),
(42, 'https://picsum.photos/seed/staysmart-p42-a/800/600', 0, now());

-- ------------------------------------------------------------------------------------
-- property amenities (amenity ids reference the fixed list seeded in V2)
-- ------------------------------------------------------------------------------------
INSERT INTO property_amenities (property_id, amenity_id) VALUES
(19, 1), (19, 8), (19, 11), (19, 3), (19, 12),
(20, 1), (20, 8), (20, 11), (20, 4), (20, 12),
(21, 1), (21, 8), (21, 11), (21, 15),
(22, 1), (22, 8), (22, 11), (22, 9),
(23, 1), (23, 8), (23, 12),
(24, 1), (24, 8), (24, 11),
(25, 1), (25, 8), (25, 12),
(26, 1), (26, 8), (26, 11), (26, 5), (26, 4),
(27, 1), (27, 3), (27, 8),
(28, 1), (28, 3), (28, 8), (28, 11), (28, 12),
(29, 1), (29, 8), (29, 11),
(30, 1), (30, 8), (30, 11),
(31, 1), (31, 3), (31, 9),
(32, 1), (32, 12),
(33, 1), (33, 8), (33, 11),
(34, 1), (34, 8), (34, 11), (34, 5), (34, 12),
(35, 1), (35, 2), (35, 7), (35, 13), (35, 15),
(36, 1), (36, 2), (36, 7), (36, 13), (36, 5), (36, 15),
(37, 1), (37, 2), (37, 7),
(38, 1), (38, 2), (38, 7), (38, 13), (38, 4),
(39, 1), (39, 2), (39, 12),
(40, 1), (40, 2), (40, 4), (40, 3),
(41, 1), (41, 2), (41, 7), (41, 4),
(42, 1), (42, 2), (42, 7), (42, 15);

-- ------------------------------------------------------------------------------------
-- keep sequences in sync after explicit-id inserts
-- ------------------------------------------------------------------------------------
SELECT setval('properties_id_seq', (SELECT MAX(id) FROM properties));
SELECT setval('property_images_id_seq', (SELECT MAX(id) FROM property_images));
