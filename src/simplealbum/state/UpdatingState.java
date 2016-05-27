/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.state;

/**
 *
 * @author elialva
 */
public class UpdatingState implements State {

    private final StateManager stateManager;
    private final GraphicInterface screen;

    UpdatingState(StateManager stateManager) {
        this.stateManager = stateManager;
        this.screen = stateManager.screen;
    }

    @Override
    public void search() {
        System.out.println("UpdatingState: Se ignora petición - search.");
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
                /**
                 * Llama a actualizaProd() (valida inputs antes)
                 */
                screen.updateObject();
                break;
            case StateManager.DELETE:
                screen.deleteObject();
                break;
            default:
        }

        stateManager.setState(stateManager.waitingState);
        screen.configWaitingScreen();
    }
}
