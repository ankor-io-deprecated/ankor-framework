package at.irian.ankor.fx.binding.value;

import at.irian.ankor.change.Change;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Package local helper class for {@link at.irian.ankor.fx.binding.value.ObservableListRef}.
 *
 * @author Manfred Geiler
 */
class FxMapChangeHelper<K,V> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FxListChangeHelper.class);

    private final ObservableMap<K,V> observableMap;

    public FxMapChangeHelper(ObservableMap<K,V> observableMap) {
        this.observableMap = observableMap;
    }

    @SuppressWarnings("unchecked")
    public Set<MapChange<K, V>> toFxChanges(Change ankorChange) {
        switch (ankorChange.getType()) {
            case insert:
                return Collections.singleton(new MapChange<>(observableMap, (K)ankorChange.getKey(),
                                                             true, false, (V)ankorChange.getValue(), null));
            case delete:
                return Collections.singleton(new MapChange<>(observableMap, (K)ankorChange.getKey(),
                                                             false, true, null, null)); // todo  we have no old value here!
            case replace:
                Object value = ankorChange.getValue();
                if (value instanceof Collection) {
                    value = ((Collection) value).iterator().next();
                }
                return Collections.singleton(new MapChange<>(observableMap, (K)ankorChange.getKey(),
                                                             true, true, (V)value, null)); // todo  we have no old value here!
            case value:
            default:
                throw new IllegalArgumentException("Unsupported change " + ankorChange);
        }
//        ArrayList<MapChangeListener.Change<K, V>> changes = new ArrayList<>();
//        for (Map.Entry<K, V> entry : observableMap.entrySet()) {
//            changes.add(new MapChange<>(observableMap, entry.getKey(), true, true, entry.getValue(), entry.getValue()));
//        }
//        return changes;
    }


    @SuppressWarnings("unchecked")
    private static class MapChange<K,V> extends MapChangeListener.Change<K,V> {

        private final K key;
        private final boolean wasAdded;
        private final boolean wasRemoved;
        private final V valueAdded;
        private final V valueRemoved;

        private MapChange(ObservableMap<K, V> kvObservableMap,
                          K key,
                          boolean wasAdded,
                          boolean wasRemoved,
                          V valueAdded,
                          V valueRemoved) {
            super(kvObservableMap);
            this.key = key;
            this.wasAdded = wasAdded;
            this.wasRemoved = wasRemoved;
            this.valueAdded = valueAdded;
            this.valueRemoved = valueRemoved;
        }

        @Override
        public boolean wasAdded() {
            return wasAdded;
        }

        @Override
        public boolean wasRemoved() {
            return wasRemoved;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValueAdded() {
            return valueAdded;
        }

        @Override
        public V getValueRemoved() {
            return valueRemoved;
        }
    }


}
