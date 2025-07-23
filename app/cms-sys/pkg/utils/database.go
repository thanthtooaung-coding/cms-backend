package utils

import (
	"fmt"
	"time"

	"github.com/sirupsen/logrus"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
	"gorm.io/gorm/logger"
)

type DatabaseConfig struct {
	Host            string
	Port            int
	User            string
	Password        string
	DBName          string
	SSLMode         string
	MaxOpenConns    int
	MaxIdleConns    int
	ConnMaxLifetime time.Duration
	ConnMaxIdleTime time.Duration
	RetryAttempts   int
	RetryDelay      time.Duration
	LogLevel        logger.LogLevel
}

type DatabaseConnection struct {
	DB     *gorm.DB
	config DatabaseConfig
	logger *logrus.Logger
}

func NewDatabaseConnection(config DatabaseConfig, logger *logrus.Logger) *DatabaseConnection {
	return &DatabaseConnection{
		config: config,
		logger: logger,
	}
}

func (dc *DatabaseConnection) Connect() error {
	var db *gorm.DB
	var err error

	dsn := fmt.Sprintf("host=%s port=%d user=%s password=%s dbname=%s sslmode=%s",
		dc.config.Host, dc.config.Port, dc.config.User, dc.config.Password, dc.config.DBName, dc.config.SSLMode)

	gormLogger := logger.New(
		dc.logger,
		logger.Config{
			SlowThreshold:             200 * time.Millisecond,
			LogLevel:                  dc.config.LogLevel,
			IgnoreRecordNotFoundError: true,
			Colorful:                  false,
		},
	)

	for i := 0; i < dc.config.RetryAttempts; i++ {
		db, err = gorm.Open(postgres.Open(dsn), &gorm.Config{
			Logger: gormLogger,
		})

		if err == nil {
			dc.logger.Info("Successfully connected to the database")
			break
		}

		dc.logger.WithError(err).Warnf("Failed to connect to database. Retrying in %v... (Attempt %d/%d)",
			dc.config.RetryDelay, i+1, dc.config.RetryAttempts)
		time.Sleep(dc.config.RetryDelay)
	}

	if err != nil {
		return fmt.Errorf("failed to connect to database after %d attempts: %w", dc.config.RetryAttempts, err)
	}

	sqlDB, err := db.DB()
	if err != nil {
		return fmt.Errorf("failed to get underlying sql.DB: %w", err)
	}

	sqlDB.SetMaxOpenConns(dc.config.MaxOpenConns)
	sqlDB.SetMaxIdleConns(dc.config.MaxIdleConns)
	sqlDB.SetConnMaxLifetime(dc.config.ConnMaxLifetime)
	sqlDB.SetConnMaxIdleTime(dc.config.ConnMaxIdleTime)

	if err = sqlDB.Ping(); err != nil {
		return fmt.Errorf("failed to ping database: %w", err)
	}

	dc.DB = db
	dc.logger.Info("Database connection pool configured and verified.")
	return nil
}

func (dc *DatabaseConnection) GetStats() map[string]interface{} {
	if dc.DB == nil {
		return nil
	}

	sqlDB, err := dc.DB.DB()
	if err != nil {
		dc.logger.WithError(err).Error("Failed to get database stats")
		return nil
	}

	stats := sqlDB.Stats()
	return map[string]interface{}{
		"max_open_connections": stats.MaxOpenConnections,
		"open_connections":     stats.OpenConnections,
		"in_use":               stats.InUse,
		"idle":                 stats.Idle,
		"wait_count":           stats.WaitCount,
		"wait_duration":        stats.WaitDuration,
		"max_idle_closed":      stats.MaxIdleClosed,
		"max_idle_time_closed": stats.MaxIdleTimeClosed,
		"max_lifetime_closed":  stats.MaxLifetimeClosed,
	}
}