/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.autocomplete;

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author elialva
 */
public class ACModel extends AbstractListModel<String> {

    private List<String> list = new ArrayList<>();

    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public String getElementAt(int index) {
        return list.get(index);
    }

    void setList(List<String> list) {
        this.list = list;
        fireContentsChanged(list, 0, list.size() - 1);
    }

}
