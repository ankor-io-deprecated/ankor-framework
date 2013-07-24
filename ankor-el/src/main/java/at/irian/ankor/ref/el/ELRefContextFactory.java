package at.irian.ankor.ref.el;

import at.irian.ankor.context.ModelHolder;
import at.irian.ankor.el.ELSupport;
import at.irian.ankor.event.EventDelaySupport;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefContextFactory;
import com.typesafe.config.Config;

/**
 * @author Manfred Geiler
 */
public class ELRefContextFactory implements RefContextFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELRefContextFactory.class);

    private final Config config;
    private final ModelHolder modelHolder;
    private final ELSupport elSupport;
    private final EventListeners globalEventListeners;
    private final MessageSender messageSender;
    private final EventDelaySupport eventDelaySupport;

    public ELRefContextFactory(Config config,
                               ModelHolder modelHolder,
                               ELSupport elSupport,
                               EventListeners globalEventListeners,
                               MessageSender messageSender,
                               EventDelaySupport eventDelaySupport) {
        this.config = config;
        this.modelHolder = modelHolder;
        this.elSupport = elSupport;
        this.globalEventListeners = globalEventListeners;
        this.messageSender = messageSender;
        this.eventDelaySupport = eventDelaySupport;
    }

    @Override
    public RefContext createRefContext() {
        return new ELRefContext(elSupport, config, globalEventListeners,
                                modelHolder, messageSender, eventDelaySupport);
    }
}
