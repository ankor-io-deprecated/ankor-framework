package at.irian.ankor.core.test;

import at.irian.ankor.core.listener.ModelChangeListener;
import at.irian.ankor.core.ref.ModelRef;
import at.irian.ankor.core.test.animal.AnimalSearchContainer;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class NewContainerChangeListener implements ModelChangeListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(NewContainerChangeListener.class);


    @Override
    public void handleModelChange(ModelRef watchedRef, ModelRef changedRef) {
        if (watchedRef.path().startsWith("containers")) {
            watchedRef.setValue(new AnimalSearchContainer());
        }
    }
}
