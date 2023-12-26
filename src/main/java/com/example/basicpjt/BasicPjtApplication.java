package com.example.basicpjt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BasicPjtApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasicPjtApplication.class, args);
    }

}
