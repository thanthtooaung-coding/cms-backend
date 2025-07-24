package main

import (
    "time"
    "fmt"
    "net"
    "github.com/sirupsen/logrus"
    "github.com/hashicorp/consul/api"
    "github.com/thanthtooaung-coding/cms-backend/app/cms-sys/pkg/utils"
    "github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/routes"
    "github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/handler"
    "github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/repository"
    "github.com/thanthtooaung-coding/cms-backend/app/cms-sys/internal/service"
    "github.com/gofiber/fiber/v2"
    loggMiddleware "github.com/gofiber/fiber/v2/middleware/logger"
    "strconv"
    "strings"
    "os"
    "os/signal"
    "errors"
    "syscall"
    "gorm.io/gorm/logger"
    "gorm.io/gorm"
)

type dISection struct {
	ownerHandler       handler.OwnerHandle
	consulClient       *api.Client
}

type consulConfig struct {
	Address    string
	Datacenter string
	Token      string
	Scheme     string
	ServiceID  string
	Name       string
	Tags       []string
	Port       int
	CheckTTL   time.Duration
	CheckHTTP  string
}

func newConsulClient(config consulConfig, logger *logrus.Logger) (*api.Client, error) {
	consulConfig := api.DefaultConfig()
	consulConfig.Address = config.Address
	consulConfig.Datacenter = config.Datacenter
	consulConfig.Token = config.Token
	consulConfig.Scheme = config.Scheme

	client, err := api.NewClient(consulConfig)
	if err != nil {
		logger.WithError(err).Error("Failed to create Consul client")
		return nil, err
	}

	logger.WithField("address", config.Address).Info("Consul client created successfully")
	return client, nil
}

func registerService(client *api.Client, config consulConfig, logger *logrus.Logger) error {
	localIP, err := getLocalIP()
	if err != nil {
		logger.WithError(err).Error("Failed to get local IP address")
		return err
	}

	cmsService := &api.AgentServiceRegistration{
		ID:   config.ServiceID,
		Name: config.Name,
		Port:    config.Port,
		Address: localIP,
		Meta: map[string]string{
			"health-check-path": "/cms/health",
		},
		Checks: api.AgentServiceChecks{
			{
				HTTP:                           fmt.Sprintf("http://%s:%d/cms/health", localIP, config.Port),
				Interval:                       "10s",
				Timeout:                        "5s",
				DeregisterCriticalServiceAfter: "30s",
			},
			{
				CheckID:                        config.ServiceID + ":ttl",
				TTL:                            config.CheckTTL.String(),
				DeregisterCriticalServiceAfter: "1m",
			},
		},
	}

	err = client.Agent().ServiceRegister(cmsService)
	if err != nil {
		logger.WithError(err).Error("Failed to register service with Consul")
		return err
	}

	logger.WithFields(logrus.Fields{
		"service_id":   config.ServiceID,
		"service_name": config.Name,
		"address":      localIP,
		"port":         config.Port,
		"metadata":     "health-check-path=/cms/health",
	}).Info("Service registered with Consul successfully")

	return nil
}

func getLocalIP() (string, error) {
	conn, err := net.Dial("udp", "8.8.8.8:80")
	if err != nil {
		return "", err
	}
	defer func(conn net.Conn) {
		err := conn.Close()
		if err != nil {

		}
	}(conn)

	localAddr := conn.LocalAddr().(*net.UDPAddr)
	return localAddr.IP.String(), nil
}

