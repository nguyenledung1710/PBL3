package com.pbl.component;

import com.pbl.form.MainForm;
import com.pbl.model.Task;
import com.pbl.service.TaskService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;

/**
 * TaskEditor: Dialog để thêm/sửa/xóa Task. Giữ nguyên giao diện gốc, chỉ bổ
 * sung callback onUpdate để refresh danh sách.
 */
public class TaskEditor {

    // Mảng danh mục cho drop down menu
    String[] categories = {"General", "Holiday", "Personal", "Meeting", "Social", "Study"};
    private final TaskService taskService = new TaskService();

    /**
     * @param t Task mới hoặc đã tồn tại
     * @param mainForm Đối tượng MainForm
     * @param parentPanel Panel cha chứa giao diện (để attach callback)
     * @param onUpdate Callback để gọi sau khi save hoặc delete
     */
    public TaskEditor(Task t, MainForm mainForm, JPanel parentPanel, Runnable onUpdate) {
        // Lấy ngày và tháng nếu cần (không thay đổi UI)
        int year = t.getDate().getYear();
        int month = t.getDate().getMonthValue();

        // Cấu hình frame như cũ
        JFrame frame = new JFrame("Add Task");
        if (t.getTitle() != null) {
            frame.setTitle(t.getTitle());
        }
        ImageIcon img = new ImageIcon("resources/add.png");
        frame.setIconImage(img.getImage());
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.WHITE);

        // Panel chính
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setPreferredSize(new Dimension(300, 300));
        mainPanel.setBackground(Color.WHITE);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(15, 30, 0, 30);

        // Title
        constraints.gridx = 0;
        constraints.gridy = 0;
        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setFont(new Font("Helvetica", Font.PLAIN, 20));
        titleLabel.setPreferredSize(new Dimension(120, 40));
        titleLabel.setHorizontalAlignment(JLabel.LEFT);
        mainPanel.add(titleLabel, constraints);
        constraints.gridx = 1;
        JTextField titleField = new JTextField();
        titleField.setFont(new Font("Helvetica", Font.PLAIN, 20));
        titleField.setPreferredSize(new Dimension(200, 40));
        mainPanel.add(titleField, constraints);

        // Time
        constraints.gridx = 0;
        constraints.gridy = 1;
        JLabel timeLabel = new JLabel("Time:");
        timeLabel.setFont(new Font("Helvetica", Font.PLAIN, 20));
        timeLabel.setPreferredSize(new Dimension(120, 40));
        timeLabel.setHorizontalAlignment(JLabel.LEFT);
        mainPanel.add(timeLabel, constraints);
        constraints.gridx = 1;
        JTextField timeField = new JTextField();
        timeField.setFont(new Font("Helvetica", Font.PLAIN, 20));
        timeField.setPreferredSize(new Dimension(200, 40));
        mainPanel.add(timeField, constraints);

        // Category
        constraints.gridx = 0;
        constraints.gridy = 2;
        JLabel categoriesLabel = new JLabel("Category:");
        categoriesLabel.setFont(new Font("Helvetica", Font.PLAIN, 20));
        categoriesLabel.setPreferredSize(new Dimension(100, 40));
        categoriesLabel.setHorizontalAlignment(JLabel.LEFT);
        mainPanel.add(categoriesLabel, constraints);
        constraints.gridx = 1;
        JComboBox<String> categoriesField = new JComboBox<>(categories);
        categoriesField.setFont(new Font("Helvetica", Font.PLAIN, 20));
        categoriesField.setPreferredSize(new Dimension(200, 40));
        ((JLabel) categoriesField.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
        mainPanel.add(categoriesField, constraints);

        // Description
        constraints.gridx = 0;
        constraints.gridy = 3;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Helvetica", Font.PLAIN, 20));
        descLabel.setPreferredSize(new Dimension(120, 40));
        descLabel.setHorizontalAlignment(JLabel.LEFT);
        mainPanel.add(descLabel, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        JTextArea descField = new JTextArea(3, 0);
        descField.setPreferredSize(new Dimension(200, 300));
        descField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        descField.setFont(new Font("Helvetica", Font.PLAIN, 20));
        JScrollPane scroll = new JScrollPane(descField);
        mainPanel.add(scroll, constraints);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));
        JButton deleteTaskButton = new JButton("Delete");
        deleteTaskButton.setFont(new Font("Helvetica", Font.PLAIN, 15));
        deleteTaskButton.setBackground(Color.decode("#e3deca"));
        deleteTaskButton.setForeground(Color.decode("#3c3a1e"));
        JButton saveTaskButton = new JButton("Save");
        saveTaskButton.setFont(new Font("Helvetica", Font.PLAIN, 15));
        saveTaskButton.setBackground(Color.decode("#3c3a1e"));
        saveTaskButton.setForeground(Color.WHITE);
        bottomPanel.add(deleteTaskButton);
        bottomPanel.add(saveTaskButton);

        // Gán giá trị mặc định
        timeField.setText(t.getTimeToString());
        if (t.getTitle() != null) {
            titleField.setText(t.getTitle());
            descField.setText(t.getDescription());
            categoriesField.setSelectedItem(t.getCategory());
        } else {
            deleteTaskButton.setVisible(false);
        }

        frame.add(mainPanel);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.setVisible(true);

       
        saveTaskButton.addActionListener(e -> {
            String rawTime = timeField.getText().trim();

            // Chuẩn hóa định dạng giờ phút
            if (rawTime.matches("^\\d{1}:\\d{2}$")) {
                rawTime = "0" + rawTime; // thêm số 0 ở đầu nếu giờ chỉ 1 chữ số, ví dụ 8:30 → 08:30
            }

            // Kiểm tra định dạng hợp lệ: HH:mm (00-23):(00-59)
            if (!rawTime.matches("^(0\\d|1\\d|2[0-3]):[0-5]\\d$")) {
                JOptionPane.showMessageDialog(frame,
                        "Invalid time format. Please enter time as HH:mm (e.g. 08:30).",
                        "Invalid Time",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            t.setTitle(titleField.getText().trim());
            t.setDescription(descField.getText().trim());
            t.setCategory((String) categoriesField.getSelectedItem());
            t.setTime(rawTime); // dùng chuỗi đã chuẩn hóa

            if (t.getID() != 0) {
                taskService.updateTask(t);
            } else {
                taskService.createTask(t);
            }

            onUpdate.run();  // gọi callback
            frame.dispose();
        });

        // Delete action
        deleteTaskButton.addActionListener(e -> {
            if (t.getID() != 0) {
                taskService.deleteTask(t.getID());
            }
            onUpdate.run();  // gọi callback
            frame.dispose();
        });
    }
}
