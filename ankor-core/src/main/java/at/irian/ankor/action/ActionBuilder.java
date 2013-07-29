package at.irian.ankor.action;

import java.util.HashMap;
import java.util.Map;

/**
* @author Manfred Geiler
*/
public class ActionBuilder {

    private String name;
    private final Map<String, Object> params = new HashMap<String, Object>();

    public ActionBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ActionBuilder withParam(String name, Object value) {
        params.put(name, value);
        return this;
    }

    public Action create() {
        return new Action(name, params);
    }

}
