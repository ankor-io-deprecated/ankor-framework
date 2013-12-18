package at.irian.ankor.viewmodel.proxy;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModels;
import at.irian.ankor.viewmodel.factory.InitMethodMetadata;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author Manfred Geiler
 */
public class InitMethodCallback implements MethodInterceptor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(InitMethodCallback.class);

    private final Ref ref;

    public InitMethodCallback(Ref ref) {
        this.ref = ref;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Object result = proxy.invokeSuper(obj, args);
        ViewModels.invokePostProcessorsOn(obj, ref);
        return result;
    }

    public static boolean accept(Method method, BeanMetadata metadata) {
        return metadata.getMethodMetadata(method).getGenericMetadata(InitMethodMetadata.class) != null;
    }
}
