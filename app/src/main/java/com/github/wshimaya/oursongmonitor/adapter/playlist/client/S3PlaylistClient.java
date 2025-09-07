package com.github.wshimaya.oursongmonitor.adapter.playlist.client;

import static java.util.Objects.isNull;

import com.github.wshimaya.oursongmonitor.adapter.S3Ops;
import com.github.wshimaya.oursongmonitor.domain.PlaylistItemList;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;


/**
 * S3 client to download/upload playlist information.
 */
@AllArgsConstructor
public class S3PlaylistClient {

  /**
   * S3 client.
   */
  private final S3Ops s3Ops;

  /**
   * Directory (prefix) to store playlist info.
   */
  private final String s3Directory;

  /**
   * Bucket to store playlist info.
   */
  private final String bucketName;

  /**
   * Playlist ID.
   */
  private final String playlistId;

  /**
   * Find playlist info cached in previous run.
   *
   * @return playlist info
   */
  @Nullable
  public PlaylistItemList fetchPlaylistItems() throws IOException {
    var latestResult = s3Ops.findNewestObject(
        bucketName, s3Directory, playlistId + ".json");

    if (isNull(latestResult)) {
      return null;
    }

    return s3Ops.getObject(bucketName, latestResult.key(), PlaylistItemList.class);
  }

  /**
   * Put playlist as cache.
   *
   * @param playlistItems playlist
   */
  public void putPlaylist(PlaylistItemList playlistItems)
      throws IOException {
    var key = s3Directory
        + "/"
        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        + "-"
        + playlistId
        + ".json";
    s3Ops.putJsonObject(bucketName, key, playlistItems);
  }
}
