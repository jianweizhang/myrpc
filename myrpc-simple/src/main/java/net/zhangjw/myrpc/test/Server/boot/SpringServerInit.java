package net.zhangjw.myrpc.test.Server.boot;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created on 2016/11/15.
 */
public class SpringServerInit {
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring-provider.xml");
    }
}
