package at.irian.ankor.viewmodel.factory;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.metadata.BeanMetadataProvider;
import org.apache.commons.lang.reflect.ConstructorUtils;

/**
 * @author Manfred Geiler
 */
public class ReflectionBeanFactory extends AbstractBeanFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ReflectionBeanFactory.class);

    public ReflectionBeanFactory(BeanMetadataProvider metadataProvider) {
        super(metadataProvider);
    }

    @Override
    protected <T> T createInstance(Class<T> type, Ref ref, Object[] constructorArgs) {
        T instance;
        try {
            //noinspection unchecked
            instance = (T) ConstructorUtils.invokeConstructor(type, constructorArgs, getParameterTypes(constructorArgs));
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not create instance of type " + type, e);
        }
        return instance;
    }

}
