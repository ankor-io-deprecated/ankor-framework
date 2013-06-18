package at.irian.ankor.core.application;

import at.irian.ankor.core.el.BeanResolver;
import at.irian.ankor.core.el.StandardELContext;
import at.irian.ankor.core.listener.ListenerRegistry;
import at.irian.ankor.core.method.MethodExecutor;
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
    private final MethodExecutor methodExecutor;
    private final BeanResolver beanResolver;
    private final ModelChangeWatcher modelChangeWatcher;

    public Application(Class<?> modelType, BeanResolver beanResolver) {
        this.beanResolver = beanResolver;
        this.listenerRegistry = new ListenerRegistry();
        this.modelHolder = new ModelHolder(modelType);
        ExpressionFactory expressionFactory = ExpressionFactory.newInstance();
        StandardELContext standardELContext = new StandardELContext();
        this.modelChangeWatcher = new ModelChangeWatcher(listenerRegistry);
        this.refFactory = new RefFactory(expressionFactory,
                                         standardELContext,
                                         modelChangeWatcher,
                                         new ModelActionBus(listenerRegistry),
                                         modelHolder);
        this.methodExecutor = new MethodExecutor(expressionFactory, standardELContext, modelHolder, beanResolver);
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

    public MethodExecutor getMethodExecutor() {
        return methodExecutor;
    }

    protected BeanResolver getBeanResolver() {
        return beanResolver;
    }

    public ModelChangeWatcher getModelChangeWatcher() {
        return modelChangeWatcher;
    }
}
