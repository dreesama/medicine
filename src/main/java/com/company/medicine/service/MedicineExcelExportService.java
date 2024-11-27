package com.company.medicine.service;

import com.company.medicine.entity.Medicine;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@SuppressWarnings("unused")
public class MedicineExcelExportService {

    @SuppressWarnings("unused")
    public byte[] exportMedicinesToExcel(List<Medicine> medicines) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Medicines");

            // Create header row
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = createHeaderStyle(workbook);

            String[] headers = {
                    "Brand Name",
                    "Review Priority",
                    "Active Ingredient",
                    "Strength",
                    "Dosage Form",
                    "Marketing Status",
                    "Route",
                    "Price"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }

            // Create data rows
            int rowNum = 1;
            for (Medicine medicine : medicines) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(medicine.getBrandName());
                row.createCell(1).setCellValue(medicine.getReviewPriority());
                row.createCell(2).setCellValue(medicine.getActiveIngredientName());
                row.createCell(3).setCellValue(medicine.getActiveIngredientStrength());
                row.createCell(4).setCellValue(medicine.getDosageForm());
                row.createCell(5).setCellValue(medicine.getMarketingStatus());
                row.createCell(6).setCellValue(medicine.getRoute());

                Cell priceCell = row.createCell(7);
                if (medicine.getPrice() != null) {
                    priceCell.setCellValue(medicine.getPrice().doubleValue());
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to export medicines to Excel", e);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }
}