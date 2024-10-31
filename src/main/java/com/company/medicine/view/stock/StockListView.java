package com.company.medicine.view.stock;

import com.company.medicine.entity.Stock;
import com.company.medicine.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;
import io.jmix.core.DataManager;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.model.CollectionContainer;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Route(value = "stocks", layout = MainView.class)
@ViewController("Stock.list")
@ViewDescriptor("stock-list-view.xml")
@LookupComponent("stocksDataGrid")
@DialogMode(width = "64em")
public class StockListView extends StandardListView<Stock> {

    @Autowired
    private DataManager dataManager;

    @ViewComponent
    private DataGrid<Stock> stocksDataGrid;

    @ViewComponent
    private CollectionLoader<Stock> stocksDl;

    @ViewComponent
    private CollectionContainer<Stock> stocksDc;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        refreshStockList();
    }

    private void refreshStockList() {
        // Reload data to include only items with quantity > 0
        stocksDl.setQuery("select e from Stock e where e.quantity > 0");
        stocksDl.load();
    }

    // Method to be called when quantity is updated, can be triggered from elsewhere in the code
    public void onQuantityUpdated(Stock stock) {
        if (stock.getQuantity() <= 0) {
            dataManager.remove(stock); // Remove from database if quantity is zero
            refreshStockList(); // Refresh the UI to remove it from the displayed list
        }
    }
}