func loadConsulConfig() consulConfig {
	port, _ := strconv.Atoi(utils.GetEnv("PORT", "8081"))
	checkTTL, _ := time.ParseDuration(utils.GetEnv("CONSUL_CHECK_TTL", "30s"))

	tagsStr := utils.GetEnv("CONSUL_SERVICE_TAGS", "cms,multi-tenant,api")
	var tags []string
	if tagsStr != "" {
		for _, tag := range strings.Split(tagsStr, ",") {
			tags = append(tags, strings.TrimSpace(tag))
		}
	}

	localIP, err := getLocalIP()
	if err != nil {
		localIP, _ = os.Hostname()
	}

	return consulConfig{
		Address:    utils.GetEnv("CMS_CONSUL_ADDRESS", "consul:8500"),
		Datacenter: utils.GetEnv("CMS_CONSUL_DATACENTER", "dc1"),
		Token:      utils.GetEnv("CMS_CONSUL_TOKEN", ""),
		Scheme:     utils.GetEnv("CMS_CONSUL_SCHEME", "http"),
		ServiceID:  utils.GetEnv("CMS_CONSUL_SERVICE_ID", fmt.Sprintf("cms-api-%s", localIP)),
		Name:       utils.GetEnv("CMS_CONSUL_SERVICE_NAME", "cms-service"),
		Tags:       tags,
		Port:       port,
		CheckTTL:   checkTTL,
		CheckHTTP:  fmt.Sprintf("http://%s:%d/health", localIP, port),
	}
}

func startHealthUpdateRoutine(client *api.Client, serviceID string, logger *logrus.Logger) {
	ticker := time.NewTicker(10 * time.Second)
	go func() {
		for range ticker.C {
			err := client.Agent().UpdateTTL(serviceID+":ttl", "Service is healthy", api.HealthPassing)
			if err != nil {
				logger.WithError(err).Error("Failed to update service health check")
			}
		}
	}()
}

func deregisterService(client *api.Client, serviceID string, logger *logrus.Logger) error {
	err := client.Agent().ServiceDeregister(serviceID)
	if err != nil {
		logger.WithError(err).Error("Failed to deregister service from Consul")
		return err
	}

	logger.WithField("service_id", serviceID).Info("Service deregistered from Consul")
	return nil
}

func dependencyInjectionSection(
	logger *logrus.Logger,
	db *gorm.DB,
	consulClient *api.Client,
) *dISection {
	ownerRepo := repository.NewOwnerRepository(logger, db)
	ownerService := service.NewOwnerService(logger, ownerRepo)
	ownerHandler := handler.NewOwnerHandler(ownerService)

	return &dISection{
		ownerHandler:       ownerHandler,
	}
}

