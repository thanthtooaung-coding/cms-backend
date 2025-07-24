DROP TYPE IF EXISTS course_status CASCADE;
CREATE TYPE course_status AS ENUM ('Pending', 'Published', 'Unpublished', 'Archived');

DROP TYPE IF EXISTS enrollment_status CASCADE;
CREATE TYPE enrollment_status AS ENUM ('Pending', 'Enrolled', 'Completed', 'Dropped');

DROP TYPE IF EXISTS material_type CASCADE;
CREATE TYPE material_type AS ENUM ('Video', 'PDF', 'Slide', 'Link');

DROP TYPE IF EXISTS lms_role_name CASCADE;
CREATE TYPE lms_role_name AS ENUM ('Owner', 'Admin', 'Staff');