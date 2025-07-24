-- ENUM types for various status and type fields.

CREATE TYPE "lms_role_name" AS ENUM (
  'Owner',
  'Admin',
  'Staff'
);

CREATE TYPE "course_status" AS ENUM (
  'Pending',
  'Published',
  'Unpublished',
  'Archived'
);

CREATE TYPE "material_type" AS ENUM (
  'Video',
  'PDF',
  'Slide',
  'Link'
);

CREATE TYPE "enrollment_status" AS ENUM (
  'Pending',
  'Enrolled',
  'Completed',
  'Dropped'
);

--------------------------------------------------------------------------------

-- Table Definition for Tenants
-- Represents different tenants in a multi-tenant architecture.

CREATE TABLE "Tenants" (
                           "id" BIGSERIAL PRIMARY KEY,
                           "name" VARCHAR(255),
                           "is_active" BOOLEAN DEFAULT true,
                           "created_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                           "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                           "deleted_at" TIMESTAMP WITH TIME ZONE
);

--------------------------------------------------------------------------------

-- Table Definition for Roles
-- Stores user roles within the LMS.

CREATE TABLE "Role" (
                        "id" BIGSERIAL PRIMARY KEY,
                        "name" lms_role_name NOT NULL UNIQUE,
                        "created_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                        "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                        "deleted_at" TIMESTAMP WITH TIME ZONE
);

--------------------------------------------------------------------------------

-- Table Definition for Users
-- Stores user account information for students, instructors, and admins.

CREATE TABLE "User" (
                        "id" BIGSERIAL PRIMARY KEY,
                        "username" VARCHAR(255) UNIQUE NOT NULL,
                        "password" VARCHAR(255) NOT NULL,
                        "email" VARCHAR(255) UNIQUE NOT NULL,
                        "name" VARCHAR(255),
                        "role_id" BIGINT,
                        "registration_date" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                        "address" TEXT,
                        "phone_number" VARCHAR(50),
                        "tenant_id" BIGINT,
                        "created_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                        "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                        "deleted_at" TIMESTAMP WITH TIME ZONE,
                        CONSTRAINT "fk_user_role" FOREIGN KEY ("role_id") REFERENCES "Role" ("id") ON DELETE SET NULL,
                        CONSTRAINT "fk_user_tenant" FOREIGN KEY ("tenant_id") REFERENCES "Tenants" ("id") ON DELETE SET NULL
);

--------------------------------------------------------------------------------

-- Table Definition for Course Categories
-- Organizes courses into different categories.

CREATE TABLE "Course_Category" (
                                   "id" BIGSERIAL PRIMARY KEY,
                                   "name" VARCHAR(255),
                                   "description" TEXT,
                                   "tenant_id" BIGINT,
                                   "created_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                                   "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                                   "deleted_at" TIMESTAMP WITH TIME ZONE,
                                   CONSTRAINT "fk_coursecategory_tenant" FOREIGN KEY ("tenant_id") REFERENCES "Tenants" ("id") ON DELETE SET NULL
);

--------------------------------------------------------------------------------

-- Table Definition for Courses
-- Stores the main course information.

CREATE TABLE "Course" (
                          "id" BIGSERIAL PRIMARY KEY,
                          "title" VARCHAR(255),
                          "description" TEXT,
                          "category_id" BIGINT,
                          "instructor_id" BIGINT,
                          "overall_rating" INT,
                          "status" course_status DEFAULT 'Pending',
                          "duration_day_count" INT,
                          "created_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                          "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                          "deleted_at" TIMESTAMP WITH TIME ZONE,
                          CONSTRAINT "fk_course_category" FOREIGN KEY ("category_id") REFERENCES "Course_Category" ("id") ON DELETE SET NULL,
                          CONSTRAINT "fk_course_instructor" FOREIGN KEY ("instructor_id") REFERENCES "User" ("id") ON DELETE SET NULL
);

--------------------------------------------------------------------------------

-- Table Definition for Ratings
-- Stores user ratings for each course.

