-- MySQL 8.0 Schema for ECS (E-Commerce System)
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
  `role` BIGINT DEFAULT NULL,
  `tenant_id` BIGINT DEFAULT NULL,
  `address` VARCHAR(1024),
  `phone_number` VARCHAR(255),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_email` (`email`),
  INDEX `idx_users_tenant_id` (`tenant_id`),
  INDEX `idx_users_deleted_at` (`deleted_at`),
  CONSTRAINT `fk_users_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `tenants` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table Definition for Categories
CREATE TABLE IF NOT EXISTS `categories` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `name` VARCHAR(255) NOT NULL,
  `description` VARCHAR(1024),
  `image_url` VARCHAR(255),
  `tenant_id` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_categories_tenant_id` (`tenant_id`),
  INDEX `idx_categories_deleted_at` (`deleted_at`),
  CONSTRAINT `fk_categories_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `tenants` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table Definition for Products
CREATE TABLE IF NOT EXISTS `products` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `name` VARCHAR(255) NOT NULL,
  `description` VARCHAR(2048),
  `price` DECIMAL(19,2) NOT NULL,
  `stock` INT DEFAULT 0,
  `sku` VARCHAR(255),
  `image_url` VARCHAR(255),
  `is_active` BOOLEAN DEFAULT TRUE,
  `category_id` BIGINT DEFAULT NULL,
  `tenant_id` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_products_category_id` (`category_id`),
  INDEX `idx_products_tenant_id` (`tenant_id`),
  INDEX `idx_products_deleted_at` (`deleted_at`),
  CONSTRAINT `fk_products_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_products_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `tenants` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table Definition for Promotions (defined before orders due to foreign key)
CREATE TABLE IF NOT EXISTS `promotions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `name` VARCHAR(255) NOT NULL,
  `code` VARCHAR(255) NOT NULL,
  `promotion_type` VARCHAR(50) NOT NULL,
  `discount_value` DECIMAL(19,2) DEFAULT NULL,
  `min_purchase_amount` DECIMAL(19,2) DEFAULT NULL,
  `max_discount_amount` DECIMAL(19,2) DEFAULT NULL,
  `start_date` DATETIME(6) DEFAULT NULL,
  `end_date` DATETIME(6) DEFAULT NULL,
  `is_active` BOOLEAN DEFAULT TRUE,
  `usage_limit` INT DEFAULT NULL,
  `used_count` INT DEFAULT 0,
  `tenant_id` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_promotions_tenant_id` (`tenant_id`),
  INDEX `idx_promotions_deleted_at` (`deleted_at`),
  CONSTRAINT `fk_promotions_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `tenants` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table Definition for Orders
CREATE TABLE IF NOT EXISTS `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `order_number` VARCHAR(255) UNIQUE,
  `user_id` BIGINT NOT NULL,
  `order_status` BIGINT DEFAULT NULL,
  `payment_status` BIGINT DEFAULT NULL,
  `total_amount` DECIMAL(19,2) DEFAULT NULL,
  `shipping_address` VARCHAR(1024),
  `billing_address` VARCHAR(1024),
  `payment_method` VARCHAR(255),
  `tenant_id` BIGINT DEFAULT NULL,
  `promotion_id` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_orders_order_number` (`order_number`),
  INDEX `idx_orders_user_id` (`user_id`),
  INDEX `idx_orders_tenant_id` (`tenant_id`),
  INDEX `idx_orders_promotion_id` (`promotion_id`),
  INDEX `idx_orders_deleted_at` (`deleted_at`),
  CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_orders_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `tenants` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_orders_promotion` FOREIGN KEY (`promotion_id`) REFERENCES `promotions` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table Definition for Order Items
CREATE TABLE IF NOT EXISTS `order_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `order_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `quantity` INT NOT NULL,
  `unit_price` DECIMAL(19,2) NOT NULL,
  `total_price` DECIMAL(19,2) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_order_items_order_id` (`order_id`),
  INDEX `idx_order_items_product_id` (`product_id`),
  CONSTRAINT `fk_order_items_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_order_items_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table Definition for Cart Items
CREATE TABLE IF NOT EXISTS `cart_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `user_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `quantity` INT NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  INDEX `idx_cart_items_user_id` (`user_id`),
  INDEX `idx_cart_items_product_id` (`product_id`),
  CONSTRAINT `fk_cart_items_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_cart_items_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table Definition for Reviews
CREATE TABLE IF NOT EXISTS `reviews` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `user_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `rating` INT NOT NULL CHECK (`rating` >= 1 AND `rating` <= 5),
  `comment` VARCHAR(1024),
  PRIMARY KEY (`id`),
  INDEX `idx_reviews_user_id` (`user_id`),
  INDEX `idx_reviews_product_id` (`product_id`),
  INDEX `idx_reviews_deleted_at` (`deleted_at`),
  CONSTRAINT `fk_reviews_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_reviews_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table Definition for Refunds
CREATE TABLE IF NOT EXISTS `refunds` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `order_id` BIGINT NOT NULL,
  `status` BIGINT DEFAULT NULL,
  `refund_amount` DECIMAL(19,2) DEFAULT NULL,
  `reason` VARCHAR(1024),
  `approved_by` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_refunds_order_id` (`order_id`),
  INDEX `idx_refunds_approved_by` (`approved_by`),
  INDEX `idx_refunds_deleted_at` (`deleted_at`),
  CONSTRAINT `fk_refunds_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_refunds_approved_by` FOREIGN KEY (`approved_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
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

