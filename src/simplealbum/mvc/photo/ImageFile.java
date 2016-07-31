/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.photo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

/**
 *
 * @author elialva
 */
public class ImageFile {

    private final ByteArrayInputStream bais;
    private final String file;
    private BufferedImage image;

    public ImageFile(ByteArrayInputStream bais, String file) {
        this.bais = bais;
        this.file = file;
    }

    /**
     * @return the bais
     */
    public ByteArrayInputStream getBais() {
        return bais;
    }

    /**
     * @return the file
     */
    public String getFile() {
        return file;
    }

    /**
     * @return the image
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(BufferedImage image) {
        this.image = image;
    }

}
