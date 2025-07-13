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

public class DayScheduleTasks extends JPanel {
    private final TaskService taskService;
    private final LocalDate date;
    private final MainForm frame;
    private final JPanel parentPanel;
    private final int userId;
    private final JPanel listContainer;

    public DayScheduleTasks(LocalDate date, MainForm frame, JPanel parentPanel, int userId) {
        this.date = date;
        this.frame = frame;
        this.parentPanel = parentPanel;
        this.userId = userId;
        this.taskService = new TaskService();

        setPreferredSize(new Dimension(350, 350));
        setLayout(new BorderLayout());
        setBackground(Color.decode("#FFF8E1"));  // Window nền chính (vibrant)

        // Header Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        topPanel.setBackground(null);

        JLabel tasksLabel = new JLabel("Tasks");
        tasksLabel.setFont(new Font("Helvetica", Font.BOLD, 25));
        topPanel.add(tasksLabel, BorderLayout.WEST);

        JLabel addButton = new JLabel(new ImageIcon(getClass().getResource("/com/pbl/icon/add.png")));
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new TaskEditor(new Task(userId, date), frame, parentPanel, DayScheduleTasks.this::refresh);
            }
        });
        topPanel.add(addButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Container cho phần danh sách tasks
        listContainer = new JPanel(new BorderLayout());
        add(listContainer, BorderLayout.CENTER);

        // Build lần đầu
        refresh();
    }

    /**
     * Tải lại danh sách tasks và cập nhật giao diện
     */
    private void refresh() {
        listContainer.removeAll();

        List<Task> tasks = taskService.getTasksByDate(
            date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), userId
        );
        int rows = Math.max(6, tasks.size() + 1);

        // Panel chứa toàn bộ bảng
        JPanel list = new JPanel(new GridLayout(rows, 5, 2, 2));
        list.setBackground(Color.decode("#D1F2EB"));

        // Header row
        JPanel header = new JPanel(new GridLayout(1, 5));
        header.setPreferredSize(new Dimension(350, 40));
        header.setBackground(Color.decode("#FF8A65"));      // cam-hồng sáng
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        header.add(createHeaderLabel("No."));
        header.add(createHeaderLabel("Time"));
        header.add(createHeaderLabel("Task"));
        header.add(createHeaderLabel("Category"));
        header.add(createHeaderLabel("Done"));
        list.add(header);

        // Dữ liệu tasks
        for (int i = 0; i < tasks.size(); i++) {
            final int idx = i;
            Task t = tasks.get(i);

            // xen kẽ trắng & cam nhạt
            Color rowBg = (i % 2 == 0)
                ? Color.WHITE
                : Color.decode("#FFF3E0");
            JPanel taskPanel = new JPanel(new GridLayout(1, 5));
            taskPanel.setPreferredSize(new Dimension(350, 30));
            taskPanel.setBackground(rowBg);
            taskPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            taskPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    new TaskEditor(tasks.get(idx), frame, parentPanel, DayScheduleTasks.this::refresh);
                }
            });

            taskPanel.add(createBodyLabel(String.valueOf(idx + 1), "#212121"));
            taskPanel.add(createBodyLabel(t.getTimeToString(), "#212121"));
            taskPanel.add(createBodyLabel(t.getTitle(), "#212121"));
            taskPanel.add(createBodyLabel(t.getCategory(), getTaskColor(t.getCategory())));

            JCheckBox doneCheck = new JCheckBox();
            doneCheck.setBorder(BorderFactory.createEmptyBorder(0, 66, 0, 0));
            doneCheck.setIcon(new ImageIcon(getClass().getResource("/com/pbl/icon/check-box-not-selected.png")));
            doneCheck.setSelectedIcon(new ImageIcon(getClass().getResource("/com/pbl/icon/check-box-selected.png")));
            doneCheck.setSelected(t.isDone());
            doneCheck.addItemListener(evt -> {
                t.setDone(doneCheck.isSelected());
                taskService.updateTask(t);
                refresh();
            });
            taskPanel.add(doneCheck);

            list.add(taskPanel);
        }

        JScrollPane scrollPane = new JScrollPane(list);
        listContainer.add(scrollPane, BorderLayout.CENTER);
        listContainer.revalidate();
        listContainer.repaint();
    }

    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Helvetica", Font.BOLD, 15));
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return label;
    }

    private JLabel createBodyLabel(String text, String fontColor) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Helvetica", Font.PLAIN, 15));
        label.setForeground(Color.decode(fontColor));
        label.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        return label;
    }

    /**
     * Trả về mã màu cho từng category (vibrant palette)
     */
    private String getTaskColor(String category) {
        if (category == null || category.isEmpty()) return "#1E88E5"; // General mặc định
        switch (category) {
            case "General":  return "#1E88E5"; // xanh dương
            case "Holiday":  return "#D81B60"; // hồng đậm
            case "Personal": return "#8E24AA"; // tím tươi
            case "Meeting":  return "#5E35B1"; // tím đậm
            case "Social":   return "#3949AB"; // xanh chàm
            case "Study":    return "#43A047"; // xanh lá tươi
            default:          return "#1E88E5";
        }
    }
}
