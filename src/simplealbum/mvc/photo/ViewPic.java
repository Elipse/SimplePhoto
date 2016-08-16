/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.photo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractAction;
import static javax.swing.Action.NAME;
import javax.swing.FocusManager;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import utils.KeyStrokesUtil;
import utils.Utils;

/**
 *
 * @author elialva
 */
public class ViewPic {

    private ControllerPic controllerPicture;
    private final List<JLabel> carousel;
    private final List<JLabel> picture;
    private final List listEngaged;
    private JPanel panel;
    private JLabel smallPic;
    private JLabel currentLabel;
    private JLabel lastBlueLabel;

    ViewPic(final Container container) {

        MyListener myListener = new MyListener();
        //
        carousel = new ArrayList<>();
        picture = new ArrayList<>();
        listEngaged = new ArrayList();

        List<Component> components = Utils.getAllComponents(container);
        for (Component component : components) {
            if (component.getName() != null && component.getName().startsWith("_CAR")) {
                carousel.add((JLabel) component);
            }
            if (component.getName() != null && component.getName().startsWith("_SPIC")) {
                smallPic = (JLabel) component;
            }
            if (component.getName() != null && component.getName().startsWith("_PICTURE")) {
                picture.add((JLabel) component);
            }

            if (component.getName() != null && component.getName().startsWith("_PICPANEL")) {
                panel = (JPanel) component;
                panel.setFocusable(true);
                panel.addFocusListener(myListener);
            }
        }

        Collections.sort(carousel, (Component o1, Component o2) -> {
            // <editor-fold defaultstate="collapsed" desc="Traversal Policy">
            int x1 = o1.getLocation().x;
            int y1 = o1.getLocation().y;

            int x2 = o2.getLocation().x;
            int y2 = o2.getLocation().y;

            if (y1 > y2) {
                return -1;
            }

            if (y1 < y2) {
                return 1;
            }

            if (y1 == y2) {
                if (x1 == x2) {
                    return 0;
                }
                if (x1 > x2) {
                    return -1;
                }
                if (x1 < x2) {
                    return 1;
                }
            }
            throw new AssertionError();
        });
        // </editor-fold>

        KeyStrokesUtil.assignKeyStrokes(panel, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "RIGHT", new MyAction("RIGHT"));
        KeyStrokesUtil.assignKeyStrokes(panel, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "LEFT", new MyAction("LEFT"));
        KeyStrokesUtil.assignKeyStrokes(panel, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "released CONTROL", new MyAction("released CONTROL"));
        KeyStrokesUtil.assignKeyStrokes(panel, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "released SPACE", new MyAction("released SPACE"));
        KeyStrokesUtil.assignKeyStrokes(panel, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "pressed ENTER", new MyAction("pressed ENTER"));
    }

    void setController(ControllerPic controller) {
        this.controllerPicture = controller;
//        controllerPicture.setCarousel(carousel);
    }

    int traverse(int i) {

        int size = 0;
        size = carousel.stream().filter((label) -> (label.getIcon() != null)).map((_item) -> 1).reduce(size, Integer::sum);

        if (size == 0) {
            return -1;
        }

        if (currentLabel != null) {
            LineBorder border = (LineBorder) currentLabel.getBorder();
            if (border != null && border.getLineColor() != Color.blue) {
                currentLabel.setBorder(null);
            }
        }

        if (currentLabel == smallPic) {
            if (i >= 0) {
                currentLabel = carousel.get(0);
                currentLabel.setBorder(new LineBorder(Color.gray));
                return 0;
            } else {
                currentLabel = carousel.get(size - 1);
                currentLabel.setBorder(new LineBorder(Color.gray));
                return size - 1;
            }
        }

        int indexOf = carousel.indexOf(currentLabel) + i;

        if (indexOf < 0 || indexOf >= size) {
            currentLabel = smallPic;
            currentLabel.setBorder(new LineBorder(Color.gray));
            return -1;
        }

        currentLabel = carousel.get(indexOf);

        currentLabel.setBorder(new LineBorder(Color.gray));
        return indexOf;
    }

    void showPicture(JLabel label, BufferedImage image) {

        if (label == picture.get(0)) {
            label.setIcon(new ImageIcon(image));
            return;
        }

//        System.out.println("Se muestra chiquis " + currentLabel);
//        System.out.println("Se muestra nueva " + label);
        if (lastBlueLabel != null) {
            lastBlueLabel.setBorder(null);
        }

        lastBlueLabel = label;
        lastBlueLabel.setBorder(new LineBorder(Color.blue));
        lastBlueLabel.setIcon(new ImageIcon(image));

        currentLabel = lastBlueLabel;
    }

    void clearLabels() {
        carousel.stream().forEach((label) -> {
            label.setIcon(null);
        });
    }

    void clearBorders() {
        carousel.stream().forEach((label) -> {
            LineBorder border = (LineBorder) label.getBorder();
            if (border != null && border.getLineColor().equals(Color.gray)) {
                label.setBorder(null);
            }
        });
        currentLabel = lastBlueLabel;
    }

    private void focusGained(FocusEvent e) {
        JComponent source = (JComponent) e.getSource();
        source.setBorder(new LineBorder(Color.blue));
//        controllerPicture.streamOn();
    }

    private void focusLost(FocusEvent e) {
        JComponent source = (JComponent) e.getSource();
        source.setBorder(null);
    }

    private void actionPerformed(String name) {
        switch (name) {
            case "pressed ENTER":
                controllerPicture.processSelection();
                break;
            case "released SPACE":
                controllerPicture.streamToggle();
                break;
            case "released CONTROL":
                System.out.println("indexOfFocusOwner " + carousel.indexOf(currentLabel));
                controllerPicture.remove(carousel.indexOf(currentLabel));
                break;
            case "RIGHT":
                System.out.println("RIGHT");
                controllerPicture.amplify("RIGHT");
                break;
            case "LEFT":
                System.out.println("LEFT");
                controllerPicture.amplify("LEFT");
                break;
            default:
                throw new AssertionError();
        }
    }

    void getAway() {
        FocusManager.getCurrentManager().focusNextComponent(smallPic);
    }

    int getCapacity() {
        return carousel.size();
    }

    JLabel getLabel(int i) {
        return carousel.get(i);
    }

    JLabel getLabel() {
        List<JLabel> labels = carousel;
        int c = 0;
        for (JLabel label : labels) {
            c++;
            if (c > 5) {
                //System.out.println("SIN CoNTAR");
                return null;
            }

            if (label.getIcon() == null) {
                return label;
            }
        }
        return null;
    }

    JLabel getPic() {
        return picture.get(0);
    }

    JLabel getSmallPic() {
        return smallPic;
    }

    private class MyListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {
            ViewPic.this.focusGained(e);
        }

        @Override
        public void focusLost(FocusEvent e) {
            ViewPic.this.focusLost(e);
        }
    }

    private class MyAction extends AbstractAction {

        private MyAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ViewPic.this.actionPerformed(getValue(NAME).toString());
        }
    }
}
