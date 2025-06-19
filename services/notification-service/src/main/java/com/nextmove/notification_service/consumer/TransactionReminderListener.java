package com.nextmove.notification_service.consumer;

import com.nextmove.notification_service.config.RabbitMQConfig;
import com.nextmove.notification_service.dto.TransactionReminderEvent;
import com.nextmove.notification_service.service.EmailService;
import com.nextmove.notification_service.util.RabbitRetryHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionReminderListener {

    private final EmailService emailService;
    private final RabbitRetryHandler retryHandler;

    @RabbitListener(queues = RabbitMQConfig.TRANSACTION_REMINDER_QUEUE)
    public void onTransactionReminder(TransactionReminderEvent event, Message message) {
        int retryCount = retryHandler.getRetryCount(message, RabbitMQConfig.TRANSACTION_REMINDER_QUEUE);

        log.info("📩 [TransactionReminder] Recebido: {} | Tentativa: {}", event, retryCount + 1);

        try {
            emailService.sendTransactionReminderEmail(event);
            log.info("✅ [TransactionReminder] Email enviado para: {}", event.email());

        } catch (Exception ex) {
            retryHandler.handleError(
                    event,
                    ex,
                    retryCount,
                    RabbitMQConfig.DLQ_EXCHANGE,
                    RabbitMQConfig.TRANSACTION_REMINDER_DLQ_ROUTING_KEY,
                    "TransactionReminder",
                    event.email()
            );
        }
    }
}