package com.nextmove.transaction_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String TRANSACTION_REMINDER_QUEUE = "transaction.reminder.queue";
    public static final String TRANSACTION_REMINDER_ROUTING_KEY = "transaction.reminder";
    public static final String EXCHANGE = "nextmove.direct";

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue transactionReminderQueue() {
        return new Queue(TRANSACTION_REMINDER_QUEUE);
    }

    @Bean
    public Binding transactionRemindeBinding(Queue transactionReminderQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(transactionReminderQueue)
                .to(directExchange)
                .with(TRANSACTION_REMINDER_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

}
