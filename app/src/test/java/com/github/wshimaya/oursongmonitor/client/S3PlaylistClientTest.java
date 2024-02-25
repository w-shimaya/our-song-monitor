package com.github.wshimaya.oursongmonitor.client;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.github.wshimaya.oursongmonitor.model.PlaylistItemList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class S3PlaylistClientTest {

  @Mock
  S3Client s3Client;

  @Test
  void findPreviousPlaylist() {
    S3ObjectSummary summary = new S3ObjectSummary();
    summary.setKey("newest-playlist.json");
    when(s3Client.findNewestObjectSummary("bucket", "directory", "playlist.json")).thenReturn(summary);
    when(s3Client.getObject("bucket", "newest-playlist.json", PlaylistItemList.class)).thenReturn(new PlaylistItemList());
    S3PlaylistClient target = new S3PlaylistClient(
        s3Client, "directory", "bucket", "playlist");

    PlaylistItemList actual = target.findPreviousPlaylist();

    assertThat(actual).isNotNull();
    verify(s3Client, times(1)).findNewestObjectSummary("bucket", "directory", "playlist.json");
    verify(s3Client, times(1)).getObject("bucket", "newest-playlist.json", PlaylistItemList.class);
  }

  @Test
  void findPreviousPlaylist_null() {
    when(s3Client.findNewestObjectSummary(any(), any(), any())).thenReturn(null);

    S3PlaylistClient target = new S3PlaylistClient(s3Client, "directory", "bucket", "playlist");
    PlaylistItemList actual = target.findPreviousPlaylist();

    assertThat(actual).isNull();
    verify(s3Client, times(1)).findNewestObjectSummary("bucket", "directory", "playlist.json");
    verify(s3Client, never()).getObject(any(), any(), any());
  }

  @Test
  void putPlaylist() {
    when(s3Client.putJsonObject(any(), any(), any())).thenReturn(true);

    S3PlaylistClient target = new S3PlaylistClient(s3Client, "directory/", "bucket", "playlist");
    target.putPlaylist(List.of());

    verify(s3Client, times(1)).putJsonObject(eq("bucket"), matches("directory/\\d+-playlist.json"), any());
  }
}