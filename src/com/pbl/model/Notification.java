
package com.pbl.model;

import java.time.LocalDateTime;
import java.util.Objects; // Thêm để dùng trong equals và hashCode

public class Notification {
    private int notificationId;
    private int userId;
    private int notificationTime;
    private String notificationTimeText; // Định dạng "HH:MM"

    // Constructor mặc định
    public Notification() {
    }

    // Constructor với các tham số
    public Notification(int userId, int notificationTime, String notificationTimeText) {
        this.userId = userId;
        this.notificationTime = notificationTime;
        this.notificationTimeText = notificationTimeText;
    }

    // Getters
    public int getNotificationId() {
        return notificationId;
    }

    public int getUserId() {
        return userId;
    }

    public int getNotificationTime() {
        return notificationTime;
    }

    public String getNotificationTimeText() {
        return notificationTimeText;
    }

    // Setters
    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setNotificationTime(int notificationTime) {
        this.notificationTime = notificationTime;
    }

    public void setNotificationTimeText(String notificationTimeText) {
        this.notificationTimeText = notificationTimeText;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", userId=" + userId +
                ", notificationTime=" + notificationTime +
                ", notificationTimeText='" + notificationTimeText + '\'' +
                '}';
    }

    // Nên có equals và hashCode nếu bạn dùng các đối tượng này trong Collection
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return notificationId == that.notificationId &&
               userId == that.userId &&
               Objects.equals(notificationTime, that.notificationTime) &&
               Objects.equals(notificationTimeText, that.notificationTimeText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationId, userId, notificationTime, notificationTimeText);
    }
}
