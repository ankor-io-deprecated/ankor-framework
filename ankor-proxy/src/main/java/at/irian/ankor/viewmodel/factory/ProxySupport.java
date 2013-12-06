package at.irian.ankor.viewmodel.factory;

import at.irian.ankor.ref.Ref;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.beans.Introspector;
import java.lang.reflect.Method;

/**
 * @author Manfred Geiler
 */
public class ProxySupport {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ProxySupport.class);

    public static <E> E createProxyBean(final Ref ref, Class<E> type, Object... args) {
        Enhancer e = new Enhancer();
        e.setSuperclass(type);
//        e.setCallbackFilter(new CallbackFilter() {
//            @Override
//            public int accept(Method method) {
//                if (method.getName().equals("foo")) {
//                    return 0;
//                } else
//            }
//        });
        e.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                LOG.debug("Intercept {}, {}, {}", method, args, proxy);
                Object retValue = proxy.invokeSuper(obj, args);
                String methodName = method.getName();
                if (methodName.length() > 3 && methodName.startsWith("set")) {
                    String propName = methodName.substring(3);
                    propName = Introspector.decapitalize(propName);
                    Ref propRef = ref.appendPath(propName);
                    LOG.info("Auto signal value change for {}", propRef);
                    propRef.signalValueChange();
                }
                return retValue;
            }
        });

        Object proxy = e.create(AnkorBeanFactory.getParameterTypes(args), args);

        //noinspection unchecked
        return (E)proxy;
    }



//    public static <E> E createDelegateProxy(final Ref ref, final E bean) {
//        if (bean == null) {
//            throw new NullPointerException("bean");
//        }
//
//        Class<?> type = bean.getClass();
//        if (Enhancer.isEnhanced(type)) {
//            throw new IllegalArgumentException("Already enhanced: " + bean);
//        }
//
//        Enhancer e = new Enhancer();
//        e.setSuperclass(type);
////        e.setCallbackFilter(new CallbackFilter() {
////            @Override
////            public int accept(Method method) {
////                if (method.getName().equals("foo")) {
////                    return 0;
////                } else
////            }
////        });
//        e.setCallback(new MethodInterceptor() {
//            @Override
//            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
//                LOG.debug("Intercept {}, {}, {}", method, args, proxy);
//                Object retValue = proxy.invokeSuper(bean, args);
//                String methodName = method.getName();
//                if (methodName.length() > 3 && methodName.startsWith("set")) {
//                    String propName = methodName.substring(3);
//                    propName = Introspector.decapitalize(propName);
//                    Ref propRef = ref.appendPath(propName);
//                    LOG.info("Auto signal value change for {}", propRef);
//                    propRef.signalValueChange();
//                }
//                return retValue;
//            }
//        });
//
//        Object proxy = e.create();
//
//        //noinspection unchecked
//        return (E)proxy;
//    }


}