CREATE TABLE "Rating" (
                          "id" BIGSERIAL PRIMARY KEY,
                          "user_id" BIGINT,
                          "course_id" BIGINT,
                          "rating_count" INT,
                          "created_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                          "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                          "deleted_at" TIMESTAMP WITH TIME ZONE,
                          CONSTRAINT "fk_rating_user" FOREIGN KEY ("user_id") REFERENCES "User" ("id") ON DELETE CASCADE,
                          CONSTRAINT "fk_rating_course" FOREIGN KEY ("course_id") REFERENCES "Course" ("id") ON DELETE CASCADE
);

--------------------------------------------------------------------------------

-- Table Definition for Modules
-- Breaks down courses into smaller modules.

CREATE TABLE "Module" (
                          "id" BIGSERIAL PRIMARY KEY,
                          "name" VARCHAR(255),
                          "course_id" BIGINT,
                          "description" TEXT,
                          "created_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                          "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                          "deleted_at" TIMESTAMP WITH TIME ZONE,
                          CONSTRAINT "fk_module_course" FOREIGN KEY ("course_id") REFERENCES "Course" ("id") ON DELETE CASCADE
);

--------------------------------------------------------------------------------

-- Table Definition for Quizzes
-- Stores quiz questions and answers for each module.

CREATE TABLE "Quiz" (
                        "id" BIGSERIAL PRIMARY KEY,
                        "question" VARCHAR(255),
                        "answer" VARCHAR(255),
                        "module_id" BIGINT,
                        "created_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                        "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                        "deleted_at" TIMESTAMP WITH TIME ZONE,
                        CONSTRAINT "fk_quiz_module" FOREIGN KEY ("module_id") REFERENCES "Module" ("id") ON DELETE CASCADE
);

--------------------------------------------------------------------------------

-- Table Definition for Student Quizzes
-- Tracks student attempts and scores on quizzes.

CREATE TABLE "Student_Quiz" (
                                "id" BIGSERIAL PRIMARY KEY,
                                "student_id" BIGINT,
                                "quiz_id" BIGINT,
                                "score" INT,
                                "attempt" INT,
                                "created_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                                "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                                "deleted_at" TIMESTAMP WITH TIME ZONE,
                                CONSTRAINT "fk_student_quiz_student" FOREIGN KEY ("student_id") REFERENCES "User" ("id") ON DELETE CASCADE,
                                CONSTRAINT "fk_student_quiz_quiz" FOREIGN KEY ("quiz_id") REFERENCES "Quiz" ("id") ON DELETE CASCADE
);

--------------------------------------------------------------------------------

-- Table Definition for Lessons
-- Contains the actual learning content for each module.

CREATE TABLE "Lesson" (
                          "id" BIGSERIAL PRIMARY KEY,
                          "title" VARCHAR(255),
                          "content" TEXT,
                          "material_type" material_type,
                          "module_id" BIGINT,
                          "created_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                          "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                          "deleted_at" TIMESTAMP WITH TIME ZONE,
                          CONSTRAINT "fk_lesson_module" FOREIGN KEY ("module_id") REFERENCES "Module" ("id") ON DELETE CASCADE
);

--------------------------------------------------------------------------------

-- Table Definition for Enrollments
-- Tracks student enrollment in courses.

CREATE TABLE "Enrollment" (
                              "id" BIGSERIAL PRIMARY KEY,
                              "student_id" BIGINT,
                              "course_id" BIGINT,
                              "enrollment_date" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                              "progress" DECIMAL(5, 2),
                              "status" enrollment_status DEFAULT 'Pending',
                              "due_date" TIMESTAMP WITH TIME ZONE,
                              "created_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                              "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                              "deleted_at" TIMESTAMP WITH TIME ZONE,
                              CONSTRAINT "fk_enrollment_student" FOREIGN KEY ("student_id") REFERENCES "User" ("id") ON DELETE CASCADE,
                              CONSTRAINT "fk_enrollment_course" FOREIGN KEY ("course_id") REFERENCES "Course" ("id") ON DELETE CASCADE
);

--------------------------------------------------------------------------------

-- Table Definition for Certificates
-- Stores certificates issued to students upon course completion.

CREATE TABLE "Certificate" (
                               "id" BIGSERIAL PRIMARY KEY,
                               "enrollment_id" BIGINT UNIQUE,
                               "issue_date" TIMESTAMP WITH TIME ZONE,
                               "certificate_url" VARCHAR(2048),
                               "created_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                               "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                               "deleted_at" TIMESTAMP WITH TIME ZONE,
                               CONSTRAINT "fk_certificate_enrollment" FOREIGN KEY ("enrollment_id") REFERENCES "Enrollment" ("id") ON DELETE CASCADE
);

