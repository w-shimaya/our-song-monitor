package com.github.wshimaya.oursongmonitor.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Playlist history.
 */
@AllArgsConstructor
@Data
public class PlaylistHistory {

  /**
   * Current playlist info.
   */
  private PlaylistItemList currentList;

  /**
   * Previous playlist info.
   */
  private PlaylistItemList previousList;

  /**
   * Enumerate newly added playlist items.
   *
   * @return items that exists only in current list
   */
  public PlaylistChange getChanges() {
    return currentList.changesFrom(previousList);
  }
}
