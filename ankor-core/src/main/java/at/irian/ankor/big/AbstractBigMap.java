package at.irian.ankor.big;

import at.irian.ankor.serialization.AnkorIgnore;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.*;

/**
 * @author Manfred Geiler
 */
public abstract class AbstractBigMap<K,V> implements BigMap<K,V> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AbstractBigMap.class);

    private static class NullDummy {}

    @AnkorIgnore
    private Map<K, Reference> elements;

    private int size;

    protected AbstractBigMap(int size) {
        this.elements = new HashMap<K, Reference>();
        this.size = size;
    }

    @SuppressWarnings("unchecked")
    protected Reference createReferenceFor(V value) {
        return value != null ? new SoftReference(value) : new SoftReference(new NullDummy());
    }

    @Override
    public void reset() {
        this.elements.clear();
    }

    @Override
    public void cleanup() {
        Iterator<Entry<K, Reference>> iterator = elements.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<K, Reference> next = iterator.next();
            if (next.getValue().get() == null) {
                iterator.remove();
            }
        }
    }

    @Override
    public boolean isAvailable(K key) {
        Reference reference = elements.get(key);
        return reference != null && reference.get() != null;
    }

    protected abstract boolean containsMissingValue(Object key);
    protected abstract V getMissingValue(Object key);

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<Entry<K, V>>() {
            @Override
            public Iterator<Entry<K, V>> iterator() {
                throw new UnsupportedOperationException();
            }

            @Override
            public int size() {
                return size;
            }
        };
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings({"SuspiciousMethodCalls", "SimplifiableIfStatement"})
    @Override
    public boolean containsKey(Object key) {
        Reference reference = elements.get(key);
        if (reference == null) {
            return containsMissingValue(key);
        }
        Object value = reference.get();
        if (value instanceof NullDummy) {
            return true;
        } else if (value == null) {
            return containsMissingValue(key);
        } else {
            return true;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(Object key) {
        Reference reference = elements.get(key);
        if (reference == null) {
            return getMissingValue(key);
        }
        Object value = reference.get();
        if (value instanceof NullDummy) {
            return null;
        } else if (value == null) {
            return getMissingValue(key);
        } else {
            return (V)value;
        }
    }

    private V getReferencedValue(Reference referenced) {
        if (referenced != null) {
            Object v = referenced.get();
            if (v instanceof NullDummy) {
                return null;
            } else {
                //noinspection unchecked
                return (V) v;
            }
        } else {
            return null;
        }
    }

    @Override
    public V put(K key, V value) {
        Reference prev = elements.put(key, createReferenceFor(value));
        // we cannot adjust the size here ... any solution for this?
        return getReferencedValue(prev);
    }

    @Override
    public V remove(Object key) {
        return getReferencedValue(elements.remove(key));
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.elements.clear();
        this.size = 0;
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException();
    }
}
