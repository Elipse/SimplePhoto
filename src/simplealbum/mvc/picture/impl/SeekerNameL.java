/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.picture.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import simplealbum.mvc.autocomplete.InfoPage;
import simplealbum.mvc.autocomplete.Seeker;

/**
 *
 * @author elialva
 */
public class SeekerNameL implements Seeker {

    private int mode;
    private List<List<String>> list;

    public SeekerNameL() {
    }

    @Override
    public InfoPage command(String lastRequest) {
//        System.out.println("command e nseeker");
        try {
            Thread.sleep(0500);
        } catch (InterruptedException ex) {
            Logger.getLogger(Seeker.class.getName()).log(Level.SEVERE, null, ex);
        }
        InfoPage infoPage = new InfoPage();
        infoPage.setQuery(lastRequest);

        list = new ArrayList();
        list.add(Arrays.asList("11", "Poliza", "\\tiny\\images\\y34535.jpg"));
        list.add(Arrays.asList("22", "Cobertura", "\\tiny\\images\\l132.jpg"));
        list.add(Arrays.asList("33", "Objecto", "\\tiny\\images\\qv5.jpg"));
        list.add(Arrays.asList("44", "Primas", "\\tiny\\images\\nf3.jpg"));
        infoPage.setData(list);

        return infoPage;
    }

    @Override
    public InfoPage command(InfoPage infoPage) {
        System.out.println("PAGINANDO EN NOMBRE LARGO " + infoPage.getDirection());
        return infoPage;
    }

    @Override
    public List<String> getColors() {

        ArrayList<String> l = new ArrayList();
        l.add("Lust");
        l.add("Lumber");
        l.add("Livid");
        return l;
    }

    @Override
    public String getFilter() {
        return "^[\\d\\s]*$";
    }

    @Override
    public String getTitle() {
        return "Busqueda por nombre largo.";
    }

}
