package com.library.scheduler;

import com.library.entity.BorrowRecord;
import com.library.entity.BorrowRecord.BorrowStatus;
import com.library.repository.BorrowRecordRepository;
import com.library.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Scheduled tasks that run once a day to manage overdue books and send email notifications:
 *   1. 3-day pre-due reminder  → notifies users whose book is due in exactly 3 days.
 *   2. Overdue detection       → marks records OVERDUE and sends a fine-warning email.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OverdueReminderScheduler {

    private final BorrowRecordRepository borrowRecordRepository;
    private final EmailService            emailService;

    // ─────────────────────────────────────────────────────────────────────────
    // Task 1 – Pre-Due Reminder (3 days before due date)
    // Runs every day at 07:00 AM
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Finds all BORROWED records whose due date is exactly 3 days from today
     * and sends each user a reminder email warning about the ₹20/day fine.
     */
    @Scheduled(cron = "0 0 7 * * *")
    @Async
    @Transactional
    public void checkAndNotifyPreDue() {
        LocalDate targetDate = LocalDate.now().plusDays(3);
        log.info("═══ Pre-Due Reminder Scheduler started – checking for due date: {} ═══", targetDate);

        List<BorrowRecord> preDueRecords = borrowRecordRepository.findRecordsDueOn(targetDate);

        if (preDueRecords.isEmpty()) {
            log.info("No records due on {} found.", targetDate);
            return;
        }

        for (BorrowRecord record : preDueRecords) {
            try {
                emailService.sendPreDueReminder(record);
            } catch (Exception e) {
                log.warn("Could not send pre-due reminder to '{}': {}",
                        record.getUser().getEmail(), e.getMessage());
            }
        }

        log.info("Pre-due reminder sent for {} record(s) due on {}.",
                preDueRecords.size(), targetDate);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Task 2 – Overdue Detection + Fine Warning Email
    // Runs every day at 08:00 AM
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Finds all BORROWED records past their due date, marks them OVERDUE in the DB,
     * and sends each user an overdue notice with the total fine accrued so far.
     */
    @Scheduled(cron = "0 0 8 * * *")
    @Async
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

            // Send real overdue email notification
            try {
                emailService.sendOverdueReminder(record);
            } catch (Exception e) {
                log.warn("Could not send overdue reminder to '{}': {}",
                        record.getUser().getEmail(), e.getMessage());
            }
        }

        log.info("Processed {} overdue record(s).", overdueRecords.size());
    }
}
