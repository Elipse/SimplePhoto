/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

/**
 *
 * @author elialva
 */
public class KeyStrokesUtil {

    public static void reassignKeyStrokes(JComponent component, int condition, String keyStrokeOld, String keyStrokeNew) {
        Object binding = component.getInputMap(condition).get(KeyStroke.getKeyStroke(keyStrokeOld));
        System.out.println("binding " + binding);
        Action actionC = component.getActionMap().get(binding);
        System.out.println("actionC " + actionC);

        component.getInputMap(condition).put(KeyStroke.getKeyStroke(keyStrokeOld), "none");

        System.out.println("Poniendo... " + keyStrokeNew + "-" + binding);
        component.getInputMap(condition).put(KeyStroke.getKeyStroke(keyStrokeNew), binding);
        component.getActionMap().put(binding, actionC);
    }

    public static void assignKeyStrokes(JComponent component, int condition, String keyStrokeNew, Action action) {

        component.getInputMap(condition).put(KeyStroke.getKeyStroke(keyStrokeNew), action.getValue("Name"));
        component.getActionMap().put(action.getValue("Name"), action);
    }

    private static final KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    private static final String dispatchWindowClosingActionMapKey = "com.spodding.tackline.dispatch:WINDOW_CLOSING";

    public static void installEscapeCloseOperation(final JDialog dialog) {
        Action dispatchClosing = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                dialog.dispatchEvent(new WindowEvent(
                        dialog, WindowEvent.WINDOW_CLOSING
                ));
            }
        };
        JRootPane root = dialog.getRootPane();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, dispatchWindowClosingActionMapKey);
        root.getActionMap().put(dispatchWindowClosingActionMapKey, dispatchClosing);
    }
}
