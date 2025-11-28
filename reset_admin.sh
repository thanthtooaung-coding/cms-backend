#!/bin/bash

# Reset admin password script
# This script resets the admin user password in the CMS database

echo "Resetting admin password..."

# Try to connect as postgres user first (default superuser)
if docker exec -i postgres-cms psql -U postgres -d cms_db < schema/reset_admin_password.sql 2>/dev/null; then
    echo "✓ Password reset successfully using postgres user"
    exit 0
fi

# If that fails, try to get the user from environment or use default
DB_USER=${POSTGRES_CMS_USER:-postgres}
DB_NAME=${POSTGRES_CMS_DB:-cms_db}

echo "Trying with user: $DB_USER, database: $DB_NAME"

if docker exec -i postgres-cms psql -U "$DB_USER" -d "$DB_NAME" < schema/reset_admin_password.sql 2>/dev/null; then
    echo "✓ Password reset successfully"
    exit 0
else
    echo "✗ Failed to reset password. Please check:"
    echo "  1. Docker is running"
    echo "  2. postgres-cms container is running"
    echo "  3. Database credentials are correct"
    echo ""
    echo "You can also run manually:"
    echo "  docker exec -i postgres-cms psql -U postgres -d cms_db < schema/reset_admin_password.sql"
    exit 1
fi


