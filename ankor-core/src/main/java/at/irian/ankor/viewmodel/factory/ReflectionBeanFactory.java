package at.irian.ankor.viewmodel.factory;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.metadata.BeanMetadataProvider;

/**
 * Simple {@link BeanFactory} implementation that creates new instances of view model beans by directly invoking
 * the matching constructor via reflection.
 *
 * @author Manfred Geiler
 */
public class ReflectionBeanFactory extends AbstractBeanFactory {

    public ReflectionBeanFactory(BeanMetadataProvider metadataProvider) {
        super(metadataProvider);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T createRawInstance(Class<T> type, Ref ref, Object[] constructorArgs) {
        return new ConstructionHelper<T>(type)
                .withArguments(constructorArgs)
                .withOptionalPrefixArguments(ref)
                .invoke();
    }

}
