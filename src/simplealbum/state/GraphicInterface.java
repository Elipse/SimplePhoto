/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.state;

/**
 *
 * @author elialva
 */
public interface GraphicInterface {

    public boolean selectObject();

    public void insertObject();

    public void updateObject();

    public void deleteObject();

    public void configWaitingScreen();

    public void configCapturingScreen();

    public void configInsertingScreen();

    public void configUpdatingScreen();
}
