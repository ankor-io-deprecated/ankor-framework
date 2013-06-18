package at.irian.ankor.core.test;

import at.irian.ankor.core.action.ModelAction;
import at.irian.ankor.core.action.SimpleAction;
import at.irian.ankor.core.listener.ModelActionListener;
import at.irian.ankor.core.ref.ModelRef;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class InitActionListener implements ModelActionListener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(InitActionListener.class);

    @Override
    public void handleModelAction(ModelRef actionContext, ModelAction action) {
        if (action.name().equals("init")) {
            LOG.info("Creating new TestModel");
            actionContext.root().setValue(createNewModel());
            actionContext.fire(SimpleAction.withName("initialized"));
        }
    }

    private TestModel createNewModel() {
        return new TestModel();
    }
}
