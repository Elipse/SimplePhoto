/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.photo;

import java.beans.PropertyChangeListener;
import java.util.Arrays;

/**
 *
 * @author elialva
 */
public class QuickList extends Quick {

    public QuickList(PropertyChangeListener l, String property) {
        super(l, property);
    }

    @Override
    protected Object doInBackgroud(Object request) {
        System.out.println("Procesando " + request);
        return Arrays.asList(new String[]{"Xico", "Mexico"});
    }

}
