package com.github.wshimaya.oursongmonitor.configuration;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Collection of configuration properties.
 */
@ConfigurationProperties(prefix = "app")
@Data
public class AppConfiguration {

  /**
   * Discord configuration.
   */
  @NestedConfigurationProperty
  private DiscordConfiguration discord;

  /**
   * Youtube configuration.
   */
  @NestedConfigurationProperty
  private YoutubeConfiguration youtube;

  /**
   * S3 configuration.
   */
  @NestedConfigurationProperty
  private S3Configuration s3;

  /**
   * ID of playlist to monitor.
   */
  @NotBlank
  private String playlistId;
}
