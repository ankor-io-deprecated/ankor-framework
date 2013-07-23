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

}
