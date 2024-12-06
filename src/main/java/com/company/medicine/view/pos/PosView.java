package com.company.medicine.view.pos;

import com.company.medicine.entity.Sale;
import com.company.medicine.entity.SaleItem;
import com.company.medicine.entity.Stock;
import com.company.medicine.entity.User;
import com.company.medicine.view.main.MainView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import io.jmix.core.DataManager;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.imageio.ImageIO;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.List;

import com.vaadin.flow.component.dialog.Dialog;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Base64;


@Route(value = "pos", layout = MainView.class)
@ViewController("PosView")
@ViewDescriptor("pos-view.xml")
public class PosView extends StandardView {

    @Autowired
    private DataManager dataManager;
    private TextField onlineReferenceField;
    private TextField cardNumberField;
    private static final String NUMERIC_PATTERN = "^[0-9]*$";
    private Dialog paymentDialog;
    private Tabs paymentMethodTabs;
    private VerticalLayout paymentContentLayout;
    private Button confirmPaymentButton;
    private TextField changeField;
    private TextField receivedAmountField;
    private RadioButtonGroup<String> onlinePaymentGroup;
    private RadioButtonGroup<String> cardPaymentGroup;
    private Checkbox seniorCitizenCheckbox;
    private Checkbox pwdCheckbox;
    private H4 totalHeader;
    private ComboBox<Stock> medicineSelect;
    private ComboBox<String> saleTypeComboBox;
    private IntegerField quantityField;
    private Button addToReceiptButton;
    private Grid<ReceiptItem> receiptGrid;
    private TextField subtotalField;
    private TextField taxField;
    private TextField totalField;
    private Button processButton;
    private Grid.Column<ReceiptItem> saleTypeColumn;


    private static class ReceiptItem {
        private Stock stock;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal total;
        private int unitsSold;
        private int packagesSold;
        private String saleType;
        private String unitType;

        public ReceiptItem(Stock stock, int quantity, int unitsSold, String saleType, BigDecimal price, String unitType) {
            this.stock = stock;
            this.quantity = quantity;
            this.unitPrice = price;
            this.total = this.unitPrice.multiply(new BigDecimal(quantity));
            this.unitsSold = unitsSold;
            this.saleType = saleType;
            this.packagesSold = (saleType.equals("Package")) ? quantity : 0;
            this.unitType = unitType;
        }

        public Stock getStock() {
            return stock;
        }

        public String getMedicineName() {
            return stock.getBrandName();
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
            if (saleType.equals("Unit")) {
                this.unitsSold = quantity;
                this.packagesSold = 0;
            } else {
                this.unitsSold = quantity * stock.getUnitsPerPackage();
                this.packagesSold = quantity;
            }
            this.total = this.unitPrice.multiply(new BigDecimal(this.unitsSold));
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        public BigDecimal getTotal() {
            return total;
        }

        public int getUnitsSold() {
            return unitsSold;
        }

        public void setUnitsSold(int unitsSold) {
            this.unitsSold = unitsSold;
            this.total = this.unitPrice.multiply(new BigDecimal(unitsSold));
        }

        public int getPackagesSold() {
            return packagesSold;
        }

        public void setPackagesSold(int packagesSold) {
            this.packagesSold = packagesSold;
        }

        public String getSaleType() {
            return saleType;
        }

        public String getSaleTypeWithQuantity() {
            return this.saleType + " (" + this.quantity + ")";
        }

        public String getUnitType() {
            return unitType;
        }
    }

    private List<ReceiptItem> receiptItems = new ArrayList<>();

    @Subscribe
    public void onInit(InitEvent event) {
        initComponents();
        setupListeners();
    }

    private void initComponents() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setPadding(true);
        mainLayout.setSpacing(true);
        mainLayout.setSizeFull();

        HorizontalLayout headerLayout = createHeaderLayout();
        mainLayout.add(headerLayout);

        HorizontalLayout mainContentLayout = new HorizontalLayout();
        mainContentLayout.setWidthFull();
        mainContentLayout.setSpacing(true);

        VerticalLayout formLayout = createFormLayout();
        formLayout.setWidth("30%");

