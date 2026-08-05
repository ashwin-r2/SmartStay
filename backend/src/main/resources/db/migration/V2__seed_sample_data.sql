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
