package com.nextmove.notification_service.service;

import com.nextmove.notification_service.dto.TransactionReminderEvent;
import com.nextmove.notification_service.model.EmailHistory;
import com.nextmove.notification_service.repository.EmailHistoryRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final EmailHistoryRepository repository;

    @Value("${mail.username}")
    private String from;

    public void sendWelcomeEmail(String to, String name) {
        Context context = new Context();
        context.setVariable("nome", name);

        String htmlContent = templateEngine.process("welcome", context);

        sendEmail(
                to,
                "🎉 Bem-vindo ao NextMove!",
                htmlContent
        );
    }

    public void sendTransactionReminderEmail(TransactionReminderEvent event) {
        Context context = new Context();
        context.setVariable("nome", event.name());
        context.setVariable("title", event.title());
        context.setVariable("description", event.description());
        context.setVariable("dueDate", event.dueDate().toString());
        context.setVariable("amount", formatCurrency(event.amount()));
        context.setVariable("type", event.type().equalsIgnoreCase("INCOME") ? "Receita" : "Despesa");

        String htmlContent = templateEngine.process("transaction-reminder", context);

        sendEmail(
                event.email(),
                "🔔 Lembrete de Transação no NextMove",
                htmlContent
        );
    }

    private void sendEmail(String to, String subject, String htmlContent) {
        EmailHistory emailHistory = EmailHistory.builder()
                .to(to)
                .subject(subject)
                .content(htmlContent)
                .sentAt(LocalDateTime.now())
                .status("SUCCESS")
                .build();

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(from);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            repository.save(emailHistory);
        } catch (Exception e) {
            emailHistory.setStatus("ERROR");
            emailHistory.setErrorMessage(e.getMessage());
            emailHistory.setSentAt(LocalDateTime.now());

            repository.save(emailHistory);

            throw new RuntimeException("Erro ao enviar e-mail: " + subject, e);
        }
    }

    private String formatCurrency(java.math.BigDecimal value) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return formatter.format(value);
    }
}