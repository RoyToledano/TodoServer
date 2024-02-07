package com.http_server.demo;


import Controller.ServerHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import repository.TodoMongoRepository;

@SpringBootApplication
@Import({ServerHandler.class})
@ComponentScan(basePackages = {"service", "repository"})
public class HttpServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HttpServerApplication.class, args);
	}

}
