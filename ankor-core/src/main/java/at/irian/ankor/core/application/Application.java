package at.irian.ankor.core.application;

import at.irian.ankor.core.el.BeanResolver;
import at.irian.ankor.core.el.ELSupport;
import at.irian.ankor.core.el.StandardELContext;
import at.irian.ankor.core.listener.ListenerRegistry;
import at.irian.ankor.core.action.method.MethodExecutor;
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
    private final BeanResolver beanResolver;
    private final ModelChangeNotifier modelChangeNotifier;
    private final ELSupport elSupport;

    public Application(Class<?> modelType, BeanResolver beanResolver) {
        this.beanResolver = beanResolver;
        this.listenerRegistry = new ListenerRegistry();
        this.modelHolder = new ModelHolder(modelType);
        ExpressionFactory expressionFactory = ExpressionFactory.newInstance();
        StandardELContext standardELContext = new StandardELContext();
        this.elSupport = new ELSupport(expressionFactory, standardELContext);
        this.modelChangeNotifier = new ModelChangeNotifier(listenerRegistry);
        this.refFactory = new RefFactory(expressionFactory,
                                         standardELContext,
                                         modelChangeNotifier,
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

    public BeanResolver getBeanResolver() {
        return beanResolver;
    }

    public ModelChangeNotifier getModelChangeNotifier() {
        return modelChangeNotifier;
    }

    public ELSupport getELSupport() {
        return elSupport;
    }
}
