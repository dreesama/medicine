package com.company.medicine.view.dashboard;

import com.company.medicine.entity.Stock;
import com.company.medicine.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.view.*;

@Route(value = "", layout = MainView.class)
@ViewController("DashboardView")
@ViewDescriptor("dashboard-view.xml")
public class DashboardView extends StandardView {

}