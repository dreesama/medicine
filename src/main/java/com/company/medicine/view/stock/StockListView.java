package com.company.medicine.view.stock;

import com.company.medicine.entity.Stock;
import com.company.medicine.service.StockListPdfExportService;
import com.company.medicine.service.ExcelExportService;
import com.company.medicine.view.main.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import io.jmix.flowui.view.*;
import io.jmix.core.DataManager;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.model.CollectionContainer;

import org.springframework.beans.factory.annotation.Autowired;
import java.io.ByteArrayInputStream;
import java.util.List;

@Route(value = "stocks", layout = MainView.class)
@ViewController("Stock.list")
@ViewDescriptor("stock-list-view.xml")
@LookupComponent("stocksDataGrid")
@DialogMode(width = "64em")
public class StockListView extends StandardListView<Stock> {

    @Autowired
    private DataManager dataManager;

    @Autowired
    private StockListPdfExportService stockListPdfExportService;

    @Autowired
    private ExcelExportService excelExportService;

    @ViewComponent
    private DataGrid<Stock> stocksDataGrid;

    @ViewComponent
    private CollectionLoader<Stock> stocksDl;

    @ViewComponent
    private CollectionContainer<Stock> stocksDc;

    @ViewComponent
    private Button exportPdfBtn;

    @ViewComponent
    private Button exportExcelBtn;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        refreshStockList();
    }

    private void refreshStockList() {
        stocksDl.setQuery("select e from Stock e where e.quantity > 0");
        stocksDl.load();
    }

    @Subscribe
    public void onInit(InitEvent event) {
        exportPdfBtn.addClickListener(e -> onExportPdfBtnClick());
        exportExcelBtn.addClickListener(e -> onExportExcelBtnClick());
    }

    private void onExportPdfBtnClick() {
        try {
            List<Stock> allStocks = dataManager.load(Stock.class)
                    .query("select e from Stock e where e.quantity > 0")
                    .list();

            byte[] pdfBytes = stockListPdfExportService.exportStockListToPdf(allStocks);

            String fileName = "stock_list.pdf";
            StreamResource resource = new StreamResource(fileName, () -> new ByteArrayInputStream(pdfBytes));

            Anchor downloadLink = new Anchor();
            downloadLink.setHref(resource);
            downloadLink.getElement().setAttribute("download", true);
            downloadLink.setTarget("_blank");
            downloadLink.getElement().setAttribute("type", "application/pdf");

            getUI().ifPresent(ui -> {
                ui.getElement().appendChild(downloadLink.getElement());
                downloadLink.getElement().executeJs(
                        "const link = this;" +
                                "setTimeout(() => {" +
                                "  link.click();" +
                                "  link.remove();" +
                                "}, 100);"
                );
            });

            Notification.show("Report Generated", 3000, Notification.Position.MIDDLE);
        } catch (Exception e) {
            Notification.show("Error generating PDF: " + e.getMessage(),
                    3000,
                    Notification.Position.MIDDLE);
            e.printStackTrace();
        }
    }

    private void onExportExcelBtnClick() {
        try {
            List<Stock> allStocks = dataManager.load(Stock.class)
                    .query("select e from Stock e where e.quantity > 0")
                    .list();

            byte[] excelBytes = excelExportService.exportStocksToExcel(allStocks);

            String fileName = "stock_list.xlsx";
            StreamResource resource = new StreamResource(fileName, () -> new ByteArrayInputStream(excelBytes));

            Anchor downloadLink = new Anchor();
            downloadLink.setHref(resource);
            downloadLink.getElement().setAttribute("download", true);
            downloadLink.setTarget("_blank");
            downloadLink.getElement().setAttribute("type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            getUI().ifPresent(ui -> {
                ui.getElement().appendChild(downloadLink.getElement());
                downloadLink.getElement().executeJs(
                        "const link = this;" +
                                "setTimeout(() => {" +
                                "  link.click();" +
                                "  link.remove();" +
                                "}, 100);"
                );
            });

            Notification.show("Report Generated", 3000, Notification.Position.MIDDLE);
        } catch (Exception e) {
            Notification.show("Error generating Excel: " + e.getMessage(),
                    3000,
                    Notification.Position.MIDDLE);
            e.printStackTrace();
        }
    }

    public void onQuantityUpdated(Stock stock) {
        if (stock.getQuantity() <= 0) {
            dataManager.remove(stock);
            refreshStockList();
        }
    }
}