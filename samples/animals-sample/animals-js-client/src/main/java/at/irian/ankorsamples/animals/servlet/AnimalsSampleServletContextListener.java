package at.irian.ankorsamples.animals.servlet;

import at.irian.ankor.application.Application;
import at.irian.ankor.application.SimpleSingleRootApplication;
import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.servlet.polling.AnkorServletContextListener;
import at.irian.ankorsamples.animals.domain.AnimalRepository;
import at.irian.ankorsamples.animals.viewmodel.ModelRoot;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Manfred Geiler
 */
public class AnimalsSampleServletContextListener extends AnkorServletContextListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalsSampleServletContextListener.class);

    @Override
    protected String getName() {
        return "animals-sample-servlet-server";
    }

    @Override
    protected BeanResolver getBeanResolver() {
        return new BeanResolver() {
            @Override
            public Object resolveByName(String beanName) {
                return null;
            }

            @Override
            public Collection<String> getKnownBeanNames() {
                return Collections.emptyList();
            }
        };
    }

    @Override
    protected Application getApplication() {
        return new SimpleSingleRootApplication("Animals", "root") {
            @Override
            public Object createModel(Ref rootRef) {
                return new ModelRoot(rootRef, new AnimalRepository());
            }

        };
    }
}
