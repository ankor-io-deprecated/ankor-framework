package at.irian.ankor.viewmodel.proxy;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.RefAware;
import at.irian.ankor.viewmodel.factory.AbstractBeanFactory;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;
import at.irian.ankor.viewmodel.metadata.BeanMetadataProvider;
import net.sf.cglib.proxy.Enhancer;
import org.apache.commons.lang.reflect.ConstructorUtils;

import java.lang.reflect.Constructor;

/**
 * @author Manfred Geiler
 */
public class CglibProxyBeanFactory extends AbstractBeanFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CglibProxyBeanFactory.class);

    private static final Class[] INTERFACES = new Class[] {RefAware.class};

    public CglibProxyBeanFactory(BeanMetadataProvider metadataProvider) {
        super(metadataProvider);
    }

    @Override
    protected <T> T createRawInstance(Class<T> type,
                                      Ref ref,
                                      Object[] args) {
        BeanMetadata metadata = metadataProvider.getMetadata(type);
        Enhancer e = new Enhancer();
        e.setSuperclass(type);
        e.setInterfaces(INTERFACES);

        InterceptorChainFactory interceptorChainFactory = new InterceptorChainFactory(metadata);
        CglibCallback cglibCallback = new CglibCallback(metadata, ref, interceptorChainFactory);
        e.setCallback(cglibCallback);

        Class[] parameterTypes = getParameterTypes(args);
        Constructor matchingAccessibleConstructor
                = ConstructorUtils.getMatchingAccessibleConstructor(type, parameterTypes);
        if (matchingAccessibleConstructor == null) {
            StringBuilder sb = new StringBuilder();
            for (Class parameterType : parameterTypes) {
                if (sb.length() > 0) {
                    sb = sb.append(',');
                }
                sb = sb.append(parameterType.getSimpleName());
            }

            throw new IllegalArgumentException("No matching constructor found for " + sb.toString());
        }

        //noinspection unchecked
        return (T) e.create(matchingAccessibleConstructor.getParameterTypes(), args);
    }

}
