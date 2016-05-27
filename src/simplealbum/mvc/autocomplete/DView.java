/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.autocomplete;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

/**
 *
 * @author elialva
 */
public final class DView extends javax.swing.JDialog {

    private static final KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    public static final String dispatchWindowClosingActionMapKey = "com.spodding.tackline.dispatch:WINDOW_CLOSING";
    private String action;
    private JTextField textField;
    private DController dController;

    /**
     * Creates new form VDialog
     */
    DView(Frame frame, boolean b) {
        super(frame, b);
        initComponents();

//        installEscapeCloseOperation(this);
        Action enter = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                dController.processSelection();
                action = "NEXT";
                setVisible(false);
            }
        };
        Action escape = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                action = "STAY";
                dController.cancel();
                setVisible(false);
            }
        };

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "DO_ENTER");
        rootPane.getActionMap().put("DO_ENTER", enter);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "DO_TAB");
        rootPane.getActionMap().put("DO_TAB", enter);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "DO_ESCAPE");
        rootPane.getActionMap().put("DO_ESCAPE", escape);

        reassignKeyStrokes(jTextPane1, JComponent.WHEN_FOCUSED, "pressed ENTER", "shift pressed ENTER");
        reassignKeyStrokes(jTextPane1, JComponent.WHEN_FOCUSED, "pressed TAB", "shift pressed TAB");

        InputMap inputMap = jTextPane1.getInputMap();
//        InputMap inputMap = jScrollPane3.getInputMap();
        KeyStroke[] allKeys = inputMap.allKeys();
        for (KeyStroke allKey : allKeys) {
            System.out.println("inputMap " + allKey);
        }
        action = "";

        configureRowSelectionKeyboard();
        configureRowSelectionMouse();

        jTextPane1.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    String text = e.getDocument().getText(0, e.getDocument().getLength());
                    dController.request(text);
                } catch (BadLocationException ex) {
                    Logger.getLogger(DView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    String text = e.getDocument().getText(0, e.getDocument().getLength());
                    dController.request(text);
                } catch (BadLocationException ex) {
                    Logger.getLogger(DView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

    void configureRowSelectionMouse() {
        jTable1.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
//                    System.out.println("Solo fue 1");
//                    dController.processPreSelection();
                } else {
                    //TODO Se puede asociar la action ??? al doble click
                    dController.processSelection();
                    action = "NEXT";
                    setVisible(false);
                }
            }

        });
    }

    void configureRowSelectionKeyboard() {
        reassignKeyStrokes(jTextPane1, JComponent.WHEN_FOCUSED, "pressed UP", "alt UP");
        reassignKeyStrokes(jTextPane1, JComponent.WHEN_FOCUSED, "pressed PAGE_UP", "alt PAGE_UP");
        reassignKeyStrokes(jTextPane1, JComponent.WHEN_FOCUSED, "pressed DOWN", "alt DOWN");
        reassignKeyStrokes(jTextPane1, JComponent.WHEN_FOCUSED, "pressed PAGE_DOWN", "alt PAGE_DOWN");
        reassignKeyStrokes(jScrollPane3, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "pressed UP", "alt UP");
        reassignKeyStrokes(jScrollPane3, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "pressed PAGE_UP", "alt PAGE_UP");
        reassignKeyStrokes(jScrollPane3, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "pressed DOWN", "alt DOWN");
        reassignKeyStrokes(jScrollPane3, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "pressed PAGE_DOWN", "alt PAGE_DOWN");

        Action up = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                dController.selectionUp();
            }
        };

        Action down = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                dController.selectionDown();
            }
        };

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "DO_UP");
        rootPane.getActionMap().put("DO_UP", up);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), "PAGE_UP");
        rootPane.getActionMap().put("PAGE_UP", up);

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "DO_DOWN");
        rootPane.getActionMap().put("DO_DOWN", down);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), "PAGE_DOWN");
        rootPane.getActionMap().put("PAGE_DOWN", down);

    }

    void setDController(DController dController) {
        this.dController = dController;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Id", "Descripción", "Imagen"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setFocusable(false);
        jTable1.setRowHeight(64);
        jScrollPane2.setViewportView(jTable1);

        jScrollPane3.setViewportView(jTextPane1);

        jButton1.setText("RePág");
        jButton1.setFocusable(false);

        jButton2.setText("AvPág");
        jButton2.setFocusable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2)
                    .addComponent(jButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void reassignKeyStrokes(JComponent component, int condition, String keyStrokeOld, String keyStrokeNew) {
        Object binding = component.getInputMap(condition).get(KeyStroke.getKeyStroke(keyStrokeOld));
        System.out.println("binding " + binding);
        Action actionC = component.getActionMap().get(binding);
        System.out.println("actionC " + actionC);

        component.getInputMap(condition).put(KeyStroke.getKeyStroke(keyStrokeOld), "none");

        System.out.println("Poniendo... " + keyStrokeNew + "-" + binding);
        component.getInputMap(condition).put(KeyStroke.getKeyStroke(keyStrokeNew), binding);
        component.getActionMap().put(binding, actionC);
    }

    public static void installEscapeCloseOperation(final JDialog dialog) {
        Action dispatchClosing = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                dialog.dispatchEvent(new WindowEvent(
                        dialog, WindowEvent.WINDOW_CLOSING
                ));
            }
        };
        JRootPane root = dialog.getRootPane();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, dispatchWindowClosingActionMapKey);
        root.getActionMap().put(dispatchWindowClosingActionMapKey, dispatchClosing);

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    int getSelectedRow() {
        return jTable1.getSelectedRow();
    }

    int getRowCount() {
        return jTable1.getModel().getRowCount();
    }

    void setRowSelectionInterval(int index0, int index1) {
        jTable1.setRowSelectionInterval(index0, index1);
    }

}
