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
public class XeamQTiles_2 implements Runnable, PropertyChangeListener {

    private volatile Thread blinker;
    private volatile Boolean threadSuspended;
    private final PropertyChangeSupport pcs;
    private final LinkedBlockingDeque queue;
    private volatile int count;
    private final LinkedBlockingDeque commQueue;

    public XeamQTiles_2(LinkedBlockingDeque queue, LinkedBlockingDeque commQueue, PropertyChangeListener l) {
        this.queue = queue;
        this.commQueue = commQueue;
        pcs = new PropertyChangeSupport(XeamQTiles_2.this);
        pcs.addPropertyChangeListener(l);
        blinker = new Thread(XeamQTiles_2.this);
        threadSuspended = false;
    }

    public XeamQTiles_2 start() {
        blinker.start();
        return XeamQTiles_2.this;
    }

    public void stop() {
        assert EventQueue.isDispatchThread();

        try {
            queue.putFirst("STOP");
            if (threadSuspended) {
                resume();
            }
            Object take = commQueue.take();
            System.out.println("stop Cc " + take);
        } catch (InterruptedException ex) {
            Logger.getLogger(XeamQTiles_2.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void suspend() {
        assert EventQueue.isDispatchThread();
        assert threadSuspended == false;

        try {
            queue.putFirst("SUSPEND");
            Object take = commQueue.take();
            System.out.println("suspend Aa " + take);
        } catch (InterruptedException ex) {
            Logger.getLogger(XeamQTiles_2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void resume() {
        assert EventQueue.isDispatchThread();
        assert threadSuspended == true;

        try {
            synchronized (this) {
                notifyAll();
            }
            Object take = commQueue.take();
            System.out.println("resume Aa " + take);
        } catch (InterruptedException ex) {
            Logger.getLogger(XeamQTiles_2.class.getName()).log(Level.SEVERE, null, ex);
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
                            commQueue.putFirst("SUSPENDED");
                            EventQueue.invokeLater(() -> {
                                pcs.firePropertyChange("SUSPEN_", null, "SUSPEN");
                            });
                            wait();
                        }
                        threadSuspended = false;
                        commQueue.putFirst("ACTIVATED");
                    }
                }

                Object take = queue.take();

                if (take instanceof String) {
                    switch (take.toString()) {
                        case "SUSPEND":
                            isSuspended = true;
                            continue;
                        case "STOP":
                            blinker = null;
                            continue;
                        default:
                            throw new AssertionError();
                    }
                }

                BufferedImage img = (BufferedImage) take;

                BufferedImage small = Scalr.resize(img, Scalr.Method.AUTOMATIC, 64);
                BufferedImage big = Scalr.resize(img, Scalr.Method.AUTOMATIC, 64 * 4);

                EventQueue.invokeLater(() -> {
                    pcs.firePropertyChange("BIG_", null, big);
                    pcs.firePropertyChange("SMALL_", null, small);
                });
            } catch (InterruptedException ex) {
                Logger.getLogger(XeamQTiles_2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            commQueue.putFirst("STOPPED");
        } catch (InterruptedException ex) {
            Logger.getLogger(XeamQTiles_2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        assert "Consumer".equals(Thread.currentThread().getName());

        switch (evt.getPropertyName()) {
            case "NEW_IMG": {
                try {
                    if (count < 5) {
                        queue.put(evt.getNewValue());
                        count++;
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(XeamQTiles_2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            break;
            default:
                throw new AssertionError();
        }
    }

    public void clear() {
        queue.clear();
    }

    public void post(List model) {
        int c = queue.remainingCapacity();
        for (int i = 0; i < c; i++) {
            if (i < model.size() && i < 5) {
                try {
                    queue.put(model.get(i));
                } catch (InterruptedException ex) {
                    Logger.getLogger(XeamQTiles_2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