        VerticalLayout receiptLayout = createReceiptLayout();
        receiptLayout.setWidth("70%");

        mainContentLayout.add(formLayout, receiptLayout);
        mainLayout.add(mainContentLayout);
        getContent().add(mainLayout);

        saleTypeColumn = receiptGrid.addColumn(ReceiptItem::getSaleTypeWithQuantity)
                .setHeader("Sale Type (Quantity)")
                .setWidth("150px");
        receiptGrid.addColumn(ReceiptItem::getUnitType).setHeader("Unit Type");
    }

    private HorizontalLayout createHeaderLayout() {
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setPadding(true);
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        H3 title = new H3("Hello, " + getCurrentUserFirstName() + "!");
        headerLayout.add(title);
        H4 timestamp = new H4(LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        headerLayout.add(timestamp);
        return headerLayout;
    }

    private VerticalLayout createFormLayout() {
        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setWidthFull();
        formLayout.setSpacing(true);
        formLayout.setPadding(true);
        formLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);

        H4 medicineSelectionTitle = new H4("Medicine Selection");
        medicineSelectionTitle.getStyle().set("margin-bottom", "0.5rem");

        medicineSelect = new ComboBox<>("Select Medicine");
        medicineSelect.setItems(loadAvailableStock());
        medicineSelect.setItemLabelGenerator(stock ->
                stock.getBrandName() + " - " + stock.getActiveIngredientName() +
                        " (" + stock.getActiveIngredientStrength() + ")");
        medicineSelect.setWidthFull();
        medicineSelect.setPlaceholder("Choose medicine...");
        medicineSelect.setRequired(true);
        medicineSelect.addValueChangeListener(event -> {
            updateQuantityField(event.getValue());
        });

        HorizontalLayout saleTypeQuantityLayout = new HorizontalLayout();
        saleTypeQuantityLayout.setWidthFull();
        saleTypeQuantityLayout.setSpacing(true);
        saleTypeQuantityLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        saleTypeQuantityLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);


        saleTypeComboBox = new ComboBox<>("Sale Type");
        saleTypeComboBox.setItems("Unit", "Package");
        saleTypeComboBox.setValue("Unit");
        saleTypeComboBox.setWidthFull();
        saleTypeComboBox.addValueChangeListener(event -> {
            updateQuantityField(medicineSelect.getValue());
            quantityField.setValue(1);
        });

        quantityField = new IntegerField("Quantity");
        quantityField.setValue(1);
        quantityField.setMin(1);
        quantityField.setStepButtonsVisible(true);
        quantityField.setWidthFull();
        quantityField.addValueChangeListener(e -> {
            if (e.getValue() == null || e.getValue() < 1) {
                quantityField.setValue(1);
            }
            if(medicineSelect.getValue() != null){
                updateQuantityField(medicineSelect.getValue());
            }
        });

        saleTypeQuantityLayout.add(saleTypeComboBox, quantityField);


        addToReceiptButton = new Button("Add to Receipt", VaadinIcon.PLUS.create());
        addToReceiptButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addToReceiptButton.setWidthFull();
        addToReceiptButton.addClickListener(e -> addToReceipt());


