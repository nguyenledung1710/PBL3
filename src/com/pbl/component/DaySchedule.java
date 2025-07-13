package com.pbl.component;

import com.pbl.form.CalendarForm;
import com.pbl.form.MainForm;
import com.pbl.main.Main;
import com.pbl.service.NoteService;
import com.pbl.service.QuoteService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DaySchedule extends JPanel {

    public DaySchedule(MainForm main_form, LocalDate selectedDay, int userId) {
        // Set light gray background for the entire panel
        setBackground(Color.decode("#E3F2FD"));
        setOpaque(true);
        setPreferredSize(new Dimension(970, 570));
        setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(5, 15, 5, 15);

        // Header row: date title
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        JLabel todayLabel = createHeader(selectedDay, main_form, userId);
        add(todayLabel, constraints);

        // Row 1: Tasks table
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 0.995;
        constraints.weighty = 1.0;
        DayScheduleTasks tasks = new DayScheduleTasks(selectedDay, main_form, this, userId);
        add(tasks, constraints);

        // Row 1: Notes panel (truyền thêm userId)
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 0.005;
        constraints.weighty = 1.0;
        JPanel notesPanel = createNotesSection(selectedDay, userId);
        add(notesPanel, constraints);

        // Row 2: Quote area
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(10, 15, 10, 15);
        JTextArea quoteLabel = createQuoteLabel();
        quoteLabel.setBackground(Color.WHITE);
        quoteLabel.setBorder(BorderFactory.createLineBorder(Color.decode("#D5DBDB")));
        add(quoteLabel, constraints);
    }

    private JLabel createHeader(LocalDate selectedDay, MainForm main_form, int user_id) {
        String dateString = selectedDay.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, EEEE"));
        JLabel todayLabel = new JLabel(dateString);
        todayLabel.setFont(new Font("Helvetica", Font.BOLD, 40));
        // Dark brown for strong contrast
        todayLabel.setForeground(Color.decode("#6E2C00"));
        todayLabel.setHorizontalAlignment(JLabel.CENTER);
        todayLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        todayLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        todayLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                resetCalendarSchedular(main_form, user_id);
            }
        });
        return todayLabel;
    }

    /**
     * Đã sửa: thêm tham số userId, gọi đúng NoteService.getNoteByUserAndDate(...) và saveOrUpdateNote(userId,...)
     */
    private JPanel createNotesSection(LocalDate selectedDay, int userId) {
        JPanel textAreaPanel = new JPanel(new BorderLayout());
        // Light cream background for notes
        textAreaPanel.setBackground(Color.decode("#FEF9E7"));
        textAreaPanel.setBorder(BorderFactory.createLineBorder(Color.decode("#D5DBDB")));
        textAreaPanel.setPreferredSize(new Dimension(200, 340));

        JLabel notesLabel = new JLabel("Notes:");
        notesLabel.setFont(new Font("Helvetica", Font.BOLD, 25));
        notesLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        textAreaPanel.add(notesLabel, BorderLayout.NORTH);

        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Helvetica", Font.PLAIN, 18));
        textArea.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.decode("#D1F2EB")));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        // Sử dụng service mới: getNoteByUserAndDate(userId, selectedDay)
        NoteService noteService = new NoteService();
        String existingNote = "";
        if (noteService.getNoteByUserAndDate(userId, selectedDay) != null) {
            existingNote = noteService.getNoteByUserAndDate(userId, selectedDay).getNote();
        }
        textArea.setText(existingNote);

        // Tạo timer để auto-save sau khi user ngưng gõ 1 giây
        Timer autoSaveTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String updatedNote = textArea.getText();
                 
                noteService.saveOrUpdateNote(userId, selectedDay, updatedNote);
            }
        });
        autoSaveTimer.setRepeats(false);

        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void restartTimer() {
                autoSaveTimer.restart();
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) {
                restartTimer();
            }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) {
                restartTimer();
            }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) {
                restartTimer();
            }
        });

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.getViewport().setBackground(Color.decode("#FEF9E7"));
        textAreaPanel.add(scrollPane, BorderLayout.CENTER);
        return textAreaPanel;
    }

    private JTextArea createQuoteLabel() {
        JTextArea quoteLabel = new JTextArea();
        QuoteService quoteService = new QuoteService();
        quoteLabel.setText(quoteService.getRandomQuote());
        quoteLabel.setEditable(false);
        quoteLabel.setWrapStyleWord(true);
        quoteLabel.setLineWrap(true);
        quoteLabel.setFont(new Font("Helvetica", Font.ITALIC, 14));
        quoteLabel.setForeground(Color.decode("#2471A3")); // navy blue quote
        quoteLabel.setPreferredSize(new Dimension(700, 50));
        return quoteLabel;
    }

    private void resetCalendarSchedular(MainForm main_form, int user_id) {
        Window win = SwingUtilities.getWindowAncestor(this);
        if (win instanceof Main) {
            Main mainFrame = (Main) win;
            LocalDate today = LocalDate.now();
            CalendarForm form2 = new CalendarForm(main_form, today, user_id);
            mainFrame.mainBody.displayForm(form2);
        }
    }
}
