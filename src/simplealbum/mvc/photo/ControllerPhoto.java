/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.photo;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

/**
 *
 * @author elialva
 */
public class ControllerPhoto {

    private final ViewPhoto view;
    private final ModelPhoto model;
    private final Quick amplifier;
    private final QTiles tiles;
    private BufferedImage currentImg;
    private Integer lastOne;
    private volatile Object synch;
    private int global;
    private boolean stream;

    private final PropertyChangeSupport pcs;

    public ControllerPhoto(ViewPhoto view, ModelPhoto model) {
        this.view = view;
        this.view.setController(ControllerPhoto.this);
        this.model = model;
        this.model.setController(ControllerPhoto.this);
        //
        MyListener myListener = new MyListener();
        model.addPropertyChangeListener(myListener);
        //
        tiles = new QTiles(model, myListener);
        //
        amplifier = new Quick(myListener, "AMP_IMG");
        //
        lastOne = 0;
        stream = true;
        //
        pcs = new PropertyChangeSupport(this);
    }

    public void on() {
        model.on();
        synch = new Object();
        tiles.start(synch);
        amplifier.start();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    private void propertyChange(PropertyChangeEvent e) throws InterruptedException {

        QBallon b = null;
        if (e.getNewValue() instanceof QBallon) {
            b = (QBallon) e.getNewValue();
        }

        switch (e.getPropertyName()) {
            case "BIG_":
                //System.out.println("BIG b.getSynch() " + b.getSynch() + "-" + synch + "#" + System.currentTimeMillis());
                if (b.getSynch() != synch || getFreeLabel() == null) {
                    System.out.println("Bigger no se exhibe");
                    break;
                }
                showPicture(view.getPic(), b.getScaled());
                currentImg = (BufferedImage) e.getOldValue();
                System.out.println("LA ACUTAL " + currentImg);
                break;
            case "SMALL_":
                if (b.getSynch() != synch) {
                    //System.out.println("Basurela " + lastOne + "#" + System.currentTimeMillis());
                    break;
                }
                //System.out.println("SMALL b.getSynch() " + b.getSynch() + "-" + synch + "#" + e.getNewValue());
                JLabel label = getFreeLabel();
                if (label != null) {
                    QBallon b2 = (QBallon) e.getNewValue();
                    showPicture(label, b2.getScaled());
                    //System.out.println("En controller el Focus");
                    label.requestFocusInWindow();
                    lastOne++;
                    global++;
                    //System.out.println("GLOBAL " + global + " " + " OFFSET " + lastOne);
                    assert (lastOne > 0 && lastOne <= 5);
                } else {
                    //System.out.println("VIA SMALL");
                    streamOff();
                }
                break;
            case "SUSPENDED":
//                match();
                break;
            case "ACTIVATED":
                break;
            case "STOPPED":
                break;
            case "AMP_IMG":
                currentImg = (BufferedImage) e.getOldValue();
                showPicture(view.getPic(), (BufferedImage) e.getNewValue());
                break;
            case "NO_PICS":
//                streamOff();
                break;
            case "NEW_IMG":
                break;
            case "DEL_IMG":
                break;
            default:
//                throw new AssertionError();
        }
    }

    void showPicture(JLabel label, BufferedImage image) {
        if (label != null) {
            view.showPicture(label, image);
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

    void remove(int indexOf) throws InterruptedException {
        System.out.println("VIA REMOVE " + indexOf);

        view.clearLabels();

        lastOne = 0;
        streamOff();
        for (int i = 0; i < indexOf; i++) {
            model.remove();
        }
        streamOn();

//        lastOne = 0;
//        synch = new Object();
//        tiles.remove(indexOf, synch);
//        lastOne = 0;
//        streamOff();
//        tiles.remove(indexOf);
//        streamOn();
        int j = 5 - indexOf;
        global = global - j;

        System.gc();
    }

    void amplify(String action) {
        System.out.println("FRANCE");
        if (stream) {
            streamOff();
            amplify(view.traverse(0));
            return;
        }
        switch (action) {
            case "DO_RIGHT":
                amplify(view.traverse(+1));
                break;
            case "DO_LEFT":
                amplify(view.traverse(-1));
                break;
            default:
                throw new AssertionError();
        }
    }

    void amplify(int indexOf) {
        amplifier.request(model.get(indexOf));
    }

    void streamToggle() {
        if (stream) {
            streamOff();
        } else {
            streamOn();
        }
    }

    void streamOn() {
        System.out.println("STEAM ON");
        stream = true;
        tiles.resume();
    }

    void streamOff() {
        //System.out.println("srrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrOFFFF " + System.currentTimeMillis());
        stream = false;
        synch = new Object();
        tiles.suspend(synch, lastOne);
    }

    public BufferedImage getPicture() {
        System.out.println("GETOOOP%%%%%%%%%%%%%%%%%%%%");
        streamOff();
        return currentImg; //Podemos pedir el Picture xq los que lleguen después serán ignorados        
    }

    void stop() {
        tiles.stop();
    }

    void focusGained(FocusEvent e) {
        JLabel source = (JLabel) e.getSource();
        source.setBorder(new LineBorder(Color.blue));
    }

    void processSelection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private class MyListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            try {
                ControllerPhoto.this.propertyChange(e);
            } catch (InterruptedException ex) {
                Logger.getLogger(ControllerPhoto.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
