package com.github.wshimaya.oursongmonitor.adapter.playlist.client;

import com.github.wshimaya.oursongmonitor.domain.PlaylistItem;
import java.util.List;

public interface PlaylistClient {

  List<PlaylistItem> fetchPlaylistItems();

}
