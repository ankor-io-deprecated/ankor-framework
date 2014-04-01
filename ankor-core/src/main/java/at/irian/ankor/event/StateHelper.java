package at.irian.ankor.event;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.state.StateDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class StateHelper {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StateHelper.class);

    private final RefFactory refFactory;

    public StateHelper(RefFactory refFactory) {
        this.refFactory = refFactory;
    }

    public Map<String, Object> createState(StateDefinition stateDefinition) {
        Map<String, Object> result = new HashMap<String, Object>(stateDefinition.getPaths().size());
        for (String p : stateDefinition.getPaths()) {
            Ref ref = refFactory.ref(p);
            if (ref.isValid()) {
                result.put(p, ref.getValue());
            }
        }
        return result;
    }

}
