
package com.pbl.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types; // Cần thiết cho việc đặt giá trị NULL
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.pbl.model.Notification;
public class NotificationDaoImpl implements NotificationDao {

     private DBHelper dbHelper;

    public NotificationDaoImpl() {
      dbHelper = DBHelper.getInstance();
    }

    @Override
    public boolean  addNotification(Notification notification) throws SQLException {
        String sql = "INSERT INTO notifications (user_id, notification_time, notification_time_text) VALUES (?, ?, ?)";
        try (Connection conn = dbHelper.getConnection(); // Lấy connection mới
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, notification.getUserId());

             pstmt.setInt(2, notification.getNotificationTime() );
     
            pstmt.setString(3, notification.getNotificationTimeText());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        notification.setNotificationId(generatedKeys.getInt(1));
                     return true;
                    }
                }
            }
          return false; 
        }
        // Không cần dbHelper.close() nếu dùng try-with-resources, vì nó tự đóng Connection và PreparedStatement
    }


    @Override
    public Notification getNotificationsByUserId(int userId) throws SQLException {
      Notification notification = null; // Khởi tạo là null
    // Thêm "LIMIT 1" để chỉ lấy một hàng (hàng đầu tiên sau khi sắp xếp)
    String sql = "SELECT notification_id, user_id, notification_time, notification_time_text FROM notifications WHERE user_id = ? ORDER BY notification_time DESC LIMIT 1";
    try (Connection conn = dbHelper.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, userId);
        try (ResultSet rs = pstmt.executeQuery()) {
            // Chỉ cần kiểm tra rs.next() một lần vì chúng ta chỉ mong đợi một kết quả
            if (rs.next()) {
                notification = mapRowToNotification(rs); // Gọi phương thức mapRowToNotification của bạn
            }
        }
    }
    return notification; // Trả về đối tượng Notification hoặc null
    }



    @Override
    public boolean  updateNotification(Notification notification) throws SQLException {
        String sql = "UPDATE notifications SET  notification_time = ?, notification_time_text = ? WHERE user_id = ?";
        try (Connection conn = dbHelper.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
          
            pstmt.setInt(1, notification.getNotificationTime() );
            pstmt.setString(2, notification.getNotificationTimeText());
            pstmt.setInt(3, notification.getUserId());

          return   pstmt.executeUpdate() > 0;
        }
    }

//    // Phương thức tiện ích để map một dòng ResultSet thành đối tượng Notification
    private Notification mapRowToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setNotificationId(rs.getInt("notification_id"));
        notification.setUserId(rs.getInt("user_id"));        
        notification.setNotificationTime(rs.getInt("notification_time"));
        notification.setNotificationTimeText(rs.getString("notification_time_text"));
        return notification;
    }

      public boolean doesUserHaveNotifications(int userId) throws SQLException {
        // Sử dụng COUNT(*) để hiệu quả hơn là lấy tất cả dữ liệu
        // Hoặc có thể dùng "SELECT 1 FROM notifications WHERE user_id = ? LIMIT 1"
        // và kiểm tra rs.next()
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ?";
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Nếu count > 0, nghĩa là userId tồn tại
                }
            }
        }
        return false; // Mặc định là không tìm thấy hoặc có lỗi (mặc dù lỗi sẽ ném SQLException)
    }
    
    @Override
    public Notification getNotificationById(int notificationId) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }



    @Override
    public List<Notification> getAllNotifications() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Notification> getNotificationsDue(LocalDateTime dueTime) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean deleteNotification(int notificationId) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}