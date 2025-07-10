/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pbl.dao;
import java.sql.*;
/**
 *
 * @author ADMIN
 */
public class QuotesDAOImp implements QuotesDAO{
    private DBHelper dbHelper;

    public QuotesDAOImp() {
        dbHelper = DBHelper.getInstance();
    }
    @Override
    public String getRandomQuote() {
        String select = "SELECT quote from quotes ORDER BY RAND() LIMIT 1";
        try (ResultSet rs = dbHelper.getRecords(select)) {
        if (rs.next()) {
            return rs.getString("quote");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return "";
    }
    
}
