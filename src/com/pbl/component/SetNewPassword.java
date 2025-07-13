package com.pbl.component;

import java.awt.event.ActionListener;
import com.pbl.swing.*;

public class SetNewPassword extends javax.swing.JPanel {

    /**
     * Creates new form Login
     */
    public SetNewPassword(ActionListener confirmEvent) {
        initComponents();
        addConfirmAction(confirmEvent);
    }

    // Thiết lập thông báo lỗi/thành công (nếu cần)
    public void setMessage(String msg) {
        this.txtMes.setText(msg);
    }

    // Lấy giá trị mật khẩu mới
    public String getNewPassword() {
        return newPasswordField.getText();
    }

    // Lấy giá trị xác nhận mật khẩu
    public String getConfirmPassword() {
        return confirmPasswordField.getText();
    }
    

  
    public void addConfirmAction(ActionListener event) {
        btnConfirm.addActionListener(event);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblHeader    = new javax.swing.JLabel();
        lblNewPass   = new javax.swing.JLabel();
        lblConfirm   = new javax.swing.JLabel();
        newPasswordField     = new com.pbl.swing.MyPassword();
        confirmPasswordField = new com.pbl.swing.MyPassword();
        btnConfirm   = new com.pbl.swing.MyButton();
        txtMes       = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        // Tiêu đề chính (có thể để trống hoặc sửa lại nếu cần)
        lblHeader.setFont(new java.awt.Font("sansserif", 1, 36)); // NOI18N
        lblHeader.setForeground(new java.awt.Color(69, 68, 68));
        lblHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeader.setText("Set New Password");

        // Label Mật khẩu mới
        lblNewPass.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        lblNewPass.setText("New Password");

        // Label Xác nhận mật khẩu
        lblConfirm.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        lblConfirm.setText("Confirm Password");

        // Trường nhập mật khẩu mới (hiển thị ký tự *)
        newPasswordField.setText(""); // Khởi tạo trống

        // Trường nhập xác nhận mật khẩu (hiển thị ký tự *)
        confirmPasswordField.setText(""); // Khởi tạo trống

        // Nút “Xác nhận”
        btnConfirm.setBackground(new java.awt.Color(102, 174, 242));
        btnConfirm.setText("Confirm");

        // Dòng hiển thị thông báo lỗi (màu đỏ)
        txtMes.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        txtMes.setForeground(new java.awt.Color(255, 0, 0));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // Tiêu đề
                    .addComponent(lblHeader, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                    // Dòng Thông báo
                    .addComponent(txtMes, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    // Mật khẩu mới
                    .addComponent(lblNewPass, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(newPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                    // Xác nhận mật khẩu
                    .addComponent(lblConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(confirmPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                    // Nút xác nhận
                    .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                )
                .addContainerGap(30, Short.MAX_VALUE))
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                // Tiêu đề
                .addComponent(lblHeader)
                .addGap(20, 20, 20)
                // Mật khẩu mới
                .addComponent(lblNewPass)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                // Xác nhận mật khẩu
                .addComponent(lblConfirm)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(confirmPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                // Nút xác nhận
                .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                // Dòng thông báo (nếu có)
                .addComponent(txtMes, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblHeader;
    private javax.swing.JLabel lblNewPass;
    private javax.swing.JLabel lblConfirm;
    private com.pbl.swing.MyPassword newPasswordField;
    private com.pbl.swing.MyPassword confirmPasswordField;
    private com.pbl.swing.MyButton btnConfirm;
    private javax.swing.JLabel txtMes;
    // End of variables declaration//GEN-END:variables
}
