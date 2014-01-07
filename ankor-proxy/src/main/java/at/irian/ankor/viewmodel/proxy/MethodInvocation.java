package at.irian.ankor.viewmodel.proxy;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;

import java.lang.reflect.Method;

/**
 * Method invocation information for method interceptors.
 * (Similar to the AOP Alliance's Joinpoint and MethodInvocation)
 *
 * @author Manfred Geiler
 */
public interface MethodInvocation {

    Method getMethod();

    Object[] getArguments();

    Object getThis();

    Ref getRef();

    BeanMetadata getMetadata();

    Object proceed() throws Throwable;

}
