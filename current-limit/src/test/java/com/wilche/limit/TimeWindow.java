package com.wilche.limit;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;

/**
 * 滑动窗口：是对计数器方式的改进，增加一个时间粒度的度量单位
 *    1. 把一分钟分成若干等分（6份，没份10s），在每一份上设置独立的计数器，在 00:00~00:09 之间发生请求计数器累加1
 *    2. 等份数量越大，统计越详细
 *
 * @author caoweiquan
 * @date 2021/3/17
 */
public class TimeWindow {

    private ConcurrentLinkedQueue<Long> queue = new ConcurrentLinkedQueue<>();

    /**
     * 间隔秒数
     */
    private int seconds;

    /**
     * 最大限流
     */
    private int max;

    public static void main(String[] args) throws Exception {

        final TimeWindow timeWindow = new TimeWindow(20, 10);

        // 测试3个线程
        IntStream.range(0, 10).forEach((i) -> new Thread(() -> {

            while (true) {

                try {
                    Thread.sleep(new Random().nextInt(20) * 100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                timeWindow.take();
            }

        }).start());

    }

    public TimeWindow(int seconds, int max) {
        this.seconds = seconds;
        this.max = max;

        // 永续线程执行queue任务
        new Thread(() -> {
            while (true) {
                try {
                    // 等待 间隔秒数-1 执行清理操作
                    Thread.sleep((seconds - 1) * 1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                clean();
            }
        }).start();
    }

    /**
     * 获取令牌，并添加时间
     */
    public void take() {
        long start = System.currentTimeMillis();

        int size = sizeOfValid();
        if (size > max) {
            System.err.println("超限");
        }
        synchronized (queue) {
            if (sizeOfValid() > max) {
                System.err.println("超限");
                System.err.println("queue 中有" + queue.size() + "最大数量" + max);
            }
            this.queue.offer(System.currentTimeMillis());
        }
        System.out.println("queue 中有" + queue.size() + "最大数量" + max);
    }

    public int sizeOfValid() {
        Iterator<Long> it = queue.iterator();
        long ms = System.currentTimeMillis() - seconds * 1000;
        int count = 0;
        while (it.hasNext()) {
            long t = it.next();
            if (t > ms) {
                // 在当前的统计时间范围
                count++;
            }
        }
        return count;
    }

    /**
     * 清理过期时间
     */
    public void clean() {
        Long c = System.currentTimeMillis() - seconds * 1000;

        Long t1;
        while ((t1 = queue.peek()) != null && t1 < c) {
            System.out.println("清理成功");
            queue.poll();
        }
    }
}
