package com.nextmove.notification_service.consumer;

import com.nextmove.notification_service.config.RabbitMQConfig;
import com.nextmove.notification_service.dto.TransactionReminderEvent;
import com.nextmove.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionReminderListener {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.TRANSACTION_REMINDER_QUEUE)
    public void onTransactionReminder(TransactionReminderEvent event) {
        log.info("📩 Recebido Transaction Reminder: {}", event);

        try {
            emailService.sendTransactionReminderEmail(event);
            log.info("✅ Email de lembrete enviado para: {}", event.email());

        } catch (Exception ex) {
            log.error("❌ Erro ao processar Transaction Reminder para {}: {}", event.email(), ex.getMessage(), ex);
            throw ex;
        }
    }
}