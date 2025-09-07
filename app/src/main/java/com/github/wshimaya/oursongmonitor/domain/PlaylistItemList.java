package com.github.wshimaya.oursongmonitor.domain;

import static java.util.Objects.isNull;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * List of playlist items to store.
 */
@AllArgsConstructor
@Data
public class PlaylistItemList {

  /**
   * Items.
   */
  private List<PlaylistItem> items;

  public PlaylistChange changesFrom(PlaylistItemList src) {
    return new PlaylistChange(findAddedItems(src), findRemovedItems(src));
  }

  private List<PlaylistItem> findAddedItems(PlaylistItemList src) {
    if (isNull(items)) {
      return null;
    }

    return items.stream()
        .filter(item -> !src.getItems().stream()
            .map(PlaylistItem::getVideoId)
            .collect(Collectors.toList())
            .contains(item.getVideoId()))
        .collect(Collectors.toList());
  }

  private List<PlaylistItem> findRemovedItems(PlaylistItemList src) {
    if (isNull(src.getItems())) {
      return null;
    }

    return src.getItems().stream()
        .filter(item -> !items.stream()
            .map(PlaylistItem::getVideoId)
            .collect(Collectors.toList())
            .contains(item.getVideoId()))
        .collect(Collectors.toList());
  }
}
