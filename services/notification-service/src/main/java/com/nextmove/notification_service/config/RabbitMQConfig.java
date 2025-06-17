package com.nextmove.notification_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String USER_CREATED_QUEUE = "user.created.queue";
    public static final String USER_CREATED_ROUTING_KEY = "user.created";
    public static final String TRANSACTION_REMINDER_QUEUE = "transaction.reminder.queue";
    public static final String TRANSACTION_REMINDER_ROUTING_KEY = "transaction.reminder";
    public static final String EXCHANGE = "nextmove.direct";

    @Bean
    public Queue userCreatedQueue() {
        return new Queue(USER_CREATED_QUEUE, true);
    }

    @Bean
    public Queue transactionReminderQueue() {
        return new Queue(TRANSACTION_REMINDER_QUEUE);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding userCreatedBinding(Queue userCreatedQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(userCreatedQueue).to(directExchange).with(USER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding transactionReminderBinding(Queue transactionReminderQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(transactionReminderQueue).to(directExchange).with(TRANSACTION_REMINDER_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
