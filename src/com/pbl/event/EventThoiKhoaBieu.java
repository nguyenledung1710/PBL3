    
package com.pbl.event;

import com.pbl.component.ThoiKhoaBieu;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter; // Import for file filter
import java.nio.file.Files; // Import for reading text files
import java.nio.file.Paths; // Import for reading text files
import java.io.IOException; // Import for handling file IO exceptions
import java.nio.charset.StandardCharsets; // Import for UTF-8 encoding
import java.util.List; // Import List for reading lines
import javax.swing.SwingUtilities;


public class EventThoiKhoaBieu {
 private ThoiKhoaBieu targetPanel;


 // Renamed class

    // Hàm chuyển đổi Thứ mấy/CN -> index (0-6)
    private static int getDayIndex(String dayIdentifier) {
        if (dayIdentifier == null) return -1;
        String lowerId = dayIdentifier.trim().toLowerCase();
        switch (lowerId) {
            case "2": return 0;
            case "3": return 1;
            case "4": return 2;
            case "5": return 3;
            case "6": return 4;
            case "7": return 5;
            case "cn":
            case "chủ nhật":
            case "nhật":
                return 6;
            default:
                try {
                    if (Integer.parseInt(lowerId) == 8) return 6;
                } catch (NumberFormatException e) {
                    // Ignore error
                }
                return -1;
        }
    }

    public EventThoiKhoaBieu(ThoiKhoaBieu panelToUpdate)  throws UnsupportedEncodingException {
        this.targetPanel = panelToUpdate;
        // Load OpenCV library if needed
        
        try {
             System.loadLibrary("opencv_java4110");
        } catch (UnsatisfiedLinkError e) {
             System.err.println("Lỗi: Không thể tải thư viện OpenCV. Đảm bảo đường dẫn thư viện đúng.");
             // return; // Consider exiting
        }

        // Set output encoding
        System.setOut(new PrintStream(System.out, true, "UTF-8"));

        // Tesseract data path
     //   String tessDataPath = "E:\\NetBeans_java\\Thoi_khoa_bieu\\src\\tessdata"; // Đường dẫn của bạn
                     String tessDataPath ="E:\\NetBean\\PBL-VIET\\PBL3\\tessdata";
        // --- FILE CHOOSER SETUP ---
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn ảnh TKB hoặc file TXT chứa TKB");
        // Create filters for image and text files
        FileNameExtensionFilter imgFilter = new FileNameExtensionFilter(
            "Ảnh (png, jpg, jpeg, bmp, gif)", "png", "jpg", "jpeg", "bmp", "gif");
        FileNameExtensionFilter txtFilter = new FileNameExtensionFilter(
            "Tệp văn bản (txt)", "txt");
        // Add filters to the chooser
        fileChooser.addChoosableFileFilter(imgFilter);
        fileChooser.addChoosableFileFilter(txtFilter);
        // Set a default filter (optional, e.g., show text files first)
        fileChooser.setFileFilter(txtFilter);
        // Allow selection of files only
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Show the file chooser dialog
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue != JFileChooser.APPROVE_OPTION) {
            System.out.println("Chưa chọn file!");
            return;
        }
        File selectedFile = fileChooser.getSelectedFile();
        String filePath = selectedFile.getAbsolutePath();
        String fileNameLower = selectedFile.getName().toLowerCase(); 

