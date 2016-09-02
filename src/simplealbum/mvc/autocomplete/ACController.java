/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.autocomplete;

import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import simplealbum.test.NewJFrame1;
import utils.KeyStrokesUtil;
import utils.WrapperAction;

/**
 *
 * @author elialva
 */
public class ACController {

    private static MyListener myListener;
    private static ACModel model;
    private static String sync;
    private static Consumer consumer;
    private static Hunter hunter;

    private static void doInEDTProducer(CaretEvent e) {
        assert (EventQueue.isDispatchThread());
        try {
            JTextComponent jtf = (JTextComponent) e.getSource();
            ACController.sync = jtf.getText();
            boolean offer = buffer.offer(ACController.sync);
            if (!offer) {
                System.out.println("Limpiando!!!");
                buffer.clear();
                buffer.offer(ACController.sync);
            }
            Rectangle c = jtf.modelToView(jtf.getCaretPosition());
            Point p = jtf.getLocationOnScreen();
            SwingUtilities.convertPointFromScreen(p, lp);
//                    System.out.println("p " + p);
            panel.setBounds(p.x + c.x + 3, p.y + c.y + c.height, panel.getPreferredSize().width, panel.getPreferredSize().height);
        } catch (BadLocationException ex) {
            Logger.getLogger(NewJFrame1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void doInBackgroundConsumer() {
        assert (!EventQueue.isDispatchThread());
        while (true) {
            try {
                String hint = buffer.take();
                if (!hint.equals(ACController.sync)) {
                    System.out.println("Se tira en Background" + hint);
                    continue;
                }
                System.out.println("Consumo " + hint);
                List<String> listOfWords = hunter.getListOfWords(hint);
                consumer.firePropertyChange("words", hint, listOfWords);
            } catch (InterruptedException ex) {
                Logger.getLogger(ACController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private static void displayOnEDT(PropertyChangeEvent evt) {
        assert (EventQueue.isDispatchThread());
        String property = evt.getPropertyName();
        switch (property) {
            case "words":
                if (!evt.getOldValue().equals(ACController.sync)) {
                    System.out.println("Se tira en propertyChange " + evt.getOldValue());
                    break;
                }
                model.setList((List<String>) evt.getNewValue());
                System.out.println("Se muestra en propertyChange " + evt.getOldValue());
                break;
            default:
                System.out.println("Property " + evt.getPropertyName());
        }
    }

    Map map = new HashMap();
    private static ACController autocomplete;
    private static ArrayBlockingQueue<String> buffer;

    public static ACController newAutoComplete() {
        if (autocomplete == null) {
            autocomplete = new ACController();
            myListener = new MyListener();
            panel = new ACView();
            buffer = new ArrayBlockingQueue<>(20);
            model = new ACModel();
            consumer = new Consumer();
            consumer.addPropertyChangeListener(myListener);
            consumer.execute();
            hunter = new Hunter();
        }
        return autocomplete;
    }

    private static ACView panel;
    private static JLayeredPane lp;

    public void wrap(JTextField jTextField) {
        String name = jTextField.getName();
        map.put(name, jTextField);

        panel.setBounds(10, 10, panel.getPreferredSize().width, panel.getPreferredSize().height);
        JList jList = panel.getjList();
        jList.setModel(model);

        lp = (JLayeredPane) jTextField.getParent().getParent();
        lp.add(panel, new Integer(200));

        System.out.println("Parent " + jTextField.getParent().getParent());

        jTextField.getDocument().addDocumentListener(myListener);
        jTextField.addCaretListener(myListener);
        jTextField.addFocusListener(myListener);
        jTextField.addActionListener(myListener);

        KeyStrokesUtil.wrapAction(jTextField, JComponent.WHEN_FOCUSED, "pressed KP_LEFT", new MyAction());
        KeyStrokesUtil.wrapAction(jTextField, JComponent.WHEN_FOCUSED, "pressed LEFT", new MyAction());
        KeyStrokesUtil.wrapAction(jTextField, JComponent.WHEN_FOCUSED, "pressed KP_RIGHT", new MyAction());
        KeyStrokesUtil.wrapAction(jTextField, JComponent.WHEN_FOCUSED, "pressed RIGHT", new MyAction());
        KeyStrokesUtil.wrapAction(jTextField, JComponent.WHEN_FOCUSED, "pressed ENTER", new MyAction());
    }

    public void wrap(JTextArea jTextArea) {
        KeyStrokesUtil.wrapAction(jTextArea, JComponent.WHEN_FOCUSED, "pressed KP_LEFT", new MyAction());
        KeyStrokesUtil.wrapAction(jTextArea, JComponent.WHEN_FOCUSED, "pressed LEFT", new MyAction());
        KeyStrokesUtil.wrapAction(jTextArea, JComponent.WHEN_FOCUSED, "pressed KP_RIGHT", new MyAction());
        KeyStrokesUtil.wrapAction(jTextArea, JComponent.WHEN_FOCUSED, "pressed RIGHT", new MyAction());

        KeyStrokesUtil.wrapAction(jTextArea, JComponent.WHEN_FOCUSED, "pressed UP", new MyAction());
        KeyStrokesUtil.wrapAction(jTextArea, JComponent.WHEN_FOCUSED, "pressed DOWN", new MyAction());

        KeyStrokesUtil.wrapAction(jTextArea, JComponent.WHEN_FOCUSED, "pressed ENTER", new MyAction());
        KeyStrokesUtil.wrapAction(jTextArea, JComponent.WHEN_FOCUSED, "pressed TAB", new MyAction());

        jTextArea.addCaretListener(myListener);
    }

    private class MyAction extends WrapperAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            originalAction.actionPerformed(e);
//            System.out.println("Presionó AutoComplete3: " + e.getActionCommand());
        }
    }

    private static class Consumer extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            ACController.doInBackgroundConsumer();
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
            } catch (ExecutionException | InterruptedException e) {
                Logger.getLogger(ACController.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    private static class MyListener implements DocumentListener, FocusListener, CaretListener, ActionListener, PropertyChangeListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
//            panel.setVisible(true);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            //          jPopupMenu.setVisible(true);
//            panel.setVisible(false);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }

        @Override
        public void focusGained(FocusEvent e) {
            JTextField source = (JTextField) e.getSource();
            panel.setVisible(true);
        }

        @Override
        public void focusLost(FocusEvent e) {
            JTextField source = (JTextField) e.getSource();
            panel.setVisible(true);
        }

        @Override
        public void caretUpdate(CaretEvent e) {
            SwingUtilities.invokeLater(() -> {
                ACController.doInEDTProducer(e);
            });
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Si es visible haz la selección");
            panel.setVisible(false);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            ACController.displayOnEDT(evt);
        }
    }
}
