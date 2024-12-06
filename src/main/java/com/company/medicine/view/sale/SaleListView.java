package com.company.medicine.view.sale;

import com.company.medicine.entity.Sale;
import com.company.medicine.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "sales", layout = MainView.class)
@ViewController(id = "Sale.list")
@ViewDescriptor(path = "sale-list-view.xml")
@LookupComponent("salesDataGrid")
@DialogMode(width = "64em")
public class SaleListView extends StandardListView<Sale> {
}