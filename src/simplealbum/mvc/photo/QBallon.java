/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.photo;

import java.awt.image.BufferedImage;

/**
 *
 * @author elialva
 */
public class QBallon {

    private final BufferedImage original;
    private final BufferedImage scaled;
    private final Object synch;

    public QBallon(BufferedImage original, BufferedImage scaled, Object synch) {
        this.original = original;
        this.scaled = scaled;
        this.synch = synch;
    }

    /**
     * @return the original
     */
    public BufferedImage getOriginal() {
        return original;
    }

    /**
     * @return the scaled
     */
    public BufferedImage getScaled() {
        return scaled;
    }

    /**
     * @return the synch
     */
    public Object getSynch() {
        return synch;
    }
}
