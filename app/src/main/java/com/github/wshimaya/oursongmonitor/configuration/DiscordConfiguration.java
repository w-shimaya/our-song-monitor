package com.github.wshimaya.oursongmonitor.configuration;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties regarding Discord webhook.
 */
@ConfigurationProperties(prefix = "discord")
@Data
public class DiscordConfiguration {

  /**
   * URL.
   */
  @NotBlank
  private String webhookUrl;
}
