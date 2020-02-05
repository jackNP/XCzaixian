package com.xuecheng.test.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//配置类
@Configuration
public class RabbitMQConfig {
    //邮件队列名称
    public static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    //消息队列名称
    public static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    //交换机名称
    public static final String EXCHANGE_TOPICS_INFORM="exchange_topics_inform";
    //路由队列，的路由通配符key
    public static final String ROUTINGKEY_EMAIL="inform.#.email.#";
    //消息队列，的路由通配符key
    public static final String ROUTINGKEY_SMS="inform.#.sms.#";

    //声明交换机
    @Bean(EXCHANGE_TOPICS_INFORM)
    public Exchange EXCHANGE_TOPICS_INFORM(){
        //durable(true) 持久化，mq重启之后交换机还在，.build()创建交换机
        return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_INFORM).durable(true).build();
    }

    //声明邮件队列
    @Bean(QUEUE_INFORM_EMAIL)
    public Queue QUEUE_INFORM_EMAIL(){
        return new Queue(QUEUE_INFORM_EMAIL);
    }
    //声明短信队列
    @Bean(QUEUE_INFORM_SMS)
    public Queue QUEUE_INFORM_SMS(){
        return new Queue(QUEUE_INFORM_SMS);
    }

    //@Qualifier注解：在IOC容器中，通过变量名，注入对象。
    // bind邮件队列，绑定，to交换机，with指定routingKey（暗号），noargs()不要扩展参数
    @Bean
    public Binding BINDING_QUEUE_INFORM_EMAIL(@Qualifier(QUEUE_INFORM_EMAIL) Queue queue,
                                              @Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_EMAIL).noargs();
    }
    //@Qualifier注解：在IOC容器中，通过变量名，注入对象。
    // bind短信队列，绑定，to交换机，with指定routingKey（暗号），noargs()不要扩展参数
    @Bean
    public Binding BINDING_ROUTINGKEY_SMS(@Qualifier(QUEUE_INFORM_SMS) Queue queue,
                                          @Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_SMS).noargs();
    }

}
