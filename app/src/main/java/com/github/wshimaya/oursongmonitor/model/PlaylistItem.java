package com.github.wshimaya.oursongmonitor.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Data model of PlaylistItem.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
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
    if (fieldName.equals("snippet") && value instanceof GenericJson valueJson) {
      snippet = new PlaylistItemSnippet();
      valueJson.forEach(snippet::set);
    } else {
      super.set(fieldName, value);
    }
    return this;
  }
}
