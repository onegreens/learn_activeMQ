package com.cl.spring_jms.producer;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by cl on 2017/12/5.
 * 测试 发送消息
 */
public class AppProducer {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("producer.xml");
        ProducerService producerService = (ProducerService) context.getBean("ProducerService");
        for (int i = 0; i < 100; i++) {
            producerService.sendMessage("test:" + i);
        }
        context.close();
    }
}
