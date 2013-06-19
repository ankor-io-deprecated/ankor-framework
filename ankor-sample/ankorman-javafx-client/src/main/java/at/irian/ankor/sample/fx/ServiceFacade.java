package at.irian.ankor.sample.fx;

import at.irian.ankor.core.ref.ModelRef;
import at.irian.ankor.core.ref.RootRef;
import at.irian.ankor.sample.fx.app.ActionCompleteCallback;
import at.irian.ankor.sample.fx.app.AppService;

/**
 * @author Thomas Spiegl
 */
public class ServiceFacade {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ServiceFacade.class);

    private final AppService appService;

    public ServiceFacade(AppService appService) {
        this.appService = appService;
    }

    public void createAnimalSearchTab(String tabId, ActionCompleteCallback cb) {
        ModelRef tabsRef = appService.getApplication().getRefFactory().rootRef().sub("tabs");
        appService.executeAction(tabsRef, String.format("service.createAnimalSearchTab('%s')", tabId), tabId, cb);
    }

    public void initApplication(ActionCompleteCallback cb) {
        RootRef rootRef = appService.getApplication().getRefFactory().rootRef();
        appService.executeAction(rootRef, "service.init()", rootRef.path(), cb);
    }

    public void searchAnimals(ModelRef tabRef, ActionCompleteCallback cb) {
        appService.executeAction(tabRef.sub("model"), "service.searchAnimals(context.filter)", "animals", cb); // TODO context.animals
    }
}
