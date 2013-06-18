package at.irian.ankor.sample.fx.infra;

import at.irian.ankor.core.application.Application;

/**
 * @author Thomas Spiegl
 */
public class App {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(App.class);

    static Application APP_INSTANCE;

    public static void setInstance(Application clientApp) {
        APP_INSTANCE = clientApp;
    }

    public static Application getApplication() {
        return APP_INSTANCE;
    }
}
