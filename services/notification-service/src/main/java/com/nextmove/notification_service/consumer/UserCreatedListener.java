package com.nextmove.notification_service.consumer;

import com.nextmove.notification_service.config.RabbitMQConfig;
import com.nextmove.notification_service.dto.UserCreatedEvent;
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
public class UserCreatedListener {

    private final EmailService emailService;
    private final RabbitRetryHandler retryHandler;

    @RabbitListener(queues = RabbitMQConfig.USER_CREATED_QUEUE)
    public void onUserCreated(UserCreatedEvent event, Message message) {
        int retryCount = retryHandler.getRetryCount(message, RabbitMQConfig.USER_CREATED_QUEUE);

        log.info("📩 [UserCreated] Recebido: {} | Tentativa: {}", event, retryCount + 1);

        try {
            emailService.sendWelcomeEmail(event.email(), event.name());
            log.info("✅ [UserCreated] Email de boas-vindas enviado para: {}", event.email());

        } catch (Exception ex) {
            retryHandler.handleError(
                    event,
                    ex,
                    retryCount,
                    RabbitMQConfig.DLQ_EXCHANGE,
                    RabbitMQConfig.USER_CREATED_DLQ_ROUTING_KEY,
                    "UserCreated",
                    event.email()
            );
        }
    }
}