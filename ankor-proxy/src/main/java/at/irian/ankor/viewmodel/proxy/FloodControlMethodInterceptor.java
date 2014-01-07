package at.irian.ankor.viewmodel.proxy;

import at.irian.ankor.delay.FloodControl;
import at.irian.ankor.delay.FloodControlMetadata;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class FloodControlMethodInterceptor implements MethodInterceptor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FloodControlMethodInterceptor.class);

    private final BeanMetadata metadata;
    private Map<Method, FloodControl> floodControlMap;

    public FloodControlMethodInterceptor(BeanMetadata metadata) {
        this.metadata = metadata;
        this.floodControlMap = null;
    }

    @Override
    public boolean accept(Method method) {
        return metadata.getMethodMetadata(method).getGenericMetadata(FloodControlMetadata.class) != null;
    }

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {

        Method method = invocation.getMethod();

        FloodControl floodControl = null;

        if (floodControlMap != null) {
            floodControl = floodControlMap.get(method);
        }

        if (floodControl == null) {
            FloodControlMetadata floodControlMetadata = metadata.getMethodMetadata(method)
                                                                .getGenericMetadata(FloodControlMetadata.class);
            if (floodControlMetadata != null) {
                RefContext refContext = invocation.getRef().context();
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
                        invocation.proceed();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            });
            return null;
        } else {
            return invocation.proceed();
        }
    }

}
