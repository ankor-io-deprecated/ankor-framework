package at.irian.ankor.viewmodel.proxy;

import java.lang.reflect.Method;

/**
 * @author Manfred Geiler
 */
public class RefAwareMethodInterceptor implements MethodInterceptor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefAwareMethodInterceptor.class);


    @Override
    public boolean accept(Method method) {
        return method.getName().equals("getRef") && method.getParameterTypes().length == 0;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return invocation.getRef();
    }

}
