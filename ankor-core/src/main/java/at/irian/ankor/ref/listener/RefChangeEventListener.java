package at.irian.ankor.ref.listener;

import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.ref.Ref;

/**
* @author Manfred Geiler
*/
class RefChangeEventListener extends ChangeEventListener implements RefEventListenerImplementor {

    private final RefChangeListener listener;
    private Ref ref;

    RefChangeEventListener(Ref ref, RefChangeListener listener) {
        super(null);
        this.ref = ref;
        this.listener = listener;
    }

    @Override
    public void process(ChangeEvent event) {
        listener.processChange(event.getChangedProperty());
    }

    @Override
    public Ref getOwner() {
        return ref;
    }

    @Override
    public RefListener getRefListener() {
        return listener;
    }

}
