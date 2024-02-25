package com.github.wshimaya.oursongmonitor.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.lang.NonNull;
import java.net.http.HttpClient;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

/**
 * Client that send requests to webhook of Discord.
 */
@Slf4j
public class DiscordWebhookClient {

  /**
   * Client {@link HttpClient}.
   */
  private final RestTemplate restTemplate;

  /**
   * URL of the Discord webhook.
   */
  private final String webhookUrl;

  /**
   * Mapper {@link ObjectMapper}.
   */
  private final ObjectMapper mapper = new ObjectMapper();

  /**
   * Constructor.
   *
   * @param webhookUrl URL of Discord webhook
   */
  public DiscordWebhookClient(@NonNull final String webhookUrl,
      @NonNull final RestTemplate restTemplate) {
    this.webhookUrl = webhookUrl;
    this.restTemplate = restTemplate;
  }

  /**
   * Send a message via webhook.
   *
   * @param body {@link MessageBody}
   */
  public void sendMessage(@NonNull final MessageBody body) {
    try {
      String response = restTemplate.postForObject(webhookUrl, body, String.class);
    } catch (Exception exception) {
      log.error("Failed to send a discord message: {}", exception.getMessage());
    }
  }

  /**
   * Data model of <a href="https://discord.com/developers/docs/resources/webhook#execute-webhook">message
   * body</a>.
   */
  @Builder
  @Data
  public static class MessageBody {

    /**
     * Maximum number of embeds.
     */
    public static final int MAX_EMBED_NUM = 10;

    /**
     * Content of a simple message.
     */
    String content;

    /**
     * Rich messages.
     */
    List<Embed> embeds;

    /**
     * Data model of <a href="https://discord.com/developers/docs/resources/channel#embed-object">embeds</a>.
     */
    @Builder
    @Data
    public static class Embed {

      /**
       * Title.
       */
      String title;

      /**
       * Type (always "rich").
       */
      String type;

      /**
       * Description.
       */
      String description;

      /**
       * Url.
       */
      String url;

      /**
       * Thumbnail.
       */
      Thumbnail thumbnail;

      /**
       * Fields.
       */
      List<Field> fields;

      /**
       * Data model of <a href="https://discord.com/developers/docs/resources/channel#embed-object-embed-field-structure">embed
       * fields</a>.
       */
      @Builder
      @Data
      public static class Field {

        /**
         * Name.
         */
        String name;

        /**
         * Value.
         */
        String value;

        /**
         * Inline.
         */
        Boolean inline;
      }

      /**
       * Data model of <a href="https://discord.com/developers/docs/resources/channel#embed-object-embed-thumbnail-structure">embed
       * thumbnails</a>.
       */
      @Builder
      @Data
      public static class Thumbnail {

        /**
         * URL of the image.
         */
        String url;

        /**
         * Height (optional).
         */
        Integer height;

        /**
         * Width (optional).
         */
        Integer width;
      }
    }
  }
}
