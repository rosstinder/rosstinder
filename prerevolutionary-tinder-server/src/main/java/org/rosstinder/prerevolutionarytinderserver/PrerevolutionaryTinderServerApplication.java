package org.rosstinder.prerevolutionarytinderserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({"org.rosstinder.prerevolutionarytinderserver.controller", "org.rosstinder.prerevolutionarytinderserver.service"})
@EntityScan({"org.rosstinder.prerevolutionarytinderserver.model.entity"})
@EnableJpaRepositories("org.rosstinder.prerevolutionarytinderserver.model.repository")
public class PrerevolutionaryTinderServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrerevolutionaryTinderServerApplication.class, args);
    }

}
