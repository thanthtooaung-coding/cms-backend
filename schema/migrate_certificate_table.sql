-- Migration script to update Certificate table structure
-- Run this if the Certificate table already exists with old schema

-- Drop the old Certificate table if it exists with wrong structure
DROP TABLE IF EXISTS "Certificate" CASCADE;

-- Create the Certificate table with correct structure
CREATE TABLE "Certificate" (
    "id" BIGSERIAL PRIMARY KEY,
    "student_id" BIGINT NOT NULL,
    "course_id" BIGINT NOT NULL,
    "certificate_number" VARCHAR(255) UNIQUE NOT NULL,
    "issued_date" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
    "score_percentage" DECIMAL(5, 2) NOT NULL,
    "created_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
    "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
    "deleted_at" TIMESTAMP WITH TIME ZONE,
    CONSTRAINT "fk_certificate_student" FOREIGN KEY ("student_id") REFERENCES "User" ("id") ON DELETE CASCADE,
    CONSTRAINT "fk_certificate_course" FOREIGN KEY ("course_id") REFERENCES "Course" ("id") ON DELETE CASCADE
);

-- Alternative: If you want to preserve existing data, use ALTER TABLE instead:
-- ALTER TABLE "Certificate" DROP COLUMN IF EXISTS "enrollment_id";
-- ALTER TABLE "Certificate" DROP COLUMN IF EXISTS "issue_date";
-- ALTER TABLE "Certificate" DROP COLUMN IF EXISTS "certificate_url";
-- ALTER TABLE "Certificate" ADD COLUMN IF NOT EXISTS "student_id" BIGINT;
-- ALTER TABLE "Certificate" ADD COLUMN IF NOT EXISTS "course_id" BIGINT;
-- ALTER TABLE "Certificate" ADD COLUMN IF NOT EXISTS "certificate_number" VARCHAR(255);
-- ALTER TABLE "Certificate" ADD COLUMN IF NOT EXISTS "issued_date" TIMESTAMP WITH TIME ZONE DEFAULT (now());
-- ALTER TABLE "Certificate" ADD COLUMN IF NOT EXISTS "score_percentage" DECIMAL(5, 2);
-- ALTER TABLE "Certificate" ADD CONSTRAINT "fk_certificate_student" FOREIGN KEY ("student_id") REFERENCES "User" ("id") ON DELETE CASCADE;
-- ALTER TABLE "Certificate" ADD CONSTRAINT "fk_certificate_course" FOREIGN KEY ("course_id") REFERENCES "Course" ("id") ON DELETE CASCADE;
-- CREATE UNIQUE INDEX IF NOT EXISTS "idx_certificate_number" ON "Certificate" ("certificate_number");

