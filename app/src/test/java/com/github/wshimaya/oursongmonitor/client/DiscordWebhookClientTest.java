package com.github.wshimaya.oursongmonitor.client;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.wshimaya.oursongmonitor.client.DiscordWebhookClient.MessageBody;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class DiscordWebhookClientTest {

  @Mock
  private RestTemplate restTemplate;

  @Test
  void sendMessage() {
    String webhookUrl = "https://discord.com/api/webhooks/hogehoge";
    when(restTemplate.postForObject(eq("https://discord.com/api/webhooks/hogehoge"), any(),
        eq(String.class)))
        .thenReturn("response");
    DiscordWebhookClient target = new DiscordWebhookClient(webhookUrl,
        restTemplate);
    MessageBody body = MessageBody.builder()
        .content("content")
        .build();

    target.sendMessage(body);

    verify(restTemplate, times(1)).postForObject(webhookUrl, body, String.class);
  }

  @Test
  void sendMessage_empty_embeds() {
    String webhookUrl = "https://discord.com/api/webhooks/hogehoge";
    when(restTemplate.postForObject(eq("https://discord.com/api/webhooks/hogehoge"), any(),
        eq(String.class)))
        .thenReturn("response");
    DiscordWebhookClient target = new DiscordWebhookClient(webhookUrl,
        restTemplate);
    MessageBody body = MessageBody.builder()
        .embeds(List.of())
        .build();

    target.sendMessage(body);

    verify(restTemplate, never()).postForObject(any(), any(), any());
  }
}
