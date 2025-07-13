package com.pbl.component;

import com.pbl.form.CalendarForm;
import com.pbl.form.MainForm;
import com.pbl.service.TaskService;
import com.pbl.swing.DayLabel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Calendar component with themed colors.
 */
public class Calendar extends JPanel {

    private MainForm mainForm;
    private int userId;

    public Calendar(int year, int month, LocalDate selectedDay, MainForm mainForm, JPanel parentPanel, int userId) {
        this.mainForm = mainForm;
        this.userId = userId;

        // Setup panel
        setPreferredSize(new Dimension(380, 380));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 0));
        setBackground(Color.decode("#E5E7E9"));  // Neutral background

        // Top panel: month/year navigation
        JPanel top = new JPanel(new BorderLayout());
        top.setPreferredSize(new Dimension(350, 50));
        top.setBackground(null);

        JLabel dateLabel = new JLabel(LocalDate.of(year, month, 1)
                .format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        dateLabel.setHorizontalAlignment(JLabel.CENTER);
        dateLabel.setFont(new Font("Helvetica", Font.BOLD, 25));
        dateLabel.setForeground(Color.decode("#1ABC9C"));  // Secondary color
        top.add(dateLabel, BorderLayout.CENTER);

        JLabel left = new JLabel(new ImageIcon(getClass().getResource("/com/pbl/icon/arrow-left.png")));
        left.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        left.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                Calendar prev = (month > 1)
                    ? new Calendar(year, month - 1, selectedDay, mainForm, parentPanel, userId)
                    : new Calendar(year - 1, 12, selectedDay, mainForm, parentPanel, userId);
                resetMainPanel(parentPanel, selectedDay, prev);
            }
        });
        top.add(left, BorderLayout.WEST);

        JLabel right = new JLabel(new ImageIcon(getClass().getResource("/com/pbl/icon/arrow-right.png")));
        right.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        right.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                Calendar next = (month < 12)
                    ? new Calendar(year, month + 1, selectedDay, mainForm, parentPanel, userId)
                    : new Calendar(year + 1, 1, selectedDay, mainForm, parentPanel, userId);
                resetMainPanel(parentPanel, selectedDay, next);
            }
        });
        top.add(right, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        // Days grid
        JPanel days = new JPanel(new GridLayout(7, 7));
        days.setBackground(null);

        // Weekday headers
        Color headerBg = Color.decode("#3498DB");  // Primary
        for (String d : new String[]{"S","M","T","W","T","F","S"}) {
            days.add(new DayLabel(d, headerBg, Color.WHITE, false));
        }

        // Empty slots before 1st
        String[] weekDays = {"SUNDAY","MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"};
        LocalDate firstDay = LocalDate.of(year, month, 1);
        int offset = 0;
        while (!firstDay.getDayOfWeek().toString().equals(weekDays[offset])) {
            days.add(new DayLabel("", Color.decode("#E5E7E9"), Color.BLACK, false));
            offset++;
        }

        int daysNum = YearMonth.of(year, month).lengthOfMonth();
        LocalDate today = LocalDate.now();
        TaskService taskService = new TaskService();

        // Render each day
        for (int i = 1; i <= daysNum; i++) {
            final int day = i;
            LocalDate current = LocalDate.of(year, month, i);
            boolean hasTasks = taskService.hasTaskOnDate(
                    current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), userId);

            // Default neutral cell
            Color bg = Color.decode("#F5F7FA");
            Color fg = Color.BLACK;

            // Selected day (accent)
            if (current.equals(selectedDay)) {
                bg = Color.decode("#E74C3C");  fg = Color.WHITE;
            }
            // Today highlight
            else if (current.equals(today)) {
                bg = Color.decode("#F1C40F");  fg = Color.BLACK;
            }
            // Task indicator
            else if (hasTasks) {
                bg = Color.decode("#2ECC71");  fg = Color.WHITE;
            }

            DayLabel dl = new DayLabel(String.valueOf(i), bg, fg, true);
            dl.addMouseListener(new MouseAdapter() {
                private Timer timer;
                @Override public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        if (timer != null && timer.isRunning()) timer.stop();
                        LocalDate sel = LocalDate.of(year, month, day);
                        parentPanel.removeAll();
                        if (parentPanel instanceof CalendarForm) ((CalendarForm)parentPanel).updateTasks(sel);
                        parentPanel.add(new DaySchedule(mainForm, sel, userId),
                                new GridBagConstraints());
                        parentPanel.revalidate(); parentPanel.repaint();
                    } else {
                        timer = new Timer(200, new ActionListener() {
                            @Override public void actionPerformed(ActionEvent ae) {
                                LocalDate sel = LocalDate.of(year, month, day);
                                if (parentPanel instanceof CalendarForm) ((CalendarForm)parentPanel).updateTasks(sel);
                            }
                        });
                        timer.setRepeats(false); timer.start();
                    }
                }
            });
            days.add(dl);
        }

        // Fill trailing blanks
        int blanks = 42 - (offset + daysNum);
        for (int b = 0; b < blanks; b++) {
            days.add(new DayLabel("", Color.decode("#E5E7E9"), Color.BLACK, false));
        }
        add(days, BorderLayout.CENTER);
    }

    private static void resetMainPanel(JPanel panel, LocalDate day, Calendar cal) {
        for (Component c : panel.getComponents()) {
            if (c instanceof Calendar) { panel.remove(c); break; }
        }
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 1; gbc.weighty = 1; gbc.fill = GridBagConstraints.BOTH;
        panel.add(cal, gbc);
        panel.revalidate(); panel.repaint();
    }
}
