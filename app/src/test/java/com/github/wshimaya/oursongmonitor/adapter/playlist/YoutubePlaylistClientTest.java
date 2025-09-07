package com.github.wshimaya.oursongmonitor.adapter.playlist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.wshimaya.oursongmonitor.adapter.playlist.client.YoutubePlaylistClient;
import com.github.wshimaya.oursongmonitor.adapter.playlist.mapper.YoutubePlaylistItemMapper;
import com.github.wshimaya.oursongmonitor.domain.PlaylistItem;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.PlaylistItems;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class YoutubePlaylistClientTest {

  @Mock
  YouTube youtube;

  @Mock
  PlaylistItems playlistItems;

  @Mock
  PlaylistItems.List list;

  @Mock
  YoutubePlaylistItemMapper mapper;

  PlaylistItemListResponse response1, response2;

  com.google.api.services.youtube.model.PlaylistItem item1, item2;

  @BeforeEach
  void setup() {
    response1 = new PlaylistItemListResponse();
    response2 = new PlaylistItemListResponse();
    item1 = new com.google.api.services.youtube.model.PlaylistItem();
    item2 = new com.google.api.services.youtube.model.PlaylistItem();
  }

  @Test
  void fetchAllPlaylistItemsTest() throws Exception {
    when(youtube.playlistItems()).thenReturn(playlistItems);
    when(playlistItems.list(any())).thenReturn(list);
    when(list.setPlaylistId(any())).thenReturn(list);
    when(list.setKey(any())).thenReturn(list);
    response1.setItems(List.of(item1, item2));
    when(list.execute()).thenReturn(response1);
    PlaylistItem expected1 = PlaylistItem.builder()
        .title("title1")
        .videoId("id1")
        .channelTitle("ch1")
        .thumbnailUrl("https://thumbnail/1.jpg")
        .build();
    PlaylistItem expected2 = PlaylistItem.builder()
        .title("title2")
        .videoId("id2")
        .channelTitle("ch2")
        .thumbnailUrl("https://thumbnail/2.jpg")
        .build();

    var target = new YoutubePlaylistClient(youtube, "api-key", "playlist-id", mapper);
    var actual = target.fetchAllPlaylistItems();

    assertThat(actual.getItems()).containsExactly(expected1, expected2);
  }

//  @Test
//  void fetchAllPlaylistItems_paging() throws Exception {
//    when(youtube.playlistItems()).thenReturn(playlistItems);
//    when(playlistItems.list(any())).thenReturn(list);
//    when(list.setPlaylistId(any())).thenReturn(list);
//    when(list.setKey(any())).thenReturn(list);
//    when(list.setPageToken(any())).thenReturn(list);
//    snippet1.setTitle("title1");
//    item1.setSnippet(snippet1);
//    response1.setItems(List.of(item1));
//    response1.setNextPageToken("token");
//    snippet2.setTitle("title2");
//    item2.setSnippet(snippet2);
//    response2.setItems(List.of(item2));
//    response2.setNextPageToken(null);
//    when(list.execute()).thenReturn(response1, response2);
//    PlaylistItem expected1 = new PlaylistItem(
//        new PlaylistItemSnippet("title1", null, null, null));
//    PlaylistItem expected2 = new PlaylistItem(
//        new PlaylistItemSnippet("title2", null, null, null));
//
//    var target = new YoutubePlaylistClient(youtube, "api-key", "playlist-id");
//    var actual = target.fetchAllPlaylistItems();
//
//    assertThat(actual).containsExactly(expected1, expected2);
//    verify(list, times(2)).execute();
//  }
}
