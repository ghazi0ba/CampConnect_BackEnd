-- User Preferences Table
-- Created for storing user location and sports preferences

CREATE TABLE IF NOT EXISTS user_preferences (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    sports TEXT,
    skill_level VARCHAR(20),
    age_min INT,
    age_max INT,
    latitude DOUBLE,
    longitude DOUBLE,
    city VARCHAR(255),
    radius_km DOUBLE DEFAULT 50.0,
    availability TEXT,
    group_size_min INT,
    group_size_max INT,
    engagement_level VARCHAR(20),
    languages TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    SPATIAL INDEX idx_location (latitude, longitude)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create trigger for updated_at
DELIMITER $$
CREATE TRIGGER IF NOT EXISTS user_preferences_update_trigger
BEFORE UPDATE ON user_preferences
FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$
DELIMITER ;
