package com.github.wshimaya.oursongmonitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.github.wshimaya.oursongmonitor.model.PlaylistItem;
import com.github.wshimaya.oursongmonitor.model.PlaylistItemList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;

@ExtendWith(MockitoExtension.class)
class S3ClientTest {

  @Mock
  AmazonS3 s3;

  @Mock
  PutObjectResult result;

  @Test
  void findNewestObjectTest() {
    var target = new S3Client(s3);

    var actual = target.findNewestObject("inoristatest", "our-song-monitor/", ".json");

    System.out.println(actual);
  }

  @Test
  void testPutJsonObjectTest() {
    ArgumentMatcher<String> jsonMatcher = json -> {
      try {
        JSONAssert.assertEquals(json, "{\"items\":[{}]}", false);
      } catch (JSONException exception) {
        throw new AssertionError(exception.getMessage());
      }
      return true;
    };
    when(s3.putObject(anyString(), anyString(), anyString())).thenReturn(result);
    var jsonObj = new PlaylistItemList(List.of(new PlaylistItem()));

    var target = new S3Client(s3);
    target.putJsonObject("bucketName", "key", jsonObj);

    verify(s3, times(1))
        .putObject(eq("bucketName"), eq("key"), argThat(jsonMatcher));

  }

  @Test
  void test() {
    var actual = Stream.of(new Date(100L), new Date(200L))
        .max(Comparator.naturalOrder())
        .orElse(null);
    assertEquals(new Date(200L), actual);
  }
}
