package at.irian.ankor.bigcoll;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class BigCollectionSerializerConverter extends StdConverter<Collection, Collection> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BigCollectionSerializerConverter.class);

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    public Collection convert(Collection actualCollection) {
        Map<String,Integer> dummyEntry = Collections.singletonMap("@size", actualCollection.size());
        Collection<Map<String,Integer>> dummyCollection = Collections.singletonList(dummyEntry);
        return dummyCollection;
    }

}
