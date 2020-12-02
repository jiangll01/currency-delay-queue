package com.inspur.delay.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jiangll01
 * @Date: 2020/11/28 21:09
 * @Description:
 */
@ConfigurationProperties(prefix = "delay.queue")
public class DelayQueueProperties  {

    private  String exchange;

    private  String queue;

    private  String ttlExchange;

    private  String ttlQueue;

    private  String routingKey;

    private  String ttlRoutingKey;

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getTtlExchange() {
        return ttlExchange;
    }

    public void setTtlExchange(String ttlExchange) {
        this.ttlExchange = ttlExchange;
    }

    public String getTtlQueue() {
        return ttlQueue;
    }

    public void setTtlQueue(String ttlQueue) {
        this.ttlQueue = ttlQueue;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getTtlRoutingKey() {
        return ttlRoutingKey;
    }

    public void setTtlRoutingKey(String ttlRoutingKey) {
        this.ttlRoutingKey = ttlRoutingKey;
    }
}
