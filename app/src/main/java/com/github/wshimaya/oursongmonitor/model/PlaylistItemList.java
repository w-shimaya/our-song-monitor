package com.github.wshimaya.oursongmonitor.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * List of playlist items to store.
 */
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistItemList extends GenericJson {

  /**
   * Items.
   */
  @Key
  @Getter
  @Setter
  private List<PlaylistItem> items;

}
