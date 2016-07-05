/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author elialva
 */
public abstract class MyAction extends AbstractAction {

    private MyAction(String name) {
        super(name);
    }

    @Override
    public abstract void actionPerformed(ActionEvent e);
}
