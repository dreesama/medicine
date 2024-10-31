package com.company.medicine.view.stock;

import com.company.medicine.entity.Stock;

import com.company.medicine.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "out-of-stock", layout = MainView.class)
@ViewController("OutOfStock")
@ViewDescriptor("out-of-stock-view.xml")
@LookupComponent("stocksDataGrid")
@DialogMode(width = "64em")
public class OutOfStock extends StandardListView<Stock> {
}