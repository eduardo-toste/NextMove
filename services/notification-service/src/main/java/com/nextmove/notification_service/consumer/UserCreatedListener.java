package com.nextmove.notification_service.consumer;

import com.nextmove.notification_service.config.RabbitMQConfig;
import com.nextmove.notification_service.dto.UserCreatedEvent;
import com.nextmove.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCreatedListener {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.USER_CREATED_QUEUE)
    public void onUserCreated(UserCreatedEvent event) {
        log.info("📩 Recebido User Created Event: {}", event);

        try {
            emailService.sendWelcomeEmail(event.email(), event.name());
            log.info("✅ Email de boas-vindas enviado para: {}", event.email());

        } catch (Exception ex) {
            log.error("❌ Erro ao enviar email de boas-vindas para {}: {}", event.email(), ex.getMessage(), ex);
            throw ex;
        }
    }
}