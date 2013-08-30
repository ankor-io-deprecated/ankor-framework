package at.irian.ankorman.sample1.server;

import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.servlet.AnkorServletContextListener;
import at.irian.ankor.session.ModelRootFactory;
import at.irian.ankorman.sample1.viewmodel.ModelRoot;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

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
    protected ModelRootFactory getModelRootFactory() {
        return new ModelRootFactory() {

            @Override
            public Set<String> getKnownRootNames() {
                return Collections.singleton("root");
            }

            @Override
            public Object createModelRoot(Ref rootRef) {
                return new ModelRoot(rootRef, new AnimalRepository());
            }
        };
    }
}
