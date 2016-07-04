/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.app;

import simplealbum.mvc.autocomplete.*;
import java.awt.Container;
import javax.swing.JRootPane;
import simplealbum.mvc.photo.ControllerPhoto;
import simplealbum.mvc.photo.ModelPhoto;
import simplealbum.mvc.photo.ViewPhoto;
import simplealbum.mvc.picture.impl.DBProvider;

/**
 *
 * @author elialva
 */
public class App {

    public static ControllerApp control(JRootPane rootPane, DBProvider dbProvider) {
        ViewApp view = new ViewApp(rootPane);
        ModelApp model = new ModelApp(dbProvider);
        return new ControllerApp(view, model);
    }
}
