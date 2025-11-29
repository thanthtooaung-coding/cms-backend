-- MySQL 8.0 Schema for BMS (Booking Management System)
-- Multi-tenant support with Tenants table

-- Table Definition for Tenants
-- Represents different tenants in a multi-tenant architecture.
CREATE TABLE IF NOT EXISTS `tenants` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `is_active` BOOLEAN DEFAULT TRUE,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `deleted_at` DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_tenants_deleted_at` (`deleted_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table Definition for Users
CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `email` VARCHAR(255) UNIQUE,
  `name` VARCHAR(255),
  `otp` VARCHAR(255),
  `otp_generated_time` DATETIME(6) DEFAULT NULL,
  `password` VARCHAR(255),
  `profile_url` VARCHAR(255),
  `role` VARCHAR(50) DEFAULT NULL,
  `tenant_id` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_email` (`email`),
  INDEX `idx_users_tenant_id` (`tenant_id`),
  INDEX `idx_users_deleted_at` (`deleted_at`),
  CONSTRAINT `fk_users_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `tenants` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table Definition for Movies
CREATE TABLE IF NOT EXISTS `movies` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `description` VARCHAR(2048),
  `director` VARCHAR(255),
  `duration` VARCHAR(255),
  `language` VARCHAR(50) DEFAULT NULL,
  `movie_cast` VARCHAR(1024),
  `movie_poster_url` VARCHAR(255),
  `rating` VARCHAR(10) DEFAULT NULL,
  `release_date` DATE,
  `status` VARCHAR(50) DEFAULT NULL,
  `title` VARCHAR(255),
  `trailer_url` VARCHAR(255),
  `tenant_id` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_movies_tenant_id` (`tenant_id`),
  INDEX `idx_movies_deleted_at` (`deleted_at`),
  CONSTRAINT `fk_movies_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `tenants` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table Definition for Movie Genres
CREATE TABLE IF NOT EXISTS `movie_genres` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `description` VARCHAR(255),
  `name` VARCHAR(255),
  `tenant_id` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_movie_genres_tenant_id` (`tenant_id`),
  INDEX `idx_movie_genres_deleted_at` (`deleted_at`),
  CONSTRAINT `fk_movie_genres_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `tenants` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table Definition for Movie Has Genres (Many-to-Many)
