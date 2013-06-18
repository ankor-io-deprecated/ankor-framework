package at.irian.ankor.sample.fx.app;

import at.irian.ankor.core.ref.ModelRef;
import at.irian.ankor.core.ref.PropertyRef;
import at.irian.ankor.core.ref.RootRef;

/**
 * @author Thomas Spiegl
 */
public class ServiceFacade {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ServiceFacade.class);

    private static ServiceFacade INSTANCE = new ServiceFacade();

    public static ServiceFacade service() {
        return INSTANCE;
    }

    public void createAnimalSearchTab(String tabId, ActionCompleteCallback cb) {
        ModelRef tabsRef = App.getApplication().getRefFactory().rootRef().sub("tabs");
        App.executeAction(String.format("service.createAnimalSearchTab('%s')", tabId), tabsRef, tabId, cb);
    }

    public void initApplication(ActionCompleteCallback cb) {
        RootRef rootRef = App.getApplication().getRefFactory().rootRef();
        App.executeAction("service.init()", rootRef, rootRef.path(), cb);
    }
}
