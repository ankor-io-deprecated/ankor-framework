package at.irian.ankor.ref.el;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEventListener;
import at.irian.ankor.change.ChangeEventListener;
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
    public void addChangeListener(final ChangeListener listener) {
        ChangeEventListener eventListener = new ChangeEventListener(this) {
            @Override
            public void processChange(Ref changedProperty) {
                listener.processChange(changedProperty, getWatchedProperty());
            }
        };
        context().modelEventListeners().add(eventListener);
    }

    @Override
    public void addActionListener(final ActionListener actionListener) {
        ActionEventListener eventListener = new ActionEventListener(this) {
            @Override
            public void processAction(Ref actionProperty, Action action) {
                actionListener.processAction(getWatchedProperty(), action);
            }
        };
        context().modelEventListeners().add(eventListener);
    }
}
