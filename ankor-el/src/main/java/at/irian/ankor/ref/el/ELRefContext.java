package at.irian.ankor.ref.el;

import at.irian.ankor.context.ModelHolder;
import at.irian.ankor.el.ModelContextELResolver;
import at.irian.ankor.el.ModelHolderELResolver;
import at.irian.ankor.el.ModelRootELResolver;
import at.irian.ankor.el.StandardELContext;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.path.el.ELPathSyntax;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.ref.impl.RefContextImplementor;
import com.typesafe.config.Config;

import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import java.util.Iterator;

/**
 * @author Manfred Geiler
 */
public class ELRefContext implements RefContext, RefContextImplementor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELRefContext.class);

    private final ExpressionFactory expressionFactory;
    private final StandardELContext elContext;
    private final Config config;
    private final String modelRootVarName;
    private final String modelContextPath;
    private final EventListeners globalEventListeners;
    private final ELRefFactory refFactory;
    private final ModelHolder modelHolder;
    private final MessageSender messageSender;

    ELRefContext(ExpressionFactory expressionFactory,
                         StandardELContext elContext,
                         Config config,
                         EventListeners globalEventListeners,
                         String modelContextPath,
                         ModelHolder modelHolder, MessageSender messageSender) {
        this.expressionFactory = expressionFactory;
        this.elContext = elContext;
        this.config = config;
        this.modelHolder = modelHolder;
        this.messageSender = messageSender;
        this.modelRootVarName = config.getString("ankor.variable-names.modelRoot");
        this.modelContextPath = modelContextPath;
        this.globalEventListeners = globalEventListeners;
        this.refFactory = new ELRefFactory(this);
    }

    @Deprecated //todo weg
    public static ELRefContext create(ExpressionFactory expressionFactory,
                                      StandardELContext baseELContext,
                                      Config config,
                                      ModelHolder modelHolder,
                                      EventListeners globalEventListeners,
                                      MessageSender messageSender) {
        StandardELContext elContext = baseELContext.withAdditional(new ModelRootELResolver(config, modelHolder))
                                                   .withAdditional(new ModelHolderELResolver(config, modelHolder));
        return new ELRefContext(expressionFactory, elContext, config, globalEventListeners, null,
                                modelHolder, messageSender);
    }

    @Override
    public MessageSender messageSender() {
        return messageSender;
    }

    @Override
    public ELRefContext withMessageSender(MessageSender newMessageSender) {
        return new ELRefContext(expressionFactory, elContext, config, globalEventListeners, modelContextPath, modelHolder, newMessageSender);
    }

    @Override
    public RefFactory refFactory() {
        return refFactory;
    }

    ExpressionFactory getExpressionFactory() {
        return expressionFactory;
    }

    public StandardELContext getElContext() {
        return elContext;
    }

    @Override
    public EventListeners modelEventListeners() {
        return modelHolder.getModelEventListeners();
    }

    @Override
    public EventListeners globalEventListeners() {
        return globalEventListeners;
    }

    @Override
    public Iterable<ModelEventListener> allEventListeners() {
        return new Iterable<ModelEventListener>() {
            @Override
            public Iterator<ModelEventListener> iterator() {

                final Iterator<ModelEventListener> globalIterator = globalEventListeners().iterator();
                final Iterator<ModelEventListener> modelIterator = modelEventListeners().iterator();

                return new Iterator<ModelEventListener>() {

                    private int internalState = 0;

                    @Override
                    public boolean hasNext() {
                        return globalIterator.hasNext() || modelIterator.hasNext();
                    }

                    @Override
                    public ModelEventListener next() {
                        if (globalIterator.hasNext()) {
                            internalState = 1;
                            return globalIterator.next();
                        } else {
                            internalState = 2;
                            return modelIterator.next();
                        }
                    }

                    @Override
                    public void remove() {
                        if (internalState == 1) {
                            internalState = 0;
                            globalIterator.remove();
                        } else if (internalState == 2) {
                            internalState = 0;
                            modelIterator.remove();
                        } else {
                            throw new IllegalStateException();
                        }
                    }
                };
            }
        };
    }

    @Override
    public EventListeners eventListeners() {
        return modelEventListeners();
    }

    String getModelRootVarName() {
        return modelRootVarName;
    }

    @Override
    public PathSyntax pathSyntax() {
        return ELPathSyntax.getInstance();
    }

    @Override
    public String getModelContextPath() {
        return modelContextPath;
    }

    public ELRefContext withModelContextPath(String modelContextPath) {
        return withAdditionalELResolver(new ModelContextELResolver(expressionFactory,
                                                                   config,
                                                                   modelContextPath,
                                                                   refFactory));
    }

    public ELRefContext withAdditionalELResolver(ELResolver elResolver) {
        return new ELRefContext(expressionFactory,
                                elContext.withAdditional(elResolver),
                                config, globalEventListeners, modelContextPath, modelHolder, messageSender);
    }

}
