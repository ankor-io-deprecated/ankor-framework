package at.irian.ankor.core.action;

import at.irian.ankor.core.ref.ModelRef;

import java.util.Arrays;
import java.util.List;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ParamsAction extends SimpleAction implements CompleteAware {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ParamsAction.class);

    private String[] params = new String[0];
    private ModelAction completeAction;

    protected ParamsAction(String name) {
        super(name);
    }

    protected ParamsAction(String name, String[] params, ModelAction completeAction) {
        super(name);
        this.params = params;
        this.completeAction = completeAction;
    }

    public ModelRef[] params(ModelRef actionContext) {
        ModelRef[] result = new ModelRef[params.length];
        for (int i = 0; i < params.length; i++) {
            result[i] = actionContext.sub(params[i]);
        }
        return result;
    }

    @Override
    public void complete(ModelRef actionContext) {
        if (completeAction != null) {
            actionContext.fire(completeAction);
        }
    }

    public static ParamsAction withName(String name) {
        return new ParamsAction(name);
    }

    public ParamsAction withParam(String paramPath) {
        List<String> lst = Arrays.asList(params);
        lst.add(paramPath);
        return new ParamsAction(name(), lst.toArray(new String[lst.size()]), completeAction);
    }

    public ParamsAction onCompleteFire(ModelAction action) {
        return new ParamsAction(name(), params, action);
    }
}
