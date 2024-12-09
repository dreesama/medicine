package com.company.medicine.view.medicine;

import com.company.medicine.entity.Medicine;
import com.company.medicine.service.MedicineListPdfExportService;
import com.company.medicine.service.MedicineExcelExportService;
import com.company.medicine.view.main.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import io.jmix.flowui.view.*;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.DataComponents;
import io.jmix.core.DataManager;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.util.List;

@Route(value = "medicines", layout = MainView.class)
@ViewController("Medicine.list")
@ViewDescriptor("medicine-list-view.xml")
@LookupComponent("medicinesDataGrid")
@DialogMode(width = "64em")
public class  MedicineListView extends StandardListView<Medicine> {

    @Autowired
    private MedicineListPdfExportService medicineListPdfExportService;

    @Autowired
    private MedicineExcelExportService medicineExcelExportService;

    @Autowired
    private DataManager dataManager;

    @ViewComponent
    private Button exportPdfBtn;

    @ViewComponent
    private Button exportExcelBtn;

    @ViewComponent
    private CollectionContainer<Medicine> medicinesDc;

    @Subscribe
    public void onInit(InitEvent event) {
        exportPdfBtn.addClickListener(e -> exportToPdf());
//        exportExcelBtn.addClickListener(e -> exportToExcel());

    }

    private List<Medicine> loadAllMedicines() {
        return dataManager.load(Medicine.class)
                .all()
                .list();
    }

//    private void exportToExcel() {
//        try {
//            // Load all medicines instead of using the container
//            List<Medicine> medicines = loadAllMedicines();
//            byte[] excelBytes = medicineExcelExportService.exportMedicinesToExcel(medicines);
//
//            String fileName = "medicine_list.xlsx";
//            StreamResource resource = new StreamResource(fileName,
//                    () -> new ByteArrayInputStream(excelBytes));
//
//            Anchor downloadLink = new Anchor(resource, "");
//            downloadLink.getElement().setAttribute("download", true);
//            downloadLink.setHref(resource);
//            downloadLink.getElement().setAttribute("type",
//                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//
//            getUI().ifPresent(ui -> {
//                ui.getElement().appendChild(downloadLink.getElement());
//                downloadLink.getElement().executeJs(
//                        "const link = this;" +
//                                "setTimeout(() => {" +
//                                "  link.click();" +
//                                "  link.remove();" +
//                                "}, 100);"
//                );
//            });
//
//            Notification.show("Report Generated",
//                    3000, Notification.Position.MIDDLE);
//        } catch (Exception e) {
//            Notification.show("Error generating Excel: " + e.getMessage(),
//                    3000, Notification.Position.MIDDLE);
//            e.printStackTrace();
//        }
//    }

    private void exportToPdf() {
        try {
            // Load all medicines instead of using the container
            List<Medicine> medicines = loadAllMedicines();
            byte[] pdfBytes = medicineListPdfExportService.exportMedicineListToPdf(medicines);

            String fileName = "medicine_list.pdf";
            StreamResource resource = new StreamResource(fileName,
                    () -> new ByteArrayInputStream(pdfBytes));

            Anchor downloadLink = new Anchor(resource, "");
            downloadLink.getElement().setAttribute("download", true);
            downloadLink.setHref(resource);
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
}