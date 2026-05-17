package com.library.service;

import com.library.entity.BorrowRecord;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Handles all outgoing email notifications for the Library Management System.
 * All methods are @Async so email sending never blocks the main request thread.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${library.fine.per-day:20}")
    private int finePerDay;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    // ─────────────────────────────────────────────────────────────────────────
    // 1. Borrow Confirmation Email
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Sent immediately when a user successfully borrows a book.
     */
    @Async
    public void sendBorrowConfirmation(BorrowRecord record) {
        String toEmail  = record.getUser().getEmail();
        String fullName = record.getUser().getFullName();
        String bookTitle = record.getBook().getTitle();
        String borrowDate = record.getBorrowDate().format(DATE_FMT);
        String dueDate    = record.getDueDate().format(DATE_FMT);

        String subject = "📚 Book Borrowed: " + bookTitle;
        String body = buildBorrowConfirmationHtml(fullName, bookTitle, borrowDate, dueDate);

        sendHtmlEmail(toEmail, subject, body);
        log.info("✅ Borrow confirmation email sent to '{}' for book '{}'", toEmail, bookTitle);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. Pre-Due Reminder Email (3 days before)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Sent 3 days before the due date as a friendly reminder.
     */
    @Async
    public void sendPreDueReminder(BorrowRecord record) {
        String toEmail   = record.getUser().getEmail();
        String fullName  = record.getUser().getFullName();
        String bookTitle = record.getBook().getTitle();
        String dueDate   = record.getDueDate().format(DATE_FMT);

        String subject = "⏰ Return Reminder: \"" + bookTitle + "\" is due in 3 days!";
        String body = buildPreDueReminderHtml(fullName, bookTitle, dueDate, finePerDay);

        sendHtmlEmail(toEmail, subject, body);
        log.info("⏰ Pre-due reminder email sent to '{}' for book '{}' (due: {})", toEmail, bookTitle, dueDate);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. Overdue Notice Email
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Sent daily for every record that is already past its due date.
     */
    @Async
    public void sendOverdueReminder(BorrowRecord record) {
        String toEmail   = record.getUser().getEmail();
        String fullName  = record.getUser().getFullName();
        String bookTitle = record.getBook().getTitle();
        String dueDate   = record.getDueDate().format(DATE_FMT);

        long daysLate = LocalDate.now().toEpochDay() - record.getDueDate().toEpochDay();
        long totalFine = daysLate * finePerDay;

        String subject = "🚨 OVERDUE: \"" + bookTitle + "\" – Fine Accruing (₹" + totalFine + ")";
        String body = buildOverdueReminderHtml(fullName, bookTitle, dueDate, daysLate, totalFine, finePerDay);

        sendHtmlEmail(toEmail, subject, body);
        log.warn("🚨 Overdue email sent to '{}' for book '{}' ({} days late, fine=₹{})",
                toEmail, bookTitle, daysLate, totalFine);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Internal helper
    // ─────────────────────────────────────────────────────────────────────────

    private void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, "Library Management System");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML
            mailSender.send(message);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            log.error("❌ Failed to send email to '{}': {}", to, e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HTML Email Templates
    // ─────────────────────────────────────────────────────────────────────────

    private String buildBorrowConfirmationHtml(String name, String book,
                                                String borrowDate, String dueDate) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="margin:0;padding:0;font-family:'Segoe UI',Arial,sans-serif;background:#f4f6f9;">
              <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f6f9;padding:30px 0;">
                <tr><td align="center">
                  <table width="600" cellpadding="0" cellspacing="0"
                         style="background:#ffffff;border-radius:12px;overflow:hidden;
                                box-shadow:0 4px 20px rgba(0,0,0,0.08);">
                    <!-- Header -->
                    <tr>
                      <td style="background:linear-gradient(135deg,#1a73e8,#0d47a1);
                                 padding:36px 40px;text-align:center;">
                        <h1 style="color:#ffffff;margin:0;font-size:26px;">📚 Book Borrowed Successfully!</h1>
                        <p style="color:#bbdefb;margin:8px 0 0;">Library Management System</p>
                      </td>
                    </tr>
                    <!-- Body -->
                    <tr>
                      <td style="padding:36px 40px;">
                        <p style="font-size:16px;color:#333;">Dear <strong>%s</strong>,</p>
                        <p style="font-size:15px;color:#555;line-height:1.6;">
                          Your book has been successfully issued. Please find the details below:
                        </p>
                        <!-- Book Detail Box -->
                        <table width="100%%" style="background:#f0f7ff;border-radius:8px;
                                                    border-left:4px solid #1a73e8;margin:20px 0;">
                          <tr>
                            <td style="padding:20px 24px;">
                              <p style="margin:0 0 10px;font-size:15px;color:#333;">
                                📖 <strong>Book Title:</strong> %s
                              </p>
                              <p style="margin:0 0 10px;font-size:15px;color:#333;">
                                📅 <strong>Borrow Date:</strong> %s
                              </p>
                              <p style="margin:0;font-size:15px;color:#d32f2f;">
                                ⏳ <strong>Due Date:</strong> %s
                              </p>
                            </td>
                          </tr>
                        </table>
                        <p style="font-size:14px;color:#777;line-height:1.6;">
                          ⚠️ Please return the book by the due date to avoid a fine of
                          <strong>₹%d per day</strong>.
                        </p>
                        <p style="font-size:14px;color:#555;">Happy Reading! 😊</p>
                      </td>
                    </tr>
                    <!-- Footer -->
                    <tr>
                      <td style="background:#f9f9f9;padding:20px 40px;
                                 border-top:1px solid #eee;text-align:center;">
                        <p style="font-size:12px;color:#aaa;margin:0;">
                          This is an automated email from Library Management System.
                          Please do not reply to this email.
                        </p>
                      </td>
                    </tr>
                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(name, book, borrowDate, dueDate, finePerDay);
    }

    private String buildPreDueReminderHtml(String name, String book,
                                            String dueDate, int fine) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="margin:0;padding:0;font-family:'Segoe UI',Arial,sans-serif;background:#f4f6f9;">
              <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f6f9;padding:30px 0;">
                <tr><td align="center">
                  <table width="600" cellpadding="0" cellspacing="0"
                         style="background:#ffffff;border-radius:12px;overflow:hidden;
                                box-shadow:0 4px 20px rgba(0,0,0,0.08);">
                    <!-- Header -->
                    <tr>
                      <td style="background:linear-gradient(135deg,#f59e0b,#d97706);
                                 padding:36px 40px;text-align:center;">
                        <h1 style="color:#ffffff;margin:0;font-size:26px;">⏰ Return Reminder</h1>
                        <p style="color:#fef3c7;margin:8px 0 0;">Your book is due in 3 days!</p>
                      </td>
                    </tr>
                    <!-- Body -->
                    <tr>
                      <td style="padding:36px 40px;">
                        <p style="font-size:16px;color:#333;">Dear <strong>%s</strong>,</p>
                        <p style="font-size:15px;color:#555;line-height:1.6;">
                          This is a friendly reminder that the following book is due to be returned
                          in <strong>3 days</strong>:
                        </p>
                        <!-- Book Detail Box -->
                        <table width="100%%" style="background:#fffbeb;border-radius:8px;
                                                    border-left:4px solid #f59e0b;margin:20px 0;">
                          <tr>
                            <td style="padding:20px 24px;">
                              <p style="margin:0 0 10px;font-size:15px;color:#333;">
                                📖 <strong>Book Title:</strong> %s
                              </p>
                              <p style="margin:0;font-size:15px;color:#d32f2f;">
                                📅 <strong>Due Date:</strong> %s
                              </p>
                            </td>
                          </tr>
                        </table>
                        <!-- Warning Box -->
                        <table width="100%%" style="background:#fff3cd;border-radius:8px;
                                                    border:1px solid #ffc107;margin:16px 0;">
                          <tr>
                            <td style="padding:16px 20px;">
                              <p style="margin:0;font-size:14px;color:#856404;">
                                ⚠️ <strong>Important:</strong> If the book is not returned by the due date,
                                a fine of <strong>₹%d per day</strong> will be charged.
                                Please return it as early as possible!
                              </p>
                            </td>
                          </tr>
                        </table>
                        <p style="font-size:14px;color:#555;">Thank you for being a valued member! 🙏</p>
                      </td>
                    </tr>
                    <!-- Footer -->
                    <tr>
                      <td style="background:#f9f9f9;padding:20px 40px;
                                 border-top:1px solid #eee;text-align:center;">
                        <p style="font-size:12px;color:#aaa;margin:0;">
                          This is an automated email from Library Management System.
                          Please do not reply to this email.
                        </p>
                      </td>
                    </tr>
                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(name, book, dueDate, fine);
    }

    private String buildOverdueReminderHtml(String name, String book,
                                             String dueDate, long daysLate,
                                             long totalFine, int finePerDay) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="margin:0;padding:0;font-family:'Segoe UI',Arial,sans-serif;background:#f4f6f9;">
              <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f6f9;padding:30px 0;">
                <tr><td align="center">
                  <table width="600" cellpadding="0" cellspacing="0"
                         style="background:#ffffff;border-radius:12px;overflow:hidden;
                                box-shadow:0 4px 20px rgba(0,0,0,0.08);">
                    <!-- Header -->
                    <tr>
                      <td style="background:linear-gradient(135deg,#dc2626,#991b1b);
                                 padding:36px 40px;text-align:center;">
                        <h1 style="color:#ffffff;margin:0;font-size:26px;">🚨 Book Overdue Notice</h1>
                        <p style="color:#fecaca;margin:8px 0 0;">Immediate action required</p>
                      </td>
                    </tr>
                    <!-- Body -->
                    <tr>
                      <td style="padding:36px 40px;">
                        <p style="font-size:16px;color:#333;">Dear <strong>%s</strong>,</p>
                        <p style="font-size:15px;color:#555;line-height:1.6;">
                          Your borrowed book is <strong style="color:#dc2626;">overdue</strong>.
                          Please return it to the library as soon as possible.
                        </p>
                        <!-- Book Detail Box -->
                        <table width="100%%" style="background:#fff5f5;border-radius:8px;
                                                    border-left:4px solid #dc2626;margin:20px 0;">
                          <tr>
                            <td style="padding:20px 24px;">
                              <p style="margin:0 0 10px;font-size:15px;color:#333;">
                                📖 <strong>Book Title:</strong> %s
                              </p>
                              <p style="margin:0 0 10px;font-size:15px;color:#d32f2f;">
                                📅 <strong>Was Due On:</strong> %s
                              </p>
                              <p style="margin:0 0 10px;font-size:15px;color:#333;">
                                📆 <strong>Days Overdue:</strong> %d day(s)
                              </p>
                              <p style="margin:0;font-size:16px;color:#dc2626;font-weight:bold;">
                                💸 Total Fine: ₹%d (₹%d × %d days)
                              </p>
                            </td>
                          </tr>
                        </table>
                        <!-- Urgent Warning -->
                        <table width="100%%" style="background:#fee2e2;border-radius:8px;
                                                    border:1px solid #fca5a5;margin:16px 0;">
                          <tr>
                            <td style="padding:16px 20px;">
                              <p style="margin:0;font-size:14px;color:#7f1d1d;">
                                🔴 <strong>Action Required:</strong> Return the book immediately to stop
                                the fine from increasing. Fine will continue to accrue at
                                <strong>₹%d per day</strong> until the book is returned.
                              </p>
                            </td>
                          </tr>
                        </table>
                        <p style="font-size:14px;color:#555;">
                          Please visit the library at your earliest convenience.
                        </p>
                      </td>
                    </tr>
                    <!-- Footer -->
                    <tr>
                      <td style="background:#f9f9f9;padding:20px 40px;
                                 border-top:1px solid #eee;text-align:center;">
                        <p style="font-size:12px;color:#aaa;margin:0;">
                          This is an automated email from Library Management System.
                          Please do not reply to this email.
                        </p>
                      </td>
                    </tr>
                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(name, book, dueDate, daysLate, totalFine, finePerDay, daysLate, finePerDay);
    }
}
