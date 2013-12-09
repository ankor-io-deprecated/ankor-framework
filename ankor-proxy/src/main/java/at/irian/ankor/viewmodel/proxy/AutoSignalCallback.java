package at.irian.ankor.viewmodel.proxy;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;
import at.irian.ankor.viewmodel.metadata.ChangeSignalMetadata;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Manfred Geiler
 */
public class AutoSignalCallback implements MethodInterceptor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefAwareCallback.class);

    private final Ref ref;
    private final BeanMetadata metadata;

    public AutoSignalCallback(Ref ref, BeanMetadata metadata) {
        this.ref = ref;
        this.metadata = metadata;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

        Object result = proxy.invokeSuper(obj, args);

        List<ChangeSignalMetadata> changeSignals = metadata.findChangeSignals(method);
        for (ChangeSignalMetadata changeSignal : changeSignals) {
            String path = changeSignal.getPath();
            if (path.startsWith(".")) {
                ref.appendPath(path.substring(1)).signalValueChange();
            } else {
                ref.context().refFactory().ref(path).signalValueChange();
            }
        }

        return result;
    }

    public static boolean accept(Method method, BeanMetadata metadata) {
        return !metadata.findChangeSignals(method).isEmpty();
    }
}
