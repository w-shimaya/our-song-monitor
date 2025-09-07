package com.github.wshimaya.oursongmonitor.domain;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

/**
 * Data model of PlaylistItem.
 */
@Data
@Builder
public class PlaylistItem implements Serializable {

  private String title;

  private String videoId;

  private String channelTitle;

  private String thumbnailUrl;
}
