package at.irian.ankor.viewmodel.proxy;

import java.lang.reflect.Method;

/**
 * @author Manfred Geiler
 */
public interface MethodInterceptor {

    boolean accept(Method method);

    Object invoke(MethodInvocation invocation) throws Throwable;

}
