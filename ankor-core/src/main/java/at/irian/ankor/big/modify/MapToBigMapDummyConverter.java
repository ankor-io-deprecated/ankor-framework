package at.irian.ankor.big.modify;

import at.irian.ankor.big.AnkorBigMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class MapToBigMapDummyConverter {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ListToBigListDummyConverter.class);

    public static final String SIZE_KEY = "@size";
    public static final String SUBSTITUTE_KEY = "@subst";

    private final int thresholdSize;
    private final int initialSize;
    private final Object missingValueSubstitute;

    public MapToBigMapDummyConverter(int thresholdSize,
                                     int initialSize,
                                     Object missingValueSubstitute) {
        this.thresholdSize = thresholdSize;
        this.initialSize = initialSize;
        this.missingValueSubstitute = missingValueSubstitute;
    }

    public static MapToBigMapDummyConverter createFromAnnotation(AnkorBigMap ann) {
        Class<?> missingValueSubstituteType = ann.missingValueSubstitute();
        Object missingValueSubstitute = null;
        if (missingValueSubstituteType != AnkorBigMap.Null.class) {
            try {
                missingValueSubstitute = missingValueSubstituteType.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Unable to instantiate " + missingValueSubstituteType);
            }
        }
        return new MapToBigMapDummyConverter(ann.threshold(),
                                               ann.initialSize(),
                                               missingValueSubstitute);
    }


    @SuppressWarnings("unchecked")
    public Map convert(Map actualMap) {
        if (actualMap == null) {
            return null;
        }

        if (actualMap.size() < thresholdSize) {
            return actualMap;
        }

        Map dummyMap = new HashMap(actualMap.size() + 2);
        dummyMap.put(SIZE_KEY,  actualMap.size());
        dummyMap.put(SUBSTITUTE_KEY, missingValueSubstitute);

        if (initialSize > 0) {
            Iterator<Map.Entry> iterator = actualMap.entrySet().iterator();
            for(int i = 0; i < initialSize && iterator.hasNext(); i++) {
                Map.Entry entry = iterator.next();
                dummyMap.put(entry.getKey(), entry.getValue());
            }
        }

        return dummyMap;
    }

}
