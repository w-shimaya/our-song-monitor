package com.github.wshimaya.oursongmonitor.notifier;

import com.github.wshimaya.oursongmonitor.adapter.discord.DiscordWebhookClient;
import com.github.wshimaya.oursongmonitor.adapter.discord.DiscordWebhookClient.MessageBody;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NotifyDiscordOnJobFailure implements JobExecutionListener {

  private final DiscordWebhookClient client;

  @Override
  public void afterJob(@NonNull final JobExecution jobExecution) {
    if (jobExecution.getStatus() != BatchStatus.FAILED) {
      return;
    }

    String message = ":warning: **Job Failed**\n"
        + "> "
        + jobExecution.getAllFailureExceptions().stream()
        .findFirst()
        .map(Throwable::toString)
        .orElse("unknown cause")
        .replaceAll("[\\r\\n\\t]+", " ");
    var body = MessageBody.builder()
        .content(message)
        .build();
    client.sendMessage(body);
  }
}
