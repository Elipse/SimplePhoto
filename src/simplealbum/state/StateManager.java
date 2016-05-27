/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.state;

/**
 *
 * @author elialva
 */
public class StateManager implements State {

    public static final int APPLY = 1;
    public static final int CANCEL = 2;
    public static final int DELETE = 3;
    State waitingState;
    State capturingState;
    State updatingState;
    State insertingState;
    private State state;
    GraphicInterface screen;

    public StateManager(GraphicInterface screen) {
        this.screen = screen;
        waitingState = new WaitingState(this);
        capturingState = new CapturingState(this);
        updatingState = new UpdatingState(this);
        insertingState = new InsertingState(this);
        state = waitingState;
        this.screen.configWaitingScreen();
    }

    void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    @Override
    public void search() {
        state.search();
    }

    @Override
    public void capture() {
        state.capture();
    }

    @Override
    public void commit(int accion) {
        state.commit(accion);
    }
}
