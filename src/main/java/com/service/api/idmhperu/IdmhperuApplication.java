package com.service.api.idmhperu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IdmhperuApplication {

  public static void main(String[] args) {
    SpringApplication.run(IdmhperuApplication.class, args);
  }

}
