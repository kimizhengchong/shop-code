package com.baidu.rabbitmq.simple;

import com.baidu.rabbitmq.utils.RabbitmqConnectionUtil;
import com.rabbitmq.client.*;
import java.io.IOException;


/**
 * @ClassName Receive
 * @Description: TODO
 * @Author Mr.Zheng
 * @Date 2020-10-09 15:14
 * @Version V1.0
 **/
public class Receive {

    //队列名称
    private final static String QUEUE_NAME = "simple_queue";

    public static void main(String[] arg) throws Exception {
        // 获取连接
        Connection connection = RabbitmqConnectionUtil.getconnection();
        // 创建通道
        Channel channel = connection.createChannel();
        // 声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        // 定义队列 接收端==》消费者
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            // 监听队列中的消息，如果有消息，进行处理
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                // body： 消息中参数信息
                String msg = new String(body);
                System.out.println(" 收到消息，执行中 : " + msg + "!");

                //在有错误的情况下手动确认消息
                //System.out.println(1/0);
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
       /*
       param1 : 队列名称
       param2 : 是否自动确认消息
       param3 : 消费者
        */
        channel.basicConsume(QUEUE_NAME, false, consumer);

        //消费者需要时时监听消息，不用关闭通道与连接
    }

}
