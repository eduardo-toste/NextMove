package com.nextmove.notification_service.consumer;

import com.nextmove.notification_service.config.RabbitMQConfig;
import com.nextmove.notification_service.dto.TransactionReminderEvent;
import com.nextmove.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionReminderListener {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.TRANSACTION_REMINDER_QUEUE)
    public void onTransactionReminder(TransactionReminderEvent event) {
        emailService.sendTransactionReminderEmail(event);
    }

}
