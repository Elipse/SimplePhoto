package simplealbum.mvc.autocomplete;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

class MyPopupMenuListener implements PopupMenuListener {
    
    public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
        System.out.println("Canceled");
    }
    
    public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
        System.out.println("Becoming Invisible");
    }
    
    public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
        System.out.println("Becoming Visible");
    }
}

public class JPopupMenuX {
    
    public static void main(final String args[]) {
        JFrame frame = new JFrame("Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPopupMenu popupMenu = new JPopupMenu("Title");
        PopupMenuListener popupMenuListener = new MyPopupMenuListener();
        
        popupMenu.addPopupMenuListener(popupMenuListener);
        
        JMenuItem cutMenuItem = new JMenuItem("Cut");
        popupMenu.add(cutMenuItem);
        
        JMenuItem copyMenuItem = new JMenuItem("Copy");
        popupMenu.add(copyMenuItem);
        
        JMenuItem pasteMenuItem = new JMenuItem("Paste");
        pasteMenuItem.setEnabled(false);
        popupMenu.add(pasteMenuItem);
        
        popupMenu.addSeparator();
        
        JMenuItem findMenuItem = new JMenuItem("Find");
        popupMenu.add(findMenuItem);
        JButton label = new JButton();
        frame.add(label);
        frame.getRootPane().setComponentPopupMenu(popupMenu);
//        label.setComponentPopupMenu(popupMenu);

        frame.setSize(350, 250);
        frame.setVisible(true);
    }
}
