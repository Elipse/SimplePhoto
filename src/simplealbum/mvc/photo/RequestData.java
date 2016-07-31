/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.photo;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

/**
 *
 * @author elialva
 */
public class RequestData {

    private BufferedImage image;
    private Object synch;
    private Dimension dimensionSmall;
    private Dimension dimensionBig;
    private final String type;
    private String file;

    public static final String STREAMING = "Streaming";
    public static final String AMPLIFING = "Amplifing";
    private ByteArrayInputStream bais;
    private int index;

    public RequestData(String type) {
        this.type = type;
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

    /**
     * @return the synch
     */
    public Object getSynch() {
        return synch;
    }

    /**
     * @param synch the synch to set
     */
    public void setSynch(Object synch) {
        this.synch = synch;
    }

    /**
     * @return the dimensionSmall
     */
    public Dimension getDimensionSmall() {
        return dimensionSmall;
    }

    /**
     * @param dimensionSmall the dimensionSmall to set
     */
    public void setDimensionSmall(Dimension dimensionSmall) {
        this.dimensionSmall = dimensionSmall;
    }

    /**
     * @return the dimensionBig
     */
    public Dimension getDimensionBig() {
        return dimensionBig;
    }

    /**
     * @param dimensionBig the dimensionBig to set
     */
    public void setDimensionBig(Dimension dimensionBig) {
        this.dimensionBig = dimensionBig;
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

    ByteArrayInputStream getBais() {
        return this.bais;
    }

    void setBais(ByteArrayInputStream bais) {
        this.bais = bais;
    }

    void setIndex(int indexOf) {
        this.index = indexOf;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

}
