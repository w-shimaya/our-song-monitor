package com.github.wshimaya.oursongmonitor;

import com.github.wshimaya.oursongmonitor.configuration.AppConfiguration;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Application.
 */
@SpringBootApplication
@EnableConfigurationProperties(AppConfiguration.class)
public class Application {

  /**
   * Launch Spring Boot application.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    SpringApplication application = new SpringApplication(Application.class);
    application.setBannerMode(Banner.Mode.OFF);
    application.run(args);
  }

}
