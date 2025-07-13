package com.pbl.form;

import com.pbl.component.Form;
import com.pbl.dao.NotificationDaoImpl;
import com.pbl.event.EventColorChange;
import com.pbl.model.Notification;
import com.pbl.model.Users;
import com.pbl.properties.SystemProperties;
import com.pbl.service.EmailConfig;
import com.pbl.swing.EventSwitchSelected;
import com.pbl.theme.ThemeColorChange;
import java.awt.Color;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractSpinnerModel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

public class Setting_Form extends Form {

    private  Notification notification;
    private int usID;
    private NotificationDaoImpl notificationDaoImpl;
    private EmailConfig ec = null;
    public Setting_Form(int UserID) throws SQLException {
        initComponents();
        usID = UserID;
        notificationDaoImpl = new NotificationDaoImpl();
        Notification notification = notificationDaoImpl.getNotificationsByUserId(UserID);
        if(notification != null ){
            SpinnerNumberModel spinnerModel = new SpinnerNumberModel(notification.getNotificationTime(), 0, 59, 1);
            Spinner_Time.setModel(spinnerModel);
            
            Combobox_Time.setSelectedItem(notification.getNotificationTimeText());
            
            boolean iteamExists = false;
            for(int i = 0 ; i < Combobox_Time.getItemCount(); i++){
                if(Combobox_Time.getItemAt(i).equals(notification.getNotificationTimeText())){
                    iteamExists = true;
                    break;
                }
            }
            if(iteamExists){
                Combobox_Time.setSelectedItem(notification.getNotificationTimeText());
            }
        }
   
        ec.setMinutesToNotify(getSelectedTime());
        ec.setText(getSelectedCombobox());
        ThemeColorChange.getInstance().addEventColorChange(new EventColorChange() {
            @Override
            public void colorChange(Color color) {
                switchButton.setBackground(color);
                imageBackgroundOption1.getSwitch().setBackground(color);
            }
        });
        if (ThemeColorChange.getInstance().getMode() == ThemeColorChange.Mode.LIGHT) {
            lbDark.setForeground(new Color(80, 80, 80));
            lbColor.setForeground(new Color(80, 80, 80));
        }
    }

    @Override
    public void changeColor(Color color) {
        lbDark.setForeground(color);
        lbColor.setForeground(color);
        imageBackgroundOption1.changeColorLabel(color);
    }

    public void setEventColorChange(EventColorChange event) {
        colorOption1.setEvent(event);
    }

    public void setSelectedThemeColor(Color color) {
        colorOption1.setSelectedColor(color);
    }

    public void setDarkMode(boolean darkMode) {
        switchButton.setSelected(darkMode);
        switchButton.addEventSelected(new EventSwitchSelected() {
            @Override
            public void onSelected(boolean selected) {
                new SystemProperties().save("dark_mode", selected + "");
                if (selected) {
                    ThemeColorChange.getInstance().changeMode(ThemeColorChange.Mode.DARK);
                } else {
                    ThemeColorChange.getInstance().changeMode(ThemeColorChange.Mode.LIGHT);
                }
            }
        });
    }

    public String getSelectedCombobox() {
        Object selectedItemObject = Combobox_Time.getSelectedItem();
        if (selectedItemObject != null) {
            return (String) selectedItemObject; // Ép kiểu về String
        }
        return null; // Hoặc trả về giá trị mặc định, hoặc ném lỗi
    }
    
    public int getSelectedTime() {
        Object value = Spinner_Time.getValue();
        if (value instanceof Integer) {
            return (Integer) value;
        }
        // Trả về giá trị mặc định hoặc xử lý lỗi nếu giá trị không hợp lệ
        return 1; // Ví dụ
    }
    
    public void initBackgroundImage(String imageSelected) {
        imageBackgroundOption1.init(imageSelected);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lbDark = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        switchButton = new com.pbl.swing.SwitchButton();
        imageBackgroundOption1 = new com.pbl.component.ImageBackgroundOption();
        jPanel3 = new javax.swing.JPanel();
        lbColor = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        colorOption1 = new com.pbl.component.ColorOption();
        jPanel4 = new javax.swing.JPanel();
        lbDark1 = new javax.swing.JLabel();
        Combobox_Time = new javax.swing.JComboBox<>();
        Spinner_Time = new javax.swing.JSpinner();
        btn_Save = new javax.swing.JButton();

        jPanel1.setOpaque(false);

        lbDark.setFont(new java.awt.Font("sansserif", 1, 16)); // NOI18N
        lbDark.setForeground(new java.awt.Color(230, 230, 230));
        lbDark.setText("Dark mode");

        jLabel2.setForeground(new java.awt.Color(128, 128, 128));
        jLabel2.setText("Use darker color paletter for system backgrounds and compatible apps");

        jPanel2.setOpaque(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(switchButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(switchButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbDark)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lbDark)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel3.setOpaque(false);

        lbColor.setFont(new java.awt.Font("sansserif", 1, 16)); // NOI18N
        lbColor.setForeground(new java.awt.Color(230, 230, 230));
        lbColor.setText("Theme Color");

        jLabel4.setForeground(new java.awt.Color(128, 128, 128));
        jLabel4.setText("Select color to set theme system");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbColor)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(colorOption1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(colorOption1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbColor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)))
                .addGap(7, 7, 7))
        );

        jPanel4.setOpaque(false);

        lbDark1.setFont(new java.awt.Font("sansserif", 1, 16)); // NOI18N
        lbDark1.setForeground(new java.awt.Color(230, 230, 230));
        lbDark1.setText("Setting time");

        Combobox_Time.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Combobox_Time.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Phút", "Tiếng", "Ngày", "Tuần", " " }));

        Spinner_Time.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        btn_Save.setText("Save");
        btn_Save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(lbDark1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Spinner_Time, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Combobox_Time, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btn_Save)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbDark1)
                    .addComponent(Combobox_Time, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Spinner_Time, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_Save))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(imageBackgroundOption1, javax.swing.GroupLayout.DEFAULT_SIZE, 924, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(imageBackgroundOption1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(89, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btn_SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SaveActionPerformed
        try {
       
            ec.setMinutesToNotify(getSelectedTime());
            ec.setText(getSelectedCombobox());
            notification = new Notification(usID, getSelectedTime(), getSelectedCombobox());
            if(notificationDaoImpl.doesUserHaveNotifications(notification.getUserId()))
            {
                System.out.println("Update");
                notificationDaoImpl.updateNotification(notification);
            }else{
                  notificationDaoImpl.addNotification(notification);
            }
          
        } catch (SQLException ex) {
            Logger.getLogger(Setting_Form.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btn_SaveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> Combobox_Time;
    private javax.swing.JSpinner Spinner_Time;
    private javax.swing.JButton btn_Save;
    private com.pbl.component.ColorOption colorOption1;
    private com.pbl.component.ImageBackgroundOption imageBackgroundOption1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel lbColor;
    private javax.swing.JLabel lbDark;
    private javax.swing.JLabel lbDark1;
    private com.pbl.swing.SwitchButton switchButton;
    // End of variables declaration//GEN-END:variables
}
