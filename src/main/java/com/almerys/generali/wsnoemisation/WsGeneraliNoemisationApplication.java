package com.almerys.generali.wsnoemisation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class WsGeneraliNoemisationApplication implements WebMvcConfigurer{

	public static void main(String[] args) {
		SpringApplication.run(WsGeneraliNoemisationApplication.class, args);
	}

}
