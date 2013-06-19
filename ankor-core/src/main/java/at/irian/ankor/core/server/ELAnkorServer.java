package at.irian.ankor.core.server;

import at.irian.ankor.core.action.method.RemoteMethodActionListener;
import at.irian.ankor.core.application.ELApplication;

/**
 * @author Manfred Geiler
 */
public abstract class ELAnkorServer extends AnkorServerBase {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELAnkorServer.class);

    public ELAnkorServer(ELApplication application) {
        super(application);
    }

    @Override
    public void start() {
        super.start();
        registerDefaultListeners();
    }

    protected void registerDefaultListeners() {
        ELApplication application = (ELApplication)getApplication();
        RemoteMethodActionListener remoteMethodActionListener
                = new RemoteMethodActionListener(application.getExpressionFactory(),
                                                 application.getRefFactory());
        getApplication().getListenerRegistry().registerRemoteActionListener(null, remoteMethodActionListener);
    }
}
