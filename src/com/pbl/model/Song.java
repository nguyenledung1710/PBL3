
package com.pbl.model;


public class Song {
     private int id;
    private String title;
    private String filePath;

    public Song(int id, String title, String filePath) {
        this.id       = id;
        this.title    = title;
        this.filePath = filePath;
    }
    public int getId()         { return id; }
    public String getTitle()   { return title; }
    public String getFilePath(){ return filePath; }
    
    public static final String[] DEFAULT_SONG_PATHS = {
    "com/pbl/music/AnhNangCuaAnhLive-DucPhuc-5912152.mp3",
    "com/pbl/music/ChamDayNoiDau.mp3",
    "com/pbl/music/CoEmCho-MINMrA-4928094.mp3",
};
    
    public static final String[] DEFAULT_SONG_TITLES = {
    "AnhNangCuaAnh", "ChamDayNoiDau", "CoEmCho"
};
    @Override
    public String toString()   { return title; }
    
}
