
package com.pbl.dao;

import com.pbl.form.Clock;
import com.pbl.model.Track;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;


public class TrackDAO {
      public static List<Track> loadTracks() {
        String sql = "SELECT id, title, file_path FROM tracks";
        List<Track> list = new ArrayList<>();
        try (ResultSet rs = DBHelper.getInstance().getRecords(sql)) {
            while (rs.next()) {
                list.add(new Track(
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
        String sql = "DELETE FROM tracks WHERE id = ?";
        int rows = DBHelper.getInstance().executeUpdate(sql, id);
        return rows > 0;
    }
    
      
   public static void insertTrack(int userId, String title, String filePath) {
        String sql = "INSERT INTO tracks(user_id, title, file_path) VALUES(?, ?, ?)";
        try {
            DBHelper.getInstance().executeUpdate(sql,
                userId,   
                title,
                filePath);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
   
}
