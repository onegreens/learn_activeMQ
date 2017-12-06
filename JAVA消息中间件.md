# JAVA消息中间件

## 1. JMS

Java消息服务（Java Message Service），是一个Java平台中关于面向消息中间件的API，用于两个应用程序之间，或分布式系统中发送消息，进行异步通信

## 2. AMQP

advanced message queuing protocol

一个提供统一消息服务的应用层标准协议，基于此协议的客户端与消息中间件可传递消息，并不受客户端/中间件不同产品，不同开发语言等条件的限制

|      | JMS规范                                    | AMQP协议                                   |
| ---- | ---------------------------------------- | ---------------------------------------- |
| 定义   | Java Api                                 | Wire-protocol                            |
| 跨语言  | 否                                        | 是                                        |
| 消息类型 | 提供了两种消息模型：p2p，pub/sub                    | 提供了五种消息模型：direct;fanout;topic;headers;system |
| 消息类型 | TextMessage,MapMessage,BytesMessage,SteamMessage,ObjectMessage,Message | byte[]                                   |
| 综合评价 | JMS定义了JAVA API层面的标准，在java体系中，多个client均可以通过JMS进行交互，不需要应用修改代码，但是其对跨平台的支持较差 | AMQP的主要特征是面向消息、队列、路由（包括点对点和发布/订阅）、可靠性。安全性 |

- ActiveMQ
- RabbitMQ
- Kafka


## 3. JMS规范

- 提供者：实现JMS规范的消息中间件服务器
- 客户端：发送或接受消息的应用程序
- 生产者：创建并发送消息的客户端
- 消费者：接受并处理消息的客户端
- 消息：应用程序之间传递的数据内容
- 消息模式；在客户端之间传递消息的方式，JMS中定义了主题和队列两种模式

## 4. 消息模式

### 队列模式

- 客户端包括生产者和消费者
- 队列中的消息只能被一个消费者消费
- 消费者可以随时消费队列中的消息

### 主题模式

- 客户端包括发布者和订阅者
- 主题中的消息被所有订阅者消费
- 消费者不能消费订阅之前就发送到主题中的消息

##  5. JMS编码接口

- ConnectionFactory

  用于创建连接到消息中间件的连接工厂

- Connection

  代表了应用程序和消息服务器之间的通信链路

- Destination

  指消息发布和接收的地点，包括队列或主题

- Session

  表示一个单线程的上下文，用于发送和接收消息

- MessageConsumer

  由会话创建，用于接收发送到目标的消息

- MessageProducer

  由会话创建，用于发送消息到目标

- Message

  是在消费者和生产着之间传送的对象，消息头，一组消息属性，一个消息体

## 6. 安装ActiveMQ

在官网下载最新版本 apache-activemq-5.15.2

- 启动方式1

  以管理员的身份启动 D:\tool\activeMq\apache-activemq-5.15.2\bin\win64\activemq.bat

  在浏览器中输入：127.0.0.1:8161 判断是否成功


- 启动方式2

  以管理员的身份启动 D:\tool\activeMq\apache-activemq-5.15.2\bin\win64\installService.bat

  打开服务即可查询程序运行

  启动

## 7. 基本演示 - 队列模式

### 1. 提供者 发送消息

```java
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

```

### 2. 消费者 接收消息

```java
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
 
```



## 8. 基本演示 - 主题模式

### 1. 提供者 发送消息

```java
package com.cl.jms.queue;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by cl on 2017/12/5.
 * 提供者 发送消息
 */
public class AppProducer {
    private static final String url = "tcp://localhost:61616";
    private static final String topicName = "queue-test";

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
        Destination destination = session.createTopic(topicName);

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
```

### 2. 消费者 接收消息

```java
package com.cl.jms.queue;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by cl on 2017/12/5.
 */
public class AppConsumer {
    private static final String url = "tcp://localhost:61616";
    private static final String topicName = "queue-test";

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
        Destination destination = session.createTopic(topicName);

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
 
```

## 9. Spring整合JMS

使用Spring集成JMS连接ActiveMQ

- ConnectionFactory 用于管理连接的工厂，连接池

  1. SingleConnectionFactory 只有一个连接
  2. CachingConnectionFactory 提供缓存功能

- JmsTemplate 用于发送和接收消息的模板类

  用于操作jms，线程安全

- MessageListerner 消息监听器

  实现其onMessage()

### 1.添加依赖

