
package com.pbl.event;

import net.sourceforge.tess4j.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class OCRProcessor {
    private Tesseract tesseract;

    public OCRProcessor(String tessDataPath, String language) {
        tesseract = new Tesseract();
        tesseract.setDatapath(tessDataPath);
        tesseract.setLanguage(language);
    }

    // Trả về dữ liệu dưới dạng mảng String[]
    public String[] getAllRowsAsArray(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            String extractedText = tesseract.doOCR(imageFile);

         
            return extractedText.split("\n");
        } catch (TesseractException e) {
            System.err.println("Lỗi OCR: " + e.getMessage());
        }
        return new String[]{};
    }

    // Trả về dữ liệu dưới dạng danh sách List<String>
    public List<String> getAllRowsAsList(String imagePath) {
        List<String> rowData = new ArrayList<>();
        try {
            File imageFile = new File(imagePath);
            String extractedText = tesseract.doOCR(imageFile);

            // Chia dữ liệu thành từng dòng và thêm vào danh sách
            String[] rows = extractedText.split("\n");
            for (String row : rows) {
                rowData.add(row);
            }
        } catch (TesseractException e) {
            System.err.println("Lỗi OCR: " + e.getMessage());
        }
        return rowData;
}
}