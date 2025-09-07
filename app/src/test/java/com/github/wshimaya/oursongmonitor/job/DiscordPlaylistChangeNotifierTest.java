package com.github.wshimaya.oursongmonitor.job;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.wshimaya.oursongmonitor.adapter.discord.DiscordWebhookClient;
import com.github.wshimaya.oursongmonitor.adapter.discord.DiscordWebhookClient.MessageBody;
import com.github.wshimaya.oursongmonitor.adapter.discord.DiscordWebhookClient.MessageBody.Embed;
import com.github.wshimaya.oursongmonitor.domain.PlaylistHistory;
import com.github.wshimaya.oursongmonitor.domain.PlaylistItem;
import com.github.wshimaya.oursongmonitor.domain.PlaylistItemList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DiscordPlaylistChangeNotifierTest {

  @Mock
  DiscordWebhookClient discordWebhookClient;

  @Mock
  DiscordMessageBuilder messageBuilder;

  @Test
  void process() {
    var added = PlaylistItem.builder().videoId("added").build();
    var removed = PlaylistItem.builder().videoId("removed").build();
    List<PlaylistItem> current = List.of(added);
    List<PlaylistItem> previous = List.of(removed);
    when(messageBuilder.build(new PlaylistHistory(
        new PlaylistItemList(current), new PlaylistItemList(previous))))
        .thenReturn(MessageBody.builder()
            .embeds(List.of(Embed.builder().build()))
            .build());
    doNothing().when(discordWebhookClient).sendMessage(any());

    DiscordPlaylistChangeNotifier target = new DiscordPlaylistChangeNotifier(discordWebhookClient,
        messageBuilder);
    PlaylistItemList actual = target.process(
        new PlaylistHistory(new PlaylistItemList(current), new PlaylistItemList(previous)));

    assertThat(actual.getItems()).isEqualTo(current);
    verify(discordWebhookClient, times(1)).sendMessage(any());
  }
}