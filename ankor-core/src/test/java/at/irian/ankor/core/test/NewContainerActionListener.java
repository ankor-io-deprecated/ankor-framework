package at.irian.ankor.core.test;

import at.irian.ankor.core.listener.ModelActionListener;
import at.irian.ankor.core.ref.ModelRef;
import at.irian.ankor.core.test.animal.AnimalSearchContainer;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class NewContainerActionListener implements ModelActionListener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(InitActionListener.class);

    @Override
    public void handleModelAction(ModelRef modelRef, String action) {
        if (action.equals("newAnimalSearchContainer")) {
            LOG.info("Adding new Animal Search Container");
            modelRef.setValue(new AnimalSearchContainer());
        }
    }

}
