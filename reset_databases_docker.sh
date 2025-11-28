#!/bin/bash

# Script to reset CMS and LMS databases using Docker containers
# Usage: ./reset_databases_docker.sh [admin_username] [admin_password]

set -e

# Default admin credentials
ADMIN_USERNAME=${1:-"admin"}
ADMIN_PASSWORD=${2:-"admin123"}

echo "=========================================="
echo "Resetting CMS and LMS Databases"
echo "=========================================="
echo "Admin Username: $ADMIN_USERNAME"
echo "Admin Password: $ADMIN_PASSWORD"
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "Error: Docker is not running. Please start Docker first."
    exit 1
fi

# Check if containers exist
if ! docker ps -a --format '{{.Names}}' | grep -q "^postgres-cms$"; then
    echo "Error: postgres-cms container not found. Please start docker-compose first."
    exit 1
fi

if ! docker ps -a --format '{{.Names}}' | grep -q "^postgres-lms$"; then
    echo "Error: postgres-lms container not found. Please start docker-compose first."
    exit 1
fi

# Generate bcrypt hash using a simple Go program
echo "Generating password hash..."
PASSWORD_HASH=$(docker run --rm golang:1.21-alpine sh -c "
cat > /tmp/hash.go << 'EOG'
package main
import (
    \"fmt\"
    \"golang.org/x/crypto/bcrypt\"
)
func main() {
    hash, err := bcrypt.GenerateFromPassword([]byte(\"$ADMIN_PASSWORD\"), bcrypt.DefaultCost)
    if err != nil {
        panic(err)
    }
    fmt.Print(string(hash))
}
EOG
go mod init hash 2>/dev/null || true
go get golang.org/x/crypto/bcrypt 2>/dev/null || true
go run /tmp/hash.go
")

if [ -z "$PASSWORD_HASH" ]; then
    echo "Warning: Could not generate password hash. Using default hash."
    # Default hash for 'admin123'
    PASSWORD_HASH='$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92s.ag/iHjM6lYSkGf70O'
fi

echo "Password hash generated: ${PASSWORD_HASH:0:20}..."

echo ""
echo "=========================================="
echo "Resetting CMS Database"
echo "=========================================="

# Drop all tables and types in CMS database
echo "Dropping all tables and types in CMS database..."
docker exec -i postgres-cms psql -U postgres -d cms_db <<EOF
DO \$\$ 
DECLARE 
    r RECORD;
BEGIN
    -- Drop all tables
    FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = 'public') 
    LOOP
        EXECUTE 'DROP TABLE IF EXISTS ' || quote_ident(r.tablename) || ' CASCADE';
    END LOOP;
    
    -- Drop all types
    FOR r IN (SELECT typname FROM pg_type WHERE typnamespace = (SELECT oid FROM pg_namespace WHERE nspname = 'public') AND typtype = 'e')
    LOOP
        EXECUTE 'DROP TYPE IF EXISTS ' || quote_ident(r.typname) || ' CASCADE';
    END LOOP;
END \$\$;
EOF

# Run CMS SQL file
echo "Running CMS SQL schema..."
docker exec -i postgres-cms psql -U postgres -d cms_db < ./schema/cms.sql

# Update admin password
echo "Setting admin credentials..."
docker exec -i postgres-cms psql -U postgres -d cms_db <<EOF
-- Update or insert admin user
INSERT INTO "User" ("username", "password", "email", "name", "role_id")
SELECT 
  '$ADMIN_USERNAME',
  '$PASSWORD_HASH',
  'admin@example.com',
  'Admin User',
  (SELECT "id" FROM "Role" WHERE "name" = 'Admin')
WHERE NOT EXISTS (
  SELECT 1 FROM "User" WHERE "username" = '$ADMIN_USERNAME'
);

UPDATE "User" 
SET "password" = '$PASSWORD_HASH',
    "email" = 'admin@example.com',
    "name" = 'Admin User'
WHERE "username" = '$ADMIN_USERNAME';

-- Verify admin user
SELECT "username", "email", "name", 
       (SELECT "name" FROM "Role" WHERE "id" = "User"."role_id") as role
FROM "User" 
WHERE "username" = '$ADMIN_USERNAME';
EOF

echo ""
echo "=========================================="
echo "Resetting LMS Database"
echo "=========================================="

# Drop all tables and types in LMS database
echo "Dropping all tables and types in LMS database..."
docker exec -i postgres-lms psql -U lms_user -d lms_db <<EOF
DO \$\$ 
DECLARE 
    r RECORD;
BEGIN
    -- Drop all tables
    FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = 'public') 
    LOOP
        EXECUTE 'DROP TABLE IF EXISTS ' || quote_ident(r.tablename) || ' CASCADE';
    END LOOP;
    
    -- Drop all types
    FOR r IN (SELECT typname FROM pg_type WHERE typnamespace = (SELECT oid FROM pg_namespace WHERE nspname = 'public') AND typtype = 'e')
    LOOP
        EXECUTE 'DROP TYPE IF EXISTS ' || quote_ident(r.typname) || ' CASCADE';
    END LOOP;
END \$\$;
EOF

# Run LMS SQL file
echo "Running LMS SQL schema..."
docker exec -i postgres-lms psql -U lms_user -d lms_db < ./schema/lms.sql

echo ""
echo "=========================================="
echo "Database Reset Complete!"
echo "=========================================="
echo "CMS Admin Credentials:"
echo "  Username: $ADMIN_USERNAME"
echo "  Password: $ADMIN_PASSWORD"
echo ""
echo "You can now start the application."

