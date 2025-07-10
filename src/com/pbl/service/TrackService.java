
package com.pbl.service;

import com.pbl.dao.TrackDAO;
import com.pbl.model.Track;
import java.util.List;


public class TrackService {
     private final TrackDAO trackDAO = new TrackDAO();

    public boolean deleteTrack(int id) throws Exception {
        return trackDAO.deleteById(id);
    }

    public List<Track> getAllTracks() throws Exception {
        return trackDAO.loadTracks();
    }
    
    public void addTrack(int userId, String title, String path) throws Exception {
    trackDAO.insertTrack(userId, title, path);
}
}
