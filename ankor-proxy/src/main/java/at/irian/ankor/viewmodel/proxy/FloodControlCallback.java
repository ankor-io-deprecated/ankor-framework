package at.irian.ankor.viewmodel.proxy;

import at.irian.ankor.delay.FloodControl;
import at.irian.ankor.delay.FloodControlMetadata;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class FloodControlCallback implements MethodInterceptor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(InitMethodCallback.class);

    private final RefContext refContext;
    private final BeanMetadata metadata;
    private Map<Method, FloodControl> floodControlMap;

    public FloodControlCallback(RefContext refContext, BeanMetadata metadata) {
        this.refContext = refContext;
        this.metadata = metadata;
        this.floodControlMap = null;
    }

    @Override
    public Object intercept(final Object obj, Method method, final Object[] args, final MethodProxy proxy) throws Throwable {

        FloodControl floodControl = null;

        if (floodControlMap != null) {
            floodControl = floodControlMap.get(method);
        }

        if (floodControl == null) {
            FloodControlMetadata floodControlMetadata = metadata.getMethodMetadata(method)
                                                                .getGenericMetadata(FloodControlMetadata.class);
            if (floodControlMetadata != null) {
                floodControl = new FloodControl(refContext, floodControlMetadata.getDelayMillis());
                if (floodControlMap == null) {
                    floodControlMap = new HashMap<Method, FloodControl>();
                }
                floodControlMap.put(method, floodControl);
            }
        }

        if (floodControl != null) {
            floodControl.control(new Runnable() {
                @Override
                public void run() {
                    try {
                        proxy.invokeSuper(obj, args);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            });
            return null;
        } else {
            return proxy.invokeSuper(obj, args);
        }
    }

    public static boolean accept(Method method, BeanMetadata metadata) {
        return metadata.getMethodMetadata(method).getGenericMetadata(FloodControlMetadata.class) != null;
    }
}
