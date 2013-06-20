package at.irian.ankor.service.test;

import at.irian.ankor.action.ModelAction;
import at.irian.ankor.action.SimpleAction;
import at.irian.ankor.action.ActionListener;
import at.irian.ankor.ref.Ref;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class InitActionListener implements ActionListener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(InitActionListener.class);

    @Override
    public void processAction(Ref actionContext, ModelAction action) {
        if (action.name().equals("init")) {
            LOG.info("Creating new TestModel");
            Ref root = actionContext.root();
            root.setValue(createNewModel());
            actionContext.fire(SimpleAction.create("initialized"));
        }
    }

    private TestModel createNewModel() {
        return new TestModel();
    }
}
