package com.nextmove.notification_service.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitRetryHandler {

    private final RabbitTemplate rabbitTemplate;

    private static final int MAX_ATTEMPTS = 3;

    /**
     * Conta quantas vezes a mensagem já foi tentada.
     */
    @SuppressWarnings("unchecked")
    public int getRetryCount(Message message, String queueName) {
        Object xDeathHeader = message.getMessageProperties().getHeaders().get("x-death");

        if (!(xDeathHeader instanceof List<?> xDeathList)) {
            return 0;
        }

        return xDeathList.stream()
                .filter(item -> item instanceof Map)
                .map(item -> (Map<String, Object>) item)
                .filter(death -> queueName.equals(death.get("queue")))
                .map(death -> (Long) death.getOrDefault("count", 0L))
                .mapToInt(Long::intValue)
                .sum();
    }

    /**
     * Trata erro, envia para DLQ se atingiu limite, ou relança exceção para Retry.
     */
    public void handleError(
            Object event,
            Exception ex,
            int retryCount,
            String dlqExchange,
            String dlqRoutingKey,
            String context,
            String identifier
    ) {
        log.error("❌ [{}] Erro ao processar {}: {}", context, identifier, ex.getMessage(), ex);

        if (retryCount >= MAX_ATTEMPTS) {
            log.warn("🚫 [{}] Máximo de tentativas atingido para {}. Enviando para DLQ.", context, identifier);
            rabbitTemplate.convertAndSend(dlqExchange, dlqRoutingKey, event);
        } else {
            log.warn("⏳ [{}] Tentativa {} falhou. Vai para Retry.", context, retryCount + 1);
            throw (ex instanceof RuntimeException) ? (RuntimeException) ex
                    : new RuntimeException(ex);
        }
    }
}