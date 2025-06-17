package com.nextmove.notification_service.service;

import com.nextmove.notification_service.config.GetEnv;
import com.nextmove.notification_service.dto.TransactionReminderEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    private final String from = GetEnv.get("MAIL_USERNAME");

    public void sendWelcomeEmail(String to, String name) {
        Context context = new Context();
        context.setVariable("nome", name);

        String htmlContent = templateEngine.process("welcome", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("🎉 Bem-vindo ao NextMove!");
            helper.setFrom(from);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Send e-mail error", e);
        }
    }

    public void sendTransactionReminderEmail(TransactionReminderEvent event) {
        Context context = new Context();
        context.setVariable("nome", event.name());
        context.setVariable("title", event.title());
        context.setVariable("description", event.description());
        context.setVariable("dueDate", event.dueDate().toString());
        context.setVariable("amount", "R$ " + event.amount().setScale(2).toString().replace(".", ","));
        context.setVariable("type", event.type().equalsIgnoreCase("INCOME") ? "Receita" : "Despesa");

        String htmlContent = templateEngine.process("transaction-reminder", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(event.email());
            helper.setSubject("🔔 Lembrete de Transação no NextMove");
            helper.setFrom(from);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar e-mail de lembrete", e);
        }
    }
}
