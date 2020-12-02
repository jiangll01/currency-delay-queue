package com.inspur.delay.consumer;

import com.inspur.delay.provider.DelayMessage;

/**
 * @author jiangll01
 * @Date: 2020/11/30 20:25
 * @Description:
 */
public interface Strategy<T extends DelayMessage> {
    /**
     * 处理消息的方法
     *
     * @param delayMessage 收到的消息
     */
    void handle(T delayMessage);
}
