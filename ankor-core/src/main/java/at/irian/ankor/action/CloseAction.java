package at.irian.ankor.action;

/**
 * @author Manfred Geiler
 */
public class CloseAction extends Action {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CloseAction.class);

    public static final String CLOSE_ACTION_NAME = "$close";

    public CloseAction() {
        super(CLOSE_ACTION_NAME);
    }

}
