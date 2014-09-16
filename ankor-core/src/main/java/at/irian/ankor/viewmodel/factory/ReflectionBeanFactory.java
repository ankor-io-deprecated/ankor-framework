package at.irian.ankor.viewmodel.factory;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.metadata.BeanMetadataProvider;
import org.apache.commons.lang.reflect.ConstructorUtils;

/**
 * @author Manfred Geiler
 */
public class ReflectionBeanFactory extends AbstractBeanFactory {

    public ReflectionBeanFactory(BeanMetadataProvider metadataProvider) {
        super(metadataProvider);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T createRawInstance(Class<T> type,
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

        return instance;
    }

}
