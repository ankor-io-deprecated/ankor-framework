package at.irian.ankor.core.test;

import at.irian.ankor.core.listener.ModelActionListener;
import at.irian.ankor.core.ref.ModelRef;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class InitActionListener implements ModelActionListener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(InitActionListener.class);

    @Override
    public void handleModelAction(ModelRef modelRef, String action) {
        if (action.equals("init")) {
            LOG.info("Creating new TestModel");
            modelRef.root().setValue(createNewModel());
            modelRef.fireAction("initialized");
        }
    }

    private TestModel createNewModel() {
        return new TestModel();
    }
}
