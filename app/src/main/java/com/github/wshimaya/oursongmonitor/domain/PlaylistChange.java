package com.github.wshimaya.oursongmonitor.domain;

import java.util.List;
import lombok.Value;

@Value
public class PlaylistChange {

  List<PlaylistItem> addedItems;

  List<PlaylistItem> removedItems;

}
