package com.nextmove.transaction_service.scheduler;

import com.nextmove.transaction_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionReminderScheduler {

    private final TransactionService transactionService;

    @Scheduled(cron = "0 0 9 * * *")
    public void sendTransactionReminders() {
        transactionService.sendRemindersForPendingTransactions();
    }

}
