package com.github.wshimaya.oursongmonitor.client;

import static java.util.Objects.isNull;

import com.github.wshimaya.oursongmonitor.model.PlaylistItem;
import com.github.wshimaya.oursongmonitor.model.PlaylistItemList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class S3PlaylistClient {

  /**
   * S3 client.
   */
  private final S3Client s3Client;

  /**
   * Directory (prefix) to store playlist info.
   */
  private final String s3Directory;

  /**
   * Bucket to store playlist info.
   */
  private final String bucketName;

  /**
   * Target playlist ID.
   */
  private final String playlistId;

  /**
   * Find playlist info cached in previous run.
   *
   * @return playlist info
   */
  @Nullable
  public PlaylistItemList findPreviousPlaylist() {
    var latestResult = s3Client.findNewestObjectSummary(
        bucketName, s3Directory, playlistId + ".json");

    if (isNull(latestResult)) {
      return null;
    }

    return s3Client.getObject(bucketName, latestResult.getKey(), PlaylistItemList.class);
  }

  /**
   * Put playlist as cache.
   *
   * @param playlistItems playlist
   */
  public void putPlaylist(List<PlaylistItem> playlistItems) {
    var key = s3Directory
        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        + "-"
        + playlistId
        + ".json";
    s3Client.putJsonObject(bucketName, key, new PlaylistItemList(playlistItems));
  }
}
