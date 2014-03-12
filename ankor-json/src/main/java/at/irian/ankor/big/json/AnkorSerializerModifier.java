package at.irian.ankor.big.json;

import at.irian.ankor.big.BigListMetadata;
import at.irian.ankor.big.BigMapMetadata;
import at.irian.ankor.big.modify.ListToBigListDummyConverter;
import at.irian.ankor.big.modify.MapToBigMapDummyConverter;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;
import at.irian.ankor.viewmodel.metadata.BeanMetadataProvider;
import at.irian.ankor.viewmodel.metadata.PropertyMetadata;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

import java.util.List;

/**
 * @author Manfred Geiler
 */
public class AnkorSerializerModifier extends BeanSerializerModifier {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorSerializerModifier.class);

    private final BeanMetadataProvider metadataProvider;

    public AnkorSerializerModifier(BeanMetadataProvider metadataProvider) {
        this.metadataProvider = metadataProvider;
    }

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                     BeanDescription beanDesc,
                                                     List<BeanPropertyWriter> beanProperties) {

        for (BeanPropertyWriter beanProperty : beanProperties) {
            checkForBigList(beanProperty);
            checkForBigMap(beanProperty);
        }

        return super.changeProperties(config, beanDesc, beanProperties);
    }

    private PropertyMetadata getPropertyMetadata(BeanPropertyWriter beanProperty) {
        Class<?> beanType = beanProperty.getMember().getDeclaringClass();
        BeanMetadata beanMetadata = metadataProvider.getMetadata(beanType);
        return beanMetadata.getPropertyMetadata(beanProperty.getName());
    }

    private void checkForBigList(BeanPropertyWriter beanProperty) {
        PropertyMetadata propertyMetadata = getPropertyMetadata(beanProperty);
        BigListMetadata bigListMetadata = propertyMetadata.getGenericMetadata(BigListMetadata.class);
        if (bigListMetadata != null) {
            ListToBigListDummyConverter converter = ListToBigListDummyConverter.createFromMetadata(bigListMetadata);
            JsonSerializer serializer = new BigListSerializer(converter);
            //noinspection unchecked
            beanProperty.assignSerializer(serializer); // TODO thread safety!!!
            LOG.info("Assigned {} for property {}", serializer, beanProperty);
        }
    }

    private void checkForBigMap(BeanPropertyWriter beanProperty) {
        PropertyMetadata propertyMetadata = getPropertyMetadata(beanProperty);
        BigMapMetadata bigMapMetadata = propertyMetadata.getGenericMetadata(BigMapMetadata.class);
        if (bigMapMetadata != null) {
            MapToBigMapDummyConverter converter = MapToBigMapDummyConverter.createFromMetadata(bigMapMetadata);
            JsonSerializer serializer = new BigMapSerializer(converter);
            //noinspection unchecked
            beanProperty.assignSerializer(serializer); // TODO thread safety!!!
            LOG.info("Assigned {} for property {}", serializer, beanProperty);
        }
    }

}
