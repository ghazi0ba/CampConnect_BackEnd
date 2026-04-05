-- Test data for CampConnect database

-- Insert users
INSERT INTO users (username, email, password, avatar_url) VALUES
('john_doe', 'john@example.com', '$2a$10$encryptedpassword1', 'https://example.com/avatar1.jpg'),
('jane_smith', 'jane@example.com', '$2a$10$encryptedpassword2', 'https://example.com/avatar2.jpg'),
('mike_johnson', 'mike@example.com', '$2a$10$encryptedpassword3', 'https://example.com/avatar3.jpg'),
('sarah_wilson', 'sarah@example.com', '$2a$10$encryptedpassword4', 'https://example.com/avatar4.jpg'),
('alex_brown', 'alex@example.com', '$2a$10$encryptedpassword5', 'https://example.com/avatar5.jpg'),
('emma_davis', 'emma@example.com', '$2a$10$encryptedpassword6', 'https://example.com/avatar6.jpg'),
('chris_miller', 'chris@example.com', '$2a$10$encryptedpassword7', 'https://example.com/avatar7.jpg'),
('lisa_garcia', 'lisa@example.com', '$2a$10$encryptedpassword8', 'https://example.com/avatar8.jpg');

-- Insert group matches
INSERT INTO group_match (name, description, sport, skill_level, max_participants, scheduled_at, location, latitude, longitude, created_at) VALUES
('Football Match Downtown', 'Casual football game in the city center', 'football', 'INTERMEDIATE', 22, '2026-04-10 14:00:00', 'Central Park', 40.7128, -74.0060, NOW()),
('Tennis Tournament', 'Competitive tennis tournament for advanced players', 'tennis', 'ADVANCED', 8, '2026-04-15 10:00:00', 'Tennis Club', 40.7589, -73.9851, NOW()),
('Basketball Pickup Game', 'Fun basketball game for all levels', 'basketball', 'BEGINNER', 10, '2026-04-12 16:00:00', 'Community Court', 40.7505, -73.9934, NOW()),
('Soccer Training Session', 'Training session for soccer enthusiasts', 'soccer', 'INTERMEDIATE', 15, '2026-04-08 18:00:00', 'Sports Complex', 40.7282, -73.7949, NOW()),
('Volleyball Beach Day', 'Beach volleyball for intermediate players', 'volleyball', 'INTERMEDIATE', 12, '2026-04-20 11:00:00', 'Coney Island Beach', 40.5755, -73.9707, NOW());

-- Insert user preferences
INSERT INTO user_preferences (user_id, sports, skill_level, age_min, age_max, latitude, longitude, city, radius_km, availability, group_size_min, group_size_max) VALUES
(1, 'football,soccer', 'INTERMEDIATE', 20, 35, 40.7128, -74.0060, 'New York', 25.0, 'WEEKEND,AFTERNOON', 8, 22),
(2, 'tennis,badminton', 'ADVANCED', 25, 40, 40.7589, -73.9851, 'New York', 30.0, 'MORNING,WEEKEND', 4, 8),
(3, 'basketball,football', 'BEGINNER', 18, 30, 40.7505, -73.9934, 'New York', 20.0, 'AFTERNOON,EVENING', 6, 12),
(4, 'soccer,volleyball', 'INTERMEDIATE', 22, 35, 40.7282, -73.7949, 'Queens', 15.0, 'WEEKEND', 8, 16),
(5, 'tennis,golf', 'ADVANCED', 28, 45, 40.5755, -73.9707, 'Brooklyn', 40.0, 'MORNING,AFTERNOON', 2, 6),
(6, 'basketball,volleyball', 'BEGINNER', 19, 28, 40.7128, -74.0060, 'New York', 10.0, 'AFTERNOON,WEEKEND', 4, 10),
(7, 'football,soccer', 'INTERMEDIATE', 24, 38, 40.7589, -73.9851, 'Manhattan', 35.0, 'EVENING,WEEKEND', 10, 20),
(8, 'tennis,badminton', 'ADVANCED', 26, 42, 40.7505, -73.9934, 'New York', 25.0, 'MORNING,WEEKEND', 6, 12);

-- Insert user participants (join some groups)
INSERT INTO user_participant (user_id, group_match_id, role, status, joined_at, score) VALUES (1, 1, 'MEMBER', 'ACCEPTED', '2026-04-01 10:00:00', 85);
INSERT INTO user_participant (user_id, group_match_id, role, status, joined_at, score) VALUES (2, 2, 'ADMIN', 'ACCEPTED', '2026-04-02 11:00:00', 95);
INSERT INTO user_participant (user_id, group_match_id, role, status, joined_at, score) VALUES (3, 3, 'MEMBER', 'ACCEPTED', '2026-04-03 12:00:00', 78);
INSERT INTO user_participant (user_id, group_match_id, role, status, joined_at, score) VALUES (4, 4, 'MEMBER', 'ACCEPTED', '2026-04-04 13:00:00', 88);
INSERT INTO user_participant (user_id, group_match_id, role, status, joined_at, score) VALUES (5, 5, 'ADMIN', 'ACCEPTED', '2026-04-05 14:00:00', 92);
INSERT INTO user_participant (user_id, group_match_id, role, status, joined_at, score) VALUES (6, 1, 'MEMBER', 'ACCEPTED', '2026-04-01 15:00:00', 76);
INSERT INTO user_participant (user_id, group_match_id, role, status, joined_at, score) VALUES (7, 2, 'MEMBER', 'PENDING', '2026-04-06 16:00:00', 82);
INSERT INTO user_participant (user_id, group_match_id, role, status, joined_at, score) VALUES (8, 3, 'MEMBER', 'ACCEPTED', '2026-04-07 17:00:00', 89);
INSERT INTO user_participant (user_id, group_match_id, role, status, joined_at, score) VALUES (1, 4, 'MEMBER', 'ACCEPTED', '2026-04-08 18:00:00', 91);
INSERT INTO user_participant (user_id, group_match_id, role, status, joined_at, score) VALUES (2, 5, 'MEMBER', 'ACCEPTED', '2026-04-09 19:00:00', 87);

-- Insert messages
INSERT INTO messages (content, sent_at, sender_id, receiver_id, group_match_id, is_read) VALUES
('Hey everyone, excited for the football match!', '2026-04-01 10:30:00', 1, NULL, 1, true),
('Count me in for tennis tournament', '2026-04-02 11:30:00', 2, NULL, 2, true),
('Looking forward to the basketball game!', '2026-04-03 12:30:00', 3, NULL, 3, false),
('Soccer training should be great', '2026-04-04 13:30:00', 4, NULL, 4, true),
('Beach volleyball anyone?', '2026-04-05 14:30:00', 5, NULL, 5, false),
('Hi John, want to practice together?', '2026-04-06 15:30:00', 2, 1, NULL, false),
('Sure Jane, when are you free?', '2026-04-06 16:00:00', 1, 2, NULL, false),
('Great game yesterday!', '2026-04-07 17:30:00', 3, 6, NULL, true);