package at.irian.ankor.service.test;

import at.irian.ankor.action.Action;
import at.irian.ankor.event.ActionListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.service.test.animal.AnimalSearchContainer;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class NewContainerActionListener implements ActionListener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(InitActionListener.class);

    @Override
    public void processAction(Ref modelContext, Action action) {
        if (action.name().equals("newAnimalSearchContainer")) {
            LOG.info("Adding new Animal Search Container");
            modelContext.setValue(new AnimalSearchContainer());
        }
    }

}
