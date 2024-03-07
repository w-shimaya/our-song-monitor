package com.github.wshimaya.oursongmonitor.client;

import static java.util.Objects.nonNull;

import com.github.wshimaya.oursongmonitor.model.PlaylistItem;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.PlaylistItems;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import io.micrometer.common.lang.NonNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

/**
 * Client of YouTube Data API.
 */
public class YoutubePlaylistClient {

  /**
   * YouTube API.
   */
  private final YouTube youtube;

  /**
   * API key.
   */
  private final String apiKey;

  /**
   * Target Playlist ID.
   */
  private final String playlistId;

  /**
   * Constructor.
   *
   * @param youtube    Youtube API client
   * @param apiKey     API key
   * @param playlistId Playlist ID
   */
  public YoutubePlaylistClient(@NonNull final YouTube youtube,
      @NonNull final String apiKey,
      @NonNull final String playlistId) {
    this.youtube = youtube;
    this.apiKey = apiKey;
    this.playlistId = playlistId;
  }

  /**
   * Fetch all items in a playlist. Send some requests if necessary.
   *
   * @return List of playlist items
   */
  @Nullable
  public List<PlaylistItem> fetchAllPlaylistItems() throws IOException {
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
      var items = res.getItems().stream()
          .map(YoutubePlaylistClient::extractFields)
          .collect(Collectors.toList());
      playlistItems.addAll(items);
      nextPageToken = res.getNextPageToken();
    } while (nonNull(nextPageToken));

    return playlistItems;
  }

  /**
   * Extract necessary fields from YouTube response.
   *
   * @param item item represented in Youtube data model
   * @return Extracted {@link PlaylistItem}
   */
  private static PlaylistItem extractFields(
      final com.google.api.services.youtube.model.PlaylistItem item) {
    var ret = new PlaylistItem();
    item.forEach(ret::set);
    return ret;
  }

}
