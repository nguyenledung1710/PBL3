package com.pbl.service;

import com.pbl.dao.TasksDAO;
import com.pbl.dao.TasksDAOImp;
import com.pbl.model.Task;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
public class TaskService {

    private TasksDAO tasksDAO;

    // chỉnh sửa gửi mail
   private static final List<Task> task = new ArrayList<>();
    private static int nextTaskId = 1;
    
    public TaskService() {
        this.tasksDAO = new TasksDAOImp();
    }

    /**
     * Lấy danh sách task theo ngày và userId.
     *
     * @param date Ngày ở định dạng "yyyy-MM-dd"
     * @param userId ID của người dùng cần lấy task
     * @return Danh sách task tương ứng
     */
    public List<Task> getTasksByDate(String date, int userId) {
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException("Date không được để trống!");
        }
        return tasksDAO.getTasks(date, userId);
    }

    /**
     * Kiểm tra xem có task nào của user vào ngày cho trước không.
     *
     * @param date Ngày ở định dạng "yyyy-MM-dd"
     * @param userId ID của người dùng
     * @return true nếu tồn tại task, false nếu không có
     */
    public boolean hasTaskOnDate(String date, int userId) {
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException("Date không được để trống!");
        }
        return tasksDAO.hasTasks(date, userId);
    }

    /**
     * Tạo mới một task. Task phải có tiêu đề hợp lệ và chứa userId > 0.
     *
     * @param task Task cần tạo
     */
    public void createTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task không được null!");
        }
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Tiêu đề công việc không được để trống!");
        }
        if (task.getUserId() <= 0) {
            throw new IllegalArgumentException("User ID không hợp lệ!");
        }
        tasksDAO.createTask(task);
    }

    /**
     * Cập nhật một task hiện có. Task phải có task_id hợp lệ (khác 0).
     *
     * @param task Task cần cập nhật
     */
    public void updateTask(Task task) {
        if (task == null || task.getID() == 0) {
            throw new IllegalArgumentException("Task hoặc ID không hợp lệ!");
        }
        tasksDAO.updateTask(task);
    }

    public List<Task> getAllTasksByUser(int userID) {
        if (userID < 0) {
            throw new IllegalArgumentException("Khong ton tai nguoi dung hop le!");
        }
        return tasksDAO.getAllTasksByUser(userID);
    }

    /**
     * Xoá task theo task_id.
     *
     * @param id ID của task cần xoá (phải > 0)
     */
    public void deleteTask(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID phải > 0!");
        }
        tasksDAO.deleteTask(id);
    }

    /**
     * Lấy một câu nói truyền cảm hứng dựa vào số day.
     *
     * @param day Số ngày (ví dụ: 1, 2, …)
     * @return Câu nói
     */
    // Nếu có nhu cầu, bạn có thể thêm phương thức
    // public void createOrUpdateNotes(String date, String note) {
    //     tasksDAO.createOrUpdateNotes(date, note);
    // }
    public int getCountByMonthAndStatus(int userId, int month, boolean status) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID không hợp lệ!");
        }
        return tasksDAO.countTasksByMonthAndStatus(userId, month, status);
    }

    public int getCountByCategoryAndMonth(int userId, String category, int month, int year) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID không hợp lệ!");
        }
        if (category == null || category.isEmpty()) {
            throw new IllegalArgumentException("Category không đúng!");
        }
        return tasksDAO.countByCategoryAndMonth(userId, category, month, year);
    }

    public int getOverdueCount(int userId, int month) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID không hợp lệ!");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month phải từ 1 đến 12!");
        }
        return tasksDAO.countOverdueTasks(userId, month);
    }

    /**
     * Lấy tất cả các task SẮP DIỄN RA (trong tương lai) và CHƯA ĐƯỢC THÔNG BÁO "sắp diễn ra".
     * Trong thực tế, bạn sẽ truy vấn CSDL.
     * @param userId ID của người dùng (trong bản mock này, nếu userId là 0 sẽ lấy tất cả)
     * @return Danh sách các task phù hợp
     */
    public List<Task> getUpcomingAndUnnotifiedTasks(int userId) {
        // System.out.println("TaskService: Lấy các task sắp diễn ra và chưa thông báo cho user " + (userId == 0 ? "ALL" : userId));
        LocalDateTime now = LocalDateTime.now();
        return task.stream()
                .filter(task -> task.getDateTime() != null && task.getDateTime().isAfter(now)) // Task trong tương lai
                .filter(task -> !task.isIsDone()) // Chưa được thông báo "sắp diễn ra"
                .filter(task -> userId == 0 || task.getUserId() == userId) // Lọc theo userId nếu userId khác 0
                .collect(Collectors.toList());
    }

    /**
     * Đánh dấu một task là đã được gửi thông báo "sắp diễn ra".
     * Trong thực tế, bạn sẽ cập nhật trạng thái này trong CSDL.
     * @param taskId ID của task
     */
    public void markTaskAsNotifiedForUpcoming(int taskId) {
        System.out.println("TaskService: Đánh dấu task ID " + taskId + " là đã thông báo (sắp diễn ra).");
        task.stream()
            .filter(t -> t.getID() == taskId)
            .findFirst()
            .ifPresent(t -> t.setIsDone(true));
        // Trong CSDL: UPDATE tasks SET notifiedUpcoming = true WHERE id = taskId;
    }
    
}
