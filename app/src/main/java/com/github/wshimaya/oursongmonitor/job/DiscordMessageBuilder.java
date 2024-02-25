package com.github.wshimaya.oursongmonitor.job;

import static com.github.wshimaya.oursongmonitor.client.DiscordWebhookClient.MessageBody.MAX_EMBED_NUM;

import com.github.wshimaya.oursongmonitor.client.DiscordWebhookClient.MessageBody;
import com.github.wshimaya.oursongmonitor.client.DiscordWebhookClient.MessageBody.Embed;
import com.github.wshimaya.oursongmonitor.client.DiscordWebhookClient.MessageBody.Embed.Field;
import com.github.wshimaya.oursongmonitor.client.DiscordWebhookClient.MessageBody.Embed.Thumbnail;
import com.github.wshimaya.oursongmonitor.model.PlaylistItem;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

/**
 * Discord message builder.
 */
@Component
public class DiscordMessageBuilder {

  /**
   * Prefix to build URL to YouTube videos.
   */
  private static final String YOUTUBE_URL_PREFIX = "https://youtube.com/watch?v=";

  /**
   * Summarize added/removed items and build message to post Discord webhook.
   *
   * @param addedItems added playlist items
   * @param removedItems removed playlist items
   * @return message body
   */
  public MessageBody build(final List<PlaylistItem> addedItems,
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
