package com.company.medicine.view.medicine;

import com.company.medicine.entity.Medicine;
import com.company.medicine.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "medicines", layout = MainView.class)
@ViewController("Medicine.list")
@ViewDescriptor("medicine-list-view.xml")
@LookupComponent("medicinesDataGrid")
@DialogMode(width = "64em")
public class MedicineListView extends StandardListView<Medicine> {
}