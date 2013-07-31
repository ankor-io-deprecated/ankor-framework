package at.irian.ankor.action;

import java.util.Collections;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class Action {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Action.class);

    private String name;

    private Map<String, Object> params;

    /**
     * for deserialization only
     */
    protected Action() {}

    public Action(String name) {
        this(name, null);
    }

    public Action(String name, Map<String, Object> params) {
        this.name = name;
        this.params = params != null && !params.isEmpty()
                      ? Collections.unmodifiableMap(params)
                      : null;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getParams() {
        return params != null ? params : Collections.<String, Object>emptyMap();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Action action = (Action) o;

        return name.equals(action.name) && params.equals(action.params);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + params.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Action{" +
               "name='" + name + '\'' +
               ", params=" + getParams() +
               '}';
    }
}
