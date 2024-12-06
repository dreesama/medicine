package com.company.medicine.view.sale;

import com.company.medicine.entity.Sale;
import com.company.medicine.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "sales/:id", layout = MainView.class)
@ViewController("Sale.detail")
@ViewDescriptor("sale-detail-view.xml")
@EditedEntityContainer("saleDc")
public class SaleDetailView extends StandardDetailView<Sale> {
}