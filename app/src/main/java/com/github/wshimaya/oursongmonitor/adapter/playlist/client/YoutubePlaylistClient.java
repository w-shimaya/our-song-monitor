package com.github.wshimaya.oursongmonitor.adapter.playlist.client;

import static java.util.Objects.nonNull;

import com.github.wshimaya.oursongmonitor.adapter.DomainMapper;
import com.github.wshimaya.oursongmonitor.adapter.playlist.mapper.YoutubePlaylistItemMapper;
import com.github.wshimaya.oursongmonitor.domain.PlaylistItem;
import com.github.wshimaya.oursongmonitor.domain.PlaylistItemList;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.PlaylistItems;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import io.micrometer.common.lang.NonNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;

/**
 * Client of YouTube Data API.
 */
@AllArgsConstructor
public class YoutubePlaylistClient {

  /**
   * YouTube API.
   */
  private final YouTube youtube;

  /**
   * API key.
   */
  private final String apiKey;

  private final String playlistId;

  /**
   * Mapper.
   */
  private final YoutubePlaylistItemMapper mapper;

  /**
   * Fetch all items in a playlist. Send some requests if necessary.
   *
   * @return List of playlist items
   */
  @Nullable
  public PlaylistItemList fetchAllPlaylistItems() throws IOException {
    List<PlaylistItem> playlistItems = new ArrayList<>();
    String nextPageToken = null;
    do {
      PlaylistItems.List list = youtube.playlistItems()
          .list(List.of("snippet"))
          .setPlaylistId(playlistId)
          .setKey(apiKey);
      if (nonNull(nextPageToken)) {
        list = list.setPageToken(nextPageToken);
      }
      PlaylistItemListResponse res = list.execute();
      List<PlaylistItem> items = res.getItems().stream()
          .map(mapper::map)
          .collect(Collectors.toList());
      playlistItems.addAll(items);
      nextPageToken = res.getNextPageToken();
    } while (nonNull(nextPageToken));

    return new PlaylistItemList(playlistItems);
  }
}
