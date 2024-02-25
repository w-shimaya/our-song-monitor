package com.github.wshimaya.oursongmonitor.io;

import com.github.wshimaya.oursongmonitor.client.S3PlaylistClient;
import com.github.wshimaya.oursongmonitor.model.PlaylistItem;
import io.micrometer.common.lang.NonNull;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Component;

/**
 * Cache current state of playlists in S3.
 */
@AllArgsConstructor
@Component
public class S3PlaylistCacheWriter implements PlaylistWriter {

  /**
   * Cache destination.
   */
  private final S3PlaylistClient s3Client;

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(@NonNull final Chunk<? extends List<PlaylistItem>> chunk) {
    for (List<PlaylistItem> list : chunk) {
      s3Client.putPlaylist(list);
    }
  }
}
