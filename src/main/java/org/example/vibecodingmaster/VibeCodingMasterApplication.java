package org.example.vibecodingmaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class VibeCodingMasterApplication {

    public static void main(String[] args) {
        SpringApplication.run(VibeCodingMasterApplication.class, args);
    }

}
