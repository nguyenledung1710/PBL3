
package com.pbl.model;


public class Track {
    private int id;
    private String title;
    private String filePath; 
    
    public Track(int id, String title, String filePath) {
        this.id       = id;
        this.title    = title;
        this.filePath = filePath;
    }
    public int getId()
            { return id; }
    public String getTitle()
            { return title; }
    public String getFilePath()   
            { return filePath; }
    public static final String[] DEFAULT_TONE_PATHS = {
    "com/pbl/track/alarm.mp3",
    "com/pbl/track/alarm-327234.mp3",
    "com/pbl/track/germany-eas-alarm-1945-242750.mp3",
};
    
    public static final String[] DEFAULT_TONE_TITLES = {
    "alarm1", "alarm2", "alarm3"
};
    @Override
        public String toString()
            { return title; }
}
