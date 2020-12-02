package com.inspur.delay.config;


import com.inspur.delay.consumer.MessageConsumer;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jiangll01
 * @Date: 2020/11/28 21:25
 * @Description:
 */
@Configuration
@EnableConfigurationProperties(DelayQueueProperties.class)
public class DelayQueueAutoConfig {

    private final DelayQueueProperties delayQueueProperties;
    private static final String EXCHANGE = "x-dead-letter-exchange";
    private static final String ROUTING = "x-dead-letter-routing-key";

    public DelayQueueAutoConfig(DelayQueueProperties delayQueueProperties) {
        this.delayQueueProperties = delayQueueProperties;
    }

    /**
     * 普通消息交换机配置
     *
     * @return messageDirect
     */
    @Bean
    public DirectExchange messageDirect() {
        return new DirectExchange(delayQueueProperties.getExchange(), true, false);
    }

    /**
     *  普通消息队列配置
     * @return
     */

    @Bean
    public Queue messageQueue() {
        Map<String, Object> argsMap = new HashMap<>();
        argsMap.put(EXCHANGE, delayQueueProperties.getTtlExchange());
        argsMap.put(ROUTING, delayQueueProperties.getTtlRoutingKey());
        return new Queue(delayQueueProperties.getQueue(), true, false, false, argsMap);
    }

    @Bean
    Binding bindingDelayQueue() {
        return BindingBuilder
                .bind(messageQueue())
                .to(messageDirect())
                .with(delayQueueProperties.getRoutingKey());
    }

    /**
     *  死信交换机配置
     * @return 死信交换机
     */
    @Bean
    public DirectExchange messageTtlDirect() {
        return new DirectExchange(delayQueueProperties.getTtlExchange(), true, false);
    }

    /**
     * 死信消息队列配置
     * @return 死信队列
     */
    @Bean
    public Queue ttlMessageQueue() {
        return new Queue(delayQueueProperties.getTtlQueue(), true, false, false);
    }

    @Bean
    Binding bindingDelayQueue1() {
        return BindingBuilder.bind(ttlMessageQueue()).to(messageTtlDirect())
                .with(delayQueueProperties.getTtlRoutingKey());
    }

    @ConditionalOnMissingBean
    @Bean(value = "myRabbitTemplate")
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        //开启交换机到队列的消息是否到达，自动删除不可达消息，默认为false，true 则调用回调函数
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.containerAckMode(AcknowledgeMode.MANUAL);
        return rabbitTemplate;
    }

    /**
     * 通过监听器来实现监听
     * @param messageConsumer 消费者
     * @param factory 连接工厂
     * @return {@link SimpleMessageListenerContainer}
     */

    @Bean
    SimpleMessageListenerContainer simpleMessageListenerContainer(@Autowired MessageConsumer messageConsumer, ConnectionFactory factory) {
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(factory);
        simpleMessageListenerContainer.setQueueNames(delayQueueProperties.getTtlQueue());
        simpleMessageListenerContainer.setExposeListenerChannel(true);
        simpleMessageListenerContainer.setMessageListener(messageConsumer);
        return simpleMessageListenerContainer;
    }


}
