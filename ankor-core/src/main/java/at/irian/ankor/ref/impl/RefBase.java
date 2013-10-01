package at.irian.ankor.ref.impl;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.base.Wrapper;
import at.irian.ankor.change.Change;
import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeType;
import at.irian.ankor.change.OldValuesAwareChangeEvent;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.event.PropertyWatcher;
import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.ref.*;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public abstract class RefBase implements Ref, RefImplementor, CollectionRef, MapRef {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefBase.class);

    private final RefContextImplementor refContext;

    protected RefBase(RefContextImplementor refContext) {
        this.refContext = refContext;
    }

    @Override
    public void setValue(final Object newValue) {
        apply(Change.valueChange(newValue));
    }

    @Override
    public void delete(String key) {
        apply(Change.deleteChange(key));
    }

    @Override
    public void delete(int idx) {
        apply(Change.deleteChange(idx));
    }

    @Override
    public void delete() {
        ((RefImplementor)parent()).apply(Change.deleteChange(propertyName()));
    }

    @Override
    public void insert(int idx, Object value) {
        apply(Change.insertChange(idx, value));
    }

    @Override
    public void add(Object value) {
        Object collOrArray = getValue();
        if (collOrArray == null) {
            LOG.error("Cannot add to null list or array");
            return;
        }

        int size;
        if (collOrArray instanceof Collection) {
            size = ((Collection) collOrArray).size();
        } else if (collOrArray.getClass().isArray()) {
            size = Array.getLength(collOrArray);
        } else {
            throw new IllegalArgumentException("collection or array of type " + collOrArray.getClass());
        }

        apply(Change.insertChange(size, value));
    }

    @Override
    public void apply(Change change) {

        // remember old value of the referenced property
        Object oldValue;
        try {
            oldValue = getValue();
        } catch (IllegalStateException e) {
            oldValue = null;
        }

        // remember old values of the watched properties
        Map<Ref, Object> oldWatchedValues = getOldWatchedValues();

        ChangeType changeType = change.getType();
        switch(changeType) {
            case new_value:
                handleNewValueChange(change.getValue());
                break;

            case insert:
                handleInsertChange(oldValue, change.getKey(), change.getValue());
                break;

            case delete:
                handleDeleteChange(oldValue, change.getKey());
                break;

            default:
                throw new IllegalArgumentException("Unsupported change type " + changeType);
        }

        // fire change event
        ChangeEvent changeEvent
                = new OldValuesAwareChangeEvent(this, change, oldValue, oldWatchedValues);
        context().modelContext().getEventDispatcher().dispatch(changeEvent);
    }

    @Override
    public void signalValueChange() {
        signal(Change.valueChange(getValue()));
    }

    @Override
    public void signal(Change change) {
        ChangeEvent changeEvent = new ChangeEvent(this, change);
        context().modelContext().getEventDispatcher().dispatch(changeEvent);
    }

    private void handleNewValueChange(Object newValue) {
        Class<?> type = getType();
        if (Wrapper.class.isAssignableFrom(type)) {
            Object newValueUnwrapped = unwrapIfNecessary(newValue);
            internalSetWrapperValue(type, newValue, newValueUnwrapped);
        } else {
            internalSetValue(newValue);
        }
    }

    @SuppressWarnings({"unchecked", "SuspiciousSystemArraycopy"})
    private void handleInsertChange(Object listOrArray, Object key, Object value) {
        if (listOrArray instanceof List) {
            int idx = asIndex(key);
            List list = (List)listOrArray;
            if (idx == list.size()) {
                list.add(value);
            } else {
                list.add(idx, value);
            }
        } else if (listOrArray.getClass().isArray()) {
            int idx = asIndex(key);
            int length = Array.getLength(listOrArray);
            Class<?> componentType = listOrArray.getClass().getComponentType();
            Object newArray = Array.newInstance(componentType, length + 1);
            if (idx > 0) {
                System.arraycopy(listOrArray, 0, newArray, 0, idx);
            }
            Array.set(listOrArray, idx, value);
            if (idx < length) {
                System.arraycopy(listOrArray, idx, newArray, idx + 1, length - idx);
            }
        } else {
            throw new IllegalArgumentException("list/array of type " + listOrArray.getClass().getName());
        }
    }

    private int asIndex(Object idxObj) {
        if (idxObj instanceof Number) {
            return ((Number) idxObj).intValue();
        } else if (idxObj instanceof String) {
            return Integer.parseInt((String) idxObj);
        } else {
            throw new IllegalArgumentException("list/array index of type " + idxObj.getClass());
        }
    }

    private String asMapKey(Object keyObj) {
        if (keyObj instanceof String) {
            return (String)keyObj;
        } else {
            throw new IllegalArgumentException("map key of type " + keyObj.getClass());
        }
    }

    @SuppressWarnings("SuspiciousSystemArraycopy")
    private void handleDeleteChange(Object listOrArrayOrMap, Object key) {
        if (listOrArrayOrMap instanceof List) {
            int idx = asIndex(key);
            ((List) listOrArrayOrMap).remove(idx);
        } else if (listOrArrayOrMap.getClass().isArray()) {
            int idx = asIndex(key);
            int length = Array.getLength(listOrArrayOrMap);
            Class<?> componentType = listOrArrayOrMap.getClass().getComponentType();
            Object newArray = Array.newInstance(componentType, length - 1);
            if (idx > 0) {
                System.arraycopy(listOrArrayOrMap, 0, newArray, 0, idx);
            }
            if (idx + 1 < length) {
                System.arraycopy(listOrArrayOrMap, idx, newArray, idx - 1, length - idx - 1);
            }
        } else if (listOrArrayOrMap instanceof Map) {
            String mapKey = asMapKey(key);
            ((Map) listOrArrayOrMap).remove(mapKey);
        } else if (key instanceof String) {
            // neither List nor Array nor Map, than we assume it is a bean and set the corresponding property to null...
            Ref propertyRef = refFactory().ref(pathSyntax().concat(path(), (String) key));
            ((RefBase)propertyRef).handleNewValueChange(null);
        } else {
            throw new IllegalArgumentException("list/array/map of type " + listOrArrayOrMap.getClass().getName());
        }
    }

    private Map<Ref, Object> getOldWatchedValues() {
        Map<Ref, Object> result = new HashMap<Ref, Object>();
        for (ModelEventListener listener : context().modelContext().getEventListeners()) {
            if (listener instanceof PropertyWatcher) {
                Ref watchedProperty = ((PropertyWatcher) listener).getWatchedProperty();
                if (watchedProperty != null && !(result.containsKey(watchedProperty))) {
                    Object oldWatchedValue;
                    try {
                        oldWatchedValue = watchedProperty.getValue();
                    } catch (IllegalStateException e) {
                        // watched property is currently not valid
                        oldWatchedValue = null;
                    }
                    result.put(watchedProperty, oldWatchedValue);
                }
            }
        }
        return result;
    }

    private Object unwrapIfNecessary(Object newValue) {
        Object newUnwrappedValue;
        if (newValue != null && newValue instanceof Wrapper) {
            newUnwrappedValue = ((Wrapper)newValue).getWrappedValue();
        } else {
            newUnwrappedValue = newValue;
        }
        return newUnwrappedValue;
    }

    private void internalSetWrapperValue(Class<?> type, Object newValue, Object newUnwrappedValue) {
        if (newValue != null && newValue instanceof Wrapper) {

            // replace the wrapper itself
            internalSetValue(newValue);

        } else {

            Wrapper wrapper = internalGetValue();
            if (wrapper == null) {
                wrapper = createNewWrapperInstance(type);
                internalSetValue(wrapper);
            }

            //noinspection unchecked
            wrapper.putWrappedValue(newUnwrappedValue);
        }
    }

    private Wrapper createNewWrapperInstance(Class<?> type) {
        Wrapper wrapper;
        try {
            //noinspection unchecked
            Constructor<Wrapper> refConstructor
                    = ((Class<Wrapper>) type).getDeclaredConstructor(Ref.class);
            if (!refConstructor.isAccessible()) {
                refConstructor.setAccessible(true);
            }
            try {
                wrapper = refConstructor.newInstance(this);
            } catch (Exception e) {
                throw new RuntimeException("Error invoking ref based constructor for type " + type,
                                           e);
            }
        } catch (NoSuchMethodException e) {
            try {
                //noinspection unchecked
                Constructor<Wrapper> defaultConstructor
                        = ((Class<Wrapper>) type).getDeclaredConstructor();
                if (!defaultConstructor.isAccessible()) {
                    defaultConstructor.setAccessible(true);
                }
                try {
                    wrapper = defaultConstructor.newInstance();
                } catch (Exception e1) {
                    throw new RuntimeException("Error invoking default constructor for type "
                                               + type, e1);
                }
            } catch (NoSuchMethodException e1) {
                throw new IllegalStateException(type
                                                + " does not have a ref based or default constructor",
                                                e);
            }
        }
        return wrapper;
    }

    protected abstract void internalSetValue(Object newUnwrappedValue);

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue() {
        T val = internalGetValue();
        if (val != null && val instanceof Wrapper) {
            return (T)((Wrapper)val).getWrappedValue();
        }
        return val;
    }

    protected abstract <T> T internalGetValue();

    protected abstract Class<?> getType();

    @Override
    public RefContextImplementor context() {
        return refContext;
    }

    protected PathSyntax pathSyntax() {
        return refContext.pathSyntax();
    }

    protected RefFactory refFactory() {
        return refContext.refFactory();
    }

    @Override
    public Ref root() {
        return refFactory().ref(pathSyntax().rootOf(path()));
    }

    @Override
    public Ref parent() {
        if (isRoot()) {
            throw new UnsupportedOperationException("root ref has no parent");
        } else {
            return refFactory().ref(pathSyntax().parentOf(path()));
        }
    }

    @Override
    public Ref appendPath(String propertyOrSubPath) {
        return refFactory().ref(pathSyntax().concat(path(), propertyOrSubPath));
    }

    @Override
    public Ref appendIndex(int index) {
        return refFactory().ref(pathSyntax().addArrayIdx(path(), index));
    }

    @Override
    public Ref appendLiteralKey(String literalKey) {
        return refFactory().ref(pathSyntax().addLiteralMapKey(path(), literalKey));
    }

    @Override
    public Ref appendPathKey(String pathKey) {
        return refFactory().ref(pathSyntax().addPathMapKey(path(), pathKey));
    }

    @Override
    public Ref $(String propertyOrSubPath) {
        return appendPath(propertyOrSubPath);
    }

    @Override
    public Ref $(int index) {
        return appendIndex(index);
    }

    @Override
    public boolean isDescendantOf(Ref ref) {
        if (isRoot()) {
            return false;
        }
        Ref parentRef = parent();
        return parentRef != null && (parentRef.equals(ref) || parentRef.isDescendantOf(ref));
    }

    @Override
    public boolean isAncestorOf(Ref ref) {
        return ref.isDescendantOf(this);
    }

    @Override
    public String propertyName() {
        return context().pathSyntax().getPropertyName(path());
    }

    @Override
    public Ref ancestor(String ancestorPropertyName) {
        if (isRoot()) {
            throw new IllegalArgumentException("No ancestor with name " + ancestorPropertyName);
        }
        Ref parent = parent();
        if (parent.propertyName().equals(ancestorPropertyName)) {
            return parent;
        }
        return parent.ancestor(ancestorPropertyName);
    }

    @Override
    public void fire(Action action) {
        context().modelContext().getEventDispatcher().dispatch(new ActionEvent(this, action));
    }

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public String toString() {
        return "Ref{" + path() + "}";
    }


    @Override
    public CollectionRef toCollectionRef() {
        return this;
    }

    @Override
    public MapRef toMapRef() {
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypedRef<T> toTypedRef() {
        return (TypedRef<T>)this;
    }
}
