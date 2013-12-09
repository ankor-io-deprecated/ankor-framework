package at.irian.ankor.viewmodel.proxy;

import at.irian.ankor.ref.Ref;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author Manfred Geiler
 */
public class RefAwareCallback implements MethodInterceptor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefAwareCallback.class);

    private final Ref ref;

    public RefAwareCallback(Ref ref) {
        this.ref = ref;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return ref;
    }

    public static boolean accept(Method method) {
        return method.getName().equals("getRef") && method.getParameterTypes().length == 0;
    }
}
