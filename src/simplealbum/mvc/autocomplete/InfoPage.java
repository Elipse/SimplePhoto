/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.autocomplete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author elialva
 */
public class InfoPage {

    private String query;
    private List<List<String>> list;
    private int direction;

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

    public List<List<String>> getRows() {
        return list;
    }

    public void setData(List<List<String>> list) {
        this.list = list;
    }

    void setDirection(int direction) {
        this.direction = direction;
    }

    /**
     * @return the direction
     */
    public int getDirection() {
        return direction;
    }
}
