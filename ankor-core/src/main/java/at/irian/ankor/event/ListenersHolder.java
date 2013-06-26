package at.irian.ankor.event;

import java.util.List;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface ListenersHolder {

    void addListener(ModelEventListener listener);
    void removeListener(ModelEventListener listener);
    List<ModelEventListener> getListeners();
    void cleanupListeners();

}
