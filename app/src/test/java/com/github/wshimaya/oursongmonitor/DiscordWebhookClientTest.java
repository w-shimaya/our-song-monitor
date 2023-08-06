package com.github.wshimaya.oursongmonitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wshimaya.oursongmonitor.DiscordWebhookClient.MessageBody;
import com.github.wshimaya.oursongmonitor.DiscordWebhookClient.MessageBody.Embed;
import com.github.wshimaya.oursongmonitor.DiscordWebhookClient.MessageBody.Embed.Field;
import com.github.wshimaya.oursongmonitor.DiscordWebhookClient.MessageBody.Embed.Thumbnail;
import com.github.wshimaya.oursongmonitor.model.PlaylistItemList;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.reflect.TypeToken;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.junit.jupiter.api.Test;

class DiscordWebhookClientTest {

  @Test
  void test() throws Exception {
    String url = "https://discord.com/api/webhooks/1134115036407808011/wF0zs_5_XZMsq5tfpcMwXUcy7MBDzmuCG14LaZrCfV1qK6m9v-VdMo0NLbwGZFu-osy7";
    HttpClient client = HttpClient.newBuilder()
        .version(Version.HTTP_2)
        .build();

    ObjectMapper mapper = new ObjectMapper();

    MessageBody body = MessageBody.builder()
        .embeds(List.of(
            Embed.builder()
                .title(":musical_note: MELODY IN THE POCKET")
                .url("https://www.youtube.com/watch?v=OY9jG8T1h84")
                .thumbnail(Thumbnail.builder()
                    .url("https://i.ytimg.com/vi/tmHbLmqFq-s/default.jpg")
                    .build())
                .description("みんなのうたに追加されました :+1:")
                .fields(List.of(
                    Field.builder()
                        .name("Channel")
                        .value("SuiSui")
                        .inline(true)
                        .build(),
                    Field.builder()
                        .name("Added by")
                        .value("inorista")
                        .inline(true)
                        .build()
                ))
                .build()
        ))
        .build();

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(
            mapper.writeValueAsString(body)))
        .build();

    var res = client.send(request, HttpResponse.BodyHandlers.ofString());
    System.out.println(res.body());
  }



}
