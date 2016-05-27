/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.photo;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.imgscalr.Scalr;

/**
 *
 * @author elialva
 */
public class XeamQTiles_1 implements Runnable, PropertyChangeListener {

    private volatile Thread blinker;
    private volatile Boolean threadSuspended;
    private final PropertyChangeSupport pcs;
    private final LinkedBlockingDeque queue;
    private volatile int capacity;
    private volatile Boolean threadStopped;
    private int count;

    public XeamQTiles_1(LinkedBlockingDeque queue, PropertyChangeListener l) {
        this.queue = queue;
        this.capacity = queue.remainingCapacity();
        pcs = new PropertyChangeSupport(XeamQTiles_1.this);
        pcs.addPropertyChangeListener(l);
        blinker = new Thread(XeamQTiles_1.this);
        threadSuspended = false;
        threadStopped = false;
    }

    public XeamQTiles_1 start() {
        blinker.start();
        return XeamQTiles_1.this;
    }

    public void stop() {
        assert EventQueue.isDispatchThread();

        if (threadStopped == null || threadSuspended == null) {
            return;
        }

        if (!threadStopped) {
            threadStopped = null;
            try {
                queue.putFirst("STOP");
                if (threadSuspended) {
                    resume();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(QTiles.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void suspend() {
        assert EventQueue.isDispatchThread();

        if (threadSuspended == null) {
            return;
        }

        if (!threadSuspended) {
            threadSuspended = null;
            try {
                queue.putFirst("SUSPEND");
            } catch (InterruptedException ex) {
                Logger.getLogger(QTiles.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void resume() {
        assert EventQueue.isDispatchThread();

        if (threadSuspended == null) {
            return;
        }

        if (threadSuspended) {
            threadSuspended = null;
            synchronized (this) {
                notifyAll();
            }
        }
    }

    @Override
    public void run() {
        assert !EventQueue.isDispatchThread();

        Thread thisThread = Thread.currentThread();
        boolean isSuspended = false;

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

                Object take = queue.take();

                if (take instanceof String) {
                    String value = (String) take;
                    switch (value) {
                        case "STOP":
                            blinker = null;
                            continue;
                        case "SUSPEND":
                            isSuspended = true;
                            continue;
                        default:
                    }
                }

                BufferedImage img = (BufferedImage) take;

                BufferedImage small = Scalr.resize(img, Scalr.Method.AUTOMATIC, 64);
                BufferedImage big = Scalr.resize(img, Scalr.Method.AUTOMATIC, 64 * 4);

                EventQueue.invokeLater(() -> {
                    pcs.firePropertyChange("BIG_", img, big);
                    pcs.firePropertyChange("SMALL_", img, small);
                });
            } catch (InterruptedException ex) {
                Logger.getLogger(QTiles.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        threadStopped = true;
        EventQueue.invokeLater(() -> pcs.firePropertyChange("STOPPED", null, null));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //assert -> Solo entra Consumer y nadie más

        switch (evt.getPropertyName()) {
            case "NEW_IMG": {
                try {
                    if (count < capacity) {
                        queue.put(evt.getNewValue());
                        count++;
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(QTiles.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println("Escuchando Stream " + Thread.currentThread());
            break;
            default:
                throw new AssertionError();
        }
    }

    void clear(List model) {
        queue.clear();
        resume();
        int c = queue.remainingCapacity();
        for (int i = 0; i < c; i++) {
            if (i < model.size()) {
                try {
                    queue.put(model.get(i));
                } catch (InterruptedException ex) {
                    Logger.getLogger(QTiles.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
