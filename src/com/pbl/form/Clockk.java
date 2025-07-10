package com.pbl.form;

import com.pbl.dao.TrackDAO;
import com.pbl.dao.songDAO;
import com.pbl.model.Song;
import static com.pbl.model.Song.DEFAULT_SONG_PATHS;
import static com.pbl.model.Song.DEFAULT_SONG_TITLES;
import com.pbl.model.Track;
import static com.pbl.model.Track.DEFAULT_TONE_PATHS;
import static com.pbl.model.Track.DEFAULT_TONE_TITLES;
import com.pbl.service.SongService;
import com.pbl.service.TrackService;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Clockk extends javax.swing.JPanel implements Runnable {

    private final TrackService trackService = new TrackService();
    private final SongService songService = new SongService();

    String sound, title;
    private FileInputStream alarmInputStream;
    private BufferedInputStream alarmBufferedInputStream;
    Player alarmPlayer;
    long all;

    private String customSoundPath;
    private String customTitle;
    private InputStream musicInputStream;
    private BufferedInputStream musicBufferedInputStream;
    private Player musicPlayer;
    private long musicTotalSongLength;
    private long musicPausedOnFrame;
    private boolean musicIsPaused = false;

    String hours, hh, mm;
    String hourAlarm, minuteAlarm;
    private volatile boolean alarmCanceled = false;

    private JButton jBListen;
    private JButton jBRingtone;
    private JButton jBSetAlarm;
    private JButton jBCancelAlarm;
    private JButton jButtonSetSong;
    private JButton jButtonPause;
    private JComboBox<String> jComboBox1;
    private JComboBox<String> jComboBox2;
    private JLabel jLabelTitle;
    private JLabel jLabelClock;
    private JLabel jLabelHour;
    private JLabel jLabelMinute;
    private JLabel jLabelToneIcon;
    private JLabel jLabelStatus;
    private JLabel jLabelMusicIcon;
    private JLabel jLabelSongName;
    private JComboBox<Track> cboTone;
    private JComboBox<Song> cboSong;
    private JButton jBAddSong;
    private JButton jBAddTone;
    private int currentUserId;
    private final ImageIcon checkMarkIcon;
    private final ImageIcon checkedIcon;
    private JButton butXoaTone;
    private JButton butXoaSong;

    public Clockk(int userID) {
        checkMarkIcon = new ImageIcon(getClass().getResource("/com/pbl/icon/check-mark.png"));
        checkedIcon = new ImageIcon(getClass().getResource("/com/pbl/icon/checked.png"));
        URL url = getClass().getResource("/com/pbl/icon/alarm.png");

        currentUserId = userID;
        buildGUI();
        Thread t = new Thread(this);
        t.start();
        loadIcons();
        Calendar c = Calendar.getInstance();
        hh = new SimpleDateFormat("HH").format(c.getTime());
        mm = new SimpleDateFormat("mm").format(c.getTime());
        jComboBox1.setSelectedItem(hh);
        jComboBox2.setSelectedItem(mm);

        jBListen.setEnabled(false);
        jBCancelAlarm.setEnabled(false);
    }

    private void loadIcons() {
        jLabelToneIcon.setIcon(new ImageIcon(getClass().getResource("/com/pbl/icon/bell (1).png")));
        jLabelStatus.setIcon(checkMarkIcon);
        jLabelMusicIcon.setIcon(new ImageIcon(getClass().getResource("/com/pbl/icon/listen.png")));
        jLabelTitle.setIcon(new ImageIcon(getClass().getResource("/com/pbl/icon/alarm.png")));
    }

    private void buildGUI() {
        Color ivory = new Color(255, 255, 255);

        setLayout(new BorderLayout(20, 20));
        setBackground(ivory);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        header.setBackground(ivory);
        jLabelTitle = new JLabel("ALARM CLOCK", SwingConstants.CENTER);
        jLabelTitle.setFont(new Font("Segoe UI", Font.BOLD, 44));
        header.add(jLabelTitle);
        add(header, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        mainPanel.setOpaque(true);
        mainPanel.setBackground(ivory);

        // Alarm panel
        JPanel alarmPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        alarmPanel.setOpaque(true);
        alarmPanel.setBackground(ivory);
        TitledBorder alarmBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(50, 50, 50), 2),
                "Alarm", TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 38), new Color(0, 100, 0)
        );
        alarmPanel.setBorder(alarmBorder);

        // Clock display
        jLabelClock = new JLabel("00:00:00", SwingConstants.CENTER);
        jLabelClock.setFont(new Font("Segoe UI", Font.BOLD, 32)); // in đậm
        jLabelClock.setOpaque(true);
        jLabelClock.setBackground(new Color(255, 239, 213));
        alarmPanel.add(jLabelClock);

        // Spinner giờ và phút
        JPanel hm = new JPanel(new FlowLayout(FlowLayout.LEFT, 130, 0));
        hm.setOpaque(true);
        hm.setBackground(ivory);

        int shiftLeftInPx = 50;
        hm.setBorder(BorderFactory.createEmptyBorder(0, shiftLeftInPx, 0, 0));

        jLabelHour = new JLabel("Hour", SwingConstants.CENTER);
        jLabelHour.setFont(new Font("Segoe UI", Font.BOLD, 28));

        // Không còn dòng: jLabelHour.setBorder(...)
        jLabelMinute = new JLabel("Minute", SwingConstants.CENTER);
        jLabelMinute.setFont(new Font("Segoe UI", Font.BOLD, 28));

        hm.add(jLabelHour);
        hm.add(jLabelMinute);

        // Sau đó add hm vào panel cao hơn
        alarmPanel.add(hm);

        alarmPanel.add(hm);

        JPanel combo = new JPanel(new FlowLayout(FlowLayout.CENTER, 100, 10));
        jComboBox1 = new JComboBox<>(generateArray(24));
        jComboBox1.setFont(new Font("Segoe UI", Font.BOLD, 20));
        jComboBox1.setPreferredSize(new Dimension(110, 40));

        jComboBox2 = new JComboBox<>(generateArray(60));
        jComboBox2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        jComboBox2.setPreferredSize(new Dimension(110, 40));
        combo.add(jComboBox1);
        combo.add(jComboBox2);
        combo.setBackground(ivory);
        alarmPanel.add(combo);

        // Tone selection
        JPanel tone = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        tone.setOpaque(true);
        tone.setBackground(ivory);

        jBAddTone = new JButton("AddTone");
        jBAddTone.setMargin(new Insets(2, 10, 2, 10));
        jBAddTone.setPreferredSize(new Dimension(115, 35));
        jBAddTone.setFont(new Font("Segoe UI", Font.BOLD, 14));

        jBAddTone.setBackground(new Color(0, 123, 253));    // Xanh dương nổi bật
        jBAddTone.setFocusPainted(false);
        jBAddTone.setBorder(BorderFactory.createLineBorder(new Color(200, 35, 50), 1));
        butXoaTone = new JButton("Delete");
        butXoaTone.setMargin(new Insets(2, 10, 2, 10));
        butXoaTone.setPreferredSize(new Dimension(110, 35));
        butXoaTone.setFont(new Font("Segoe UI", Font.BOLD, 14));
        butXoaTone.setBackground(new Color(220, 53, 69));   // Đỏ tươi
        butXoaTone.setFocusPainted(false);
        butXoaTone.setBorder(BorderFactory.createLineBorder(new Color(200, 35, 50), 1));

        jLabelToneIcon = new JLabel();

        cboTone = new JComboBox<>();
        cboTone.setPreferredSize(new Dimension(250, 40));
        cboTone.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cboTone.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                return lbl;
            }
        });

        loadCombo(cboTone);

        tone.add(jLabelToneIcon);
        tone.add(cboTone);
        tone.add(jBAddTone);
        tone.add(butXoaTone);

        alarmPanel.add(tone);

        // Buttons row 1: Set Ring và Listen
        JPanel ringPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 10));
        ringPanel.setOpaque(true);
        ringPanel.setBackground(ivory);
        jBRingtone = new JButton("Set Ring");
        jBRingtone.setFont(new Font("Segoe UI", Font.BOLD, 24));
        jBRingtone.setBackground(new Color(144, 238, 144));
        jBRingtone.setForeground(Color.DARK_GRAY);
        jBListen = new JButton("Listen");
        jBListen.setFont(new Font("Segoe UI", Font.BOLD, 24));
        jBListen.setBackground(new Color(255, 215, 0));
        jBListen.setForeground(Color.DARK_GRAY);
        ringPanel.add(jBRingtone);
        ringPanel.add(jBListen);
        alarmPanel.add(ringPanel);

        // Buttons row 2: Set Alarm, Status icon, Cancel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.setOpaque(true);
        controlPanel.setBackground(ivory);
        jBSetAlarm = new JButton("Set Alarm");
        jBSetAlarm.setFont(new Font("Segoe UI", Font.BOLD, 24));
        jBSetAlarm.setBackground(new Color(255, 182, 193)); // hồng nhạt
        jBSetAlarm.setForeground(Color.DARK_GRAY);
        jLabelStatus = new JLabel();
        jBCancelAlarm = new JButton("Cancel");
        jBCancelAlarm.setFont(new Font("Segoe UI", Font.BOLD, 24));
        jBCancelAlarm.setBackground(new Color(211, 211, 211)); // xám
        jBCancelAlarm.setForeground(Color.DARK_GRAY);
        controlPanel.add(jBSetAlarm);
        controlPanel.add(jLabelStatus);
        controlPanel.add(jBCancelAlarm);
        alarmPanel.add(controlPanel);

        mainPanel.add(alarmPanel);

        // Music panel
        JPanel musicPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        musicPanel.setOpaque(true);
        musicPanel.setBackground(ivory);
        TitledBorder musicBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(50, 50, 50), 2),
                "Music Player", TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 36), new Color(0, 100, 0)
        );
        musicPanel.setBorder(musicBorder);

        JPanel toppMusic = new JPanel(new GridLayout(2, 1, 10, 10));
        toppMusic.setBackground(ivory);
        JPanel topMusic = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topMusic.setOpaque(true);
        topMusic.setBackground(ivory);
        jLabelMusicIcon = new JLabel();
        cboSong = new JComboBox<>();
        cboSong.setPreferredSize(new Dimension(300, 37));
        cboSong.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cboSong.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                return lbl;
            }
        });
        loadSongCombo(cboSong);

        jLabelMusicIcon = new JLabel();
        jLabelSongName = new JLabel();
        topMusic.add(jLabelMusicIcon);
        topMusic.add(cboSong);
        toppMusic.add(topMusic);
        musicPanel.add(toppMusic);

        JPanel button = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        button.setBackground(ivory);
        jBAddSong = new JButton("Add Song");
        jBAddSong.setMargin(new Insets(2, 10, 2, 10));
        jBAddSong.setPreferredSize(new Dimension(120, 35));
        jBAddSong.setFont(new Font("Segoe UI", Font.BOLD, 14));

