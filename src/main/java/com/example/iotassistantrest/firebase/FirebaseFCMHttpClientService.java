package com.example.iotassistantrest.firebase;

import com.example.iotassistantrest.iot.config.Lang;
import com.example.iotassistantrest.firebase.body.push.Data;
import com.example.iotassistantrest.firebase.body.push.MessageBody;
import com.example.iotassistantrest.firebase.body.push.Notification;
import com.example.iotassistantrest.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Service
class FirebaseFCMHttpClientService {
    private static final Logger log = LoggerFactory.getLogger(FirebaseFCMHttpClientService.class);

    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";

    private final String firebaseToken;
    private final String serverId;

    FirebaseFCMHttpClientService(@Value(value = "${local-server.id}") final String serverId,
                                 final String firebaseToken) {
        this.serverId = serverId;
        this.firebaseToken = firebaseToken;
        log.info("LocalServerId = " + this.serverId);
    }

    void push(Map<Lang,String> messages, Long sensorId) {
        pushMessage(messages.get(Lang.EN), Lang.EN.toString(), sensorId);
        pushMessage(messages.get(Lang.PL), Lang.PL.toString(), sensorId);
    }

    void pushMessage(String message, String lang, Long sensorId) {
        String json = JSONUtils.objectToJson(new MessageBody()
                                                    .to("/topics/"+ serverId + "_" + lang)
                                                    .notification(new Notification()
                                                                        .body(message)
                                                                        .title("IOT Assistant"))
                .data(new Data().sensorId(sensorId).serverId(serverId)));
        log.info(json);
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .headers("Content-Type","application/json", "Authorization", "Bearer "+ firebaseToken)
                    .uri(new URI(FCM_URL))
                    .POST( HttpRequest.BodyPublishers.ofString(json))
                    .build();

            httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(log::debug)
                    .join();
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
        }
    }
}
