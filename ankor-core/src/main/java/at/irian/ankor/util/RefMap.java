package at.irian.ankor.util;

import at.irian.ankor.change.Change;
import at.irian.ankor.event.source.CustomSource;
import at.irian.ankor.ref.MapRef;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.impl.RefImplementor;

import java.util.*;

/**
 * @author Manfred Geiler
 */
public class RefMap<K,V> extends AbstractMap<K,V> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefMap.class);

    protected final MapRef mapRef;

    public RefMap(Ref mapRef) {
        this.mapRef = mapRef.toMapRef();
    }

    private Map<K,V> getReferencedMap() {
        Map<K, V> map = mapRef.getValue();
        return map != null ? map : Collections.<K,V>emptyMap();
    }


    // read methods

    @Override
    public int size() {
        return getReferencedMap().size();
    }

    @Override
    public boolean isEmpty() {
        return getReferencedMap().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return getReferencedMap().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return getReferencedMap().containsValue(value);
    }

    @Override
    public V get(Object key) {
        return getReferencedMap().get(key);
    }


    // write methods

    @SuppressWarnings({"SuspiciousMethodCalls"})
    @Override
    public V put(Object key, Object value) {
        if (key == null || !(key instanceof String)) {
            throw new IllegalArgumentException("unsupported key " + key);
        }
        V oldVal = getReferencedMap().get(key);
        ((RefImplementor)mapRef.appendLiteralKey((String)key)).apply(new CustomSource(this),
                                                                     Change.valueChange(value));
        return oldVal;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public V remove(Object key) {
        V oldVal = getReferencedMap().get(key);
        ((RefImplementor)mapRef).apply(new CustomSource(this), Change.deleteChange(key));
        return oldVal;
    }



    // write methods that may be optimized later

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        super.putAll(m);
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public Set<K> keySet() {
        return super.keySet();
    }

    @Override
    public Collection<V> values() {
        return super.values();
    }

    @Override
    public Set<Entry<K,V>> entrySet() {
        return new AbstractSet<Entry<K, V>>() {
            @Override
            public Iterator<Entry<K, V>> iterator() {
                final Iterator<Entry<K, V>> iterator = getReferencedMap().entrySet().iterator();
                return new Iterator<Entry<K, V>>() {

                    private Entry<K, V> lastEntry = null;

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Entry<K, V> next() {
                        lastEntry = iterator.next();
                        return lastEntry;
                    }

                    @Override
                    public void remove() {
                        if (lastEntry == null) {
                            throw new IllegalStateException();
                        }
                        iterator.remove();
                        ((RefImplementor)mapRef).signal(new CustomSource(RefMap.this),
                                                        Change.deleteChange(lastEntry.getKey()));
                    }
                };
            }

            @Override
            public int size() {
                return RefMap.this.size();
            }
        };
    }
}
