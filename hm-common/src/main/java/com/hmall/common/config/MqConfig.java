package com.hmall.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmall.common.utils.RabbitMqHelper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(value = {RabbitTemplate.class, MessageConverter.class} )
public class MqConfig {
    @Bean
    @ConditionalOnClass(value = {ObjectMapper.class} )
    public MessageConverter messageConverter(ObjectMapper mapper){

        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        jackson2JsonMessageConverter.setCreateMessageIds( true);
        return jackson2JsonMessageConverter;
    }
    @Bean
    public RabbitMqHelper rabbitMqHelper(RabbitTemplate rabbitTemplate){
        return new RabbitMqHelper(rabbitTemplate);
    }
}
