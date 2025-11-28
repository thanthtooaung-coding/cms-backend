#!/bin/bash

# Script to reset CMS and LMS databases and set up admin credentials
# Usage: ./reset_databases.sh [admin_username] [admin_password]

set -e

# Default admin credentials
ADMIN_USERNAME=${1:-"admin"}
ADMIN_PASSWORD=${2:-"admin123"}

# Database connection details (from docker-compose)
CMS_DB_HOST=${POSTGRES_CMS_HOST:-"localhost"}
CMS_DB_PORT=${POSTGRES_CMS_PORT:-"5401"}
CMS_DB_NAME=${POSTGRES_CMS_DB:-"cms_db"}
CMS_DB_USER=${POSTGRES_CMS_USER:-"postgres"}
CMS_DB_PASSWORD=${POSTGRES_CMS_PASSWORD:-"cms_password_123"}

LMS_DB_HOST=${POSTGRES_LMS_HOST:-"localhost"}
LMS_DB_PORT=${POSTGRES_LMS_PORT:-"5402"}
LMS_DB_NAME=${POSTGRES_LMS_DB:-"lms_db"}
LMS_DB_USER=${POSTGRES_LMS_USER:-"lms_user"}
LMS_DB_PASSWORD=${POSTGRES_LMS_PASSWORD:-"lms_password"}

echo "=========================================="
echo "Resetting CMS and LMS Databases"
echo "=========================================="
echo "Admin Username: $ADMIN_USERNAME"
echo "Admin Password: $ADMIN_PASSWORD"
echo ""

# Function to generate bcrypt hash (requires Go or Python)
generate_bcrypt_hash() {
    local password=$1
    # Try using Go if available
    if command -v go &> /dev/null; then
        go run - <<EOF
package main
import (
    "fmt"
    "golang.org/x/crypto/bcrypt"
)
func main() {
    hash, _ := bcrypt.GenerateFromPassword([]byte("$password"), bcrypt.DefaultCost)
    fmt.Print(string(hash))
}
EOF
    # Fallback to Python
    elif command -v python3 &> /dev/null; then
        python3 -c "import bcrypt; print(bcrypt.hashpw('$password'.encode('utf-8'), bcrypt.gensalt()).decode('utf-8'))"
    else
        echo "Error: Need Go or Python with bcrypt to generate password hash"
        exit 1
    fi
}

# Generate password hash
echo "Generating password hash..."
PASSWORD_HASH=$(generate_bcrypt_hash "$ADMIN_PASSWORD")
if [ -z "$PASSWORD_HASH" ]; then
    echo "Error: Failed to generate password hash"
    exit 1
fi
echo "Password hash generated: ${PASSWORD_HASH:0:20}..."

# Export password for psql
export PGPASSWORD="$CMS_DB_PASSWORD"

echo ""
echo "=========================================="
echo "Resetting CMS Database"
echo "=========================================="

# Drop all tables in CMS database
echo "Dropping all tables in CMS database..."
psql -h "$CMS_DB_HOST" -p "$CMS_DB_PORT" -U "$CMS_DB_USER" -d "$CMS_DB_NAME" <<EOF
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
psql -h "$CMS_DB_HOST" -p "$CMS_DB_PORT" -U "$CMS_DB_USER" -d "$CMS_DB_NAME" -f ./schema/cms.sql

# Update admin password
echo "Setting admin credentials..."
psql -h "$CMS_DB_HOST" -p "$CMS_DB_PORT" -U "$CMS_DB_USER" -d "$CMS_DB_NAME" <<EOF
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

# Export password for LMS database
export PGPASSWORD="$LMS_DB_PASSWORD"

echo ""
echo "=========================================="
echo "Resetting LMS Database"
echo "=========================================="

# Drop all tables in LMS database
echo "Dropping all tables in LMS database..."
psql -h "$LMS_DB_HOST" -p "$LMS_DB_PORT" -U "$LMS_DB_USER" -d "$LMS_DB_NAME" <<EOF
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
psql -h "$LMS_DB_HOST" -p "$LMS_DB_PORT" -U "$LMS_DB_USER" -d "$LMS_DB_NAME" -f ./schema/lms.sql

echo ""
echo "=========================================="
echo "Database Reset Complete!"
echo "=========================================="
echo "CMS Admin Credentials:"
echo "  Username: $ADMIN_USERNAME"
echo "  Password: $ADMIN_PASSWORD"
echo ""
echo "You can now start the application."

