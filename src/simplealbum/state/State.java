/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simplealbum.state;

/**
 * State debe aceptar ruido, pueden llamarse, aún sin sentido, todos los
 * métodos, sin que haya efectos colaterales.
 *
 * @author elialva
 */
public interface State {

    /**
     * Cuando se efectua una búsqueda. Cuando la llave está informada y se
     * registra un Enter o se presiona un botón de búsqueda o se pierde el foco
     */
    public void search();

    /**
     * Cuando se presiona cualquier tecla. Podría llamarse inmediatamente a
     * search y estar solo un instante en CapturingState
     */
    public void capture();

    /**
     * Cuando se presiona Inserta, Actualiza, Borra o Cancela
     *
     * @param accion
     */
    public void commit(int accion);
}
