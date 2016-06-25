/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.autocomplete;

import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import org.apache.commons.lang3.StringUtils;
import utils.ColorUtils;
import static utils.KeyStrokesUtil.assignKeyStrokes;
import static utils.KeyStrokesUtil.reassignKeyStrokes;

/**
 *
 * @author elialva
 */
public class DController implements DocumentListener, MouseListener {

    private final DView view;
    private final DModel model;

    private Style[] styles;

    private final SeekerFactory factory;

    public static DController newInstance(Frame frame, SeekerFactory factory) {
        DView view = new DView(frame, true);
        DModel model = DModel.newInstance();
        return new DController(view, model, factory);
    }
    private Controller masterController;
    private final JTextPane jTextPane;
    private final JTable jTable;
    private final StyledDocument jTextPaneDocument;
//    private final DModel.MyTableModel jTableModel;
    private String action;
    private final JScrollPane jScrollPane;
    private String filter;
    private String focusOwner;
    private InfoPage infoPage;

    private Map<String, Integer> idMap;

    private DController(DView view, DModel model, SeekerFactory factory) {
        this.view = view;
        this.view.setDController(DController.this);

        this.model = model;
        this.model.setDController(DController.this);
        this.model.execute();

        jTextPane = this.view.jTextPane1;
        jTextPaneDocument = (StyledDocument) jTextPane.getDocument();
        jTextPaneDocument.addDocumentListener(DController.this);

        jScrollPane = this.view.jScrollPane1;

        jTable = this.view.jTable1;
        jTable.addMouseListener(DController.this);

        //System.out.println("Modelito " + this.model.getTableModel());
        jTable.setModel(this.model.getTableModel());

        action = "";

        this.factory = factory;

        idMap = new HashMap<>();

        configureKeyBoard();

    }

    private void configureKeyBoard() {
        reassignKeyStrokes(jTextPane, JComponent.WHEN_FOCUSED, "pressed ENTER", "shift pressed ENTER");
        assignKeyStrokes(view.getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW, "pressed ENTER", new MyAction("DO_ENTER"));

        assignKeyStrokes(view.getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW, "pressed ESCAPE", new MyAction("DO_ESCAPE"));

        reassignKeyStrokes(jTextPane, JComponent.WHEN_FOCUSED, "pressed TAB", "shift pressed TAB");
        assignKeyStrokes(view.getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW, "pressed TAB", new MyAction("DO_TAB"));

        reassignKeyStrokes(jTextPane, JComponent.WHEN_FOCUSED, "pressed UP", "alt UP");
        reassignKeyStrokes(jScrollPane, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "pressed UP", "alt UP");
        assignKeyStrokes(view.getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW, "pressed UP", new MyAction("DO_UP"));

        reassignKeyStrokes(jTextPane, JComponent.WHEN_FOCUSED, "pressed DOWN", "alt DOWN");
        reassignKeyStrokes(jScrollPane, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "pressed DOWN", "alt DOWN");
        assignKeyStrokes(view.getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW, "pressed DOWN", new MyAction("DO_DOWN"));

        reassignKeyStrokes(jTextPane, JComponent.WHEN_FOCUSED, "pressed PAGE_UP", "alt PAGE_UP");
        reassignKeyStrokes(jScrollPane, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "pressed PAGE_UP", "alt PAGE_UP");
        assignKeyStrokes(view.getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW, "pressed PAGE_UP", new MyAction("DO_PAGE_UP"));

        reassignKeyStrokes(jTextPane, JComponent.WHEN_FOCUSED, "pressed PAGE_DOWN", "alt PAGE_DOWN");
        reassignKeyStrokes(jScrollPane, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, "pressed PAGE_DOWN", "alt PAGE_DOWN");
        assignKeyStrokes(view.getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW, "pressed PAGE_DOWN", new MyAction("DO_PAGE_DOWN"));

        assignKeyStrokes(view.getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW, "pressed F5", new MyAction("pressed F5"));
    }

    public void request() {
        //System.out.println("REUEQSR ");
        String inputText = jTextPane.getText();
        if (inputText.isEmpty()) {
            model.emptyList();
        } else {
            model.request(inputText);
        }
    }

    String getAction() {
        return action;
    }

    void setAction(String string) {
        action = string;
    }

    boolean isShowing() {
        return view.isShowing();
    }

    void setLocation(Point locationOnScreen) {
        view.setLocation(locationOnScreen);
    }

    void setVisible(boolean b) {
        view.setVisible(b);
    }

