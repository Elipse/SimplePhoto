/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.autocomplete;

/**
 *
 * @author elialva
 */
public abstract class SeekerFactory {

    public abstract Seeker retrieveNextSeeker(String type);

    public abstract Seeker retrieveSeeker(String type);

}
