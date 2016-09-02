
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import simplealbum.mvc.picture.impl.SimpleCat;

public class FocusedField {

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(FocusedField.class.getName()).log(Level.SEVERE, null, ex);
        }
        new FocusedField();
    }

    public FocusedField() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

//                try {
//                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
//                }
                JTextField field1 = new JTextField(20);
                JTextField field2 = new JTextField(20);

                FocusListener highlighter = new FocusListener() {

                    @Override
                    public void focusGained(FocusEvent e) {
                        e.getComponent().setBackground(Color.YELLOW);
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        e.getComponent().setBackground(UIManager.getColor("TextField.background"));
                    }
                };

                field1.addFocusListener(highlighter);
                field2.addFocusListener(highlighter);

                JFrame frame = new JFrame("Testing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(4, 4, 4, 4);
                gbc.gridwidth = gbc.REMAINDER;
                frame.add(field1, gbc);
                frame.add(field2, gbc);
                frame.add(new JButton("Hi!"), gbc);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

}
