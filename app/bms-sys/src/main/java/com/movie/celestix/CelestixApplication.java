package com.movie.celestix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CelestixApplication {

	public static void main(String[] args) {
		SpringApplication.run(CelestixApplication.class, args);
	}

}
