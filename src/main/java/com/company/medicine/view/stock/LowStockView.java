package com.company.medicine.view.stock;

import com.company.medicine.entity.Stock;
import com.company.medicine.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "low-stock", layout = MainView.class)
@ViewController("LowStockView")
@ViewDescriptor("low-stock-view.xml")
@LookupComponent("stocksDataGrid")
@DialogMode(width = "64em")
public class LowStockView extends StandardListView<Stock> {
}