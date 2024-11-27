package com.company.medicine.view.medicine;

import com.company.medicine.entity.Medicine;
import com.company.medicine.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "medicines/:id", layout = MainView.class)
@ViewController("Medicine.detail")
@ViewDescriptor("medicine-detail-view.xml")
@EditedEntityContainer("medicineDc")
@DialogMode(width = "800px",height = "800px")
public class MedicineDetailView extends StandardDetailView<Medicine> {
}