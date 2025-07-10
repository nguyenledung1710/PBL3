
package com.pbl.dao;
import com.pbl.model.Takenote;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
public class TakeNoteDAO {
     private final DBHelper db = DBHelper.getInstance();

    // Fetch all notes for a user, newest first
    public List<Takenote> findAllByUser(int userId) {
        List<Takenote> list = new ArrayList<>();
        String sql = "SELECT id, user_id, title, content, created_at"
                   + " FROM takenotes WHERE user_id = ? ORDER BY created_at DESC";
        try (ResultSet rs = db.getRecords(sql, userId)) {
            while (rs.next()) {
                Takenote note = new Takenote();
                note.setId(rs.getInt("id"));
                note.setUserId(rs.getInt("user_id"));
                note.setTitle(rs.getString("title"));
                note.setContent(rs.getString("content"));
                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) {
                    note.setCreatedAt(ts.toLocalDateTime());
                }
                list.add(note);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching notes", e);
        }
        return list;
    }

    // Insert new note
    public int insert(Takenote note) {
        String sql = "INSERT INTO takenotes (user_id, title, content) VALUES (?, ?, ?)";
        try {
            return db.executeUpdate(sql,
                note.getUserId(),
                note.getTitle(),
                note.getContent()
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting note", e);
        }
    }

    // Update existing note
    public int update(Takenote note) {
        String sql = "UPDATE takenotes SET title = ?, content = ? WHERE id = ?";
        try {
            return db.executeUpdate(sql,
                note.getTitle(),
                note.getContent(),
                note.getId()
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error updating note", e);
        }
    }

    // Delete note by id
    public int delete(int noteId) {
        String sql = "DELETE FROM takenotes WHERE id = ?";
        try {
            return db.executeUpdate(sql, noteId);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting note", e);
        }
    }
    /**
     * Search notes by title keyword (case-insensitive)
     * @param userId the owner
     * @param keyword substring to search in title
     */
    public List<Takenote> findByTitleLike(int userId, String keyword) {
        String sql = "SELECT id, user_id, title, content, created_at"
                   + " FROM takenotes"
                   + " WHERE user_id=? AND lower(title) LIKE ?"
                   + " ORDER BY created_at DESC";
        List<Takenote> list = new ArrayList<>();
        try (ResultSet rs = db.getRecords(sql, userId, "%" + keyword.toLowerCase() + "%")) {
            while (rs.next()) {
                Takenote n = new Takenote();
                n.setId(rs.getInt("id"));
                n.setUserId(rs.getInt("user_id"));
                n.setTitle(rs.getString("title"));
                n.setContent(rs.getString("content"));
                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) n.setCreatedAt(ts.toLocalDateTime());
                list.add(n);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching notes", e);
        }
        return list;
    }
}