// Mình gợi ý xài teal để khác đôi chút so với “Add Tone” vẫn cùng hệ xanh:
        jBAddSong.setBackground(new Color(23, 162, 184));  // Teal (xanh biển)
        jBAddSong.setFocusPainted(false);
        jBAddSong.setBorder(BorderFactory.createLineBorder(new Color(20, 130, 160), 1));
        jBAddSong.addActionListener(e -> onAddSong());

        butXoaSong = new JButton("Delete");
        butXoaSong.setMargin(new Insets(2, 10, 2, 10));
        butXoaSong.setPreferredSize(new Dimension(110, 35));
        butXoaSong.setFont(new Font("Segoe UI", Font.BOLD, 14));
        butXoaSong.setBackground(new Color(220, 53, 69));   // Đỏ tươi
        butXoaSong.setFocusPainted(false);
        butXoaSong.setBorder(BorderFactory.createLineBorder(new Color(200, 35, 50), 1));
        button.add(jBAddSong);
        button.add(butXoaSong);

        toppMusic.add(button);

        JPanel set2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        JLabel musicon = new JLabel();
        ImageIcon icon = new ImageIcon(getClass().getResource("/com/pbl/icon/headphones (1).png"));
        musicon.setIcon(icon);
        set2.add(musicon);
        set2.setBackground(ivory);
        musicPanel.add(set2);

        JPanel musicButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        musicButtons.setOpaque(true);
        musicButtons.setBackground(ivory);
        jButtonSetSong = new JButton("Set Song");
        jButtonSetSong.setFont(new Font("Segoe UI", Font.BOLD, 24));
        jButtonSetSong.setBackground(new Color(135, 206, 235));
        jButtonSetSong.setForeground(Color.DARK_GRAY);
        jButtonPause = new JButton("Pause");
        jButtonPause.setFont(new Font("Segoe UI", Font.BOLD, 24));
        jButtonPause.setBackground(new Color(255, 160, 122));

        Dimension btnSize = new Dimension(160, 45);
        jButtonSetSong.setPreferredSize(btnSize);
        jButtonSetSong.setMinimumSize(btnSize);
        jButtonSetSong.setMaximumSize(btnSize);

        jButtonPause.setPreferredSize(btnSize);
        jButtonPause.setMinimumSize(btnSize);
        jButtonPause.setMaximumSize(btnSize);

        musicButtons.add(jButtonSetSong);
        musicButtons.add(jButtonPause);
        musicPanel.add(musicButtons);
        mainPanel.add(musicPanel);

        add(mainPanel, BorderLayout.CENTER);

        // --- Listeners ---
        jBListen.addActionListener(e -> {
            if (alarmPlayer != null) {
                alarmPlayer.close();
                alarmPlayer = null;
                jBListen.setText("Listen");
            } else if (sound != null) {
                playTone(sound);
                jBListen.setText("Stop");
            }
        });
        jBAddTone.addActionListener(e -> onAddTone());
        jBSetAlarm.addActionListener(e -> setAlarm());
        jBCancelAlarm.addActionListener(e -> cancelAlarm());
        jButtonPause.addActionListener(e -> {
            if (musicPlayer != null) {
                pauseSong();   // dừng
                jButtonPause.setText("Continue");
            } else {
                continueSong();
                jButtonPause.setText("Pause");
            }
        });
        jBRingtone.addActionListener(e -> chooseToneFromCombo());
        jButtonSetSong.addActionListener(e -> chooseSongFromCombo());

        cboTone.addActionListener(e -> updateDeleteToneButtonState());
        butXoaTone.addActionListener(e -> deleteTone());
        cboSong.addActionListener(e -> updateDeleteSongButtonState());
        butXoaSong.addActionListener(e -> deleteSong());

    }

    private void loadCombo(JComboBox<Track> cb) {
        DefaultComboBoxModel<Track> model = new DefaultComboBoxModel<>();

        for (int i = 0; i < DEFAULT_TONE_PATHS.length; i++) {
            model.addElement(new Track(
                    -1,
                    DEFAULT_TONE_TITLES[i],
                    "RES:" + DEFAULT_TONE_PATHS[i]
            ));
        }
        try {
            List<Track> userList = trackService.getAllTracks();
            userList.forEach(model::addElement);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Lỗi khi load Tones:\n" + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
        cb.setModel(model);
        if (model.getSize() > 0) {
            cb.setSelectedIndex(0);
        }
    }

    private void loadSongCombo(JComboBox<Song> cb) {
        DefaultComboBoxModel<Song> model = new DefaultComboBoxModel<>();

        for (int i = 0; i < DEFAULT_SONG_PATHS.length; i++) {
            model.addElement(new Song(
                    -1,
                    DEFAULT_SONG_TITLES[i],
                    "RES:" + DEFAULT_SONG_PATHS[i]
            ));
        }

        try {
            List<Song> userList = songService.getAllSongs();
            userList.forEach(model::addElement);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Lỗi khi load Songs:\n" + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        cb.setModel(model);
        if (model.getSize() > 0) {
            cb.setSelectedIndex(0);
        }
    }

    private void chooseToneFromCombo() {
        Track t = (Track) cboTone.getSelectedItem();
        if (t == null) {
            return;
        }
        sound = t.getFilePath();
        jBListen.setEnabled(true);
        jBListen.setText("Listen");
    }

    private void chooseSongFromCombo() {
        Song s = (Song) cboSong.getSelectedItem();
        if (s == null) {
            return;
        }
        customSoundPath = s.getFilePath();
        customTitle = s.getTitle();
        jLabelSongName.setText(customTitle);
        playSong(customSoundPath);
    }

    private String[] generateArray(int n) {
        String[] arr = new String[n];
        for (int i = 0; i < n; i++) {
            arr[i] = String.format("%02d", i);
        }
        return arr;
    }

    private void startAlarm() throws Exception {
        InputStream in;
        if (sound.startsWith("RES:")) {
            String res = sound.substring(4);
            in = getClass().getClassLoader()
                    .getResourceAsStream(res);
            if (in == null) {
                throw new FileNotFoundException(res);
            }
        } else {
            in = new FileInputStream(sound);
        }
        BufferedInputStream buf = new BufferedInputStream(in);
        alarmPlayer = new Player(buf);
        new Thread(() -> {
            try {
                alarmPlayer.play();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public void stopAlarm() {
        if (alarmPlayer != null) {
            alarmPlayer.close();
        }
    }

    private void alarmTime(Calendar alarmDateTime) {
        Calendar now = Calendar.getInstance();

        long diffMillis = alarmDateTime.getTimeInMillis() - now.getTimeInMillis();
        if (diffMillis < 0) {
            return; 
        }
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(diffMillis);
            } catch (InterruptedException ex) {
                return; 
            }
            SwingUtilities.invokeLater(() -> {
                
                if (sound != null) {
                    playTone(sound);
                }

                JOptionPane.showMessageDialog(
                        Clockk.this,
                        "ĐÃ ĐẾN GIỜ HẸN!",
                        "Báo thức",
                        JOptionPane.INFORMATION_MESSAGE
                );
                if (alarmPlayer != null) {
                    alarmPlayer.close();
                    alarmPlayer = null;
                }

                jLabelStatus.setIcon(checkMarkIcon);
                jBCancelAlarm.setEnabled(false);
            });
        });

        t.start();
    }

    private void pauseSong() {
        try {
            musicPausedOnFrame = musicBufferedInputStream.available();
            if (musicPlayer != null) {
                musicPlayer.close();
            }
            musicPlayer = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void continueSong() {
        if (customSoundPath == null) {
            return;
        }
        new Thread(() -> {
            try {
                InputStream base;
                String fp = customSoundPath;
                if (fp.startsWith("RES:")) {
                    String res = fp.substring(4);
                    base = getClass().getClassLoader().getResourceAsStream(res);
                    if (base == null) {
                        throw new FileNotFoundException("Resource không tìm thấy: " + res);
                    }
                } else {
                    base = new FileInputStream(fp);
                }
                long toSkip = musicTotalSongLength - musicPausedOnFrame;
                base.skip(toSkip);

                musicBufferedInputStream = new BufferedInputStream(base);
                if (musicPlayer != null) {
                    musicPlayer.close();
                }
                musicPlayer = new Player(musicBufferedInputStream);

                musicPlayer.play();

            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(()
                        -> JOptionPane.showMessageDialog(
                                this,
                                "Lỗi tiếp tục nhạc: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        )
                );
            }
        }).start();
    }

    private void playSong(String fp) {
        new Thread(() -> {
            try {
                if (fp.startsWith("RES:")) {
                    String resPath = fp.substring(4);
                    musicInputStream = getClass()
                            .getClassLoader()
                            .getResourceAsStream(resPath);
                    if (musicInputStream == null) {
                        throw new FileNotFoundException("Không tìm thấy resource: " + resPath);
                    }
                } else {
                    musicInputStream = new FileInputStream(fp);
                }
                musicTotalSongLength = musicInputStream.available();

                musicBufferedInputStream = new BufferedInputStream(musicInputStream);
                if (musicPlayer != null) {
                    musicPlayer.close();
                }
                musicPlayer = new Player(musicBufferedInputStream);

                musicPlayer.play();
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(()
                        -> JOptionPane.showMessageDialog(this,
                                "Lỗi phát nhạc: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE)
                );
            }
        }).start();
    }

    private void playTone(String fp) {
        new Thread(() -> {
            try {
                InputStream in;
                if (fp.startsWith("RES:")) {
                    String resPath = fp.substring(4);
                    in = getClass().getClassLoader()
                            .getResourceAsStream(resPath);
                    if (in == null) {
                        throw new FileNotFoundException("Không tìm thấy resource: " + resPath);
                    }
                } else {
                    in = new FileInputStream(fp);
                }

                BufferedInputStream buf = new BufferedInputStream(in);
                if (alarmPlayer != null) {
                    alarmPlayer.close();
                }
                alarmPlayer = new Player(buf);
                alarmPlayer.play();

            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(()
                        -> JOptionPane.showMessageDialog(
                                this,
                                "Lỗi phát chuông: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        )
                );
            }
        }).start();
    }

    private void toggleListen() {
        if (jButtonPause.getText().equals("Pause")) {
            pauseSong();
            jButtonPause.setText("Continue");
        } else {
            continueSong();
            jButtonPause.setText("Pause");
        }
    }

    private void setAlarm() {
        int h = Integer.parseInt((String) jComboBox1.getSelectedItem());
        int m = Integer.parseInt((String) jComboBox2.getSelectedItem());

        Calendar now = Calendar.getInstance();
        Calendar alarm = Calendar.getInstance();
        alarm.set(Calendar.HOUR_OF_DAY, h);
        alarm.set(Calendar.MINUTE, m);
        alarm.set(Calendar.SECOND, 0);
        alarm.set(Calendar.MILLISECOND, 0);

        if (!alarm.after(now)) {
            alarm.add(Calendar.DAY_OF_MONTH, 1);
        }

        long diffMillis = alarm.getTimeInMillis() - now.getTimeInMillis();
        long totalSeconds = diffMillis / 1000;          
        long diffHours = totalSeconds / 3600;                
        long diffMinutes = (totalSeconds % 3600) / 60;     
        long diffSeconds = totalSeconds % 60;                

        String message = String.format(
                "%d giờ %d phút %d giây nữa đồng hồ sẽ reo!",
                diffHours,
                diffMinutes,
                diffSeconds
        );
        JOptionPane.showMessageDialog(this, message);

        alarmTime(alarm);

        jLabelStatus.setIcon(checkedIcon);
        jBCancelAlarm.setEnabled(true);
    }

    private void cancelAlarm() {
        alarmCanceled = true;
        stopAlarm();
        jLabelStatus.setIcon(checkMarkIcon);
        jBCancelAlarm.setEnabled(false);
    }

    private void setSong() {
        JFileChooser jfc = new JFileChooser();
        if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = jfc.getSelectedFile();
            customSoundPath = f.getAbsolutePath();
            customTitle = f.getName();
            jLabelSongName.setText(customTitle);
            playSong(customSoundPath);
        }
    }

    private void toggleSong() {
        if (jButtonPause.getText().equals("Pause")) {
            pauseSong();
            jButtonPause.setText("Continue");
        } else {
            continueSong();
            jButtonPause.setText("Pause");
        }
    }

// Thêm Tone (nhạc nền)
    private void onAddTone() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileNameExtensionFilter("WAV/MP3 Files", "wav", "mp3"));
        chooser.setDialogTitle("Chọn file âm thanh để thêm vào Tones");

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            String title = f.getName();
            String path = f.getAbsolutePath();
            try {
                trackService.addTrack(currentUserId, title, path);
                loadCombo(cboTone);
                cboTone.setSelectedIndex(cboTone.getItemCount() - 1);
                JOptionPane.showMessageDialog(
                        this,
                        "Đã thêm tone: " + title,
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Lỗi khi thêm tone:\n" + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

// Thêm Song (bài hát)
    private void onAddSong() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileNameExtensionFilter("MP3 Files", "mp3"));
        chooser.setDialogTitle("Chọn file MP3 để thêm vào Songs");

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            String title = f.getName();
            String path = f.getAbsolutePath();
            try {
                songService.addSong(currentUserId, title, path);
                loadSongCombo(cboSong);
                cboSong.setSelectedIndex(cboSong.getItemCount() - 1);
                JOptionPane.showMessageDialog(
                        this,
                        "Đã thêm bài: " + title,
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Lỗi khi thêm bài hát:\n" + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void deleteTone() {
        Track t = (Track) cboTone.getSelectedItem();
        if (t != null && t.getId() != -1
                && JOptionPane.showConfirmDialog(this,
                        "Xác nhận xóa chuông \"" + t.getTitle() + "\"?",
                        "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                if (trackService.deleteTrack(t.getId())) {
                    loadCombo(cboTone);
                    updateDeleteToneButtonState();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa không thành công.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        }
    }

    public void deleteSong() {
        Song s = (Song) cboSong.getSelectedItem();
        if (s != null && s.getId() != -1
                && JOptionPane.showConfirmDialog(this,
                        "Xác nhận xóa bài \"" + s.getTitle() + "\"?",
                        "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                if (songService.deleteSong(s.getId())) {
                    loadSongCombo(cboSong);
                    updateDeleteSongButtonState();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa không thành công.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        }
    }

    private void updateDeleteToneButtonState() {
        Track t = (Track) cboTone.getSelectedItem();
        butXoaTone.setEnabled(t != null && t.getId() != -1);
    }

    private void updateDeleteSongButtonState() {
        Song s = (Song) cboSong.getSelectedItem();
        butXoaSong.setEnabled(s != null && s.getId() != -1);
    }

    public void run() {
        while (true) {
            String t = new SimpleDateFormat("HH:mm:ss").format(new Date());
            jLabelClock.setText(t);
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // </editor-fold>
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
