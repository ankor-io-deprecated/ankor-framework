package at.irian.ankor.big.modify;

import at.irian.ankor.big.MissingPropertyActionFiringBigList;
import at.irian.ankor.big.MissingPropertyActionFiringBigMap;
import at.irian.ankor.change.Change;
import at.irian.ankor.ref.Ref;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class SimpleTreeBigDataChangeModifier {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleTreeBigDataChangeModifier.class);

    public Change modify(Change change, Ref changedProperty) {
        Object value = change.getValue();
        return change.withValue(handleChangeValueAfterReceive(changedProperty, value));
    }

    private Object handleChangeValueAfterReceive(Ref property, Object value) {
        if (value == null) {
            return null;
        } else if (isBigList(value)) {
            return handleBigList(property, (List) value);
        } else if (isBigMap(value)) {
            return handleBigMap(property, (Map) value);
        } else if (value instanceof List) {
            return handleList(property, (List) value);
        } else if (value instanceof Map) {
            return handleMap(property, (Map) value);
        } else {
            return handleObject(property, value);
        }
    }

    @SuppressWarnings("UnusedParameters")
    protected Object handleObject(Ref property, Object value) {
        return value;
    }

    @SuppressWarnings("unchecked")
    protected List handleList(Ref property, List list) {
        for (int i = 0; i < list.size(); i++) {
            Object o1 = list.get(i);
            Object o2 = handleChangeValueAfterReceive(property.appendIndex(i), o1);
            if (o2 != o1) {
                list.set(i, o2);
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    protected Map handleMap(Ref property, Map map) {
        for (Map.Entry entry : (Iterable<Map.Entry>) map.entrySet()) {
            Object key = entry.getKey();
            if (key instanceof String) {
                Object o1 = entry.getValue();
                Object o2 = handleChangeValueAfterReceive(property.appendLiteralKey((String) key), o1);
                if (o2 != o1) {
                    entry.setValue(o2);
                }
            }
        }
        return map;
    }

    private boolean isBigList(Object value) {
        if (value instanceof Collection) {
            if (((Collection) value).size() == 1) {
                Object firstEntry = ((Collection) value).iterator().next();
                if (firstEntry instanceof Map) {
                    Object size = ((Map) firstEntry).get(ListToBigListDummyConverter.SIZE_KEY);
                    if (size != null && size instanceof Number) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    protected List handleBigList(Ref property, List serializedList) {
        int size = getBigListSize(serializedList);
        Object substitute = getBigListMissingElementSubstitute(serializedList);
        int chunkSize = getChunkSize(serializedList);
        List initialElements = getBigListInitialElements(serializedList);
        return new MissingPropertyActionFiringBigList(size, property, substitute, chunkSize, initialElements);
    }

    private int getBigListSize(List list) {
        Object size = ((Map) list.get(0)).get(ListToBigListDummyConverter.SIZE_KEY);
        return ((Number)size).intValue();
    }

    private Object getBigListMissingElementSubstitute(List list) {
        return ((Map) list.get(0)).get(ListToBigListDummyConverter.SUBSTITUTE_KEY);
    }

    private int getChunkSize(List list) {
        Object size = ((Map) list.get(0)).get(ListToBigListDummyConverter.CHUNK_SIZE_KEY);
        return ((Number)size).intValue();
    }

    private List getBigListInitialElements(List list) {
        List initialList = (List) ((Map) list.get(0)).get(ListToBigListDummyConverter.INITIAL_LIST_KEY);
        if (initialList != null) {
            return initialList;
        } else {
            return Collections.emptyList();
        }
    }



    private boolean isBigMap(Object value) {
        if (value instanceof Map) {
            Object size = ((Map) value).get(MapToBigMapDummyConverter.SIZE_KEY);
            if (size != null && size instanceof Number) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    protected Map handleBigMap(Ref property, Map serializedMap) {
        int size = getBigMapSize(serializedMap);
        Object substitute = getBigMapMissingValueSubstitute(serializedMap);
        Map bigMap = new MissingPropertyActionFiringBigMap(size, property, false, substitute);
        for (Map.Entry entry : (Iterable<? extends Map.Entry>) serializedMap.entrySet()) {
            Object key = entry.getKey();
            if (!isSpecialBigMapKey(key)) {
                bigMap.put(key, entry.getValue());
            }
        }
        return bigMap;
    }

    private boolean isSpecialBigMapKey(Object key) {
        return MapToBigMapDummyConverter.SIZE_KEY.equals(key) || MapToBigMapDummyConverter.SUBSTITUTE_KEY.equals(key);
    }

    private int getBigMapSize(Map map) {
        return ((Number) map.get(MapToBigMapDummyConverter.SIZE_KEY)).intValue();
    }

    private Object getBigMapMissingValueSubstitute(Map map) {
        return map.get(MapToBigMapDummyConverter.SUBSTITUTE_KEY);
    }

}
