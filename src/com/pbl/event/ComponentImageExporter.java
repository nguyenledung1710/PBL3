package com.pbl.event; // Hoặc com.pbl.util

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class ComponentImageExporter {

    // Logger để ghi lỗi (tùy chọn)
    private static final Logger LOGGER = Logger.getLogger(ComponentImageExporter.class.getName());

    /**
     * Xuất một JComponent thành file ảnh (PNG hoặc JPG).
     *
     * @param component       Component cần xuất ảnh.
     * @param parentComponent Component cha để neo các hộp thoại (JFileChooser, JOptionPane).
     * Thường là cửa sổ hoặc panel chứa component.
     */
    public static void exportComponentAsImage(JComponent component, Component parentComponent) {
        if (component == null) {
            LOGGER.log(Level.WARNING, "Component to export is null.");
            JOptionPane.showMessageDialog(parentComponent, "Không có component để xuất ảnh.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
  


        // Thêm bộ lọc file ảnh
        FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG Images (*.png)", "png");
        FileNameExtensionFilter jpgFilter = new FileNameExtensionFilter("JPG Images (*.jpg, *.jpeg)", "jpg", "jpeg");
        chooser.addChoosableFileFilter(pngFilter);
        chooser.addChoosableFileFilter(jpgFilter);
        chooser.setFileFilter(pngFilter); // Đặt PNG làm mặc định

        // Đặt tên file gợi ý (tùy chọn)
        chooser.setSelectedFile(new File("ThoiKhoaBieu.png"));

        int result = chooser.showSaveDialog(parentComponent);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            String selectedFormat = "png"; // Mặc định là png

            // Xác định định dạng dựa trên bộ lọc đã chọn
            if (chooser.getFileFilter() == jpgFilter) {
                selectedFormat = "jpg";
            }
            // Đảm bảo tên file có đuôi đúng với định dạng đã chọn
            String filePath = selectedFile.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith("." + selectedFormat)) {
                selectedFile = new File(filePath + "." + selectedFormat);
            }

            // Kiểm tra kích thước component
            int width = component.getWidth();
            int height = component.getHeight();
            if (width <= 0 || height <= 0) {
                 LOGGER.log(Level.WARNING, "Component has invalid dimensions (width={0}, height={1})", new Object[]{width, height});
                 JOptionPane.showMessageDialog(parentComponent, "Component không có kích thước hợp lệ để xuất ảnh.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                 return;
            }


            // Tạo ảnh BufferedImage
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();

            // --- Cải thiện chất lượng ảnh ---
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            // ---------------------------------

            // Vẽ component lên ảnh
            // quan trọng: Vẽ nền trước nếu component không phải là opaque
            // hoặc nếu muốn đảm bảo nền trắng cho JPG
            if (!component.isOpaque() || "jpg".equalsIgnoreCase(selectedFormat)) {
                g2d.setColor(component.getBackground() != null ? component.getBackground() : Color.WHITE);
                g2d.fillRect(0, 0, width, height);
            }
            component.print(g2d); // Sử dụng print() thay vì paint() cho output
            g2d.dispose(); // Giải phóng tài nguyên graphics

            // Xử lý riêng cho JPG (vì JPG không hỗ trợ kênh alpha/trong suốt)
            BufferedImage finalImage = image;
            if ("jpg".equalsIgnoreCase(selectedFormat)) {
                // Tạo ảnh mới với định dạng RGB (không có alpha) và vẽ ảnh ARGB lên đó
                 finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                 Graphics2D gRgb = finalImage.createGraphics();
                 gRgb.setColor(Color.WHITE); // Đảm bảo nền trắng hoàn toàn
                 gRgb.fillRect(0,0,width, height);
                 gRgb.drawImage(image, 0, 0, null);
                 gRgb.dispose();
            }


            // Lưu ảnh ra file
            try {
                boolean success = ImageIO.write(finalImage, selectedFormat, selectedFile);
                if (success) {
                    JOptionPane.showMessageDialog(parentComponent, "Xuất ảnh thành công!\nĐã lưu tại: " + selectedFile.getAbsolutePath(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                     LOGGER.log(Level.SEVERE, "ImageIO.write returned false for format: {0}", selectedFormat);
                     JOptionPane.showMessageDialog(parentComponent, "Không thể ghi file ảnh với định dạng đã chọn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Lỗi khi lưu ảnh", ex);
                JOptionPane.showMessageDialog(parentComponent, "Lỗi khi lưu ảnh:\n" + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}