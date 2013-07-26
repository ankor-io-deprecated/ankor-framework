package at.irian.ankor.fx.app;

import at.irian.ankor.ref.RefFactory;

/**
 * @author Thomas Spiegl
 */
public class AppService {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AppService.class);

    private RefFactory refFactory;

    public AppService(RefFactory refFactory) {
        this.refFactory = refFactory;
    }

    public RefFactory getRefFactory() {
        return refFactory;
    }

}
