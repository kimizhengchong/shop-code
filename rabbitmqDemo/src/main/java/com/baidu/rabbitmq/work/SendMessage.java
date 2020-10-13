package com.baidu.rabbitmq.work;

import com.baidu.rabbitmq.utils.RabbitmqConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * @ClassName SendMessage
 * @Description: TODO
 * @Author Mr.Zheng
 * @Date 2020-10-09 19:20
 * @Version V1.0
 **/
public class SendMessage {

    //序列名称
    private final static String QUEUE_NAME="test_work_queue";

    //主函数
    public static void main(String[] arg) throws Exception{
        //获取到连接
        Connection connection = RabbitmqConnectionUtil.getconnection();
        //获取通道
        Channel channel = connection.createChannel();

        /**
         * 参数1：队列名称
         * 参数2：是否持久化
         * 参数3：是否排外
         * 参数4：是否自动删除
         * 参数5：其他参数
         */
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        //循环发送消息100条
        for (int i = 0; i<100;i++){

            //消息参数内容
            String message = "task - good study -" + i;

            /**
             * 参数1：交换机名称
             * 参数2：routingkey
             * 参数3：一些配置信息
             * 参数4：发送的消息
             */
            //发送消息
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes());

            System.out.println(" 消息发送 " + message +" 到队列 success ");
        }
        //关闭通道和连接
        channel.close();
        connection.close();
    }

}
