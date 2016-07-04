/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.photo;

import java.awt.Container;

/**
 *
 * @author elialva
 */
public class AppPhoto {

    public static ControllerPhoto control(Container container, Sender sender) {
        ViewPhoto viewPicture = new ViewPhoto(container);
        ModelPhoto modelPicture = new ModelPhoto(sender);
        return new ControllerPhoto(viewPicture, modelPicture);
    }
}