        VerticalLayout totalsLayout = new VerticalLayout();
        totalsLayout.setSpacing(true);
        totalsLayout.setPadding(false);
        totalsLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);

        H4 totalsTitle = new H4("Sale Summary");
        totalsTitle.getStyle().set("margin-bottom", "0.5rem");

        subtotalField = createReadOnlyTextField("Subtotal:");
        taxField = createReadOnlyTextField("Tax (12%):");
        totalField = createReadOnlyTextField("Total:");
        totalField.getStyle().set("font-weight", "bold");

        processButton = new Button("Process Sale", VaadinIcon.CASH.create());
        processButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        processButton.setWidthFull();
        processButton.addClickListener(e -> processSale());


        totalsLayout.add(totalsTitle, subtotalField, taxField, totalField, processButton);

        formLayout.add(
                medicineSelectionTitle,
                medicineSelect,
                saleTypeQuantityLayout,
                addToReceiptButton,
                new Hr(),
                totalsLayout
        );

        return formLayout;
    }

    private VerticalLayout createReceiptLayout() {
        VerticalLayout receiptLayout = new VerticalLayout();
        receiptLayout.setSpacing(false);
        receiptLayout.setPadding(true);
        receiptLayout.setSizeFull();

        H4 receiptTitle = new H4("Receipt");
        receiptTitle.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("margin-bottom", "var(--lumo-space-m)");

        receiptGrid = new Grid<>();
        receiptGrid.setSizeFull();
        receiptGrid.addColumn(ReceiptItem::getMedicineName).setHeader("Medicine").setFlexGrow(2);

        Grid.Column<ReceiptItem> quantityColumn = receiptGrid.addColumn(ReceiptItem::getQuantity)
                .setHeader("Quantity")
                .setWidth("100px");

        receiptGrid.addColumn(item -> String.format("₱%.2f", item.getUnitPrice()))
                .setHeader("Unit Price")
                .setTextAlign(ColumnTextAlign.END);

        receiptGrid.addColumn(item -> String.format("₱%.2f", item.getTotal()))
                .setHeader("Total")
                .setTextAlign(ColumnTextAlign.END);

        receiptGrid.addComponentColumn(item -> {
            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            deleteButton.addClickListener(e -> {
                receiptItems.remove(item);
                updateReceipt();
            });
            return deleteButton;
        }).setWidth("70px").setFlexGrow(0);

        Editor<ReceiptItem> editor = receiptGrid.getEditor();
        Binder<ReceiptItem> binder = new Binder<>(ReceiptItem.class);
        editor.setBinder(binder);

        IntegerField quantityEditor = new IntegerField();
        quantityEditor.setWidthFull();
        quantityEditor.setMin(1);
        binder.forField(quantityEditor)
                .bind(ReceiptItem::getQuantity, this::updateItemQuantity);
        quantityColumn.setEditorComponent(quantityEditor);

        receiptGrid.addItemDoubleClickListener(event -> {
            editor.editItem(event.getItem());
            quantityEditor.focus();
        });

        processButton = new Button("Process Sale", VaadinIcon.CASH.create());
        processButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        processButton.addClickListener(e -> processSale());


        receiptLayout.add(receiptTitle, receiptGrid);
        return receiptLayout;
    }

    private TextField createReadOnlyTextField(String label) {
        TextField field = new TextField(label);
        field.setReadOnly(true);
        field.setValue("₱0.00");
        field.setWidth("100%");
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
            updateQuantityField(event.getValue());
        });

        saleTypeComboBox.addValueChangeListener(event -> {
            updateQuantityField(medicineSelect.getValue());
            quantityField.setValue(1);
        });
    }

    private void updateQuantityField(Stock selectedStock) {
        if (selectedStock == null) {
            quantityField.setLabel("Quantity");
            quantityField.setValue(1);
            return;
        }

        Stock freshStock = dataManager.load(Stock.class)
                .id(selectedStock.getId())
                .one();

        if (isStockValid(freshStock)) {
            String saleType = saleTypeComboBox.getValue();
            int maxQuantity;
            String quantityLabel;

            if (saleType.equals("Package")) {
                maxQuantity = freshStock.getPackageQuantity();
                quantityLabel = String.format("Quantity (%d Packages)", maxQuantity);
            } else {
                maxQuantity = freshStock.getTotalUnits();
                quantityLabel = String.format("Quantity (%d Units)", maxQuantity);
            }

            quantityField.setLabel(quantityLabel);
            quantityField.setMax(maxQuantity);
        } else {
            medicineSelect.setValue(null);
            medicineSelect.getStyle().setFlexGrow("1");
            quantityField.setValue(1);
            quantityField.setLabel("Quantity");
        }
    }

    private boolean isStockValid(Stock stock) {
        if (stock.getTotalUnits() <= 0) {
            showError("Medicine out of stock: " + stock.getBrandName());
            return false;
        }

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
        int quantity = quantityField.getValue();
        String saleType = saleTypeComboBox.getValue();

        if (selectedStock == null || quantity <= 0 || saleType == null) {
            showError("Please select a medicine, sale type, and enter a valid quantity.");
            return;
        }

        Stock freshStock = dataManager.load(Stock.class).id(selectedStock.getId()).one();

        if (!isStockValid(freshStock)) {
            return;
        }

        int unitsAvailable = freshStock.getTotalUnits();
        int unitsToSell;
        int packagesToSell;
        int quantityToUpdate;
        BigDecimal priceToUse;
        String unitTypeToUse;

        if (saleType.equals("Unit")) {
            if (quantity > unitsAvailable) {
                showError("Insufficient stock. Available units: " + unitsAvailable);
                return;
            }
            unitsToSell = quantity;
            packagesToSell = 0;
            quantityToUpdate = quantity;
            priceToUse = freshStock.getPricePerUnit();
            unitTypeToUse = freshStock.getUnitType() != null ? freshStock.getUnitType().toString() : "";
        } else {
            if (quantity > freshStock.getPackageQuantity()) {
                showError("Insufficient stock. Available packages: " + freshStock.getPackageQuantity());
                return;
            }
            unitsToSell = quantity * freshStock.getUnitsPerPackage();
            packagesToSell = quantity;
            quantityToUpdate = quantity;
            priceToUse = freshStock.getPricePerPackage();
            unitTypeToUse = freshStock.getPackageType() != null ? freshStock.getPackageType().toString() : "";
        }

        Optional<ReceiptItem> existingItem = receiptItems.stream()
                .filter(item ->
                        item.getStock().getId().equals(selectedStock.getId()) &&
                                item.getSaleType().equals(saleType) &&
                                item.getUnitType().equals(unitTypeToUse) &&
                                Objects.equals(
                                        item.getStock().getExpirationDate(),
                                        selectedStock.getExpirationDate()
                                )
                )
                .findFirst();

        if (existingItem.isPresent()) {
            ReceiptItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantityToUpdate;

            if (saleType.equals("Unit")) {
                if (newQuantity > unitsAvailable) {
                    showError("Insufficient stock. Available units: " + unitsAvailable);
                    return;
                }
            } else {
                if (newQuantity > freshStock.getPackageQuantity()) {
                    showError("Insufficient stock. Available packages: " + freshStock.getPackageQuantity());
                    return;
                }
            }

            item.setQuantity(newQuantity);
            if (saleType.equals("Unit")) {
                item.setUnitsSold(newQuantity);
            } else {
                item.setUnitsSold(newQuantity * freshStock.getUnitsPerPackage());
                item.setPackagesSold(newQuantity);
            }
        } else {
            receiptItems.add(new ReceiptItem(selectedStock, quantity, unitsToSell, saleType, priceToUse, unitTypeToUse));
        }

        updateReceipt();
        quantityField.setValue(1);
        resetSelectionForm();
    }

    private void updateItemQuantity(ReceiptItem item, Integer newQuantity) {
        Stock freshStock = dataManager.load(Stock.class).id(item.getStock().getId()).one();
        int unitsAvailable = freshStock.getTotalUnits();
        String saleType = item.getSaleType();

        int newUnitsSold;
        int newPackagesSold;
        BigDecimal priceToUse;

        if (saleType.equals("Unit")) {
            if (newQuantity > unitsAvailable) {
                showError("Insufficient stock. Available units: " + unitsAvailable);
                return;
            }
            newUnitsSold = newQuantity;
            newPackagesSold = 0;
            priceToUse = freshStock.getPricePerUnit();
        } else {
            if (newQuantity > freshStock.getPackageQuantity()) {
                showError("Insufficient stock. Available packages: " + freshStock.getPackageQuantity());
                return;
            }
            newUnitsSold = newQuantity * freshStock.getUnitsPerPackage();
            newPackagesSold = newQuantity;
            priceToUse = freshStock.getPricePerPackage();
        }
        item.setUnitsSold(newUnitsSold);
        item.setQuantity(newQuantity);
        item.setPackagesSold(newPackagesSold);
        item.setUnitPrice(priceToUse);
        updateReceipt();
    }

    private void updateReceipt() {
        receiptGrid.setItems(receiptItems);

        BigDecimal subtotal = receiptItems.stream()
                .map(ReceiptItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = subtotal.multiply(new BigDecimal("0.12")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal preDiscountTotal = subtotal.add(tax); // Calculate total before discount

        BigDecimal discountPercentage = BigDecimal.ZERO;
        if (seniorCitizenCheckbox != null && (seniorCitizenCheckbox.getValue() || pwdCheckbox.getValue())) {
            discountPercentage = new BigDecimal("0.10");
        }

        BigDecimal discountAmount = preDiscountTotal.multiply(discountPercentage).setScale(2, RoundingMode.HALF_UP); // Discount on total
        BigDecimal total = preDiscountTotal.subtract(discountAmount); // Subtract discount from total

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
            BufferedImage image = new BufferedImage(500, 1000, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
            g2d.setFont(new Font("Monospaced", Font.PLAIN, 14));
            g2d.setColor(Color.BLACK);

            String pharmacyName = "Andres Pharmacy Ormoc";
            String address = "Brgy. Mabini, Ormoc City, Leyte 6541";
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));

            FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
            int x = (image.getWidth() - metrics.stringWidth(pharmacyName)) / 2;
            g2d.drawString(pharmacyName, x, 55);
            g2d.drawLine(50, 125, 450, 125);

            x = (image.getWidth() - metrics.stringWidth(address)) / 2;
            g2d.drawString(address, x, 75);
            String dateTime = currentDate + " " + currentTime;
            x = (image.getWidth() - metrics.stringWidth(dateTime)) / 2;
            g2d.drawString(dateTime, x, 95);

            String cashierName = getCurrentUserFirstName();
            g2d.drawString("Cashier: " + cashierName, 50, 115);

            int y = 150;
            for (ReceiptItem item : receiptItems) {
                List<String> nameLines = splitLongText(item.getMedicineName(), 25);

                String itemLine = String.format("%-25s x%-3d  ₱%.2f",
                        nameLines.get(0),
                        item.getQuantity(),
                        item.getTotal());

                g2d.drawString(itemLine, 50, y);
                y += 20;

                for (int i = 1; i < nameLines.size(); i++) {
                    g2d.drawString(String.format("%-25s", nameLines.get(i)), 50, y);
                    y += 20;
                }

                y += 10;
            }

            g2d.drawLine(50, y, 450, y);
            y += 30;
            g2d.drawString(String.format("%-25s %s", "Subtotal:", subtotalField.getValue()), 50, y);
            y += 20;
            g2d.drawString(String.format("%-25s %s", "Tax (12%):", taxField.getValue()), 50, y);
            y += 20;
            g2d.drawString(String.format("%-25s %s", "TOTAL:", totalField.getValue()), 50, y);

            y += 40;
            g2d.drawString("Payment Details:", 50, y);
            y += 20;

            String paymentMethod = "";
            String amountReceived = "N/A";
            String changeAmount = "N/A";

            switch (paymentMethodTabs.getSelectedIndex()) {
                case 0:
                    paymentMethod = "Online - " + onlinePaymentGroup.getValue();
                    break;
                case 1:
                    paymentMethod = "Card - " + cardPaymentGroup.getValue();
                    break;
                case 2:
                    paymentMethod = "Cash";
                    amountReceived = receivedAmountField.getValue();
                    changeAmount = changeField.getValue();
                    break;
            }

            g2d.drawString(String.format("%-25s %s", "Payment Method:", paymentMethod), 50, y);
            y += 20;
            g2d.drawString(String.format("%-25s %s", "Amount Received:", amountReceived), 50, y);
            y += 20;
            g2d.drawString(String.format("%-25s %s", "Change:", changeAmount), 50, y);

            g2d.dispose();

            File tempFile = File.createTempFile("receipt", ".png");
            ImageIO.write(image, "png", tempFile);

            byte[] fileContent = Files.readAllBytes(tempFile.toPath());
            String base64Image = Base64.getEncoder().encodeToString(fileContent);

            UI.getCurrent().getPage().executeJs(
                    "const link = document.createElement('a');" +
                            "link.href = 'data:image/png;base64," + base64Image + "';" +
                            "link.download = 'receipt.png';" +
                            "link.click();"
            );

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

        showPaymentDialog();
    }

    private void showPaymentDialog() {
        paymentDialog = new Dialog();
        paymentDialog.setWidth("500px");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setPadding(true);
        mainLayout.setSpacing(true);

        totalHeader = new H4("Total Amount: " + totalField.getValue()); // Initialize here
        totalHeader.getStyle().set("margin", "0");
        mainLayout.add(totalHeader);

        Tab onlineTab = new Tab("Online Payment");
        Tab cardTab = new Tab("Card Payment");
        Tab cashTab = new Tab("Cash Payment");
        paymentMethodTabs = new Tabs(onlineTab, cardTab, cashTab);
        paymentMethodTabs.setWidthFull();

        paymentContentLayout = new VerticalLayout();
        paymentContentLayout.setSpacing(true);
        paymentContentLayout.setPadding(false);

        seniorCitizenCheckbox = new Checkbox("Senior Citizen (10%)");
        pwdCheckbox = new Checkbox("PWD (10%)");
        seniorCitizenCheckbox.addValueChangeListener(event -> applyDiscountIfChecked());
        pwdCheckbox.addValueChangeListener(event -> applyDiscountIfChecked());
        paymentContentLayout.add(seniorCitizenCheckbox, pwdCheckbox);

        mainLayout.add(paymentMethodTabs, paymentContentLayout);

        setupPaymentContent();

        confirmPaymentButton = new Button("Confirm Payment", event -> processPayment());
        confirmPaymentButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        Button cancelButton = new Button("Cancel", event -> paymentDialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout buttonLayout = new HorizontalLayout(confirmPaymentButton, cancelButton);
        buttonLayout.setJustifyContentMode(FlexLayout.JustifyContentMode.END);
        buttonLayout.setWidthFull();

        mainLayout.add(buttonLayout);

        paymentDialog.add(mainLayout);
        paymentDialog.open();
    }

    private void setupPaymentContent() {
        paymentMethodTabs.addSelectedChangeListener(event -> {
            paymentContentLayout.removeAll();
            paymentContentLayout.add(seniorCitizenCheckbox, pwdCheckbox); // Add checkboxes back after removing

            switch (paymentMethodTabs.getSelectedIndex()) {
                case 0:
                    setupOnlinePaymentContent();
                    break;
                case 1:
                    setupCardPaymentContent();
                    break;
                case 2:
                    setupCashPaymentContent();
                    break;
            }
        });

        setupOnlinePaymentContent();
    }

    private void setupOnlinePaymentContent() {
        onlinePaymentGroup = new RadioButtonGroup<>();
        onlinePaymentGroup.setLabel("Select Online Payment Method");
        onlinePaymentGroup.setItems("GCash", "PayMaya", "GoTyme");
        onlinePaymentGroup.setValue("GCash");

        onlineReferenceField = new TextField("Reference Number");
        onlineReferenceField.setWidthFull();
        onlineReferenceField.setRequired(true);
        onlineReferenceField.setHelperText("Enter numbers only");
        onlineReferenceField.addValueChangeListener(event -> {
            String value = event.getValue();
            if (!value.isEmpty() && !value.matches(NUMERIC_PATTERN)) {
                onlineReferenceField.setInvalid(true);
                onlineReferenceField.setErrorMessage("Please enter numbers only");
                confirmPaymentButton.setEnabled(false);
            } else if (value.isEmpty()) {
                onlineReferenceField.setInvalid(true);
                onlineReferenceField.setErrorMessage("Reference number is required");
                confirmPaymentButton.setEnabled(false);
            } else {
                onlineReferenceField.setInvalid(false);
                confirmPaymentButton.setEnabled(true);
            }
        });
        paymentContentLayout.add(onlinePaymentGroup, onlineReferenceField);
    }

    private void setupCardPaymentContent() {
        cardPaymentGroup = new RadioButtonGroup<>();
        cardPaymentGroup.setLabel("Select Card Type");
        cardPaymentGroup.setItems("Visa", "BDO");
        cardPaymentGroup.setValue("Visa");

        cardNumberField = new TextField("Card Number");
        cardNumberField.setWidthFull();
        cardNumberField.setRequired(true);
        cardNumberField.setHelperText("Enter numbers only");
        cardNumberField.addValueChangeListener(event -> {
            String value = event.getValue();
            if (!value.isEmpty() && !value.matches(NUMERIC_PATTERN)) {
                cardNumberField.setInvalid(true);
                cardNumberField.setErrorMessage("Please enter numbers only");
                confirmPaymentButton.setEnabled(false);
            } else if (value.isEmpty()) {
                cardNumberField.setInvalid(true);
                cardNumberField.setErrorMessage("Card number is required");
                confirmPaymentButton.setEnabled(false);
            } else {
                cardNumberField.setInvalid(false);
                confirmPaymentButton.setEnabled(true);
            }
        });

        paymentContentLayout.add(cardPaymentGroup, cardNumberField);
    }

    private void setupCashPaymentContent() {
        BigDecimal totalAmount = new BigDecimal(totalField.getValue().replaceAll("[^0-9.]", "").trim());

        receivedAmountField = new TextField("Amount Received");
        receivedAmountField.setWidthFull();
        receivedAmountField.setRequired(true);
        receivedAmountField.setHelperText("Enter numbers only");

        receivedAmountField.addValueChangeListener(event -> {
            String value = event.getValue().replaceAll("[^0-9.]", "");

            if (!event.getValue().equals(value)) {
                receivedAmountField.setValue(value);
                receivedAmountField.setErrorMessage("Please enter numbers only");
                return;
            }

            try {
                if (value.isEmpty()) {
                    changeField.setValue("Enter amount");
                    confirmPaymentButton.setEnabled(false);
                    return;
                }

                BigDecimal received = new BigDecimal(value);
                BigDecimal change = received.subtract(totalAmount);

                changeField.setValue(String.format("₱%.2f", change));
                confirmPaymentButton.setEnabled(change.compareTo(BigDecimal.ZERO) >= 0);
            } catch (NumberFormatException e) {
                changeField.setValue("Invalid amount");
                confirmPaymentButton.setEnabled(false);
            }
        });

        changeField = new TextField("Change");
        changeField.setWidthFull();
        changeField.setReadOnly(true);
        changeField.setValue("₱0.00");

        paymentContentLayout.add(receivedAmountField, changeField);
    }

    private void processPayment() {
        boolean isValid = validatePaymentInformation();
        if (!isValid) {
            return;
        }

        String paymentMethod = "";
        String details = "";
        BigDecimal totalAmount = new BigDecimal(totalField.getValue().replaceAll("[^0-9.]", "").trim());

        switch (paymentMethodTabs.getSelectedIndex()) {
            case 0:
                paymentMethod = "Online - " + onlinePaymentGroup.getValue();
                details = "Reference Number: " + onlineReferenceField.getValue();
                break;
            case 1:
                paymentMethod = "Card - " + cardPaymentGroup.getValue();
                details = "Card Number: " + cardNumberField.getValue();
                break;
            case 2:
                paymentMethod = "Cash";
                BigDecimal received = new BigDecimal(receivedAmountField.getValue().replaceAll("[^0-9.]", "").trim());
                details = "Amount Received: ₱" + received + "\nChange: " + changeField.getValue();
                break;
        }

        try {
            Sale sale = dataManager.create(Sale.class);
            sale.setSaleDate(LocalDateTime.now());
            sale.setPaymentMethod(paymentMethod);

            BigDecimal subtotal = receiptItems.stream()
                    .map(ReceiptItem::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal tax = subtotal.multiply(new BigDecimal("0.12")).setScale(2, RoundingMode.HALF_UP);
            sale.setTaxAmount(tax);

            BigDecimal preDiscountTotal = subtotal.add(tax);
            BigDecimal discountPercentage = BigDecimal.ZERO;
            if (seniorCitizenCheckbox.getValue() || pwdCheckbox.getValue()) {
                discountPercentage = new BigDecimal("0.10");
            }
            BigDecimal discountAmount = preDiscountTotal.multiply(discountPercentage).setScale(2, RoundingMode.HALF_UP);
            BigDecimal total = preDiscountTotal.subtract(discountAmount);
            sale.setTotalAmount(total);
            sale.setDiscountAmount(discountAmount);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User currentUser = (User) authentication.getPrincipal();
                sale.setCashier(currentUser); // Correct way to associate the cashier
            } else {
                showError("Error: Could not determine the cashier. Please log in.");
                return;
            }

            List<SaleItem> saleItemsList = new ArrayList<>();
            for (ReceiptItem item : receiptItems) {
                SaleItem saleItem = dataManager.create(SaleItem.class);
                saleItem.setSale(sale);
                saleItem.setStock(item.getStock());
                int quantity = item.getSaleType().equals("Unit") ? item.getUnitsSold() : item.getPackagesSold();
                saleItem.setQuantity(quantity);
                saleItem.setSaleType(item.getSaleType());
                saleItem.setUnitPrice(item.getUnitPrice());
                saleItem.setTotalAmount(item.getTotal());
                saleItemsList.add(saleItem);

                // Update Stock quantity
                Stock stock = dataManager.load(Stock.class)
                        .id(item.getStock().getId())
                        .one();
                if (item.getSaleType().equals("Unit")) {
                    stock.setTotalUnits(stock.getTotalUnits() - item.getUnitsSold());
                    stock.setPackageQuantity(stock.getTotalUnits() / stock.getUnitsPerPackage());
                } else {
                    stock.setPackageQuantity(stock.getPackageQuantity() - item.getPackagesSold());
                    stock.setTotalUnits(stock.getPackageQuantity() * stock.getUnitsPerPackage());
                }
                dataManager.save(stock);
            }

            sale.setSaleItems(saleItemsList);
            dataManager.save(sale);
            exportToImage();

            String successMessage = "Payment processed successfully!\n" +
                    "Payment Method: " + paymentMethod + "\n" +
                    "Total: " + totalField.getValue() + "\n" + details;

            Notification.show(successMessage, 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            paymentDialog.close();
            receiptItems.clear();
            updateReceipt();
            resetSelectionForm();

        } catch (Exception e) {
            showError("Error processing payment: " + e.getMessage());
            e.printStackTrace(); // Remove in production
        }
    }

    private boolean validatePaymentInformation() {
        switch (paymentMethodTabs.getSelectedIndex()) {
            case 0:
                if (onlineReferenceField.getValue().isEmpty()) {
                    showError("Please enter a reference number");
                    return false;
                }
                if (!onlineReferenceField.getValue().matches(NUMERIC_PATTERN)) {
                    showError("Reference number must contain only numbers");
                    return false;
                }
                break;

            case 1:
                if (cardNumberField.getValue().isEmpty()) {
                    showError("Please enter a card number");
                    return false;
                }
                if (!cardNumberField.getValue().matches(NUMERIC_PATTERN)) {
                    showError("Card number must contain only numbers");
                    return false;
                }
                break;

            case 2:
                if (receivedAmountField.getValue().isEmpty()) {
                    showError("Please enter the received amount");
                    return false;
                }
                try {
                    BigDecimal received = new BigDecimal(receivedAmountField.getValue().replaceAll("[^0-9.]", "").trim());
                    BigDecimal total = new BigDecimal(totalField.getValue().replaceAll("[^0-9.]", "").trim());
                    if (received.compareTo(total) < 0) {
                        showError("Insufficient payment amount");
                        return false;
                    }
                } catch (NumberFormatException e) {
                    showError("Invalid payment amount");
                    return false;
                }
                break;
        }
        return true;
    }

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
                .query("select s from Stock s where s.totalUnits > 0 and s.expirationDate > current_date order by s.brandName asc")
                .list();
    }

    private void resetSelectionForm() {
        medicineSelect.setValue(null);
        saleTypeComboBox.setValue("Unit");
        quantityField.setValue(1);
    }

    private void showError(String message) {
        Notification.show(message, 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private String getCurrentUserFirstName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            return currentUser.getFirstName() != null ? currentUser.getFirstName() : currentUser.getUsername();
        }
        return "Unknown";
    }

    private void applyDiscountIfChecked() {
        updateReceipt();
        // Update the totalHeader in the payment dialog
        if (paymentDialog != null && paymentDialog.isOpened()) {
            totalHeader.setText("Total Amount: " + totalField.getValue());
        }
    }

}