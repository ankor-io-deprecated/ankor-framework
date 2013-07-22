package at.irian.ankor.system;

import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.action.SimpleAction;
import at.irian.ankor.annotation.BeanAnnotationActionEventListener;
import at.irian.ankor.rmi.ELRemoteMethodActionEventListener;
import at.irian.ankor.rmi.RemoteMethodActionEventListener;

/**
 * @author Thomas Spiegl
 */
public class RemoteActionListenerSplitter extends ActionEvent.Listener {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteActionListener.class);

    private final RemoteMethodActionEventListener remoteMethodActionEventListener;
    private final BeanAnnotationActionEventListener beanAnnotationActionEventListener;

    public RemoteActionListenerSplitter(BeanResolver beanResolver) {
        super(null); // global listener
        remoteMethodActionEventListener = new ELRemoteMethodActionEventListener();
        beanAnnotationActionEventListener = new BeanAnnotationActionEventListener(beanResolver);
    }

    @Override
    public void process(ActionEvent event) {
        if (event.getAction() instanceof SimpleAction) {
            beanAnnotationActionEventListener.process(event);
        } else {
            remoteMethodActionEventListener.process(event);
        }
    }
}
