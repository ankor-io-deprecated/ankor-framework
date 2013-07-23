package at.irian.ankor.action;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Spiegl
 */
public class SimpleParamAction extends SimpleAction {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleParamAction.class);

    private Map<String, Object> params;

    protected SimpleParamAction() {
    }

    public SimpleParamAction(String name, Map<String, Object> params) {
        super(name);
        this.params = params;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public static Builder simpleAction() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private final Map<String, Object> params = new HashMap<String, Object>();

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withParam(String name, Object value) {
            params.put(name, value);
            return this;
        }

        public SimpleParamAction create() {
            return new SimpleParamAction(name, params);
        }

    }
}
