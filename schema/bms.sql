-- ENUM types for various status and type fields.

CREATE TYPE "booking_status" AS ENUM (
  'Pending',
  'Confirmed',
  'Cancelled'
);

CREATE TYPE "payment_status" AS ENUM (
  'Pending',
  'Paid',
  'Refunded'
);

CREATE TYPE "refund_status" AS ENUM (
  'Pending',
  'Approved',
  'Rejected'
);

CREATE TYPE "movie_language" AS ENUM (
  'English',
  'Spanish',
  'French',
  'German',
  'Mandarin'
);

CREATE TYPE "movie_rating" AS ENUM (
  'G',
  'PG',
  'PG-13',
  'R',
  'NC-17'
);

CREATE TYPE "movie_status" AS ENUM (
  'Coming Soon',
  'Now Playing',
  'Archived'
);

CREATE TYPE "showtime_status" AS ENUM (
  'Scheduled',
  'Cancelled',
  'Completed'
);

CREATE TYPE "user_role" AS ENUM (
  'Admin',
  'User'
);

CREATE TYPE "food_category" AS ENUM (
  'Drinks',
  'Food',
  'Snacks'
);


--------------------------------------------------------------------------------

-- Table Definition for Users
CREATE TABLE "users" (
  "id" BIGSERIAL PRIMARY KEY,
  "created_at" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now()),
  "deleted_at" TIMESTAMP WITH TIME ZONE,
  "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
  "email" VARCHAR(255) UNIQUE,
  "name" VARCHAR(255),
  "otp" VARCHAR(255),
  "otp_generated_time" TIMESTAMP WITH TIME ZONE,
  "password" VARCHAR(255),
  "profile_url" VARCHAR(255),
  "role" user_role
);

--------------------------------------------------------------------------------

-- Table Definition for Bookings
CREATE TABLE "bookings" (
  "id" BIGSERIAL PRIMARY KEY,
  "created_at" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now()),
  "deleted_at" TIMESTAMP WITH TIME ZONE,
  "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
  "booking_id" VARCHAR(255),
  "booking_status" booking_status,
  "payment_status" payment_status,
  "total_price" DECIMAL(38,2),
  "user_id" BIGINT NOT NULL,
  CONSTRAINT "fk_booking_user" FOREIGN KEY ("user_id") REFERENCES "users" ("id")
);

--------------------------------------------------------------------------------

-- Table Definition for Movies
CREATE TABLE "movies" (
  "id" BIGSERIAL PRIMARY KEY,
  "created_at" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now()),
  "deleted_at" TIMESTAMP WITH TIME ZONE,
  "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
  "description" VARCHAR(2048),
  "director" VARCHAR(255),
  "duration" VARCHAR(255),
  "language" movie_language,
  "movie_cast" VARCHAR(1024),
  "movie_poster_url" VARCHAR(255),
  "rating" movie_rating,
  "release_date" DATE,
  "status" movie_status,
  "title" VARCHAR(255),
  "trailer_url" VARCHAR(255)
);

--------------------------------------------------------------------------------

-- Table Definition for Theaters
CREATE TABLE "theaters" (
  "id" BIGSERIAL PRIMARY KEY,
  "created_at" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now()),
  "deleted_at" TIMESTAMP WITH TIME ZONE,
  "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
  "total_basic_price" DECIMAL(38,2),
  "total_basic_rows" INT,
  "capacity" INT NOT NULL,
  "total_economy_price" DECIMAL(38,2),
  "total_economy_rows" INT,
  "location" VARCHAR(255),
  "name" VARCHAR(255),
  "total_premium_price" DECIMAL(38,2),
  "total_premium_rows" INT,
  "total_regular_price" DECIMAL(38,2),
  "total_regular_rows" INT,
  "seat_column" INT,
  "seat_row" INT
);

--------------------------------------------------------------------------------

-- Table Definition for Showtimes
CREATE TABLE "showtimes" (
  "id" BIGSERIAL PRIMARY KEY,
  "created_at" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now()),
  "deleted_at" TIMESTAMP WITH TIME ZONE,
  "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
  "seats_available" INT NOT NULL,
  "showtime_date" DATE NOT NULL,
  "showtime_time" TIME NOT NULL,
  "status" showtime_status,
  "movie_id" BIGINT NOT NULL,
  "theater_id" BIGINT NOT NULL,
  CONSTRAINT "fk_showtime_movie" FOREIGN KEY ("movie_id") REFERENCES "movies" ("id"),
  CONSTRAINT "fk_showtime_theater" FOREIGN KEY ("theater_id") REFERENCES "theaters" ("id")
);

--------------------------------------------------------------------------------

-- Table Definition for Booked Seats
CREATE TABLE "booked_seats" (
  "id" BIGSERIAL PRIMARY KEY,
  "created_at" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now()),
  "deleted_at" TIMESTAMP WITH TIME ZONE,
  "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
  "price" DECIMAL(38,2) NOT NULL,
  "seat_number" VARCHAR(255) NOT NULL,
  "booking_id" BIGINT NOT NULL,
  "showtime_id" BIGINT NOT NULL,
  CONSTRAINT "fk_bookedseat_booking" FOREIGN KEY ("booking_id") REFERENCES "bookings" ("id"),
  CONSTRAINT "fk_bookedseat_showtime" FOREIGN KEY ("showtime_id") REFERENCES "showtimes" ("id")
);

--------------------------------------------------------------------------------

