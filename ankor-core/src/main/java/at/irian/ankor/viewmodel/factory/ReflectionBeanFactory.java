package at.irian.ankor.viewmodel.factory;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;
import at.irian.ankor.viewmodel.metadata.BeanMetadataProvider;
import at.irian.ankor.viewmodel.metadata.MethodMetadata;
import org.apache.commons.lang.reflect.ConstructorUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Manfred Geiler
 */
public class ReflectionBeanFactory extends AbstractBeanFactory {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ReflectionBeanFactory.class);

    public ReflectionBeanFactory(BeanMetadataProvider metadataProvider) {
        super(metadataProvider);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T createInstance(Class<T> type,
                                   Ref ref,
                                   Object[] constructorArgs) {
        T instance;
        try {
            //noinspection unchecked
            try {
                instance = (T) ConstructorUtils.invokeConstructor(type, constructorArgs, getParameterTypes(constructorArgs));
            } catch (NoSuchMethodException e) {
                // try with Ref as first argument
                Object[] extConstructorArgs = new Object[constructorArgs.length + 1];
                extConstructorArgs[0] = ref;
                System.arraycopy(constructorArgs, 0, extConstructorArgs, 1, constructorArgs.length);
                instance = (T) ConstructorUtils.invokeConstructor(type, extConstructorArgs, getParameterTypes(extConstructorArgs));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not create instance of type " + type, e);
        }

        BeanMetadata metadata = metadataProvider.getMetadata(type);
        for (MethodMetadata methodMetadata : metadata.getMethodsMetadata()) {
            if (methodMetadata.getGenericMetadata(InitMethodMetadata.class) != null) {
                LOG.warn("This bean factory does not support automatic view model bean init. " +
                         "Init method {} will never be invoked - please call AnkorPatterns.init explicitly in your init method",
                         methodMetadata.getMethod());
            }
        }

        return instance;
    }

}
