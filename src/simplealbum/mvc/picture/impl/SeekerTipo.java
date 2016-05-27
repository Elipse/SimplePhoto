/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.picture.impl;

import java.util.logging.Level;
import java.util.logging.Logger;
import simplealbum.mvc.autocomplete.InfoPage;
import simplealbum.mvc.autocomplete.Seeker;

/**
 *
 * @author elialva
 */
public class SeekerTipo implements Seeker {

    public SeekerTipo() {
    }

    @Override
    public InfoPage command(String lastRequest) {

        System.out.println("command e nseeker");
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(Seeker.class.getName()).log(Level.SEVERE, null, ex);
        }
        InfoPage infoPage = new InfoPage();
        infoPage.setQuery(lastRequest);
        return infoPage;
    }

}
