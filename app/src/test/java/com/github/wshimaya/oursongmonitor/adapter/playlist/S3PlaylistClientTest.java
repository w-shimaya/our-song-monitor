package com.github.wshimaya.oursongmonitor.adapter.playlist;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.wshimaya.oursongmonitor.adapter.S3Ops;
import com.github.wshimaya.oursongmonitor.adapter.playlist.client.S3PlaylistClient;
import com.github.wshimaya.oursongmonitor.domain.PlaylistItemList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.model.S3Object;

@ExtendWith(MockitoExtension.class)
class S3PlaylistClientTest {

  @Mock
  S3Ops s3Ops;

  @Test
  void fetchPlaylistItems() throws Throwable {
    S3Object summary = S3Object.builder()
        .key("newest-playlist.json")
        .build();
    when(s3Ops.findNewestObject("bucket", "directory", "playlist.json")).thenReturn(
        summary);
    when(s3Ops.getObject("bucket", "newest-playlist.json", PlaylistItemList.class)).thenReturn(
        new PlaylistItemList(List.of()));
    S3PlaylistClient target = new S3PlaylistClient(
        s3Ops, "directory", "bucket", "playlist");

    PlaylistItemList actual = target.fetchPlaylistItems();

    assertThat(actual).isNotNull();
    verify(s3Ops, times(1)).findNewestObject("bucket", "directory", "playlist.json");
    verify(s3Ops, times(1)).getObject("bucket", "newest-playlist.json", PlaylistItemList.class);
  }
//
//  @Test
//  void findPreviousPlaylist_null() {
//    when(s3Ops.findNewestObjectSummary(any(), any(), any())).thenReturn(null);
//
//    S3PlaylistClient target = new S3PlaylistClient(s3Ops, "directory", "bucket", "playlist");
//    PlaylistItemList actual = target.findPreviousPlaylist();
//
//    assertThat(actual).isNull();
//    verify(s3Ops, times(1)).findNewestObjectSummary("bucket", "directory", "playlist.json");
//    verify(s3Ops, never()).getObject(any(), any(), any());
//  }
//
//  @Test
//  void putPlaylist() throws Exception {
//    doNothing().when(s3Ops).putJsonObject(any(), any(), any());
//
//    S3PlaylistClient target = new S3PlaylistClient(s3Ops, "directory/", "bucket", "playlist");
//    target.putPlaylist(List.of());
//
//    verify(s3Ops, times(1)).putJsonObject(eq("bucket"), matches("directory/\\d+-playlist.json"),
//        any());
//  }
//
//  @Test
//  void putPlaylist_AmazonServiceException() throws Exception {
//    doThrow(AmazonServiceException.class).when(s3Ops).putJsonObject(any(), any(), any());
//
//    S3PlaylistClient target = new S3PlaylistClient(s3Ops, "directory/", "bucket", "playlist");
//
//    assertThatThrownBy(() -> target.putPlaylist(List.of()))
//        .isInstanceOf(AmazonServiceException.class);
//  }
//
//  @Test
//  void putPlaylist_IOException() throws Exception {
//    doThrow(IOException.class).when(s3Ops).putJsonObject(any(), any(), any());
//
//    S3PlaylistClient target = new S3PlaylistClient(s3Ops, "directory/", "bucket", "playlist");
//    assertThatThrownBy(() -> target.putPlaylist(List.of()))
//        .isInstanceOf(IOException.class);
//  }
}