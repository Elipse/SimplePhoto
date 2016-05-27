/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.state;

/**
 *
 * @author elialva
 */
public class CapturingState implements State {

    private final StateManager stateManager;
    private final GraphicInterface screen;

    CapturingState(StateManager stateManager) {
        this.stateManager = stateManager;
        this.screen = stateManager.screen;
    }

    @Override
    public void search() {
        if (screen.selectObject()) {
            stateManager.setState(stateManager.updatingState);
            screen.configUpdatingScreen();
        } else {
            stateManager.setState(stateManager.insertingState);
            screen.configInsertingScreen();
        }
    }

    @Override
    public void capture() {
        System.out.println("CapturingState: Se ignora petición - capture.");
    }

    @Override
    public void commit(int action) {
        if (action == StateManager.CANCEL) {
            stateManager.setState(stateManager.waitingState);
            screen.configWaitingScreen();
        } else {
            System.out.println("CapturingState: Se ignora petición - commit.");
        }
    }
}
