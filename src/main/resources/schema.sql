CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255),
    avatar_url VARCHAR(255),
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_username (username),
    UNIQUE KEY uk_users_email (email)
);

CREATE TABLE IF NOT EXISTS group_match (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    sport VARCHAR(255),
    skill_level VARCHAR(255),
    max_participants INT NOT NULL,
    scheduled_at DATETIME(6),
    location VARCHAR(255),
    latitude DOUBLE,
    longitude DOUBLE,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS equipment (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DOUBLE NOT NULL,
    available BIT(1) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS user_preferences (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    sports TEXT,
    skill_level VARCHAR(255),
    age_min INT NOT NULL,
    age_max INT NOT NULL,
    latitude DOUBLE,
    longitude DOUBLE,
    city VARCHAR(255),
    radius_km DOUBLE NOT NULL,
    availability TEXT,
    group_size_min INT NOT NULL,
    group_size_max INT NOT NULL,
    engagement_level VARCHAR(255),
    languages TEXT,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_preferences_user_id (user_id),
    CONSTRAINT fk_user_preferences_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS user_participant (
    user_id BIGINT NOT NULL,
    group_match_id BIGINT NOT NULL,
    role VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    joined_at DATETIME(6) NOT NULL,
    score INT NOT NULL,
    PRIMARY KEY (user_id, group_match_id),
    CONSTRAINT fk_user_participant_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_participant_group FOREIGN KEY (group_match_id) REFERENCES group_match (id)
);

CREATE TABLE IF NOT EXISTS messages (
    id BIGINT NOT NULL AUTO_INCREMENT,
    content TEXT NOT NULL,
    sent_at DATETIME(6) NOT NULL,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT,
    group_match_id BIGINT,
    is_read BIT(1) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_messages_sender FOREIGN KEY (sender_id) REFERENCES users (id),
    CONSTRAINT fk_messages_receiver FOREIGN KEY (receiver_id) REFERENCES users (id),
    CONSTRAINT fk_messages_group FOREIGN KEY (group_match_id) REFERENCES group_match (id)
);

CREATE TABLE IF NOT EXISTS match_results (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    matched_group_id BIGINT NOT NULL,
    compatibility_score DOUBLE NOT NULL,
    sport_score DOUBLE NOT NULL,
    location_score DOUBLE NOT NULL,
    skill_score DOUBLE NOT NULL,
    availability_score DOUBLE NOT NULL,
    group_size_score DOUBLE NOT NULL,
    distance_km DOUBLE NOT NULL,
    computed_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_match_results_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_match_results_group FOREIGN KEY (matched_group_id) REFERENCES group_match (id)
);

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT NOT NULL AUTO_INCREMENT,
    order_date DATETIME(6),
    total_amount DOUBLE NOT NULL,
    status VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS order_equipment (
    order_id BIGINT NOT NULL,
    equipment_id BIGINT NOT NULL,
    PRIMARY KEY (order_id, equipment_id),
    CONSTRAINT fk_order_equipment_order FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT fk_order_equipment_equipment FOREIGN KEY (equipment_id) REFERENCES equipment (id)
);
