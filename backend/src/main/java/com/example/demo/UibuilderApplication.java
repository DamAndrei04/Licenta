package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punctul de intrare al modulului backend (REST API). Pornește aplicația Spring Boot
 * care expune serviciile de gestionare a utilizatorilor, proiectelor, paginilor și
 * componentelor.
 */
@SpringBootApplication
public class UibuilderApplication {

	/**
	 * Metoda principală care pornește aplicația Spring Boot.
	 *
	 * @param args argumentele liniei de comandă
	 */
	public static void main(String[] args) {
		SpringApplication.run(UibuilderApplication.class, args);
	}

}
