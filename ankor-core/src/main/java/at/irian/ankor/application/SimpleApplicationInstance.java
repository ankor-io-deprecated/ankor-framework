package at.irian.ankor.application;

import at.irian.ankor.ref.RefContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
* @author Manfred Geiler
*/
public class SimpleApplicationInstance implements ApplicationInstance {

    private Map<String, Object> roots;

    @Override
    public void init(RefContext refContext) {
        roots = new HashMap<String, Object>();
    }

    @Override
    public Set<String> getKnownRootNames() {
        return roots.keySet();
    }

    @Override
    public Object getModelRoot(String rootVarName) {
        return roots.get(rootVarName);
    }

    @Override
    public void setModelRoot(String rootVarName, Object bean) {
        roots.put(rootVarName, bean);
    }

    @Override
    public void release() {
        roots.clear();
        roots = null;
    }
}
