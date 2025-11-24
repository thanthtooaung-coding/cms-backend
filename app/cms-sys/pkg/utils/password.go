package utils

import (
	"crypto/rand"
	"math/big"
	"golang.org/x/crypto/bcrypt"
)

const (
	letterBytes   = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
	specialBytes  = "!@#$%^&*()_+-=[]{}|'"
	numBytes      = "0123456789"
	allChars      = letterBytes + numBytes + specialBytes
)

func HashPassword(password string) (string, error) {
	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
	if err != nil {
		return "", err
	}
	return string(hashedPassword), nil
}

func CheckPassword(password, hashedPassword string) error {
	return bcrypt.CompareHashAndPassword([]byte(hashedPassword), []byte(password))
}

func GenerateSecurePassword(length int) (string, error) {
	b := make([]byte, length)
	for i := range b {
		n, err := rand.Int(rand.Reader, big.NewInt(int64(len(allChars))))
		if err != nil {
			return "", err
		}
		b[i] = allChars[n.Int64()]
	}

	b[0] = letterBytes[mustRandInt(len(letterBytes))]
	b[1] = numBytes[mustRandInt(len(numBytes))]
	b[2] = specialBytes[mustRandInt(len(specialBytes))]

	return string(b), nil
}

func mustRandInt(max int) int {
	n, err := rand.Int(rand.Reader, big.NewInt(int64(max)))
	if err != nil {
		panic(err)
	}
	return int(n.Int64())
}