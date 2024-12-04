package com.company.medicine.service;

import com.company.medicine.entity.Stock;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelExportService {

    private static final Logger log = LoggerFactory.getLogger(ExcelExportService.class);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public byte[] exportStocksToExcel(List<Stock> stocks) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Out of Stock Medicines");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {
                    "Brand name",
                    "Active ingredient name",
                    "Strength",
                    "Dosage form",
                    "Package type",
                    "Units per package",
                    "Package quantity",
                    "Total units",
                    "Price",
                    "Unit price",
                    "Expiration date",
                    "Storage location"
            };

            // Style for header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create styles for numbers and currency
            CellStyle currencyStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            currencyStyle.setDataFormat(format.getFormat("$#,##0.00"));

            // Populate data rows
            int rowNum = 1;
            for (Stock stock : stocks) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(stock.getBrandName());
                row.createCell(1).setCellValue(stock.getActiveIngredientName());
                row.createCell(2).setCellValue(stock.getActiveIngredientStrength());
                row.createCell(3).setCellValue(stock.getDosageForm());
                row.createCell(4).setCellValue(stock.getPackageType() != null ? stock.getPackageType().toString() : "");
                row.createCell(5).setCellValue(stock.getUnitsPerPackage() != null ? stock.getUnitsPerPackage() : 0);
                row.createCell(6).setCellValue(stock.getPackageQuantity() != null ? stock.getPackageQuantity() : 0);
//                row.createCell(7).setCellValue(stock.getTotalUnits() != null ? stock.getTotalUnits() : 0);

                // Price cells with currency formatting
                Cell priceCell = row.createCell(8);
                priceCell.setCellStyle(currencyStyle);
                priceCell.setCellValue(stock.getPricePerPackage() != null ? stock.getPricePerPackage().doubleValue() : 0.0);

                Cell unitPriceCell = row.createCell(9);
                unitPriceCell.setCellStyle(currencyStyle);
                unitPriceCell.setCellValue(stock.getPricePerUnit() != null ? stock.getPricePerUnit().doubleValue() : 0.0);

                row.createCell(10).setCellValue(formatDate(stock.getExpirationDate()));
            }

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            log.error("Error generating Excel file", e);
            return new byte[0];
        }
    }

    private String formatDate(java.util.Date date) {
        if (date == null) {
            return "";
        }
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.format(dateFormatter);
    }
}