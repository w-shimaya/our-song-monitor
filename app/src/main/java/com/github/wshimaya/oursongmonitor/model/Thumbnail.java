package com.github.wshimaya.oursongmonitor.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data model of Thumbnail of a YouTube video.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Thumbnail extends GenericJson {

  /**
   * URL.
   */
  @Key
  private String url;
}