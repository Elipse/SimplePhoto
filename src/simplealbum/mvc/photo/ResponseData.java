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
public class ResponseData {

    private final BufferedImage imageSmall;
    private final BufferedImage imageBig;
    private final Object synch;
    private final String type;
    private String file;
    private final ByteArrayInputStream bais;
    private final int index;

    ResponseData(int index, BufferedImage small, BufferedImage big, Object synch, String type, String file, ByteArrayInputStream bais) {

        this.index = index;
        this.imageSmall = small;
        this.imageBig = big;
        this.synch = synch;
        this.type = type;
        this.file = file;
        this.bais = bais;
    }

    /**
     * @return the imageSmall
     */
    public BufferedImage getImageSmall() {
        return imageSmall;
    }

    /**
     * @return the imageBig
     */
    public BufferedImage getImageBig() {
        return imageBig;
    }

    /**
     * @return the synch
     */
    public Object getSynch() {
        return synch;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the file
     */
    public String getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * @return the bais
     */
    public ByteArrayInputStream getBais() {
        return bais;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }
}
