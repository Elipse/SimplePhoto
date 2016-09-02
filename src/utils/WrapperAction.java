/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.beans.PropertyChangeListener;
import javax.swing.Action;

/**
 *
 * @author elialva
 */
public abstract class WrapperAction implements Action {

    protected Action originalAction;

    public void setOriginalAction(Action originalAction) {
        this.originalAction = originalAction;
    }

    @Override
    public Object getValue(String key) {
        return originalAction.getValue(key);
    }

    @Override
    public void putValue(String key, Object value) {
        originalAction.putValue(key, value);
    }

    @Override
    public void setEnabled(boolean b) {
        originalAction.setEnabled(b);
    }

    @Override
    public boolean isEnabled() {
        return originalAction.isEnabled();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        originalAction.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        originalAction.addPropertyChangeListener(listener);
    }
}