CREATE TABLE IF NOT EXISTS `movie_has_genres` (
  `movie_id` BIGINT NOT NULL,
  `genre_id` BIGINT NOT NULL,
  PRIMARY KEY (`movie_id`, `genre_id`),
  INDEX `idx_movie_has_genres_genre_id` (`genre_id`),
  CONSTRAINT `fk_moviegenre_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_moviegenre_genre` FOREIGN KEY (`genre_id`) REFERENCES `movie_genres` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table Definition for Theaters
CREATE TABLE IF NOT EXISTS `theaters` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `total_basic_price` DECIMAL(38,2) DEFAULT NULL,
  `total_basic_rows` INT DEFAULT NULL,
  `capacity` INT NOT NULL,
  `total_economy_price` DECIMAL(38,2) DEFAULT NULL,
  `total_economy_rows` INT DEFAULT NULL,
  `location` VARCHAR(255),
  `name` VARCHAR(255),
  `total_premium_price` DECIMAL(38,2) DEFAULT NULL,
  `total_premium_rows` INT DEFAULT NULL,
  `total_regular_price` DECIMAL(38,2) DEFAULT NULL,
  `total_regular_rows` INT DEFAULT NULL,
  `seat_column` INT DEFAULT NULL,
  `seat_row` INT DEFAULT NULL,
  `tenant_id` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_theaters_tenant_id` (`tenant_id`),
  INDEX `idx_theaters_deleted_at` (`deleted_at`),
  CONSTRAINT `fk_theaters_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `tenants` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table Definition for Showtimes
CREATE TABLE IF NOT EXISTS `showtimes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `seats_available` INT NOT NULL,
  `showtime_date` DATE NOT NULL,
  `showtime_time` TIME(6) NOT NULL,
  `status` VARCHAR(50) DEFAULT NULL,
  `movie_id` BIGINT NOT NULL,
  `theater_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_showtimes_movie_id` (`movie_id`),
  INDEX `idx_showtimes_theater_id` (`theater_id`),
  INDEX `idx_showtimes_deleted_at` (`deleted_at`),
  CONSTRAINT `fk_showtime_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_showtime_theater` FOREIGN KEY (`theater_id`) REFERENCES `theaters` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table Definition for Bookings
CREATE TABLE IF NOT EXISTS `bookings` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `booking_id` VARCHAR(255),
  `booking_status` VARCHAR(50) DEFAULT NULL,
  `payment_status` VARCHAR(50) DEFAULT NULL,
  `total_price` DECIMAL(38,2) DEFAULT NULL,
  `user_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_bookings_user_id` (`user_id`),
  INDEX `idx_bookings_deleted_at` (`deleted_at`),
  CONSTRAINT `fk_booking_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table Definition for Booked Seats
CREATE TABLE IF NOT EXISTS `booked_seats` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `price` DECIMAL(38,2) NOT NULL,
  `seat_number` VARCHAR(255) NOT NULL,
  `booking_id` BIGINT NOT NULL,
  `showtime_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_booked_seats_booking_id` (`booking_id`),
  INDEX `idx_booked_seats_showtime_id` (`showtime_id`),
  INDEX `idx_booked_seats_deleted_at` (`deleted_at`),
  CONSTRAINT `fk_bookedseat_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_bookedseat_showtime` FOREIGN KEY (`showtime_id`) REFERENCES `showtimes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table Definition for Booking Refund Records
CREATE TABLE IF NOT EXISTS `booking_refund_records` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `status` VARCHAR(50) DEFAULT NULL,
  `approved_by_id` BIGINT DEFAULT NULL,
  `booking_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_booking_refund_records_booking_id` (`booking_id`),
  INDEX `idx_booking_refund_records_approved_by_id` (`approved_by_id`),
  INDEX `idx_booking_refund_records_deleted_at` (`deleted_at`),
  CONSTRAINT `fk_refund_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_refund_user` FOREIGN KEY (`approved_by_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table Definition for Food Categories
CREATE TABLE IF NOT EXISTS `food_categories` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `description` VARCHAR(255),
  `name` VARCHAR(255),
  `tenant_id` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_food_categories_tenant_id` (`tenant_id`),
  INDEX `idx_food_categories_deleted_at` (`deleted_at`),
  CONSTRAINT `fk_food_categories_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `tenants` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table Definition for Food
CREATE TABLE IF NOT EXISTS `food` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `allergens` VARCHAR(255),
  `category` ENUM('Drinks','Food','Snacks') DEFAULT NULL,
  `description` VARCHAR(255),
  `name` VARCHAR(255),
  `photo_url` VARCHAR(255),
  `price` DOUBLE NOT NULL,
  `tenant_id` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_food_tenant_id` (`tenant_id`),
  INDEX `idx_food_deleted_at` (`deleted_at`),
  CONSTRAINT `fk_food_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `tenants` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table Definition for Combo
CREATE TABLE IF NOT EXISTS `combo` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `combo_name` VARCHAR(255),
  `combo_price` DOUBLE NOT NULL,
  `photo_url` VARCHAR(255),
  `tenant_id` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_combo_tenant_id` (`tenant_id`),
  INDEX `idx_combo_deleted_at` (`deleted_at`),
  CONSTRAINT `fk_combo_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `tenants` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table Definition for Combo Foods (Many-to-Many)
CREATE TABLE IF NOT EXISTS `combo_foods` (
  `combo_id` BIGINT NOT NULL,
  `food_id` BIGINT NOT NULL,
  PRIMARY KEY (`combo_id`, `food_id`),
  INDEX `idx_combo_foods_food_id` (`food_id`),
  CONSTRAINT `fk_combofood_combo` FOREIGN KEY (`combo_id`) REFERENCES `combo` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_combofood_food` FOREIGN KEY (`food_id`) REFERENCES `food` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table Definition for Food Orders
CREATE TABLE IF NOT EXISTS `food_orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `payment_status` VARCHAR(50) DEFAULT NULL,
  `total_price` DECIMAL(38,2) DEFAULT NULL,
  `user_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_food_orders_user_id` (`user_id`),
  INDEX `idx_food_orders_deleted_at` (`deleted_at`),
  CONSTRAINT `fk_foodorder_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table Definition for Food Order Items
CREATE TABLE IF NOT EXISTS `food_order_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `price` DECIMAL(38,2) NOT NULL,
  `quantity` INT NOT NULL,
  `combo_id` BIGINT DEFAULT NULL,
  `food_id` BIGINT DEFAULT NULL,
  `food_order_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_food_order_items_combo_id` (`combo_id`),
  INDEX `idx_food_order_items_food_id` (`food_id`),
  INDEX `idx_food_order_items_food_order_id` (`food_order_id`),
  INDEX `idx_food_order_items_deleted_at` (`deleted_at`),
  CONSTRAINT `fk_foodorderitem_combo` FOREIGN KEY (`combo_id`) REFERENCES `combo` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_foodorderitem_food` FOREIGN KEY (`food_id`) REFERENCES `food` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_foodorderitem_foodorder` FOREIGN KEY (`food_order_id`) REFERENCES `food_orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table Definition for Configurations
CREATE TABLE IF NOT EXISTS `configurations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `code` VARCHAR(255) NOT NULL,
  `value` VARCHAR(255) NOT NULL,
  `tenant_id` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configurations_code_tenant` (`code`, `tenant_id`),
  INDEX `idx_configurations_tenant_id` (`tenant_id`),
  INDEX `idx_configurations_deleted_at` (`deleted_at`),
  CONSTRAINT `fk_configurations_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `tenants` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
