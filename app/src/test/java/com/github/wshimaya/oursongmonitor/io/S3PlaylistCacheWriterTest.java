package com.github.wshimaya.oursongmonitor.io;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.wshimaya.oursongmonitor.client.S3PlaylistClient;
import com.github.wshimaya.oursongmonitor.model.PlaylistItem;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;

@ExtendWith(MockitoExtension.class)
class S3PlaylistCacheWriterTest {

  @Mock
  S3PlaylistClient client;

  @Test
  void write() throws Exception {
    List<PlaylistItem> list = List.of(new PlaylistItem());
    doNothing().when(client).putPlaylist(any());

    PlaylistWriter target = new S3PlaylistCacheWriter(client);

    target.write(new Chunk<List<PlaylistItem>>(list));

    verify(client, times(1)).putPlaylist(list);
  }
}