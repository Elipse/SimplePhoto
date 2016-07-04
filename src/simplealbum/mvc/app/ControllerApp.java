/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.app;

import java.awt.KeyboardFocusManager;

/**
 *
 * @author elialva
 */
public class ControllerApp {

    private final ViewApp view;
    private final ModelApp model;

    ControllerApp(ViewApp view, ModelApp model) {
        this.view = view;
        this.model = model;

        this.view.setController(ControllerApp.this);

    }

    void focusNextComponent(String owner) {
        switch (owner) {
            case "PICPANEL":
                view.jDBConn.grabFocus();
                break;
            case "DBCONN":
                KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
                break;

            default:
                KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
        }

    }

}
