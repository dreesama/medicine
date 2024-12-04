package com.company.medicine.service;

import com.company.medicine.entity.Stock;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExpiredMedicinePdfExportService {

    private static final Logger log = LoggerFactory.getLogger(ExpiredMedicinePdfExportService.class);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public byte[] exportExpiredMedicinesToPdf(List<Stock> stocks) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            pdfDocument.setDefaultPageSize(PageSize.LEGAL.rotate());

            Document document = new Document(pdfDocument);

            // Add header
            document.add(new Paragraph("Andres Pharmacy Ormoc").setBold().setFontSize(18).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Brgy. Mabini, Ormoc City, Leyte 6541").setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Expired Medicines").setBold().setFontSize(18).setTextAlignment(TextAlignment.CENTER).setMarginTop(10));

            document.add(new Paragraph("Total Expired Medicines: " + stocks.size()));

            // Create table
            Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 1, 1, 1, 1, 2}));
            table.setWidth(UnitValue.createPercentValue(100));
            table.setMarginTop(5);

            // Define light soft blue color
            DeviceRgb lightSoftBlue = new DeviceRgb(173, 216, 230);

            // Add table headers
            String[] headers = {"Brand name", "Active ingredient name", "Strength", "Dosage form", "Price", "Quantity", "Expiration date"};
            for (String header : headers) {
                Cell cell = new Cell().add(new Paragraph(header).setBold());
                cell.setBackgroundColor(lightSoftBlue);
                cell.setTextAlignment(TextAlignment.CENTER);
                cell.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
                table.addHeaderCell(cell);
            }

            // Add data to table
            for (Stock stock : stocks) {
                addCenteredCell(table, stock.getBrandName());
                addCenteredCell(table, stock.getActiveIngredientName());
                addCenteredCell(table, stock.getActiveIngredientStrength());
                addCenteredCell(table, stock.getDosageForm());
                addCenteredCell(table, stock.getPricePerPackage() != null ? stock.getPricePerPackage().toString() : "");
                addCenteredCell(table, stock.getPackageQuantity() != null ? stock.getPackageQuantity().toString() : "");
                String formattedDate = formatDate(stock.getExpirationDate());
                addCenteredCell(table, formattedDate);
            }
            document.add(table);

            // Add signature line
            document.add(new Paragraph("Checked by: __________________________").setMarginTop(20).setTextAlignment(TextAlignment.RIGHT));

            document.close();
        } catch (Exception e) {
            log.error("Error generating PDF", e);
        }

        return byteArrayOutputStream.toByteArray();
    }

    private void addCenteredCell(Table table, String content) {
        Cell cell = new Cell().add(new Paragraph(content));
        cell.setTextAlignment(TextAlignment.CENTER);
        cell.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
        table.addCell(cell);
    }

    private String formatDate(java.util.Date date) {
        if (date == null) {
            return "";
        }
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.format(dateFormatter);
    }
}