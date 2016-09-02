/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.picture.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import simplealbum.entities.Picture;
import simplealbum.mvc.photo.ImageFile;
import simplealbum.mvc.photo.Sender;

/**
 *
 * @author elialva
 */
public class SenderFTP implements Sender {

    private final FTPClient ftpClient;
    private final String logPath;
    private final ArrayList listLog;
    private final ArrayList listConveyed;

    public SenderFTP() throws IOException {
        ftpClient = new FTPClient();
//        ftpClient.connect("192.168.1.68", 2151);
        ftpClient.connect("10.64.112.191", 2151);
        ftpClient.login("eam", "qaz");
        ftpClient.enterLocalPassiveMode();
        ftpClient.setKeepAlive(true);
        logPath = "C:\\Users\\IBM_ADMIN\\Documents\\@EAM\\201506 SimpleCatalog\\SimpleCat\\build\\classes\\resource\\log.txt";
        listLog = new ArrayList(FileUtils.readLines(new File(logPath)));
        listConveyed = new ArrayList();
        Object[] toArray = FileUtils.readLines(new File(logPath)).toArray();
        Arrays.sort(toArray);
        System.out.println("Logdd " + logPath);

    }

    @Override
    public ImageFile convey() {
        System.out.println("Llamando convey " + System.currentTimeMillis());

        try {
            if (listPending().size() == 0) {
                return null;
            }
            String next = listPending().remove(0);
//            System.out.println("nexta " + next);
//            InputStream is = retrieveFileInputStream(next);
//            System.out.println("isExxon " + is.read());
//            Picture picture = new Picture();
//            picture.setOriginal(IOUtils.toByteArray(is));
            listConveyed.add(next);
            System.out.println("Nexto " + next);
//            ByteArrayInputStream bais = new ByteArrayInputStream(IOUtils.toByteArray(is));
//            bais.close();

            ByteArrayOutputStream bais2 = new ByteArrayOutputStream();
            System.out.println("Antes de retrive");
            ftpClient.retrieveFile(next, bais2);
            System.out.println("DEepses de retrive");
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bais2.toByteArray()));
//            System.out.println("image " + image);
            return new ImageFile(new ByteArrayInputStream(bais2.toByteArray()), next);
        } catch (Exception ex) {
            System.out.println("Sender Ex ? " + ex);
            Logger.getLogger(SenderFTP.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<String> listPending() throws IOException {
        //Manejar en memoria el log, manejar string[] en listnames
        List<String> listDir = Arrays.asList(ftpClient.listNames());
        ArrayList listPending = new ArrayList();
        for (String listName : listDir) {
            if (!listName.contains(":")
                    && !listName.contains("~")
                    && listName.endsWith(".jpg")) {
                listPending.add(listName);
            }
        }
        listPending.removeAll(listConveyed);
        System.out.println("lis " + listPending.size());
        Collections.sort(listPending);
        return listPending;
    }

    public InputStream retrieveFileInputStream(String remote) {
        try {
            InputStream retrieveFileStream = ftpClient.retrieveFileStream(remote);
            InputStream toBufferedInputStream = IOUtils.toBufferedInputStream(retrieveFileStream);
            retrieveFileStream.close();
            System.out.println("ftpClient.completePendingCommand(): " + ftpClient.completePendingCommand());
            return toBufferedInputStream;
        } catch (IOException ex) {
            Logger.getLogger(SenderFTP.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void stop() throws IOException {
        FileUtils.writeLines(new File(logPath), listLog);
    }

    public static void main(String[] args) throws IOException {
        SenderFTP tf = new SenderFTP();
        tf.convey();
    }
}
