package com.github.wshimaya.oursongmonitor.io;

import com.github.wshimaya.oursongmonitor.model.PlaylistItem;
import java.util.List;
import org.springframework.batch.item.ItemWriter;

public interface PlaylistWriter extends ItemWriter<List<PlaylistItem>> {

}
