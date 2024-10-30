package com.company.medicine.view.stock;

import com.company.medicine.entity.Stock;

import com.company.medicine.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "stocks", layout = MainView.class)
@ViewController("Stock.list")
@ViewDescriptor("stock-list-view.xml")
@LookupComponent("stocksDataGrid")
@DialogMode(width = "64em")
public class StockListView extends StandardListView<Stock> {
}