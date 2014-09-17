package at.irian.ankor.viewmodel.proxy;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.RefAware;
import at.irian.ankor.viewmodel.factory.AbstractBeanFactory;
import at.irian.ankor.viewmodel.factory.ConstructionHelper;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;
import at.irian.ankor.viewmodel.metadata.BeanMetadataProvider;
import net.sf.cglib.proxy.Enhancer;

/**
 * Cglib-based View Model {@link at.irian.ankor.viewmodel.factory.BeanFactory BeanFactory}.
 * Other than the normal {@link at.irian.ankor.viewmodel.factory.ReflectionBeanFactory ReflectionBeanFactory} this
 * factory actually creates intercepted proxies to support special enhanced Ankor features like "auto signalling",
 * "method flood control" and "automatic RefAware mixin".
 * @see at.irian.ankor.viewmodel.proxy.InterceptorChainFactory
 * @see at.irian.ankor.viewmodel.proxy.AutoSignalMethodInterceptor
 * @see at.irian.ankor.viewmodel.proxy.FloodControlMethodInterceptor
 * @see at.irian.ankor.viewmodel.proxy.RefAwareMethodInterceptor
 * @author Manfred Geiler
 */
public class CglibProxyBeanFactory extends AbstractBeanFactory {

    private static final Class[] INTERFACES = new Class[] {RefAware.class};

    public CglibProxyBeanFactory(BeanMetadataProvider metadataProvider) {
        super(metadataProvider);
    }

    @Override
    public <T> T createRawInstance(Class<T> type,
                                      Ref ref,
                                      Object[] args) {
        BeanMetadata metadata = metadataProvider.getMetadata(type);
        Enhancer e = new Enhancer();
        e.setSuperclass(type);
        e.setInterfaces(INTERFACES);

        InterceptorChainFactory interceptorChainFactory = new InterceptorChainFactory(metadata);
        CglibCallback cglibCallback = new CglibCallback(metadata, ref, interceptorChainFactory);
        e.setCallback(cglibCallback);

        ConstructionHelper<T> constructionHelper = new ConstructionHelper<T>(type)
                .withArguments(args)
                .withOptionalPrefixArguments(ref)
                .resolve();

        //noinspection unchecked
        return (T) e.create(constructionHelper.getResolvedArgumentTypes(),
                            constructionHelper.getResolvedArguments());
    }

}
