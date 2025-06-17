package com.nextmove.notification_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "email_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailHistory {

    @Id
    private String id;

    private String to;
    private String subject;
    private String content;

    private LocalDateTime sentAt;

    private String status;
    private String errorMessage;
}
