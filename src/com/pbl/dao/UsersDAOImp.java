/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pbl.dao;

import com.pbl.model.Users;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Random;

/**
 *
 * @author ADMIN
 */
public class UsersDAOImp implements UsersDAO {

    private DBHelper dbHelper;

    public UsersDAOImp() {
        dbHelper = DBHelper.getInstance();
    }

    @Override
    public Users getUserByEmai(String email) {
        String sql = "SELECT * FROM USERS WHERE email = ? LIMIT 1";
        try (ResultSet rs = dbHelper.getRecords(sql, email)) {
            if (rs.next()) {
                return mapRowToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public void createUser(Users user) {
        String code = generateVerifyCode();
        String sql = "INSERT INTO users (username, email, password, salt, status, role, verifyCode, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try {
            dbHelper.executeUpdate(sql,
                    user.getUsername(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getSalt(),
                    0,
                    user.getRole(),
                    code);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Users mapRowToUser(ResultSet rs) throws SQLException {
        Users u = new Users();
        u.setUser_id(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setSalt(rs.getString("salt"));
        u.setStatus(rs.getBoolean("status"));
        u.setRole(rs.getString("role"));
        u.setVerifyCode(rs.getString("verifyCode"));
        return u;
    }

    @Override
    public String generateVerifyCode() {
        DecimalFormat df = new DecimalFormat("000000");
        Random ran = new Random();
        String code = df.format(ran.nextInt(1000000));
        while (checkDuplicateCode(code)) {
            code = df.format(ran.nextInt(1000000));
        }
        return code;
    }

    @Override
    public boolean checkDuplicateCode(String code) {
        String sql = "SELECT user_id FROM users WHERE verifyCode = ? LIMIT 1";
        try {
            ResultSet rs = dbHelper.getRecords(sql, code);
            if (rs.next()) {
                rs.close();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean checkDuplicateUser(String user) {
        String sql = "SELECT user_id FROM `users` WHERE username = ? LIMIT 1";
        try {
            ResultSet rs = dbHelper.getRecords(sql, user);
            if (rs.next()) {
                rs.close();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean checkDuplicateEmail(String user) {
        String sql = "SELECT user_id FROM `users` WHERE email = ? LIMIT 1";
        try {
            ResultSet rs = dbHelper.getRecords(sql, user);
            if (rs.next()) {
                rs.close();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void doneVerify(int userID) {
        String sql = "UPDATE `users` SET verifyCode = '', status = 1 WHERE user_id = ? LIMIT 1";
        try {
            dbHelper.executeUpdate(sql, userID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean verifyCodeWithUser(int userID, String code) {
        String sql = "SELECT user_id FROM `users` WHERE user_id = ? AND verifyCode = ? LIMIT 1";
        ResultSet rs = null;
        try {
            rs = dbHelper.getRecords(sql, userID, code);
            if (rs.next()) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try {
            dbHelper.executeUpdate(sql, userId);
        } catch (SQLException e) {
            e.printStackTrace();
            // bạn có thể ném ra một exception custom ở đây nếu muốn
        }
    }

    @Override
    public void updateUser(Users user) {
        String sql = "UPDATE users "
                + "SET username    = ?, "
                + "    email       = ?, "
                + "    password    = ?, "
                + "    salt        = ?, "
                + "    status      = ?, "
                + "    role        = ?, "
                + "    verifyCode  = ?, "
                + "    updated_at  = NOW() "
                + "WHERE user_id = ? LIMIT 1";
        try {
           
            String code = user.getVerifyCode();
            if (code == null || code.trim().isEmpty()) {
                code = "";
            }
            dbHelper.executeUpdate(sql,
                    user.getUsername(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getSalt(),
                    user.isStatus() ? 1 : 0,
                    user.getRole(),
                    code,
                    user.getUser_id());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
