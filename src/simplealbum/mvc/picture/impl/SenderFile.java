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
import simplealbum.mvc.photo.Sender;

public class SenderFile implements Sender {

    private final Collection<File> listFiles;
    private int index;
    public final Object[] toArray;
    private int c;

    public SenderFile() {

//        listFiles = FileUtils.listFiles(new File("C:\\Users\\IBM_ADMIN\\Pictures\\Tester"), new String[]{"jpg"}, true);
//        listFiles = FileUtils.listFiles(new File("C:\\Users\\IBM_ADMIN\\Pictures\\EliD680 2015-03-29"), new String[]{"jpg"}, true);
        listFiles = FileUtils.listFiles(new File("C:\\Users\\IBM_ADMIN\\Pictures\\X_20150119_MotoG\\Camera"), new String[]{"jpg"}, true);

        toArray = listFiles.toArray();
        System.out.println("Talla " + toArray.length);
        index = 0;
    }

    @Override
    public ByteArrayInputStream convey() {
        try {
            if (index >= toArray.length) {
                System.out.println("Nada que procesar");
                try {
                    System.out.println("Enviados " + c);
                    System.out.println("Total memory (bytes): " + Runtime.getRuntime().totalMemory());
                    Thread.sleep(19000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SenderFile.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.exit(0);
            }
            File file = (File) toArray[index++];
            if (file.length() == 0) {
                System.out.println("PicVacia");
                return null;
            }
//            Image read = ImageIO.read(file).getScaledInstance(36, 27, Image.SCALE_FAST);
//            BufferedImage original = ImageIO.read(file);
            //
//            Picture picture = new Picture(UUID.randomUUID().toString());
//            picture.setOriginal();
            //
//            System.out.println("FileXXX " + file);
            c++;
            return new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
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
