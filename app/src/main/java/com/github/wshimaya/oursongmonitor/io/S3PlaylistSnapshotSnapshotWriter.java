package com.github.wshimaya.oursongmonitor.io;

import com.github.wshimaya.oursongmonitor.adapter.playlist.client.S3PlaylistClient;
import com.github.wshimaya.oursongmonitor.domain.PlaylistItemList;
import io.micrometer.common.lang.NonNull;
import lombok.AllArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Component;

/**
 * Cache current state of playlists in S3.
 */
@AllArgsConstructor
@Component
public class S3PlaylistSnapshotSnapshotWriter implements PlaylistSnapshotWriter {

  /**
   * Cache destination.
   */
  private final S3PlaylistClient s3Client;

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(@NonNull final Chunk<? extends PlaylistItemList> chunk) throws Exception {
    for (PlaylistItemList list : chunk) {
      s3Client.putPlaylist(list);
    }
  }
}
