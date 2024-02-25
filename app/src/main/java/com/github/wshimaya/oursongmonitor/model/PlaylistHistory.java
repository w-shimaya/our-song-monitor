package com.github.wshimaya.oursongmonitor.model;

import static java.util.Objects.isNull;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PlaylistHistory {

  private List<PlaylistItem> currentList;

  private List<PlaylistItem> previousList;

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
