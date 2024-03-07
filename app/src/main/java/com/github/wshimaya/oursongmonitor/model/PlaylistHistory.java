package com.github.wshimaya.oursongmonitor.model;

import static java.util.Objects.isNull;

import java.util.List;
import java.util.stream.Collectors;
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
  private List<PlaylistItem> currentList;

  /**
   * Previous playlist info.
   */
  private List<PlaylistItem> previousList;

  /**
   * Enumerate newly added playlist items.
   *
   * @return items that exists only in current list
   */
  public List<PlaylistItem> enumerateAddedItems() {
    if (isNull(currentList)) {
      return null;
    }

    return currentList.stream()
        .filter(item -> !previousList.stream()
            .map(PlaylistItem::getSnippet)
            .map(PlaylistItemSnippet::getResourceId)
            .map(ResourceId::getVideoId)
            .collect(Collectors.toList())
            .contains(item.getSnippet().getResourceId().getVideoId()))
        .collect(Collectors.toList());
  }

  /**
   * Enumerate removed items.
   *
   * @return items that exists only in previous list
   */
  public List<PlaylistItem> enumerateRemovedItems() {
    if (isNull(previousList)) {
      return null;
    }

    return previousList.stream()
        .filter(item -> !currentList.stream()
            .map(PlaylistItem::getSnippet)
            .map(PlaylistItemSnippet::getResourceId)
            .map(ResourceId::getVideoId)
            .collect(Collectors.toList())
            .contains(item.getSnippet().getResourceId().getVideoId()))
        .collect(Collectors.toList());
  }
}
