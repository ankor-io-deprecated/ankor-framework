package at.irian.ankor.ref.el;

import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.ref.ActionListener;
import at.irian.ankor.ref.ChangeListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.impl.RefContextImplementor;

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
                Ref changedProperty = event.getChangedProperty();
                Ref watchedProperty = getWatchedProperty();
                if (watchedProperty.equals(changedProperty) || watchedProperty.isDescendantOf(changedProperty)) {
                    listener.processChange(changedProperty, watchedProperty);
                }
            }
        });
    }

    @Override
    public void addTreeChangeListener(final ChangeListener listener) {
        context().modelEventListeners().add(new ChangeEvent.Listener(this) {
            @Override
            public void process(ChangeEvent event) {
                Ref changedProperty = event.getChangedProperty();
                Ref watchedProperty = getWatchedProperty();
                if (watchedProperty.equals(changedProperty) || watchedProperty.isAncestorOf(changedProperty)) {
                    listener.processChange(changedProperty, watchedProperty);
                }
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
}
