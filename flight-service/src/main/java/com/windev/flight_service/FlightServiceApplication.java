package com.windev.flight_service;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(
				title = "Flight Service REST APIs",
				description = "Comprehensive documentation of Flight Service REST APIs.",
				version = "v1.0",
				contact = @Contact(
						name = "Ha Thanh Phong",
						email = "haphong2134@gmail.com"
				),
				license = @License(
						name = "Apache 2.0",
						url = "http://www.apache.org/licenses/LICENSE-2.0.html"
				)
		),
		servers = {
				@Server(url = "http://localhost:8081", description = "Local Development Server"),
		}
)
@SpringBootApplication
public class FlightServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlightServiceApplication.class, args);
	}

}
