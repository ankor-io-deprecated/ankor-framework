package at.irian.ankor.viewmodel.metadata;

/**
 * @author Manfred Geiler
 */
public interface BeanMetadataProvider {

    BeanMetadata getMetadata(Class<?> viewModelBeanType);
    BeanMetadata getMetadata(Object viewModelBean);

}
