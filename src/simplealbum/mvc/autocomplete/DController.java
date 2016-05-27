/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.mvc.autocomplete;

import java.awt.Frame;
import java.awt.Point;

/**
 *
 * @author elialva
 */
public class DController {

    private final DView view;
    private final DModel model;

    private final SeekerFactory factory;

    private String owner;
    private Seeker seeker;

    public static DController newInstance(Frame frame, SeekerFactory factory) {
        DView view = new DView(frame, true);
        DModel model = new DModel();
        return new DController(view, model, factory);
    }
    private Controller masterController;

    private DController(DView view, DModel model, SeekerFactory factory) {
        this.view = view;
        this.view.setDController(DController.this);
        this.model = model;
        this.model.setDController(DController.this);
        this.model.execute();

        this.factory = factory;
    }

    public void request(String query) {
        System.out.println("Solicitó  " + System.currentTimeMillis());
        model.request(query);
    }

    public void response(InfoPage infoPage) {
        System.out.println("Respondió " + System.currentTimeMillis());
//        view.response();
    }

    String getAction() {
        return view.getAction();
    }

    void setAction(String string) {
        view.setAction(string);
    }

    boolean isShowing() {
        return view.isShowing();
    }

    void setLocation(Point locationOnScreen) {
        view.setLocation(locationOnScreen);
    }

    void setVisible(boolean b) {
        view.setVisible(b);
    }

    void setOwner(String owner) {
        this.owner = owner;

        view.setTitle(owner);
        model.setSeeker(factory.retrieveSeeker(owner));
    }

    void selectionDown() {
        int selectedRow = view.getSelectedRow() + 1;
        selectedRow = selectedRow > view.getRowCount() - 1 ? 0 : selectedRow;
        view.setRowSelectionInterval(selectedRow, selectedRow);
    }

    void selectionUp() {
        int selectedRow = view.getSelectedRow() - 1;
        selectedRow = selectedRow < 0 ? view.getRowCount() - 1 : selectedRow;
        view.setRowSelectionInterval(selectedRow, selectedRow);
    }

    void cancel() {
        System.out.println("Cancelando captura " + masterController);
    }

    void setController(Controller controller) {
        this.masterController = controller;
    }

    void processSelection() {
        System.out.println("Se diao enter procesa selection del dialog para jbeans");

    }

}
