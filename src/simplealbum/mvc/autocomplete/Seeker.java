/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.autocomplete;

import java.awt.Color;
import java.util.List;

/**
 *
 * @author elialva
 */
public interface Seeker {

    InfoPage command(String query);

    InfoPage command(InfoPage infoPage);

    List<String> getColors();

    String getFilter();

    String getTitle();
}
