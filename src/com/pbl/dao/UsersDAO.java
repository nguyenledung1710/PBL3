/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pbl.dao;

import com.pbl.model.Users;

/**
 *
 * @author ADMIN
 */
public interface UsersDAO {

    Users getUserByEmai(String email);

    void createUser(Users user);

    String generateVerifyCode();

    boolean checkDuplicateCode(String code);

    boolean checkDuplicateUser(String user);

    boolean checkDuplicateEmail(String user);

    void doneVerify(int userID);

    boolean verifyCodeWithUser(int userID, String code);

    void deleteUser(int userId);
    
    void updateUser(Users user);
}
