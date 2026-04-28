package com.library.scheduler;

import com.library.entity.BorrowRecord;
import com.library.entity.BorrowRecord.BorrowStatus;
import com.library.repository.BorrowRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Scheduled task that runs once a day to:
 *   1. Detect overdue borrow records and mark them OVERDUE.
 *   2. Log reminder notifications for overdue users.
 *
 * In a real system, step 2 would send emails via JavaMailSender.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OverdueReminderScheduler {

    private final BorrowRecordRepository borrowRecordRepository;

    /**
     * Runs every day at 08:00 AM server time.
     * Cron format: second minute hour day-of-month month day-of-week
     */
    @Scheduled(cron = "0 0 8 * * *")
    @Async                           // Executes in the async thread pool, not main scheduler thread
    @Transactional
    public void checkAndNotifyOverdue() {
        log.info("═══ Overdue Reminder Scheduler started at {} ═══", LocalDate.now());

        List<BorrowRecord> overdueRecords =
            borrowRecordRepository.findOverdueRecords(LocalDate.now());

        if (overdueRecords.isEmpty()) {
            log.info("No overdue records found today.");
            return;
        }

        for (BorrowRecord record : overdueRecords) {
            // Mark as OVERDUE in DB
            record.setStatus(BorrowStatus.OVERDUE);
            borrowRecordRepository.save(record);

            // Send reminder (log here; replace with email in production)
            sendOverdueReminder(record);
        }

        log.info("Processed {} overdue record(s).", overdueRecords.size());
    }

    /**
     * Async reminder notification method.
     * Replace with actual email sending logic (e.g., JavaMailSender).
     */
    @Async
    public void sendOverdueReminder(BorrowRecord record) {
        log.warn("📧 OVERDUE REMINDER → User: '{}' | Book: '{}' | Due: {} | Days Late: {}",
            record.getUser().getUsername(),
            record.getBook().getTitle(),
            record.getDueDate(),
            LocalDate.now().toEpochDay() - record.getDueDate().toEpochDay());
        // TODO: JavaMailSender.send(...)
    }
}
