package at.irian.ankor.fx.binding.cache;

import at.irian.ankor.ref.Ref;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class BindingCache {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BindingCache.class);

    private final Map<CacheKey,ObservableValue<?>> observableValuesCache = new HashMap<>();
    private final Map<CacheKey,Property<?>> propertiesCache = new HashMap<>();
    private final Map<CacheKey,ObservableList<?>> observableListCache = new HashMap<>();
    private final Map<CacheKey,ObservableValue<ObservableList<?>>> observableValueListCache = new HashMap<>();


    @SuppressWarnings("unchecked")
    public <T> ObservableValue<T> getObservableValue(Ref ref, Object defaultValue, Callback<Ref,ObservableValue<T>> creator) {
        CacheKey key = getKey(ref, defaultValue);
        ObservableValue result = observableValuesCache.get(key);
        if (result == null) {
            result = creator.call(ref);
            observableValuesCache.put(key, result);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> Property<T> getProperty(Ref ref, Object defaultValue, Callback<Ref,Property<T>> creator) {
        CacheKey key = getKey(ref, defaultValue);
        Property result = propertiesCache.get(key);
        if (result == null) {
            result = creator.call(ref);
            propertiesCache.put(key, result);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> ObservableList<T> getObservableList(Ref ref, Object defaultValue, Callback<Ref,ObservableList<T>> creator) {
        CacheKey key = getKey(ref, defaultValue);
        ObservableList result = observableListCache.get(key);
        if (result == null) {
            result = creator.call(ref);
            observableListCache.put(key, result);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> ObservableValue<ObservableList<T>> getObservableValueList(Ref ref, Object defaultValue,
                                                                         Callback<Ref,ObservableValue<ObservableList<T>>> creator) {
        CacheKey key = getKey(ref, defaultValue);
        ObservableValue result = observableValueListCache.get(key);
        if (result == null) {
            result = creator.call(ref);
            observableValueListCache.put(key, result);
        }
        return result;
    }


    public CacheKey getKey(Ref ref, Object defaultValue) {
        return new CacheKey(ref, defaultValue);
    }

    public static class CacheKey {
        private final Ref ref;
        private final Object defaultValue;

        public CacheKey(Ref ref, Object defaultValue) {
            this.ref = ref;
            this.defaultValue = defaultValue;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }
            CacheKey otherKey = (CacheKey) other;
            return !(defaultValue != null ? !defaultValue.equals(otherKey.defaultValue) : otherKey.defaultValue != null)
                   && ref.equals(otherKey.ref);
        }

        @Override
        public int hashCode() {
            int result = ref.hashCode();
            result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
            return result;
        }
    }

}
