package com.nextmove.notification_service.service;

import com.nextmove.notification_service.repository.EmailHistoryRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

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

    @Captor
    private ArgumentCaptor<MimeMessage> messageCaptor;

    @Test
    void shouldSendWelcomeEmail() throws Exception {
        // Arrange
        String to = "eduardo@test.com";
        String name = "Eduardo";
        String expectedHtml = "<html>Bem-vindo, Eduardo!</html>";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("welcome"), any(Context.class))).thenReturn(expectedHtml);

        ReflectionTestUtils.setField(emailService, "from", "no-reply@nextmove.com");

        // Act
        emailService.sendWelcomeEmail(to, name);

        // Assert
        verify(templateEngine).process(eq("welcome"), any(Context.class));
        verify(mailSender).send(any(MimeMessage.class));
        verify(repository).save(argThat(emailHistory ->
                emailHistory.getTo().equals(to) &&
                        emailHistory.getSubject().equals("🎉 Bem-vindo ao NextMove!") &&
                        emailHistory.getContent().equals(expectedHtml) &&
                        emailHistory.getStatus().equals("SUCCESS")
        ));
    }
}