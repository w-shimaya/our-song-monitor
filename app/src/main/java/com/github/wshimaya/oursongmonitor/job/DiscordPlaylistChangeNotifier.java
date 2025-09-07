package com.github.wshimaya.oursongmonitor.job;

import com.github.wshimaya.oursongmonitor.adapter.discord.DiscordWebhookClient;
import com.github.wshimaya.oursongmonitor.adapter.discord.DiscordWebhookClient.MessageBody;
import com.github.wshimaya.oursongmonitor.domain.PlaylistHistory;
import com.github.wshimaya.oursongmonitor.domain.PlaylistItem;
import com.github.wshimaya.oursongmonitor.domain.PlaylistItemList;
import io.micrometer.common.lang.NonNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Notify playlist changes.
 */
@AllArgsConstructor
@Component
@Slf4j
public class DiscordPlaylistChangeNotifier implements PlaylistChangeNotifier {

  /**
   * Discord client.
   */
  private final DiscordWebhookClient discordWebhookClient;

  /**
   * Discord message builder.
   */
  private final DiscordMessageBuilder messageBuilder;

  /**
   * {@inheritDoc}
   */
  @Override
  public PlaylistItemList process(@NonNull final PlaylistHistory history) {
    var changes = history.getChanges();
    List<PlaylistItem> addedItems = changes.getAddedItems();
    List<PlaylistItem> removedItems = changes.getRemovedItems();
    log.info("{} items added", addedItems.size());
    log.info("{} items removed", removedItems.size());

    // send message via discord webhook
    MessageBody body = messageBuilder.build(history);

    if (StringUtils.hasText(body.getContent()) || !CollectionUtils.isEmpty(body.getEmbeds())) {
      discordWebhookClient.sendMessage(body);
    }

    return history.getCurrentList();
  }
}
