package com.github.wshimaya.oursongmonitor;


import static com.github.wshimaya.oursongmonitor.DiscordWebhookClient.MessageBody.MAX_EMBED_NUM;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.github.wshimaya.oursongmonitor.DiscordWebhookClient.MessageBody;
import com.github.wshimaya.oursongmonitor.DiscordWebhookClient.MessageBody.Embed;
import com.github.wshimaya.oursongmonitor.DiscordWebhookClient.MessageBody.Embed.Field;
import com.github.wshimaya.oursongmonitor.DiscordWebhookClient.MessageBody.Embed.Thumbnail;
import com.github.wshimaya.oursongmonitor.model.PlaylistItem;
import com.github.wshimaya.oursongmonitor.model.PlaylistItemList;
import com.github.wshimaya.oursongmonitor.model.PlaylistItemSnippet;
import com.github.wshimaya.oursongmonitor.model.ResourceId;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube.Builder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Message handler invoked by Lambda.
 */
public class MonitorHandler implements RequestHandler<ScheduledEvent, Void> {

  /**
   * Prefix to build URL to YouTube videos.
   */
  private static final String YOUTUBE_URL_PREFIX = "https://youtube.com/watch?v=";

  /**
   * S3 directory name to store playlist items.
   */
  public static final String S3_DIRECTORY = "our-song-monitor/";

  /**
   * YouTube API client.
   */
  private final PlaylistItemsClient playlistClient;

  /**
   * Discord client.
   */
  private final DiscordWebhookClient discordClient;

  /**
   * S3 client.
   */
  private final S3Client s3Client;

  /**
   * Configuration.
   */
  private final Configuration configuration;

  /**
   * Logger.
   */
  public static LambdaLogger logger;

  /**
   * Constructor.
   *
   * @throws Exception runtime error
   */
  public MonitorHandler() throws Exception {
    configuration = Configuration.fromEnv();
    playlistClient = new PlaylistItemsClient(
        new Builder(GoogleNetHttpTransport.newTrustedTransport(),
            new GsonFactory(),
            null)
            .setApplicationName("our-song-monitor")
            .build(), configuration.apiKey());
    discordClient = new DiscordWebhookClient(configuration.discordWebhookUrl());
    s3Client = new S3Client(AmazonS3ClientBuilder.standard()
        .withRegion(Regions.AP_NORTHEAST_1)
        .build());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Void handleRequest(ScheduledEvent input, Context context) {
    logger = context.getLogger();
    logger.log("DEBUG: Handler started\n");

    // fetch all items in the playlist
    var playlistItems = playlistClient.fetchAllPlaylistItems(configuration.playlistId());
    if (isNull(playlistItems)) {
      logger.log("ERROR: Unable to get playlist items\n");
      return null;
    }
    PlaylistItemList currentPlaylistItemList = new PlaylistItemList(playlistItems);

    // get playlist items stored in the previous run
    var latestResult = s3Client.findNewestObject(
        configuration.bucketName(), S3_DIRECTORY,
        configuration.playlistId() + ".json");

    // if it is the first run, skip notification
    if (nonNull(latestResult)) {
      logger.log("DEBUG: Previous run was found, %s\n".formatted(latestResult.getKey()));
      PlaylistItemList latestPlaylistItemList = s3Client.getObject(
          configuration.bucketName(), latestResult.getKey(), PlaylistItemList.class);

      // compute diff
      List<PlaylistItem> addedItems = getDifference(currentPlaylistItemList.getItems(),
          latestPlaylistItemList.getItems());
      List<PlaylistItem> removedItems = getDifference(latestPlaylistItemList.getItems(),
          currentPlaylistItemList.getItems());

      // send message via discord webhook
      MessageBody body = buildDiscordMessage(addedItems, removedItems);

      discordClient.sendMessage(body);
    }

    var key = S3_DIRECTORY
        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        + "-"
        + configuration.playlistId()
        + ".json";
    logger.log("DEBUG: Writing %s\n".formatted(key));
    boolean succeeded = s3Client.putJsonObject(configuration.bucketName(), key,
        currentPlaylistItemList);
    if (!succeeded) {
      logger.log("ERROR: Unable to upload json\n");
    }

    return null;
  }

  /**
   * Get list of items that exists in {@code a}, and does not in {@code b}.
   *
   * @param a List
   * @param b List
   * @return a - b
   */
  private List<PlaylistItem> getDifference(List<PlaylistItem> a, List<PlaylistItem> b) {
    return a.stream()
        .filter(item -> !b.stream()
            .map(PlaylistItem::getSnippet)
            .map(PlaylistItemSnippet::getResourceId)
            .map(ResourceId::getVideoId)
            .collect(Collectors.toList())
            .contains(item.getSnippet().getResourceId().getVideoId()))
        .collect(Collectors.toList());
  }

  /**
   * Build a message.
   *
   * @param addedItems   items newly added to the playlist
   * @param removedItems items removed from the playlist
   * @return {@link MessageBody}
   */
  public MessageBody buildDiscordMessage(final List<PlaylistItem> addedItems,
      final List<PlaylistItem> removedItems) {
    int numDisplayAdded = addedItems.size();
    int numDisplayRemoved = removedItems.size();
    if (numDisplayAdded + numDisplayRemoved >= MAX_EMBED_NUM) {
      numDisplayRemoved = Integer.max(MAX_EMBED_NUM - numDisplayAdded, 3);
      numDisplayAdded = MAX_EMBED_NUM - numDisplayRemoved;
    }

    return MessageBody.builder()
        .embeds(
            Stream.concat(
                addedItems.stream().limit(numDisplayAdded)
                    .map(item ->
                        Embed.builder()
                            .title(":musical_note: " + item.getSnippet().getTitle())
                            .url(
                                YOUTUBE_URL_PREFIX + item.getSnippet().getResourceId().getVideoId())
                            .thumbnail(Thumbnail.builder()
                                .url(item.getSnippet().getThumbnails().getDef().getUrl())
                                .build())
                            .description(addedItems.size() + "件の歌がみんなのうたに追加されました！ :+1:")
                            .fields(List.of(
                                Field.builder()
                                    .name("Channel")
                                    .value(item.getSnippet().getVideoOwnerChannelTitle())
                                    .inline(true)
                                    .build()
                            ))
                            .build()),
                removedItems.stream().limit(numDisplayRemoved)
                    .map(item -> Embed.builder()
                        .title(":wave: " + item.getSnippet().getTitle())
                        .url(
                            YOUTUBE_URL_PREFIX + item.getSnippet().getResourceId().getVideoId())
                        .thumbnail(Thumbnail.builder()
                            .url(item.getSnippet().getThumbnails().getDef().getUrl())
                            .build())
                        .description(removedItems.size() + "件の歌がみんなのうたから消えました :sob:")
                        .fields(List.of(
                            Field.builder()
                                .name("Channel")
                                .value(item.getSnippet().getVideoOwnerChannelTitle())
                                .inline(true)
                                .build()
                        )).build())
            ).collect(Collectors.toList())
        ).build();
  }
}
