
package com.pbl.dao;
import com.pbl.model.Song;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class songDAO {
    public static List<Song> loadSongs() {
        String sql = "SELECT id, title, file_path FROM songs";
        List<Song> list = new ArrayList<>();
        try (ResultSet rs = DBHelper.getInstance().getRecords(sql)) {
            while (rs.next()) {
                list.add(new Song(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("file_path")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
      public boolean deleteById(int id) throws SQLException {
        String sql = "DELETE FROM songs WHERE id = ?";
        int rows = DBHelper.getInstance().executeUpdate(sql, id);
        return rows > 0;
    }
    
    public static void insertSong(int userId, String title, String path) {
        String sql = "INSERT INTO songs(user_id, title, file_path) VALUES(?, ?, ?)";
        try {
            DBHelper.getInstance().executeUpdate(sql, userId, title, path);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