--------------------------------------------------------------------------------

-- Table Definition for Assignments
-- Stores assignment details for courses.

CREATE TABLE "Assignment" (
                              "id" BIGSERIAL PRIMARY KEY,
                              "course_id" BIGINT,
                              "title" VARCHAR(255),
                              "instructions" TEXT,
                              "created_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                              "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                              "deleted_at" TIMESTAMP WITH TIME ZONE,
                              CONSTRAINT "fk_assignment_course" FOREIGN KEY ("course_id") REFERENCES "Course" ("id") ON DELETE CASCADE
);

--------------------------------------------------------------------------------

-- Table Definition for Submissions
-- Tracks student submissions for assignments.

CREATE TABLE "Submission" (
                              "id" BIGSERIAL PRIMARY KEY,
                              "assignment_id" BIGINT,
                              "student_id" BIGINT,
                              "submitted_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                              "file_url" VARCHAR(2048),
                              "created_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                              "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                              "deleted_at" TIMESTAMP WITH TIME ZONE,
                              CONSTRAINT "fk_submission_assignment" FOREIGN KEY ("assignment_id") REFERENCES "Assignment" ("id") ON DELETE CASCADE,
                              CONSTRAINT "fk_submission_student" FOREIGN KEY ("student_id") REFERENCES "User" ("id") ON DELETE CASCADE
);

--------------------------------------------------------------------------------

-- Table Definition for Reviews
-- Stores user reviews for courses.

CREATE TABLE "Review" (
                          "id" BIGSERIAL PRIMARY KEY,
                          "course_id" BIGINT,
                          "user_id" BIGINT,
                          "title" VARCHAR(255),
                          "description" TEXT,
                          "created_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                          "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                          "deleted_at" TIMESTAMP WITH TIME ZONE,
                          CONSTRAINT "fk_review_course" FOREIGN KEY ("course_id") REFERENCES "Course" ("id") ON DELETE CASCADE,
                          CONSTRAINT "fk_review_user" FOREIGN KEY ("user_id") REFERENCES "User" ("id") ON DELETE CASCADE
);

--------------------------------------------------------------------------------

-- Table Definition for Reports
-- Stores generated reports about LMS data.

CREATE TABLE "Report" (
                          "report_id" BIGSERIAL PRIMARY KEY,
                          "report_name" VARCHAR(255),
                          "generated_by_user_id" BIGINT,
                          "generated_date" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                          "data_snapshot" TEXT,
                          "created_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                          "updated_at" TIMESTAMP WITH TIME ZONE DEFAULT (now()),
                          "deleted_at" TIMESTAMP WITH TIME ZONE,
                          CONSTRAINT "fk_report_user" FOREIGN KEY ("generated_by_user_id") REFERENCES "User" ("id") ON DELETE SET NULL
);

--------------------------------------------------------------------------------

-- Add Indexes

CREATE INDEX ON "User" ("role_id");
CREATE INDEX ON "User" ("tenant_id");
CREATE INDEX ON "Course_Category" ("tenant_id");
CREATE INDEX ON "Course" ("category_id");
CREATE INDEX ON "Course" ("instructor_id");
CREATE INDEX ON "Rating" ("user_id");
CREATE INDEX ON "Rating" ("course_id");
CREATE INDEX ON "Module" ("course_id");
CREATE INDEX ON "Quiz" ("module_id");
CREATE INDEX ON "Student_Quiz" ("student_id");
CREATE INDEX ON "Student_Quiz" ("quiz_id");
CREATE INDEX ON "Lesson" ("module_id");
CREATE INDEX ON "Enrollment" ("student_id");
CREATE INDEX ON "Enrollment" ("course_id");
CREATE INDEX ON "Assignment" ("course_id");
CREATE INDEX ON "Submission" ("assignment_id");
CREATE INDEX ON "Submission" ("student_id");
CREATE INDEX ON "Review" ("course_id");
CREATE INDEX ON "Review" ("user_id");
CREATE INDEX ON "Report" ("generated_by_user_id");
