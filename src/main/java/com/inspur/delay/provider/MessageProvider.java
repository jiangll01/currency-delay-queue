package com.inspur.delay.provider;

import com.inspur.delay.config.DelayQueueProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author jiangll01
 * @Date: 2020/11/30 20:20
 * @Description:
 */
@Component
@Slf4j
public class MessageProvider {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final DelayQueueProperties delayQueueProperties;


    @Autowired
    public MessageProvider(DelayQueueProperties delayQueueProperties) {
        this.delayQueueProperties = delayQueueProperties;
    }

    public void sendMessage(DelayMessage delayMessage) {
        log.info(" delay {} seconds to write to the message queue", delayMessage.getDelay());

        rabbitTemplate.convertAndSend(delayQueueProperties.getExchange(), delayQueueProperties.getRoutingKey(), delayMessage,
                message -> {
                    message.getMessageProperties().setCorrelationId(UUID.randomUUID().toString());
                    message.getMessageProperties().setExpiration(String.valueOf(delayMessage.getDelay() * 1000));
                    return message;
                });

    }
}

