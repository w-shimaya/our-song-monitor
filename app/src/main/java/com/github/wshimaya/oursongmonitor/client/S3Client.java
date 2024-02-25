package com.github.wshimaya.oursongmonitor.client;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;

/**
 * Amazon S3 client.
 */
@Slf4j
public class S3Client {

  /**
   * Amazon S3 client.
   */
  private final AmazonS3 s3;

  /**
   * Factory of Json parser/generator.
   */
  private final JsonFactory factory = new GsonFactory();

  /**
   * Constructor.
   *
   * @param s3 Amazon S3 client
   */
  public S3Client(final AmazonS3 s3) {
    this.s3 = s3;
  }

  /**
   * Fetch an object as {@code T} from S3 bucket.
   *
   * @param bucketName Bucket name
   * @param key Key
   * @param clazz Class
   * @param <T> Type
   * @return S3 object parsed as {@code T}
   */
  public <T> T getObject(final String bucketName, final String key, final Class<T> clazz) {
    try {
      S3Object object = s3.getObject(bucketName, key);
      S3ObjectInputStream s3inputStream = object.getObjectContent();
      Reader reader = new BufferedReader(
          new InputStreamReader(s3inputStream, StandardCharsets.UTF_8));

      return factory.createJsonObjectParser().parseAndClose(reader, clazz);

    } catch (AmazonServiceException | IOException exception) {
      System.out.println(exception.getMessage());
      return null;
    }
  }

  /**
   * Upload an object as JSON to S3 bucket.
   *
   * @param bucketName Bucket name
   * @param key Key
   * @param object Object to upload
   * @return Succeeds or not
   */
  public boolean putJsonObject(final String bucketName,
      final String key,
      final GenericJson object) {
    String json = "";
    try (var writer = new StringWriter();
        var jsonGenerator = factory.createJsonGenerator(writer)) {
      jsonGenerator.serialize(object);
      json = writer.toString();
    } catch (IOException exception) {
      log.error("An error has occurred in serializing object: %s\n"
          .formatted(exception.getMessage()));
    }

    try {
      s3.putObject(bucketName, key, json);
    } catch (AmazonServiceException exception) {
      log.error(exception.getMessage());
      return false;
    }
    return true;
  }

  /**
   * Find the newest object that has {@code suffix}.
   *
   * @param bucketName Bucket name
   * @param prefix Prefix (directory name)
   * @param suffix Suffix
   * @return The newest object if any
   */
  @Nullable
  public S3ObjectSummary findNewestObjectSummary(final String bucketName,
      final String prefix,
      final String suffix) {
    var result = s3.listObjects(bucketName, prefix);

    return result.getObjectSummaries().stream()
        .filter(obj -> obj.getKey().endsWith(suffix))
        .max(Comparator.comparing(S3ObjectSummary::getLastModified,
            Comparator.naturalOrder()))
        .orElse(null);
  }
}
