/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pbl.service;

import com.pbl.dao.UsersDAO;
import com.pbl.dao.UsersDAOImp;
import com.pbl.model.ModelMessage;
import com.pbl.model.Users;
import com.pbl.utility.PasswordUtils;
import java.time.LocalDateTime;

/**
 *
 * @author ADMIN
 */
public class AuthService {
    private UsersDAO userDAO;
    
    public AuthService(){
        this.userDAO = new UsersDAOImp();
    }
    public Users Login(String email, String password){
        Users user = userDAO.getUserByEmai(email);
        if(user == null){
            System.out.println("User không tổn tại!!");
            return null;
        }
        String salt = user.getSalt();
        String hashedInput = PasswordUtils.hashPassword(password, salt);
        if(hashedInput.equals(user.getPassword())){
            System.out.println("Đăng nhập thành công");
            return user;
        }else{
            System.out.println("Mat khau hoac username khong dung");
            return null;
        }
        
    }
    public void register(String username, String email, String rawPassword, String role) {
        String salt = PasswordUtils.generateSalt();
        String hashedPassword = PasswordUtils.hashPassword(rawPassword, salt);
        Users newUser = new Users();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(hashedPassword);
        newUser.setSalt(salt);
        newUser.setStatus(true);
        newUser.setRole(role);
        userDAO.createUser(newUser);
         System.out.println(userDAO.getUserByEmai(email).getVerifyCode());
         sendMain(userDAO.getUserByEmai(email));
    }
      public void sendMain(Users user) {
        new Thread(new Runnable() {
            @Override
            public void run() {
              
                ModelMessage ms = new MailService().sendMain(user.getEmail(),user.getVerifyCode());
             
            }
        }).start();
    }

}
