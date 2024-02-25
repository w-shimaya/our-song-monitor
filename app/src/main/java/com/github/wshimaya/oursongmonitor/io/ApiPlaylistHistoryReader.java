package com.github.wshimaya.oursongmonitor.io;

import static java.util.Objects.isNull;

import com.github.wshimaya.oursongmonitor.client.S3PlaylistClient;
import com.github.wshimaya.oursongmonitor.client.YoutubePlaylistClient;
import com.github.wshimaya.oursongmonitor.model.PlaylistHistory;
import com.github.wshimaya.oursongmonitor.model.PlaylistItemList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Fetch playlist info from YouTube API and cache in S3.
 */
@Component
@RequiredArgsConstructor
public class ApiPlaylistHistoryReader implements PlaylistHistoryReader {

  /**
   * Process finished or not.
   */
  private boolean isFinished = false;

  /**
   * S3 client.
   */
  private final S3PlaylistClient s3PlaylistClient;

  /**
   * Youtube API client.
   */
  private final YoutubePlaylistClient youtubePlaylistClient;

  /**
   * {@inheritDoc}
   */
  @Override
  public PlaylistHistory read() throws Exception {
    if (isFinished) {
      return null;
    }

    // fetch all items in the playlist
    var currentPlaylistItems = youtubePlaylistClient.fetchAllPlaylistItems();
    if (isNull(currentPlaylistItems)) {
      return null;
    }

    // get playlist items stored in the previous run
    var previousPlaylistItems = Optional.ofNullable(s3PlaylistClient.findPreviousPlaylist())
        .map(PlaylistItemList::getItems)
        .orElse(List.of());

    isFinished = true;

    return new PlaylistHistory(currentPlaylistItems, previousPlaylistItems);
  }
}
