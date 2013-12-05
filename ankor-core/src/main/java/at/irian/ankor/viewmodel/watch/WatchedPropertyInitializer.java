package at.irian.ankor.viewmodel.watch;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.metadata.WatchedPropertyMetadata;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Manfred Geiler
 */
public class WatchedPropertyInitializer {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(WatchedPropertyInitializer.class);

    public void init(Object bean, Ref beanRef, WatchedPropertyMetadata info) {
        Field field = info.getField();
        Class<?> fieldType = field.getType();
        if (List.class.isAssignableFrom(fieldType)) {

            try {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }

                List list = (List)field.get(bean);

                if (list == null) {
                    LOG.error("Cannot watch " + field + " with value null");
                    return;
                }

                if (list instanceof WatchedList) {
                    LOG.warn("Value of " + field + " is already a WatchedList - not wrapped");
                    return;
                }

                Ref fieldRef = beanRef.appendPath(field.getName());
                int diffThreshold = info.getDiffThreshold();
                //noinspection unchecked
                field.set(bean, new WatchedList(fieldRef.toCollectionRef(), list).withDiffThreshold(diffThreshold));

            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error setting field " + field, e);
            }

        }

    }

}
