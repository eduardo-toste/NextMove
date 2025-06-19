package com.nextmove.notification_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchanges
    public static final String EXCHANGE = "nextmove.direct";
    public static final String DLQ_EXCHANGE = "nextmove.direct.dlq";

    // Fila de criação de usuário
    public static final String USER_CREATED_QUEUE = "user.created.queue";
    public static final String USER_CREATED_ROUTING_KEY = "user.created";
    public static final String USER_CREATED_QUEUE_DLQ = "user.created.queue.dlq";
    public static final String USER_CREATED_ROUTING_KEY_DLQ = "user.created.dlq";

    // Fila de lembrete de transação
    public static final String TRANSACTION_REMINDER_QUEUE = "transaction.reminder.queue";
    public static final String TRANSACTION_REMINDER_ROUTING_KEY = "transaction.reminder";
    public static final String TRANSACTION_REMINDER_QUEUE_DLQ = "transaction.reminder.queue.dlq";
    public static final String TRANSACTION_REMINDER_ROUTING_KEY_DLQ = "transaction.reminder.dlq";

    // Exchanges
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public DirectExchange directExchangeDlq() {
        return new DirectExchange(DLQ_EXCHANGE);
    }

    // 🎯 Fila principal - User Created
    @Bean
    public Queue userCreatedQueue() {
        return QueueBuilder.durable(USER_CREATED_QUEUE)
                .withArgument("x-dead-letter-exchange", DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", USER_CREATED_ROUTING_KEY_DLQ)
                .build();
    }

    @Bean
    public Queue userCreatedQueueDlq() {
        return QueueBuilder.durable(USER_CREATED_QUEUE_DLQ).build();
    }

    // 🎯 Fila principal - Transaction Reminder
    @Bean
    public Queue transactionReminderQueue() {
        return QueueBuilder.durable(TRANSACTION_REMINDER_QUEUE)
                .withArgument("x-dead-letter-exchange", DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", TRANSACTION_REMINDER_ROUTING_KEY_DLQ)
                .build();
    }

    @Bean
    public Queue transactionReminderQueueDlq() {
        return QueueBuilder.durable(TRANSACTION_REMINDER_QUEUE_DLQ).build();
    }

    // 🔗 Bindings - User Created
    @Bean
    public Binding userCreatedBinding() {
        return BindingBuilder.bind(userCreatedQueue())
                .to(directExchange())
                .with(USER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding userCreatedBindingDlq() {
        return BindingBuilder.bind(userCreatedQueueDlq())
                .to(directExchangeDlq())
                .with(USER_CREATED_ROUTING_KEY_DLQ);
    }

    // 🔗 Bindings - Transaction Reminder
    @Bean
    public Binding transactionReminderBinding() {
        return BindingBuilder.bind(transactionReminderQueue())
                .to(directExchange())
                .with(TRANSACTION_REMINDER_ROUTING_KEY);
    }

    @Bean
    public Binding transactionReminderBindingDlq() {
        return BindingBuilder.bind(transactionReminderQueueDlq())
                .to(directExchangeDlq())
                .with(TRANSACTION_REMINDER_ROUTING_KEY_DLQ);
    }

    // 🔧 Conversor de mensagens
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}