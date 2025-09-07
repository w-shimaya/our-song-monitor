package com.github.wshimaya.oursongmonitor.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.lang.NonNull;
import java.io.IOException;
import java.util.Comparator;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

/**
 * Amazon S3 client.
 */
@Slf4j
@RequiredArgsConstructor
public class S3Ops {

  /**
   * Amazon S3 client.
   */
  private final S3Client s3;

  private final ObjectMapper objectMapper;

  /**
   * Fetch an object as {@code T} from S3 bucket.
   *
   * @param bucketName Bucket name
   * @param key        Key
   * @param clazz      Class
   * @param <T>        Type
   * @return S3 object parsed as {@code T}
   */
  public <T> T getObject(@NonNull final String bucketName,
      @NonNull final String key,
      @NonNull final Class<T> clazz) throws IOException {
    ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(GetObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build());

    return objectMapper.readValue(objectBytes.asInputStream(), clazz);
  }

  /**
   * Upload an object as JSON to S3 bucket.
   *
   * @param bucketName Bucket name
   * @param key        Key
   * @param object     Object to upload
   */
  public <T> void putJsonObject(@NonNull final String bucketName,
      @NonNull final String key,
      @NonNull final T object) throws IOException {
    s3.putObject(PutObjectRequest.builder()
            .key(key)
            .bucket(bucketName)
            .build(),
        RequestBody.fromString(objectMapper.writeValueAsString(object)));
  }

  /**
   * Find the newest object that has {@code suffix}.
   *
   * @param bucketName Bucket name
   * @param prefix     Prefix (directory name)
   * @param suffix     Suffix
   * @return The newest object if any
   */
  @Nullable
  public S3Object findNewestObject(@NonNull final String bucketName,
      @NonNull final String prefix,
      @NonNull final String suffix) {
    ListObjectsResponse result = s3.listObjects(ListObjectsRequest.builder()
        .bucket(bucketName)
        .prefix(prefix)
        .build());

    return result.contents().stream()
        .filter(obj -> obj.key().endsWith(suffix))
        .max(Comparator.comparing(S3Object::lastModified, Comparator.naturalOrder()))
        .orElse(null);
  }
}
