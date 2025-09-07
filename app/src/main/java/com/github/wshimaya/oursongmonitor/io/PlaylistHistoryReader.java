package com.github.wshimaya.oursongmonitor.io;

import com.github.wshimaya.oursongmonitor.domain.PlaylistHistory;
import org.springframework.batch.item.ItemReader;

/**
 * Playlist history reader.
 */
public interface PlaylistHistoryReader extends ItemReader<PlaylistHistory> {

}
