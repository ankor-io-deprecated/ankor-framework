package at.irian.ankor.viewmodel.factory;

import at.irian.ankor.ref.Ref;

/**
 * Ankor's View Model Bean Factory.
 * All View Model Beans should be created by means of an Ankor {@link BeanFactory BeanFactory}. This ensures
 * View Model compatibility with different run-time environments and independency of a specific IoC container.
 *
 * @author Manfred Geiler
 */
public interface BeanFactory {
    /**
     * Create a new instance of the given type.
     *
     * Constructor matching follows this convention:
     * <ol>
     *     <li>Look for a constructor with a Ref as first parameter and additional parameters matching the given constructor arguments</li>
     *     <li>Look for a constructor with parameters matching the given constructor arguments</li>
     * </ol>
     * @param type             type of bean to create
     * @param ref              Ref, the newly created bean will be associated with
     * @param constructorArgs  optional constructor arguments; may be empty but must not be null!
     * @param <T> type of bean to create
     * @return newly created view model bean
     */
    <T> T createAndInitializeInstance(Class<T> type, Ref ref, Object[] constructorArgs);
}
