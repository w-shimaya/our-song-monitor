package com.github.wshimaya.oursongmonitor;

import com.github.wshimaya.oursongmonitor.configuration.AppConfiguration;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppConfiguration.class)
public class Application {

  public static void main(String[] args) {
    SpringApplication application = new SpringApplication(Application.class);
    application.setBannerMode(Banner.Mode.OFF);
    application.run(args);
  }

}
