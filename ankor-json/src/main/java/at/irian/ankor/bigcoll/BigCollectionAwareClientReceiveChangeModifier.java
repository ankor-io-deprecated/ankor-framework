package at.irian.ankor.bigcoll;

import at.irian.ankor.change.Change;
import at.irian.ankor.messaging.ChangeModifier;
import at.irian.ankor.ref.Ref;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class BigCollectionAwareClientReceiveChangeModifier implements ChangeModifier {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BigCollectionAwareClientReceiveChangeModifier.class);

    @Override
    public Change modify(Change change, Ref changedProperty) {
        Object value = change.getValue();
        return change.withValue(handle(changedProperty, value));
    }

    @SuppressWarnings("unchecked")
    private Object handle(Ref property, Object value) {
        if (value == null) {
            return null;
        }
        if (isBigCollection(value)) {
            int size = getSize((Collection)value);
            return new ActionFiringBigList(size, property);
        }

        if (value instanceof List) {
            List list = (List) value;
            for (int i = 0; i < list.size(); i++) {
                Object o1 = list.get(i);
                Object o2 = handle(property.appendIndex(i), o1);
                if (o2 != o1) {
                    list.set(i, o2);
                }
            }
        } else if (value instanceof Map) {
            Map<Object,Object> map = (Map) value;
            for (Map.Entry<Object,Object> entry : map.entrySet()) {
                Object key = entry.getKey();
                if (key instanceof String) {
                    Object o1 = entry.getValue();
                    Object o2 = handle(property.appendLiteralKey((String)key), o1);
                    if (o2 != o1) {
                        entry.setValue(o2);
                    }
                }
            }
        }

        return value;
    }

    private boolean isBigCollection(Object value) {
        if (value instanceof Collection) {
            if (((Collection) value).size() == 1) {
                Object firstEntry = ((Collection) value).iterator().next();
                if (firstEntry instanceof Map) {
                    Object size = ((Map) firstEntry).get("@size");
                    if (size != null && size instanceof Number) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int getSize(Collection value) {
        Object firstEntry = value.iterator().next();
        Object size = ((Map) firstEntry).get("@size");
        return ((Number)size).intValue();
    }



}
