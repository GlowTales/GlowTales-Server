package com.example.glowtales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GlowTalesApplication {

    public static void main(String[] args) {
        SpringApplication.run(GlowTalesApplication.class, args);
    }

}
