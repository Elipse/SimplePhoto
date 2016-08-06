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
    private int currentPic;

    ControllerPic(ViewPic view, ModelPic model) {
        this.view = view;
        this.view.setController(ControllerPic.this);
        this.model = model;
        this.model.setController(ControllerPic.this);
        //

        currentIndex = -1;

        stream = true;
//        propertyChangeListener = new ControllerPhoto.MyListener();
//
//        ControllerPhoto.MyListener myListener = new ControllerPhoto.MyListener();
//        model.addPropertyChangeListener(myListener);
    }

    public void on() {
        model.on();
    }

    void showPicture(ResponseData response) {
        JLabel label = view.getFreeLabel();
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
                JLabel get = view.getLabels().get(response.getIndex());
                view.showPicture(get, small);
                view.showPicture(view.getPic(), big);
                System.out.println("ControlAmpl " + response.getFile());
                currentBais = response.getBais();
                currentIndex = response.getIndex();
                break;
            default:
                throw new AssertionError();
        }
    }

    void remove(int i) {
        view.clearLabels();
        if (i >= 0) {
            model.remove(i);
        }
    }

    void amplify(String action) {

        model.streamOff();

        switch (action) {
            case "RIGHT":
                currentPic = view.traverse(-1);
                model.createRequest(currentPic);
                break;
            case "LEFT":
                currentPic = view.traverse(+1);
                model.createRequest(currentPic);
                break;
            default:
                throw new AssertionError();
        }
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

        System.out.println("currentPic " + currentPic + " displays " + model.getDisplays());

        //dispara propiedad currentImage o guarda propiedad
        view.getAway();
    }
}
