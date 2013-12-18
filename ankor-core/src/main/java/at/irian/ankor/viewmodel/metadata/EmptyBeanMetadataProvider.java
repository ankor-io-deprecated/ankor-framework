package at.irian.ankor.viewmodel.metadata;

/**
 * @author Manfred Geiler
 */
public class EmptyBeanMetadataProvider implements BeanMetadataProvider {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(EmptyBeanMetadataProvider.class);


    @Override
    public BeanMetadata getMetadata(Class<?> viewModelBeanType) {
        return BeanMetadata.EMPTY_BEAN_METADATA;
    }

    @Override
    public BeanMetadata getMetadata(Object viewModelBean) {
        return BeanMetadata.EMPTY_BEAN_METADATA;
    }
}
