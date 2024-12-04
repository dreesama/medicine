package com.company.medicine.view.stock;

import com.company.medicine.entity.Medicine;
import com.company.medicine.entity.Stock;
import com.company.medicine.entity.User;
import com.company.medicine.view.main.MainView;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import io.jmix.core.MetadataTools;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Route(value = "stocks/:id", layout = MainView.class)
@ViewController("Stock.detail")
@ViewDescriptor("stock-detail-view.xml")
@EditedEntityContainer("stockDc")
@DialogMode(width = "800px", height = "500px")
public class StockDetailView extends StandardDetailView<Stock> {

    @ViewComponent
    private EntityComboBox<Medicine> medicineField;

    @ViewComponent
    private TextField unitsPerPackageField;

    @ViewComponent
    private TextField packageQuantityField;

    @Autowired
    private MetadataTools metadataTools;

    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Subscribe
    public void onInitEntity(InitEntityEvent<Stock> event) {
        // Only calculate total units for new stock entries
        if (event.getEntity().getId() == null) {
            updateTotalUnits(event.getEntity());
        }
    }
    @Subscribe
    public void onBeforeSave(BeforeSaveEvent event) {
        Stock stock = getEditedEntity();
        // Only update if this is a new stock or we're in the stock detail view (not POS)
        if (stock.getId() == null || event.getSource().equals(this)) {
            updateTotalUnits(stock);
        }
    }
    @Subscribe
    public void onInit(InitEvent event) {
        medicineField.addValueChangeListener(this::onMedicineFieldValueChange);
        medicineField.setItemLabelGenerator(this::generateMedicineLabel);
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        Stock stock = getEditedEntity();
        if (stock.getId() == null) {
            User currentUser = (User) currentAuthentication.getUser();
            stock.setCreatedBy(currentUser);
        }
    }

    @Subscribe("packageQuantityField")
    public void onPackageQuantityFieldValueChange(HasValue.ValueChangeEvent<String> event) {
        if (event.getValue() != null && !event.getValue().isEmpty()) {
            try {
                Stock stock = getEditedEntity();
                Integer value = Integer.parseInt(event.getValue());
                stock.setPackageQuantity(value);
            } catch (NumberFormatException e) {
                // Handle invalid input if needed
            }
        }
    }

    @Subscribe("unitsPerPackageField")
    public void onUnitsPerPackageFieldValueChange(HasValue.ValueChangeEvent<String> event) {
        if (event.getValue() != null && !event.getValue().isEmpty()) {
            try {
                Stock stock = getEditedEntity();
                Integer value = Integer.parseInt(event.getValue());
                stock.setUnitsPerPackage(value);
                calculateUnitPrice();
            } catch (NumberFormatException e) {
                // Handle invalid input if needed
            }
        }
    }
    private void updateTotalUnits(Stock stock) {
        if (stock.getPackageQuantity() != null && stock.getUnitsPerPackage() != null) {
            Integer totalUnits = stock.getPackageQuantity() * stock.getUnitsPerPackage();
            stock.setTotalUnits(totalUnits);
        } else {
            stock.setTotalUnits(0);
        }
    }

    private void calculateUnitPrice() {
        Stock stock = getEditedEntity();
        BigDecimal price = stock.getPricePerPackage();
        Integer unitsPerPackage = stock.getUnitsPerPackage();

        if (price != null && unitsPerPackage != null && unitsPerPackage > 0) {
            BigDecimal unitPrice = price.divide(new BigDecimal(unitsPerPackage), 2, RoundingMode.HALF_UP);
            stock.setPricePerUnit(unitPrice);
        } else {
            stock.setPricePerUnit(null);
        }
    }

    private String generateMedicineLabel(Medicine medicine) {
        if (medicine == null) {
            return "";
        }
        return medicine.getBrandName() != null
                ? medicine.getBrandName()
                : metadataTools.getInstanceName(medicine);
    }

    private void onMedicineFieldValueChange(HasValue.ValueChangeEvent<Medicine> event) {
        Medicine medicine = event.getValue();
        Stock stock = getEditedEntity();

        if (medicine != null) {
            stock.setBrandName(medicine.getBrandName());
            stock.setActiveIngredientName(medicine.getActiveIngredientName());
            stock.setActiveIngredientStrength(medicine.getActiveIngredientStrength());
            stock.setDosageForm(medicine.getDosageForm());
            stock.setPricePerPackage(medicine.getPrice());

            // Set default values and trigger calculations
            if (stock.getUnitsPerPackage() == null) {
                stock.setUnitsPerPackage(1);
            }
            if (stock.getPackageQuantity() == null) {
                stock.setPackageQuantity(0);
            }

            calculateUnitPrice();
        } else {
            // Clear the fields if medicine is deselected
            stock.setBrandName(null);
            stock.setActiveIngredientName(null);
            stock.setActiveIngredientStrength(null);
            stock.setDosageForm(null);
            stock.setPricePerPackage(null);
            stock.setPricePerUnit(null);
            stock.setUnitsPerPackage(null);
            stock.setPackageQuantity(null);
            stock.setTotalUnits(null);
        }
    }
}