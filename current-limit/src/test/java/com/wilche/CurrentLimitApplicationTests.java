package com.wilche;

import com.wilche.limit.Counter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.IntStream;

/**
 * 限流：限流可以认为服务降级的一种，限流就是限制系统的输入和输出量已达到保护系统的目的，。一般来说系统的吞吐量是可以被测算出来的，为了保证系统的稳定运行，
 * 一旦达到需要限制的阈值，就需要限制流量并采取一些措施已完成限制流量的目的。比如说：延迟处理，拒绝处理，或者部分拒绝处理等等。
 */
@SpringBootTest
class CurrentLimitApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void countTest() {
        Counter counter = new Counter();
        counter.setTime(System.currentTimeMillis());

        IntStream.range(0, 35).forEach(i -> {
//            try {
//                Thread.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            if (counter.limit()) {
                System.out.println("超出单位时间");
            } else {
                System.out.println("正常执行");
            }
        });
    }

}
