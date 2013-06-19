package at.irian.ankor.core.application;

import at.irian.ankor.core.el.*;
import at.irian.ankor.core.listener.ListenerRegistry;
import at.irian.ankor.core.ref.RefFactory;
import at.irian.ankor.core.ref.el.ELRefFactory;

import javax.el.ELContext;
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
    private final ELSupport elSupport;

    public Application(Class<?> modelType, BeanResolver beanResolver) {
        this.beanResolver = beanResolver;
        this.listenerRegistry = new ListenerRegistry();
        this.modelHolder = new ModelHolder(modelType);
        this.elSupport = createELSupport(beanResolver, modelHolder);
        this.refFactory = new ELRefFactory(elSupport,
                                           new DefaultChangeNotifier(listenerRegistry),
                                           new DefaultActionNotifier(listenerRegistry));
    }

    private ELSupport createELSupport(BeanResolver beanResolver, ModelHolder modelHolder) {
        ExpressionFactory expressionFactory = ExpressionFactory.newInstance();
        ELContext standardELContext = new StandardELContext();
        ELContext beanELContext = new BeanResolverELContext(expressionFactory, standardELContext, beanResolver);
        ELContext baseELContext = new ModelHolderELContext(expressionFactory, beanELContext, modelHolder);
        return new ELSupport(expressionFactory, baseELContext);
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

    public ELSupport getELSupport() {
        return elSupport;
    }
}
