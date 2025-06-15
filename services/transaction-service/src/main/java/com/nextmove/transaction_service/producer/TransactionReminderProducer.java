package com.nextmove.transaction_service.producer;

import com.nextmove.transaction_service.config.RabbitMQConfig;
import com.nextmove.transaction_service.dto.TransactionReminderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionReminderProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendTransactionReminderEvent(TransactionReminderEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.TRANSACTION_REMINDER_ROUTING_KEY,
                event
        );
    }

}
