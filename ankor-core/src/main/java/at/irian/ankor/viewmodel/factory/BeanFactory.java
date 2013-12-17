package at.irian.ankor.viewmodel.factory;

import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public interface BeanFactory {
    <T> T createNewInstance(Class<T> type, Ref ref, Object[] constructorArgs);
}