        String[] rowsArray = null; 

       
        try {
            if (fileNameLower.endsWith(".txt")) {
               
                System.out.println("--- Đọc dữ liệu từ file TXT: " + filePath + " ---");
                List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
                
                lines.removeIf(String::isBlank); 
                rowsArray = lines.toArray(new String[0]); 
                System.out.println("Đã đọc " + rowsArray.length + " dòng từ file TXT.");

            } else if (fileNameLower.endsWith(".png") || fileNameLower.endsWith(".jpg") ||
                       fileNameLower.endsWith(".jpeg") || fileNameLower.endsWith(".bmp") ||
                       fileNameLower.endsWith(".gif")) {
               
                System.out.println("--- Thực hiện OCR trên ảnh: " + filePath + " ---");
               
                OCRProcessor ocrProcessor = new OCRProcessor(tessDataPath, "vie");
                
                rowsArray = ocrProcessor.getAllRowsAsArray(filePath);

                
                System.out.println("--- Dữ liệu OCR từ ảnh danh sách ---");
                if (rowsArray != null && rowsArray.length > 0) {
                    for (String row : rowsArray) {
                        System.out.println(row);
                    }
                } else {
                    System.out.println("OCR không trả về dữ liệu hoặc trả về mảng rỗng.");
                    rowsArray = null; 
                }
                System.out.println("--- Kết thúc dữ liệu OCR ---\n");

            } else {
                
                System.err.println("Lỗi: Định dạng file không được hỗ trợ: " + selectedFile.getName());
                return;
            }
        } catch (IOException e) {
             System.err.println("Lỗi khi đọc file: " + e.getMessage());
             e.printStackTrace();
             return; 
        } catch (Exception e) {
             System.err.println("Lỗi không xác định trong quá trình đọc/OCR: " + e.getMessage());
             e.printStackTrace();
             return; 
        }


        
        if (rowsArray == null || rowsArray.length <= 1) { 
             System.err.println("Lỗi: Không có đủ dữ liệu đầu vào (cần ít nhất tiêu đề và 1 dòng dữ liệu) để xử lý.");
             return;
        }


       
        Map<Integer, Map<Integer, String>> gridData = new HashMap<>();
       
