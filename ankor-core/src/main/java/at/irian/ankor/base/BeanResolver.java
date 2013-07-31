package at.irian.ankor.base;

import java.util.Collection;

/**
 * Abstraction for third-party bean factories like <a href="http://www.springsource.org/spring-framework">Spring</a>
 * or <a href="http://docs.oracle.com/javaee/6/api/javax/annotation/ManagedBean.html">ManagedBean</a>s.
 *
 * @author Manfred Geiler
 */
public interface BeanResolver {

    Object resolveByName(String beanName);

    Collection<String> getKnownBeanNames();
}