```xml
<properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>

        <!-- spring版本号 -->
        <spring.version>4.3.5.RELEASE</spring.version>

        <!--maven打包时候忽略测试代码-->
        <maven.test.skip>true</maven.test.skip>
    </properties>
    <dependencies>

        <!-- 添加junit4依赖 -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <!-- 指定范围，在测试时才会加载 -->
            <scope>test</scope>
        </dependency>


        <dependency>
            <groupId>org.apache.activemq</groupId>
            <artifactId>activemq-all</artifactId>
            <version>5.7.0</version>
        </dependency>

        <dependency>
            <groupId>org.apache.activemq</groupId>
            <artifactId>activemq-core</artifactId>
            <version>5.7.0</version>
            <exclusions>
                <exclusion>
                    <artifactId>spring-context</artifactId>
                    <groupId>org.springframework</groupId>
                </exclusion>
            </exclusions>
        </dependency>


        <!-- 添加spring核心依赖 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jms</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>${spring.version}</version>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>${spring.version}</version>
        </dependency>
    </dependencies>
```



### 2. 添加配置

- 公共

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <beans xmlns="http://www.springframework.org/schema/beans"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns:context="http://www.springframework.org/schema/context"
         xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

      <!--配置注解扫描-->
      <context:annotation-config/>

      <!--创建ActiveMQ连接工厂-->
      <bean id="targetConnectionFactory" class="org.apache.activemq.ActiveMQConnectionFactory">
          <property name="brokerURL" value="tcp://localhost:61616"/>
      </bean>
      <!--spring jms连接池-->
      <bean id="connectionFactory" class="org.springframework.jms.connection.SingleConnectionFactory">
          <property name="targetConnectionFactory" ref="targetConnectionFactory"/>
      </bean>

      <!--一个队列目的地 点对点的-->
      <bean id="queueDestination" class="org.apache.activemq.command.ActiveMQQueue">
          <constructor-arg value="queue"/>
      </bean>
  </beans>
  ```

- 发送

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <beans xmlns="http://www.springframework.org/schema/beans"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
      <import resource="spring.xml"/>

      <!--配置jmsTemplate 用于发送消息-->
      <bean id="jmsTemplate" class="org.springframework.jms.core.JmsTemplate">
          <property name="connectionFactory" ref="connectionFactory"/>
      </bean>

      <bean id="ProducerService" class="com.cl.spring_jms.producer.ProducerServiceImpl"/>
  </beans> 
  ```

- 接收

  ```
  <?xml version="1.0" encoding="UTF-8"?>
  <beans xmlns="http://www.springframework.org/schema/beans"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
      <!--导入公共配置-->
      <import resource="spring.xml"/>

      <!--配置消息监听器-->
      <bean class="com.cl.spring_jms.consumer.ConsumerMessageListener" id="consumerMessageListener"/>

      <!--配置消息监听容器-->
      <bean id="jsmContainer" class="org.springframework.jms.listener.DefaultMessageListenerContainer">
          <!--工厂-->
          <property name="connectionFactory" ref="connectionFactory"/>
          <!--目的地-->
          <property name="destination" ref="queueDestination"/>
          <!--监听器-->
          <property name="messageListener" ref="consumerMessageListener"/>
      </bean>
  </beans>
  ```

### 3. 编写接口

- 发送

  ```java
  package com.cl.spring_jms.producer;

  /**
   * Created by cl on 2017/12/5.
   */
  public interface ProducerService {
      void sendMessage(String message);
  }
  ```

```java
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
    Destination destination;

    public void sendMessage(final String message) {
        //使用jmsTemplate发送消息
        jmsTemplate.send(destination, new MessageCreator() {
//            创建消息
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage(message);
                return textMessage;
            }
        });
        System.out.println("发送：" + message);

    }
}
```

```java
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
```

- 接收

  ```java
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
  ```

```java
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
```



### 4. 模式切换

#### 1. 添加定义主题模式

```xml
<!--主题模式-->
<bean id="topicDestination" class="org.apache.activemq.command.ActiveMQTopic">
    <!--消息名称-->
    <constructor-arg value="topic"/>
</bean>
```

#### 2. 修改发送部分

```java
@Resource(name = "topicDestination")//将名称替换
Destination topicDestination;
```

####  3. 修改接收部分

