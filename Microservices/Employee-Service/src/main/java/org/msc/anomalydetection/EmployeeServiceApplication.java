package org.msc.anomalydetection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmployeeServiceApplication {

    public static void main(String[] args) {

        System.out.println("main found");
        SpringApplication.run(EmployeeServiceApplication.class, args);
    }
}
