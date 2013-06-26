package at.irian.ankor.context;

import at.irian.ankor.event.UnsynchronizedListenersHolder;
import at.irian.ankor.el.StandardELContext;
import at.irian.ankor.event.ListenersHolder;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.ref.el.ELRefContext;
import com.typesafe.config.Config;

import javax.el.ExpressionFactory;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class SingletonInstanceAnkorContextFactory implements AnkorContextFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SingletonInstanceAnkorContextFactory.class);

    private final AnkorContext ankorContext;

    public SingletonInstanceAnkorContextFactory(Class<?> modelType,
                                                ListenersHolder globalListenersHolder,
                                                StandardELContext baseELContext,
                                                Config config, MessageSender messageSender) {
        ModelHolder modelHolder = new ModelHolder(modelType, new UnsynchronizedListenersHolder(globalListenersHolder));
        ELRefContext refContext = ELRefContext.create(ExpressionFactory.newInstance(),
                                                      baseELContext,
                                                      config,
                                                      modelHolder);
        this.ankorContext = new AnkorContext(modelHolder, refContext, messageSender);
    }

    @Override
    public AnkorContext create() {
        return ankorContext;
    }
}
