package com.example.demo;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplicationBuilder app = new SpringApplicationBuilder(DemoApplication.class)
		        .web(WebApplicationType.SERVLET);
		      app.build();
		app.run(args);
	}

}
