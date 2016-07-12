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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.imgscalr.Scalr;

/**
 *
 * @author elialva
 */
public class QTiles implements Runnable {

    private volatile Thread blinker;
    private volatile Boolean threadSuspended;
    private volatile Boolean threadStopped;
    private volatile int theLastOne;
    private final ModelPhoto model;
    private final PropertyChangeSupport pcs;
    private int globalCount;
    private volatile Object synch;
    private volatile boolean isSuspended;

    public QTiles(ModelPhoto model, PropertyChangeListener listener) {
        this.model = model;
        pcs = new PropertyChangeSupport(QTiles.this);
        pcs.addPropertyChangeListener(listener);

        blinker = new Thread(QTiles.this);
        threadSuspended = false;
        isSuspended = false;
        threadStopped = false;

        theLastOne = -1;
    }

    public QTiles start(Object synch) {
        this.synch = synch;
        blinker.start();
        return QTiles.this;
    }

    public void stop() {
        assert EventQueue.isDispatchThread();

        if (threadStopped) {
            return;
        }

        threadStopped = null;
        blinker = null;
        resume();
        while (threadStopped == null) {
            /* join: se detiene hasta que se detiene */
        }
    }

    public void suspend(Object synch, int lastOne) {
        assert EventQueue.isDispatchThread();

        System.out.println("Duerme al despertar recuerda que último fue: " + lastOne);

        if (threadSuspended) {
            this.synch = synch; //porque cambia en Controller
            theLastOne = lastOne - 1;
            return;
        }

        threadSuspended = null;
        isSuspended = true;
        while (threadSuspended == null) {
            /* join: se detiene hasta que se cancela */
        }

        this.synch = synch;
        theLastOne = lastOne - 1;
    }

    public void resume() {
        assert EventQueue.isDispatchThread();

        if (!threadSuspended) {
            return;
        }

        threadSuspended = null;

        synchronized (this) {
            notifyAll();
        }

        while (threadSuspended == null) {
            /* join: se detiene hasta que se reinicia */
        }
    }

    @Override
    public void run() {
        assert !EventQueue.isDispatchThread();

        Thread thisThread = Thread.currentThread();

        while (blinker == thisThread) {
            try {
                if (isSuspended) {
                    synchronized (this) {
                        while (blinker == thisThread && isSuspended) {
                            isSuspended = false;
                            threadSuspended = true;
                            EventQueue.invokeLater(() -> pcs.firePropertyChange("SUSPENDED", null, null));
                            wait();
                        }
                        threadSuspended = false;
                        EventQueue.invokeLater(() -> pcs.firePropertyChange("ACTIVATED", null, null));
                    }
                }

                //Itera sobre el modelo, solo escala las que no ha escalado
                Object[] images = model.getAll().toArray();

                if (theLastOne + 1 < images.length) {

                    //System.out.println("X Mandar: " + (theLastOne + 1));
                    BufferedImage img = (BufferedImage) images[theLastOne + 1];

                    BufferedImage big = Scalr.resize(img, Scalr.Method.AUTOMATIC, 64 * 4);
                    BufferedImage small = Scalr.resize(img, Scalr.Method.AUTOMATIC, 64);

                    QBallon b1 = new QBallon(img, big, synch);
                    QBallon b2 = new QBallon(img, small, synch);

                    //System.out.println("Qtiles init");
                    EventQueue.invokeLater(() -> {
                        //System.out.println("BIGGER " + theLastOne);
                        pcs.firePropertyChange("BIG_", null, b1);
                        //System.out.println("SMALLER " + theLastOne);
                        pcs.firePropertyChange("SMALL_", null, b2);
                    });
                    //System.out.println("Qtiles end");

                    theLastOne++;
                    globalCount++;
                } else {
//                    System.out.println("SOLICITA SUSPNECIONS EN REASQUIS " + globalCount);
                    EventQueue.invokeLater(() -> {
                        pcs.firePropertyChange("NO_PICS", null, globalCount);
                    });
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(QTiles.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        threadStopped = true;
    }
}
