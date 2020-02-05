package com.xuecheng.test.mq;


import com.rabbitmq.client.Channel;
import com.xuecheng.test.config.RabbitMQConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ReceiveHandler {
    //可以监听，多个队列，用逗号隔开。
    @RabbitListener(queues = {RabbitMQConfig.QUEUE_INFORM_EMAIL})
    public void send_email(String msg, Message message, Channel channel){
        //如果进来的消息，是字符串，就直接String,跟形参就可以取值
        System.err.println("吃包子:"+msg);
        //如果进来的是其他类型，用getBody方法取值
        System.err.println("吃大包子:"+message.getBody().toString());

    }

}
