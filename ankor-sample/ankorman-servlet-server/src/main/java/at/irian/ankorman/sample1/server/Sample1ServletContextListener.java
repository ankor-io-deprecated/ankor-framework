package at.irian.ankorman.sample1.server;

import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.servlet.AnkorServletContextListener;
import at.irian.ankor.session.ModelRootFactory;
import at.irian.ankorman.sample1.viewmodel.ModelRoot;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Manfred Geiler
 */
public class Sample1ServletContextListener extends AnkorServletContextListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Sample1ServletContextListener.class);

    @Override
    protected String getName() {
        return "sample1-servlet-server";
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
    protected ModelRootFactory getModelRootFactory() {
        return new ModelRootFactory() {
            @Override
            public Object createModelRoot(Ref rootRef) {
                return new ModelRoot(rootRef, new AnimalRepository());
            }
        };
    }
}
