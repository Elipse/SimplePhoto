
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.photo;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.imgscalr.Scalr;
import simplealbum.mvc.picture.impl.SenderFile;

/**
 *
 * @author elialva
 */
public class ModelPic {

    private final ArrayBlockingQueue<ImageFile> buffer;
    private final ArrayBlockingQueue<RequestData> tiles;
    private final Producer producer;
    private final Consumer consumer;
    private final Sender sender;
    private Object synch;
    private boolean streaming;
    private ControllerPic controller;
    private int index;
    private int displaysIndex;
    private final int maxIndex;
    private int requestsIndex;
    private int imagesIndex;

    public ModelPic(Sender sender) {
        this.sender = sender;
        buffer = new ArrayBlockingQueue<>(20, true);
        tiles = new ArrayBlockingQueue<>(40);

        MyListener myListener = new MyListener();

        producer = new Producer();
        producer.addPropertyChangeListener(myListener);
        consumer = new Consumer();
        consumer.addPropertyChangeListener(myListener);

        synch = new Object();

        maxIndex = 5;
        streaming = false;
    }

    public void on() {
        producer.execute();
        consumer.execute();
    }

    private void doInBackgroundProducer() {
        while (true) {
//            System.out.println("Producer...en while...");
            ImageFile imageFile = sender.convey();
            if (imageFile != null && imageFile.getBais() != null) {
                ByteArrayInputStream inputStream = imageFile.getBais();
                try {
                    BufferedImage image = ImageIO.read(inputStream);
                    imageFile.setImage(image);
                    buffer.put(imageFile);
                    producer.firePropertyChange("NEW_IMG", null, imageFile);
                } catch (IOException | InterruptedException ex) {
                    Logger.getLogger(ModelPic.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void doInBackgroundConsumer() {
        while (true) {
            RequestData request;
            try {
                request = tiles.take();
                if (request.getSynch() != synch) {
                    System.out.println("Se tira: " + request.getType());
                    continue;
                }

                BufferedImage big, small = null;
                BufferedImage image = request.getImage();

                Dimension dimensionBig = request.getDimensionBig();
                big = Scalr.resize(image, Scalr.Method.AUTOMATIC, dimensionBig.height);

                Dimension dimensionSmall = request.getDimensionSmall();
                if (dimensionSmall != null) {
                    small = Scalr.resize(image, Scalr.Method.AUTOMATIC, dimensionSmall.height);
                }

                ResponseData response = new ResponseData(request.getIndex(), small, big, request.getSynch(), request.getType(), request.getFile(), request.getBais());

                consumer.firePropertyChange("DIS_IMG", null, response);
            } catch (InterruptedException ex) {
                Logger.getLogger(ModelPic.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void propertyChange(PropertyChangeEvent evt) {

        String name = evt.getPropertyName();
        switch (name) {
            case "NEW_IMG":
                imagesIndex++;
                if (streaming) {
                    createRequests();
                }
                break;
            case "DIS_IMG":
                ResponseData response = (ResponseData) evt.getNewValue();
                if (response.getSynch() == synch) {
                    controller.showPicture(response);
                }
                break;
            default:
                System.out.println("evt " + evt);
        }
    }

    public int increaseDisplays() {
        return ++displaysIndex;
    }

    public void streamOn() {
        System.out.println("requestsIndex " + requestsIndex + " displaysIndex " + displaysIndex);
        requestsIndex = displaysIndex;
        createRequests();
        streaming = true;
    }

    public void streamOff() {
        streaming = false;
        synch = new Object();
    }

    void setController(ControllerPic controller) {
        this.controller = controller;
    }

    public void createRequest(int indexOf) {
        if (indexOf < 0) {
            //Aqui solicita acceso a BD
            return;
        }
        synch = new Object();
        try {
            System.out.println("createRequest only one");
            RequestData request = new RequestData(RequestData.AMPLIFING);
            request.setDimensionBig(new Dimension(64 * 4, 64 * 4));
            request.setDimensionSmall(new Dimension(64, 64));
            ImageFile imageFile = (ImageFile) buffer.toArray()[indexOf];
            request.setIndex(indexOf);
            request.setBais(imageFile.getBais());
            request.setImage(imageFile.getImage());
            request.setFile(imageFile.getFile());
            request.setSynch(synch);
            tiles.put(request);
        } catch (InterruptedException ex) {
            Logger.getLogger(ModelPic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createRequests() {

        /* A partir de la última y hasta acabarse el modelo o llenar la pantalla */
//        for (int i = displaysIndex; i < toArray.length && i < maxIndex; i++) {
        for (int i = requestsIndex; i < imagesIndex && i <= 5; i++) {
//        for (int i = requestsIndex; i < imagesIndex; i++) {
            try {
                System.out.println("createRequest " + i);
                RequestData request = new RequestData(RequestData.STREAMING);
                request.setDimensionBig(new Dimension(64 * 4, 64 * 4));
                request.setDimensionSmall(new Dimension(64, 64));
                ImageFile imageFile = (ImageFile) buffer.toArray()[i];
                request.setIndex(i);
                request.setImage(imageFile.getImage());
                request.setFile(imageFile.getFile());
                request.setSynch(synch);
                tiles.put(request);
                requestsIndex++;
//                System.out.println("Producer " + toArray[i]);
            } catch (InterruptedException ex) {
                Logger.getLogger(ModelPic.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void remove(int i) {

        //TODO se queda trabado si se le piden 4 debe ser dinámico
        System.out.println("remover ini");
        for (int j = 0; j < i; j++) {
            try {
                buffer.take();
                imagesIndex--;
            } catch (InterruptedException ex) {
                Logger.getLogger(ModelPic.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        requestsIndex = 0;
        displaysIndex = 0;
        synch = new Object();
        createRequests();
        System.out.println("remover fin");
    }

    class MyListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            ModelPic.this.propertyChange(evt);
        }
    }

    class Consumer extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            ModelPic.this.doInBackgroundConsumer();
            return null;
        }
    }

    class Producer extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            ModelPic.this.doInBackgroundProducer();
            return null;
        }
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            ModelPic m = new ModelPic(new SenderFile());
            m.on();
            JOptionPane.showInputDialog("Prueba");
        });

    }
}
