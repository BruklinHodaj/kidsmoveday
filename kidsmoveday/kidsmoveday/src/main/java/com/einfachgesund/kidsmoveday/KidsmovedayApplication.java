package com.einfachgesund.kidsmoveday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Kids Move Day application.
 *
 * <p>Kids Move Day is a family health event management system for
 * EinfachGesund Krankenkasse. It exposes a RESTful API for managing
 * user registrations, guest sign-ups, and event statistics.</p>
 *
 * @author [FirstName LastName]
 * @version 1.0
 */
@SpringBootApplication
public class KidsmovedayApplication {

	/**
	 * Application entry point.
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(KidsmovedayApplication.class, args);
	}
}