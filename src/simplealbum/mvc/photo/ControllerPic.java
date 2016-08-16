/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.photo;

import java.awt.image.BufferedImage;
import javax.swing.JLabel;

/**
 *
 * @author elialva
 */
public class ControllerPic {

    private final ViewPic view;
    private final ModelPic model;
    private int currentIndex;
    private int currentPic;
    private ResponseData currentResponse;

    ControllerPic(ViewPic view, ModelPic model) {
        this.view = view;
        this.view.setController(ControllerPic.this);
        this.model = model;
        this.model.setController(ControllerPic.this);

        currentIndex = -1;
    }

    public void on() {
        model.on();
    }

    public ResponseData getPicture() {
        return currentResponse;
    }

    boolean showPicture(ResponseData response) {
        JLabel label = view.getLabel();
        boolean show = false;
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
                //model.increaseDisplays();
                show = true;
                currentResponse = response;
                currentIndex = response.getIndex();
                break;
            case RequestData.AMPLIFING:
                JLabel get = view.getLabel(response.getIndex());
                view.showPicture(get, small);
                view.showPicture(view.getPic(), big);
                System.out.println("ControlAmpl " + response.getFile());
                show = false;
                currentResponse = response;
                currentIndex = response.getIndex();
                break;
            default:
                throw new AssertionError();
        }
        return show;
    }

    void remove(int i) {
        streamOff();
        view.clearLabels();
        if (i >= 0) {
            model.remove(i);
        }
        streamOn();
    }

    void amplify(String action) {
        streamOff();
        switch (action) {
            case "RIGHT":
                currentPic = view.traverse(-1);
                System.out.println("RIGHT " + currentPic);
                model.createRequest(currentPic);
                break;
            case "LEFT":

                currentPic = view.traverse(+1);
                System.out.println("LEFT " + currentPic);
                model.createRequest(currentPic);
                break;
            default:
                throw new AssertionError();
        }
    }

    void streamOn() {
        model.streamOn();
    }

    void streamOff() {
        model.streamOff();
    }

    void clearBorders() {
        view.clearBorders();
    }

    void streamToggle() {
        System.out.println("Stream es " + model.isIsStreaming());

        view.clearBorders();

        if (model.isIsStreaming()) {
            System.out.println("Apgo");
            model.streamOff();
        } else {
            System.out.println("Enciendo");
            model.streamOn();
        }
    }

    void processSelection() {
        model.streamOff();

        view.clearBorders();

        System.out.println("currentPic " + currentPic);

        //dispara propiedad currentImage o guarda propiedad
        view.getAway();
    }
}
