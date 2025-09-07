package com.github.wshimaya.oursongmonitor.adapter.playlist.mapper;

import com.github.wshimaya.oursongmonitor.adapter.DomainMapper;
import com.github.wshimaya.oursongmonitor.domain.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.ThumbnailDetails;
import io.micrometer.common.lang.NonNull;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class YoutubePlaylistItemMapper implements
    DomainMapper<com.google.api.services.youtube.model.PlaylistItem,
        PlaylistItem> {

  @Override
  public PlaylistItem map(@NonNull final com.google.api.services.youtube.model.PlaylistItem source) {
    return PlaylistItem.builder()
        .title(source.getSnippet().getTitle())
        .channelTitle(source.getSnippet().getVideoOwnerChannelTitle())
        .videoId(source.getSnippet().getResourceId().getVideoId())
        .thumbnailUrl(Optional.of(source)
            .map(com.google.api.services.youtube.model.PlaylistItem::getSnippet)
            .map(PlaylistItemSnippet::getThumbnails)
            .map(ThumbnailDetails::getDefault)
            .map(Thumbnail::getUrl)
            .orElse(null))
        .build();
  }
}
