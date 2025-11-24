# 🚀 CMS Backend Setup Guide

This guide will help you set up and run the CMS Backend project locally.

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Docker** (version 20.10 or higher)
- **Docker Compose** (version 2.0 or higher)
- **Git** (for cloning the repository)

### Verify Installation

```bash
docker --version
docker-compose --version
git --version
```

## 🔧 Setup Steps

### Step 1: Clone the Repository (if not already done)

```bash
git clone https://github.com/thanthtooaung-coding/cms-backend.git
cd cms-backend
```

### Step 2: Create Environment File

Create a `.env` file in the root directory by copying the example:

```bash
cp .env.example .env
```

### Step 3: Configure Environment Variables

Edit the `.env` file and update the following values according to your needs:

#### Required Configuration:

1. **Database Credentials**: Update PostgreSQL usernames and passwords
2. **Cloudinary** (for file service): 
   - Get your credentials from [Cloudinary Dashboard](https://cloudinary.com/console)
   - Update `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`

3. **Email Service** (for sending emails):
   - Update `SPRING_MAIL_USERNAME` with your Gmail address
   - Update `SPRING_MAIL_PASSWORD` with your Gmail App Password
   - [How to create Gmail App Password](https://support.google.com/accounts/answer/185833)

4. **AI Service** (optional):
   - Update `GOOGLE_API_KEY` with your Google API key if you plan to use AI features

### Step 4: Build and Start Services

Start all services using Docker Compose:

```bash
docker-compose up -d
```

This command will:
- Build Docker images for all services
- Start all containers in detached mode
- Set up the network and volumes

### Step 5: Verify Services are Running

Check the status of all containers:

```bash
docker-compose ps
```

You should see all services in "Up" status.

### Step 6: View Logs (Optional)

To view logs from all services:

```bash
docker-compose logs -f
```

To view logs from a specific service:

```bash
docker-compose logs -f gateway
docker-compose logs -f cms-main-system
docker-compose logs -f lms-main-system
```

## 🌐 Service Endpoints

Once all services are running, you can access:

| Service | URL | Description |
|---------|-----|-------------|
| **API Gateway** | http://localhost:4001 | Main entry point for all API requests |
| **CMS Service** | http://localhost:4002 | Content Management System API |
| **LMS Service** | http://localhost:4003 | Learning Management System API |
| **Consul UI** | http://localhost:8500 | Service discovery and configuration UI |
| **Dozzle** | http://localhost:4004 | Log viewer for all containers |
| **File Service** | http://localhost:4005 | File upload/download service |
| **Email Service** | http://localhost:4006 | Email sending service |
| **AI Service** | http://localhost:4007 | AI-powered features service |
| **PgAdmin** | http://localhost:4008 | PostgreSQL database management UI |

### Database Connections

| Database | Host | Port | Database Name |
|----------|------|------|---------------|
| **CMS DB** | localhost | 5401 | cms_db |
| **LMS DB** | localhost | 5402 | lms_db |

## 🛠️ Common Commands

### Stop All Services

```bash
docker-compose down
```

### Stop and Remove Volumes (⚠️ This will delete database data)

```bash
docker-compose down -v
```

### Restart a Specific Service

```bash
docker-compose restart gateway
```

### Rebuild a Specific Service

```bash
docker-compose up -d --build gateway
```

### View Service Health

```bash
# Check Consul for service registration
curl http://localhost:8500/v1/health/service/cms-service

# Check individual service health
curl http://localhost:4002/health
curl http://localhost:4003/health
```

## 🐛 Troubleshooting

### Port Already in Use

If you get an error about ports being already in use:

1. Check what's using the port:
   ```bash
   sudo lsof -i :4001
   ```

2. Either stop the conflicting service or change the port in `docker-compose.yml`

### Database Connection Issues

1. Ensure PostgreSQL containers are healthy:
   ```bash
   docker-compose ps postgres-cms postgres-lms
   ```

2. Check database logs:
   ```bash
   docker-compose logs postgres-cms
   ```

### Service Won't Start

1. Check service logs:
   ```bash
   docker-compose logs <service-name>
   ```

2. Verify environment variables are set correctly:
   ```bash
   docker-compose config
   ```

3. Rebuild the service:
   ```bash
   docker-compose up -d --build <service-name>
   ```

### Clean Start (Reset Everything)

If you want to start fresh:

```bash
# Stop and remove all containers, networks, and volumes
docker-compose down -v

# Remove all images (optional)
docker-compose down --rmi all

# Start fresh
docker-compose up -d --build
```

## 📚 Additional Resources

- **Consul UI**: http://localhost:8500 - View registered services
- **Dozzle**: http://localhost:4004 - View all container logs in real-time
- **PgAdmin**: http://localhost:4008 - Manage databases with a GUI

## 🔐 Security Notes

- Never commit your `.env` file to version control
- Use strong passwords for production environments
- Keep your API keys and secrets secure
- The `.env` file is already in `.gitignore`

## ✅ Verification Checklist

- [ ] Docker and Docker Compose installed
- [ ] `.env` file created and configured
- [ ] All services started successfully (`docker-compose ps`)
- [ ] API Gateway accessible at http://localhost:4001
- [ ] Consul UI accessible at http://localhost:8500
- [ ] No errors in service logs

## 🆘 Need Help?

If you encounter issues:

1. Check the logs: `docker-compose logs -f`
2. Verify your `.env` file configuration
3. Ensure all prerequisites are installed
4. Check the service health endpoints

---

Happy coding! 🎉

