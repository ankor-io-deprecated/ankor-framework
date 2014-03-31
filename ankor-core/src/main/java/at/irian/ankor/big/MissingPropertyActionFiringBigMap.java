package at.irian.ankor.big;

import at.irian.ankor.action.Action;
import at.irian.ankor.serialization.AnkorIgnore;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.event.MissingPropertyActionEventListener;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Manfred Geiler
 */
public class MissingPropertyActionFiringBigMap<K extends String,V> extends AbstractBigMap<K,V> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MissingPropertyActionFiringBigList.class);

    @AnkorIgnore
    private final Ref mapRef;

    @AnkorIgnore
    private final Set<Object> pendingRequests = new CopyOnWriteArraySet<Object>();

    @AnkorIgnore
    private final boolean missingValueDefaultContained;

    @AnkorIgnore
    private final V missingValueSubstitute;

    public MissingPropertyActionFiringBigMap(int size,
                                             Ref mapRef,
                                             boolean missingValueDefaultContained,
                                             V missingValueSubstitute) {
        super(size);
        this.mapRef = mapRef;
        this.missingValueDefaultContained = missingValueDefaultContained;
        this.missingValueSubstitute = missingValueSubstitute;
    }

    @Override
    protected boolean containsMissingValue(Object key) {
        return missingValueDefaultContained;
    }

    @Override
    protected V getMissingValue(Object key) {
        if (pendingRequests.add(key)) {
            mapRef.appendLiteralKey((String)key).fire(new Action(MissingPropertyActionEventListener.MISSING_PROPERTY_ACTION_NAME));
        }
        return missingValueSubstitute;
    }

    @Override
    public void clear() {
        pendingRequests.clear();
        super.clear();
    }

    @Override
    public V put(K key, V value) {
        pendingRequests.remove(key);
        return super.put(key, value);
    }

    @Override
    public V remove(Object key) {
        pendingRequests.remove(key);
        return super.remove(key);
    }
}
