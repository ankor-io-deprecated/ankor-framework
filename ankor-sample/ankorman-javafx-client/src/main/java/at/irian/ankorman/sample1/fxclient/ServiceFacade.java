package at.irian.ankorman.sample1.fxclient;

import at.irian.ankor.fx.app.ActionCompleteCallback;
import at.irian.ankor.fx.app.AppService;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefFactory;

/**
 * @author Thomas Spiegl
 */
public class ServiceFacade {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ServiceFacade.class);

    private final AppService appService;
    private RefFactory refFactory;

    public ServiceFacade(AppService appService) {
        this.appService = appService;
        this.refFactory = appService.getRefFactory();
    }

    public void initApplication(ActionCompleteCallback cb) {
        Ref rootRef = refFactory.rootRef();
        appService.remoteMethod("service.init()")
                .inContext(rootRef)
                .withResultIn(rootRef)
                .onComplete(cb)
                .execute();
    }

    public void createAnimalSearchTab(String tabId, ActionCompleteCallback cb) {
        Ref tabsRef = refFactory.ref("root.tabs");
        appService.remoteMethod("service.createAnimalSearchTab(contextRef, tabId)")
                .inContext(tabsRef)
                .setParam("tabId", tabId)
                //.withResultIn(tabsRef.append(tabId))
                .onComplete(cb)
                .execute();
    }

    public void createAnimalDetailTab(String tabId, ActionCompleteCallback cb) {
        Ref tabsRef = refFactory.ref("root.tabs");
        appService.remoteMethod("service.createAnimalDetailTab(contextRef, tabId)")
                .inContext(tabsRef)
                .setParam("tabId", tabId)
                //.withResultIn(tabsRef.append(tabId))
                .onComplete(cb)
                .execute();
    }

    public void searchAnimals(Ref tabRef, ActionCompleteCallback cb) {
        appService.remoteMethod("service.searchAnimals(context.filter, context.animals.paginator)")
                .inContext(tabRef.append("model"))
                .withResultIn("context.animals")
                .onComplete(cb)
                .execute();
    }

    public void saveAnimal(Ref tabRef, ActionCompleteCallback cb) {
        appService.remoteMethod("service.saveAnimal(context.animal)")
                .inContext(tabRef.append("model"))
                .onComplete(cb)
                .execute();
    }

    public void saveAnimals(Ref tabRef, ActionCompleteCallback cb) {
        appService.remoteMethod("service.saveAnimals(context.animals)")
                .inContext(tabRef.append("model"))
                .onComplete(cb)
                .execute();
    }
}
