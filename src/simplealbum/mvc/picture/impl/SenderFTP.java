/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.picture.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        ftpClient.connect("192.168.1.67", 2151);
        ftpClient.login("eam", "qaz");
        ftpClient.enterLocalPassiveMode();
        ftpClient.setKeepAlive(true);
        logPath = "C:\\Users\\IBM_ADMIN\\Documents\\@Eli\\201506 SimpleCatalog\\SimpleCat\\build\\classes\\resource\\log.txt";
        listLog = new ArrayList(FileUtils.readLines(new File(logPath)));
        listConveyed = new ArrayList();
        Object[] toArray = FileUtils.readLines(new File(logPath)).toArray();
        Arrays.sort(toArray);
        System.out.println("Logdd " + logPath);

    }

    @Override
    public ImageFile convey() {
        try {
            String next = listPending().remove(0);
            InputStream is = retrieveFileInputStream(next);
            Picture picture = new Picture();
            picture.setOriginal(IOUtils.toByteArray(is));
            listConveyed.add(next);
            ByteArrayInputStream bais = new ByteArrayInputStream(IOUtils.toByteArray(is));
            return new ImageFile(bais, next);
        } catch (IOException ex) {
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
        listPending.removeAll(listLog);

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
    }
}
