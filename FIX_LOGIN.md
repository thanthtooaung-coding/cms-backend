# Fix Login Issue - Step by Step

## Problem
Getting "invalid username or password" when trying to login with:
- Username: `admin`
- Password: `password123`

## Solution

### Step 1: Make sure Docker is running
```bash
docker ps
```

If Docker isn't running, start it first.

### Step 2: Check if containers are running
```bash
docker ps | grep -E "postgres-cms|cms-main"
```

You should see both `postgres-cms` and `cms-main-system` containers running.

### Step 3: Run the password reset script

**Option A: Using the SQL script directly (Recommended)**
```bash
cd /home/alvin/cms-dev/cms-backend
sudo docker exec -i postgres-cms psql -U postgres -d cms_db < schema/reset_admin_password.sql
```

**Option B: Using the shell script**
```bash
cd /home/alvin/cms-dev/cms-backend
./reset_admin.sh
```

### Step 4: Verify the admin user exists
```bash
sudo docker exec postgres-cms psql -U postgres -d cms_db -c "SELECT username, email, name FROM \"User\" WHERE username = 'admin';"
```

You should see:
```
 username |       email        |    name     
----------+--------------------+-------------
 admin    | admin@example.com  | Admin User
```

### Step 5: Try logging in again
- Username: `admin`
- Password: `password123`

## If it still doesn't work

### Check if the database was initialized
```bash
sudo docker exec postgres-cms psql -U postgres -d cms_db -c "SELECT COUNT(*) FROM \"User\";"
```

If this returns 0 or an error, the database wasn't initialized. Reinitialize it:

```bash
# Stop containers
docker compose down

# Remove the database volume (WARNING: This deletes all data!)
docker volume rm cms-backend_postgres-cms

# Start again (this will run the init script)
docker compose up -d postgres-cms

# Wait for database to initialize (about 10-20 seconds)
sleep 15

# Run the reset script again
sudo docker exec -i postgres-cms psql -U postgres -d cms_db < schema/reset_admin_password.sql
```

## Troubleshooting

### Check CMS service logs
```bash
docker compose logs cms-main-system | tail -50
```

Look for database connection errors or authentication errors.

### Check if the gateway is routing correctly
```bash
docker compose logs gateway | tail -50
```

### Test the login endpoint directly
```bash
curl -X POST http://localhost:4001/api/cms/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'
```

You should get a response with a token if it works.


