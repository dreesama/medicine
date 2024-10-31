package com.company.medicine.view.stock;

import com.company.medicine.entity.Medicine;
import com.company.medicine.entity.Stock;
import com.company.medicine.view.main.MainView;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.router.Route;
import io.jmix.core.MetadataTools;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "stocks/:id", layout = MainView.class)
@ViewController("Stock.detail")
@ViewDescriptor("stock-detail-view.xml")
@EditedEntityContainer("stockDc")
@DialogMode(width = "800px",height = "500px")
public class StockDetailView extends StandardDetailView<Stock> {

    @ViewComponent
    private EntityComboBox<Medicine> medicineField;

    @Autowired
    private MetadataTools metadataTools;

    @Subscribe
    public void onInit(InitEvent event) {
        medicineField.addValueChangeListener(this::onMedicineFieldValueChange);
        medicineField.setItemLabelGenerator(this::generateMedicineLabel);
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
            stock.setPrice(medicine.getPrice());
        } else {
            // Clear the fields if medicine is deselected
            stock.setBrandName(null);
            stock.setActiveIngredientName(null);
            stock.setActiveIngredientStrength(null);
            stock.setDosageForm(null);
            stock.setPrice(null);
        }
    }
}