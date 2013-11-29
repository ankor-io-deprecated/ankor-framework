package at.irian.ankor.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.Collections;
import java.util.Map;

/**
 * An action that happened.
 * Typically an Action is reported by means of an {@link ActionEvent}.
 * Every action must have a name and optionally can have parameters.
 *
 * @author Manfred Geiler
 */
public class Action {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Action.class);

    private final String name;

    @JsonInclude(Include.NON_EMPTY)
    private final Map<String, Object> params;

    public Action(String name) {
        this(name, null);
    }

    public Action(String name, Map<String, Object> params) {
        this.name = name;
        if (params != null && !params.isEmpty()) {
            this.params = Collections.unmodifiableMap(params);
        } else {
            this.params = Collections.emptyMap();
        }
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getParams() {
        return params;
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
               ", params=" + params +
               '}';
    }
}
