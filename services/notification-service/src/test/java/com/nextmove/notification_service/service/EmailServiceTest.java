package com.nextmove.notification_service.service;

import com.nextmove.notification_service.dto.TransactionReminderEvent;
import com.nextmove.notification_service.repository.EmailHistoryRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private EmailHistoryRepository repository;

    @InjectMocks
    private EmailService emailService;

    @Test
    void shouldSendWelcomeEmail() throws Exception {
        String to = "eduardo@test.com";
        String name = "Eduardo";
        String expectedHtml = "<html>Bem-vindo, Eduardo!</html>";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("welcome"), any(Context.class))).thenReturn(expectedHtml);

        ReflectionTestUtils.setField(emailService, "from", "no-reply@nextmove.com");

        emailService.sendWelcomeEmail(to, name);

        verify(templateEngine).process(eq("welcome"), any(Context.class));
        verify(mailSender).send(any(MimeMessage.class));
        verify(repository).save(argThat(emailHistory ->
                emailHistory.getTo().equals(to) &&
                        emailHistory.getSubject().equals("🎉 Bem-vindo ao NextMove!") &&
                        emailHistory.getContent().equals(expectedHtml) &&
                        emailHistory.getStatus().equals("SUCCESS")
        ));
    }

    @Test
    void shouldSendTransactionReminderEmail() throws Exception {
        TransactionReminderEvent event = new TransactionReminderEvent(
                "eduardo@test.com",
                "Eduardo",
                "Pagamento Fatura",
                "Fatura do cartão Nubank",
                LocalDate.now().plusDays(3),
                new BigDecimal("150.75"),
                "EXPENSE"
        );

        String expectedHtml = "<html>Lembrete de transação</html>";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("transaction-reminder"), any(Context.class))).thenReturn(expectedHtml);

        ReflectionTestUtils.setField(emailService, "from", "no-reply@nextmove.com");

        emailService.sendTransactionReminderEmail(event);

        verify(templateEngine).process(eq("transaction-reminder"), any(Context.class));
        verify(mailSender).send(any(MimeMessage.class));
        verify(repository).save(argThat(emailHistory ->
                emailHistory.getTo().equals(event.email()) &&
                        emailHistory.getSubject().equals("🔔 Lembrete de Transação no NextMove") &&
                        emailHistory.getContent().equals(expectedHtml) &&
                        emailHistory.getStatus().equals("SUCCESS")
        ));
    }

    @Test
    void shouldHandleErrorWhenSendingWelcomeEmail() {
        String to = "eduardo@test.com";
        String name = "Eduardo";
        String expectedHtml = "<html>Bem-vindo, Eduardo!</html>";

        when(templateEngine.process(eq("welcome"), any(Context.class))).thenReturn(expectedHtml);
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("SMTP error"));

        ReflectionTestUtils.setField(emailService, "from", "no-reply@nextmove.com");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendWelcomeEmail(to, name);
        });

        assertTrue(exception.getMessage().contains("Erro ao enviar e-mail"));

        verify(repository).save(argThat(emailHistory ->
                emailHistory.getTo().equals(to) &&
                        emailHistory.getSubject().equals("🎉 Bem-vindo ao NextMove!") &&
                        emailHistory.getStatus().equals("ERROR") &&
                        emailHistory.getErrorMessage().contains("SMTP error")
        ));
    }

    @Test
    void shouldHandleErrorWhenSendingTransactionReminderEmail() {
        TransactionReminderEvent event = new TransactionReminderEvent(
                "eduardo@test.com",
                "Eduardo",
                "Pagamento Fatura",
                "Fatura do cartão Nubank",
                LocalDate.now().plusDays(3),
                new BigDecimal("150.75"),
                "EXPENSE"
        );

        String expectedHtml = "<html>Lembrete de transação</html>";

        when(templateEngine.process(eq("transaction-reminder"), any(Context.class))).thenReturn(expectedHtml);
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("SMTP server down"));

        ReflectionTestUtils.setField(emailService, "from", "no-reply@nextmove.com");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendTransactionReminderEmail(event);
        });

        assertTrue(exception.getMessage().contains("Erro ao enviar e-mail"));

        verify(repository).save(argThat(emailHistory ->
                emailHistory.getTo().equals(event.email()) &&
                        emailHistory.getSubject().equals("🔔 Lembrete de Transação no NextMove") &&
                        emailHistory.getStatus().equals("ERROR") &&
                        emailHistory.getErrorMessage().contains("SMTP server down")
        ));
    }
}