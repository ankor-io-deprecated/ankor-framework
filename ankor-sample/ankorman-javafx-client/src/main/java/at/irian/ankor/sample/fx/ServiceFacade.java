package at.irian.ankor.sample.fx;

import at.irian.ankor.core.ref.Ref;
import at.irian.ankor.fx.app.ActionCompleteCallback;
import at.irian.ankor.fx.app.AppService;
import at.irian.ankor.sample.fx.server.model.Animal;

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
        Ref tabsRef = appService.getApplication().getRefFactory().rootRef().sub("tabs");
        appService.executeAction(tabsRef, String.format("service.createAnimalSearchTab('%s')", tabId), "context." + tabId, cb);
    }

    public void createAnimalDetailTab(String tabId, ActionCompleteCallback cb) {
        Ref tabsRef = appService.getApplication().getRefFactory().rootRef().sub("tabs");
        appService.executeAction(tabsRef, String.format("service.createAnimalDetailTab('%s')", tabId), "context." + tabId, cb);
    }

    public void initApplication(ActionCompleteCallback cb) {
        Ref rootRef = appService.getApplication().getRefFactory().rootRef();
        appService.executeAction(rootRef, "service.init()", rootRef.path(), cb);
    }

    public void searchAnimals(Ref tabRef, ActionCompleteCallback cb) {
        appService.executeAction(tabRef.sub("model"), "service.searchAnimals(context.filter)", "context.animals", cb);
    }

    public void saveAnimal(Ref tabRef, ActionCompleteCallback cb) {
        appService.executeAction(tabRef.sub("model"), "service.saveAnimal(context.animal)", null, cb);
    }

    public void saveAnimals(Ref tabRef, ActionCompleteCallback cb) {
        appService.executeAction(tabRef.sub("model"), "service.saveAnimals(context.animals)", null, cb);
    }
}
