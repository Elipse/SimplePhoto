/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.state;

/**
 *
 * @author elialva
 */
public class InsertingState implements State {

    private final StateManager stateManager;
    private final GraphicInterface screen;

    public InsertingState(StateManager stateManager) {
        this.stateManager = stateManager;
        this.screen = stateManager.screen;
    }

    @Override
    public void search() {
        System.out.println("InsertingState: Se ignora petición - search.");
    }

    @Override
    public void capture() {
        stateManager.setState(stateManager.capturingState);
        screen.configCapturingScreen();
    }

    @Override
    public void commit(int action) {
        switch (action) {
            case StateManager.APPLY:
                /* Llama a insertaProd() (valida inputs antes) */
                screen.insertObject();
                break;
            /* Any other action gets it back to WaitingState. */
            default:
        }

        stateManager.setState(stateManager.waitingState);
        screen.configWaitingScreen();
    }
}
