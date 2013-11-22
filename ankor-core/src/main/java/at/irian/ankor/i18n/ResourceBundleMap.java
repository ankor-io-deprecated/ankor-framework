package at.irian.ankor.i18n;

import java.util.*;

/**
 * @author Manfred Geiler
 */
public class ResourceBundleMap implements Map<String,String> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ResourceBundleMap.class);

    private final ResourceBundle resourceBundle;

    public ResourceBundleMap(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public static ResourceBundleMap getBundleMap(String resourceBundleName, Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(resourceBundleName, locale);
        return new ResourceBundleMap(bundle);
    }

    @Override
    public int size() {
        return resourceBundle.keySet().size();
    }

    @Override
    public boolean isEmpty() {
        return resourceBundle.keySet().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        //noinspection SuspiciousMethodCalls
        return resourceBundle.keySet().contains(key);
    }

    @Override
    public boolean containsValue(Object value) {
        for (String k : resourceBundle.keySet()) {
            String v = resourceBundle.getString(k);
            if (v.equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String get(Object key) {
        return resourceBundle.getString((String)key);
    }

    @Override
    public String put(String key, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> keySet() {
        return resourceBundle.keySet();
    }

    @Override
    public Collection<String> values() {
        List<String> result = new ArrayList<String>();
        for (String k : resourceBundle.keySet()) {
            result.add(resourceBundle.getString(k));
        }
        return result;
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        Set<Entry<String, String>> result = new HashSet<Entry<String, String>>();
        for (String k : resourceBundle.keySet()) {
            final String key = k;
            final String value = resourceBundle.getString(key);
            result.add(new Entry<String, String>() {
                @Override
                public String getKey() {
                    return key;
                }

                @Override
                public String getValue() {
                    return value;
                }

                @Override
                public String setValue(String value) {
                    throw new UnsupportedOperationException();
                }
            });
        }
        return result;
    }
}
