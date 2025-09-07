package com.github.wshimaya.oursongmonitor.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wshimaya.oursongmonitor.adapter.S3Ops;
import com.github.wshimaya.oursongmonitor.adapter.discord.DiscordWebhookClient;
import com.github.wshimaya.oursongmonitor.adapter.playlist.client.S3PlaylistClient;
import com.github.wshimaya.oursongmonitor.adapter.playlist.client.YoutubePlaylistClient;
import com.github.wshimaya.oursongmonitor.adapter.playlist.mapper.YoutubePlaylistItemMapper;
import com.github.wshimaya.oursongmonitor.domain.PlaylistHistory;
import com.github.wshimaya.oursongmonitor.domain.PlaylistItem;
import com.github.wshimaya.oursongmonitor.domain.PlaylistItemList;
import com.github.wshimaya.oursongmonitor.io.PlaylistHistoryReader;
import com.github.wshimaya.oursongmonitor.io.PlaylistSnapshotWriter;
import com.github.wshimaya.oursongmonitor.job.PlaylistChangeNotifier;
import com.github.wshimaya.oursongmonitor.notifier.NotifyDiscordOnJobFailure;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube.Builder;
import io.micrometer.common.lang.NonNull;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Create batch job beans.
 */
@Configuration
public class JobConfiguration {

  /**
   * Job.
   *
   * @param jobRepository {@link JobRepository}
   * @param monitorStep   step
   * @return job
   */
  @Bean
  public Job monitorJob(@NonNull final JobRepository jobRepository,
      @NonNull final Step monitorStep,
      @NonNull final NotifyDiscordOnJobFailure failureListener) {
    return new JobBuilder("monitorJob", jobRepository)
        .listener(failureListener)
        .start(monitorStep)
        .build();
  }

  /**
   * Step.
   *
   * @param jobRepository      {@link JobRepository}
   * @param reader             playlist reader
   * @param notifier           notifier
   * @param writer             playlist writer
   * @param transactionManager {@link PlatformTransactionManager}
   * @return step
   */
  @Bean
  public Step monitorStep(@NonNull final JobRepository jobRepository,
      @NonNull final PlaylistHistoryReader reader,
      @NonNull final PlaylistChangeNotifier notifier,
      @NonNull final PlaylistSnapshotWriter writer,
      @NonNull final PlatformTransactionManager transactionManager) {
    return new StepBuilder("monitorStep", jobRepository)
        .<PlaylistHistory, PlaylistItemList>chunk(3, transactionManager)
        .reader(reader)
        .processor(notifier)
        .writer(writer)
        .build();
  }

  /**
   * S3 playlist client.
   *
   * @param configuration configuration
   * @return client
   */
  @Bean
  public S3PlaylistClient s3PlaylistClient(@NonNull final AppConfiguration configuration,
      @NonNull final S3Ops s3Ops) {
    return new S3PlaylistClient(s3Ops,
        configuration.getS3().getDirectoryName(),
        configuration.getS3().getBucketName(),
        configuration.getPlaylistId());
  }

  @Bean
  public S3Ops s3Ops(@NonNull final ObjectMapper objectMapper) {
    return new S3Ops(S3Client.builder()
        .region(Region.AP_NORTHEAST_1)
        .build(), objectMapper);
  }

  /**
   * Youtube API playlist client.
   *
   * @param configuration configuration
   * @return client
   * @throws Exception exception
   */
  @Bean
  public YoutubePlaylistClient youtubePlaylistClient(
      @NonNull final AppConfiguration configuration,
      @NonNull final YoutubePlaylistItemMapper mapper) throws Exception {
    return new YoutubePlaylistClient(
        new Builder(GoogleNetHttpTransport.newTrustedTransport(),
            new GsonFactory(),
            null)
            .setApplicationName("our-song-monitor")
            .build(), configuration.getYoutube().getApiKey(),
        configuration.getPlaylistId(),
        mapper);
  }

  /**
   * Discord client.
   *
   * @param restTemplate  {@link RestTemplate}
   * @param configuration configuration
   * @return client
   */
  @Bean
  public DiscordWebhookClient discordWebhookClient(@NonNull final RestTemplate restTemplate,
      @NonNull final AppConfiguration configuration) {
    return new DiscordWebhookClient(configuration.getDiscord().getWebhookUrl(), restTemplate);
  }

  /**
   * Rest template.
   *
   * @param restTemplateBuilder {@link RestTemplateBuilder}
   * @return {@link RestTemplate}
   */
  @Bean
  public RestTemplate restTemplate(@NonNull final RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder.build();
  }

  @Bean
  public JsonFactory googleJsonFactory() {
    return new GsonFactory();
  }

  @Bean
  public HttpRequestFactory googleHttpRequestFactory(@NonNull final JsonFactory jsonFactory) {
    var transport = new NetHttpTransport();
    return transport.createRequestFactory(req -> req.setParser(new JsonObjectParser(jsonFactory)));
  }
}
