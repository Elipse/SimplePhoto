/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.photo;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author elialva
 */
public class ModelPhoto {

    private final ArrayBlockingQueue buffer;
    private final Producer producer;

    private final PropertyChangeSupport pcs;
    private ControllerPhoto controller;

    public ModelPhoto(Sender sender) {
        buffer = new ArrayBlockingQueue(20);
        producer = new Producer(sender);
        pcs = new PropertyChangeSupport(this);
    }

    void on() {
        new Thread(producer).start();
    }

    BufferedImage get(int indexOf) {
        return (BufferedImage) buffer.toArray()[indexOf];
    }

    List getAll() {
        return Arrays.asList(buffer.toArray());
    }

    Object remove() {
        try {
            return buffer.take();
        } catch (InterruptedException ex) {
            Logger.getLogger(ModelPhoto.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    void setController(ControllerPhoto controller) {
        this.controller = controller;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    private class Producer implements Runnable {

        private final Sender sender;
        private int c;

        private Producer(Sender sender) {
            this.sender = sender;
        }

        @Override
        public void run() {
            while (true) {
                ByteArrayInputStream inputStream = (ByteArrayInputStream) produce();

                if (inputStream != null) {
                    try {
                        ModelPhoto.this.buffer.put(ImageIO.read(inputStream));
                        c++;
                        EventQueue.invokeLater(() -> {
                            ModelPhoto.this.pcs.firePropertyChange("NEW_IMG", null, c);
                        });
                    } catch (InterruptedException | IOException ex) {
                        Logger.getLogger(ModelPhoto.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        private Object produce() {
            ByteArrayInputStream inputStream = sender.convey().getBais();
            if (inputStream == null) {
                return null;
            }
            return inputStream;
        }
    }
}
