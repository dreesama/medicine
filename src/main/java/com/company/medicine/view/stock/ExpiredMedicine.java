package com.company.medicine.view.stock;

import com.company.medicine.entity.Stock;
import com.company.medicine.service.ExpiredMedicinePdfExportService;
import com.company.medicine.view.main.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import io.jmix.flowui.view.*;
import io.jmix.flowui.model.CollectionContainer;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.util.List;

@Route(value = "expired-medicine", layout = MainView.class)
@ViewController("ExpiredMedicine")
@ViewDescriptor("expired-medicine-view.xml")
@LookupComponent("stocksDataGrid")
@DialogMode(width = "64em")
public class ExpiredMedicine extends StandardListView<Stock> {

    @Autowired
    private ExpiredMedicinePdfExportService expiredMedicinePdfExportService;

    @ViewComponent
    private Button exportPdfBtn;

    @ViewComponent
    private CollectionContainer<Stock> stocksDc;

    @Subscribe
    public void onInit(InitEvent event) {
        exportPdfBtn.addClickListener(e -> exportToPdf());
    }

    private void exportToPdf() {
        try {
            List<Stock> stocks = stocksDc.getItems();
            byte[] pdfBytes = expiredMedicinePdfExportService.exportExpiredMedicinesToPdf(stocks);

            String fileName = "expired_medicines.pdf";
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