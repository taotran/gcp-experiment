package com.ttran.gcp.config;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Slf4j
@Configuration
public class PubSubConfig {

    @Value("${gcp.pubsub.subscription}")
    private String subscriptionName;

    /*Publisher config*/

    @Bean
    @ServiceActivator(inputChannel = "pubsubOutputChannel")
    public MessageHandler messageSender(PubSubTemplate pubSubTemplate) {
        final String topic = "mdm-internal";
        PubSubMessageHandler handler = new PubSubMessageHandler(pubSubTemplate, topic);
        handler.setPublishTimeout(1000);
        handler.setSuccessCallback(((ackId, message) -> log.info("Message was sent, content {}, topic {}, ackId {}",
                message.getPayload(), topic, ackId)));

        handler.setFailureCallback(((cause, message) -> log.info("Message was sent, content {}, topic {}, cause {}",
                message.getPayload(), topic, cause)));

        return handler;
    }

    /*Subscriber config*/
    @Bean
    public DirectChannel inputMessageChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    public PubSubInboundChannelAdapter inboundChannelAdapter(@Qualifier("inputMessageChannel") MessageChannel messageChannel,
                                                             PubSubTemplate pubSubTemplate) {
        final PubSubInboundChannelAdapter adapter =
                new PubSubInboundChannelAdapter(pubSubTemplate, subscriptionName);

        adapter.setOutputChannel(messageChannel);
        adapter.setAckMode(AckMode.AUTO);
        adapter.setPayloadType(String.class);

        return adapter;
    }

    @MessagingGateway(defaultRequestChannel = "pubsubOutputChannel")
    public interface PubsubOutboundGateway {
        void sendToPubSub(Message<String> msg);
    }

}
