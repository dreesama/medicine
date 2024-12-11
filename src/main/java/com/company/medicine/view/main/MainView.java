package com.company.medicine.view.main;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import io.jmix.flowui.app.main.StandardMainView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import com.vaadin.flow.component.AttachEvent;
import io.jmix.flowui.component.applayout.JmixAppLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import jakarta.annotation.security.RolesAllowed;

@Route("main")
@ViewController("MainView")
@ViewDescriptor("main-view.xml")
public class MainView extends StandardMainView {


    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        // Get the app layout
        JmixAppLayout appLayout = (JmixAppLayout) getContent();

        // Set background color
//        appLayout.getElement().getStyle().set("background-color", "#E6F3FF");

        // Create new image component
        Image image = new Image();

        // Set image source
        StreamResource imageResource = new StreamResource("img.png",
                () -> getClass().getResourceAsStream("/META-INF/resources/icons/img.png"));
        image.setSrc(imageResource);
        image.setAlt("Main Image");

        // Set image size
        image.setWidth("700px");
        image.setHeight("auto");

        // Center align the image using styles
        image.getStyle()
                .set("display", "block")
                .set("justify-content", "center")
                .set("align-items", "center")
                .set("margin-top", "20px")
                .set("border-radius", "10px");

        // Find the initial layout and add the image
        VerticalLayout initialLayout = (VerticalLayout) appLayout.getChildren()
                .filter(component -> component instanceof VerticalLayout)
                .findFirst()
                .orElse(null);

        if (initialLayout != null) {
            initialLayout.add(image);
        }
    }
}