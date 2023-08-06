package com.github.wshimaya.oursongmonitor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.PlaylistItems;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class PlaylistItemsClientTest {

  @Mock
  YouTube youtube;

  @Mock
  PlaylistItems playlistItems;

  @Mock
  PlaylistItems.List list;

  @Test
  void fetchAllPlaylistItemsTest() throws Exception {
    when(youtube.playlistItems()).thenReturn(playlistItems);
    when(playlistItems.list(any())).thenReturn(list);

    var target = new PlaylistItemsClient(youtube, "api-key");
    var actual = target.fetchAllPlaylistItems("playlist-id");
  }
}
