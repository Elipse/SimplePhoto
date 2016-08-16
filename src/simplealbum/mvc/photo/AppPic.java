/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.photo;

import java.awt.Container;
import java.awt.Dimension;

/**
 *
 * @author elialva
 */
public class AppPic {

    public static ControllerPic control(Container container, Sender sender) {
        ViewPic viewPic = new ViewPic(container);
        ModelPic modelPicture = new ModelPic(sender, viewPic.getCapacity(), new Dimension(64, 64), new Dimension(64 * 4, 64 * 4));
        return new ControllerPic(viewPic, modelPicture);
    }
}
