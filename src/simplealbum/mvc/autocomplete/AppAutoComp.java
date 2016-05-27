/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.autocomplete;

import java.awt.Container;

/**
 *
 * @author elialva
 */
public class AppAutoComp {

    public static Controller control(Container container, SeekerFactory factory) {
        View view = new View(container);
        return new Controller(view, factory);
    }
}
