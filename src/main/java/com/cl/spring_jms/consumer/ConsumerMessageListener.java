package com.cl.spring_jms.consumer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * Created by cl on 2017/12/5.
 */
public class ConsumerMessageListener implements MessageListener {
    public void onMessage(Message message) {
        TextMessage message1 = (TextMessage) message;
        try {
            System.out.println("接收消息：" + message1.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
