package at.irian.ankor.switching.handler;

/**
 *
 *
 *
 * @author Manfred Geiler
 */
public interface CloseHandler<P> {

    void closeConnector(P party);

}
