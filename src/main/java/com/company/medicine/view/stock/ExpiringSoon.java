package com.company.medicine.view.stock;

import com.company.medicine.entity.Stock;
import com.company.medicine.service.ExpiringSoonPdfExportService;
import com.company.medicine.view.main.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import io.jmix.flowui.model.CollectionContainer;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Route(value = "expiring-soon-view", layout = MainView.class)
@ViewController("ExpiringSoon")
@ViewDescriptor("expiring-soon-view.xml")
@LookupComponent("stocksDataGrid")
@DialogMode(width = "64em")
public class ExpiringSoon extends StandardListView<Stock> {

    @Autowired
    private ExpiringSoonPdfExportService expiringSoonPdfExportService;

    @ViewComponent
    private Button exportPdfBtn;

    @ViewComponent
    private CollectionContainer<Stock> stocksDc;

    @Subscribe
    public void onInit(InitEvent event) {
        exportPdfBtn.addClickListener(e -> exportToPdf());
    }

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

    private void exportToPdf() {
        try {
            List<Stock> stocks = stocksDc.getItems();
            byte[] pdfBytes = expiringSoonPdfExportService.exportExpiringSoonToPdf(stocks);

            String fileName = "expiring_soon_medicines.pdf";
            StreamResource resource = new StreamResource(fileName, () -> new ByteArrayInputStream(pdfBytes));

            // Create a new anchor component
            Anchor downloadLink = new Anchor();
            downloadLink.setHref(resource);
            downloadLink.getElement().setAttribute("download", true);
            downloadLink.setTarget("_blank"); // Opens in new tab
            downloadLink.getElement().setAttribute("type", "application/pdf");

            // Add the anchor to the UI
            getUI().ifPresent(ui -> {
                ui.getElement().appendChild(downloadLink.getElement());
                // Trigger download using JavaScript
                downloadLink.getElement().executeJs(
                        "const link = this;" +
                                "setTimeout(() => {" +
                                "  link.click();" +
                                "  link.remove();" +
                                "}, 100);"
                );
            });

            Notification.show("Report Generated!", 3000, Notification.Position.MIDDLE);
        } catch (Exception e) {
            Notification.show("Error generating PDF: " + e.getMessage(),
                    3000,
                    Notification.Position.MIDDLE);
            e.printStackTrace();
        }
    }
}