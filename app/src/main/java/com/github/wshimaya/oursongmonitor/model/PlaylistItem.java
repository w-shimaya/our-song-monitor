package com.github.wshimaya.oursongmonitor.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data model of PlaylistItem.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PlaylistItem extends GenericJson {

  /**
   * Snippet.
   */
  @Key
  private PlaylistItemSnippet snippet;

  /**
   * {@inheritDoc}
   */
  @Override
  public GenericJson set(String fieldName, Object value) {
    if (fieldName.equals("snippet") && value instanceof GenericJson) {
      snippet = new PlaylistItemSnippet();
      ((GenericJson) value).forEach(snippet::set);
    } else {
      super.set(fieldName, value);
    }
    return this;
  }
}
