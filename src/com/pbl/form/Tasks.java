package com.pbl.form;

import com.pbl.model.Task;
import com.pbl.service.TaskService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Tasks extends JPanel {

    private TaskService taskService; // Sử dụng lớp nghiệp vụ TaskService
    private int userId;              // ID của người dùng hiện hành

    /**
     * Class constructor.
     *
     * @param date      The date of the tasks.
     * @param mainForm  The main form object.
     * @param mainPanel The parent panel object.
     * @param userId    ID của người dùng đang đăng nhập
     */
    public Tasks(LocalDate date, MainForm mainForm, JPanel mainPanel, int userId) {
        // Lưu userId
        this.userId = userId;
        
        // Set up tasks panel
        setPreferredSize(new Dimension(400, 400));
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(199, 215, 251));
        setBorder(BorderFactory.createEmptyBorder(20, 10, 15, 10));

        // Khởi tạo TaskService để gọi các phương thức nghiệp vụ
        taskService = new TaskService();

        // Thêm tasks vào panel
        createTasksSection(date, mainForm, mainPanel);
    }

    /**
     * createTasksSection - Set up the panels to show the list of tasks.
     *
     * @param date      The selected date.
     * @param mainForm  The main form object.
     * @param mainPanel The parent panel object.
     */
    private void createTasksSection(LocalDate date, MainForm mainForm, JPanel mainPanel) {
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Lấy danh sách task theo ngày và userId
        List<Task> tasks = taskService.getTasksByDate(formattedDate, userId);

        int rows = Math.max(4, tasks.size() + 1);
        JPanel list = new JPanel(new GridLayout(rows, 1, 10, 5));
        list.setBackground(new Color(199, 215, 251));
        JScrollPane scrollPane = new JScrollPane(list);

        // Duyệt qua danh sách tasks và tạo giao diện cho từng task
        for (int i = 0; i < tasks.size(); i++) {
            final int j = i;
            JPanel taskPanel = new JPanel(new GridLayout(2, 2));
            taskPanel.setPreferredSize(new Dimension(400, 80));
            taskPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(20, 20, 20, 20),
                    BorderFactory.createMatteBorder(0, 10, 0, 0, Color.decode(getTaskColor(tasks.get(i).getCategory())))
            ));
            taskPanel.setBackground(Color.decode("#F1B0DA"));
            taskPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            taskPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Mở TaskEditor để chỉnh sửa task đã chọn
                    new TaskEditor(tasks.get(j), mainForm, mainPanel);
                }
            });

            JPanel taskTop = new JPanel(new BorderLayout());
            taskTop.setBackground(null);
            JLabel titleLabel = new JLabel(tasks.get(i).getTitle());
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));
            titleLabel.setFont(new Font("Helvetica", Font.PLAIN, 20));
            titleLabel.setForeground(Color.decode("#5c2c0c"));
            taskTop.add(titleLabel, BorderLayout.WEST);

            JCheckBox checkBox = new JCheckBox();
            checkBox.setBackground(null);
            checkBox.setSelected(tasks.get(i).isDone());
            checkBox.addItemListener(e -> {
                Task t = tasks.get(j);
                boolean isSelected = checkBox.isSelected();
                t.setDone(isSelected);
                // Cập nhật task qua TaskService
                taskService.updateTask(t);
            });
            taskTop.add(checkBox, BorderLayout.EAST);

            JLabel timeLabel = new JLabel(tasks.get(i).getDateTimeToString());
            timeLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            timeLabel.setFont(new Font("Helvetica", Font.PLAIN, 15));
            timeLabel.setForeground(Color.DARK_GRAY);

            taskPanel.add(taskTop);
            taskPanel.add(timeLabel);
            list.add(taskPanel);
        }
        add(scrollPane, BorderLayout.CENTER);

        JButton newTaskButton = new JButton("New");
        newTaskButton.setFont(new Font("Helvetica", Font.PLAIN, 15));
        newTaskButton.setBackground(Color.decode("#dda35d"));
        newTaskButton.setForeground(Color.WHITE);
        newTaskButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        newTaskButton.addActionListener(e -> {
          
            new TaskEditor(new Task(userId, date), mainForm, mainPanel);
        });
        add(newTaskButton, BorderLayout.SOUTH);
    }

    /**
     * getTaskColor - Lấy màu tương ứng dựa trên category của task.
     *
     * @param category The task category.
     * @return A string representing the hex color.
     */
    private String getTaskColor(String category) {
        switch (category) {
            case "General":
                return "#666822";
            case "Holiday":
                return "#c67713";
            case "Personal":
                return "#c1380a";
            case "Meeting":
                return "#742505";
            case "Social":
                return "#4d2508";
            default:
                return "#666822";
        }
    }
}
