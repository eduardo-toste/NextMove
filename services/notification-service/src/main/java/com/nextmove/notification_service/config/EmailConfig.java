package com.nextmove.notification_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(GetEnv.get("MAIL_HOST"));
        sender.setPort(Integer.parseInt(GetEnv.get("MAIL_PORT")));
        sender.setUsername(GetEnv.get("MAIL_USERNAME"));
        sender.setPassword(GetEnv.get("MAIL_PASSWORD"));

        Properties props = sender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", GetEnv.get("MAIL_TLS_ENABLED"));

        return sender;
    }
}