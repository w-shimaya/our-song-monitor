package com.github.wshimaya.oursongmonitor.configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties regarding S3.
 */
@ConfigurationProperties(prefix = "s3")
@Data
@Validated
public class S3Configuration {

  /**
   * Bucket name.
   */
  @NotEmpty
  @Pattern(regexp = ".+/$")
  private String bucketName;

  /**
   * Directory (prefix).
   */
  @NotBlank
  private String directoryName;
}
