
package com.pbl.model;

import java.time.LocalDateTime;


public class Takenote {
     private int id;
    private int userId;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public Takenote() {}

    /**
     * @param id        Khóa chính (auto-generated)
     * @param userId    Khóa ngoại tham chiếu user
     * @param title     Tiêu đề ghi chú
     * @param content   Nội dung ghi chú
     * @param createdAt Thời điểm tạo (có thể null khi insert)
     */
    public Takenote(int id, int userId, String title, String content, LocalDateTime createdAt) {
        this.id        = id;
        this.userId    = userId;
        this.title     = title;
        this.content   = content;
        this.createdAt = createdAt;
    }

    // === Getters & Setters ===
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        // Hiển thị trong JList sẽ gọi toString(), nên trả về title
        return title;
    }
}
