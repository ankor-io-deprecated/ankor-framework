package at.irian.ankor.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Manfred Geiler
 */
public class DefaultStateHolderDefinition implements StateHolderDefinition {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultStateHolderDefinition.class);

    private final Map<String, Class<?>> stateHolderPaths = new HashMap<String, Class<?>>();

    @Override
    public Class<?> getTypeOf(String path) {
        return stateHolderPaths.get(path);
    }

    @Override
    public void add(String path, Class<?> type) {
        stateHolderPaths.put(path, type);
    }

    @Override
    public Set<String> getPaths() {
        return stateHolderPaths.keySet();
    }

}
