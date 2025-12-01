package com.itcjx.dms.service;

import com.itcjx.dms.config.RabbitMQConfig;
import com.itcjx.dms.entity.AiGenerateTask;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AiMessageProducerService {
    //创建消息生产者服务

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送ai生成任务到消息队列
     */
    public void sendAiGenerateTask(AiGenerateTask aiGenerateTask) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.AI_EXCHANGE,
                RabbitMQConfig.AI_ROUTING_KEY,
                aiGenerateTask
        );
    }
}
