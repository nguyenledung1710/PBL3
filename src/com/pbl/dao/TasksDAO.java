/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pbl.dao;

import com.pbl.model.Task;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public interface TasksDAO {
    List<Task> getAllTasksByUser(int userId);
    List<Task> getTasks(String date, int userId);
    boolean hasTasks(String date, int userId);
    void createTask(Task t);
    void deleteTask(int taskId);
    void updateTask(Task t);
    int countTasksByMonthAndStatus(int userId, int month, boolean status); 
    int countByCategoryAndMonth(int userId, String category, int month, int year);
    int countOverdueTasks(int userId, int month);
   

    
}
