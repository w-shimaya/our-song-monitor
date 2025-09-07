package com.github.wshimaya.oursongmonitor.io;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.github.wshimaya.oursongmonitor.adapter.playlist.client.S3PlaylistClient;
import com.github.wshimaya.oursongmonitor.adapter.playlist.client.YoutubePlaylistClient;
import com.github.wshimaya.oursongmonitor.domain.PlaylistItem;
import com.github.wshimaya.oursongmonitor.domain.PlaylistItemList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApiPlaylistHistoryReaderTest {

  @Mock
  S3PlaylistClient s3PlaylistClient;

  @Mock
  YoutubePlaylistClient youtubePlaylistClient;

  @Test
  void read() throws Exception {
    when(s3PlaylistClient.fetchPlaylistItems()).thenReturn(new PlaylistItemList(List.of()));
    when(youtubePlaylistClient.fetchAllPlaylistItems()).thenReturn(new PlaylistItemList(List.of(
        PlaylistItem.builder()
            .title("title")
            .videoId("id")
            .channelTitle("ch")
            .thumbnailUrl("url")
            .build())));

    var target = new ApiPlaylistHistoryReader(s3PlaylistClient, youtubePlaylistClient);

    var actual = target.read();

    assertThat(actual).isNotNull();
    assertThat(actual.getPreviousList().getItems()).isEmpty();
    assertThat(actual.getCurrentList().getItems()).isNotEmpty();

    assertThat(target.read()).isNull();
  }

  @Test
  void read_no_cache() throws Exception {
    when(s3PlaylistClient.fetchPlaylistItems()).thenReturn(null);
    when(youtubePlaylistClient.fetchAllPlaylistItems()).thenReturn(new PlaylistItemList(
        List.of(PlaylistItem.builder()
            .build())));

    var target = new ApiPlaylistHistoryReader(s3PlaylistClient, youtubePlaylistClient);
    var actual = target.read();

    assertThat(actual).isNotNull();
    assertThat(actual.getPreviousList().getItems()).isEmpty();
    assertThat(actual.getCurrentList().getItems()).isNotEmpty();
  }

  @Test
  void read_failed_to_fetch_current_list() throws Exception {
    when(youtubePlaylistClient.fetchAllPlaylistItems()).thenReturn(null);

    var target = new ApiPlaylistHistoryReader(s3PlaylistClient, youtubePlaylistClient);
    var actual = target.read();

    assertThat(actual).isNull();
  }
}