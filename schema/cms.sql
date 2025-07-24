--  ENUM types for status and role fields

CREATE TYPE "role_name" AS ENUM (
  'Owner',
  'Admin',
  'Staff'
);

CREATE TYPE "page_status" AS ENUM (
  'Draft',
  'Published',
  'Archived'
);

CREATE TYPE "request_status" AS ENUM (
  'Pending',
  'Approved',
  'Rejected'
);

--------------------------------------------------------------------------------

-- Table Definition for Roles
-- Stores user roles like Owner, Admin, or Staff.

CREATE TABLE "Role" (
                        "id" BIGSERIAL PRIMARY KEY,
                        "name" role_name NOT NULL UNIQUE,
                        "created_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                        "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now())
);

--------------------------------------------------------------------------------

-- Auto-insert default roles

INSERT INTO "Role" ("name") VALUES ('Owner'), ('Admin'), ('Staff');

-- Table Definition for Users
-- Stores user account information.

CREATE TABLE "User" (
                        "id" BIGSERIAL PRIMARY KEY,
                        "username" VARCHAR(255) UNIQUE NOT NULL,
                        "password" VARCHAR(255) NOT NULL,
                        "email" VARCHAR(255) UNIQUE NOT NULL,
                        "name" VARCHAR(255),
                        "role_id" BIGINT,
                        "address" TEXT,
                        "phone_number" VARCHAR(50),
                        "created_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                        "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                        CONSTRAINT "fk_user_role" FOREIGN KEY ("role_id") REFERENCES "Role" ("id") ON DELETE SET NULL
);

--------------------------------------------------------------------------------

-- Table Definition for Pages
-- Stores the main content pages of the CMS.

CREATE TABLE "Page" (
                        "id" BIGSERIAL PRIMARY KEY,
                        "title" VARCHAR(255),
                        "image_url" VARCHAR(2048),
                        "status" page_status DEFAULT 'Draft',
                        "owner_id" BIGINT NOT NULL,
                        "published_by_staff_id" BIGINT,
                        "created_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                        "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                        CONSTRAINT "fk_page_owner" FOREIGN KEY ("owner_id") REFERENCES "User" ("id") ON DELETE CASCADE,
                        CONSTRAINT "fk_page_staff" FOREIGN KEY ("published_by_staff_id") REFERENCES "User" ("id") ON DELETE SET NULL
);

--------------------------------------------------------------------------------

-- Table Definition for Page Requests
-- Manages user requests for actions like page creation.

CREATE TABLE "Page_Request" (
                                "id" BIGSERIAL PRIMARY KEY,
                                "owner_id" BIGINT NOT NULL,
                                "request_type" VARCHAR(255) NOT NULL,
                                "title" VARCHAR(255) NOT NULL,
                                "page_url" VARCHAR(255) NOT NULL,
                                "logo_url" VARCHAR(255) NOT NULL,
                                "status" request_status DEFAULT 'Pending',
                                "admin_id" BIGINT,
                                "created_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                                "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                                CONSTRAINT "fk_request_owner" FOREIGN KEY ("owner_id") REFERENCES "User" ("id") ON DELETE CASCADE,
                                CONSTRAINT "fk_request_admin" FOREIGN KEY ("admin_id") REFERENCES "User" ("id") ON DELETE SET NULL
);

--------------------------------------------------------------------------------

-- Table Definition for Reports
-- Stores generated reports.

CREATE TABLE "Report" (
                          "id" BIGSERIAL PRIMARY KEY,
                          "report_name" VARCHAR(255),
                          "generated_date" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                          "generated_by_user_id" BIGINT NOT NULL,
                          "created_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                          "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                          CONSTRAINT "fk_report_user" FOREIGN KEY ("generated_by_user_id") REFERENCES "User" ("id") ON DELETE CASCADE
);

--------------------------------------------------------------------------------

-- Add Indexes

CREATE INDEX ON "User" ("role_id");
CREATE INDEX ON "Page" ("owner_id");
CREATE INDEX ON "Page" ("published_by_staff_id");
CREATE INDEX ON "Page_Request" ("owner_id");
CREATE INDEX ON "Page_Request" ("admin_id");
CREATE INDEX ON "Report" ("generated_by_user_id");