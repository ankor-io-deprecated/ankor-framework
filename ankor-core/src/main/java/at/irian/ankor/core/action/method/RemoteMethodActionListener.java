package at.irian.ankor.core.action.method;

import at.irian.ankor.core.action.ModelAction;
import at.irian.ankor.core.application.Application;
import at.irian.ankor.core.el.ELSupport;
import at.irian.ankor.core.el.SimpleReadonlySingletonELContext;
import at.irian.ankor.core.listener.ModelActionListener;
import at.irian.ankor.core.ref.Ref;
import at.irian.ankor.core.ref.RefFactory;
import at.irian.ankor.core.ref.el.ELRefContext;

import javax.el.ELContext;
import javax.el.ValueExpression;

/**
* @author MGeiler (Manfred Geiler)
*/
public class RemoteMethodActionListener implements ModelActionListener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteMethodActionListener.class);

    private static final String CONTEXT_VAR_NAME = "context";

    private final ELSupport elSupport;
    private final RefFactory refFactory;

    public RemoteMethodActionListener(Application application) {
        this.elSupport = application.getELSupport();
        this.refFactory = application.getRefFactory();
    }

    @Override
    public void handleModelAction(Ref actionContext, ModelAction action) {
        if (action instanceof RemoteMethodAction) {
            processMethodAction(actionContext, (RemoteMethodAction) action);
        }
    }

    private void processMethodAction(Ref actionContext, RemoteMethodAction action) {

        ELContext methodExecutionELContext = createMethodExecutionELContext(actionContext);

        Object result;
        try {
            result = elSupport.executeMethod(methodExecutionELContext, action.getMethodExpression());
        } catch (Exception e) {
            handleError(actionContext, action, e);
            return;
        }

        handleResult(actionContext, methodExecutionELContext, action.getResultPath(), result);

        if (action.isAutoRefreshActionContext()) {
            actionContext.setValue(actionContext.getValue());
        }

        fireCompleteAction(actionContext, action.getCompleteAction());
    }

    private void handleResult(Ref actionContext,
                              ELContext methodExecutionELContext,
                              String resultPath,
                              Object result) {
        if (resultPath != null) {
            ELRefContext refContext = ((ELRefContext) actionContext.refContext()).with(methodExecutionELContext);
            Ref resultRef = refFactory.ref(resultPath, refContext);
            resultRef.setValue(result);
        }
    }

    private void fireCompleteAction(Ref actionContext, ModelAction completeAction) {
        if (completeAction != null) {
            actionContext.fire(completeAction);
        }
    }

    private void handleError(Ref actionContext, RemoteMethodAction action, Exception e) {
        ModelAction errorAction = action.getErrorAction();
        if (errorAction != null) {
            actionContext.fire(errorAction);
        } else {
            LOG.error("Error executing " + action, e);
        }
    }

    private ELContext createMethodExecutionELContext(Ref actionContext) {
        ValueExpression contextVE = elSupport.createValueExpression(elSupport.getBaseELContext(), actionContext.path());
        return new SimpleReadonlySingletonELContext(elSupport.getBaseELContext(), CONTEXT_VAR_NAME, contextVE);
    }

}
