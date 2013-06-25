package at.irian.ankor.context;

import at.irian.ankor.event.UnsynchronizedEventBus;
import at.irian.ankor.el.StandardELContext;
import at.irian.ankor.event.EventBus;
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
                                                EventBus globalEventBus,
                                                StandardELContext baseELContext,
                                                Config config) {
        ModelHolder modelHolder = new ModelHolder(modelType, new UnsynchronizedEventBus(globalEventBus));
        ELRefContext refContext = ELRefContext.create(ExpressionFactory.newInstance(),
                                                      baseELContext,
                                                      config,
                                                      modelHolder);
        this.ankorContext = new AnkorContext(modelHolder, refContext);
    }

    @Override
    public AnkorContext create() {
        return ankorContext;
    }
}
