/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.photo;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author elialva
 */
public class XeamModelPicture {

    private final ArrayBlockingQueue buffer;
    private final BlockingQueue picturesList;
    private final Producer producer;
    private final Consumer consumerScr;

    private final PropertyChangeSupport propertyChangeSupport;
    private ControllerPicture controller;

    public XeamModelPicture(Sender sender) {
        buffer = new ArrayBlockingQueue(20);
        picturesList = new ArrayBlockingQueue(20);
        //
        producer = new Producer(sender);
        consumerScr = new Consumer();
        //
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    void on() {
        new Thread(producer).start();
        Thread consumer = new Thread(consumerScr, "Consumer");
        consumer.start();
    }

    BufferedImage get(int indexOf) {
        return (BufferedImage) picturesList.toArray()[indexOf];
    }

    List getAll() {
        return Arrays.asList(picturesList.toArray());
    }

    Object remove() {
        try {
            return picturesList.take();
        } catch (InterruptedException ex) {
            Logger.getLogger(ModelPicture.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    void setController(ControllerPicture controller) {
        this.controller = controller;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    private class Producer implements Runnable {

        private final Sender sender;

        private Producer(Sender sender) {
            this.sender = sender;
        }

        @Override
        public void run() {
            while (true) {
                ByteArrayInputStream inputStream = (ByteArrayInputStream) produce();

                if (inputStream != null) {
                    try {
                        XeamModelPicture.this.buffer.put(inputStream);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(XeamModelPicture.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        private Object produce() {
            ByteArrayInputStream inputStream = sender.convey();
            if (inputStream == null) {
                return null;
            }
            return inputStream;
        }
    }

    private class Consumer implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    ByteArrayInputStream inputStream = (ByteArrayInputStream) XeamModelPicture.this.buffer.take();

                    BufferedImage bufferedImage = ImageIO.read(inputStream);

                    XeamModelPicture.this.picturesList.put(bufferedImage);

                    final BufferedImage tmpImg = bufferedImage;

//                    EventQueue.invokeLater(() -> {
                    XeamModelPicture.this.propertyChangeSupport.firePropertyChange("NEW_IMG", null, tmpImg);
//                    });

                } catch (InterruptedException | IOException ex) {
                    Logger.getLogger(ModelPicture.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
