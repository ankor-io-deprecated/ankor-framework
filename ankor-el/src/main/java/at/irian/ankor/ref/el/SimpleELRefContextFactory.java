package at.irian.ankor.ref.el;

import at.irian.ankor.context.ModelHolder;
import at.irian.ankor.el.StandardELContext;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.messaging.MessageSender;
import com.typesafe.config.Config;

import javax.el.ExpressionFactory;

/**
 * @author Manfred Geiler
 */
public class SimpleELRefContextFactory extends ELRefContextFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleELRefContextFactory.class);

    public SimpleELRefContextFactory(Config config,
                                     Class<?> modelType,
                                     StandardELContext baseELContext,
                                     EventListeners globalEventListeners,
                                     MessageSender messageSender) {
        super(config,
              ModelHolder.create(modelType),
              ExpressionFactory.newInstance(),
              baseELContext,
              globalEventListeners,
              messageSender);
    }
}
