package at.irian.ankor.ref.el;

import at.irian.ankor.context.ModelHolder;
import at.irian.ankor.el.AnkorELSupport;
import at.irian.ankor.el.ELSupport;
import at.irian.ankor.event.EventDelaySupport;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.system.BeanResolver;
import com.typesafe.config.Config;

/**
 * @author Manfred Geiler
 */
public class SingletonModelELRefContextFactory extends ELRefContextFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SingletonModelELRefContextFactory.class);

    protected SingletonModelELRefContextFactory(Config config,
                                                ModelHolder modelHolder,
                                                ELSupport elSupport,
                                                EventListeners globalEventListeners,
                                                MessageSender messageSender,
                                                EventDelaySupport eventDelaySupport) {
        super(config,
              modelHolder,
              elSupport,
              globalEventListeners,
              messageSender,
              eventDelaySupport);
    }


    public static ELRefContextFactory getInstance(Config config,
                                                  Class<?> modelType,
                                                  EventListeners globalEventListeners,
                                                  MessageSender messageSender,
                                                  BeanResolver beanResolver,
                                                  EventDelaySupport eventDelaySupport) {
        ModelHolder modelHolder = ModelHolder.create(modelType);
        AnkorELSupport elSupport = new AnkorELSupport(config, modelHolder, beanResolver);
        return new SingletonModelELRefContextFactory(config,
                                                     modelHolder,
                                                     elSupport,
                                                     globalEventListeners,
                                                     messageSender,
                                                     eventDelaySupport);
    }

}
