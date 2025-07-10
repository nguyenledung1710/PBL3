package com.pbl.service;

import com.pbl.form.Setting_Form;

public class EmailConfig {
    public static final String SENDER_EMAIL = "victomblack2020@gmail.com";
    public static final String SENDER_APP_PASSWORD = "ozkr jkqq dcwr rwee";
    public static final String ADMIN_EMAIL = "";
    public static final String SMTP_HOST = "smtp.gmail.con";
    public static final String SMTP_PORT = "587";
    
    public Setting_Form setting_Form;
    
    // -- Cau hinh thong bao su kien
    public static final long EVENT_CHECK_INTERVAL_MINUTES = 1; // kiem tra sau moi phut
    public static int MINUTES_BEFORE_EVENT_TO_NOTIFY = 1; // thong bao truoc
//    public static final int DEFAULT_USER_ID_FOR_TASKS = 1; // ID người dùng mặc định để lấy task (cần thay đổi logic này nếu quản lý nhiều user)
    public static String Text_Setting= "";
     public static void setMinutesToNotify(int minutes) {
        MINUTES_BEFORE_EVENT_TO_NOTIFY = minutes;
        System.out.println("da duoc cap nhat thanh: " + minutes);
    }
    
      public static void setText(String text) {
        Text_Setting = text;
        System.out.println("da duoc cap nhat thanh: " + text);
    }
     
    
    // --- Tên Ứng dụng ---
     public static final String APP_NAME = "My Calendar";

    // Constructor private để ngăn việc tạo đối tượng từ lớp tiện ích này
    private EmailConfig() {
        throw new IllegalStateException("Utility class");
    }
}