package com.inspur.delay.provider;

/**
 * @author jiangll01
 * @Date: 2020/11/30 20:25
 * @Description:
 */
public interface DelayMessage {
    /**
     * 获得延迟时间（单位秒）
     *
     * @return 延迟时间单位秒
     */
    int getDelay();
}
