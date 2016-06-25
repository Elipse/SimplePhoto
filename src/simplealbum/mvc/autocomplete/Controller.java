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
public class Controller {

    private final View view;
    private final DController dController;

    Controller(View view, SeekerFactory factory) {
        this.dController = DController.newInstance(view.getFrame(), factory);
        this.dController.setController(Controller.this);

        this.view = view;
        this.view.setController(Controller.this);
        this.view.setDController(dController);
    }

    void changedId(String focusOwner, int id) {
        System.out.println("Master Controller " + "_ID$" + focusOwner + ": " + id);
    }
}
