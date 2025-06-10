
package com.pbl.service;

import com.pbl.model.ModelMessage;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Date;
import java.util.Properties;

public class GmailService {


 public ModelMessage sendEmail(String recipient, String subject, String body) throws MessagingException {
        ModelMessage ms = new ModelMessage(false, "");
        String from = "victomblack2020@gmail.com";
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        String username = "victomblack2020@gmail.com";
        String password = "ozkr jkqq dcwr rwee";    
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // session.setDebug(true); // Bật debug nếu cần

        try {
            // Tạo MimeMessage
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(from)); // Người gửi là username đã cấu hình
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(body);
            mimeMessage.setSentDate(new Date());

            // Gửi email
      
            Transport.send(mimeMessage);
            System.out.println("Email sent successfully!");

   
            ms.setSuccess(true);
        } catch (MessagingException e) {
            if (e.getMessage().equals("Invalid Addresses")) {
                ms.setMessage("Invalid email");
            } else {
                ms.setMessage("Error");
            }
        }
        return ms;
    }



     // (Tùy chọn) Bạn có thể thêm hàm gửi email với attachment hoặc HTML tại đây nếu cần
     // public void sendHtmlEmailWithAttachment(...) { ... }
}

