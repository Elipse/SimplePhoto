package simplealbum.test;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class PopUpJList extends JPanel
        implements ActionListener {

    JButton jb1, jb2;
    JPopupMenu popupMenu;
    JMenuItem jmi1, jmi2;
    JList list;
    int i = 1;

    public PopUpJList() {
        Vector data;
        setLayout(new BorderLayout());
        list = new JList();
        list.setModel(new DefaultListModel());
        add(new JScrollPane(list), "Center");
        add(jb1 = new JButton("Add"), "East");
        add(jb2 = new JButton("Clear"), "West");
        jb1.addActionListener(this);
        jb2.addActionListener(this);

        popupMenu = new JPopupMenu();
        popupMenu.add(jmi1 = new JMenuItem("Add"));
        popupMenu.add(new JPopupMenu.Separator());
        popupMenu.add(jmi2 = new JMenuItem("Clear"));

        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                // if right mouse button clicked (or me.isPopupTrigger())
                if (SwingUtilities.isRightMouseButton(me)
                        && !list.isSelectionEmpty()
                        && list.locationToIndex(me.getPoint())
                        == list.getSelectedIndex()) {
                    popupMenu.show(list, me.getX(), me.getY());
                }
            }
        }
        );

        jmi1.addActionListener(this);
        jmi2.addActionListener(this);

    }

    public Dimension getPreferredSize() {
        return new Dimension(50, 50);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == jb1 || ae.getSource() == jmi1) {
            // add
            DefaultListModel dlm
                    = (DefaultListModel) list.getModel();
            dlm.addElement((Object) Integer.toString(i++));
        } else {
            // clear
            list.setModel(new DefaultListModel());
        }
    }

    public static void main(String s[]) {
        JFrame frame = new JFrame("PopUp JList");
        PopUpJList panel = new PopUpJList();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.getContentPane().add(panel, "Center");

        frame.setSize(200, 200);
        frame.setVisible(true);
    }
}
