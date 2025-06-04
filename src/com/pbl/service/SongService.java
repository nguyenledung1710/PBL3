
package com.pbl.service;

import com.pbl.dao.songDAO;
import com.pbl.model.Song;
import java.util.List;


public class SongService {
     private final songDAO songDAO = new songDAO();

    public boolean deleteSong(int id) throws Exception {
        return songDAO.deleteById(id);
    }

    public List<Song> getAllSongs() throws Exception {
        return songDAO.loadSongs();
    }
    public void addSong(int userId, String title, String path) throws Exception {
    songDAO.insertSong(userId, title, path);
}
}
