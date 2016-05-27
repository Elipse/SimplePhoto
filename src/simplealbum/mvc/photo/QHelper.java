/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.photo;

import java.awt.EventQueue;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elialva
 */
public abstract class QHelper implements Runnable {

    private volatile Thread blinker;
    private volatile boolean isSuspended;
    private volatile Object response;
    private final PropertyChangeSupport pcs;
    private final String property;
    private final LinkedBlockingQueue queue;

    public QHelper(LinkedBlockingQueue queue, PropertyChangeListener l, String property) {
        this.queue = queue;
        pcs = new PropertyChangeSupport(QHelper.this);
        pcs.addPropertyChangeListener(l);
        this.property = property;
        blinker = new Thread(QHelper.this);
    }

    public QHelper start() {
        blinker.start();
        return QHelper.this;
    }

    public void stop() {
        assert EventQueue.isDispatchThread();

        blinker = null;
        synchronized (QHelper.this) {
            notifyAll();
        }
    }

    public void suspend() {
        assert EventQueue.isDispatchThread();

        isSuspended = true;
    }

    public boolean isSuspended() {
        return isSuspended;
    }

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void run() {
        assert !EventQueue.isDispatchThread();

        Thread thisThread = Thread.currentThread();

        while (blinker == thisThread) {
            try {
                if (isSuspended) {
                    synchronized (QHelper.this) {
                        while (blinker == thisThread && isSuspended) {
                            //Aviso a la GUI de suspension
                            wait();
                        }
                    }
                }

                response = doInBackgroud(queue.take());

                EventQueue.invokeAndWait(() -> {
                    assert EventQueue.isDispatchThread();
                    pcs.firePropertyChange(property, null, response);
                });
            } catch (InterruptedException | InvocationTargetException ex) {
                Logger.getLogger(QHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //Aviso a la GUI de termino
    }

    protected abstract Object doInBackgroud(Object request);
}
