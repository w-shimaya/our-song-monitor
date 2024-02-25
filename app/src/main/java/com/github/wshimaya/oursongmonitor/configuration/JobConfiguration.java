package com.github.wshimaya.oursongmonitor.configuration;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.github.wshimaya.oursongmonitor.client.DiscordWebhookClient;
import com.github.wshimaya.oursongmonitor.client.S3Client;
import com.github.wshimaya.oursongmonitor.client.S3PlaylistClient;
import com.github.wshimaya.oursongmonitor.client.YoutubePlaylistClient;
import com.github.wshimaya.oursongmonitor.io.PlaylistHistoryReader;
import com.github.wshimaya.oursongmonitor.io.S3PlaylistCacheWriter;
import com.github.wshimaya.oursongmonitor.job.PlaylistChangeNotifier;
import com.github.wshimaya.oursongmonitor.model.PlaylistHistory;
import com.github.wshimaya.oursongmonitor.model.PlaylistItem;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
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

@Configuration
public class JobConfiguration {

  @Bean
  public Job monitorJob(@NonNull final JobRepository jobRepository,
      @NonNull final Step temporaryStep) {
    return new JobBuilder("monitorJob", jobRepository)
        .start(temporaryStep)
        .build();
  }

  @Bean
  public Step monitorStep(@NonNull final JobRepository jobRepository,
      @NonNull final PlaylistHistoryReader reader,
      @NonNull final PlaylistChangeNotifier notifier,
      @NonNull final S3PlaylistCacheWriter writer,
      @NonNull final PlatformTransactionManager transactionManager) {
    return new StepBuilder("monitorStep", jobRepository)
        .<PlaylistHistory, List<PlaylistItem>>chunk(3, transactionManager)
        .reader(reader)
        .processor(notifier)
        .writer(writer)
        .build();
  }

  @Bean
  public S3PlaylistClient s3PlaylistClient(@NonNull final AppConfiguration configuration) {
    return new S3PlaylistClient(new S3Client(AmazonS3ClientBuilder.standard()
        .withRegion(Regions.AP_NORTHEAST_1)
        .build()),
        configuration.getS3().getDirectoryName(),
        configuration.getS3().getBucketName(),
        configuration.getPlaylistId());
  }

  @Bean
  public YoutubePlaylistClient youtubePlaylistClient(
      @NonNull final AppConfiguration configuration) throws Exception {
    return new YoutubePlaylistClient(
        new Builder(GoogleNetHttpTransport.newTrustedTransport(),
            new GsonFactory(),
            null)
            .setApplicationName("our-song-monitor")
            .build(), configuration.getYoutube().getApiKey(), configuration.getPlaylistId());
  }

  @Bean
  public DiscordWebhookClient discordWebhookClient(@NonNull final RestTemplate restTemplate,
      @NonNull final AppConfiguration configuration) {
    return new DiscordWebhookClient(configuration.getDiscord().getWebhookUrl(), restTemplate);
  }

  @Bean
  public RestTemplate restTemplate(@NonNull final RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder.build();
  }
}
