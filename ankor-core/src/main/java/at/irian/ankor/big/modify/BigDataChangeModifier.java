package at.irian.ankor.big.modify;

import at.irian.ankor.big.BigListMetadata;
import at.irian.ankor.big.BigMapMetadata;
import at.irian.ankor.change.Change;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.metadata.MetadataUtils;
import at.irian.ankor.viewmodel.metadata.PropertyMetadata;

import java.util.Collection;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
class BigDataChangeModifier {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BigListAwareSendReceiveModifier.class);

    public Change modify(Change change, Ref changedProperty) {
        switch (change.getType()) {
            case value:
            {
                Object modifiedChangeValue = handleChangeValueBeforeSend(change.getValue(),
                                                                         getPropertyMetadata(changedProperty));
                return change.withValue(modifiedChangeValue);
            }
            case insert:
            case delete:
            case replace:
                return change;
            default:
                throw new IllegalArgumentException("Unknown change type " + change.getType());
        }
    }

    private PropertyMetadata getPropertyMetadata(Ref changedProperty) {
        return MetadataUtils.getMetadataFor(changedProperty);
    }

    private Object handleChangeValueBeforeSend(Object value, PropertyMetadata propertyMetadata) {
        BigListMetadata bigListMetadata = propertyMetadata.getGenericMetadata(BigListMetadata.class);
        if (bigListMetadata != null) {
            ListToBigListDummyConverter converter
                    = ListToBigListDummyConverter.createFromMetadata(bigListMetadata);
            return converter.convert((Collection) value);
        }

        BigMapMetadata bigMapMetadata = propertyMetadata.getGenericMetadata(BigMapMetadata.class);
        if (bigMapMetadata != null) {
            MapToBigMapDummyConverter converter
                    = MapToBigMapDummyConverter.createFromMetadata(bigMapMetadata);
            return converter.convert((Map) value);
        }

        return value;
    }

}
