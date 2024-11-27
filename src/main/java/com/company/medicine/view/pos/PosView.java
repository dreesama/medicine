package com.company.medicine.view.pos;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.StreamResource;
import java.time.LocalTime;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import com.vaadin.flow.component.UI;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.nio.file.Files;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import com.vaadin.flow.server.StreamResource;
import com.company.medicine.entity.Stock;
import com.company.medicine.view.main.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.ZoneId;
import java.util.Date;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
@Route(value = "pos", layout = MainView.class)
@ViewController("PosView")
@ViewDescriptor("pos-view.xml")
public class PosView extends StandardView {

    @Autowired
    private DataManager dataManager;

    private ComboBox<Stock> medicineSelect;
    private IntegerField quantityField;
    private Button addToReceiptButton;
    private Grid<ReceiptItem> receiptGrid;
    private TextField subtotalField;
    private TextField taxField;
    private TextField totalField;
    private Button processButton;

    // Class to represent items in the receipt
    private static class ReceiptItem {
        private Stock stock;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal total;

        public ReceiptItem(Stock stock, Integer quantity) {
            this.stock = stock;
            this.quantity = quantity;
            this.unitPrice = stock.getPrice();
            this.total = stock.getPrice().multiply(new BigDecimal(quantity));
        }

        // Getters and setters
        public Stock getStock() { return stock; }
        public String getMedicineName() { return stock.getBrandName(); }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
            this.total = this.unitPrice.multiply(new BigDecimal(quantity));
        }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public BigDecimal getTotal() { return total; }
    }

    private List<ReceiptItem> receiptItems = new ArrayList<>();

    @Subscribe
    public void onInit(InitEvent event) {
        initComponents();
        setupListeners();
    }

    private void initComponents() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setPadding(false);
        mainLayout.setSpacing(true);

        H4 title = new H4("Point of Sale");
        title.getStyle()
                .set("color", "var(--lumo-primary-color)")
                .set("margin-top", "1em")
                .set("font-weight", "600");

        // Form Layout
        HorizontalLayout formLayout = createFormLayout();

        // Receipt Layout
        VerticalLayout receiptLayout = createReceiptLayout();

        mainLayout.add(title, formLayout, receiptLayout);
        mainLayout.setSizeFull();

        getContent().add(mainLayout);
    }

    private HorizontalLayout createFormLayout() {
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setWidthFull();
        formLayout.setAlignItems(FlexLayout.Alignment.BASELINE);
        formLayout.setSpacing(true);

        medicineSelect = new ComboBox<>("Select Medicine");
        medicineSelect.setItems(loadAvailableStock());
        medicineSelect.setItemLabelGenerator(stock ->
                stock.getBrandName() + " - " + stock.getActiveIngredientName() +
                        " (" + stock.getActiveIngredientStrength() + ")");
        medicineSelect.setWidth("300px");
        medicineSelect.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
        medicineSelect.setPlaceholder("Choose medicine...");
        medicineSelect.setRequired(true);

        quantityField = new IntegerField("Quantity");
        quantityField.setValue(1);
        quantityField.setMin(1);
        quantityField.setStepButtonsVisible(true);
        quantityField.setWidth("150px");
        quantityField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        addToReceiptButton = new Button("Add to Receipt", VaadinIcon.PLUS.create());
        addToReceiptButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addToReceiptButton.addClickListener(e -> addToReceipt());

        formLayout.add(medicineSelect, quantityField, addToReceiptButton);
        formLayout.setFlexGrow(1, medicineSelect);

        return formLayout;
    }

    private VerticalLayout createReceiptLayout() {
        VerticalLayout receiptLayout = new VerticalLayout();
        receiptLayout.setSpacing(false);
        receiptLayout.setPadding(true);

        H4 receiptTitle = new H4("Receipt");
        receiptTitle.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("margin-top", "0")
                .set("margin-bottom", "var(--lumo-space-m)");

        // Receipt Grid
        receiptGrid = new Grid<>();
        receiptGrid.addColumn(ReceiptItem::getMedicineName).setHeader("Medicine").setFlexGrow(2);

        // Add editable quantity column
        Grid.Column<ReceiptItem> quantityColumn = receiptGrid.addColumn(ReceiptItem::getQuantity)
                .setHeader("Quantity")
                .setWidth("100px");

        receiptGrid.addColumn(item -> String.format("₱%.2f", item.getUnitPrice()))
                .setHeader("Unit Price")
                .setTextAlign(ColumnTextAlign.END);

        receiptGrid.addColumn(item -> String.format("₱%.2f", item.getTotal()))
                .setHeader("Total")
                .setTextAlign(ColumnTextAlign.END);
        // Add delete button column
        receiptGrid.addComponentColumn(item -> {
            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            deleteButton.addClickListener(e -> {
                receiptItems.remove(item);
                updateReceipt();
            });
            return deleteButton;
        }).setWidth("70px").setFlexGrow(0);

        // Setup grid editor
        Editor<ReceiptItem> editor = receiptGrid.getEditor();
        Binder<ReceiptItem> binder = new Binder<>(ReceiptItem.class);
        editor.setBinder(binder);

        // Create quantity editor field
        IntegerField quantityEditor = new IntegerField();
        quantityEditor.setWidthFull();
        quantityEditor.setMin(1);
        binder.forField(quantityEditor)
                .bind(ReceiptItem::getQuantity, this::updateItemQuantity);
        quantityColumn.setEditorComponent(quantityEditor);

        // Enable double click to edit
        receiptGrid.addItemDoubleClickListener(event -> {
            editor.editItem(event.getItem());
            quantityEditor.focus();
        });

        // Summary Layout
        HorizontalLayout summaryLayout = new HorizontalLayout();
        summaryLayout.setWidthFull();
        summaryLayout.setJustifyContentMode(FlexLayout.JustifyContentMode.END);

        VerticalLayout totalsLayout = new VerticalLayout();
        totalsLayout.setSpacing(false);
        totalsLayout.setPadding(false);

        subtotalField = createReadOnlyTextField("Subtotal:");
        taxField = createReadOnlyTextField("Tax (12%):");
        totalField = createReadOnlyTextField("Total:");
        totalField.getStyle().set("font-weight", "bold");

        totalsLayout.add(subtotalField, taxField, totalField);

        processButton = new Button("Process Sale", VaadinIcon.CASH.create());
        processButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        processButton.addClickListener(e -> processSale());

        summaryLayout.add(totalsLayout, processButton);

        receiptLayout.add(receiptTitle, receiptGrid, summaryLayout);
        return receiptLayout;
    }

    private TextField createReadOnlyTextField(String label) {
        TextField field = new TextField(label);
        field.setReadOnly(true);
        field.setValue("₱0.00");
        field.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        field.setWidth("200px");
        return field;
    }

    private void setupListeners() {
        medicineSelect.setItemLabelGenerator(stock -> {
            String baseLabel = stock.getBrandName() + " - " + stock.getActiveIngredientName() +
                    " (" + stock.getActiveIngredientStrength() + ")";

            if (stock.getExpirationDate() != null) {
                LocalDate expiryLocalDate = stock.getExpirationDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                baseLabel += " | Expires: " + expiryLocalDate;
            }

            return baseLabel;
        });
        medicineSelect.addValueChangeListener(event -> {
            Stock selected = event.getValue();
            if (selected != null) {
                Stock freshStock = dataManager.load(Stock.class)
                        .id(selected.getId())
                        .one();

                // Comprehensive stock and expiry checks
                boolean isStockAvailable = isStockValid(freshStock);

                if (isStockAvailable) {
                    // Update quantity indicator
                    quantityField.setLabel("Quantity (" + freshStock.getQuantity()+")");
                    quantityField.setMax(freshStock.getQuantity());
                    quantityField.setValue(1);
                } else {
                    medicineSelect.setValue(null);
                    quantityField.setValue(1);
                    quantityField.setLabel("Quantity");
                }
            }
        });
    }
    private boolean isStockValid(Stock stock) {
        // Check for zero quantity
        if (stock.getQuantity() <= 0) {
            showError("Medicine out of stock: " + stock.getBrandName());
            return false;
        }

        // Check for expiration
        Date expiryDate = stock.getExpirationDate();
        if (expiryDate != null) {
            LocalDate expiryLocalDate = expiryDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            LocalDate today = LocalDate.now();

            if (expiryLocalDate.isBefore(today)) {
                showError("Cannot sell expired medicine: " + stock.getBrandName());
                return false;
            }
        }

        return true;
    }

    private void addToReceipt() {
        Stock selectedStock = medicineSelect.getValue();
        Integer quantity = quantityField.getValue();

        if (selectedStock == null || quantity == null || quantity <= 0) {
            showError("Please select a medicine and enter a valid quantity");
            return;
        }

        Stock freshStock = dataManager.load(Stock.class)
                .id(selectedStock.getId())
                .one();

        if (!isStockValid(freshStock) || quantity > freshStock.getQuantity()) {
            showError("Invalid stock or insufficient quantity");
            return;
        }
        // Check if medicine already exists in receipt
        for (ReceiptItem item : receiptItems) {
            if (item.getStock().getId().equals(selectedStock.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                updateReceipt();
                resetSelectionForm();
                return;
            }
        }

        receiptItems.add(new ReceiptItem(selectedStock, quantity));
        updateReceipt();
        resetSelectionForm();

        // Reset quantity indicator explicitly
        quantityField.setLabel("Quantity");
        quantityField.setMax(Integer.MAX_VALUE);
        quantityField.setValue(1);
    }

    private void updateItemQuantity(ReceiptItem item, Integer newQuantity) {
        Stock freshStock = dataManager.load(Stock.class)
                .id(item.getStock().getId())
                .one();

        if (newQuantity > freshStock.getQuantity()) {
            showError("Insufficient stock. Available: " + freshStock.getQuantity());
            return;
        }

        item.setQuantity(newQuantity);
        updateReceipt();
    }

    private void updateReceipt() {
        receiptGrid.setItems(receiptItems);

        // Calculate totals
        BigDecimal subtotal = receiptItems.stream()
                .map(ReceiptItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = subtotal.multiply(new BigDecimal("0.12")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax);

        // Update summary fields
        subtotalField.setValue(String.format("₱%.2f", subtotal));
        taxField.setValue(String.format("₱%.2f", tax));
        totalField.setValue(String.format("₱%.2f", total));
    }
    private void exportToImage() {
        if (receiptItems.isEmpty()) {
            showError("No items in receipt");
            return;
        }

        try {
            // Create buffered image with appropriate size
            BufferedImage image = new BufferedImage(500, 800, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            // Set white background
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

            // Set font and color
            g2d.setFont(new Font("Monospaced", Font.PLAIN, 14)); // Increased font size
            g2d.setColor(Color.BLACK);

            // Draw header
            String pharmacyName = "Andres Pharmacy Ormoc"; // Updated pharmacy name
            String address = "Brgy. Mabini, Ormoc City, Leyte 6541";
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));

            // Center the pharmacy name
            FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
            int x = (image.getWidth() - metrics.stringWidth(pharmacyName)) / 2;
            g2d.drawString(pharmacyName, x, 55);
            g2d.drawLine(50, 110, 450, 110);

            // Center the additional details
            x = (image.getWidth() - metrics.stringWidth(address)) / 2;
            g2d.drawString(address, x, 75);
            String dateTime = currentDate + " " + currentTime;
            x = (image.getWidth() - metrics.stringWidth(dateTime)) / 2;
            g2d.drawString(dateTime, x, 95);


            // Draw items
            int y = 130; // Adjust y position after header
            for (ReceiptItem item : receiptItems) {
                List<String> nameLines = splitLongText(item.getMedicineName(), 25);

                String itemLine = String.format("%-25s x%-3d  ₱%.2f",
                        nameLines.get(0),
                        item.getQuantity(),
                        item.getTotal());

                g2d.drawString(itemLine, 50, y);
                y += 20;

                // Additional name lines
                for (int i = 1; i < nameLines.size(); i++) {
                    g2d.drawString(String.format("%-25s", nameLines.get(i)), 50, y);
                    y += 20;
                }

                y += 10; // Extra space between items
            }

            // Draw summary
            g2d.drawLine(50, y, 450, y);
            y += 30;
            g2d.drawString(String.format("%-25s %s", "Subtotal:", subtotalField.getValue()), 50, y);
            y += 20;
            g2d.drawString(String.format("%-25s %s", "Tax (12%):", taxField.getValue()), 50, y);
            y += 20;
            g2d.drawString(String.format("%-25s %s", "TOTAL:", totalField.getValue()), 50, y);

            // Dispose graphics
            g2d.dispose();

            // Save image to a temporary file
            File tempFile = File.createTempFile("receipt", ".png");
            ImageIO.write(image, "png", tempFile);

            // Read file contents
            byte[] fileContent = Files.readAllBytes(tempFile.toPath());

            // Encode to base64
            String base64Image = Base64.getEncoder().encodeToString(fileContent);

            // Trigger download via JavaScript
            UI.getCurrent().getPage().executeJs(
                    "const link = document.createElement('a');" +
                            "link.href = 'data:image/png;base64," + base64Image + "';" +
                            "link.download = 'receipt.png';" +
                            "link.click();"
            );

            // Optional: delete temporary file
            tempFile.deleteOnExit();

            Notification.show("Receipt exported", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_PRIMARY);

        } catch (IOException e) {
            showError("Export failed: " + e.getMessage());
        }
    }
    private void processSale() {
        if (receiptItems.isEmpty()) {
            showError("No items in receipt");
            return;
        }

        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Sale Summary");

        // Create a scrollable container with max height and width
        Div content = new Div();
        content.getStyle()
                .set("font-family", "monospace")
                .set("max-height", "400px")
                .set("max-width", "500px")
                .set("overflow", "auto")
                .set("white-space", "pre-wrap")
                .set("line-height", "1.6")
                .set("padding", "15px")
                .set("background-color", "var(--lumo-contrast-5pct)")
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "4px");

        StringBuilder message = new StringBuilder();
        message.append("ORMOC PHARMACY \n");
        message.append("--------------------\n\n");

        for (ReceiptItem item : receiptItems) {
            String medicineName = item.getMedicineName();
            String quantityPriceLine = String.format("x%-3d  ₱%.2f",
                    item.getQuantity(),
                    item.getTotal());

            // If medicine name is longer than 25, split it across multiple lines
            List<String> nameLines = splitLongText(medicineName, 25);

            // First line with name and quantity/price
            message.append(String.format("%-25s %s\n",
                    nameLines.get(0),
                    quantityPriceLine));

            // Additional name lines without price
            for (int i = 1; i < nameLines.size(); i++) {
                message.append(String.format("%-25s\n", nameLines.get(i)));
            }

            message.append("\n");
        }

        message.append("--------------------\n");
        message.append(String.format("%-25s %s\n", "Subtotal:", subtotalField.getValue()));
        message.append(String.format("%-25s %s\n", "Tax (12%):", taxField.getValue()));
        message.append(String.format("%-25s %s\n", "TOTAL:", totalField.getValue()));

        content.setText(message.toString());
        confirmDialog.add(content);

        // Rest of the method remains the same as in the original code
        confirmDialog.setCancelable(true);
        confirmDialog.setConfirmText("Process Sale");
        confirmDialog.setCancelText("Cancel");
        confirmDialog.setConfirmButtonTheme("primary success");

        confirmDialog.addConfirmListener(event -> {
            try {
                for (ReceiptItem item : receiptItems) {
                    Stock stock = dataManager.load(Stock.class)
                            .id(item.getStock().getId())
                            .one();
                    stock.setQuantity(stock.getQuantity() - item.getQuantity());
                    dataManager.save(stock);
                }

                Notification.show("Sale processed successfully!\nTotal: " + totalField.getValue(),
                                3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                receiptItems.clear();
                updateReceipt();
                resetSelectionForm();

            } catch (Exception e) {
                showError("Error processing sale: " + e.getMessage());
            }
        });

        confirmDialog.open();

        Button exportButton = new Button("Export Receipt", VaadinIcon.CAMERA.create());
        exportButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        exportButton.addClickListener(e -> {
            exportToImage();
            confirmDialog.close();
        });

        confirmDialog.add(exportButton);

    }

    // Helper method to split long text into lines
    private List<String> splitLongText(String text, int maxLength) {
        List<String> lines = new ArrayList<>();
        while (text.length() > maxLength) {
            lines.add(text.substring(0, maxLength));
            text = text.substring(maxLength);
        }
        lines.add(text);
        return lines;
    }
    private List<Stock> loadAvailableStock() {
        return dataManager.load(Stock.class)
                .query("select s from Stock s where s.quantity > 0 and s.expirationDate > current_date order by s.brandName asc")
                .list();
    }

    private void resetSelectionForm() {
        medicineSelect.setValue(null);
        quantityField.setValue(1);
    }

    private void showError(String message) {
        Notification.show(message, 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}