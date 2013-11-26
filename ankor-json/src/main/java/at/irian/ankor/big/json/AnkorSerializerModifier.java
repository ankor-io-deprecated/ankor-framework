package at.irian.ankor.big.json;

import at.irian.ankor.big.AnkorBigList;
import at.irian.ankor.big.AnkorBigMap;
import at.irian.ankor.big.modify.ListToBigListDummyConverter;
import at.irian.ankor.big.modify.MapToBigMapDummyConverter;
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


    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                     BeanDescription beanDesc,
                                                     List<BeanPropertyWriter> beanProperties) {

        for (BeanPropertyWriter beanProperty : beanProperties) {
            checkForBigListAnnotation(beanProperty);
            checkForBigMapAnnotation(beanProperty);
        }

        return super.changeProperties(config, beanDesc, beanProperties);
    }

    private void checkForBigListAnnotation(BeanPropertyWriter beanProperty) {
        AnkorBigList bigListAnn = beanProperty.getAnnotation(AnkorBigList.class);
        if (bigListAnn != null) {
            ListToBigListDummyConverter converter = ListToBigListDummyConverter.createFromAnnotation(bigListAnn);
            JsonSerializer serializer = new BigListSerializer(converter);
            //noinspection unchecked
            beanProperty.assignSerializer(serializer);
            LOG.info("Assigned {} for property {}", serializer, beanProperty);
        }
    }

    private void checkForBigMapAnnotation(BeanPropertyWriter beanProperty) {
        AnkorBigMap bigMapAnn = beanProperty.getAnnotation(AnkorBigMap.class);
        if (bigMapAnn != null) {
            MapToBigMapDummyConverter converter = MapToBigMapDummyConverter.createFromAnnotation(bigMapAnn);
            JsonSerializer serializer = new BigMapSerializer(converter);
            //noinspection unchecked
            beanProperty.assignSerializer(serializer);
            LOG.info("Assigned {} for property {}", serializer, beanProperty);
        }
    }

}
