package at.irian.ankor.rmi;

import at.irian.ankor.application.BeanResolver;
import at.irian.ankor.el.BeanResolverELContext;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.ref.el.ELRefContext;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import java.util.Map;

/**
* @author MGeiler (Manfred Geiler)
*/
public class ELRemoteMethodActionListener extends RemoteMethodActionListener  {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteMethodActionListener.class);

    private final ExpressionFactory expressionFactory;

    public ELRemoteMethodActionListener(ExpressionFactory expressionFactory, RefFactory refFactory) {
        super(refFactory);
        this.expressionFactory = expressionFactory;
    }

    @Override
    protected Object executeMethod(Ref modelContext, String methodExpression, final Map<String, Object> params) {
        ELRefContext refContext = (ELRefContext) modelContext.refContext();
        ELContext modelContextELContext = refContext.withModelContext(modelContext).getELContext();

        ELContext executionELContext;
        if (params != null) {
            executionELContext = new BeanResolverELContext(modelContextELContext, new BeanResolver() {
                @Override
                public Object resolveByName(String beanName) {
                    return params.get(beanName);
                }
            });
        } else {
            executionELContext = modelContextELContext;
        }

        ValueExpression ve = expressionFactory.createValueExpression(executionELContext,
                                                                     "#{" + methodExpression + "}",
                                                                     Object.class);
        return ve.getValue(executionELContext);
    }

}
