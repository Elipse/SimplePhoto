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
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.swing.table.AbstractTableModel;
import javax.swing.text.BadLocationException;
import simplealbum.mvc.picture.impl.SeekerName;

/**
 *
 * @author elialva
 */
public class DModel extends SwingWorker<Void, InfoPage> {

    private MyTableModel tableModel;

    private final ArrayBlockingQueue requests;

    public static final String PROP_SEEKER = "PROP_SEEKER";

    private Seeker seeker;
    private final transient PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
    private volatile Object lastRequest;
    private String query;
    private DController controller;

    public static DModel newInstance() {
        return new DModel();
    }
    private volatile InfoPage infoPage;

    private DModel() {
        tableModel = new MyTableModel();
        requests = new ArrayBlockingQueue(20);
    }

    public boolean request(String query) {
        assert EventQueue.isDispatchThread();

        ArrayList list = new ArrayList();
        list.add(Arrays.asList("Waiting....."));
        tableModel.setList(list);

        this.query = query;
        return requests.add(query);
    }

    public boolean requestPageUp() {
        assert EventQueue.isDispatchThread();

        ArrayList list = new ArrayList();
        list.add(Arrays.asList("Waiting....."));
        tableModel.setList(list);

        infoPage.setDirection(-1);
        return requests.add(infoPage);
    }

    public boolean requestPageDown() {
        assert EventQueue.isDispatchThread();

        ArrayList list = new ArrayList();
        list.add(Arrays.asList("Waiting....."));
        tableModel.setList(list);

        infoPage.setDirection(+1);
        return requests.add(infoPage);
    }

    @Override
    protected Void doInBackground() throws Exception {
        while (!isCancelled()) {

            Object request;
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
                InfoPage response;

                if (lastRequest instanceof String) {
                    System.out.println("Un query String");
                    response = seeker.command((String) lastRequest);
                } else {
                    System.out.println("Un query InfoPage " + seeker.getClass() + " " + lastRequest);
                    response = seeker.command((InfoPage) lastRequest);
                    System.out.println("Salgo de Un query InfoPage");
                }

                publish(response);
            }
        }
        return null;
    }

    @Override
    protected void process(List<InfoPage> chunks) {
        assert EventQueue.isDispatchThread();

        int i = chunks.size() - 1;
        infoPage = chunks.get(i);

        if (infoPage.getQuery().equals(query)) {
            tableModel.setList(infoPage.getRows());
            System.out.println("Si, gracias. " + infoPage.getQuery() + " vs " + query);
        } else {
            //TODO incluir aqui recordPage??? AvPag y RePag no harian nada con null  !!!?
            System.out.println("No, gracias. " + infoPage.getQuery() + " vs " + query);
        }
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

    public void emptyList() {
        ArrayList list = new ArrayList();
        list.add(Arrays.asList("Inserte texto de búsqueda"));
        tableModel.setList(list);
    }

    /**
     * @return the tableModel
     */
    public MyTableModel getTableModel() {
        return tableModel;
    }

    /**
     * @param aTableModel the tableModel to set
     */
    public void setTableModel(MyTableModel aTableModel) {
        tableModel = aTableModel;
    }

    void setDController(DController controller) {
        this.controller = controller;
    }

    public static class MyTableModel extends AbstractTableModel {

        private List<List<String>> list;

        public MyTableModel() {
        }

        @Override
        public String getColumnName(int column) {
            return list != null ? list.get(0).get(column) : "";
        }

        @Override
        public int getRowCount() {
            return list != null ? list.size() - 1 : 0;
        }

        @Override
        public int getColumnCount() {
            return list != null ? list.get(0).size() : 0;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return list != null ? list.get(rowIndex + 1).get(columnIndex) : null;
        }

        /**
         * @param list the list to set
         */
        public void setList(List<List<String>> list) {

            System.out.println("Seteando despies de thread " + list.size());
            System.out.println("list. " + list.get(0).get(0));
            System.out.println("escuchandoA " + listenerList.getListenerCount());
            this.list = list;
//            fireTableDataChanged();
            System.out.println("escuchandoB " + listenerList.getListenerCount());
            System.out.println("Tamaño " + getRowCount());
            fireTableStructureChanged();
        }

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

        EventQueue.invokeAndWait(() -> {
            dialog.setVisible(true);

            model.execute();
            for (int i = 0; i < 3; i++) {
//                    model.request("Esta es " + i);
            }
        });
    }
}
