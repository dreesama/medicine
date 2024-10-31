package com.company.medicine.view.stock;

import com.company.medicine.entity.Stock;
import com.company.medicine.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "expired-medicine", layout = MainView.class)
@ViewController("ExpiredMedicine")
@ViewDescriptor("expired-medicine-view.xml")
@LookupComponent("stocksDataGrid")
@DialogMode(width = "64em")
public class ExpiredMedicine extends StandardListView<Stock> {
}