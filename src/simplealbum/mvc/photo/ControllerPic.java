/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.photo;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

/**
 *
 * @author elialva
 */
public class ControllerPic {

    private final ViewPic view;
    private final ModelPic model;
    private int currentIndex;
    private boolean stream;
    private ByteArrayInputStream currentBais;

    ControllerPic(ViewPic view, ModelPic model) {
        this.view = view;
        this.view.setController(ControllerPic.this);
        this.model = model;
        this.model.setController(ControllerPic.this);
        //

        currentIndex = -1;
//        propertyChangeListener = new ControllerPhoto.MyListener();
//
//        ControllerPhoto.MyListener myListener = new ControllerPhoto.MyListener();
//        model.addPropertyChangeListener(myListener);
    }

    public void on() {
        model.on();
    }

    void showPicture(ResponseData response) {
        JLabel label = getFreeLabel();
        BufferedImage small = response.getImageSmall();
        BufferedImage big = response.getImageBig();

        String action = response.getType();
        switch (action) {
            case RequestData.STREAMING:
                if (label == null) {
                    System.out.println("Breaking??");
                    break;
                }
                System.out.println("ControlNew " + response.getFile());
                view.showPicture(label, small);
                view.showPicture(view.getPic(), big);
//                label.requestFocusInWindow();
                model.increaseDisplays();
                currentBais = response.getBais();
                currentIndex = response.getIndex();
                break;
            case RequestData.AMPLIFING:
                view.showPicture(view.getPic(), big);
                System.out.println("ControlAmpl " + response.getFile());
                currentBais = response.getBais();
                currentIndex = response.getIndex();
                break;
            default:
                throw new AssertionError();
        }
    }

    JLabel getFreeLabel() {
        List<JLabel> labels = view.getLabels();
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

    void focusGained(FocusEvent e) {

        JComponent source = (JComponent) e.getSource();
        if (source.getName().equals("_PICPANEL")) {
            streamOn();
            if (currentIndex >= 0) {
                JLabel label = view.getLabel(currentIndex);
                label.setBorder(new LineBorder(Color.blue));
                label.grabFocus();
                System.out.println("Tengo q pintar la " + currentIndex);
            } else {
                view.getSmallPic().grabFocus();
            }
            System.out.println("Pic panel rules!");
        } else {
            source.setBorder(new LineBorder(Color.blue));
        }
    }

    void focusLost(FocusEvent e) {
        JComponent source = (JComponent) e.getSource();
        source.setBorder(null);
    }

    private void streamOn() {
        model.streamOn();
    }

    void remove() {
        view.clearLabels();
        int i = view.indexOfFocusOwner();
        if (i >= 0) {
            model.remove(i);
        }
    }

    void amplify(String action) {

        model.streamOff();
//        if (stream) {
//            streamOff();
//            amplify(view.traverse(0));
//            return;
//        }
        switch (action) {
            case "RIGHT":
//                amplify(view.traverse(-1));
                model.createRequest(view.traverse(-1));
                break;
            case "LEFT":
//                amplify(view.traverse(+1));
                model.createRequest(view.traverse(+1));
                break;
            default:
                throw new AssertionError();
        }

//        model.streamOn();
    }

    void streamToggle() {
        System.out.println("Stream es " + stream);
        stream = !stream;

        if (stream) {
            System.out.println("Apgo");
            model.streamOff();
        } else {
            System.out.println("Enciendo");
            model.streamOn();
        }
    }

    void processSelection() {
        model.streamOff();
        //dispara propiedad currentImage o guarda propiedad
        view.getAway();
    }
}
