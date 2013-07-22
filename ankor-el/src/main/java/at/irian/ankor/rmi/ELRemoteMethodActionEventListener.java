package at.irian.ankor.rmi;

import at.irian.ankor.el.BeanResolverELResolver;
import at.irian.ankor.el.StandardELContext;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.el.ELRefContext;
import at.irian.ankor.system.BeanResolver;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import java.util.Map;

/**
* @author Manfred Geiler
*/
public class ELRemoteMethodActionEventListener extends RemoteMethodActionEventListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteMethodActionEventListener.class);

    private final ExpressionFactory expressionFactory;

    public ELRemoteMethodActionEventListener() {
        expressionFactory = ExpressionFactory.newInstance();
    }

    @Override
    protected Object executeMethod(Ref modelContext, String methodExpression, final Map<String, Object> params) {
        ELRefContext refContext = (ELRefContext) modelContext.context();
        //ELContext modelContextELContext = refContext.withModelContext(modelContext).getELContext();

        StandardELContext executionELContext = refContext.getElContext();
        if (params != null) {
            executionELContext = executionELContext.withAdditional(new BeanResolverELResolver(new BeanResolver() {
                @Override
                public Object resolveByName(String beanName) {
                    return params.get(beanName);
                }

                @Override
                public String[] getBeanDefinitionNames() {
                    return params.keySet().toArray(new String[params.keySet().size()]);
                }
            }));
        }

        ValueExpression ve = expressionFactory.createValueExpression(executionELContext,
                                                                     "#{" + methodExpression + "}",
                                                                     Object.class);
        return ve.getValue(executionELContext);
    }

}
