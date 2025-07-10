package com.pbl.form;

import com.pbl.event.EventMenuSelected; // nếu cần
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

public class Calendar extends JPanel {

    private MainForm mainForm; 
    private int userId;        
    // Sửa constructor để nhận thêm userId
    public Calendar(int year, int month, LocalDate selectedDay, MainForm mainForm, JPanel parentPanel, int userId) {
        this.mainForm = mainForm;
        this.userId = userId;

        setPreferredSize(new Dimension(380, 380));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 0));
        setBackground(null);

        // Panel trên cùng chứa tháng/năm và mũi tên chuyển
        JPanel top = new JPanel(new BorderLayout());
        top.setPreferredSize(new Dimension(350, 50));
        top.setBackground(null);

        JLabel dateLabel = new JLabel(LocalDate.of(year, month, 1)
                .format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        dateLabel.setHorizontalAlignment(JLabel.CENTER);
        dateLabel.setFont(new Font("Helvetica", Font.BOLD, 25));
        dateLabel.setForeground(Color.decode("#c1380a"));
        top.add(dateLabel, BorderLayout.CENTER);

        JLabel left = new JLabel(new ImageIcon(getClass().getResource("/com/pbl/icon/arrow-left.png")));
        left.setCursor(new Cursor(Cursor.HAND_CURSOR));
        left.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (month != 1) {
                    resetMainPanel(parentPanel, selectedDay, new Calendar(year, month - 1, selectedDay, mainForm, parentPanel, userId));
                } else {
                    resetMainPanel(parentPanel, selectedDay, new Calendar(year - 1, 12, selectedDay, mainForm, parentPanel, userId));
                }
            }
        });
        top.add(left, BorderLayout.WEST);

        JLabel right = new JLabel(new ImageIcon(getClass().getResource("/com/pbl/icon/arrow-right.png")));
        right.setCursor(new Cursor(Cursor.HAND_CURSOR));
        right.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (month != 12) {
                    resetMainPanel(parentPanel, selectedDay, new Calendar(year, month + 1, selectedDay, mainForm, parentPanel, userId));
                } else {
                    resetMainPanel(parentPanel, selectedDay, new Calendar(year + 1, 1, selectedDay, mainForm, parentPanel, userId));
                }
            }
        });
        top.add(right, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        JPanel days = new JPanel(new GridLayout(7, 7));
        days.setBackground(null);

        Color header = Color.decode("#66CCFF");
        days.add(new DayLabel("S", header, Color.WHITE, false));
        days.add(new DayLabel("M", header, Color.WHITE, false));
        days.add(new DayLabel("T", header, Color.WHITE, false));
        days.add(new DayLabel("W", header, Color.WHITE, false));
        days.add(new DayLabel("T", header, Color.WHITE, false));
        days.add(new DayLabel("F", header, Color.WHITE, false));
        days.add(new DayLabel("S", header, Color.WHITE, false));

        String[] weekDays = {"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
        LocalDate firstDay = LocalDate.of(year, month, 1);
        int j = 0;
        while (!firstDay.getDayOfWeek().toString().equals(weekDays[j])) {
            days.add(new DayLabel("", Color.decode("#e3deca"), Color.BLACK, false));
            j++;
        }

        int daysNum = YearMonth.of(year, month).lengthOfMonth();
        LocalDate today = LocalDate.now();
        TaskService taskService = new TaskService();

        for (int i = 1; i <= daysNum; i++) {
            final int day = i;
            LocalDate currentDay = LocalDate.of(year, month, i);
            String formattedDay = currentDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            // Sửa: truyền userId khi gọi hasTaskOnDate
            boolean hasTasks = taskService.hasTaskOnDate(formattedDay, userId);
            Color bgColor = Color.decode("#e3deca");
            Color textColor = Color.BLACK;
            if (hasTasks) {
                bgColor = Color.decode("#FFCC00");
            }
            if (today.equals(currentDay)) {
                bgColor = Color.decode("#D3D3D3");
                textColor = Color.WHITE;
            }
            DayLabel dayLabel = new DayLabel(i + "", bgColor, textColor, true);

            dayLabel.addMouseListener(new MouseAdapter() {
                private Timer timer;
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        if (timer != null && timer.isRunning()) {
                            timer.stop();
                        }
                        LocalDate selected = LocalDate.of(year, month, day);
                        parentPanel.removeAll();
                        if (parentPanel instanceof Form2) {
                            ((Form2) parentPanel).updateTasks(selected);
                        }
                        DaySchedule daySchedule = new DaySchedule(mainForm, selected, userId);
                        parentPanel.add(daySchedule, new GridBagConstraints());
                        parentPanel.revalidate();
                        parentPanel.repaint();
                    } else if (e.getClickCount() == 1) {
                        timer = new Timer(200, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                LocalDate selected = LocalDate.of(year, month, day);
                                if (parentPanel instanceof Form2) {
                                    ((Form2) parentPanel).updateTasks(selected);
                                }
                            }
                        });
                        timer.setRepeats(false);
                        timer.start();
                    }
                }
            });
            days.add(dayLabel);
        }
        for (int i = 0; i < (42 - (j + daysNum)); i++) {
            days.add(new DayLabel("", Color.decode("#e3deca"), Color.BLACK, false));
        }
        add(days, BorderLayout.CENTER);
    }

    private static void resetMainPanel(JPanel mainPanel, LocalDate selectedDay, Calendar newCalendar) {
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof Calendar) {
                mainPanel.remove(comp);
                break;
            }
        }
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        mainPanel.add(newCalendar, constraints);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}