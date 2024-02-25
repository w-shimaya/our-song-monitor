package com.github.wshimaya.oursongmonitor.io;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.github.wshimaya.oursongmonitor.client.S3PlaylistClient;
import com.github.wshimaya.oursongmonitor.client.YoutubePlaylistClient;
import com.github.wshimaya.oursongmonitor.model.PlaylistItem;
import com.github.wshimaya.oursongmonitor.model.PlaylistItemList;
import com.github.wshimaya.oursongmonitor.model.PlaylistItemSnippet;
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
  void read() {
    when(s3PlaylistClient.findPreviousPlaylist()).thenReturn(new PlaylistItemList(List.of()));
    when(youtubePlaylistClient.fetchAllPlaylistItems()).thenReturn(List.of(
        new PlaylistItem(new PlaylistItemSnippet())));

    var target = new ApiPlaylistHistoryReader(s3PlaylistClient, youtubePlaylistClient);

    var actual = target.read();

    assertThat(actual).isNotNull();
    assertThat(actual.getPreviousList()).isEmpty();
    assertThat(actual.getCurrentList()).isNotEmpty();

    assertThat(target.read()).isNull();
  }

  @Test
  void read_no_cache() {
    when(s3PlaylistClient.findPreviousPlaylist()).thenReturn(null);
    when(youtubePlaylistClient.fetchAllPlaylistItems()).thenReturn(
        List.of(new PlaylistItem()));

    var target = new ApiPlaylistHistoryReader(s3PlaylistClient, youtubePlaylistClient);
    var actual = target.read();

    assertThat(actual).isNotNull();
    assertThat(actual.getPreviousList()).isEmpty();
    assertThat(actual.getCurrentList()).isNotEmpty();
  }

  @Test
  void read_failed_to_fetch_current_list() {
    when(youtubePlaylistClient.fetchAllPlaylistItems()).thenReturn(null);

    var target = new ApiPlaylistHistoryReader(s3PlaylistClient, youtubePlaylistClient);
    var actual = target.read();

    assertThat(actual).isNull();
  }
}