package com.pbl.form;

import com.pbl.service.NoteService;
import com.pbl.service.QuoteService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DaySchedule extends JPanel {

    // Constructor bây giờ nhận thêm tham số userId
    public DaySchedule(MainForm main_form, LocalDate selectedDay, int userId) {
        setPreferredSize(new Dimension(1000, 650));
        setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        setLayout(new GridBagLayout());
        setBackground(null);

        // Tạo đối tượng GridBagConstraints chung cho panel
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(5, 15, 5, 15);

        // ========== Hàng 0: Tiêu đề (Header) ==========
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;          // Header chiếm 2 cột
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        JLabel todayLabel = createHeader(selectedDay, main_form, userId);
        add(todayLabel, constraints);

        // ========== Hàng 1: Bảng nhiệm vụ và khu vực ghi chú ==========
        // Phần nhiệm vụ (Tasks)
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;          // Chiếm 1 cột
        constraints.weightx = 0.995;        // Tasks chiếm phần lớn chiều rộng bên trái
        constraints.weighty = 1.0;
        // Truyền thêm userId cho DayScheduleTasks
        DayScheduleTasks tasks = new DayScheduleTasks(selectedDay, main_form, this, userId);
        add(tasks, constraints);

        // Phần ghi chú (Notes)
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;          // Chiếm 1 cột còn lại
        constraints.weightx = 0.005;        // Notes chiếm phần nhỏ bên phải
        constraints.weighty = 1.0;
        JPanel notesPanel = createNotesSection(selectedDay);
        add(notesPanel, constraints);

        // ========== Hàng 2: Câu nói động lực (Quote) ==========
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;          // Chiếm 2 cột
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;          // Không mở rộng chiều cao
        constraints.insets = new Insets(10, 15, 10, 15); // Lề dưới
        JTextArea quoteLabel = createQuoteLabel();
        add(quoteLabel, constraints);
    }

    /**
     * Tạo tiêu đề hiển thị ngày tháng với khả năng click để quay về trang Home (Form2).
     */
    private JLabel createHeader(LocalDate selectedDay, MainForm main_form, int user_id) {
        String dateString = selectedDay.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, EEEE"));
        JLabel todayLabel = new JLabel(dateString);
        todayLabel.setFont(new Font("Helvetica", Font.BOLD, 40));
        todayLabel.setForeground(Color.decode("#4D2508"));
        todayLabel.setHorizontalAlignment(JLabel.CENTER);
        todayLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        todayLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Xử lý sự kiện click để chuyển về trang Home
        todayLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                resetCalendarSchedular(main_form, user_id);
            }
        });
        return todayLabel;
    }

    /**
     * Tạo khu vực ghi chú với tính năng AutoSave.
     */
    private JPanel createNotesSection(LocalDate selectedDay) {
        JPanel textAreaPanel = new JPanel(new BorderLayout());
        textAreaPanel.setMinimumSize(new Dimension(150, 150));
        textAreaPanel.setBackground(Color.LIGHT_GRAY);

        JLabel notesLabel = new JLabel("Notes:");
        notesLabel.setFont(new Font("Helvetica", Font.BOLD, 25));
        notesLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        textAreaPanel.add(notesLabel, BorderLayout.NORTH);

        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Helvetica", Font.PLAIN, 18));
        textArea.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        NoteService noteService = new NoteService();
        String noteContent = noteService.getNoteByDate(selectedDay).getNote();
        textArea.setText(noteContent);
        Timer autoSaveTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String updatedNote = textArea.getText();
                noteService.saveOrUpdateNote(selectedDay, updatedNote);
                System.out.println("Auto-saved note at " + selectedDay + ": " + updatedNote);
            }
        });
        autoSaveTimer.setRepeats(false);

        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void restartTimer() {
                autoSaveTimer.restart();
            }
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                restartTimer();
            }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                restartTimer();
            }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                restartTimer();
            }
        });

        JScrollPane scrollPane = new JScrollPane(textArea);
        textAreaPanel.add(scrollPane, BorderLayout.CENTER);
        return textAreaPanel;
    }

    /**
     * Tạo vùng hiển thị câu nói truyền cảm hứng.
     */
    private JTextArea createQuoteLabel() {
        JTextArea quoteLabel = new JTextArea("");
        QuoteService quoteService = new QuoteService();
        String randomQuote = quoteService.getRandomQuote();
        quoteLabel.setText(randomQuote);
        quoteLabel.setEditable(false);
        quoteLabel.setWrapStyleWord(true);
        quoteLabel.setLineWrap(true);
        quoteLabel.setFont(new Font("Helvetica", Font.ITALIC, 14));
        quoteLabel.setForeground(Color.BLUE);
        quoteLabel.setPreferredSize(new Dimension(700, 50));
        quoteLabel.setBackground(null);
        return quoteLabel;
    }

    /**
     * Hàm reset về trang Home (Form2) với ngày hiện tại.
     * Bạn cũng nên cập nhật Form2 để nhận thêm userId nếu cần.
     */
    private void resetCalendarSchedular(MainForm main_form, int user_id) {
        // Giả sử Form2 đã được cập nhật để nhận userId, ví dụ:
        // main_form.showForm(new Form2(main_form, LocalDate.now(), userId));
        // Nếu chưa, bạn có thể giữ nguyên:
        main_form.showForm(new Form2(main_form, LocalDate.now(), user_id));
    }
}