```xml
<!--配置消息监听容器-->
<bean id="jsmContainer" class="org.springframework.jms.listener.DefaultMessageListenerContainer">
    <!--工厂-->
    <property name="connectionFactory" ref="connectionFactory"/>
    <!--目的地-->
    <!--<property name="destination" ref="queueDestination"/>-->
    <property name="destination" ref="topicDestination"/><!-- 修改目的地 -->
    <!--监听器-->
    <property name="messageListener" ref="consumerMessageListener"/>
</bean>
```



## 注意

主题模式需要先启动消费者 完成订阅



# ActiveMQ集群配置

- 高可用
- 负载均衡

## 集群方式：

- 客户端集群：让多个消费者消费同一个队列
- Broker clusters : 多个Broker之间同步消息
- Master Slave : 实现高可用

## 客户端配置：

### ActiveMQ失效转移

允许当其中一台消息服务器宕机时，客户端在传输层上重新连接到其他消息服务器

语法：failover:(uri1,...uriN)?transportOptions

uri：地址，可多个


transportOptions参数说明：

- randomize:默认为true,表示在URI列表中选择URI连接时是否采用随机策略
- initialReconnectDelay : 默认为10，单位毫秒，表示第一次尝试重连之间等待的时间
- maxReconnectDelay ：默认30000，单位毫秒，最长重连的时间间隔



### Broker Cluster集群配置

- NetworkConnector (网络连接器)

  网络连接器主要用于配置 ActiveMQ服务器与服务器之间的网络通讯方式，用于服务器透传消息

  分为静态连接器和动态连接器

- 静态连接器

  ```xml
  <networkConnectors>
  	<networkConnector uri="static:(tcp://127.0.0.1:61617,tcp://127.0.0.1:61618)"/>
  </networkConnectors>
  ```

- 动态连接器

  ```xml
  <networkConnectors>
  <networkConnector uri="multicast://default" />
  </networkConnectors>

  <transportConnectors>
  <transportConnector uri="tcp://localhost:0" descoveryUri="multicast://default" />
  </transportConnectors>
  ```

  ​

  ### Master/Slave集群配置

  集群方案：

  - Share nothing storage master/slave（已过时）
  - Shared storage master/slave共享存储
  - Replicated LevelDB Store 基于复制的LevelDB Store



### ActiveMQ集群配置方案

|        | 服务端口  | 管理端口 | 存储   | 网络连接器         | 用途      |
| ------ | ----- | ---- | ---- | ------------- | ------- |
| Node-A | 61616 | 8161 | -    | Node-B,Node-C | 消费者     |
| Node-B | 61617 | 8162 | /    | Node-A        | 生产者、消费者 |
| Node-C | 61618 | 8163 | /    | Node-A        | 生产者、消费者 |



## 实践

文件地址：D:\tool\activeMq\test-activemq

1. 复制文件为A/B/C三份


2. 创建共享文件夹：kahadb

- 配置Node-A

2. 选择A（即Node-A）文件，切入conf目录下,打开activemq.xml

3. 选择transportConnectors节点，只保留openwire，注释其他传输连接

4. 为Node-A配置网络连接

   ```xml
   <networkConnectors>
               <networkConnector name="local_network" uri="static:(tcp://127.0.0.1:61617,tcp://127.0.0.1:61618)">
               </networkConnector>
           </networkConnectors>
   ```

5. 选择jetty.xml，查看port为8161，则无需修改



- 配置Node-B

1. activemq.xml

   - 在transportConnectors中注释其他协议，保留openwire，修改其uri为uri="tcp://0.0.0.0:61617"

   - 新增网络连接器：

     ```xml
     <networkConnectors>
                 <networkConnector name="network_a" uri="static:(tcp://127.0.0.1:61616)"/>
             </networkConnectors>
     ```

   - 在persistenceAdapter节点修改持久化文件存储地址，修改为之前创建的共享文件夹kahadb

     ```xml
     <persistenceAdapter>
                 <kahaDB directory="D:\tool\activeMq\test-activemq\kahadb"/>
             </persistenceAdapter>
     ```

2. jetty.xml

   修改port为8162

- 配置节点Node-C

  与Node-B配置类似，按照集群配置方案修改即可

### 测试

1. 启动Node-A
2. 启动Node-B
3. 启动Node-C
4. 由于Node-B先和Node-A连接，所以Node-C为停止状态。
5. 当Node-B停止运行时，Node-C会和Node-A建立连接

意外状况：在当前配置下，Node-A会不断获取Node-C的连接，由于当前连接被Node-B占用，Node-C停止，无法建立连接，Node-A不断抛出TransportDisposedIOException错误