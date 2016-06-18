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

    private long name = 0;
    private final SeekerName byName;
    private final SeekerNameL byNameL;

    public SeekerFactoryImpl() {
        byName = new SeekerName();
        byNameL = new SeekerNameL();
    }

    @Override
    public Seeker retrieveNextSeeker(String type) {
        switch (type) {
            case "_NAME$TXT":
                name++;
                return name % 2 == 0 ? byName : byNameL;
            case "_TIPO$TXT":
                System.out.println("SeekerTipo");
                return new SeekerTipo();
            default:
                throw new AssertionError();
        }
    }

    @Override
    public Seeker retrieveSeeker(String type) {
        switch (type) {
            case "_NAME$TXT":
                name = 0;
                return byName;
            case "_TIPO$TXT":
                return new SeekerTipo();
            default:
                throw new AssertionError();
        }
    }
}
