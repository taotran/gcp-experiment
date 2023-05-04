package com.ttran.gcp.service;

import com.ttran.gcp.config.PubSubConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
public class Publisher {

    @Autowired
    private PubSubConfig.PubsubOutboundGateway outboundGateway;

    public void write(String payload) {
        log.info("Ready to send msg: {}", payload);

        outboundGateway.sendToPubSub(MessageBuilder.createMessage(payload, new MessageHeaders(Collections.emptyMap())));
    }
}
