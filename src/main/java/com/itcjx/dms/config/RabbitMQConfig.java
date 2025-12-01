package com.itcjx.dms.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 队列名称
    public static final String AI_QUEUE = "ai.generate.queue";

    // 交换机名称
    public static final String AI_EXCHANGE = "ai.generate.exchange";

    // 路由键
    public static final String AI_ROUTING_KEY = "ai.generate.routing.key";

    // 声明队列
    @Bean
    public Queue aiGenerateQueue() {
        return QueueBuilder.durable(AI_QUEUE).build();
    }

    // 声明交换机
    @Bean
    public DirectExchange aiGenerateExchange() {
        return new DirectExchange(AI_EXCHANGE);
    }

    // 绑定队列和交换机
    @Bean
    public Binding aiGenerateBinding() {
        return BindingBuilder.bind(aiGenerateQueue()).to(aiGenerateExchange()).with(AI_ROUTING_KEY);
    }

    // 配置消息转换器
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // 配置RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
