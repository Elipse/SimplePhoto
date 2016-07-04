/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.autocomplete;

import utils.Utils;
import java.awt.Container;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import utils.KeyStrokesUtil;

/**
 *
 * @author elialva
 */
public class View {

    private List<JTextField> list;
    private JLayeredPane layeredPane;

    private Frame frame;
    private int i;
    private Controller controller;
    private DController dController;
    private Object last;

    View(Container container) {
        list = new ArrayList<>();
        List c = Utils.getAllComponents(container);
        for (Object c1 : c) {
            if (c1 instanceof JTextField) {
                JTextField tf = (JTextField) c1;
                if (tf.getName().startsWith("_")) {
                    System.out.println("tf " + tf);
                    list.add(tf);
                }
            }
            if (c1 instanceof JLayeredPane) {
                System.out.println("EUREKA " + c1);
                JPanel p = new JPanel();
                p.add(new JLabel("Esto vuela"));
                JLayeredPane pane = (JLayeredPane) c1;
                pane.add(p, new Integer(2));
                p.setVisible(true);
            }
            if (c1 instanceof Frame) {
                this.frame = (Frame) c1;
            }
        }

        MyListener listener = new MyListener();
        list.stream().forEach((tf) -> {
            tf.addFocusListener(listener);
            tf.addKeyListener(listener);
            System.out.println("Los campos son " + tf.getName());
        });
    }

    public Frame getFrame() {
        return frame;
    }

    private void focusGained(FocusEvent e) {
        String action = dController.getAction();

        switch (action) {
            case "NEXT":
                dController.setAction("");
                KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
                return;
            case "STAY":
                dController.setAction("");
                return;
            case "":
                if (!dController.isShowing()) {
                    JTextField textField = (JTextField) e.getSource();
                    Point locationOnScreen = textField.getLocationOnScreen();
                    locationOnScreen.translate(0, textField.getHeight());
                    dController.focusGained(((JTextField) e.getSource()).getName(), ((JTextField) e.getSource()).getText());
                    dController.setLocation(locationOnScreen);
                    dController.setVisible(true);
                }
        }
    }

    private void focusLost(FocusEvent e) {
        last = e.getSource();
    }

    private void keyTyped(KeyEvent e) {
        e.consume();
        if (!dController.isShowing()) {
            dController.setVisible(true);
        }
    }

    void setController(Controller controller) {
        this.controller = controller;
    }

    void setDController(DController dController) {
        this.dController = dController;
    }

    class MyListener implements FocusListener, KeyListener {

        @Override
        public void focusGained(FocusEvent e) {
            View.this.focusGained(e);
        }

        @Override
        public void focusLost(FocusEvent e) {
            View.this.focusLost(e);
        }

        @Override
        public void keyTyped(KeyEvent e) {
            View.this.keyTyped(e);

        }

        @Override
        public void keyPressed(KeyEvent e) {
//            View.this.keyReleased(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
//            View.this.keyReleased(e);
        }
    }
}
