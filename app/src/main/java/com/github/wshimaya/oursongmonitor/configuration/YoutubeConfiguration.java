package com.github.wshimaya.oursongmonitor.configuration;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "youtube")
@Data
public class YoutubeConfiguration {

  @NotBlank
  private String apiKey;
}
