package at.irian.ankor.fx.app;

import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.system.AnkorSystem;

/**
 * @author Thomas Spiegl
 */
public class AppService {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AppService.class);

    private RefFactory refFactory;

    public AppService(AnkorSystem system) {
        refFactory = system.getRefContextFactory().createRefContext().refFactory();
    }

    public RefFactory getRefFactory() {
        return refFactory;
    }

}
