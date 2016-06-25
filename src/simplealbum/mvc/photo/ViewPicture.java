/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.photo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InvocationEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.FocusManager;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

/**
 *
 * @author elialva
 */
public class ViewPicture {

    private boolean toogle = true;

    private ControllerPicture controllerPicture;
    private final List<JLabel> carousel;
    private final List<JLabel> picture;
    private final Container container;
    private final List listEngaged;
    private JButton buttonGET;
    private JPanel panel;

    ViewPicture(final Container container) {

        this.container = container;
        MyListener myListener = new MyListener();
        //
        carousel = new ArrayList<>();
        picture = new ArrayList<>();
        listEngaged = new ArrayList();

        List<Component> components = getAllComponents(container);
        for (Component component : components) {
            if (component.getName() != null && component.getName().startsWith("_CAR")) {
                System.out.println("Position A " + component.getLocation() + " " + component.isFocusable());
                component.setFocusable(true);
                component.addFocusListener(myListener);
                carousel.add((JLabel) component);
                System.out.println("Position A.B " + component.isFocusable());
            }
            if (component.getName() != null && component.getName().startsWith("_PICTURE")) {
                System.out.println("YOP " + component.isFocusable());
                System.out.println("MY FATH " + component.getParent().isFocusable());
                picture.add((JLabel) component);
            }

            if (component.getName() != null && component.getName().startsWith("_STATE")) {
                JButton button = (JButton) component;
                System.out.println("Butoon ");
                button.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
//                        System.out.println("State " + controllerPicture.getState());
                    }
                });
            }

            if (component.getName() != null && component.getName().startsWith("_GET")) {
                buttonGET = (JButton) component;
                System.out.println("GET button ");
                buttonGET.addActionListener((ActionEvent e) -> {
                    System.out.println("Press GET button");
                    controllerPicture.getPicture();
//                        System.out.println("State " + controllerPicture.getState());
                });
            }

            if (component.getName() != null && component.getName().startsWith("_STOP")) {
                buttonGET = (JButton) component;
                buttonGET.addActionListener((ActionEvent e) -> {
                    controllerPicture.stop();
//                        System.out.println("State " + controllerPicture.getState());
                });
            }

            if (component.getName() != null && component.getName().startsWith("_PICPANEL")) {
                panel = (JPanel) component;
            }

        }

        // <editor-fold defaultstate="collapsed" desc="Traversal Policy">
        Collections.sort(carousel, new Comparator<Component>() {

            @Override
            public int compare(Component o1, Component o2) {
                int x1 = o1.getLocation().x;
                int y1 = o1.getLocation().y;

                int x2 = o2.getLocation().x;
                int y2 = o2.getLocation().y;

                if (y1 < y2) {
                    return -1;
                }

                if (y1 > y2) {
                    return 1;
                }

                if (y1 == y2) {
                    if (x1 == x2) {
                        return 0;
                    }
                    if (x1 < x2) {
                        return -1;
                    }
                    if (x1 > x2) {
                        return 1;
                    }
                }
                throw new AssertionError();
            }
        });

        panel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("RIGHT"), "DO_RIGHT");
        panel.getActionMap().put("DO_RIGHT", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                controllerPicture.amplify("DO_RIGHT");
            }
        });

        panel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("LEFT"), "DO_LEFT");
        panel.getActionMap().put("DO_LEFT", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                controllerPicture.amplify("DO_LEFT");

            }
        });
        // </editor-fold>

        panel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("pressed F1"), "DO_F1");
        panel.getActionMap().put("DO_F1", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("F1...");
                if (ViewPicture.this.toogle) {
//                    controllerPicture.suspend();
                } else {
//                    controllerPicture.resume();
                }
                ViewPicture.this.toogle = !ViewPicture.this.toogle;
            }
        });

        // <editor-fold defaultstate="collapsed" desc="Select Thumbnail">
        panel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("released CONTROL"), "DO_CONTROL");
        panel.getActionMap().put("DO_CONTROL", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JLabel pic = (JLabel) FocusManager.getCurrentManager().getFocusOwner();
                int indexOf = carousel.indexOf(pic);
                if (pic.getIcon() != null) {
                    try {
                        System.out.println("indexOf " + indexOf);
                        controllerPicture.remove(indexOf);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ViewPicture.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                System.out.println("TODO Incluir llamado al controlador");
            }
        });

        panel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("released SPACE"), "DO_SPACE");
        panel.getActionMap().put("DO_SPACE", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ViewPicture.this.controllerPicture.streamToggle();
            }
        });

        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released F5"), "DO_F5");
        panel.getActionMap().put("DO_F5", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (JLabel carousel1 : carousel) {
                    System.out.println("carousel1 " + carousel1.isFocusable() + " " + carousel1.isFocusCycleRoot());
                }
                ViewPicture.this.controllerPicture.getPicture();
            }
        });

        // </editor-fold>
        for (Component thumbnail : carousel) {
            System.out.println("Position B " + thumbnail.getLocation());
        }

        Timer timer = new Timer(100, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                int c = 0;
                for (int i = 0; i < 5; i++) {
                    if (carousel.get(i).getIcon() != null) {
                        c++;
                    }
                }
                if (c == 5) {
                    try {
                        controllerPicture.remove(3);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ViewPicture.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
//        timer.start();
    }

    void showPictures(List<BufferedImage> pictures) {
        for (int i = carousel.size() - 1; i >= 0; i--) {
            if (i < pictures.size()) {
                carousel.get(i).setIcon(new ImageIcon(pictures.get(i)));
            } else {
                carousel.get(i).setIcon(null);
            }
        }
    }

    void setController(ControllerPicture controller) {
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

        System.out.println("Traverse " + indexOf);

        carousel.get(indexOf).grabFocus();

        return indexOf;
    }

    public static List<Component> getAllComponents(final Container c) {
        Component[] comps = c.getComponents();
        List<Component> compList = new ArrayList<>();
        for (Component comp : comps) {
            compList.add(comp);
            if (comp instanceof Container) {
                compList.addAll(getAllComponents((Container) comp));
            }
        }
        return compList;
    }

    JLabel empty() {

        //Acotar a 5
        for (int i = 0; i < 5; i++) {
            JLabel label = carousel.get(i);
            if (label.getIcon() == null && !listEngaged.contains(label)) {
                System.out.println("null " + (label.getIcon() == null) + " con " + !listEngaged.contains(label));
                System.out.println("Adding " + label.hashCode() + " - " + listEngaged.add(label));

                return label;
            }
        }
        return null;
    }

    void showPicture(JLabel label, BufferedImage image) {
        int indexOf = carousel.indexOf(label);
        if (picture.get(0) == label) {
            //System.out.println("Es la big " + image.hashCode() + "-" + EventQueue.isDispatchThread());
        }
        label.setIcon(new ImageIcon(image));

        Boolean flagi = null;
        if (EventQueue.getCurrentEvent() instanceof InvocationEvent) {
            InvocationEvent ie = (InvocationEvent) EventQueue.getCurrentEvent();
            flagi = ie.isDispatched();
        }

//        System.out.println("Después de... " + controllerPicture.getState() + " dd " + EventQueue.isDispatchThread() + " * " + flagi + " $ " + EventQueue.getCurrentEvent().hashCode());
//        boolean remove = listEngaged.remove(label);
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

    void disableSave() {
        buttonGET.setEnabled(false);
    }

    void enableSave() {
        buttonGET.setEnabled(true);
    }

    void requestFocusInWindow(int indexOf) {
        carousel.get(indexOf).requestFocusInWindow();
    }

    private class MyListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {
            JLabel source = (JLabel) e.getSource();
            source.setBorder(new LineBorder(Color.red));
        }

        @Override
        public void focusLost(FocusEvent e) {
            JLabel source = (JLabel) e.getSource();
            source.setBorder(null);
        }
    }
}