-- Table Definition for Booking Refund Records
CREATE TABLE "booking_refund_records" (
  "id" BIGSERIAL PRIMARY KEY,
  "created_at" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now()),
  "deleted_at" TIMESTAMP WITH TIME ZONE,
  "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
  "status" refund_status,
  "approved_by_id" BIGINT,
  "booking_id" BIGINT NOT NULL UNIQUE,
  CONSTRAINT "fk_refund_booking" FOREIGN KEY ("booking_id") REFERENCES "bookings" ("id"),
  CONSTRAINT "fk_refund_user" FOREIGN KEY ("approved_by_id") REFERENCES "users" ("id")
);

--------------------------------------------------------------------------------
-- Table structure for table `combo`
CREATE TABLE "combo" (
  "id" BIGSERIAL PRIMARY KEY,
  "created_at" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now()),
  "deleted_at" TIMESTAMP WITH TIME ZONE,
  "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
  "combo_name" VARCHAR(255),
  "combo_price" DOUBLE PRECISION NOT NULL,
  "photo_url" VARCHAR(255)
);

--------------------------------------------------------------------------------

-- Table Definition for Food
CREATE TABLE "food" (
  "id" BIGSERIAL PRIMARY KEY,
  "created_at" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now()),
  "deleted_at" TIMESTAMP WITH TIME ZONE,
  "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
  "allergens" VARCHAR(255),
  "category" food_category,
  "description" VARCHAR(255),
  "name" VARCHAR(255),
  "photo_url" VARCHAR(255),
  "price" DOUBLE PRECISION NOT NULL
);

--------------------------------------------------------------------------------

-- Table Definition for Combo Foods
CREATE TABLE "combo_foods" (
  "combo_id" BIGINT NOT NULL,
  "food_id" BIGINT NOT NULL,
  CONSTRAINT "fk_combofood_combo" FOREIGN KEY ("combo_id") REFERENCES "combo" ("id"),
  CONSTRAINT "fk_combofood_food" FOREIGN KEY ("food_id") REFERENCES "food" ("id")
);

--------------------------------------------------------------------------------

-- Table Definition for Configurations
CREATE TABLE "configurations" (
  "id" BIGSERIAL PRIMARY KEY,
  "created_at" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now()),
  "deleted_at" TIMESTAMP WITH TIME ZONE,
  "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
  "code" VARCHAR(255) NOT NULL UNIQUE,
  "value" VARCHAR(255) NOT NULL
);

--------------------------------------------------------------------------------

-- Table Definition for Food Categories
CREATE TABLE "food_categories" (
  "id" BIGSERIAL PRIMARY KEY,
  "created_at" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now()),
  "deleted_at" TIMESTAMP WITH TIME ZONE,
  "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
  "description" VARCHAR(255),
  "name" VARCHAR(255)
);

--------------------------------------------------------------------------------

-- Table Definition for Food Orders
CREATE TABLE "food_orders" (
  "id" BIGSERIAL PRIMARY KEY,
  "created_at" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now()),
  "deleted_at" TIMESTAMP WITH TIME ZONE,
  "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
  "payment_status" payment_status,
  "total_price" DECIMAL(38,2),
  "user_id" BIGINT NOT NULL,
  CONSTRAINT "fk_foodorder_user" FOREIGN KEY ("user_id") REFERENCES "users" ("id")
);

--------------------------------------------------------------------------------

-- Table Definition for Food Order Items
CREATE TABLE "food_order_items" (
  "id" BIGSERIAL PRIMARY KEY,
  "created_at" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now()),
  "deleted_at" TIMESTAMP WITH TIME ZONE,
  "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
  "price" DECIMAL(38,2) NOT NULL,
  "quantity" INT NOT NULL,
  "combo_id" BIGINT,
  "food_id" BIGINT,
  "food_order_id" BIGINT NOT NULL,
  CONSTRAINT "fk_foodorderitem_combo" FOREIGN KEY ("combo_id") REFERENCES "combo" ("id"),
  CONSTRAINT "fk_foodorderitem_food" FOREIGN KEY ("food_id") REFERENCES "food" ("id"),
  CONSTRAINT "fk_foodorderitem_foodorder" FOREIGN KEY ("food_order_id") REFERENCES "food_orders" ("id")
);

--------------------------------------------------------------------------------

-- Table Definition for Movie Genres
CREATE TABLE "movie_genres" (
  "id" BIGSERIAL PRIMARY KEY,
  "created_at" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (now()),
  "deleted_at" TIMESTAMP WITH TIME ZONE,
  "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
  "description" VARCHAR(255),
  "name" VARCHAR(255)
);

--------------------------------------------------------------------------------

-- Table Definition for Movie Has Genres
CREATE TABLE "movie_has_genres" (
  "movie_id" BIGINT NOT NULL,
  "genre_id" BIGINT NOT NULL,
  PRIMARY KEY ("movie_id", "genre_id"),
  CONSTRAINT "fk_moviegenre_movie" FOREIGN KEY ("movie_id") REFERENCES "movies" ("id"),
  CONSTRAINT "fk_moviegenre_genre" FOREIGN KEY ("genre_id") REFERENCES "movie_genres" ("id")
);

--------------------------------------------------------------------------------

-- Add Indexes

CREATE INDEX ON "booked_seats" ("booking_id");
CREATE INDEX ON "booked_seats" ("showtime_id");
CREATE INDEX ON "booking_refund_records" ("approved_by_id");
CREATE INDEX ON "bookings" ("user_id");
CREATE INDEX ON "combo_foods" ("food_id");
CREATE INDEX ON "combo_foods" ("combo_id");
CREATE INDEX ON "food_order_items" ("combo_id");
CREATE INDEX ON "food_order_items" ("food_id");
CREATE INDEX ON "food_order_items" ("food_order_id");
CREATE INDEX ON "food_orders" ("user_id");
CREATE INDEX ON "movie_has_genres" ("genre_id");
CREATE INDEX ON "showtimes" ("movie_id");
CREATE INDEX ON "showtimes" ("theater_id");