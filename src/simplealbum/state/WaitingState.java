/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.state;

/**
 *
 * @author elialva
 */
public class WaitingState implements State {

    private final StateManager stateManager;
    private final GraphicInterface screen;

    WaitingState(StateManager stateManager) {
        this.stateManager = stateManager;
        this.screen = stateManager.screen;
    }

    @Override
    public void search() {
        System.out.println("WaitingState: Se ignora petición - search.");
    }

    @Override
    public void capture() {
        stateManager.setState(stateManager.capturingState);
        screen.configCapturingScreen();
    }

    @Override
    public void commit(int action) {
        if (action == StateManager.CANCEL) {
            screen.configWaitingScreen();
        } else {
            System.out.println("WaitingState: Se ignora petición - commit.");
        }
    }
}
