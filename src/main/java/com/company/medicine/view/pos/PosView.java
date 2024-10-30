package com.company.medicine.view.pos;

import com.company.medicine.entity.Stock;
import com.company.medicine.view.main.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

@Route(value = "pos", layout = MainView.class)
@ViewController("PosView")
@ViewDescriptor("pos-view.xml")
public class PosView extends StandardView {

    @Autowired
    private DataManager dataManager;

    private ComboBox<Stock> medicineSelect;
    private IntegerField quantityField;
    private TextField totalField;
    private Button processButton;

    @Subscribe
    public void onInit(InitEvent event) {
        initComponents();
        setupListeners();
    }

    private void initComponents() {
        VerticalLayout layout = new VerticalLayout();

        H3 title = new H3("Point of Sale");

        // Medicine selection
        medicineSelect = new ComboBox<>("Select Medicine");
        medicineSelect.setItems(loadAvailableStock());
        medicineSelect.setItemLabelGenerator(stock ->
                stock.getBrandName() + " - " + stock.getActiveIngredientName() +
                        " (" + stock.getActiveIngredientStrength() + ")");

        // Quantity input
        quantityField = new IntegerField("Quantity");
        quantityField.setValue(1);
        quantityField.setMin(1);

        // Total display
        totalField = new TextField("Total Amount");
        totalField.setReadOnly(true);

        // Process button
        processButton = new Button("Process Sale", event -> processSale());

        layout.add(title, medicineSelect, quantityField, totalField, processButton);
        getContent().add(layout);
    }

    private void setupListeners() {
        quantityField.addValueChangeListener(event -> updateTotal());
        medicineSelect.addValueChangeListener(event -> {
            updateTotal();
            if (event.getValue() != null) {
                quantityField.setMax(event.getValue().getQuantity());
            }
        });
    }

    private List<Stock> loadAvailableStock() {
        return dataManager.load(Stock.class)
                .query("select s from Stock s where s.quantity > 0")
                .list();
    }

    private void updateTotal() {
        Stock selectedStock = medicineSelect.getValue();
        Integer quantity = quantityField.getValue();

        if (selectedStock != null && quantity != null) {
            BigDecimal total = selectedStock.getPrice().multiply(new BigDecimal(quantity));
            totalField.setValue(total.toString());
        } else {
            totalField.clear();
        }
    }

    private void processSale() {
        Stock selectedStock = medicineSelect.getValue();
        Integer requestedQuantity = quantityField.getValue();

        // Validation
        if (selectedStock == null) {
            showError("Please select a medicine.");
            return;
        }

        if (requestedQuantity == null || requestedQuantity <= 0) {
            showError("Please enter a valid quantity.");
            return;
        }

        if (requestedQuantity > selectedStock.getQuantity()) {
            showError("Insufficient stock. Available: " + selectedStock.getQuantity());
            return;
        }

        if (selectedStock.getExpirationDate() != null &&
                selectedStock.getExpirationDate().before(new java.util.Date())) {
            showError("This medicine has expired.");
            return;
        }

        // Process sale
        try {
            selectedStock.setQuantity(selectedStock.getQuantity() - requestedQuantity);
            dataManager.save(selectedStock);

            Notification.show("Sale processed successfully!",
                            3000,
                            Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            resetForm();

        } catch (Exception e) {
            showError("Error processing sale: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Notification.show(message, 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private void resetForm() {
        medicineSelect.setValue(null);
        quantityField.setValue(1);
        totalField.clear();
        medicineSelect.setItems(loadAvailableStock());
    }
}
