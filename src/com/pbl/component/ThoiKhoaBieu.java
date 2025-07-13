
package com.pbl.component; // Giữ nguyên package của bạn

import com.pbl.event.ComponentImageExporter;
import com.pbl.event.EventThoiKhoaBieu;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
// Bổ sung import cho BasicStroke nếu muốn dùng đường kẻ dày hơn
// import java.awt.BasicStroke;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThoiKhoaBieu extends JPanel {
    private JButton btnUpload;
    private JButton btnDownload;
    private Map<Integer, Map<Integer, String>> displayData;

    // --- Trường nhập liệu tạm thời để chỉnh sửa ---
    private JTextField editingField;
    private int editingDay = -1;
    private int editingSlot = -1;
    private int currentSlotStartY = -1; // Lưu lại Y bắt đầu của lưới để tính toán click
    private int currentHeaderY = -1; // Lưu lại Y của header
    private int currentSeparatorY = -1; // Lưu lại Y của separator

    // --- HẰNG SỐ KÍCH THƯỚC VÀ VỊ TRÍ ---
    public  int startX = 70;
    public final int startYHeader = 80; // Sẽ được tính toán lại dựa trên title
    public  int tietColumnWidth = 50;
    public  int cellWidth = 143;
    public  int cellHeight = 50;
    public  int headerHeight = 30;
    public  int titleMarginTop = 25;
    public  int padding = 4;
    public  int cellGap = 3;
    public  int separatorHeight = 5;

    // --- Hằng số cho Button ---
    private final int buttonWidth = 100;
    private final int buttonHeight = 30;
    private final int buttonMarginTop = 10;
    private final int buttonMarginRight = 15;
    private final Color buttonBgColor = new Color(0xE3, 0xB8, 0xB8);
    private final Color buttonFgColor = new Color(0x03, 0x4C, 0x36);
    

    // --- Font chữ ---
    private final Font titleFont = new Font("SansSerif", Font.BOLD, 20);
    private final Font headerFont = new Font("SansSerif", Font.BOLD, 14);
    private final Font slotFont = new Font("SansSerif", Font.BOLD, 11);
    private final Font contentFont = new Font("SansSerif", Font.PLAIN, 16); // Font cho cả ô và trường chỉnh sửa

    // --- Màu sắc ---
    private final Color backgroundColor = new Color(235, 245, 255);
    private final Color titleColor = new Color(0, 51, 102);
    private final Color headerBgColor = new Color(0xB4, 0xEB, 0xE6);
    private final Color headerBorderColor = new Color(0x64, 0x95, 0xED);
    private final Color headerTextColor = new Color(0x00, 0x1C, 0x30);
    private final Color slotBgColor = new Color(0xE3, 0xF6, 0xFF);
    private final Color slotBorderColor = headerBorderColor;
    private final Color slotTextColor = Color.BLACK;
    private final Color gridBgColor = new Color(252, 252, 252);
    private final Color gridBorderColor = new Color(200, 200, 200);
    private final Color gridTextColor = Color.BLACK;
    private final Color separatorColor = new Color(150, 180, 210);
    private final Color editingFieldBgColor = new Color(255, 255, 220); // Nền cho ô đang sửa

    public ThoiKhoaBieu() {
        // *** Quan trọng: Đặt layout thành null ***
        setLayout(null);
        setOpaque(true); // Đảm bảo setBackground có hiệu lực
        setBackground(backgroundColor);

        // --- Khởi tạo và cấu hình Button ---
        btnUpload = new JButton("Upload");
        btnUpload.setFont(headerFont);
        btnUpload.setBackground(buttonBgColor);
        btnUpload.setForeground(buttonFgColor);
        btnUpload.setBorderPainted(false); // Bỏ viền
        btnUpload.setFocusPainted(false); // Bỏ hiệu ứng focus
        
        btnDownload = new JButton("Download");
        btnDownload.setFont(headerFont);
        btnDownload.setBackground(buttonBgColor);
        btnDownload.setForeground(buttonFgColor);
        btnDownload.setBorderPainted(false); // Bỏ viền
        btnDownload.setFocusPainted(false); // Bỏ hiệu ứng focus

        // Tạm thời đặt vị trí button, sẽ cập nhật sau
        int preferredWidth = calculatePreferredSize().width;
        int buttonX = preferredWidth - buttonWidth - buttonMarginRight ;
        btnUpload.setBounds(buttonX, buttonMarginTop, buttonWidth, buttonHeight);
        
        btnDownload.setBounds(buttonX - 150, buttonMarginTop, buttonWidth, buttonHeight);

        btnUpload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Upload button clicked!");
                // Hủy chỉnh sửa nếu đang diễn ra trước khi mở dialog mới
                cancelEdit();
                try {
                    // Truyền chính đối tượng ThoiKhoaBieu hiện tại (this) vào constructor
                    new EventThoiKhoaBieu(ThoiKhoaBieu.this);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(ThoiKhoaBieu.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) { // Bắt thêm lỗi chung
                    Logger.getLogger(ThoiKhoaBieu.class.getName()).log(Level.SEVERE, "Lỗi khi xử lý upload", ex);
                    JOptionPane.showMessageDialog(ThoiKhoaBieu.this, "Lỗi khi xử lý upload:\n" + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


         btnDownload.addActionListener(e -> {
        
         // 'this' đầu tiên là component cần xuất (chính là ThoiKhoaBieu)
         // 'this' thứ hai là component cha để neo dialog (cũng là ThoiKhoaBieu)
         ComponentImageExporter.exportComponentAsImage(this, this);
        });
        
        // *** Thêm nút vào panel ***
        add(btnUpload);
        add(btnDownload);
        // --- Thêm MouseListener để xử lý click chỉnh sửa ---
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Nếu đang chỉnh sửa ô khác, hoàn thành hoặc hủy bỏ trước
                if (editingField != null && editingField.isVisible()) {
                    // Click vào chính ô đang sửa không làm gì cả
                    if (editingField.getBounds().contains(e.getPoint())) {
                        return;
                    }
                    // Click ra ngoài ô đang sửa -> xác nhận chỉnh sửa
                    confirmEdit();
                }

                // Chỉ xử lý click đơn
                if (e.getClickCount() == 1 && editingField == null) {
                     findCellAndStartEditing(e.getX(), e.getY());
                }
            }
        });

        setData(null); // Khởi tạo với dữ liệu rỗng và tính lại preferred size
    }

    // Set kích thước
    
    
    public void setStratX(int startX){
        this.startX = startX;
        refreshLayoutAndPaint();
    }
    
    public void SetTietColumnWidth(int tietColumnWidth){
        this.tietColumnWidth = Math.max(10, tietColumnWidth);
        refreshLayoutAndPaint();
    }
    
    public void setCellWidth(int cellWidth){
        this.cellWidth = Math.max(50, cellWidth);
        refreshLayoutAndPaint();
    }
    
    public void setCellHeight(int cellHeight){
        this.cellHeight = Math.max(30, cellHeight);
        refreshLayoutAndPaint();
    }
    
    public void setHeaderHeight(int headerHeight){
        this.headerHeight = Math.max(15, headerHeight);
        refreshLayoutAndPaint();
    }
    
    public void setTitleMarginTop(int titleMarginTop){
        this.titleMarginTop = Math.max(0, titleMarginTop);
        refreshLayoutAndPaint();
    }
    
    public void setPadding(int padding){
        this.padding = Math.max(0, padding);
        refreshLayoutAndPaint();
    }    

    public void setCellGap(int cellGap){
        this.cellGap = Math.max(0, cellGap);
        refreshLayoutAndPaint();
    }    

    public void setSeparatorHeight(int separatorHeight){
        this.separatorHeight = Math.max(1, separatorHeight);
        refreshLayoutAndPaint();
    }    
        private void refreshLayoutAndPaint(){
        cancelEdit();
        setPreferredSize(calculatePreferredSize());
        updateButtonPosition();
        revalidate();
        repaint();
    }
    // --- Ghi đè addNotify để cập nhật vị trí button ---
    @Override
    public void addNotify() {
        super.addNotify();
        updateButtonPosition();
    }

    // --- Ghi đè setBounds để cập nhật vị trí button ---
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        updateButtonPosition();
        // Nếu đang chỉnh sửa, có thể cần cập nhật vị trí editingField ở đây nếu muốn nó di chuyển theo panel
        // Tuy nhiên, đơn giản hơn là hủy chỉnh sửa khi panel resize
        cancelEdit();
    }

    // --- Hàm helper để cập nhật vị trí button ---
    private void updateButtonPosition() {
        if (btnUpload != null && btnDownload != null) {
            int panelWidth = getWidth();
            if(panelWidth <=0){
                panelWidth = getPreferredSize().width;
            }
            if (panelWidth > 0) {
                int uploadbuttonX = panelWidth - buttonWidth - buttonMarginRight;
                btnUpload.setBounds(uploadbuttonX, buttonMarginTop, buttonWidth, buttonHeight);
                
                int downloadbuttonX = uploadbuttonX - buttonWidth - 15;
                btnDownload.setBounds(downloadbuttonX, buttonMarginTop, buttonWidth, buttonHeight);
                
            }else{
                btnUpload.setBounds(calculatePreferredSize().width - buttonWidth - buttonMarginRight, buttonMarginTop, buttonWidth,buttonHeight);
                btnDownload.setBounds(calculatePreferredSize().width - buttonWidth - buttonMarginRight- buttonWidth - 10, buttonMarginTop, buttonWidth,buttonHeight);
            }
        }
    }

    public void setData(Map<Integer, Map<Integer, String>> data) {
        // Hủy chỉnh sửa trước khi đặt dữ liệu mới
        cancelEdit();
        this.displayData = data != null ? data : new HashMap<>();
        setPreferredSize(calculatePreferredSize());
        updateButtonPosition();
        revalidate();
        repaint();
    }

    private Dimension calculatePreferredSize() {
        int titleH = fmHeight(titleFont);
        int headerYCalc = titleMarginTop + titleH + 10; // Y của header (tính toán)
        int slotStartYCalc = headerYCalc + headerHeight + cellGap; // Y bắt đầu của lưới (tính toán)
        int separatorYCalc = slotStartYCalc + 5 * (cellHeight + cellGap); // Y của separator (tính toán)

        // Chiều rộng
        int width = (startX - tietColumnWidth - cellGap) // Khoảng trống trái
                  + tietColumnWidth // Cột tiết
                  + 7 * (cellWidth + cellGap) // 7 cột ngày + gap
                  + 10; // Lề phải

        // Chiều cao
        int height = titleMarginTop // Lề trên
                   + titleH // Tiêu đề
                   + 10 // Khoảng cách dưới tiêu đề
                   + headerHeight // Header
                   + cellGap // Gap dưới header
                   + 5 * (cellHeight + cellGap) // 5 tiết đầu + 5 gap
                   + separatorHeight // Hàng phân cách
                   + 5 * (cellHeight + cellGap) // 5 tiết sau + 5 gap
                   + 10; // Lề dưới

        // Đảm bảo chiều rộng đủ chứa nút Upload
        int minWidthForButton = startX + 7 * (cellWidth + cellGap) + buttonMarginRight; // Tính từ startX + grid width + margin
     

        return new Dimension(Math.max(width, minWidthForButton), height);
    }

    private int fmHeight(Font font) {
        // Lấy FontMetrics từ component hiện tại nếu có thể, nếu không tạo tạm
        Graphics g = getGraphics();
        if (g != null) {
            FontMetrics fm = g.getFontMetrics(font);
            g.dispose();
            return fm.getHeight();
        } else {
            // Fallback nếu graphics chưa sẵn sàng (ít xảy ra khi tính preferred size)
             return (int) (font.getSize() * 1.3); // Ước lượng
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        try {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // --- Vẽ Tiêu Đề ---
            g2d.setFont(titleFont);
            FontMetrics fmTitle = g2d.getFontMetrics();
            String title = "THỜI KHÓA BIỂU";
            int titleWidth = fmTitle.stringWidth(title);
            int titleContentWidth = 7 * (cellWidth + cellGap) - cellGap;
            int titleX = startX + (titleContentWidth - titleWidth) / 2;
            int titleY = titleMarginTop + fmTitle.getAscent();
            g2d.setColor(titleColor);
            g2d.drawString(title, titleX, titleY);

            // --- Tính toán và lưu lại các vị trí Y quan trọng ---
            currentHeaderY = titleY + fmTitle.getDescent() + 10; // Y bắt đầu của hàng header
            currentSlotStartY = currentHeaderY + headerHeight + cellGap; // Y bắt đầu của hàng tiết 1
            currentSeparatorY = currentSlotStartY + 5 * (cellHeight + cellGap); // Y bắt đầu của separator

            // --- Vẽ Header Bảng ---
            String[] headers = { "Tiết", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ Nhật" };
            g2d.setFont(headerFont);
            FontMetrics fmHeader = g2d.getFontMetrics();
            for (int i = 0; i < headers.length; i++) {
                int currentHeaderX;
                int currentHeaderWidth;
                if (i == 0) { // Cột "Tiết"
                    currentHeaderWidth = tietColumnWidth;
                    currentHeaderX = startX - tietColumnWidth - cellGap;
                } else { // Các cột ngày
                    currentHeaderWidth = cellWidth;
                    currentHeaderX = startX + (i - 1) * (cellWidth + cellGap);
                }
                g2d.setColor(headerBgColor);
                g2d.fillRect(currentHeaderX, currentHeaderY, currentHeaderWidth, headerHeight);
                g2d.setColor(headerBorderColor);
                g2d.drawRect(currentHeaderX, currentHeaderY, currentHeaderWidth, headerHeight);
                int textWidth = fmHeader.stringWidth(headers[i]);
                int xText = currentHeaderX + (currentHeaderWidth - textWidth) / 2;
                int yText = currentHeaderY + (headerHeight - fmHeader.getHeight()) / 2 + fmHeader.getAscent();
                g2d.setColor(headerTextColor);
                g2d.drawString(headers[i], xText, yText);
            }

            // --- Vẽ Cột Số Tiết ---
            g2d.setFont(slotFont);
            FontMetrics fmSlot = g2d.getFontMetrics();
            int tietColumnX = startX - tietColumnWidth - cellGap;
            for (int i = 0; i < 10; i++) {
                int currentSlotY = calculateRowY(currentSlotStartY, i); // Dùng Y đã lưu
                g2d.setColor(slotBgColor);
                g2d.fillRect(tietColumnX, currentSlotY, tietColumnWidth, cellHeight);
                g2d.setColor(slotBorderColor);
                g2d.drawRect(tietColumnX, currentSlotY, tietColumnWidth, cellHeight);
                String slotText = String.valueOf(i + 1);
                int textWidth = fmSlot.stringWidth(slotText);
                int xText = tietColumnX + (tietColumnWidth - textWidth) / 2;
                int yText = currentSlotY + (cellHeight - fmSlot.getHeight()) / 2 + fmSlot.getAscent();
                g2d.setColor(slotTextColor);
                g2d.drawString(slotText, xText, yText);
            }

            // --- Vẽ Hàng Phân Cách ---
            int separatorStartX = tietColumnX;
            int separatorEndX = startX + 7 * (cellWidth + cellGap) - cellGap;
            int separatorWidth = separatorEndX - separatorStartX;
            g2d.setColor(separatorColor);
            g2d.fillRect(separatorStartX, currentSeparatorY, separatorWidth, separatorHeight); // Dùng Y đã lưu

            // --- Vẽ Lưới và Dữ liệu TKB ---
            g2d.setFont(contentFont);
            FontMetrics fmContent = g2d.getFontMetrics();
            int availableWidth = cellWidth - 2 * padding;

            if (displayData != null) {
                for (int dayIndex = 0; dayIndex < 7; dayIndex++) {
                    Map<Integer, String> daySchedule = displayData.get(dayIndex);
                    int currentX = startX + dayIndex * (cellWidth + cellGap);

                    for (int slotIndex = 0; slotIndex < 10; slotIndex++) {
                        int currentY = calculateRowY(currentSlotStartY, slotIndex); // Dùng Y đã lưu

                        // Vẽ nền ô
                        g2d.setColor(gridBgColor);
                        g2d.fillRect(currentX, currentY, cellWidth, cellHeight);
                        // Vẽ viền ô
                        g2d.setColor(gridBorderColor);
                        g2d.drawRect(currentX, currentY, cellWidth, cellHeight);

                        // Vẽ nội dung nếu có và *không phải* ô đang được chỉnh sửa
                        if (dayIndex != editingDay || slotIndex != editingSlot) {
                            if (daySchedule != null) {
                                String cellContent = daySchedule.getOrDefault(slotIndex, "");
                                if (!cellContent.isEmpty()) {
                                    g2d.setColor(gridTextColor);
                                    drawWrappedText(g2d, cellContent, currentX + padding, currentY + padding, availableWidth, cellHeight - 2 * padding, fmContent);
                                }
                            }
                        }
                    }
                }
            } else {
                // Vẽ lưới trống nếu displayData là null
                 for (int dayIndex = 0; dayIndex < 7; dayIndex++) {
                    int currentX = startX + dayIndex * (cellWidth + cellGap);
                    for (int slotIndex = 0; slotIndex < 10; slotIndex++) {
                        int currentY = calculateRowY(currentSlotStartY, slotIndex);
                        g2d.setColor(gridBgColor);
                        g2d.fillRect(currentX, currentY, cellWidth, cellHeight);
                        g2d.setColor(gridBorderColor);
                        g2d.drawRect(currentX, currentY, cellWidth, cellHeight);
                    }
                }
            }
        } finally {
            g2d.dispose();
        }
    }

     // Hàm vẽ text có xử lý xuống dòng tự động và theo dấu ';'
     private void drawWrappedText(Graphics2D g2d, String text, int x, int y, int maxWidth, int maxHeight, FontMetrics fm) {
        int lineHeight = fm.getHeight();
        int currentY = y + fm.getAscent(); // Y bắt đầu vẽ dòng đầu tiên
        String[] manualLines = text.split(";"); // Tách theo dấu ';' trước

        for (String manualLine : manualLines) {
            manualLine = manualLine.trim();
            if (manualLine.isEmpty()) {
                 // Nếu muốn giữ khoảng trống dòng khi có ;; thì tăng currentY
                 // currentY += lineHeight;
                 // if (currentY > y + maxHeight - fm.getDescent()) break; // Kiểm tra tràn chiều cao
                 continue; // Bỏ qua dòng rỗng
            }

            String[] words = manualLine.split("\\s+");
            StringBuilder currentLine = new StringBuilder();
            for (String word : words) {
                String testLine = currentLine.length() > 0 ? currentLine + " " + word : word;
                if (fm.stringWidth(testLine) <= maxWidth) {
                    if (currentLine.length() > 0) currentLine.append(" ");
                    currentLine.append(word);
                } else {
                    // Vẽ dòng hiện tại
                    if (currentY <= y + maxHeight - fm.getDescent()) { // Kiểm tra tràn chiều cao
                         g2d.drawString(currentLine.toString(), x, currentY);
                    } else {
                        return; // Dừng nếu hết chỗ
                    }
                    currentY += lineHeight; // Xuống dòng
                    currentLine = new StringBuilder(word); // Bắt đầu dòng mới
                    // Kiểm tra từ mới có quá dài không
                    if (fm.stringWidth(currentLine.toString()) > maxWidth) {
                         // Cắt bớt hoặc hiển thị '...' nếu muốn
                         // Hiện tại: Bỏ qua phần còn lại của từ quá dài này trong dòng mới
                         currentLine.setLength(0);
                         break; // Chuyển sang manualLine tiếp theo nếu có
                    }
                }
            }
             // Vẽ phần còn lại của dòng cuối cùng
            if (currentLine.length() > 0 && currentY <= y + maxHeight - fm.getDescent()) {
                g2d.drawString(currentLine.toString(), x, currentY);
                currentY += lineHeight; // Chuẩn bị cho manualLine tiếp theo (nếu có)
            }
             // Kiểm tra tràn trước khi bắt đầu vòng lặp manualLine mới
            if (currentY > y + maxHeight - fm.getDescent()) break;
        }
    }

    // Hàm tính toán vị trí Y của một hàng (slotIndex từ 0 đến 9)
    private int calculateRowY(int baseY, int rowIndex) {
        if (rowIndex < 5) { // Tiết 1-5
            return baseY + rowIndex * (cellHeight + cellGap);
        } else { // Tiết 6-10 (sau separator)
            // Phải dùng currentSeparatorY nếu đã được tính toán và lưu trữ
            int sepY = (currentSeparatorY != -1) ? currentSeparatorY : baseY + 5 * (cellHeight + cellGap);
            return sepY + separatorHeight + cellGap + (rowIndex - 5) * (cellHeight + cellGap);
             // Hoặc tính lại đầy đủ nếu không chắc currentSeparatorY đã đúng:
             // return baseY + 5 * (cellHeight + cellGap) + separatorHeight + cellGap + (rowIndex - 5) * (cellHeight + cellGap);

             // Lưu ý: Công thức gốc thiếu cellGap sau separatorHeight, đã bổ sung ở trên
             // return baseY + 5 * (cellHeight + cellGap) + separatorHeight + (rowIndex - 5) * (cellHeight + cellGap); // Công thức cũ
        }
    }

    // --- Các hàm xử lý chỉnh sửa ---

    private void findCellAndStartEditing(int clickX, int clickY) {
        // Kiểm tra click có nằm trong khu vực lưới chính không
        if (clickX < startX || clickY < currentHeaderY + headerHeight) {
             cancelEdit(); // Click vào header hoặc lề trái/trên
             return;
        }

        // Tính toán dayIndex (0-6)
        int effectiveClickX = clickX - startX;
        int dayIndex = effectiveClickX / (cellWidth + cellGap);
        int xInDayCell = effectiveClickX % (cellWidth + cellGap);

        // Kiểm tra xem có click vào gap ngang không
        if (dayIndex >= 7 || xInDayCell >= cellWidth) {
            cancelEdit(); // Click vào lề phải hoặc gap ngang cuối cùng
            return;
        }

        // Tính toán slotIndex (0-9)
        int slotIndex = -1;
        int yInSlotCell = -1;

         if (clickY >= currentSlotStartY && clickY < currentSeparatorY) { // Click vào vùng 5 tiết đầu
            int relativeY = clickY - currentSlotStartY;
            slotIndex = relativeY / (cellHeight + cellGap);
            yInSlotCell = relativeY % (cellHeight + cellGap);
            if (slotIndex >= 5 || yInSlotCell >= cellHeight) { // Click vào gap dọc hoặc dưới tiết 5 trước separator
                cancelEdit(); return;
            }
        } else if (clickY >= currentSeparatorY && clickY < currentSeparatorY + separatorHeight + cellGap) { // Click vào separator hoặc gap ngay dưới nó
             cancelEdit(); return;
        } else if (clickY >= currentSeparatorY + separatorHeight + cellGap) { // Click vào vùng 5 tiết sau
             int relativeY = clickY - (currentSeparatorY + separatorHeight + cellGap);
             slotIndex = 5 + (relativeY / (cellHeight + cellGap));
             yInSlotCell = relativeY % (cellHeight + cellGap);
             if (slotIndex >= 10 || yInSlotCell >= cellHeight) { // Click vào gap dọc hoặc dưới tiết 10
                 cancelEdit(); return;
             }
        } else { // Click phía trên lưới hoặc đâu đó không hợp lệ
            cancelEdit(); return;
        }


        // Nếu tính toán hợp lệ, bắt đầu chỉnh sửa
        if (dayIndex >= 0 && dayIndex < 7 && slotIndex >= 0 && slotIndex < 10) {
             startEditing(dayIndex, slotIndex);
        } else {
            cancelEdit(); // Hủy nếu có gì đó sai sót trong tính toán
        }
    }

    private void startEditing(int day, int slot) {
        if (editingField != null) {
            confirmEdit(); // Hoàn thành chỉnh sửa cũ nếu có
        }

        editingDay = day;
        editingSlot = slot;

        // Tính toán lại tọa độ chính xác của ô
        int cellX = startX + editingDay * (cellWidth + cellGap);
        int cellY = calculateRowY(currentSlotStartY, editingSlot);

        editingField = new JTextField();
        editingField.setFont(contentFont); // Dùng font nội dung
//        editingField.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1)); // Viền khác biệt
//        editingField.setBackground(editingFieldBgColor);
        editingField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLUE, 1), // Viền xanh bên ngoài
                BorderFactory.createEmptyBorder(2, padding, 2, padding) // Thêm padding bên trong (2px trên/dưới, 'padding' pixel trái/phải)
        ));
        editingField.setBackground(editingFieldBgColor);
        // Lấy nội dung hiện tại
        String currentText = displayData.computeIfAbsent(editingDay, k -> new HashMap<>())
                                     .getOrDefault(editingSlot, "");
        editingField.setText(currentText);

        // Đặt vị trí và kích thước
        editingField.setBounds(cellX, cellY, cellWidth, cellHeight);

        // Thêm listeners cho editingField
        editingField.addActionListener(e -> confirmEdit()); // Enter key
        editingField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                // Chỉ xác nhận nếu focus không chuyển sang thành phần khác *bên trong* ThoiKhoaBieu
                // (ví dụ: click vào nút Upload). Nếu focus mất hoàn toàn thì xác nhận.
                 Component oppositeComponent = e.getOppositeComponent();
                 if (oppositeComponent == null || !SwingUtilities.isDescendingFrom(oppositeComponent, ThoiKhoaBieu.this)) {
                     // Nếu không phải là do click vào nút Upload thì mới confirm
                     if (oppositeComponent != btnUpload) {
                         confirmEdit();
                     } else {
                         // Nếu click vào nút Upload, chỉ cần ẩn field, không cần lưu
                         // (vì action của nút Upload sẽ xử lý tiếp)
                         // Tuy nhiên, logic đơn giản hơn là cứ confirm luôn
                         confirmEdit();
                     }
                 }
                // Nếu focus chuyển sang nút Upload, confirmEdit sẽ được gọi,
                // sau đó action của nút Upload sẽ được gọi.
            }
        });
        editingField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cancelEdit();
                }
            }
        });

        // Thêm vào panel, làm hiển thị và focus
        add(editingField);
        editingField.selectAll(); // Chọn hết text để dễ thay thế
        editingField.requestFocusInWindow();
        setComponentZOrder(editingField, 0); // Đảm bảo field vẽ trên cùng
        repaint(cellX, cellY, cellWidth, cellHeight); // Vẽ lại khu vực ô để xóa text cũ (nếu cần) và hiển thị field
    }

    private void confirmEdit() {
        if (editingField == null) return;

        String newText = editingField.getText().trim(); // Lấy text mới, bỏ khoảng trắng thừa

        // Cập nhật dữ liệu trong map
        // Nếu map cho ngày đó chưa tồn tại, tạo mới
        displayData.computeIfAbsent(editingDay, k -> new HashMap<>()).put(editingSlot, newText);

        // Xóa field và reset trạng thái
        removeEditingField();

        // Vẽ lại toàn bộ panel để hiển thị thay đổi
        repaint();
    }

    private void cancelEdit() {
        if (editingField == null) return;

        // Chỉ cần xóa field và reset trạng thái, không lưu gì cả
        int oldX = editingField.getX();
        int oldY = editingField.getY();
        int oldW = editingField.getWidth();
        int oldH = editingField.getHeight();

        removeEditingField();

        // Vẽ lại khu vực ô cũ để khôi phục hiển thị ban đầu
        repaint(oldX, oldY, oldW, oldH);
         // Hoặc repaint() toàn bộ nếu đơn giản hơn
         // repaint();
    }

    private void removeEditingField() {
         if (editingField != null) {
             editingField.setVisible(false); // Ẩn trước khi xóa
             remove(editingField);
             editingField = null;
             editingDay = -1;
             editingSlot = -1;
             // Không cần revalidate ở đây vì repaint sẽ xử lý
         }
    }

    // Getter để EventThoiKhoaBieu có thể lấy dữ liệu đã được cập nhật
    public Map<Integer, Map<Integer, String>> getDisplayData() {
        // Có thể trả về bản sao nếu muốn bảo vệ dữ liệu gốc
        // return new HashMap<>(displayData);
        return displayData;
    }
}