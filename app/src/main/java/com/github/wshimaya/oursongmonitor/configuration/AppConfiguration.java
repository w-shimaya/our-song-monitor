package com.github.wshimaya.oursongmonitor.configuration;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "app")
@Data
public class AppConfiguration {

  @NestedConfigurationProperty
  private DiscordConfiguration discord;

  @NestedConfigurationProperty
  private YoutubeConfiguration youtube;

  @NestedConfigurationProperty
  private S3Configuration s3;

  @NotBlank
  private String playlistId;
}
