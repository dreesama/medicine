package com.company.medicine.view.stock;

import com.company.medicine.entity.Stock;
import com.company.medicine.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Route(value = "expiring-soon-view", layout = MainView.class)
@ViewController("ExpiringSoon")
@ViewDescriptor("expiring-soon-view.xml")
@LookupComponent("stocksDataGrid")
@DialogMode(width = "64em")
public class ExpiringSoon extends StandardListView<Stock> {

    @ViewComponent
    private CollectionLoader<Stock> stocksDl; // Autowire the CollectionLoader for stocksDc

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        // Calculate the end date as one month from now
        LocalDate endDateLocal = LocalDate.now().plusMonths(1);

        // Convert LocalDate to java.util.Date
        Date endDate = Date.from(endDateLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Set the endDate parameter for the query
        stocksDl.setParameter("endDate", endDate);
        stocksDl.load(); // Explicitly load data with the parameter
    }
}
