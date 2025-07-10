package com.pbl.form;

import com.pbl.model.Task;
import com.pbl.service.TaskService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DayScheduleTasks extends JPanel {

    private TaskService taskService;

    // Thêm tham số userId để xác định các task của user hiện hành
    public DayScheduleTasks(LocalDate date, MainForm frame, JPanel parentPanel, int userId) {
        // Khởi tạo service
        taskService = new TaskService();

        // Set up tasks panel (có thể điều chỉnh kích thước theo ý bạn)
        setPreferredSize(new Dimension(2000, 1600));
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Lấy dữ liệu task từ DB thông qua TaskService, truyền thêm userId
        DateTimeFormatter dbDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = date.format(dbDateFormatter);
        List<Task> tasks = taskService.getTasksByDate(formattedDate, userId);

        // ================= Header Panel =================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        topPanel.setBackground(null);

        JLabel tasksLabel = new JLabel("Tasks");
        tasksLabel.setFont(new Font("Helvetica", Font.BOLD, 25));
        topPanel.add(tasksLabel, BorderLayout.WEST);

        // Nút Add để mở TaskEditor để thêm task mới
        JLabel addButton = new JLabel(new ImageIcon(getClass().getResource("/com/pbl/icon/add.png")));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Khi tạo task mới, khởi tạo Task với userId và ngày hiện hành
                new TaskEditor(new Task(userId, date), frame, parentPanel);
            }
        });
        topPanel.add(addButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // ================= Bảng hiển thị tasks =================
        // Số hàng: 1 header + số dòng task (ít nhất 6 dòng)
        int rows = Math.max(6, tasks.size() + 1);

        // Panel chứa danh sách task với lưới 5 cột
        JPanel list = new JPanel(new GridLayout(rows, 5, 2, 2));
        list.setBackground(Color.WHITE);

        // Tạo header row
        JPanel header = new JPanel(new GridLayout(1, 5));
        header.setPreferredSize(new Dimension(350, 40));
        header.setBackground(Color.decode("#aa6231"));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        header.add(createHeaderLabel("No."));
        header.add(createHeaderLabel("Time"));
        header.add(createHeaderLabel("Task"));
        header.add(createHeaderLabel("Category"));
        header.add(createHeaderLabel("Done"));
        list.add(header);

        for (int i = 0; i < tasks.size(); i++) {
            final int j = i;
            JPanel taskPanel = new JPanel(new GridLayout(1, 5));
            taskPanel.setPreferredSize(new Dimension(350, 30));
            taskPanel.setBackground(Color.decode("#f0f0f0"));
            taskPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            taskPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    new TaskEditor(tasks.get(j), frame, parentPanel);
                }
            });

            taskPanel.add(createBodyLabel(Integer.toString(i + 1), "#000000"));
            taskPanel.add(createBodyLabel(tasks.get(i).getTimeToString(), "#000000"));
            // Cột thứ 3: Task title
            taskPanel.add(createBodyLabel(tasks.get(i).getTitle(), "#000000"));
            // Cột thứ 4: Category
            taskPanel.add(createBodyLabel(tasks.get(i).getCategory(), getTaskColor(tasks.get(i).getCategory())));
            // Cột thứ 5: Checkbox Done
            JCheckBox doneCheck = new JCheckBox();
            Icon notSelected = new ImageIcon(getClass().getResource("/com/pbl/icon/check-box-not-selected.png"));
            Icon selected = new ImageIcon(getClass().getResource("/com/pbl/icon/check-box-selected.png"));
            doneCheck.setBorder(BorderFactory.createEmptyBorder(0, 66, 0, 0));
            doneCheck.setIcon(notSelected);
            doneCheck.setSelectedIcon(selected);
            doneCheck.setSelected(tasks.get(i).isDone());
            doneCheck.addItemListener(e -> {
                Task t = tasks.get(j);
                t.setDone(doneCheck.isSelected());
                taskService.updateTask(t); // Cập nhật trạng thái trong DB
            });
            taskPanel.add(doneCheck);

            list.add(taskPanel);
        }

        JScrollPane scrollPane = new JScrollPane(list);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Tạo nhãn header cho bảng.
     */
    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Helvetica", Font.BOLD, 15));
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return label;
    }

    /**
     * Tạo nhãn cho nội dung bảng.
     */
    private JLabel createBodyLabel(String text, String fontColor) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Helvetica", Font.PLAIN, 15));
        label.setForeground(Color.decode(fontColor));
        label.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        return label;
    }

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
