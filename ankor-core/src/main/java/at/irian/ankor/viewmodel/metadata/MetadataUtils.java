package at.irian.ankor.viewmodel.metadata;

import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public final class MetadataUtils {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MetadataUtils.class);

    private MetadataUtils() {}


    public static PropertyMetadata getMetadataFor(Ref propertyRef) {

        if (propertyRef.isRoot()) {
            return PropertyMetadata.emptyPropertyMetadata("");
        }

        Object parentValue = propertyRef.parent().getValue();
        if (parentValue == null) {
            // prevent NPE, happens rarely, but may happen (race conditions, etc.)
            return PropertyMetadata.emptyPropertyMetadata("");
        }

        String propertyName = propertyRef.propertyName();

        BeanMetadata beanMetadata = propertyRef.context().metadataProvider().getMetadata(parentValue);
        return beanMetadata.getPropertyMetadata(propertyName);
    }

}
