package com.baidu.rabbitmq.Topic;

import com.baidu.rabbitmq.utils.RabbitmqConnectionUtil;
import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @ClassName ReceiveTwo
 * @Description: TODO
 * @Author Mr.Zheng
 * @Date 2020-10-10 11:32
 * @Version V1.0
 **/
public class ReceiveTwo {

    //交换机名称
    private final static String EXCHANGE_NAME = "topic_exchange_test";

    //队列名称
    private final static String QUEUE_NAME = "topic_exchange_queue_2";

    public static void main(String[] arg) throws Exception {
        // 获取连接
        Connection connection = RabbitmqConnectionUtil.getconnection();
        // 创建通道
        Channel channel = connection.createChannel();
        // 声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //消息队列绑定到交换机
        /*
        param1: 序列名
        param2: 交换机名
        param3: routingKey
         */
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "goods.update");
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "goods.save");
        // 定义队列 接收端==》消费者
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            // 监听队列中的消息，如果有消息，进行处理
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                // body： 消息中参数信息
                String msg = new String(body);
                System.out.println(" [消费者2模拟es服务] 接收到消息 : " + msg );
            }
        };
       /*
       param1 : 队列名称
       param2 : 是否自动确认消息
       param3 : 消费者
        */
        channel.basicConsume(QUEUE_NAME, true, consumer);

        //消费者需要时时监听消息，不用关闭通道与连接
    }

}
