package at.irian.ankor.viewmodel.factory;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelSupport;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;
import at.irian.ankor.viewmodel.metadata.BeanMetadataProvider;
import org.apache.commons.lang.reflect.ConstructorUtils;

/**
 * @author Manfred Geiler
 */
public class AnkorBeanFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorBeanFactory.class);

    private final BeanMetadataProvider metadataProvider;

    public AnkorBeanFactory(BeanMetadataProvider metadataProvider) {
        this.metadataProvider = metadataProvider;
    }

    public static <T> T newInstance(Class<T> type, Ref ref, Object... constructorArgs) {
        BeanMetadataProvider beanMetadataProvider = ref.context().metadataProvider();
        return new AnkorBeanFactory(beanMetadataProvider).createNewInstance(type, ref, constructorArgs);
    }

    public <T> T createNewInstance(Class<T> type, Ref ref, Object[] args) {

        BeanMetadata metadata = metadataProvider.getMetadata(type);

        T instance;
        try {
            //noinspection unchecked
            instance = (T) ConstructorUtils.invokeConstructor(type, args, getParameterTypes(args));
            //instance = ProxySupport.createProxyBean(ref, type, args);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not create instance of type " + type, e);
        }

        ViewModelSupport.invokePostProcessorsOn(instance, ref, metadata);

        return instance;
    }

    static Class[] getParameterTypes(Object[] args) {
        Class parameterTypes[] = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            parameterTypes[i] = arg != null ? arg.getClass() : Object.class;
        }
        return parameterTypes;
    }
}
