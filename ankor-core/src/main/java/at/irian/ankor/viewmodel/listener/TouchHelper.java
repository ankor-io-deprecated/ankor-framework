package at.irian.ankor.viewmodel.listener;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.metadata.TouchedPropertyMetadata;

/**
 * @author Manfred Geiler
 */
class TouchHelper {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TouchHelper.class);

    private final Ref baseRef;

    TouchHelper(Ref baseRef) {
        this.baseRef = baseRef;
    }

    public void touch(Iterable<TouchedPropertyMetadata> touchedPropertyInfos) {
        for (TouchedPropertyMetadata touchedPropertyMetadata : touchedPropertyInfos) {
            touch(touchedPropertyMetadata.getPropertyPath());
        }
    }

    public void touch(String subPath) {
        baseRef.appendPath(subPath).signalValueChange();
    }
}
