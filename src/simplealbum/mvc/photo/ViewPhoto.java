/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.photo;

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
import utils.KeyStrokesUtil;
import utils.Utils;

/**
 *
 * @author elialva
 */
public class ViewPhoto {

    private ControllerPhoto controllerPicture;
    private final List<JLabel> carousel;
    private final List<JLabel> picture;
    private final List listEngaged;
    private JPanel panel;
    private JLabel smallPic;

    ViewPhoto(final Container container) {

        MyListener myListener = new MyListener();
        //
        carousel = new ArrayList<>();
        picture = new ArrayList<>();
        listEngaged = new ArrayList();

        List<Component> components = Utils.getAllComponents(container);
        for (Component component : components) {
            if (component.getName() != null && component.getName().startsWith("_CAR")) {
                component.setFocusable(true);
                component.addFocusListener(myListener);
                carousel.add((JLabel) component);
            }
            if (component.getName() != null && component.getName().startsWith("_SPIC")) {
                component.setFocusable(true);
                component.addFocusListener(myListener);
                smallPic = (JLabel) component;
//                carousel.add((JLabel) component);
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
        KeyStrokesUtil.assignKeyStrokes(panel, JComponent.WHEN_IN_FOCUSED_WINDOW, "released F5", new MyAction("released F5"));
        KeyStrokesUtil.assignKeyStrokes(panel, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "pressed ENTER", new MyAction("pressed ENTER"));
    }

    void setController(ControllerPhoto controller) {
        this.controllerPicture = controller;
//        controllerPicture.setCarousel(carousel);
    }

    public int traverse(int i) {
        Component focusOwner = FocusManager.getCurrentManager().getFocusOwner();

        int indexOf = carousel.indexOf(focusOwner) + i;

        int size = 0;
        size = carousel.stream().filter((label) -> (label.getIcon() != null)).map((_item) -> 1).reduce(size, Integer::sum);

        if (indexOf >= size) {
            indexOf = 0;
        }
        if (indexOf < 0) {
            indexOf = size > 0 ? size - 1 : 0;
        }

        carousel.get(indexOf).grabFocus();

        return indexOf;
    }

    void showPicture(JLabel label, BufferedImage image) {
        label.setIcon(new ImageIcon(image));
    }

    List<JLabel> getLabels() {
        return carousel;
    }

    void clearLabels() {
        for (JLabel carousel1 : carousel) {
            carousel1.setIcon(null);
        }
    }

    JLabel getPic() {
        return picture.get(0);
    }

    JLabel getSmallPic() {
        return smallPic;
    }

    JLabel getLabel(Integer currentIndex) {
        return carousel.get(currentIndex);
    }

    void requestFocusInWindow(int indexOf) {
        carousel.get(indexOf).requestFocusInWindow();
    }

    private void focusGained(FocusEvent e) {
        controllerPicture.focusGained(e);
    }

    private void focusLost(FocusEvent e) {
        controllerPicture.focusLost(e);
    }

    private void actionPerformed(String name) {
        switch (name) {
            case "pressed ENTER":
                //TODO Controller guarda la foto vigenete y da el salto al siguiente elemento.
                controllerPicture.processSelection();
                break;
            case "released F5":
                controllerPicture.getPicture();
                break;
            case "released SPACE":
                controllerPicture.streamToggle();
                break;
            case "released CONTROL":
                Component component = FocusManager.getCurrentManager().getFocusOwner();
                controllerPicture.remove(component, carousel);
                break;
            case "RIGHT":
                controllerPicture.amplify("DO_RIGHT");
                break;
            case "LEFT":
                controllerPicture.amplify("DO_LEFT");
                break;
            default:
                throw new AssertionError();
        }
    }

    private class MyListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {
            ViewPhoto.this.focusGained(e);
        }

        @Override
        public void focusLost(FocusEvent e) {
            ViewPhoto.this.focusLost(e);
        }
    }

    private class MyAction extends AbstractAction {

        private MyAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ViewPhoto.this.actionPerformed(getValue(NAME).toString());
        }
    }
}
