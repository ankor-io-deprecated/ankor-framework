package at.irian.ankor.core.application;

import at.irian.ankor.core.el.StandardELContext;
import at.irian.ankor.core.listener.ListenerRegistry;
import at.irian.ankor.core.model.ModelHolder;
import at.irian.ankor.core.ref.RefFactory;

import javax.el.ExpressionFactory;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Application.class);

    private final ListenerRegistry listenerRegistry;
    private final ModelHolder modelHolder;
    private final RefFactory refFactory;

    public Application(Class<?> modelType) {
        this.listenerRegistry = new ListenerRegistry();
        this.modelHolder = new ModelHolder(modelType);
        this.refFactory = new RefFactory(ExpressionFactory.newInstance(),
                                         new StandardELContext(),
                                         new ModelChangeWatcher(listenerRegistry),
                                         new ModelActionBus(listenerRegistry),
                                         modelHolder);
    }

    public ModelHolder getModelHolder() {
        return modelHolder;
    }

    public RefFactory getRefFactory() {
        return refFactory;
    }

    public ListenerRegistry getListenerRegistry() {
        return listenerRegistry;
    }
}
