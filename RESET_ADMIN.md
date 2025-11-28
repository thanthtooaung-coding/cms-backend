# Reset Admin Password

If you're getting "invalid username or password" error, follow these steps:

## Quick Fix

Run the reset script:

```bash
./reset_admin.sh
```

Or manually run:

```bash
# Using postgres superuser (most common)
docker exec -i postgres-cms psql -U postgres -d cms_db < schema/reset_admin_password.sql

# Or if you have a custom user set in .env file
docker exec -i postgres-cms psql -U $POSTGRES_CMS_USER -d $POSTGRES_CMS_DB < schema/reset_admin_password.sql
```

## Default Credentials

After running the script, use these credentials to login:

- **Username**: `admin`
- **Password**: `password123`

## Alternative: Using Go Script

You can also use the Go reset script:

```bash
cd app/cms-sys
go run cmd/reset_admin_password.go
```

Make sure to set the environment variables:
- `CMS_DB_HOST=postgres-cms` (or `localhost` if running outside Docker)
- `CMS_DB_PORT=5432`
- `CMS_DB_USER=postgres` (or your custom user)
- `CMS_DB_PASSWORD=your_password`
- `CMS_DB_NAME=cms_db`

## Troubleshooting

If the script fails:

1. **Check Docker is running**:
   ```bash
   docker ps
   ```

2. **Check the container is running**:
   ```bash
   docker ps | grep postgres-cms
   ```

3. **Check database exists**:
   ```bash
   docker exec postgres-cms psql -U postgres -l
   ```

4. **Check what users exist**:
   ```bash
   docker exec postgres-cms psql -U postgres -d cms_db -c "\du"
   ```

5. **Reinitialize database** (WARNING: This will delete all data):
   ```bash
   docker-compose down
   docker volume rm cms-backend_postgres-cms
   docker-compose up -d postgres-cms
   ```


