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

    public static ControllerPicture control(Container container, Sender sender) {
        ViewPicture viewPicture = new ViewPicture(container);
        ModelPicture modelPicture = new ModelPicture(sender);
        return new ControllerPicture(viewPicture, modelPicture);
    }
}
