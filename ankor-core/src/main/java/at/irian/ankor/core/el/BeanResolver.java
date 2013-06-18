package at.irian.ankor.core.el;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface BeanResolver {
    Object resolveByName(String beanName);
}
