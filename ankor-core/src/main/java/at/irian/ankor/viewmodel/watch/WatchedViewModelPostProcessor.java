package at.irian.ankor.viewmodel.watch;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelPostProcessor;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;
import at.irian.ankor.viewmodel.metadata.PropertyMetadata;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Manfred Geiler
 */
public class WatchedViewModelPostProcessor implements ViewModelPostProcessor {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(WatchedViewModelPostProcessor.class);

    @Override
    public void postProcess(Object viewModelObject, Ref viewModelRef, BeanMetadata metadata) {
        for (PropertyMetadata propertyMetadata : metadata.getPropertiesMetadata()) {
            WatchedPropertyMetadata md = propertyMetadata.getGenericMetadata(WatchedPropertyMetadata.class);
            if (md != null) {
                init(viewModelObject, viewModelRef, md);
            }
        }
    }

    private void init(Object bean, Ref beanRef, WatchedPropertyMetadata info) {
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
