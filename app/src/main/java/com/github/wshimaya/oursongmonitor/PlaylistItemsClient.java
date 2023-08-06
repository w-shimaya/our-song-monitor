package com.github.wshimaya.oursongmonitor;

import static java.util.Objects.nonNull;

import com.github.wshimaya.oursongmonitor.model.PlaylistItem;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Builder;
import com.google.api.services.youtube.YouTube.PlaylistItems;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

/**
 * Client of YouTube Data API.
 */
public class PlaylistItemsClient {

  /**
   * YouTube API.
   */
  private final YouTube youtube;

  /**
   * API key.
   */
  private final String apiKey;

  /**
   * Constructor.
   *
   * @param youtube Youtube API client
   * @param apiKey API key
   */
  public PlaylistItemsClient(final YouTube youtube, final String apiKey) {
    this.youtube = youtube;
    this.apiKey = apiKey;
  }

  /**
   * Fetch all items in a playlist. Send some requests if necessary.
   *
   * @param playlistId Playlist ID
   * @return List of playlist items
   */
  @Nullable
  public List<PlaylistItem> fetchAllPlaylistItems(final String playlistId) {
    List<com.github.wshimaya.oursongmonitor.model.PlaylistItem> playlistItems
        = new ArrayList<>();
    String nextPageToken = null;
    do {
      try {
        PlaylistItems.List list = youtube.playlistItems()
            .list(List.of("snippet"))
            .setPlaylistId(playlistId)
            .setKey(apiKey);
        if (nonNull(nextPageToken)) {
          list = list.setPageToken(nextPageToken);
        }
        PlaylistItemListResponse res = list.execute();
        var items = res.getItems().stream()
            .map(PlaylistItemsClient::extractFields)
            .collect(Collectors.toList());
        playlistItems.addAll(items);
        nextPageToken = res.getNextPageToken();
      } catch (IOException exception) {
        MonitorHandler.logger.log("Failed to fetch playlist items: %s\n"
            .formatted(exception.getMessage()));
        return null;
      }
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
