package at.irian.ankor.ref.impl;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.change.DelayedChangeEventListener;
import at.irian.ankor.ref.ActionListener;
import at.irian.ankor.ref.ChangeListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.Wrapper;

import java.lang.reflect.Constructor;

/**
 * @author Manfred Geiler
 */
public abstract class AbstractRef implements Ref {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AbstractRef.class);

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public void setValue(final Object newValue) {

        final Object newUnwrappedValue;
        if (newValue != null && newValue instanceof Wrapper) {
            newUnwrappedValue = ((Wrapper)newValue).getWrappedValue();
        } else {
            newUnwrappedValue = newValue;
        }

        new RefValueChanger(this).setValueTo(newValue, new RefValueChanger.SetValueCallback() {
            @Override
            public void doSetValue() {
                Class<?> type = getType();
                if (Wrapper.class.isAssignableFrom(type)) {
                    Wrapper wrapper;
                    Object oldValue = internalGetValue();
                    if (oldValue != null) {
                        wrapper = (Wrapper) oldValue;
                    } else {
                        try {
                            //noinspection unchecked
                            Constructor<Wrapper> defaultConstructor = ((Class<Wrapper>) type).getDeclaredConstructor();
                            if (!defaultConstructor.isAccessible()) {
                                defaultConstructor.setAccessible(true);
                            }
                            wrapper = defaultConstructor.newInstance();
                        } catch (Exception e) {
                            throw new IllegalStateException("Unable to instantiate wrapper of type " + type, e);
                        }
                        internalSetValue(wrapper);
                    }
                    //noinspection unchecked
                    wrapper.putWrappedValue(newUnwrappedValue);
                } else {
                    internalSetValue(newUnwrappedValue);
                }
            }
        });
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
        context().modelEventListeners().add(new ChangeEventListener(this) {
            @Override
            public void process(ChangeEvent event) {
                processPropChangeEvent(event.getChangedProperty(), getWatchedProperty(), listener);
            }
        });
    }

    @Override
    public void addPropChangeListener(final ChangeListener listener, long delayMilliseconds) {
        context().modelEventListeners().add(new DelayedChangeEventListener(this, delayMilliseconds) {
            @Override
            public void processImmediately(ChangeEvent event) {
                processPropChangeEvent(event.getChangedProperty(), getWatchedProperty(), listener);
            }
        });
    }

    @Override
    public void addTreeChangeListener(final ChangeListener listener) {
        context().modelEventListeners().add(new ChangeEventListener(this) {
            @Override
            public void process(ChangeEvent event) {
                processTreeChangeEvent(event.getChangedProperty(), getWatchedProperty(), listener);
            }
        });
    }

    @Override
    public void addTreeChangeListener(final ChangeListener listener, long delayMilliseconds) {
        context().modelEventListeners().add(new DelayedChangeEventListener(this, delayMilliseconds) {
            @Override
            public void processImmediately(ChangeEvent event) {
                processTreeChangeEvent(event.getChangedProperty(), getWatchedProperty(), listener);
            }
        });
    }

    @Override
    public void addPropActionListener(final ActionListener listener) {
        context().modelEventListeners().add(new ActionEvent.Listener(this) {
            @Override
            public void process(ActionEvent event) {
                processPropActionEvent(event.getActionProperty(), getWatchedProperty(), listener, event.getAction());
            }
        });
    }



    private void processPropChangeEvent(Ref changedProperty, Ref watchedProperty, ChangeListener listener) {
        if (isRelevantPropChange(changedProperty, watchedProperty)) {
            listener.processChange(watchedProperty, changedProperty);
        }
    }

    private boolean isRelevantPropChange(Ref changedProperty, Ref watchedProperty) {
        return watchedProperty.equals(changedProperty) || watchedProperty.isDescendantOf(changedProperty);
    }

    private void processTreeChangeEvent(Ref changedProperty, Ref watchedProperty, ChangeListener listener) {
        if (isRelevantTreeChange(changedProperty, watchedProperty)) {
            listener.processChange(watchedProperty, changedProperty);
        }
    }

    private boolean isRelevantTreeChange(Ref changedProperty, Ref watchedProperty) {
        return watchedProperty.equals(changedProperty) || watchedProperty.isAncestorOf(changedProperty);
    }

    private void processPropActionEvent(Ref actionProperty, Ref watchedProperty,
                                        ActionListener listener, Action action) {
        if (isRelevantActionProperty(actionProperty, watchedProperty)) {
            listener.processAction(watchedProperty, action);
        }
    }

    private boolean isRelevantActionProperty(Ref actionProperty, Ref watchedProperty) {
        return watchedProperty.equals(actionProperty);
    }

}
