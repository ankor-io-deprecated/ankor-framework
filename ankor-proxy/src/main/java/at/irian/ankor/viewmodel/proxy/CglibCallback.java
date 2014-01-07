package at.irian.ankor.viewmodel.proxy;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;

import java.lang.reflect.Method;

/**
 * @author Manfred Geiler
 */
public class CglibCallback implements net.sf.cglib.proxy.MethodInterceptor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CglibCallback.class);

    private final BeanMetadata metadata;
    private final Ref ref;
    private final InterceptorChainFactory interceptorChainFactory;

    public CglibCallback(BeanMetadata metadata, Ref ref, InterceptorChainFactory interceptorChainFactory) {
        this.metadata = metadata;
        this.ref = ref;
        this.interceptorChainFactory = interceptorChainFactory;
    }

    @Override
    public Object intercept(final Object obj, final Method method, final Object[] args,
                            final net.sf.cglib.proxy.MethodProxy proxy) throws Throwable {

        final MethodInterceptor[] interceptorChain = interceptorChainFactory.createInterceptorChain(method);

        return new MethodInvocation() {

            int i = -1;

            @Override
            public Method getMethod() {
                return method;
            }

            @Override
            public Object[] getArguments() {
                return args;
            }

            @Override
            public Object getThis() {
                return obj;
            }

            @Override
            public Ref getRef() {
                return ref;
            }

            @Override
            public BeanMetadata getMetadata() {
                return metadata;
            }

            @Override
            public Object proceed() throws Throwable {
                i++;
                if (i < interceptorChain.length) {
                    return interceptorChain[i].invoke(this);
                } else {
                    return proxy.invokeSuper(obj, args);
                }
            }

        }.proceed();
    }

}
