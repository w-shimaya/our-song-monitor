package com.github.wshimaya.oursongmonitor.client;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.StringInputStream;
import com.github.wshimaya.oursongmonitor.model.PlaylistItem;
import com.github.wshimaya.oursongmonitor.model.PlaylistItemList;
import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
  private AmazonS3 s3;

  @Mock
  private PutObjectResult result;

  @Mock
  private ObjectListing objectListing;

  @Mock
  private S3ObjectSummary freshObjectSummary, agingObjectSummary, oldObjectSummary, irrelevantObjectSummary;

  @Test
  void testGetObject() throws Exception {
    String json = "{\"key\":\"value\"}";
    var s3Object = new S3Object();
    s3Object.setObjectContent(new StringInputStream(json));
    when(s3.getObject("bucket-name", "key")).thenReturn(s3Object);

    var target = new S3Client(s3);

    TestObject actual = target.getObject("bucket-name", "key", TestObject.class);

    assertThat(actual).hasFieldOrPropertyWithValue("key", "value");
  }

  @Test
  void testGetObject_NullWhenAmazonExceptionThrown() {
    when(s3.getObject(anyString(), anyString())).thenThrow(AmazonServiceException.class);

    var target = new S3Client(s3);

    TestObject actual = target.getObject("bucket-name", "key", TestObject.class);

    assertThat(actual).isNull();
  }

  @Test
  void testGetObject_NullWhenIOExceptionThrown() {
    var s3Object = new S3Object();
    s3Object.setObjectContent(new InputStream() {
      @Override
      public int read() throws IOException {
        throw new IOException();
      }
    });
    when(s3.getObject("bucket-name", "key")).thenReturn(s3Object);

    var target = new S3Client(s3);

    TestObject actual = target.getObject("bucket-name", "key", TestObject.class);

    assertThat(actual).isNull();
  }

  @Test
  void findNewestObjectTest() {
    when(s3.listObjects("test-bucket", "prefix/")).thenReturn(objectListing);
    when(objectListing.getObjectSummaries()).thenReturn(
        List.of(freshObjectSummary, oldObjectSummary, agingObjectSummary, irrelevantObjectSummary));
    when(freshObjectSummary.getLastModified()).thenReturn(new Date(1000L));
    when(freshObjectSummary.getKey()).thenReturn("fresh-object.suffix");
    when(agingObjectSummary.getLastModified()).thenReturn(new Date(100L));
    when(agingObjectSummary.getKey()).thenReturn("aging-object.suffix");
    when(oldObjectSummary.getLastModified()).thenReturn(new Date(10L));
    when(oldObjectSummary.getKey()).thenReturn("old-object.suffix");
    when(irrelevantObjectSummary.getKey()).thenReturn("irrelevant-object.irr");

    var target = new S3Client(s3);

    S3ObjectSummary actual = target.findNewestObjectSummary("test-bucket", "prefix/", ".suffix");

    assertThat(actual).isEqualTo(freshObjectSummary);
  }

  @Test
  void testPutJsonObjectTest() throws Exception {
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
  void testPutJsonObjectTest_AmazonServiceException() {
    when(s3.putObject(anyString(), anyString(), anyString())).thenThrow(AmazonServiceException.class);

    var target = new S3Client(s3);

    assertThatThrownBy(() -> target.putJsonObject("bucketName", "key", new TestObject()))
        .isInstanceOf(AmazonServiceException.class);
  }

  @EqualsAndHashCode(callSuper = true)
  @Data
  public static class TestObject extends GenericJson implements Serializable {

    @Key
    private String key;
  }
}
