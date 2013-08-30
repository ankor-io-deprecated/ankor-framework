package at.irian.ankor.viewmodel;

import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Thomas Spiegl
 */
@SuppressWarnings("NullableProblems")
public abstract class ViewModelMapBase<K, V> implements Map<K, V> {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MapViewModel.class);

    protected Map<K, V> map;

    protected ViewModelMapBase(Ref viewModelRef, Map<K, V> map) {
        AnkorPatterns.initViewModel(this, viewModelRef);
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
        // todo  propagate change
        return map.put(key, value);
    }

    @Override
    public V remove(Object key) {
        // todo  propagate change
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        // todo  propagate changes
        map.putAll(m);
    }

    @Override
    public void clear() {
        // todo  propagate changes
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        // todo  propagate key set changes
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        // todo  propagate value collection changes
        return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        // todo  propagate entry set changes
        return map.entrySet();
    }
}
