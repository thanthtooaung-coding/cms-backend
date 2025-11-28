-- Reset admin password to 'password123'
-- This script updates the admin user's password hash

-- First, ensure the admin user exists
INSERT INTO "User" ("username", "password", "email", "name", "role_id")
SELECT 
  'admin',
  '$2a$12$ivx6bULb3fcJGJ5vJai1K.PObcspVX.4tKbz0cwRtPoRhsnnurFtC',
  'admin@example.com',
  'Admin User',
  (SELECT "id" FROM "Role" WHERE "name" = 'Admin')
WHERE NOT EXISTS (
  SELECT 1 FROM "User" WHERE "username" = 'admin'
);

-- Update the password hash if the user already exists
UPDATE "User" 
SET "password" = '$2a$12$ivx6bULb3fcJGJ5vJai1K.PObcspVX.4tKbz0cwRtPoRhsnnurFtC'
WHERE "username" = 'admin';

-- Verify the update
SELECT "username", "email", "name", "role_id" 
FROM "User" 
WHERE "username" = 'admin';


