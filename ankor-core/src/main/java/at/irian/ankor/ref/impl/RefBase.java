package at.irian.ankor.ref.impl;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.change.Change;
import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.OldValuesAwareChangeEvent;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.event.PropertyWatcher;
import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.ref.Wrapper;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public abstract class RefBase implements Ref {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefBase.class);

    private final RefContextImplementor refContext;

    protected RefBase(RefContextImplementor refContext) {
        this.refContext = refContext;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public void setValue(final Object newValue) {
        apply(new Change(newValue));
    }


    public void apply(Change change) {

        // remember old value of the referenced property
        Object oldValue;
        try {
            oldValue = getValue();
        } catch (IllegalStateException e) {
            oldValue = null;
        }

        // remember old value of the watched properties
        Map<Ref, Object> oldWatchedValues = getOldWatchedValues();

        Object newValue = change.getNewValue();

        Class<?> type = getType();
        if (Wrapper.class.isAssignableFrom(type)) {
            Object newValueUnwrapped = unwrapIfNecessary(newValue);
            internalSetWrapperValue(type, newValue, newValueUnwrapped);
        } else {
            internalSetValue(newValue);
        }

        // fire change event
        ChangeEvent changeEvent
                = new OldValuesAwareChangeEvent(this, change, oldValue, oldWatchedValues);
        context().eventDispatcher().dispatch(changeEvent);
    }

    private Map<Ref, Object> getOldWatchedValues() {
        Map<Ref, Object> result = new HashMap<Ref, Object>();
        for (ModelEventListener listener : context().eventListeners()) {
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
        return refFactory().rootRef();
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
    public Ref append(String propertyOrSubPath) {
        return refFactory().ref(pathSyntax().concat(path(), propertyOrSubPath));
    }

    @Override
    public Ref appendIdx(int index) {
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
    public void fireAction(Action action) {
        ActionEvent actionEvent = new ActionEvent(this, action);
        context().eventDispatcher().dispatch(actionEvent);
    }

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public String toString() {
        return "Ref{" + path() + "}";
    }

}
