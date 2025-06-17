package com.nextmove.notification_service.repository;

import com.nextmove.notification_service.model.EmailHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailHistoryRepository extends MongoRepository<EmailHistory, String> {
}
