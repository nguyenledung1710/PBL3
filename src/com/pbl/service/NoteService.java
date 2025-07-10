package com.pbl.service;

import com.pbl.dao.NotesDAO;
import com.pbl.dao.NotesDAOImp;
import com.pbl.model.Note;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NoteService {
    private NotesDAO notesDAO;

    public NoteService() {
        this.notesDAO = new NotesDAOImp();
    }

    // 1. Lấy 1 note của userId vào ngày date
    public Note getNoteByUserAndDate(int userId, LocalDate date) {
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return notesDAO.getNoteByUserAndDate(userId, dateStr);
    }

    // 2. Lấy tất cả note của user
    public List<Note> getAllNotesByUser(int userId) {
        return notesDAO.getAllNotesByUser(userId);
    }

    // 3. Tạo mới ghi chú, trả về noteId
    public int createNote(int userId, LocalDate date, String content) {
        Note note = new Note(userId, date, content);
        return notesDAO.insertNote(note);
    }

    // 4. Cập nhật ghi chú (theo noteId)
    public void updateNote(int noteId, int userId, LocalDate date, String content) {
        Note note = new Note(noteId, userId, date, content);
        notesDAO.updateNote(note);
    }

    // 5. Xóa ghi chú
    public void deleteNote(int noteId) {
        notesDAO.deleteNoteById(noteId);
    }
      public void saveOrUpdateNote(int userId, LocalDate date, String noteContent) {
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        notesDAO.saveOrUpdateNote(userId, dateStr, noteContent);
    }
}
