package com.company.medicine.service;

import com.company.medicine.entity.Medicine;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MedicineListPdfExportService {

    private static final Logger log = LoggerFactory.getLogger(MedicineListPdfExportService.class);
    private static final int FONT_SIZE = 9;

    public byte[] exportMedicineListToPdf(List<Medicine> medicines) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            pdfDocument.setDefaultPageSize(PageSize.LEGAL.rotate());

            Document document = new Document(pdfDocument);

            // Add header with reduced font size
            document.add(new Paragraph("Andres Pharmacy Ormoc").setBold().setFontSize(14).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Brgy. Mabini, Ormoc City, Leyte 6541").setFontSize(FONT_SIZE).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))).setFontSize(FONT_SIZE).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Medicine List").setBold().setFontSize(12).setTextAlignment(TextAlignment.CENTER).setMarginTop(10));

            document.add(new Paragraph("Total Medicines: " + medicines.size()).setFontSize(FONT_SIZE));

            // Create table
            Table table = new Table(UnitValue.createPercentArray(new float[]{2, 1, 2, 1, 1, 1, 1, 1}));
            table.setWidth(UnitValue.createPercentValue(100));
            table.setMarginTop(5);
            table.setFontSize(FONT_SIZE);

            // Define light soft blue color
            DeviceRgb lightSoftBlue = new DeviceRgb(173, 216, 230);

            // Add table headers
            String[] headers = {"Brand Name", "Review Priority", "Active Ingredient", "Strength", "Dosage Form", "Marketing Status", "Route", "Price"};
            for (String header : headers) {
                Cell cell = new Cell().add(new Paragraph(header).setBold().setFontSize(FONT_SIZE));
                cell.setBackgroundColor(lightSoftBlue);
                cell.setTextAlignment(TextAlignment.CENTER);
                cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
                cell.setPadding(2); // Add minimal padding
                table.addHeaderCell(cell);
            }

            // Add data to table
            for (Medicine medicine : medicines) {
                addCenteredCell(table, truncateText(medicine.getBrandName(), 30));
                addCenteredCell(table, truncateText(medicine.getReviewPriority(), 20));
                addCenteredCell(table, truncateText(medicine.getActiveIngredientName(), 30));
                addCenteredCell(table, truncateText(medicine.getActiveIngredientStrength(), 20));
                addCenteredCell(table, truncateText(medicine.getDosageForm(), 20));
                addCenteredCell(table, truncateText(medicine.getMarketingStatus(), 20));
                addCenteredCell(table, truncateText(medicine.getRoute(), 20));
                addCenteredCell(table, truncateText(String.valueOf(medicine.getPrice()), 15));
            }
            document.add(table);

            // Add signature line
            document.add(new Paragraph("Checked by: __________________________").setMarginTop(20).setTextAlignment(TextAlignment.RIGHT).setFontSize(FONT_SIZE));

            document.close();
        } catch (Exception e) {
            log.error("Error generating PDF", e);
        }

        return byteArrayOutputStream.toByteArray();
    }

    private void addCenteredCell(Table table, String content) {
        if (content == null) content = "";
        Cell cell = new Cell().add(new Paragraph(content).setFontSize(FONT_SIZE));
        cell.setTextAlignment(TextAlignment.CENTER);
        cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        cell.setPadding(2); // Add minimal padding
        table.addCell(cell);
    }

    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
}