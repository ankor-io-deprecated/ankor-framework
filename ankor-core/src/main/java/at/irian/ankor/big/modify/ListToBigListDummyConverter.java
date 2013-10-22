package at.irian.ankor.big.modify;

import at.irian.ankor.big.AnkorBigList;

import java.util.*;

/**
 * @author Manfred Geiler
 */
public class ListToBigListDummyConverter {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ListToBigListDummyConverter.class);

    public static final String SIZE_KEY = "@size";
    public static final String SUBSTITUTE_KEY = "@subst";
    public static final String INITIAL_ELEMENTS_KEY = "@init";
    public static final String AHEAD_SEND_SIZE_KEY = "@ahead";

    private final int thresholdSize;
    private final int initialSize;
    private final Object missingElementSubstitute;
    private final int aheadSendSize;

    public ListToBigListDummyConverter(int thresholdSize,
                                       int initialSize,
                                       Object missingElementSubstitute,
                                       int aheadSendSize) {
        this.thresholdSize = thresholdSize;
        this.initialSize = initialSize;
        this.missingElementSubstitute = missingElementSubstitute;
        this.aheadSendSize = aheadSendSize;
    }

    public static ListToBigListDummyConverter createFromAnnotation(AnkorBigList ann) {
        Class<?> missingElementSubstituteType = ann.missingElementSubstitute();
        Object missingElementSubstitute = null;
        if (missingElementSubstituteType != AnkorBigList.Null.class) {
            try {
                missingElementSubstitute = missingElementSubstituteType.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Unable to instantiate " + missingElementSubstitute);
            }
        }
        return new ListToBigListDummyConverter(ann.thresholdSize(),
                                               ann.initialSendSize(),
                                               missingElementSubstitute,
                                               ann.aheadSendSize());
    }


    @SuppressWarnings("UnnecessaryLocalVariable")
    public Collection convert(Collection actualCollection) {
        if (actualCollection == null) {
            return null;
        }

        if (actualCollection.size() <= thresholdSize) {
            return actualCollection;
        }

        Map<String, Object> bigListAttributes = new LinkedHashMap<String, Object>();
        bigListAttributes.put(SIZE_KEY,  actualCollection.size());
        bigListAttributes.put(SUBSTITUTE_KEY, missingElementSubstitute);
        bigListAttributes.put(AHEAD_SEND_SIZE_KEY,  aheadSendSize);

        if (initialSize > 0) {
            List initialList = new ArrayList(initialSize);
            Iterator iterator = actualCollection.iterator();
            for(int i = 0; i < initialSize && iterator.hasNext(); i++) {
                //noinspection unchecked
                initialList.add(iterator.next());
            }
            bigListAttributes.put(INITIAL_ELEMENTS_KEY, initialList);
        }

        List<Map<String,Object>> dummyList = Collections.singletonList(bigListAttributes);
        return dummyList;
    }

}
