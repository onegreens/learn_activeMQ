package com.cl.jms.queue;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by cl on 2017/12/5.
 * 提供者 发送消息
 */
public class AppProducer {
    private static final String url = "tcp://localhost:61616";
    private static final String queueName = "queue-test";

    public static void main(String[] args) throws JMSException {
//1.创建连接工厂ConnectFactory
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);

        //2. 创建连接Connection
        Connection connection = connectionFactory.createConnection();

        //3.启动连接
        connection.start();

        //4.创建会话
        //是否使用事务，自动应答
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //5. 创建目标
        Destination destination = session.createQueue(queueName);

        //6.创建一个生产者
        MessageProducer producer = session.createProducer(destination);

        for (int i = 0; i < 100; i++) {
            //7. 创建消息
            TextMessage testMessage = session.createTextMessage("test:" + i);

            //8.发送
            producer.send(testMessage);
            System.out.println("发送消息" + testMessage.getText());
        }

        //9. 关闭连接
        connection.close();
    }
}
