package com.pbl.component;

import com.pbl.form.MainForm;
import com.pbl.model.Task;
import com.pbl.service.TaskService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Tasks extends JPanel {

    private final TaskService taskService;
    private final int userId;
    private final LocalDate date;
    private final MainForm mainForm;
    private final JPanel parentPanel;
    private JPanel listPanel;

    public Tasks(LocalDate date, MainForm mainForm, JPanel parentPanel, int userId) {
        this.date = date;
        this.mainForm = mainForm;
        this.parentPanel = parentPanel;
        this.userId = userId;
        this.taskService = new TaskService();

        setPreferredSize(new Dimension(400, 400));
        setLayout(new BorderLayout(10, 10));
        // Match calendar neutral background
        setBackground(Color.decode("#E5E7E9"));
        setBorder(BorderFactory.createEmptyBorder(20, 10, 15, 10));

        // Panel danh sách tasks
        listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(Color.decode("#E5E7E9"));
        add(listPanel, BorderLayout.CENTER);

        // Nút New task
        JButton newTaskButton = new JButton("New");
        newTaskButton.setFont(new Font("Helvetica", Font.PLAIN, 15));
        newTaskButton.setBackground(Color.decode("#3498DB"));  
        newTaskButton.setForeground(Color.WHITE);
        newTaskButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        newTaskButton.addActionListener(e -> openEditor(new Task(userId, date)));
        add(newTaskButton, BorderLayout.SOUTH);

        // Build lần đầu
        refreshTasks();
    }

    private void refreshTasks() {
        listPanel.removeAll();

        List<Task> tasks = taskService.getTasksByDate(
                date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), userId);
        int rows = Math.max(4, tasks.size() + 1);
        JPanel grid = new JPanel(new GridLayout(rows, 1, 10, 5));
        grid.setBackground(Color.decode("#D1F2EB"));

        for (int i = 0; i < tasks.size(); i++) {
            final int idx = i;
            Task t = tasks.get(i);
            JPanel taskPanel = new JPanel(new GridLayout(2, 2));
            taskPanel.setPreferredSize(new Dimension(400, 80));
            taskPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(20, 20, 20, 20),
                    BorderFactory.createMatteBorder(
                            0, 10, 0, 0,
                            // decode hex color for category stripe
                            Color.decode(getTaskColor(t.getCategory()))
                    )
            ));
            // Light cell fill to match calendar
            taskPanel.setBackground(Color.decode("#FDEBD0"));
            taskPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            taskPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    openEditor(t);
                }
            });

            JPanel taskTop = new JPanel(new BorderLayout());
            taskTop.setOpaque(false);
            JLabel titleLabel = new JLabel(t.getTitle());
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));
            titleLabel.setFont(new Font("Helvetica", Font.PLAIN, 20));
            titleLabel.setForeground(Color.decode("#2C3E50"));
            taskTop.add(titleLabel, BorderLayout.WEST);

            JCheckBox checkBox = new JCheckBox();
            checkBox.setOpaque(false);
            checkBox.setSelected(t.isDone());
            checkBox.addItemListener(evt -> {
                t.setDone(checkBox.isSelected());
                taskService.updateTask(t);
            });
            taskTop.add(checkBox, BorderLayout.EAST);

            JLabel timeLabel = new JLabel(t.getDateTimeToString());
            timeLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            timeLabel.setFont(new Font("Helvetica", Font.PLAIN, 15));
            timeLabel.setForeground(Color.decode("#2C3E50"));

            taskPanel.add(taskTop);
            taskPanel.add(timeLabel);
            grid.add(taskPanel);
        }

        JScrollPane scrollPane = new JScrollPane(grid);
        scrollPane.getViewport().setBackground(Color.decode("#E5E7E9"));
        listPanel.add(scrollPane, BorderLayout.CENTER);
        listPanel.revalidate();
        listPanel.repaint();
    }

    private void openEditor(Task task) {
        new TaskEditor(task, mainForm, parentPanel, this::refreshTasks);
    }

    private String getTaskColor(String category) {
        if (category == null) return "#666822";
        switch (category) {
            case "General":  return "#666822";
            case "Holiday":  return "#c67713";
            case "Personal": return "#c1380a";
            case "Meeting":  return "#742505";
            case "Social":   return "#4d2508";
            case "Study":    return "#4d2506";
            default:          return "#666822";
        }
    }
}
