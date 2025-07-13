package com.pbl.form;

import com.pbl.component.Calendar;
import com.pbl.component.Tasks;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CalendarForm extends JPanel {
    private static CalendarForm currentInstance;
    private MainForm mainForm;
    private JPanel tasksPanel;
    private int userId; 
    
    public void setUserID(int userId){
        this.userId = userId;
    }
    public CalendarForm(MainForm frame, int userId) {
        this(frame, LocalDate.now(), userId);
    }
    
    public static CalendarForm getInstance() {
        return currentInstance;
    }
    
  
    public CalendarForm(MainForm frame, LocalDate selectedDay, int userId) {
        this.mainForm = frame;
        this.userId = userId;
        currentInstance = this;
      
        setPreferredSize(new Dimension(1000, 600));
        setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        setLayout(new GridBagLayout());
        setBackground(Color.decode("#E5E7E9"));
        setOpaque(false);
        
        LocalDate date = LocalDate.now();
        String dateString = date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, EEEE"));
        JLabel todayLabel = new JLabel(dateString);
        todayLabel.setFont(new Font("Helvetica", Font.BOLD, 40));
        todayLabel.setForeground(Color.decode("#3498DB")); 
        
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(5, 15, 5, 15);
        add(todayLabel, constraints);
        
        // Add calendar to the left side of the panel
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridwidth = 1;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets = new Insets(0, 0, 0, 0);
      
        add(new Calendar(date.getYear(), date.getMonthValue(), selectedDay, frame, this, userId), constraints);
        
        // Add tasks panel to the right side of the panel
        constraints.gridx = 1;
        constraints.gridy = 1;
        tasksPanel = new JPanel(new BorderLayout());
        tasksPanel.setBackground(Color.decode("#000000"));
    
        tasksPanel.setOpaque(true);
       
        // Gọi Tasks với userId
        tasksPanel.add(new Tasks(selectedDay, mainForm, tasksPanel, userId), BorderLayout.CENTER);
        add(tasksPanel, constraints);
    }
    
    public void updateTasks(LocalDate selectedDay) {
        tasksPanel.removeAll();
        tasksPanel.add(new Tasks(selectedDay, mainForm, tasksPanel, userId), BorderLayout.CENTER);
        tasksPanel.revalidate();
        tasksPanel.repaint();
    }
    
    public void onTaskUpdated(LocalDate date) {
        updateTasks(date);  
    }
}
