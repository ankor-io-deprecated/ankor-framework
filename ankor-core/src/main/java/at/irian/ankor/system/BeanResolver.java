package at.irian.ankor.system;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface BeanResolver {
    Object resolveByName(String beanName);
}
