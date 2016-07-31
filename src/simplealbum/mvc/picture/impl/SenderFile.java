/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.picture.impl;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.imgscalr.Scalr;
import static org.imgscalr.Scalr.resize;
import simplealbum.mvc.photo.ImageFile;
import simplealbum.mvc.photo.Sender;

public class SenderFile implements Sender {

    private Collection<File> listFiles;
    private int index;
    public Object[] toArray;
    private int c;
    private final String dir;
    public static final String DIR_DEFAULT = "C:\\Users\\IBM_ADMIN\\Pictures\\X_20150119_MotoG\\Camera";
    private final int num;

    public SenderFile() {
        this.dir = DIR_DEFAULT;
        listFiles = FileUtils.listFiles(new File(dir), new String[]{"jpg"}, true);
        toArray = listFiles.toArray();
        num = toArray.length;
        System.out.println("Talla " + toArray.length);
        index = 0;
    }

    @Override
    public ImageFile convey() {
        try {
            long time = System.currentTimeMillis();
            listFiles = FileUtils.listFiles(new File(DIR_DEFAULT), new String[]{"jpg"}, true);
//            System.out.println("List file se lleva   :  " + (System.currentTimeMillis() - time));
            toArray = listFiles.toArray();

            if (index >= toArray.length) {
                return null;
            }

            File file = (File) toArray[index++];
            if (file.length() == 0) {
                System.out.println("PicVacia");
                return null;
            }

            c++;

            ByteArrayInputStream bais = new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
            ImageFile imageFile = new ImageFile(bais, file.getName());
            return imageFile;
        } catch (IOException ex) {
            Logger.getLogger(SenderFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("FileNULL ");
        return null;
    }

    public BufferedImage scale(BufferedImage image, int length) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (width > height) {
            height = length * height / width;
            width = length;
        } else {
            width = length * width / height;
            height = length;
        }
        return resize(image, Scalr.Method.AUTOMATIC, width, height);
    }

    public static void main(String[] args) throws IOException {
        SenderFile sf = new SenderFile();
        for (int i = 0; i < sf.toArray.length; i++) {
            File file = (File) sf.toArray[i];
            try {
                Image scaledInstance = ImageIO.read(file).getScaledInstance(36, 27, Image.SCALE_FAST);
//                ImageIcon imageIcon = new ImageIcon(scaledInstance);
            } catch (Exception e) {
                System.out.println("Cahcead " + file.getName());
            }
            System.out.println("i " + i + " " + file.getName() + " size: " + file.length()
                    + " M: " + Runtime.getRuntime().totalMemory());
        }
    }

}
