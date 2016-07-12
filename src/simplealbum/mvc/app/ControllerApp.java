/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.app;

import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import simplealbum.mvc.photo.ControllerPhoto;

/**
 *
 * @author elialva
 */
public class ControllerApp implements PropertyChangeListener {

    private final ViewApp view;
    private final ModelApp model;
    private PropertyChangeSupport pcs;

    ControllerApp(ViewApp view, ModelApp model) {
        this.view = view;
        this.model = model;

        this.view.setController(ControllerApp.this);

        pcs = new PropertyChangeSupport(this);
    }

    void focusNextComponent(String owner) {
        System.out.println("focusNextComponent:ControllerApp");
        switch (owner) {
            case "PICPANEL":
                System.out.println("UPS PICPANEL");
//                view.jDBConn.grabFocus();
                break;
            case "DBCONN":
                KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
//                pcs.firePropertyChange("NOMASPA'VER", "OLD", "NEW");
                break;

            default:
                System.out.println("DEFA");
                KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
        }

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String property = evt.getPropertyName();
        switch (property) {
            case "currentImage":
                KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
                view.jDBConn.grabFocus();
                break;
            default:
                throw new AssertionError();
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

}
