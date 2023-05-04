package com.ttran.gcp.service;

import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestListener {

    @ServiceActivator(inputChannel = "inputMessageChannel")
    public void messageReceiver(@Payload String payload,
                                @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage basicAcknowledgeablePubsubMessage) {
        log.info("MESSAGE: {}", payload);
//        log.info("MESSAGE: {}", message.getPubsubMessage().getData().toStringUtf8());
        basicAcknowledgeablePubsubMessage.ack();
    }
}