        for (int i = 0; i < 7; i++) {
            gridData.put(i, new HashMap<>());
        }

     
        Pattern tkbPattern = Pattern.compile("(Thứ\\s*[\\d]+|CN|Chủ\\s*nhật)\\s*,\\s*(\\d+)(?:\\s*-\\s*(\\d+))?\\s*,\\s*(.+)", Pattern.CASE_INSENSITIVE);

       
        for (int i = 1; i < rowsArray.length; i++) {
            String currentRow = rowsArray[i];
            if (currentRow == null || currentRow.trim().isEmpty()) {
                continue; 
            }
            System.out.println("\nĐang xử lý dòng: " + currentRow);

            String tenLop = "";
            String tkbFullString = "";

            boolean isPhysicalEducation = currentRow.toLowerCase().contains("khoa g.dục thể chất");

            Matcher dayMarkerMatcher = Pattern.compile("Thứ\\s*\\d|CN", Pattern.CASE_INSENSITIVE).matcher(currentRow);
            int tkbStartIndex = -1;
            if (dayMarkerMatcher.find()) {
                tkbStartIndex = dayMarkerMatcher.start();
            }

            // Determine Course Name (tenLop)
            if (isPhysicalEducation) {
                tenLop = "Thể dục";
                System.out.println("  -> Phát hiện GDTC. Gán tên lớp: '" + tenLop + "'");
            } else {
                if (tkbStartIndex != -1) {
                    Matcher creditMatcher = Pattern.compile("\\s+(\\d\\.?\\d?)\\s+").matcher(currentRow);
                    if (creditMatcher.find(0) && creditMatcher.start() < tkbStartIndex) {
                        tenLop = currentRow.substring(0, creditMatcher.start()).trim();
                    } else {
                        tenLop = currentRow.substring(0, tkbStartIndex).trim();
                    }
                } else {
                    tenLop = currentRow.trim();
                    System.err.println("  -> Cảnh báo: Không tìm thấy TKB marker, tên lớp có thể không chính xác.");
                }
                if (tenLop.contains("Nhật")) {
                    tenLop = "Tiếng Nhật";
                }
                 System.out.println("  -> Tên lớp (ước lượng): " + tenLop);
            }

          
            if (tkbStartIndex != -1) {
                int tuanHocStartIndex = currentRow.indexOf("24-25"); // Unreliable marker
                if (tuanHocStartIndex != -1 && tuanHocStartIndex > tkbStartIndex) {
                    tkbFullString = currentRow.substring(tkbStartIndex, tuanHocStartIndex).trim();
                } else {
                    tkbFullString = currentRow.substring(tkbStartIndex).trim();
                    tkbFullString = tkbFullString.replaceAll("\\s*\\d{2}-\\d{2}\\s*:.*$", "").trim();
                }
                System.out.println("  -> Chuỗi TKB đầy đủ: " + tkbFullString);

                String[] individualSchedules = tkbFullString.split("\\s*;\\s*");
                for (String schedulePart : individualSchedules) {
                    Matcher matcher = tkbPattern.matcher(schedulePart);
                    if (matcher.find()) {
                        String dayIdentifier = matcher.group(1).replace("Thứ", "").trim();
                        String startSlotStr = matcher.group(2);
                        String endSlotStr = matcher.group(3);
                        String room = matcher.group(4).trim();
                        int dayIndex = getDayIndex(dayIdentifier);
                        try {
                             int startSlot = Integer.parseInt(startSlotStr);
                             int endSlot = (endSlotStr != null) ? Integer.parseInt(endSlotStr) : startSlot;

                             if (dayIndex != -1) {
                                 String displayString = "";
                                 if (tenLop.equals("Thể dục") || tenLop.equals("Tiếng Nhật")) {
                                     displayString = tenLop;
                                 } else {
                                     displayString = tenLop + " (" + room + ")";
                                 }
                                 for (int slot = startSlot; slot <= endSlot; slot++) {
                                     int slotIndex = slot - 1;
                                     if (slotIndex >= 0 && slotIndex < 10) {
                                         gridData.get(dayIndex).put(slotIndex, displayString);
                                         System.out.println("    -> Đã thêm vào grid: Day=" + dayIndex + ", Slot=" + slotIndex + ", Text=" + displayString);
                                     }
                                 }
                             } else {
                                 System.err.println("    -> Lỗi: Không xác định được ngày từ: '" + dayIdentifier + "' trong '" + schedulePart + "'");
                             }
                        } catch (NumberFormatException e) {
                             System.err.println("    -> Lỗi: Không phân tích được số tiết học: '" + startSlotStr + "' hoặc '" + endSlotStr + "' trong '" + schedulePart + "'");
                        }
                    } else {
                        System.err.println("    -> Lỗi: Không phân tích được TKB part: '" + schedulePart + "'");
                    }
                }
            } else {
                if (!isPhysicalEducation) {
                    System.err.println("  -> Lỗi: Không tìm thấy 'Thứ [số]' hoặc 'CN' trong dòng.");
                } else {
                    System.out.println("  -> Ghi chú: Dòng GDTC này không có thông tin TKB khớp mẫu hoặc không có TKB.");
                }
            }
            System.out.println("---"); 
        }

        
        System.out.println("\n--- Đang chuẩn bị hiển thị TKB dạng đồ họa ---");
     
        final Map<Integer, Map<Integer, String>> finalGridData = gridData;

       SwingUtilities.invokeLater(() -> {
            try {
                if (this.targetPanel != null) { // Kiểm tra xem có panel mục tiêu không
                    System.out.println("--- Cập nhật dữ liệu cho panel TKB hiện có ---");
                    this.targetPanel.setData(finalGridData); // Gọi setData trên panel ĐÃ CÓ
                    // Không cần tạo JFrame mới ở đây trừ khi đó là ý đồ
                } else {
                    System.err.println("Lỗi nghiêm trọng: Không có panel mục tiêu để cập nhật!");
                    // Có thể tạo frame mới ở đây như một phương án dự phòng nếu muốn
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi cập nhật giao diện Swing: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}

