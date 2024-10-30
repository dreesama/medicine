package com.company.medicine.view.medicine;

import com.company.medicine.entity.Medicine;
import com.company.medicine.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "medicines/:id", layout = MainView.class)
@ViewController("Medicine.detail")
@ViewDescriptor("medicine-detail-view.xml")
@EditedEntityContainer("medicineDc")
public class MedicineDetailView extends StandardDetailView<Medicine> {
}