package at.irian.ankor.system;

/**
 * @author Manfred Geiler
 */
public interface BeanResolver {
    Object resolveByName(String beanName);
}
