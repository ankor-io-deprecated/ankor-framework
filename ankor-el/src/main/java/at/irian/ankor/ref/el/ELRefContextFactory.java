package at.irian.ankor.ref.el;

import at.irian.ankor.context.ModelHolder;
import at.irian.ankor.el.ModelHolderELResolver;
import at.irian.ankor.el.ModelRootELResolver;
import at.irian.ankor.el.StandardELContext;
import at.irian.ankor.event.EventDelaySupport;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefContextFactory;
import com.typesafe.config.Config;

import javax.el.ExpressionFactory;

/**
 * @author Manfred Geiler
 */
public class ELRefContextFactory implements RefContextFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELRefContextFactory.class);

    private final Config config;
    private final ModelHolder modelHolder;
    private final ExpressionFactory expressionFactory;
    private final StandardELContext baseELContext;
    private final EventListeners globalEventListeners;
    private final MessageSender messageSender;
    private final EventDelaySupport eventDelaySupport;

    public ELRefContextFactory(Config config,
                               ModelHolder modelHolder,
                               ExpressionFactory expressionFactory,
                               StandardELContext baseELContext,
                               EventListeners globalEventListeners,
                               MessageSender messageSender,
                               EventDelaySupport eventDelaySupport) {
        this.config = config;
        this.modelHolder = modelHolder;
        this.expressionFactory = expressionFactory;
        this.baseELContext = baseELContext;
        this.globalEventListeners = globalEventListeners;
        this.messageSender = messageSender;
        this.eventDelaySupport = eventDelaySupport;
    }

    @Override
    public RefContext create() {
        StandardELContext elContext = baseELContext.withAdditional(new ModelRootELResolver(config, modelHolder))
                                                   .withAdditional(new ModelHolderELResolver(config, modelHolder));
        return new ELRefContext(expressionFactory, elContext, config, globalEventListeners, null,
                                modelHolder, messageSender, eventDelaySupport);
    }
}
