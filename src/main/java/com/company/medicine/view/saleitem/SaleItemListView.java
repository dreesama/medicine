package com.company.medicine.view.saleitem;

import com.company.medicine.entity.SaleItem;
import com.company.medicine.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "saleItems", layout = MainView.class)
@ViewController(id = "SaleItem.list")
@ViewDescriptor(path = "sale-item-list-view.xml")
@LookupComponent("saleItemsDataGrid")
@DialogMode(width = "64em")
public class SaleItemListView extends StandardListView<SaleItem> {
}