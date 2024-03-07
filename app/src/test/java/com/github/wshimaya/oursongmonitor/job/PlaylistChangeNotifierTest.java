package com.github.wshimaya.oursongmonitor.job;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.wshimaya.oursongmonitor.client.DiscordWebhookClient;
import com.github.wshimaya.oursongmonitor.client.DiscordWebhookClient.MessageBody;
import com.github.wshimaya.oursongmonitor.client.DiscordWebhookClient.MessageBody.Embed;
import com.github.wshimaya.oursongmonitor.model.PlaylistHistory;
import com.github.wshimaya.oursongmonitor.model.PlaylistItem;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlaylistChangeNotifierTest {

  @Mock
  DiscordWebhookClient discordWebhookClient;

  @Mock
  DiscordMessageBuilder messageBuilder;

  @Mock
  PlaylistHistory history;

  @Test
  void process() {
    List<PlaylistItem> current = List.of(new PlaylistItem(), new PlaylistItem(),
        new PlaylistItem());
    List<PlaylistItem> added = List.of(new PlaylistItem());
    List<PlaylistItem> removed = List.of(new PlaylistItem(), new PlaylistItem());
    when(history.getCurrentList()).thenReturn(current);
    when(history.enumerateAddedItems()).thenReturn(added);
    when(history.enumerateRemovedItems()).thenReturn(removed);
    when(messageBuilder.build(added, removed)).thenReturn(MessageBody.builder()
        .embeds(List.of(Embed.builder().build()))
        .build());
    doNothing().when(discordWebhookClient).sendMessage(any());

    PlaylistChangeNotifier target = new PlaylistChangeNotifier(discordWebhookClient,
        messageBuilder);
    List<PlaylistItem> actual = target.process(history);

    assertThat(actual).isEqualTo(current);
    verify(history, times(1)).enumerateAddedItems();
    verify(history, times(1)).enumerateRemovedItems();
    verify(history, times(1)).getCurrentList();
    verify(discordWebhookClient, times(1)).sendMessage(any());
  }
}