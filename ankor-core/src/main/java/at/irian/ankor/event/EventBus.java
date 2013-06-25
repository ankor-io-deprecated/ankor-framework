package at.irian.ankor.event;

import java.util.List;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface EventBus {

    void addListener(ModelEventListener listener);
    void removeListener(ModelEventListener listener);
    List<ModelEventListener> getListeners();
    void cleanupListeners();

    void fire(ModelEvent event);

}
