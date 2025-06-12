package com.nextmove.notification_service.consumer;

import com.nextmove.notification_service.config.RabbitMQConfig;
import com.nextmove.notification_service.dto.UserCreatedEvent;
import com.nextmove.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCreatedListener {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.USER_CREATED_QUEUE)
    public void onUserCreated(UserCreatedEvent event) {
        emailService.sendWelcomeEmail(event.email(), event.name());
    }
}
