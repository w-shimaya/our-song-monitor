package com.github.wshimaya.oursongmonitor;

/**
 * Record of parameters.
 */
public record Configuration(String playlistId, String bucketName, String apiKey,
                            String discordWebhookUrl) {

  /**
   * Collects config values from environment variables.
   *
   * @return configuration
   */
  public static Configuration fromEnv() {
    return new Configuration(System.getenv("PLAYLIST_ID"),
        System.getenv("S3_BUCKET_NAME"),
        System.getenv("YOUTUBE_API_KEY"),
        System.getenv("DISCORD_WEBHOOK_URL"));
  }

}
