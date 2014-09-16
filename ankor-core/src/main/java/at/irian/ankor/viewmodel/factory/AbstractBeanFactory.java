package at.irian.ankor.viewmodel.factory;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelPostProcessor;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;
import at.irian.ankor.viewmodel.metadata.BeanMetadataProvider;

/**
 * @author Manfred Geiler
 */
public abstract class AbstractBeanFactory implements BeanFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AbstractBeanFactory.class);

    protected final BeanMetadataProvider metadataProvider;

    protected AbstractBeanFactory(BeanMetadataProvider metadataProvider) {
        this.metadataProvider = metadataProvider;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T> T createAndInitializeInstance(Class<T> type, Ref ref, Object[] args) {
        BeanMetadata metadata = metadataProvider.getMetadata(type);
        Object instance = createRawInstance(type, ref, args);
        return (T)initializeInstance(instance, ref, metadata);
    }

    protected abstract <T> T createRawInstance(Class<T> type, Ref ref, Object[] args);

    public Object initializeInstance(Object instance, Ref ref, BeanMetadata metadata) {
        invokePostProcessorsOn(instance, ref, metadata);
        return instance;
    }

    protected void invokePostProcessorsOn(Object viewModelBean, Ref viewModelRef, BeanMetadata metadata) {
        for (ViewModelPostProcessor viewModelPostProcessor : viewModelRef.context().viewModelPostProcessors()) {
            viewModelPostProcessor.postProcess(viewModelBean, viewModelRef, metadata);
        }
    }

    protected static Class[] getParameterTypes(Object[] args) {
        Class parameterTypes[] = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            parameterTypes[i] = arg != null ? arg.getClass() : Object.class;
        }
        return parameterTypes;
    }

}