func main() {
    appLogger := utils.NewLogger(utils.LogConfig{
		Level:      utils.GetEnv("CMS_LOG_LEVEL", "info"),
		FilePath:   utils.GetEnv("CMS_LOG_FILE_PATH", "logs/app.log"),
		MaxSize:    utils.GetEnvAsInt("CMS_LOG_MAX_SIZE", 100),
		MaxBackups: utils.GetEnvAsInt("CMS_LOG_MAX_BACKUPS", 5),
		MaxAge:     utils.GetEnvAsInt("CMS_LOG_MAX_AGE", 30),
		Compress:   utils.GetEnvAsBool("CMS_LOG_COMPRESS", true),
		Console:    utils.GetEnvAsBool("CMS_LOG_CONSOLE", true),
	})

	appLogger.Info("Starting Content Management System")

	dbConfig := utils.DatabaseConfig{
        Host:            utils.GetEnv("CMS_DB_HOST", "localhost"),
        Port:            utils.GetEnvAsInt("CMS_DB_PORT", 5432),
        User:            utils.GetEnv("CMS_DB_USER", "postgres"),
        Password:        utils.GetEnv("CMS_DB_PASSWORD", "cms_password_123"),
        DBName:          utils.GetEnv("CMS_DB_NAME", "cms_db"),
        SSLMode:         utils.GetEnv("CMS_DB_SSL_MODE", "disable"),
        MaxOpenConns:    utils.GetEnvAsInt("CMS_DB_MAX_OPEN_CONNS", 25),
        MaxIdleConns:    utils.GetEnvAsInt("CMS_DB_MAX_IDLE_CONNS", 10),
        ConnMaxLifetime: utils.GetEnvAsDuration("CMS_DB_CONN_MAX_LIFETIME", 5*time.Minute),
        ConnMaxIdleTime: utils.GetEnvAsDuration("CMS_DB_CONN_MAX_IDLE_TIME", 2*time.Minute),
        RetryAttempts:   utils.GetEnvAsInt("CMS_DB_RETRY_ATTEMPTS", 5),
        RetryDelay:      utils.GetEnvAsDuration("CMS_DB_RETRY_DELAY", 2*time.Second),
        LogLevel:        logger.Info,
    }

	dbConnection := utils.NewDatabaseConnection(dbConfig, appLogger)
    if err := dbConnection.Connect(); err != nil {
        appLogger.WithError(err).Fatal("Failed to initialize database connection")
    }

    consulConfig := loadConsulConfig()
	consulEnabled := utils.GetEnvAsBool("CONSUL_ENABLED", false)

	var consulClient *api.Client
	if consulEnabled {
		var err error
		consulClient, err = newConsulClient(consulConfig, appLogger)
		if err != nil {
			appLogger.WithError(err).Fatal("Failed to create Consul client")
		}
	}

    healthChecker := utils.NewHealthChecker(dbConnection.DB, appLogger)

    app := fiber.New(fiber.Config{
		AppName: "Content Management System ",
		ErrorHandler: func(c *fiber.Ctx, err error) error {
			code := fiber.StatusInternalServerError
			var e *fiber.Error
			if errors.As(err, &e) {
				code = e.Code
			}

			appLogger.WithFields(logrus.Fields{
				"method": c.Method(),
				"path":   c.Path(),
				"ip":     c.IP(),
				"error":  err.Error(),
				"code":   code,
			}).Error("Request error")

			return c.Status(code).JSON(fiber.Map{
				"success": false,
				"message": err.Error(),
			})
		},
	})

	app.Use(loggMiddleware.New(loggMiddleware.Config{
		Format: "${time} | ${status} | ${latency} | ${ip} | ${method} | ${path} | ${error}\n",
		Output: appLogger.Writer(),
	}))

    app.Get("/", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{
			"message": "CMS Multi-Tenant API",
			"status":  "success",
			"version": "1.0.0",
		})
	})

	cmsGroup := app.Group("/cms")

	cmsGroup.Get("/health", func(c *fiber.Ctx) error {
		health := healthChecker.CheckHealth()

		statusCode := fiber.StatusOK
		if health.Status != "healthy" {
			statusCode = fiber.StatusServiceUnavailable
		}

		return c.Status(statusCode).JSON(health)
	})

	cmsGroup.Get("/health/database", func(c *fiber.Ctx) error {
		stats := dbConnection.GetStats()
		return c.JSON(fiber.Map{
			"status": "healthy",
			"stats":  stats,
		})
	})

	cmsGroup.Get("/health/consul", func(c *fiber.Ctx) error {
		if !consulEnabled || consulClient == nil {
			return c.Status(fiber.StatusServiceUnavailable).JSON(fiber.Map{
				"status":  "disabled",
				"message": "Consul is not enabled",
			})
		}

		_, err := consulClient.Status().Leader()
		if err != nil {
			return c.Status(fiber.StatusServiceUnavailable).JSON(fiber.Map{
				"status": "unhealthy",
				"error":  err.Error(),
			})
		}

		return c.JSON(fiber.Map{
			"status": "healthy",
			"consul": "connected",
		})
	})

    di := dependencyInjectionSection(appLogger, dbConnection.DB, consulClient)
	routes.SetupOwnerRoutes(app, di.ownerHandler)

    port := utils.GetEnv("CMS_PORT", "8081")

	if consulEnabled && consulClient != nil {
		if err := registerService(consulClient, consulConfig, appLogger); err != nil {
			appLogger.WithError(err).Error("Failed to register service with Consul")
		} else {
			startHealthUpdateRoutine(consulClient, consulConfig.ServiceID, appLogger)
		}
	}
	appMode := utils.GetEnv("CMS_APP_STATUS", "production")
	appLogger.WithFields(logrus.Fields{
		"mode": appMode,
	}).Info("Starting App With Mode " + appMode)
	appLogger.Info("Starting App With Mode " + appMode)
	go func() {
		appLogger.WithField("port", port).Info("Server starting")
		if err := app.Listen("0.0.0.0:" + port); err != nil {
			appLogger.WithError(err).Fatal("Server failed to start")
		}
	}()

	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit

	appLogger.Info("Shutting down server...")
	if consulEnabled && consulClient != nil {
		if err := deregisterService(consulClient, consulConfig.ServiceID, appLogger); err != nil {
			appLogger.WithError(err).Error("Failed to deregister service from Consul")
		}
	}
}