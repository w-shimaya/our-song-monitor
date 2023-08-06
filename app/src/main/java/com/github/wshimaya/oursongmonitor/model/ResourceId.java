package com.github.wshimaya.oursongmonitor.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data model of Resource ID.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ResourceId extends GenericJson {

  /**
   * Video ID.
   */
  @Key
  private String videoId;
}