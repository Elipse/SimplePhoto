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
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.imgscalr.Scalr;

/**
 *
 * @author elialva
 */
public class Quick implements Runnable {

    private volatile Thread blinker;
    private volatile boolean threadSuspended;
    private volatile Object synch;
    protected volatile Object mySynch;
    private volatile Object request;
    private volatile Object response;
    private volatile long time;
    private final PropertyChangeSupport pcs;
    private final String property;
    private volatile int indexOf;

    public Quick(PropertyChangeListener l, String property) {
        pcs = new PropertyChangeSupport(Quick.this);
        pcs.addPropertyChangeListener(l);
        this.property = property;
        blinker = new Thread(Quick.this);
        time = 150;
    }

    public Quick start() {
        blinker.start();
        return Quick.this;
    }

    public void stop() {
        assert EventQueue.isDispatchThread();

        blinker = null;
        synchronized (Quick.this) {
            notifyAll();
        }
    }

    public void suspend() {
        assert EventQueue.isDispatchThread();

        threadSuspended = true;
    }

    public void request(Object synch) {
        request(synch, synch);
    }

    private void request(Object synch, Object request) {
        assert EventQueue.isDispatchThread();

        this.synch = synch;
        this.request = request;

        if (threadSuspended) {
            threadSuspended = false;
            synchronized (Quick.this) {
                notifyAll();
            }
        }
    }

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void run() {
        assert !EventQueue.isDispatchThread();

        Thread thisThread = Thread.currentThread();
        threadSuspended = true;

        while (blinker == thisThread) {
            try {
                if (threadSuspended) {
                    synchronized (Quick.this) {
                        while (blinker == thisThread && threadSuspended) {
                            wait();
                        }
                    }
                }

//                Thread.sleep(time);
                mySynch = synch;
                response = doInBackgroud(request);

                EventQueue.invokeAndWait(() -> {
                    assert EventQueue.isDispatchThread();

                    if (synch != mySynch) {
                        System.out.println("No, gracias");
                        return;
                    }
                    suspend();
                    pcs.firePropertyChange(property, request, response);
//                    pcs.fireIndexedPropertyChange(property, indexOf, request, response);
                });
            } catch (InterruptedException | InvocationTargetException ex) {
                Logger.getLogger(Quick.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected Object doInBackgroud(Object request) {
        if (request == null) {
            return null;
        }
        BufferedImage image = (BufferedImage) request;
        BufferedImage img = Scalr.resize(image, Scalr.Method.AUTOMATIC, 64 * 4);
        return img;
    }
}
