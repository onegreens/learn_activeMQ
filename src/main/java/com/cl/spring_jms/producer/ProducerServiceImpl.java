package com.cl.spring_jms.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.annotation.Resource;
import javax.jms.*;

/**
 * Created by cl on 2017/12/5.
 */
public class ProducerServiceImpl implements ProducerService {
    @Autowired
    JmsTemplate jmsTemplate;

    @Resource(name = "queueDestination")
    Destination queueDestination;

    @Resource(name = "topicDestination")
    Destination topicDestination;

    public void sendMessage(final String message) {
        //使用jmsTemplate发送消息
        jmsTemplate.send(topicDestination, new MessageCreator() {
            //            创建消息
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage(message);
                return textMessage;
            }
        });
        System.out.println("发送：" + message);

    }
}
