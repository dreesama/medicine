package com.company.medicine.view.sale;

import com.company.medicine.entity.Sale;
import com.company.medicine.view.main.MainView;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.view.*;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;

@Route(value = "sales", layout = MainView.class)
@ViewController(id = "Sale.list")
@ViewDescriptor(path = "sale-list-view.xml")
@LookupComponent("salesDataGrid")
@DialogMode(width = "64em")
public class SaleListView extends StandardListView<Sale> {

    @ViewComponent
    private Span totalAmountLabel;

    @Autowired
    private DataManager dataManager;

    @Subscribe
    public void onInit(InitEvent event) {
        updateTotalAmount();
    }

    @Subscribe(id = "salesDc", target = Target.DATA_CONTAINER)
    public void onSalesDataContainerCollectionChange(CollectionContainer.CollectionChangeEvent<Sale> event) {
        updateTotalAmount();
    }

    private void updateTotalAmount() {
        BigDecimal totalAmount = dataManager.loadValue(
                        "select sum(s.totalAmount) from Sale s", BigDecimal.class)
                .optional()
                .orElse(BigDecimal.ZERO);


        totalAmountLabel.setText(String.format("₱%.2f", totalAmount));
        totalAmountLabel.getStyle().set("font-weight", "bold"); // Add bold styling

    }
}