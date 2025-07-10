
package com.pbl.service;


import com.pbl.model.Task;
import com.pbl.model.Users;
import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EmailNotificationService {

  //  private MailService mailService;
    private GmailService gmailService;
    private ScheduledExecutorService scheduler;
    private String cachedHostname;

    private static final DateTimeFormatter HH_MM_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter TASK_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm 'ngày' dd/MM/yyyy");

    private TaskService taskService; // TaskService của ứng dụng chính
    private Users us;
    public EmailNotificationService( Users user) {
        // Khởi tạo TaskService ở đây nếu nó là một phần của ứng dụng chính
        // và có thể được truy cập/khởi tạo từ đây.
        // Hoặc bạn có thể truyền TaskService vào qua constructor.
        us = user;
        this.taskService = new TaskService(); // Quan trọng: TaskService này phải truy cập CSDL/dữ liệu của ứng dụng bạn
      
    }

    public void start() {
        System.out.println("Starting EmailNotificationService for app: " + EmailConfig.APP_NAME);
        if (!isEmailConfigValid()) {
            System.err.println("EmailNotificationService cannot start due to invalid EmailConfig.");
            return;
        }
        cachedHostname = getHostnameInternal();
        try {
            
            gmailService = new GmailService();
            System.out.println("EmailNotificationService: GmailService initialized for " + EmailConfig.SENDER_EMAIL);

            scheduler = Executors.newScheduledThreadPool(2); // 1 luồng thường đủ cho việc kiểm tra email

            // Lên lịch tác vụ chính
            scheduleUpcomingEventNotifierTask();

            System.out.println("EmailNotificationService started and tasks scheduled.");
            
        } catch (Exception e) {
            System.err.println("FATAL: EmailNotificationService failed to start: " + e.getMessage());
            e.printStackTrace();
            // Có thể bạn muốn thông báo lỗi này trên UI của ứng dụng chính
        }
    }

    public void stop() {
        System.out.println("Stopping EmailNotificationService...");
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.err.println("EmailNotificationService: Scheduler did not terminate cleanly after 10s. Forcing shutdown...");
                    scheduler.shutdownNow();
                    if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                        System.err.println("EmailNotificationService: Scheduler did not terminate even after force shutdown.");
                    }
                } else {
                    System.out.println("EmailNotificationService: Scheduler terminated gracefully.");
                }
            } catch (InterruptedException ie) {
                System.err.println("EmailNotificationService: Shutdown hook interrupted. Forcing scheduler shutdown...");
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("EmailNotificationService stopped.");
    }

    private boolean isEmailConfigValid() {
        // (Logic của isEmailConfigValid từ Chay_Ngam_UD)
        if (EmailConfig.SENDER_EMAIL == null || EmailConfig.SENDER_EMAIL.startsWith("your_") || EmailConfig.SENDER_EMAIL.isEmpty() ||
            EmailConfig.SENDER_APP_PASSWORD == null || EmailConfig.SENDER_APP_PASSWORD.startsWith("your_") || EmailConfig.SENDER_APP_PASSWORD.isEmpty() ||
            us.getEmail() == null ||   us.getEmail().startsWith("your_") ||   us.getEmail().isEmpty()) {
            System.err.println("FATAL ERROR: SENDER_EMAIL, SENDER_APP_PASSWORD, or ADMIN_EMAIL is not configured correctly in EmailConfig.java.");
            return false;
        }
        return true;
    }

    private void scheduleUpcomingEventNotifierTask() {
        // Quan trọng: taskService ở đây phải là instance của TaskService
        // đã được khởi tạo và có khả năng truy cập vào CSDL thực của ứng dụng bạn.

        Runnable eventNotifierWorker = () -> {
            LocalDateTime serverNow = LocalDateTime.now();
            System.out.printf("%s - EmailNotificationService: Running upcoming event check for ALL users.%n", serverNow.format(HH_MM_FORMATTER));

            try {
                // Lấy TẤT CẢ các task sắp tới và chưa thông báo từ TẤT CẢ user
                // Phương thức này trong TaskService của bạn cần được triển khai để đọc từ CSDL.
                
                List<Task> tasksToNotify = taskService.getAllTasksByUser(us.getUser_id());

                if (tasksToNotify.isEmpty()) {
                    return;
                }
              
                for (Task task : tasksToNotify) {
                    LocalDateTime eventTime = task.getDateTime();
                  LocalDateTime notificationTargetTime;
                    switch (EmailConfig.Text_Setting) {
                        case "Phút":
                            notificationTargetTime = eventTime.minusMinutes(EmailConfig.MINUTES_BEFORE_EVENT_TO_NOTIFY);
                            break;
                          case "Tiếng":
                            notificationTargetTime = eventTime.minusHours(EmailConfig.MINUTES_BEFORE_EVENT_TO_NOTIFY);
                            break;
                           case "Ngày":
                            notificationTargetTime = eventTime.minusDays(EmailConfig.MINUTES_BEFORE_EVENT_TO_NOTIFY);
                            break;
                          case "Tuần":
                            notificationTargetTime = eventTime.minusWeeks(EmailConfig.MINUTES_BEFORE_EVENT_TO_NOTIFY);
                            break;
                        default:
                            throw new AssertionError();
                    }

                    if (!serverNow.isBefore(notificationTargetTime) && serverNow.isBefore(eventTime)) {
                        System.out.printf("INFO: Event '%s' (ID: %d, UserID: %d) is approaching. Scheduled at %s. Sending notification.%n",task.getTitle(), task.getID(), task.getUserId(), eventTime.format(TASK_DATETIME_FORMATTER));

                        String recipientEmail =us.getEmail();

                        if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
                            System.err.printf("WARNING: Task ID %d (UserID: %d) has no recipient email. Skipping notification.%n", task.getID(), task.getUserId());
                            continue;
                        }

                        String subject = String.format("[%s] Nhắc nhở: '%s' sắp diễn ra!", EmailConfig.APP_NAME, task.getTitle());
                        String body = String.format(
                            "Chào bạn (User ID: %d),\n\n" +
                            "Công việc/Sự kiện sau đây của bạn sắp diễn ra:\n\n" +
                            "Tiêu đề: %s\n" +
                            "Thời gian: %s\n" +
                            "Mô tả: %s\n\n" +
                            "Vui lòng chuẩn bị!\n\n" +
                            "---\n" +
                            "Thông báo được gửi lúc (giờ server): %s\n" +
                            "Hostname: %s",
                            task.getUserId(),
                            task.getTitle(),
                            eventTime.format(TASK_DATETIME_FORMATTER),
                            (task.getDescription() != null && !task.getDescription().isEmpty() ? task.getDescription() : "Không có mô tả."),
                            serverNow.format(TASK_DATETIME_FORMATTER),
                            this.cachedHostname // Sử dụng biến instance
                        );

                        try {
                            gmailService.sendEmail(recipientEmail, subject, body);

                            System.out.printf("SUCCESS: Notification email sent for task '%s' (ID: %d) to %s.%n",
                                              task.getTitle(), task.getID(), recipientEmail);

                            taskService.markTaskAsNotifiedForUpcoming(task.getID());  //đánh dấu task đã được thông báo

                        } catch (Exception e) {
                            System.err.printf("ERROR sending email for task ID %d (UserID: %d): %s%n", task.getID(), task.getUserId(), e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("ERROR during global upcoming event check cycle: " + e.getMessage());
                e.printStackTrace();
                // Cân nhắc gửi email lỗi cho admin nếu tác vụ này bị lỗi nghiêm trọng thường xuyên
            }
        };

        long initialDelay = 1; // Chạy lần đầu sau 1 phút
        long period = Math.max(1, EmailConfig.EVENT_CHECK_INTERVAL_MINUTES); // Đảm bảo period > 0
        scheduler.scheduleAtFixedRate(eventNotifierWorker, initialDelay, period, TimeUnit.MINUTES);
        System.out.printf("EmailNotificationService: Scheduled upcoming event notifier. Initial delay=%d min(s), Period=%d min(s).%n", initialDelay, period);
    }


    private void handleMessagingException(MessagingException e, String emailType) {
        // (Logic của handleMessagingException từ Chay_Ngam_UD)
        System.err.printf("ERROR: Failed to send %s (MessagingException): %s%n", emailType, e.getMessage());
        if (e instanceof AuthenticationFailedException) {
            System.err.println("!! AUTHENTICATION FAILED. Check SENDER_EMAIL and SENDER_APP_PASSWORD in EmailConfig.");
        }
    }

    private String getHostnameInternal() {
        // (Logic của getHostnameInternal từ Chay_Ngam_UD)
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            System.err.println("Warning: Could not determine hostname: " + e.getMessage());
            return "unknown_host";
        }
    }
}
