package com.nextmove.notification_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String USER_CREATED_QUEUE = "user.created.queue";
    public static final String USER_CREATED_ROUTING_KEY = "user.created";
    public static final String EXCHANGE = "nextmove.direct";

    @Bean
    public Queue userCreatedQueue() {
        return new Queue(USER_CREATED_QUEUE, true);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding userCreatedBinding(Queue userCreatedQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(userCreatedQueue).to(directExchange).with(USER_CREATED_ROUTING_KEY);
    }
}
