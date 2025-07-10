
package com.pbl.service;

import com.pbl.model.Takenote;
import com.pbl.dao.DBHelper;
import com.pbl.dao.TakeNoteDAO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class TakeNoteService {
     private final TakeNoteDAO dao = new TakeNoteDAO();

    public List<Takenote> loadAll(int userId) {
        return dao.findAllByUser(userId);
    }

    public void add(Takenote note) {
        dao.insert(note);
    }

    public void update(Takenote note) {
        dao.update(note);
    }

    public void delete(int noteId) {
        dao.delete(noteId);
    }
    /** Search notes by title keyword */
    public List<Takenote> search(int userId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return loadAll(userId);
        }
        return dao.findByTitleLike(userId, keyword.trim());
    }
}
