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
public class InfoPage {

    private String query;

    @Override
    public String toString() {
        return "Yo soy un InfoPage " + this.hashCode();
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
