package com.github.wshimaya.oursongmonitor.io;

import com.github.wshimaya.oursongmonitor.domain.PlaylistItemList;
import org.springframework.batch.item.ItemWriter;

/**
 * Playlist writer.
 */
public interface PlaylistSnapshotWriter extends ItemWriter<PlaylistItemList> {

}
