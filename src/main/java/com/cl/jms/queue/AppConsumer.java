package com.cl.jms.queue;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by cl on 2017/12/5.
 */
public class AppConsumer {
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

        //6. 创建一个消费者
        MessageConsumer consumer = session.createConsumer(destination);

        //7.创建一个监听器
        consumer.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                TextMessage textMessage = (TextMessage) message;
                try {
                    System.out.println("接收消息："+textMessage.getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
        //9. 关闭连接
        //监听器是一个异步的过程，不能立马关闭连接，会导致无法监听到消息
//        connection.close();
    }
}
