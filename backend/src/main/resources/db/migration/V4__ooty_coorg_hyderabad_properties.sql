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
