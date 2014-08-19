package at.irian.ankor.fx.binding.value;

import at.irian.ankor.change.Change;
import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.event.source.ModelSource;
import at.irian.ankor.ref.Ref;

/**
 * Package local helper class for {@link ObservableRef} and {@link ObservableListRef}.
 *
* @author Manfred Geiler
*/
abstract class ObservableChangeEventListener extends ChangeEventListener {

    private final Object observable;

    public ObservableChangeEventListener(Ref ref, Object observable) {
        super(ref);
        this.observable = observable;
    }

    @Override
    public void process(ChangeEvent event) {

        // first, let us determine the origination of this event...
        if (event.getSource() instanceof ModelSource) {
            if (((ModelSource) event.getSource()).getOrigination() == observable) {
                // ignore this change event because it originates from this Observable itself
                return;
            }
        }

        Ref changedProperty = event.getProperty();
        handleChange(changedProperty, event.getChange());
    }

    protected abstract void handleChange(Ref changedProperty, Change change);
}
