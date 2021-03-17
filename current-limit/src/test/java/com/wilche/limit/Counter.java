package com.wilche.limit;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <a href="https://gitee.com/Doocs/advanced-java/blob/main/docs/high-concurrency/huifer-how-to-limit-current.md">
 * 如何限流？在工作中是怎么做的？说一下具体的实现？</a>
 * <p>
 * 计数器
 * <p>
 * 实现方式：控制单位时间内的请求数量
 * <p>===没看懂===<p>
 *
 * @author caoweiquan
 * @date 2021/3/17
 */
public class Counter {
    /**
     * 最大访问数量
     */
    private final int limit = 10;

    /**
     * 访问时间差
     */
    private final long timeout = 1000;

    /**
     * 请求时间
     */
    private long time;

    /**
     * 当前计数器
     */
    private AtomicInteger reqCount = new AtomicInteger(0);

    public boolean limit() {
        long now = System.currentTimeMillis();
        if (now < time + timeout) {
            // 单位时间内
            reqCount.addAndGet(1);
            return reqCount.get() <= limit;
        } else {
            // 超出单位时间
            time = now;
            reqCount = new AtomicInteger(0);
            return true;
        }
    }

    public void setTime(long time) {
        this.time = time;
    }
}
