/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import static java.util.UUID.randomUUID;
import javax.imageio.ImageIO;
//import org.apache.commons.imaging.ColorTools;

/**
 *
 * @author elialva
 */
public class ImageTest {

    public static void main(String[] args) {
        System.out.println("UUID 1 " + randomUUID());
        System.out.println("UUID 2 " + randomUUID());
        System.exit(0);
        try {

            BufferedImage originalImage
                    = ImageIO.read(new File("C:\\Users\\IBM_ADMIN\\Desktop\\BorraPics\\Read\\b1.jpg"));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "bmp", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            ImageIO.write(originalImage, "jpg", new File("C:\\Users\\IBM_ADMIN\\Desktop\\BorraPics\\Read\\bb1.jpg"));

//            ColorTools d = new ColorTools();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
