#!/bin/bash

# Script to run Certificate table migration on LMS database
# Make sure docker containers are running first: docker-compose up -d

# Get database credentials from environment or use defaults
DB_USER=${POSTGRES_LMS_USER:-lms_user}
DB_NAME=${POSTGRES_LMS_DB:-lms_db}
DB_PASSWORD=${POSTGRES_LMS_PASSWORD:-lms_password}

echo "Running Certificate table migration on LMS database..."
echo "Database: $DB_NAME"
echo "User: $DB_USER"
echo ""

# Run the migration SQL
docker exec -i postgres-lms psql -U "$DB_USER" -d "$DB_NAME" < schema/migrate_certificate_table.sql

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Migration completed successfully!"
else
    echo ""
    echo "❌ Migration failed. Please check the error messages above."
    exit 1
fi

