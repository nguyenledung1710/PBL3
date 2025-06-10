/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pbl.service;

import com.pbl.dao.UsersDAO;
import com.pbl.dao.UsersDAOImp;
import com.pbl.model.Users;

/**
 *
 * @author ADMIN
 */
public class UserService {
    private UsersDAO usersDAO;
    
    public UserService() {
      
        this.usersDAO = new UsersDAOImp();
    }
    
    
    public Users getUserByEmail(String email) {
        return usersDAO.getUserByEmai(email);
    }
    
   
    public void createUser(Users user) {
        usersDAO.createUser(user);
    }
    
    
    public String generateVerifyCode() {
        return usersDAO.generateVerifyCode();
    }
    
   
    public boolean checkDuplicateCode(String code) {
        return usersDAO.checkDuplicateCode(code);
    }
    
    // Kiểm tra trùng lặp username
    public boolean checkDuplicateUser(String username) {
        return usersDAO.checkDuplicateUser(username);
    }
    
    // Kiểm tra trùng lặp email
    public boolean checkDuplicateEmail(String email) {
        return usersDAO.checkDuplicateEmail(email);
    }
    
    // Cập nhật verifyCode và trạng thái cho người dùng đã xác thực
    public void doneVerify(int userID) {
        usersDAO.doneVerify(userID);
    }
    
    // Kiểm tra mã xác thực của user
    public boolean verifyCodeWithUser(int userID, String code) {
        return usersDAO.verifyCodeWithUser(userID, code);
    }
    public void deleteUser(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID phải lớn hơn 0");
        }
        usersDAO.deleteUser(userId);
    }
    public void UpdateUser(Users user){
        usersDAO.updateUser(user);
    }
    
    
    
}
