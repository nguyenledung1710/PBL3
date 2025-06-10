package com.pbl.model;

import java.time.LocalDate;

public class Note {
    private int noteId;      // khóa chính mới (surrogate key)
    private int userId;      // khóa ngoại
    private LocalDate date;  // ngày của ghi chú
    private String note;     // nội dung

    public Note() { }

    // Constructor đầy đủ (thường dùng khi load từ DB)
    public Note(int noteId, int userId, LocalDate date, String note) {
        this.noteId = noteId;
        this.userId = userId;
        this.date   = date;
        this.note   = note;
    }

    // Constructor khi tạo mới (chưa có noteId, vì noteId auto_increment)
    public Note(int userId, LocalDate date, String note) {
        this.userId = userId;
        this.date   = date;
        this.note   = note;
    }

    // Getter / Setter
    public int getNoteId() {
        return noteId;
    }
    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Note{" +
                "noteId=" + noteId +
                ", userId=" + userId +
                ", date=" + date +
                ", note='" + note + '\'' +
                '}';
    }
}
