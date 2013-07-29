package at.irian.ankor.system;

import at.irian.ankor.change.Change;

/**
 * @author Manfred Geiler
 */
public class RemoteChange extends Change {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteChange.class);

    public RemoteChange(Object newValue) {
        super(newValue);
    }
}
