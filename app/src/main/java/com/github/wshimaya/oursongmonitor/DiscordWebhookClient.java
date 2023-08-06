package com.github.wshimaya.oursongmonitor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Client that send requests to webhook of Discord.
 */
@Slf4j
public class DiscordWebhookClient {

  /**
   * Client {@link HttpClient}.
   */
  private final HttpClient client;

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
  public DiscordWebhookClient(final String webhookUrl) {
    client = HttpClient.newBuilder()
        .version(Version.HTTP_2)
        .build();
    this.webhookUrl = webhookUrl;
  }

  /**
   * Send a message via webhook.
   *
   * @param body {@link MessageBody}
   */
  public void sendMessage(final MessageBody body) {
    String bodyJson;
    try {
      bodyJson = mapper.writeValueAsString(body);
    } catch (JsonProcessingException exception) {
      log.error("Failed to build discord webhook request: {}", exception.getMessage());
      return;
    }

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(webhookUrl))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
        .build();

    try {
      client.send(request, HttpResponse.BodyHandlers.ofString());
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
