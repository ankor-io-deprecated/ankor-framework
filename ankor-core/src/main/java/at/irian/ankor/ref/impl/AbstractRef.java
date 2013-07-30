package at.irian.ankor.ref.impl;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.action.ActionEventListener;
import at.irian.ankor.change.Change;
import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.change.OldValuesAwareChangeEvent;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.event.PropertyWatcher;
import at.irian.ankor.ref.ActionListener;
import at.irian.ankor.ref.ChangeListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.Wrapper;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public abstract class AbstractRef implements Ref {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AbstractRef.class);

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

            Wrapper wrapper = (Wrapper) internalGetValue();
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
    public abstract RefContextImplementor context();

    @Override
    public void addPropChangeListener(final ChangeListener listener) {
        context().eventListeners().add(new ChangeEventListener(this) {
            @Override
            public void process(ChangeEvent event) {
                Ref changedProperty = event.getChangedProperty();
                Ref watchedProperty = getWatchedProperty();
                if (isRelevantPropChange(changedProperty, watchedProperty)) {
                    listener.processChange(watchedProperty, changedProperty);
                }
            }

            private boolean isRelevantPropChange(Ref changedProperty, Ref watchedProperty) {
                return watchedProperty.equals(changedProperty) || watchedProperty.isDescendantOf(changedProperty);
            }

        });
    }

    @Override
    public void addTreeChangeListener(final ChangeListener listener) {
        context().eventListeners().add(new ChangeEventListener(this) {
            @Override
            public void process(ChangeEvent event) {
                Ref changedProperty = event.getChangedProperty();
                Ref watchedProperty = getWatchedProperty();
                if (isRelevantTreeChange(changedProperty, watchedProperty)) {
                    listener.processChange(watchedProperty, changedProperty);
                }
            }

            private boolean isRelevantTreeChange(Ref changedProperty, Ref watchedProperty) {
                return watchedProperty.equals(changedProperty) || watchedProperty.isAncestorOf(changedProperty);
            }

        });
    }

    @Override
    public void addPropActionListener(final ActionListener listener) {
        context().eventListeners().add(new ActionEventListener(this) {
            @Override
            public void process(ActionEvent event) {
                Ref watchedProperty = getWatchedProperty();
                if (isRelevantActionProperty(event.getActionProperty(), watchedProperty)) {
                    listener.processAction(watchedProperty, event.getAction());
                }
            }

            private boolean isRelevantActionProperty(Ref actionProperty, Ref watchedProperty) {
                return watchedProperty.equals(actionProperty);
            }
        });
    }

    @Override
    public void addChangeListener(final ChangeListener listener) {
        context().eventListeners().add(new ChangeEventListener(null) {
            @Override
            public void process(ChangeEvent event) {
                listener.processChange(null, event.getChangedProperty());
            }

            @Override
            public Ref getOwner() {
                return AbstractRef.this;
            }
        });
    }

    @Override
    public void fireAction(Action action) {
        ActionEvent actionEvent = new ActionEvent(this, action);
        context().eventDispatcher().dispatch(actionEvent);
    }
}
