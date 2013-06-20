package at.irian.ankor.application;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface BeanResolver {
    Object resolveByName(String beanName);
}
