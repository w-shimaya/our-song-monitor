package com.github.wshimaya.oursongmonitor.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Data model of playlist item snippet.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistItemSnippet extends GenericJson {

  /**
   * Title of a video.
   */
  @Key
  private String title;

  /**
   * Resource ID of a playlist item.
   */
  @Key
  private ResourceId resourceId;

  /**
   * Thumbnails.
   */
  @Key
  private ThumbnailDetails thumbnails;

  /**
   * Channel title of owner of an item.
   */
  @Key
  private String videoOwnerChannelTitle;

  /**
   * {@inheritDoc}
   */
  @Override
  public GenericJson set(String fieldName, Object value) {
    if (!(value instanceof GenericJson valueJson)) {
      return super.set(fieldName, value);
    }

    if (fieldName.equals("resourceId")) {
      resourceId = new ResourceId();
      valueJson.forEach(resourceId::set);
    } else if (fieldName.equals("thumbnails")) {
      thumbnails = new ThumbnailDetails();
      valueJson.forEach(thumbnails::set);
    } else {
      super.set(fieldName, value);
    }
    return this;
  }
}
