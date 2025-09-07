package com.github.wshimaya.oursongmonitor.domain;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;

/**
 * List of playlist items to store.
 */
@Data
public class PlaylistItemList implements Serializable {

  @JsonCreator
  public PlaylistItemList(@JsonProperty("items") List<PlaylistItem> items) {
    this.items = items;
  }

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
            .toList()
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
            .toList()
            .contains(item.getVideoId()))
        .collect(Collectors.toList());
  }
}
