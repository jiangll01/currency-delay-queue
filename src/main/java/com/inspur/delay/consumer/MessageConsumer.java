package com.inspur.delay.consumer;

import com.inspur.delay.provider.DelayMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author jiangll01
 * @Date: 2020/11/30 20:17
 * @Description:
 */
@Slf4j
@Component
public class MessageConsumer implements MessageListener {
    // key 泛型  Strategy 实现类
    private final Map<Type, Strategy> strategyMap = new ConcurrentHashMap<>();

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 策略模式的具体实现
     *
     * @param strategyProvider 实现{@link Strategy}接口的类集合
     */
    public MessageConsumer(ObjectProvider<List<Strategy>> strategyProvider) {
        //获取现在接口类的具体实现类
        List<Strategy> strategyList = strategyProvider.getIfAvailable();
        Optional.ofNullable(strategyList).ifPresent(strategies -> {
            strategies.forEach(strategy -> {
                //获取了泛型
                ParameterizedType type = (ParameterizedType) strategy.getClass().getGenericInterfaces()[0];
                Type typeArgument = type.getActualTypeArguments()[0];
                strategyMap.put(typeArgument, strategy);
            });
        });
    }

    @Override
    public void onMessage(Message message) {
        //处理重复消费问题，通过redis实现
        if (Boolean.FALSE.equals(redisTemplate.opsForValue().setIfAbsent(message.getMessageProperties().getCorrelationId(),
                message,60, TimeUnit.SECONDS))) {
            log.info("{} 出现了重复消费",message.toString());
        }
        MessageConverter messageConverter = new Jackson2JsonMessageConverter();
        DelayMessage delayMessage = (DelayMessage) messageConverter.fromMessage(message);
        Strategy strategy = strategyMap.get(delayMessage.getClass());
        if (ObjectUtils.isEmpty(strategy)) {
            strategy.handle(delayMessage);
        } else {
            log.info("Missing message processing class");
        }
    }
}

