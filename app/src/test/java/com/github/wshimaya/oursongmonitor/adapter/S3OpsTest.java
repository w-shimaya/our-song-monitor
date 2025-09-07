package com.github.wshimaya.oursongmonitor.adapter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wshimaya.oursongmonitor.adapter.S3Ops;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Key;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

@ExtendWith(MockitoExtension.class)
@JsonTest
class S3OpsTest {

  @Autowired
  private ObjectMapper objectMapper;

  @Mock
  private S3Client s3;

  //  @Mock
//  private PutObjectResult result;
//
//  @Mock
//  private ObjectListing objectListing;
//
  @Mock
  private S3Object freshObject, agingObject, oldObject, irrelevantObject;

  @Test
  void testGetObject() throws Exception {
    var body = "{\"key\":\"value\"}".getBytes(StandardCharsets.UTF_8);
    when(s3.getObjectAsBytes(GetObjectRequest.builder()
        .bucket("bucket-name")
        .key("key")
        .build()))
        .thenReturn(
            ResponseBytes.fromByteArray(GetObjectResponse.builder()
                    .contentLength((long) body.length)
                    .contentType("text/plain")
                    .build(),
                body));

    var target = new S3Ops(s3, objectMapper);
    TestObject actual = target.getObject("bucket-name", "key", TestObject.class);

    assertThat(actual).hasFieldOrPropertyWithValue("key", "value");
  }


  @Test
  void findNewestObjectTest() {
    when(s3.listObjects(ListObjectsRequest.builder()
        .bucket("test-bucket")
        .prefix("prefix")
        .build()))
        .thenReturn(ListObjectsResponse.builder()
            .contents(freshObject, oldObject, agingObject, irrelevantObject)
            .build());
    when(freshObject.lastModified()).thenReturn(Instant.ofEpochSecond(1000L));
    when(freshObject.key()).thenReturn("fresh-object.suffix");
    when(agingObject.lastModified()).thenReturn(Instant.ofEpochSecond(100L));
    when(agingObject.key()).thenReturn("aging-object.suffix");
    when(oldObject.lastModified()).thenReturn(Instant.ofEpochSecond(10L));
    when(oldObject.key()).thenReturn("old-object.suffix");
    when(irrelevantObject.key()).thenReturn("irrelevant-object.irr");

    var target = new S3Ops(s3, objectMapper);

    S3Object actual = target.findNewestObject("test-bucket", "prefix", ".suffix");

    assertThat(actual).isEqualTo(freshObject);
  }

  @Test
  void testPutJsonObjectTest() throws Exception {
    var jsonObj = new TestObject();
    jsonObj.key = "value";

    var target = new S3Ops(s3, objectMapper);
    target.putJsonObject("bucket", "key", jsonObj);

    verify(s3, times(1))
        .putObject(PutObjectRequest.builder()
            .key("key")
            .bucket("bucket")
            .build(),
            RequestBody.fromString("{\"key\": \"value\"}"));
  }
//
//  @Test
//  void testPutJsonObjectTest_AmazonServiceException() {
//    when(s3.putObject(anyString(), anyString(), anyString())).thenThrow(
//        AmazonServiceException.class);
//
//    var target = new S3Ops(s3);
//
//    assertThatThrownBy(() -> target.putJsonObject("bucketName", "key", new TestObject()))
//        .isInstanceOf(AmazonServiceException.class);
//  }

  @EqualsAndHashCode(callSuper = true)
  @Data
  public static class TestObject extends GenericJson implements Serializable {

    @Key
    private String key;
  }
}
