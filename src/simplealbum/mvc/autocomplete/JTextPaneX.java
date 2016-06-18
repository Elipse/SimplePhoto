/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.autocomplete;

import java.awt.EventQueue;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import org.apache.commons.lang3.StringUtils;
import utils.ColorUtils;

/**
 *
 * @author elialva
 */
public class JTextPaneX extends JTextPane {

    private final Style[] styles;
    private final DefaultStyledDocument dsd;
    private final MyDocumentFilter documentFilter;

    public JTextPaneX() {

        //Create the style array to show the colors 
//        List<Object> colorList = CONFIGURATION.getList("ColorWord");
        List<Object> colorList = null;
        styles = new Style[colorList.size()];
        StyleContext sc = new StyleContext();
        for (int i = 0; i < colorList.size(); i++) {
            styles[i] = sc.addStyle((String) colorList.get(i), sc.getStyle(StyleContext.DEFAULT_STYLE));
            StyleConstants.setForeground(styles[i], ColorUtils.getColorByName((String) colorList.get(i)));
            StyleConstants.setBold(styles[i], true);
        }

        //Get the document for adding a document listener
        dsd = (DefaultStyledDocument) getDocument();
        dsd.addDocumentListener(new DocumentListenerTextPane());

        //...and setting a document filter
        documentFilter = new MyDocumentFilter();
        dsd.setDocumentFilter(documentFilter);
    }

    public void setFilter(String title, String regex) {
        setText("");
        documentFilter.setRegex(regex);
        TitledBorder border = (TitledBorder) JTextPaneX.this.getBorder();
        border.setTitle(title);
        repaint();
    }

    private class DocumentListenerTextPane implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            fireText(e);
            colorStyledDocument((DefaultStyledDocument) e.getDocument());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            fireText(e);
            colorStyledDocument((DefaultStyledDocument) e.getDocument());
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }
    }

    protected void fireText(DocumentEvent e) {
        Document document = e.getDocument();
        String text = "";
        try {
            text = document.getText(0, document.getLength());
        } catch (BadLocationException ex) {
            Logger.getLogger(JTextPaneX.class.getName()).log(Level.SEVERE, null, ex);
        }
        firePropertyChange("text", null, text);
    }

    protected void colorStyledDocument(final DefaultStyledDocument document) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                String input = "";
                try {
                    input = document.getText(0, document.getLength());
                } catch (BadLocationException ex) {
                    Logger.getLogger(JTextPaneX.class.getName()).log(Level.SEVERE, null, ex);
                }

                StringBuilder inputMut = new StringBuilder(input);
                String[] split = StringUtils.split(inputMut.toString());
                int i = 0;
                for (String string : split) {
                    int start = inputMut.indexOf(string);
                    int end = start + string.length();
                    inputMut.replace(start, end, StringUtils.repeat(" ", string.length()));
                    document.setCharacterAttributes(start, string.length(), styles[i++ % styles.length], true);
                }
            }
        }
        );
    }

    private class MyDocumentFilter extends DocumentFilter {

        Pattern regExp = Pattern.compile(".");

        private void setRegex(String regex) {
            this.regExp = Pattern.compile(regex);
        }

        @Override
        public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            Matcher matcher = regExp.matcher(text);
            if (!matcher.matches()) {
                return;
            }
            super.replace(fb, offset, length, text, attrs); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
            super.remove(fb, offset, length); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            Matcher matcher = regExp.matcher(string);
            if (!matcher.matches()) {
                return;
            }
            super.insertString(fb, offset, string, attr); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
