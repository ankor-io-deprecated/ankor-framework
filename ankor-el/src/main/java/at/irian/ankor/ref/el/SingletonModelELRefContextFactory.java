package at.irian.ankor.ref.el;

import at.irian.ankor.context.ModelHolder;
import at.irian.ankor.el.AnkorELSupport;
import at.irian.ankor.el.ELSupport;
import at.irian.ankor.event.EventDelaySupport;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.model.ViewModelPostProcessor;
import at.irian.ankor.system.BeanResolver;
import com.typesafe.config.Config;

import java.util.List;

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
                                                EventDelaySupport eventDelaySupport,
                                                List<ViewModelPostProcessor> viewModelPostProcessors) {
        super(config,
              modelHolder,
              elSupport,
              globalEventListeners,
              messageSender,
              eventDelaySupport,
              viewModelPostProcessors);
    }


    public static ELRefContextFactory getInstance(Config config,
                                                  Class<?> modelType,
                                                  EventListeners globalEventListeners,
                                                  MessageSender messageSender,
                                                  BeanResolver beanResolver,
                                                  EventDelaySupport eventDelaySupport,
                                                  List<ViewModelPostProcessor> viewModelPostProcessors) {
        ModelHolder modelHolder = ModelHolder.create(modelType);
        AnkorELSupport elSupport = new AnkorELSupport(config, modelHolder, beanResolver);
        return new SingletonModelELRefContextFactory(config,
                                                     modelHolder,
                                                     elSupport,
                                                     globalEventListeners,
                                                     messageSender,
                                                     eventDelaySupport,
                                                     viewModelPostProcessors);
    }

}
