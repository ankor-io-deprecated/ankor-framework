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

    private static final String SESSION_ATTR_KEY = FloodControlMethodInterceptor.class.getName() + ".MAP";

    private final BeanMetadata metadata;

    public FloodControlMethodInterceptor(BeanMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean accept(Method method) {
        return metadata.getMethodMetadata(method).getGenericMetadata(FloodControlMetadata.class) != null;
    }

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {

        RefContext refContext = invocation.getRef().context();
        Map<String, Object> sessionAttributes = refContext.modelSession().getAttributes();
        @SuppressWarnings("unchecked")
        Map<Method, FloodControl> floodControlMap = (Map) sessionAttributes.get(SESSION_ATTR_KEY);

        Method method = invocation.getMethod();

        FloodControl floodControl;
        if (floodControlMap == null) {
            floodControl = null;
        } else {
            floodControl = floodControlMap.get(method);
        }

        if (floodControl == null) {
            FloodControlMetadata floodControlMetadata = metadata.getMethodMetadata(method)
                                                                .getGenericMetadata(FloodControlMetadata.class);
            if (floodControlMetadata != null) {
                floodControl = new FloodControl(refContext, floodControlMetadata.getDelayMillis());
                if (floodControlMap == null) {
                    floodControlMap = new HashMap<Method, FloodControl>();
                    sessionAttributes.put(SESSION_ATTR_KEY, floodControlMap);
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
