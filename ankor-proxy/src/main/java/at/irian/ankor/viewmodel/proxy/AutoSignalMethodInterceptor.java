package at.irian.ankor.viewmodel.proxy;

import at.irian.ankor.viewmodel.metadata.AutoSignalMetadata;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;
import at.irian.ankor.viewmodel.metadata.MethodMetadata;

import java.lang.reflect.Method;

/**
 * Method interceptor that implements the "auto signalling" feature for setter methods.
 * Whenever a setter is called on a view model bean, a ValueChange is signalled automatically on the
 * corresponding Ref.
 *
 * @author Manfred Geiler
 */
public class AutoSignalMethodInterceptor implements MethodInterceptor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefAwareCallback.class);

    private final BeanMetadata metadata;

    public AutoSignalMethodInterceptor(BeanMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean accept(Method method) {
        return metadata.getMethodMetadata(method).getGenericMetadata(AutoSignalMetadata.class) != null;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Object result = invocation.proceed();

        MethodMetadata methodMetadata = invocation.getMetadata().getMethodMetadata(invocation.getMethod());
        AutoSignalMetadata autoSignalMetadata = methodMetadata.getGenericMetadata(AutoSignalMetadata.class);
        for (String path : autoSignalMetadata.getPaths()) {
            if (path.startsWith(".")) {
                invocation.getRef().appendPath(path.substring(1)).signalValueChange();
            } else {
                invocation.getRef().context().refFactory().ref(path).signalValueChange();
            }
        }

        return result;
    }

}
