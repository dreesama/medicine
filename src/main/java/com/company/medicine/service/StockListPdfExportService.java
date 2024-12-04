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
@SuppressWarnings("unused")
public class StockListPdfExportService {

    private static final Logger log = LoggerFactory.getLogger(StockListPdfExportService.class);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @SuppressWarnings("unused")
    public byte[] exportStockListToPdf(List<Stock> stocks) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            pdfDocument.setDefaultPageSize(PageSize.LEGAL.rotate());

            Document document = new Document(pdfDocument);

            // Add header information
            addDocumentHeader(document, stocks.size());

            // Create and configure table
            Table table = createTable();
            addTableHeaders(table);
            addTableData(table, stocks);
            document.add(table);

            // Add signature line
            addSignatureLine(document);

            document.close();
            return byteArrayOutputStream.toByteArray();

        } catch (Exception e) {
            log.error("Error generating PDF", e);
            throw new RuntimeException("Failed to export stocks to PDF", e);
        }
    }

    private void addDocumentHeader(Document document, int totalStocks) {
        document.add(new Paragraph("Andres Pharmacy Ormoc")
                .setBold()
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("Brgy. Mabini, Ormoc City, Leyte 6541")
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph(LocalDate.now()
                .format(DateTimeFormatter.ofPattern("MMMM d, yyyy")))
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("Stock List")
                .setBold()
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10));

        document.add(new Paragraph("Total Stock Items: " + totalStocks));
    }

    private Table createTable() {
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 1, 1, 1, 1, 2}));
        table.setWidth(UnitValue.createPercentValue(100));
        table.setMarginTop(5);
        return table;
    }

    private void addTableHeaders(Table table) {
        String[] headers = {
                "Brand Name",
                "Active Ingredient",
                "Strength",
                "Dosage Form",
                "Price",
                "Quantity",
                "Expiration Date"
        };

        DeviceRgb headerColor = new DeviceRgb(173, 216, 230);

        for (String header : headers) {
            Cell cell = new Cell()
                    .add(new Paragraph(header).setBold())
                    .setBackgroundColor(headerColor)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
            table.addHeaderCell(cell);
        }
    }

    private void addTableData(Table table, List<Stock> stocks) {
        for (Stock stock : stocks) {
            addCenteredCell(table, stock.getBrandName());
            addCenteredCell(table, stock.getActiveIngredientName());
            addCenteredCell(table, stock.getActiveIngredientStrength());
            addCenteredCell(table, stock.getDosageForm());
            addCenteredCell(table, stock.getPricePerPackage()!= null ? stock.getPricePerPackage().toString() : "");
            addCenteredCell(table, stock.getPackageQuantity() != null ? stock.getPackageQuantity().toString() : "");
            addCenteredCell(table, formatDate(stock.getExpirationDate()));
        }
    }

    private void addCenteredCell(Table table, String content) {
        Cell cell = new Cell()
                .add(new Paragraph(content))
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
        table.addCell(cell);
    }

    private void addSignatureLine(Document document) {
        document.add(new Paragraph("Checked by: __________________________")
                .setMarginTop(20)
                .setTextAlignment(TextAlignment.RIGHT));
    }

    private String formatDate(java.util.Date date) {
        if (date == null) {
            return "";
        }
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.format(dateFormatter);
    }
}