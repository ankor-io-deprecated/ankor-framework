package at.irian.ankor.annotation;

import at.irian.ankor.ref.Ref;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Thomas Spiegl
 */
@SuppressWarnings("NullableProblems")
public abstract class AnnotationAwareMapViewModelBase<K, V> extends AnnotationAwareViewModelBase implements Map<K, V> {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MapViewModel.class);

    protected Map<K, V> map;

    protected AnnotationAwareMapViewModelBase(Ref viewModelRef, Map<K, V> map) {
        super(viewModelRef);
        this.map = map;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return map.get(key);
    }

    @Override
    public V put(K key, V value) {
        return map.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }
}
