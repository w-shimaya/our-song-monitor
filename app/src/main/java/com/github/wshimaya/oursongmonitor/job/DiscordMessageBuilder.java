package com.github.wshimaya.oursongmonitor.job;

import static com.github.wshimaya.oursongmonitor.adapter.discord.DiscordWebhookClient.MessageBody.MAX_EMBED_NUM;

import com.github.wshimaya.oursongmonitor.adapter.discord.DiscordWebhookClient.MessageBody;
import com.github.wshimaya.oursongmonitor.adapter.discord.DiscordWebhookClient.MessageBody.Embed;
import com.github.wshimaya.oursongmonitor.adapter.discord.DiscordWebhookClient.MessageBody.Embed.Field;
import com.github.wshimaya.oursongmonitor.adapter.discord.DiscordWebhookClient.MessageBody.Embed.Thumbnail;
import com.github.wshimaya.oursongmonitor.domain.PlaylistHistory;
import com.github.wshimaya.oursongmonitor.domain.PlaylistItem;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
   * @return message body
   */
  public MessageBody build(final PlaylistHistory history) {
    var changes = history.getChanges();
    var addedItems = changes.getAddedItems();
    var removedItems = changes.getRemovedItems();

    int numDisplayAdded = addedItems.size();
    int numDisplayRemoved = removedItems.size();
    if (numDisplayAdded + numDisplayRemoved >= MAX_EMBED_NUM) {
      numDisplayRemoved = Integer.max(MAX_EMBED_NUM - numDisplayAdded, 3);
      numDisplayAdded = MAX_EMBED_NUM - numDisplayRemoved;
    }

    List<Embed> embeds = buildAddedMessages(addedItems.stream().limit(numDisplayAdded).toList());
    embeds.addAll(buildRemovedMessages(removedItems).stream().limit(numDisplayRemoved).toList());

    if (history.getPreviousList().getItems().isEmpty()) {
      return MessageBody.builder()
          .content("Start watching playlist!")
          .embeds(embeds)
          .build();
    }

    return MessageBody.builder()
        .embeds(embeds)
        .build();
  }

  private List<Embed> buildAddedMessages(final List<PlaylistItem> items) {
    return buildEmbeds(items, ":musical_note: %s", "%d件の歌がみんなのうたに追加されました！ :+1:");
  }

  private List<Embed> buildRemovedMessages(final List<PlaylistItem> items) {
    return buildEmbeds(items, ":wave: %s", "%d件の歌がみんなのうたから消えました :sob:");
  }

  private List<Embed> buildEmbeds(final List<PlaylistItem> items,
      final String titleFormat,
      final String descriptionFormat) {
    return items.stream().map(item -> {
      var builder = Embed.builder()
          .title(String.format(titleFormat, item.getTitle()))
          .description(String.format(descriptionFormat, items.size()))
          .url(YOUTUBE_URL_PREFIX + item.getVideoId())
          .fields(List.of(
              Field.builder()
                  .name("Channel")
                  .value(Optional.ofNullable(item.getChannelTitle())
                      .orElse("<UNKNOWN>"))
                  .inline(true)
                  .build()
          ));
      builder.thumbnail(Optional.of(item)
          .map(PlaylistItem::getThumbnailUrl)
          .map(url -> Thumbnail.builder().url(url).build())
          .orElse(null));
      return builder.build();
    }).collect(Collectors.toList());
  }
}
