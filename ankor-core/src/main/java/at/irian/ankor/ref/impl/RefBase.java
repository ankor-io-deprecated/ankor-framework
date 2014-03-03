package at.irian.ankor.ref.impl;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.base.ObjectUtils;
import at.irian.ankor.base.Wrapper;
import at.irian.ankor.change.Change;
import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeType;
import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.event.dispatch.BufferingEventDispatcher;
import at.irian.ankor.event.source.ModelSessionSource;
import at.irian.ankor.event.source.Source;
import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.ref.*;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.*;

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
        apply(new ModelSessionSource(context().modelSession()), Change.valueChange(newValue));
    }

    @Override
    public void delete(String key) {
        apply(new ModelSessionSource(context().modelSession()), Change.deleteChange(key));
    }

    @Override
    public void delete(int idx) {
        apply(new ModelSessionSource(context().modelSession()), Change.deleteChange(idx));
    }

    @Override
    public void delete() {
        ((RefImplementor)parent()).apply(new ModelSessionSource(context().modelSession()),
                                         Change.deleteChange(propertyName()));
    }

    @Override
    public void insert(int idx, Object value) {
        apply(new ModelSessionSource(context().modelSession()), Change.insertChange(idx, value));
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

        apply(new ModelSessionSource(context().modelSession()), Change.insertChange(size, value));
    }

    @Override
    public void replace(int fromIdx, Collection elements) {
        apply(new ModelSessionSource(context().modelSession()), Change.replaceChange(fromIdx, elements));
    }

    @Override
    public void apply(Source source, Change change) {

        // buffer events
        BufferingEventDispatcher bufferingEventDispatcher = new BufferingEventDispatcher();
        context().modelSession().pushEventDispatcher(bufferingEventDispatcher);
        try {
            // apply change to local view model
            handleChange(change);
        } finally {
            context().modelSession().popEventDispatcher();
        }

        if (change.getType() == ChangeType.value) {
            for (ModelEvent modelEvent : bufferingEventDispatcher.getBufferedEvents()) {
                if (modelEvent instanceof ChangeEvent) {
                    Ref changedProperty = ((ChangeEvent) modelEvent).getChangedProperty();
                    if (changedProperty.equals(this)) {
                        Change nestedChange = ((ChangeEvent) modelEvent).getChange();
                        if (nestedChange.getType() == ChangeType.value) {
                            Object v1 = change.getValue();
                            Object v2 = nestedChange.getValue();
                            if (ObjectUtils.nullSafeEquals(v1, v2)) {
                                // found exactly this change
                                // do ignore, because we fire it anyway down below!
                                LOG.debug("Suppressing nested change for {}: {}", changedProperty, change);
                                continue;
                            }
                        }
                    }
                }
                context().modelSession().getEventDispatcher().dispatch(modelEvent);
            }
        }

        // fire change event
        ChangeEvent changeEvent = new ChangeEvent(source, this, change);
        context().modelSession().getEventDispatcher().dispatch(changeEvent);
    }

    private void handleChange(Change change) {
        ChangeType changeType = change.getType();
        switch(changeType) {
            case value:
                handleValueChange(change.getValue());
                break;

            case insert:
                handleInsertChange(getValue(), change.getKey(), change.getValue());
                break;

            case delete:
                handleDeleteChange(getValue(), change.getKey());
                break;

            case replace:
                handleReplaceChange(getValue(), change.getKey(), change.getValue());
                break;

            default:
                throw new IllegalArgumentException("Unsupported change type " + changeType);
        }
    }

    private void handleValueChange(Object newValue) {
        if (isValid()) {
            Class<?> type = getType();
            if (Wrapper.class.isAssignableFrom(type)) {
                Object newValueUnwrapped = unwrapIfNecessary(newValue);
                internalSetWrapperValue(type, newValue, newValueUnwrapped);
            } else {
                internalSetValue(newValue);
            }
        } else {
            internalSetValue(newValue);
        }
    }

    @SuppressWarnings({"unchecked", "SuspiciousSystemArraycopy"})
    private void handleInsertChange(Object listOrArray, Object key, Object value) {
        if (listOrArray instanceof List) {
            // TODO: list.size() is sometimes not returning the correct value. why?
            int idx = asIndex(key);
            List list = (List)listOrArray;
            if (idx == list.size()) {
                list.add(value);
            } else if (idx < list.size()) {
                list.add(idx, value);
            } else {
                LOG.warn("Could not handle insert change on {} because index {} is out of bounds (size = {})", this,
                        idx, list.size());
            }
        } else if (listOrArray.getClass().isArray()) {
            int idx = asIndex(key);
            int length = Array.getLength(listOrArray);
            Class<?> componentType = listOrArray.getClass().getComponentType();
            Object newArray = Array.newInstance(componentType, length + 1);
            if (idx > 0) {
                System.arraycopy(listOrArray, 0, newArray, 0, idx);
            }
            Array.set(newArray, idx, value);
            if (idx < length) {
                System.arraycopy(listOrArray, idx, newArray, idx + 1, length - idx);
            }
            internalSetValue(newArray);
        } else {
            throw new IllegalArgumentException("list/array of type " + listOrArray.getClass().getName());
        }
    }

    @SuppressWarnings("SuspiciousSystemArraycopy")
    private void handleDeleteChange(Object listOrArrayOrMap, Object key) {
        if (listOrArrayOrMap instanceof List) {
            int idx = asIndex(key);
            List list = (List)listOrArrayOrMap;
            if (idx < list.size()) {
                list.remove(idx);
            } else {
                LOG.warn("Could not handle delete change on {} because index {} is out of bounds", this, idx);
            }
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
            internalSetValue(newArray);
        } else if (listOrArrayOrMap instanceof Map) {
            String mapKey = asMapKey(key);
            ((Map) listOrArrayOrMap).remove(mapKey);
        } else if (key instanceof String) {
            // neither List nor Array nor Map, than we assume it is a bean and set the corresponding property to null...
            Ref propertyRef = refFactory().ref(pathSyntax().concat(path(), (String) key));
            ((RefBase)propertyRef).handleValueChange(null);
        } else {
            throw new IllegalArgumentException("list/array/map of type " + listOrArrayOrMap.getClass().getName());
        }
    }

    @SuppressWarnings({"unchecked", "SuspiciousSystemArraycopy"})
    private void handleReplaceChange(Object listOrArray, Object key, Object value) {
        int fromIdx = asIndex(key);
        Collection replacementElements = asCollection(value);
        if (listOrArray instanceof List) {
            List list = (List)listOrArray;
            Iterator iterator = replacementElements.iterator();
            for (int i = 0, len = replacementElements.size(); i < len; i++) {
                Object elem = iterator.next();
                if (fromIdx + i >= list.size()) {
                    list.add(elem);
                } else {
                    list.set(fromIdx + i, elem);
                }
            }
        } else if (listOrArray.getClass().isArray()) {
            int oldLen = Array.getLength(listOrArray);
            Class<?> componentType = listOrArray.getClass().getComponentType();
            int newLen = Math.max(oldLen, fromIdx + replacementElements.size());
            Object newArray = Array.newInstance(componentType, newLen);
            System.arraycopy(listOrArray, 0, newArray, 0, oldLen);
            Iterator iterator = replacementElements.iterator();
            for (int i = 0, len = replacementElements.size(); i < len; i++) {
                Object elem = iterator.next();
                Array.set(newArray, fromIdx + i, elem);
            }
            internalSetValue(newArray);
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

    private Collection asCollection(Object collectionOrArray) {
        if (collectionOrArray instanceof Collection) {
            return (Collection) collectionOrArray;
        } else if (collectionOrArray.getClass().isArray()) {
            return Arrays.asList((Object[]) collectionOrArray);
        } else {
            throw new IllegalArgumentException("list/array replace value of type " + collectionOrArray.getClass());
        }
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

            Wrapper wrapper;
            try {
                wrapper = internalGetValue();
            } catch (InvalidRefException e) {
                throw new RuntimeException(String.format("Ref %s is invalid", this.path()), e);
            }
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

    @Override
    public void signalValueChange() {
        signal(new ModelSessionSource(context().modelSession()), Change.valueChange(getValue()));
    }

    @Override
    public void signal(Source source, Change change) {
        LOG.trace("{} signal {}", this, change);
        ChangeEvent changeEvent = new ChangeEvent(source, this, change);
        context().modelSession().getEventDispatcher().dispatch(changeEvent);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue() {
        T val;
        try {
            val = internalGetValue();
        } catch (InvalidRefException e) {
            throw new IllegalStateException("Invalid Ref " + this, e);
        }
        if (val != null && val instanceof Wrapper) {
            return (T)((Wrapper)val).getWrappedValue();
        }
        return val;
    }

    protected abstract <T> T internalGetValue() throws InvalidRefException;

    @Override
    public RefContext context() {
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
        fire(new ModelSessionSource(context().modelSession()), action);
    }

    @Override
    public void fire(Source source, Action action) {
        context().modelSession().getEventDispatcher().dispatch(new ActionEvent(source, this, action));
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
