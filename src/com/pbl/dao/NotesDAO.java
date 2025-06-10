/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pbl.dao;

import com.pbl.model.Note;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public interface NotesDAO {

    Note getNoteByUserAndDate(int userId, String date);

    // 2. Lấy ra tất cả ghi chú của userId (nếu cần list)
    List<Note> getAllNotesByUser(int userId);

    // 3. Tạo mới ghi chú (trả về số dòng thay đổi hoặc note_id vừa sinh)
    int insertNote(Note note);

    // 4. Cập nhật ghi chú (theo noteId, hoặc theo userId+date)
    void updateNote(Note note);

    // 5. Xóa ghi chú (nếu cần)
    void deleteNoteById(int noteId);

    void saveOrUpdateNote(int userId, String date, String note);
}
