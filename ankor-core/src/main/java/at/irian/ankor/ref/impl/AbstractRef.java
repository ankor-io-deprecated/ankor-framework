package at.irian.ankor.ref.impl;

import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.DelayedChangeEventListener;
import at.irian.ankor.change.DelayedTreeChangeEventListener;
import at.irian.ankor.ref.ActionListener;
import at.irian.ankor.ref.ChangeListener;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public abstract class AbstractRef implements Ref {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AbstractRef.class);

    @Override
    public abstract RefContextImplementor context();

    @Override
    public void addValueChangeListener(final ChangeListener listener) {
        context().modelEventListeners().add(new ChangeEvent.Listener(this) {
            @Override
            public void process(ChangeEvent event) {
                processValueChangeEvent(event.getChangedProperty(), getWatchedProperty(), listener);
            }
        });
    }

    @Override
    public void addDelayedValueChangeListener(final ChangeListener listener, long delayMilliseconds) {
        context().modelEventListeners().add(new DelayedChangeEventListener(this, delayMilliseconds) {
            @Override
            public void processImmediately(ChangeEvent event) {
                processValueChangeEvent(event.getChangedProperty(), getWatchedProperty(), listener);
            }
        });
    }

    @Override
    public void addTreeChangeListener(final ChangeListener listener) {
        context().modelEventListeners().add(new ChangeEvent.TreeListener(this) {
            @Override
            public void process(ChangeEvent event) {
                processTreeChangeEvent(event.getChangedProperty(), getWatchedProperty(), listener);
            }
        });
    }

    @Override
    public void addDelayedTreeChangeListener(final ChangeListener listener, long delayMilliseconds) {
        context().modelEventListeners().add(new DelayedTreeChangeEventListener(this, delayMilliseconds) {
            @Override
            public void processImmediately(ChangeEvent event) {
                processTreeChangeEvent(event.getChangedProperty(), getWatchedProperty(), listener);
            }
        });
    }

    @Override
    public void addActionListener(final ActionListener actionListener) {
        context().modelEventListeners().add(new ActionEvent.Listener(this) {
            @Override
            public void process(ActionEvent event) {
                Ref actionProperty = event.getActionProperty();
                Ref watchedProperty = getWatchedProperty();
                if (watchedProperty.equals(actionProperty)) {
                    actionListener.processAction(watchedProperty, event.getAction());
                }
            }
        });
    }


    private void processValueChangeEvent(Ref changedProperty, Ref watchedProperty, ChangeListener listener) {
        if (watchedProperty.equals(changedProperty) || watchedProperty.isDescendantOf(changedProperty)) {
            listener.processChange(changedProperty, watchedProperty);
        }
    }

    private void processTreeChangeEvent(Ref changedProperty, Ref watchedProperty, ChangeListener listener) {
        if (watchedProperty.equals(changedProperty) || watchedProperty.isAncestorOf(changedProperty)) {
            listener.processChange(changedProperty, watchedProperty);
        }
    }

}
