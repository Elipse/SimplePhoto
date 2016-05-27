/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.photo;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.Thread.State;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elialva
 */
public abstract class RunThread implements Runnable {

    private volatile Thread blinker;
    private volatile boolean isSuspended;
    protected volatile PropertyChangeSupport propertyChangeSupport;
    private volatile Object synch;
    protected volatile Object watchword;
    private volatile long time;

    public Object getSynch() { /* Se ejecuta en este Thread */

        return synch;
    }

    public Object getWatchword() { /* Make an meth to compare synch & watchword */

        return watchword;
    }

    public boolean isSynch() {
        //suspende si es AWT quien pregunta ?
        return synch == watchword;
    }

    public RunThread(long time) {
        blinker = new Thread(RunThread.this);
        propertyChangeSupport = new PropertyChangeSupport(this);
        this.time = time;
    }

    public RunThread() {
        blinker = new Thread(RunThread.this);
        propertyChangeSupport = new PropertyChangeSupport(this);
        time = 300;
    }

    public void start() throws InterruptedException {
        blinker.start();
    }

    public void stop() {
        blinker = null;
        resume(null);
    }

    public void suspend() {
        isSuspended = true;
    }

    public void resume(Object synch) {
        this.synch = synch;
        if (!blinker.isAlive()) {
            System.out.println("Startorrrrrrrrrrrrrrrrrr " + blinker);
            blinker.start();
            return;
        }
        isSuspended = false;

        synchronized (RunThread.this) {
            notifyAll();
        }
    }

    public boolean isSuspended() {
        return isSuspended;
    }

    public State getState() {
        return blinker.getState();
    }

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void run() {
        Thread thisThread = Thread.currentThread();
        while (blinker == thisThread) {
            try {
//                System.out.println("Thread waiting..." + time);
                Thread.sleep(time);
                watchword = synch;
                doInBackgroud();
                System.out.println("despueds del terudr");

                if (isSuspended) {
                    synchronized (RunThread.this) {
                        while (blinker == thisThread && isSuspended) {
                            //Aviso a la GUI de suspension
                            wait();
                        }
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(RunThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //Aviso a la GUI de termino
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public abstract void doInBackgroud();

}