    void focusGained(String focusOwner, String inputText) {
        System.out.println("Set Owner"); //Solo puede pasar JTextComponent
        this.focusOwner = focusOwner;
        Seeker seeker = factory.retrieveSeeker(focusOwner);
        changeSeeker(seeker);

        Integer id = idMap.get(focusOwner);
        if (id != null) {
            jTextPane.setText("-i " + id);
            System.out.println("Tamaño " + jTable.getModel().getRowCount());
            for (int i = 0; i < jTable.getModel().getRowCount(); i++) {
                Integer idTmp = (Integer) jTable.getModel().getValueAt(i, 0);
                System.out.println("idTp " + idTmp + " --- " + id);
                if ((Objects.equals(idTmp, id))) {
                    jTable.setRowSelectionInterval(i, i);
                }
            }
        } else {
            jTextPane.setText("");
        }

    }

    void changeSeeker(Seeker seeker) {
        view.setTitle(seeker.getTitle());
//        jTextPane.setText("");  TODO 
        model.emptyList();
        model.setSeeker(seeker);
        request();

        List<String> colors = seeker.getColors();
        styles = new Style[colors.size()];
        StyleContext sc = new StyleContext();
        for (int i = 0; i < colors.size(); i++) {
            styles[i] = sc.addStyle((String) colors.get(i), sc.getStyle(StyleContext.DEFAULT_STYLE));
            StyleConstants.setForeground(styles[i], ColorUtils.getColorByName(colors.get(i)));
            StyleConstants.setBold(styles[i], true);
        }
        colorInputText();

        filter = seeker.getFilter();
    }

    void selectionDown() {
        int selectedRow = jTable.getSelectedRow() + 1;
        selectedRow = selectedRow > jTable.getRowCount() - 1 ? 0 : selectedRow;
        jTable.setRowSelectionInterval(selectedRow, selectedRow);
    }

    void selectionUp() {
        int selectedRow = jTable.getSelectedRow() - 1;
        selectedRow = selectedRow < 0 ? jTable.getRowCount() - 1 : selectedRow;
        jTable.setRowSelectionInterval(selectedRow, selectedRow);
    }

    void cancel() {
        //System.out.println("Cancelando captura " + masterController);
    }

    void setController(Controller controller) {
        this.masterController = controller;
    }

    void processSelection() {
        //TODO SE EXTRAE ID Y SE GRABA EN IDMAP
        int row = jTable.getSelectedRow();
        if (row >= 0) {
            String idSt = (String) jTable.getModel().getValueAt(row, 0);
            int id = Integer.parseInt(idSt);
            idMap.put(focusOwner, id);
            masterController.changedId(focusOwner, id);
        }
    }

    //* Implements DocumentListener * /
    @Override
    public void insertUpdate(DocumentEvent e) {
        request();
        colorInputText();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        request();
        colorInputText();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }
    //* Implements DocumentListener * /

    private void colorInputText() {
        EventQueue.invokeLater(() -> {
            try {
                String inputText = jTextPaneDocument.getText(0, jTextPaneDocument.getLength());
                StringBuilder inputMut = new StringBuilder(inputText);
                String[] split = StringUtils.split(inputMut.toString());
                int i = 0;
                for (String string : split) {
                    int start = inputMut.indexOf(string);
                    int end = start + string.length();
                    inputMut.replace(start, end, StringUtils.repeat(" ", string.length()));
                    jTextPaneDocument.setCharacterAttributes(start, string.length(), styles[i++ % styles.length], true);
                }
            } catch (BadLocationException ex) {
                Logger.getLogger(DController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    //* Implements MouseListener * /
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 1) {
//                    System.out.println("Solo fue 1");
//                    dController.processPreSelection();
        } else {
            //TODO Se puede asociar la action ??? al doble click
            processSelection();
            action = "NEXT";
            view.setVisible(false);
        }
        //System.out.println("en mousadasd ads en dcontroller");
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    //* Implements MouseListener * /

    private void actionPerformed(Object name) {

        switch (name.toString()) {
            case "DO_ENTER":
                action = "NEXT";
                processSelection();
                view.setVisible(false);
                break;
            case "DO_TAB":
                action = "NEXT";
                processSelection();
                view.setVisible(false);
                break;
            case "DO_ESCAPE":
                action = "STAY";
                cancel();
                view.setVisible(false);
                break;
            case "DO_UP":
                selectionUp();
                break;
            case "DO_PAGE_UP":
                pageUp();
                break;
            case "DO_DOWN":
                selectionDown();
                break;
            case "DO_PAGE_DOWN":
                pageDown();
                break;
            case "pressed F5":
                Seeker seeker = factory.retrieveNextSeeker(focusOwner);
                changeSeeker(seeker);
                break;
            default:
                throw new AssertionError();
        }

    }

    private void pageUp() {
        //System.out.println("EN DCONTROLLER PAGE UP");
        model.requestPageUp();
    }

    private void pageDown() {
        //System.out.println("EN DCONTROLLER PAGE DOWN");
        model.requestPageDown();
    }

    private class MyAction extends AbstractAction {

        private MyAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DController.this.actionPerformed(getValue(NAME));
        }
    }
}
