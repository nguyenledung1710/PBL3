
package com.pbl.dao;


import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import com.pbl.model.Notification;
public interface NotificationDao {


    boolean  addNotification(Notification notification) throws SQLException;

    /**
     * Lấy thông báo bằng ID.
     * @param notificationId ID của thông báo.
     * @return Đối tượng Notification hoặc null nếu không tìm thấy.
     * @throws SQLException
     */
    Notification getNotificationById(int notificationId) throws SQLException;

    /**
     * Lấy tất cả thông báo của một người dùng.
     * @param userId ID của người dùng.
     * @return Danh sách các Notification.
     * @throws SQLException
     */
    Notification getNotificationsByUserId(int userId) throws SQLException;

    /**
     * Lấy tất cả các thông báo.
     * @return Danh sách tất cả Notification.
     * @throws SQLException
     */
    List<Notification> getAllNotifications() throws SQLException;

    /**
     * Lấy các thông báo đến hạn (ví dụ: có notification_time <= thời gian hiện tại).
     * Bạn có thể điều chỉnh logic này cho phù hợp với yêu cầu "đến hạn".
     * @param dueTime Thời điểm để so sánh.
     * @return Danh sách các Notification.
     * @throws SQLException
     */
    List<Notification> getNotificationsDue(LocalDateTime dueTime) throws SQLException;

    /**
     * Cập nhật thông tin một thông báo.
     * @param notification Đối tượng Notification với thông tin đã cập nhật.
     * @return true nếu cập nhật thành công.
     * @throws SQLException
     */
    boolean  updateNotification(Notification notification) throws SQLException;

    /**
     * Xóa một thông báo bằng ID.
     * @param notificationId ID của thông báo cần xóa.
     * @return true nếu xóa thành công.
     * @throws SQLException
     */
    boolean deleteNotification(int notificationId) throws SQLException;
}
