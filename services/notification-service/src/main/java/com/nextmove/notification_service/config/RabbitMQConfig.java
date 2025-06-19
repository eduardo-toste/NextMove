package com.nextmove.notification_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 🔗 Exchanges
    public static final String EXCHANGE = "nextmove.direct";
    public static final String RETRY_EXCHANGE = "nextmove.direct.retry";
    public static final String DLQ_EXCHANGE = "nextmove.direct.dlq";

    // 🔔 User Created
    public static final String USER_CREATED_QUEUE = "user.created.queue";
    public static final String USER_CREATED_RETRY_QUEUE = "user.created.queue.retry";
    public static final String USER_CREATED_DLQ = "user.created.queue.dlq";

    public static final String USER_CREATED_ROUTING_KEY = "user.created";
    public static final String USER_CREATED_RETRY_ROUTING_KEY = "user.created.retry";
    public static final String USER_CREATED_DLQ_ROUTING_KEY = "user.created.dlq";

    // 🔔 Transaction Reminder
    public static final String TRANSACTION_REMINDER_QUEUE = "transaction.reminder.queue";
    public static final String TRANSACTION_REMINDER_RETRY_QUEUE = "transaction.reminder.queue.retry";
    public static final String TRANSACTION_REMINDER_DLQ = "transaction.reminder.queue.dlq";

    public static final String TRANSACTION_REMINDER_ROUTING_KEY = "transaction.reminder";
    public static final String TRANSACTION_REMINDER_RETRY_ROUTING_KEY = "transaction.reminder.retry";
    public static final String TRANSACTION_REMINDER_DLQ_ROUTING_KEY = "transaction.reminder.dlq";

    // 🔗 Exchanges
    @Bean
    public DirectExchange mainExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public DirectExchange retryExchange() {
        return new DirectExchange(RETRY_EXCHANGE);
    }

    @Bean
    public DirectExchange dlqExchange() {
        return new DirectExchange(DLQ_EXCHANGE);
    }

    // 🔔 User Created Queues
    @Bean
    public Queue userCreatedQueue() {
        return QueueBuilder.durable(USER_CREATED_QUEUE)
                .withArgument("x-dead-letter-exchange", RETRY_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", USER_CREATED_RETRY_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue userCreatedRetryQueue() {
        return QueueBuilder.durable(USER_CREATED_RETRY_QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE)
                .withArgument("x-dead-letter-routing-key", USER_CREATED_ROUTING_KEY)
                .withArgument("x-message-ttl", 60000) // ⏳ Retry após 60 segundos
                .build();
    }

    @Bean
    public Queue userCreatedDlq() {
        return QueueBuilder.durable(USER_CREATED_DLQ).build();
    }

    // 🔔 Transaction Reminder Queues
    @Bean
    public Queue transactionReminderQueue() {
        return QueueBuilder.durable(TRANSACTION_REMINDER_QUEUE)
                .withArgument("x-dead-letter-exchange", RETRY_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", TRANSACTION_REMINDER_RETRY_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue transactionReminderRetryQueue() {
        return QueueBuilder.durable(TRANSACTION_REMINDER_RETRY_QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE)
                .withArgument("x-dead-letter-routing-key", TRANSACTION_REMINDER_ROUTING_KEY)
                .withArgument("x-message-ttl", 60000) // ⏳ Retry após 60 segundos
                .build();
    }

    @Bean
    public Queue transactionReminderDlq() {
        return QueueBuilder.durable(TRANSACTION_REMINDER_DLQ).build();
    }

    // 🔗 Bindings - User Created
    @Bean
    public Binding userCreatedBinding() {
        return BindingBuilder.bind(userCreatedQueue())
                .to(mainExchange())
                .with(USER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding userCreatedRetryBinding() {
        return BindingBuilder.bind(userCreatedRetryQueue())
                .to(retryExchange())
                .with(USER_CREATED_RETRY_ROUTING_KEY);
    }

    @Bean
    public Binding userCreatedDlqBinding() {
        return BindingBuilder.bind(userCreatedDlq())
                .to(dlqExchange())
                .with(USER_CREATED_DLQ_ROUTING_KEY);
    }

    // 🔗 Bindings - Transaction Reminder
    @Bean
    public Binding transactionReminderBinding() {
        return BindingBuilder.bind(transactionReminderQueue())
                .to(mainExchange())
                .with(TRANSACTION_REMINDER_ROUTING_KEY);
    }

    @Bean
    public Binding transactionReminderRetryBinding() {
        return BindingBuilder.bind(transactionReminderRetryQueue())
                .to(retryExchange())
                .with(TRANSACTION_REMINDER_RETRY_ROUTING_KEY);
    }

    @Bean
    public Binding transactionReminderDlqBinding() {
        return BindingBuilder.bind(transactionReminderDlq())
                .to(dlqExchange())
                .with(TRANSACTION_REMINDER_DLQ_ROUTING_KEY);
    }

    // 🔧 Conversor JSON
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}