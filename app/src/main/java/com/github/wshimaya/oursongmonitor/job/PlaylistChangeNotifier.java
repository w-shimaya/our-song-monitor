package com.github.wshimaya.oursongmonitor.job;

import com.github.wshimaya.oursongmonitor.client.DiscordWebhookClient;
import com.github.wshimaya.oursongmonitor.client.DiscordWebhookClient.MessageBody;
import com.github.wshimaya.oursongmonitor.model.PlaylistHistory;
import com.github.wshimaya.oursongmonitor.model.PlaylistItem;
import io.micrometer.common.lang.NonNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


@AllArgsConstructor
@Component
@Slf4j
public class PlaylistChangeNotifier implements ItemProcessor<PlaylistHistory, List<PlaylistItem>> {

  private final DiscordWebhookClient discordWebhookClient;

  private final DiscordMessageBuilder messageBuilder;

  @Override
  public List<PlaylistItem> process(@NonNull final PlaylistHistory item) {
    List<PlaylistItem> addedItems = item.enumerateAddedItems();
    List<PlaylistItem> removedItems = item.enumerateRemovedItems();
    log.debug("{} items added", addedItems.size());
    log.debug("{} items removed", removedItems.size());

    // send message via discord webhook
    MessageBody body = messageBuilder.build(addedItems, removedItems);

    if (StringUtils.hasText(body.getContent()) || !CollectionUtils.isEmpty(body.getEmbeds())) {
      discordWebhookClient.sendMessage(body);
    }

    return item.getCurrentList();
  }
}
