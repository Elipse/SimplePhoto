/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.autocomplete;

import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import simplealbum.mvc.picture.impl.SeekerName;

/**
 *
 * @author elialva
 */
public class DModel extends SwingWorker<Void, InfoPage> {

    private final ArrayBlockingQueue<String> requests;

    public static final String PROP_SEEKER = "PROP_SEEKER";

    private Seeker seeker;
    private final transient PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
    private volatile String lastRequest;
    private String query;
    private DController controller;

    public DModel() {
        requests = new ArrayBlockingQueue(20);
    }

    @Override
    protected Void doInBackground() throws Exception {
        while (!isCancelled()) {

            String request;
            lastRequest = null;
            while (true) {
                request = requests.poll(40, TimeUnit.MILLISECONDS);

                if (request != null) {
                    lastRequest = request;
                    System.out.println("lastRequest " + lastRequest);
                }

                //Esperé más de 40 ms y tengo un dato que procesar
                if (request == null && lastRequest != null) {
                    break;
                }
            }

            System.out.println("procese " + lastRequest);
            if (seeker != null) {
                InfoPage response = seeker.command(lastRequest);
                publish(response);
            }
        }
        return null;
    }

    @Override
    protected void process(List<InfoPage> chunks) {
        assert EventQueue.isDispatchThread();

        int i = chunks.size() - 1;
        InfoPage infoPage = chunks.get(i);

        if (infoPage.getQuery().equals(query)) {
            controller.response(infoPage);
            System.out.println("Si, gracias. " + infoPage.getQuery() + " vs " + query);
        } else {
            System.out.println("No, gracias. " + infoPage.getQuery() + " vs " + query);
        }
    }

    public boolean request(String query) {
        System.out.println("Si llegó???" + Thread.currentThread());
        assert EventQueue.isDispatchThread();

        this.query = query;
        return requests.add(query);
    }

    /**
     * @return the seeker
     */
    public Seeker getSeeker() {
        return seeker;
    }

    /**
     * @param seeker the seeker to set
     */
    public void setSeeker(Seeker seeker) {
        Seeker oldSeeker = this.seeker;
        this.seeker = seeker;
        propertyChangeSupport.firePropertyChange(PROP_SEEKER, oldSeeker, seeker);

        this.query = null;
    }

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {

        DModel model = new DModel();
        model.setSeeker(new SeekerName());

        JDialog dialog = new JDialog();
        JTextField tf = new JTextField();
        dialog.add(tf);

        tf.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    String text = e.getDocument().getText(0, e.getDocument().getLength());
//                    System.out.println("insertUpdate " + text);
                    model.request(text);
                } catch (BadLocationException ex) {
                    Logger.getLogger(DModel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    String text = e.getDocument().getText(0, e.getDocument().getLength());
//                    System.out.println("removeUpdate " + text);
                    model.request(text);
                } catch (BadLocationException ex) {
                    Logger.getLogger(DModel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                System.out.println("changedUpdate");
            }
        });

        tf.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                JTextField tf = (JTextField) e.getSource();
//                System.out.println("texti " + tf.getText() + " time " + e.getWhen() + " - " + e.getKeyChar());
//                model.request(tf.getText());
            }
        });
        dialog.pack();

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Cerrando");
                model.cancel(true);
            }
        });

        EventQueue.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                dialog.setVisible(true);

                model.execute();
                for (int i = 0; i < 3; i++) {
//                    model.request("Esta es " + i);
                }

            }
        });
    }

    void setDController(DController controller) {
        this.controller = controller;
    }
}
