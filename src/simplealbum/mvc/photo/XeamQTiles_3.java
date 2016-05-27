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
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.imgscalr.Scalr;

/**
 *
 * @author elialva
 */
public class XeamQTiles_3 implements Runnable {

    private volatile Thread blinker;
    private volatile Boolean threadSuspended;
    private final LinkedBlockingDeque transfer;
    private volatile Boolean threadStopped;
    private volatile int theLastOne;
    private final ModelPicture model;
    private final PropertyChangeSupport pcs;
    private int globalCount;
    private volatile Object synch;

    public XeamQTiles_3(ModelPicture model, PropertyChangeListener listener) {
        this.transfer = new LinkedBlockingDeque(5);
        this.model = model;
        pcs = new PropertyChangeSupport(XeamQTiles_3.this);
        pcs.addPropertyChangeListener(listener);

        blinker = new Thread(XeamQTiles_3.this);
        threadSuspended = false;
        threadStopped = false;

        theLastOne = -1;
    }

    public XeamQTiles_3 start(Object synch) {
        this.synch = synch;
        blinker.start();
        return XeamQTiles_3.this;
    }

    public void stop() {
        assert EventQueue.isDispatchThread();

        if (threadStopped == null || threadSuspended == null) {
            return;
        }

        if (!threadStopped) {
            threadStopped = null;
            try {
                transfer.putFirst("STOP");
                if (threadSuspended) {
                    resume();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(XeamQTiles_3.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void suspend(Object synch, int i) {
        assert EventQueue.isDispatchThread();

        System.out.println("Duerme al despertar recuerda que último fue: " + i);

        if (threadSuspended) {
            this.synch = synch; //porque cambia desde controller
            theLastOne = i - 1;
            return;
        }

        threadSuspended = null;

        try {
            transfer.putFirst("SUSPEND");
        } catch (InterruptedException ex) {
            Logger.getLogger(XeamQTiles_3.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (threadSuspended == null) {
            /* join: se detiene hasta que se cancela */
        }
        this.synch = synch;
        theLastOne = i - 1;
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

    public void remove(int indexOf) {
        assert EventQueue.isDispatchThread();

        try {
            transfer.putFirst("REMOVE:" + StringUtils.leftPad(Integer.toString(indexOf), 3));
        } catch (InterruptedException ex) {
            Logger.getLogger(XeamQTiles_3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        assert !EventQueue.isDispatchThread();

        Thread thisThread = Thread.currentThread();
        boolean isSuspended = false;

        whileloop:
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

                //Controla la actividad del thread
                String command = (String) transfer.poll(1, TimeUnit.MILLISECONDS);

                int tmp = 0;
                if (command != null) {
                    if (command.contains(":")) {
                        tmp = Integer.parseInt(command.substring(7).trim());
                        command = command.substring(0, 6);
                        System.out.println("NUMEXX " + tmp);
                    }

                    switch (command) {
                        case "STOP":
                            blinker = null;
                            continue;
                        case "SUSPEND":
                            isSuspended = true;
                            continue;
                        case "REMOVE":
                            for (int j = 0; j < tmp; j++) {
                                model.remove();
                            }

                            theLastOne = -1;
                            EventQueue.invokeLater(() -> {
                                pcs.firePropertyChange("DEL_IMG", null, theLastOne);
                            });
                            continue;
                        default:
                    }
                }

                //Itera sobre el modelo, solo escala las que no ha escalado
                Object[] images = model.getAll().toArray();

                if (theLastOne + 1 < images.length) {

                    System.out.println("X Mandar: " + (theLastOne + 1));

                    BufferedImage img = (BufferedImage) images[theLastOne + 1];

                    BufferedImage big = Scalr.resize(img, Scalr.Method.AUTOMATIC, 64 * 4);
                    BufferedImage small = Scalr.resize(img, Scalr.Method.AUTOMATIC, 64);

                    QBallon b1 = new QBallon(img, big, synch);
                    QBallon b2 = new QBallon(img, small, synch);

                    EventQueue.invokeLater(() -> {
                        pcs.firePropertyChange("BIG_", null, b1);
                        pcs.firePropertyChange("SMALL_", null, b2);
                    });

                    theLastOne++;
                    globalCount++;
                } else {
//                    System.out.println("SOLICITA SUSPNECIONS EN REASQUIS " + globalCount);
                    EventQueue.invokeLater(() -> {
                        pcs.firePropertyChange("NO_PICS", null, globalCount);
                    });
//                    isSuspended = true;
//                    continue;
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(XeamQTiles_3.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        threadStopped = true;
        EventQueue.invokeLater(() -> pcs.firePropertyChange("STOPPED", null, null));
    }
}
