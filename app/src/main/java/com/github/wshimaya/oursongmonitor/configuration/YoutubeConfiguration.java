package com.github.wshimaya.oursongmonitor.configuration;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties regarding YouTube.
 */
@ConfigurationProperties(prefix = "youtube")
@Data
public class YoutubeConfiguration {

  /**
   * API key.
   */
  @NotBlank
  private String apiKey;
}
