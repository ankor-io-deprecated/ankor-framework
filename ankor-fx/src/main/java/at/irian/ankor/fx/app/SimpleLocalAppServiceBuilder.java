package at.irian.ankor.fx.app;

import at.irian.ankor.messaging.json.JsonViewModelMessageMapper;
import at.irian.ankor.system.BeanResolver;
import at.irian.ankor.system.SimpleAnkorSystem;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Spiegl
 */
public class SimpleLocalAppServiceBuilder {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleAppServiceFactory.class);

    private final BeanResolver beanResolver;
    private Class<?> modelType = Object.class;
    private Map<String, Object> beans = new HashMap<String, Object>();

    public SimpleLocalAppServiceBuilder() {
        beanResolver = new BeanResolver() {
            @Override
            public Object resolveByName(String beanName) {
                return beans.get(beanName);
            }

            @Override
            public String[] getBeanDefinitionNames() {
                return beans.keySet().toArray(new String[beans.keySet().size()]);
            }
        };
    }

    public SimpleLocalAppServiceBuilder withModelType(Class<?> modelType) {
        this.modelType = modelType;
        return this;
    }

    public SimpleLocalAppServiceBuilder withBean(String beanName, Object bean) {
        this.beans.put(beanName, bean);
        return this;
    }

    public AppService create() {
        // createRefContext
        SimpleAnkorSystem serverSystem = SimpleAnkorSystem.create("server", modelType, beanResolver,
                                                                  new JsonViewModelMessageMapper());
        SimpleAnkorSystem clientSystem = SimpleAnkorSystem.create("client", modelType, false);

        // connect
        clientSystem.connectTo(serverSystem);

        // start
        serverSystem.start();
        clientSystem.start();

        return new AppService(clientSystem);
    }

}
