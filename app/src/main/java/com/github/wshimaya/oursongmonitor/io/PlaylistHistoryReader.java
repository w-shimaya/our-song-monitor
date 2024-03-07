package com.github.wshimaya.oursongmonitor.io;

import com.github.wshimaya.oursongmonitor.model.PlaylistHistory;
import org.springframework.batch.item.ItemReader;

/**
 * Playlist history reader.
 */
public interface PlaylistHistoryReader extends ItemReader<PlaylistHistory> {

}
