package at.irian.ankor.application;

import at.irian.ankor.ref.RefFactory;
import com.typesafe.config.Config;

/**
 * @author Manfred Geiler
 */
public abstract class Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Application.class);

    private final Config config;
    private final ListenerRegistry listenerRegistry;
    private final ModelHolder modelHolder;

    public Application(Class<?> modelType, Config config) {
        this.config = config;
        this.listenerRegistry = new ListenerRegistry();
        this.modelHolder = new ModelHolder(modelType);
    }

    public ModelHolder getModelHolder() {
        return modelHolder;
    }


    public ListenerRegistry getListenerRegistry() {
        return listenerRegistry;
    }

    public Config getConfig() {
        return config;
    }

    public abstract RefFactory getRefFactory();
}
