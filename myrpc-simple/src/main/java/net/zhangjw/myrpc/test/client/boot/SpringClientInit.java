package net.zhangjw.myrpc.test.client.boot;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created on 2016/11/15.
 */
public class SpringClientInit {
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring-consumer.xml");
    }
}
