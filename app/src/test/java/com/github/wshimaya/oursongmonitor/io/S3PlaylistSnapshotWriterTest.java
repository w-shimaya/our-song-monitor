package com.github.wshimaya.oursongmonitor.io;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.wshimaya.oursongmonitor.adapter.playlist.client.S3PlaylistClient;
import com.github.wshimaya.oursongmonitor.domain.PlaylistItem;
import com.github.wshimaya.oursongmonitor.domain.PlaylistItemList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;

@ExtendWith(MockitoExtension.class)
class S3PlaylistSnapshotWriterTest {

  @Mock
  S3PlaylistClient client;

  @Test
  void write() throws Exception {
    PlaylistItemList list = new PlaylistItemList(List.of(PlaylistItem.builder().build()));
    doNothing().when(client).putPlaylist(any());

    PlaylistSnapshotWriter target = new S3PlaylistSnapshotSnapshotWriter(client);

    target.write(new Chunk<PlaylistItemList>(list));

    verify(client, times(1)).putPlaylist(list);
  }
}