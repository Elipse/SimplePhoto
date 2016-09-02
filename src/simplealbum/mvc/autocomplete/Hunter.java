/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.autocomplete;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elialva
 */
public class Hunter {

    List<String> listOfWords;
    private String hint;

    public List<String> getListOfWords(String hint) {
        this.hint = hint;
        listOfWords = Arrays.asList("Como", "Tu", "La Vez?");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Hunter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listOfWords;
    }
}
