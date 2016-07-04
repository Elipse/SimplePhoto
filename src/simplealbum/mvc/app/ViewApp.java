/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.app;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.function.Predicate;
import javax.swing.AbstractAction;
import static javax.swing.Action.NAME;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextPane;
import simplealbum.mvc.autocomplete.DController;
import utils.KeyStrokesUtil;
import utils.Utils;

/**
 *
 * @author elialva
 */
public class ViewApp {

    private JTextPane jTextPane;
    private ControllerApp controller;
    private JPanel jPicPanel;
    JButton jDBConn;

    ViewApp(JRootPane rootPane) {
        List<Component> components = Utils.getAllComponents(rootPane);
        for (Component component : components) {
            String name = component.getName();
            name = name != null ? name : "";
            switch (name) {
                case "_DESCRIPTION":
                    jTextPane = (JTextPane) component;
                    break;
                case "_PICPANEL":
                    jPicPanel = (JPanel) component;
                    break;
                case "_DBCONN":
                    jDBConn = (JButton) component;
                    break;
                default:
                    System.out.println("name " + name);
//                    throw new AssertionError();
            }
        }

        KeyStrokesUtil.reassignKeyStrokes(jTextPane, JComponent.WHEN_FOCUSED, "pressed ENTER", "pressed shift ENTER");
        KeyStrokesUtil.assignKeyStrokes(jTextPane, JComponent.WHEN_FOCUSED, "pressed ENTER", new MyAction("pressed ENTER"));

        KeyStrokesUtil.assignKeyStrokes(jPicPanel, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "pressed ENTER", new MyAction("pressed ENTER PICPANEL"));
        KeyStrokesUtil.assignKeyStrokes(jDBConn, JComponent.WHEN_FOCUSED, "pressed ENTER", new MyAction("pressed ENTER DBCONN"));
    }

    private void actionPerformed(String name) {
        switch (name) {
            case "pressed ENTER":
                controller.focusNextComponent("");
                System.out.println("Llamo a controller para focus");
                break;
            case "pressed ENTER PICPANEL":
                controller.focusNextComponent("PICPANEL");
                System.out.println("Llamo a controller para picpanel");
                break;
            case "pressed ENTER DBCONN":
                controller.focusNextComponent("DBCONN");
                System.out.println("Llamo a controller para dbconn");
                break;
            default:
                throw new AssertionError();
        }
    }

    void setController(ControllerApp controller) {
        this.controller = controller;
    }

    private class MyAction extends AbstractAction {

        private MyAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ViewApp.this.actionPerformed(getValue(NAME).toString());
        }
    }

}
