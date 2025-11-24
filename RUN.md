# 🚀 How to Run the CMS Backend Project

Welcome! This guide will walk you through running the CMS Backend project step by step. Follow these instructions to get all services up and running.

---

## 📋 Prerequisites

Before you begin, ensure you have the following installed on your system:

- **Docker** (version 20.10 or higher)
- **Docker Compose** (version 2.0 or higher)

### Verify Installation

```bash
docker --version
docker compose version
```

If Docker is not installed, please install it from [Docker's official website](https://www.docker.com/get-started).

---

## 🎯 Quick Start Guide

### Step 1: Navigate to Project Directory

```bash
cd /home/alvin/GitHub/cms-backend
```

### Step 2: Verify Environment File

Ensure you have a `.env` file in the root directory. If not, create one based on `.env.example`:

```bash
# Check if .env exists
ls -la .env

# If it doesn't exist, create it (you'll need to configure the values)
cp .env.example .env
```

> **Note**: The `.env` file contains all necessary environment variables for the services. Make sure to configure it with your actual values (database credentials, API keys, etc.).

### Step 3: Check Port Availability

Before starting, ensure the required ports are available. The project uses the following ports:

| Port | Service |
|------|---------|
| 4001 | API Gateway |
| 4003 | LMS Service |
| 4011 | CMS Service |
| 4012 | AI Service |
| 4013 | Email Service |
| 4014 | File Service |
| 4010 | PgAdmin |
| 4009 | Dozzle (Log Viewer) |
| 8501 | Consul UI |
| 5401 | PostgreSQL (CMS) |
| 5402 | PostgreSQL (LMS) |

If any ports are in use, you can either:
- Stop the conflicting service
- Modify the port mappings in `docker-compose.yml`

### Step 4: Start All Services

Run the following command to build and start all services:

```bash
docker compose up -d --build
```

This command will:
- Build Docker images for all services
- Start all containers in detached mode
- Set up networking and volumes
- Initialize databases

### Step 5: Verify Services are Running

Check the status of all containers:

```bash
docker compose ps
```

You should see all services with status "Up" or "Up (healthy)".

---

## 🌐 Accessing the Services

Once all services are running, you can access them at the following URLs:

### Main Services

| Service | URL | Description |
|---------|-----|-------------|
| **API Gateway** | http://localhost:4001 | Main entry point for all API requests |
| **CMS Service** | http://localhost:4011 | Content Management System API |
| **LMS Service** | http://localhost:4003 | Learning Management System API |
| **File Service** | http://localhost:4014 | File upload/download service |
| **Email Service** | http://localhost:4013 | Email sending service |
| **AI Service** | http://localhost:4012 | AI-powered features service |

### Management & Monitoring Tools

| Tool | URL | Description |
|------|-----|-------------|
| **Consul UI** | http://localhost:8501 | Service discovery and configuration UI |
| **Dozzle** | http://localhost:4009 | Real-time log viewer for all containers |
| **PgAdmin** | http://localhost:4010 | PostgreSQL database management UI |

### Database Connections

| Database | Host | Port | Database Name |
|----------|------|------|---------------|
| **CMS DB** | localhost | 5401 | cms_db |
| **LMS DB** | localhost | 5402 | lms_db |

---

## 🛠️ Common Commands

### View Logs

View logs from all services:
```bash
docker compose logs -f
```

View logs from a specific service:
```bash
docker compose logs -f gateway
docker compose logs -f cms-main-system
docker compose logs -f lms-main-system
```

### Stop All Services

```bash
docker compose down
```

### Stop and Remove Volumes (⚠️ This will delete database data)

```bash
docker compose down -v
```

### Restart a Specific Service

```bash
docker compose restart gateway
docker compose restart cms-main-system
```

### Rebuild and Restart a Service

```bash
docker compose up -d --build gateway
```

### Check Service Health

```bash
# Check Consul for service registration
curl http://localhost:8501/v1/health/service/cms-service

# Check individual service health endpoints
curl http://localhost:4011/health
curl http://localhost:4003/health
```

---

## 🔍 Troubleshooting

### Port Already in Use

If you encounter port conflicts:

1. **Find what's using the port:**
   ```bash
   sudo lsof -i :4001
   ```

2. **Stop the conflicting service** or **modify the port** in `docker-compose.yml`

3. **Restart the services:**
   ```bash
   docker compose down
   docker compose up -d
   ```

### Docker Daemon Not Running

If you see "Cannot connect to the Docker daemon":

1. **Start Docker Desktop** (if using Docker Desktop)
2. **Or start Docker service:**
   ```bash
   sudo systemctl start docker
   ```

### Service Won't Start

1. **Check service logs:**
   ```bash
   docker compose logs <service-name>
   ```

2. **Verify environment variables:**
   ```bash
   docker compose config
   ```

3. **Rebuild the service:**
   ```bash
   docker compose up -d --build <service-name>
   ```

### Database Connection Issues

1. **Ensure PostgreSQL containers are healthy:**
   ```bash
   docker compose ps postgres-cms postgres-lms
   ```

2. **Check database logs:**
   ```bash
   docker compose logs postgres-cms
   docker compose logs postgres-lms
   ```

3. **Verify database credentials** in your `.env` file

### Clean Start (Reset Everything)

If you want to start completely fresh:

```bash
# Stop and remove all containers, networks, and volumes
docker compose down -v

# Remove all images (optional)
docker compose down --rmi all

# Start fresh
docker compose up -d --build
```

---

## 📊 Service Dependencies

The services start in the following order due to dependencies:

1. **Consul** - Service discovery (must start first)
2. **PostgreSQL Databases** - CMS and LMS databases
3. **Core Services** - CMS, LMS, Gateway
4. **Supporting Services** - File, Email, AI services
5. **Management Tools** - PgAdmin, Dozzle

All services wait for their dependencies to be healthy before starting.

---

## ✅ Verification Checklist

Before considering the setup complete, verify:

- [ ] Docker and Docker Compose are installed and running
- [ ] `.env` file exists and is properly configured
- [ ] All services started successfully (`docker compose ps`)
- [ ] API Gateway is accessible at http://localhost:4001
- [ ] Consul UI is accessible at http://localhost:8501
- [ ] No errors in service logs (`docker compose logs`)
- [ ] Databases are healthy and accessible

---

## 🎉 Success!

If all services are running and accessible, congratulations! Your CMS Backend is now up and running. 

You can now:
- Access the API Gateway at http://localhost:4001
- Monitor services in Consul UI at http://localhost:8501
- View logs in Dozzle at http://localhost:4009
- Manage databases in PgAdmin at http://localhost:4010

---

## 📚 Additional Resources

- **Project README**: See `README.md` for architecture and design details
- **Setup Guide**: See `SETUP.md` for detailed setup instructions
- **API Documentation**: Check the `api-doc/` directory for API documentation

---

## 🆘 Need Help?

If you encounter issues not covered in this guide:

1. Check the service logs: `docker compose logs -f`
2. Verify your `.env` file configuration
3. Ensure all prerequisites are installed
4. Check the service health endpoints
5. Review the troubleshooting section above

---

**Happy Coding! 🚀**

