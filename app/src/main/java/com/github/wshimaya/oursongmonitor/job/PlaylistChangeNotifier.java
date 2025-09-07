package com.github.wshimaya.oursongmonitor.job;

import com.github.wshimaya.oursongmonitor.domain.PlaylistHistory;
import com.github.wshimaya.oursongmonitor.domain.PlaylistItemList;
import org.springframework.batch.item.ItemProcessor;

public interface PlaylistChangeNotifier extends ItemProcessor<PlaylistHistory, PlaylistItemList> {

}
