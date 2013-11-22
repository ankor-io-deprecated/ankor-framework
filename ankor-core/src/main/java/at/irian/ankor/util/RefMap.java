package at.irian.ankor.util;

import at.irian.ankor.ref.Ref;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Manfred Geiler
 */
public class RefMap implements Map {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefMap.class);

    private Ref mapRef;

    public RefMap(Ref mapRef) {
        this.mapRef = mapRef;
    }

    protected RefMap() {}

    protected void setMapRef(Ref mapRef) {
        this.mapRef = mapRef;
    }

    private Map getMap() {
        return mapRef.getValue();
    }

    @Override
    public int size() {
        return getMap().size();
    }

    @Override
    public boolean isEmpty() {
        return getMap().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return getMap().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return getMap().containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return getMap().get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object put(Object key, Object value) {
        return getMap().put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return getMap().remove(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void putAll(Map m) {
        getMap().putAll(m);
    }

    @Override
    public void clear() {
        getMap().clear();
    }

    @Override
    public Set keySet() {
        return getMap().keySet();
    }

    @Override
    public Collection values() {
        return getMap().values();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Entry> entrySet() {
        return getMap().entrySet();
    }
}
