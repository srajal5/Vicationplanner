package com.vacationplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin(origins = "http://localhost:3000") // React dev server
public class VacationPlannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(VacationPlannerApplication.class, args);
    }
}
