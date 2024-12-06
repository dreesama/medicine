package com.company.medicine.view.saleitem;

import com.company.medicine.entity.SaleItem;
import com.company.medicine.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "saleItems/:id", layout = MainView.class)
@ViewController(id = "SaleItem.detail")
@ViewDescriptor(path = "sale-item-detail-view.xml")
@EditedEntityContainer("saleItemDc")
public class SaleItemDetailView extends StandardDetailView<SaleItem> {
}