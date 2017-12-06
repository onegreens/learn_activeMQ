package com.cl.spring_jms.consumer;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by cl on 2017/12/5.
 * 接收消息
 */
public class AppConsumer {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("consumer.xml");
    }
}
