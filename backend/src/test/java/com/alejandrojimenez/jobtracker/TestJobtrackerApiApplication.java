package com.alejandrojimenez.jobtracker;

import org.springframework.boot.SpringApplication;

public class TestJobtrackerApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(JobtrackerApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
