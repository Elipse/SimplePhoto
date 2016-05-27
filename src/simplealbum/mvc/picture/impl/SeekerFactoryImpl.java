/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.picture.impl;

import simplealbum.mvc.autocomplete.Seeker;
import simplealbum.mvc.autocomplete.SeekerFactory;

/**
 *
 * @author elialva
 */
public class SeekerFactoryImpl extends SeekerFactory {

    @Override
    public Seeker retrieveSeeker(String type) {
        switch (type) {
            case "_NAME$TXT":
                System.out.println("SeekerName");
                return new SeekerName();
            case "_TIPO$TXT":
                System.out.println("SeekerTipo");
                return new SeekerTipo();
            default:
                throw new AssertionError();
        }
    }
}
