package com.pbl.dao;

import com.pbl.model.Note;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotesDAOImp implements NotesDAO {
    private final DBHelper dbHelper;

    public NotesDAOImp() {
        this.dbHelper = DBHelper.getInstance();
    }

    // 1. Lấy ghi chú của userId vào ngày date
    @Override
    public Note getNoteByUserAndDate(int userId, String date) {
        String sql = "SELECT note_id, user_id, date, note "
                   + "FROM note "
                   + "WHERE user_id = ? AND date = ?";

        try (ResultSet rs = dbHelper.getRecords(sql, userId, date)) {
            if (rs.next()) {
                int noteId    = rs.getInt("note_id");
                int uId       = rs.getInt("user_id");
                LocalDate d   = LocalDate.parse(rs.getString("date"),
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String content= rs.getString("note");
                return new Note(noteId, uId, d, content);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // 2. Lấy tất cả ghi chú của userId (nếu cần)
    @Override
    public List<Note> getAllNotesByUser(int userId) {
        List<Note> list = new ArrayList<>();
        String sql = "SELECT note_id, user_id, date, note "
                   + "FROM note WHERE user_id = ? "
                   + "ORDER BY date DESC";

        try (ResultSet rs = dbHelper.getRecords(sql, userId)) {
            while (rs.next()) {
                int noteId    = rs.getInt("note_id");
                int uId       = rs.getInt("user_id");
                LocalDate d   = LocalDate.parse(rs.getString("date"),
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String content= rs.getString("note");
                list.add(new Note(noteId, uId, d, content));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // 3. Tạo mới ghi chú (trả về note_id vừa tạo)
    @Override
    public int insertNote(Note note) {
        String sql = "INSERT INTO note (user_id, date, note) VALUES (?, ?, ?)";
        int generatedId = -1;
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, note.getUserId());
            ps.setString(2, note.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            ps.setString(3, note.getNote());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return generatedId;
    }

    // 4. Cập nhật ghi chú (theo noteId)
    @Override
    public void updateNote(Note note) {
        String sql = "UPDATE note "
                   + "SET date = ?, note = ? "
                   + "WHERE note_id = ? AND user_id = ?";
        try {
            dbHelper.executeUpdate(
                sql,
                note.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                note.getNote(),
                note.getNoteId(),
                note.getUserId()
            );
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // 5. Xóa ghi chú (theo noteId)
    @Override
    public void deleteNoteById(int noteId) {
        String sql = "DELETE FROM note WHERE note_id = ?";
        try {
            dbHelper.executeUpdate(sql, noteId);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void saveOrUpdateNote(int userId, String date, String note) {
          String sql = "INSERT INTO note (user_id, date, note) VALUES (?, ?, ?) "
                   + "ON DUPLICATE KEY UPDATE note = VALUES(note)";
        try {
            dbHelper.executeUpdate(sql, userId, date, note);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
