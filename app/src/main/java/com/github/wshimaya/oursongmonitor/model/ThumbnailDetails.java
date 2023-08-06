package com.github.wshimaya.oursongmonitor.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Detailed information of Thumbnail of a YouTube video.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ThumbnailDetails extends GenericJson {

  /**
   * Default thumbnail.
   */
  @Key("default")
  private Thumbnail def;

  /**
   * {@inheritDoc}
   */
  @Override
  public GenericJson set(String fieldName, Object value) {
    if (!(value instanceof GenericJson valueJson)) {
      return super.set(fieldName, value);
    }

    if (fieldName.equals("default")) {
      def = new Thumbnail();
      valueJson.forEach(def::set);
    } else {
      super.set(fieldName, value);
    }

    return this;
  }
}
