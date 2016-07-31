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
public class AppPic {

    public static ControllerPic control(Container container, Sender sender) {
        ViewPic viewPicture = new ViewPic(container);
        ModelPic modelPicture = new ModelPic(sender);
        return new ControllerPic(viewPicture, modelPicture);
    }
}
