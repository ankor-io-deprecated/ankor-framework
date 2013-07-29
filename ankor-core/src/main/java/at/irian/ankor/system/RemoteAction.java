package at.irian.ankor.system;

import at.irian.ankor.action.Action;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class RemoteAction extends Action {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteAction.class);

    public RemoteAction(String name, Map<String, Object> params) {
        super(name, params);
    }
}
