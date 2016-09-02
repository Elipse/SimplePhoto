
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
    private boolean isStreaming;
    private ControllerPic controller;
    private int displaysIndex;
    private int requestsIndex;
    private int imagesIndex;
    private final int capacity;
    private final Dimension smallDimension;
    private final Dimension bigDimension;
    private ResponseData currentResponse;

    public ModelPic(Sender sender, int capacity, Dimension smallDimension, Dimension bigDimension) {
        this.sender = sender;

        this.capacity = capacity;
        this.smallDimension = smallDimension;
        this.bigDimension = bigDimension;

        buffer = new ArrayBlockingQueue<>(capacity * 2);
        tiles = new ArrayBlockingQueue<>(capacity * 8, true);

        MyListener myListener = new MyListener();

        producer = new Producer();
        producer.addPropertyChangeListener(myListener);
        consumer = new Consumer();
        consumer.addPropertyChangeListener(myListener);

        synch = new Object();

        isStreaming = false;
    }

    void on() {
        producer.execute();
        consumer.execute();
    }

    private void doInBackgroundProducer() {
        while (true) {
            System.out.println("Iterando en doInBackgroundProducer");
            ImageFile imageFile = sender.convey();

            if (imageFile != null && imageFile.getBais() != null) {
                ByteArrayInputStream inputStream = imageFile.getBais();
                try {
                    BufferedImage image = ImageIO.read(inputStream);
                    imageFile.setImage(image);
                    System.out.println("Putting en buffer " + imageFile.getFile());
                    buffer.put(imageFile);
                    producer.firePropertyChange("NEW_IMG", null, imageFile);
                } catch (IOException | InterruptedException ex) {
                    System.out.println("Epale!!! " + ex);
                    Logger.getLogger(ModelPic.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void doInBackgroundConsumer() {
        while (true) {
            RequestData request;
            try {
                System.out.println("Taking en tiles ");
                request = tiles.take();
                System.out.println("Taken en tiles " + request.getFile());
                if (request.getSynch() != synch) {
                    System.out.println("Se tira: " + request.getFile());
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

                //System.out.println("Solicite el despliegue");
                consumer.firePropertyChange("DIS_IMG", null, response);
            } catch (Exception ex) {
                System.out.println("Lo Tome Exception!!! " + ex);
                Logger.getLogger(ModelPic.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Escucha a los threads: Productor y Consumidor
     *
     * @param evt Este parámetro indica a quien está escuchando.
     */
    private void propertyChange(PropertyChangeEvent evt) {

        String name = evt.getPropertyName();
        switch (name) {
            //Escucha al Productor
            case "NEW_IMG":
                imagesIndex++;
                System.out.println("NEW_IMG " + imagesIndex + " iSStremain " + isStreaming);
                if (isStreaming) {
                    System.out.println("Solicito request");
                    createRequests();
                }
                break;
            //Escucha al Consumidor
            case "DIS_IMG":
                ResponseData response = (ResponseData) evt.getNewValue();
                if (response.getSynch() == synch) {
                    if (controller.showPicture(response)) {
                        currentResponse = response;
                        ++displaysIndex;
                    }
                } else {
                    System.out.println("Se tira al desplegar " + response.getFile());
                }
                break;
            default:
                System.out.println("evt " + evt);
        }
    }

    void streamOn() {
        synch = new Object();
        isStreaming = true;
        System.out.println("requestsIndex " + requestsIndex + " displaysIndex " + displaysIndex);
        requestsIndex = displaysIndex;
        createRequests();
    }

    void streamOff() {
        synch = new Object();
        isStreaming = false;
        System.out.println("QUEIN cancelo");
        //controller.clearBorders();
    }

    void remove(int i) {
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
        createRequests();
    }

    private void createRequests() {

        /* A partir de la última y hasta acabarse el modelo o llenar la pantalla */
        for (int i = requestsIndex; i < imagesIndex && i <= 5; i++) {
            try {
                System.out.println("createRequest " + i);
                RequestData request = new RequestData(RequestData.STREAMING);
                request.setDimensionBig(bigDimension);
                request.setDimensionSmall(smallDimension);
                ImageFile imageFile = (ImageFile) buffer.toArray()[i];
                request.setIndex(i);
                request.setImage(imageFile.getImage());
                request.setFile(imageFile.getFile());
                request.setSynch(synch);
                System.out.println("Putting en tiles " + request.getFile());
                tiles.put(request);
                System.out.println("Put en tiles " + request.getFile());
                requestsIndex++;
//                System.out.println("Producer " + toArray[i]);
            } catch (InterruptedException ex) {
                Logger.getLogger(ModelPic.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void createRequest(int indexOf) {
        if (indexOf < 0) {
            //Aqui solicita acceso a BD
            return;
        }
        streamOff();
        try {
//            System.out.println("createRequest only one");
            RequestData request = new RequestData(RequestData.AMPLIFING);
            request.setDimensionBig(bigDimension);
            request.setDimensionSmall(smallDimension);
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

    ResponseData getCurrenResponse() {
        return currentResponse;
    }

    void setController(ControllerPic controller) {
        this.controller = controller;
    }

    /**
     * @return the isStreaming
     */
    public boolean isIsStreaming() {
        return isStreaming;
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

        @Override
        protected void done() {
            System.out.println("Estatus " + this.isCancelled() + " " + this.isDone());
        }

    }
}
