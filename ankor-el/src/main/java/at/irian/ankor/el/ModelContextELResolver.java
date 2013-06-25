package at.irian.ankor.el;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefFactory;
import com.typesafe.config.Config;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.PropertyNotWritableException;
import java.beans.FeatureDescriptor;
import java.util.Arrays;
import java.util.Iterator;

/**
* @author MGeiler (Manfred Geiler)
*/
public class ModelContextELResolver extends ELResolver {

    private final ExpressionFactory expressionFactory;
    private final RefFactory refFactory;
    private final String contextVarName;
    private final String contextPathVarName;
    private final String contextPath;
    private final String contextRefVarName;

    public ModelContextELResolver(ExpressionFactory expressionFactory,
                                  Config config,
                                  String contextPath,
                                  RefFactory refFactory) {
        this.expressionFactory = expressionFactory;
        this.refFactory = refFactory;
        this.contextVarName     = config.getString("ankor.variable-names.context");
        this.contextPathVarName = config.getString("ankor.variable-names.contextPath");
        this.contextRefVarName  = config.getString("ankor.variable-names.contextRef");
        this.contextPath = contextPath;
    }

    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        if (base == null && contextVarName.equals(property)) {
            context.setPropertyResolved(true);
            return expressionFactory.createValueExpression(context, ELUtils.pathToExpr(contextPath), Object.class)
                                    .getValue(context);
        } else if (base == null && contextPathVarName.equals(property)) {
            context.setPropertyResolved(true);
            return contextPath;
        } else if (base == null && contextRefVarName.equals(property)) {
            context.setPropertyResolved(true);
            return refFactory.ref(contextPath);
        }
        return null;
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        if (base == null && contextVarName.equals(property)) {
            context.setPropertyResolved(true);
            return Object.class;
        } else if (base == null && contextPathVarName.equals(property)) {
            context.setPropertyResolved(true);
            return String.class;
        } else if (base == null && contextRefVarName.equals(property)) {
            context.setPropertyResolved(true);
            return Ref.class;
        }
        return null;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        if (base == null && contextVarName.equals(property)) {
            context.setPropertyResolved(true);
            expressionFactory.createValueExpression(context, ELUtils.pathToExpr(contextPath), Object.class)
                             .setValue(context, value);
        } else if (base == null && contextPathVarName.equals(property)) {
            throw new PropertyNotWritableException(contextPathVarName);
        } else if (base == null && contextRefVarName.equals(property)) {
            throw new PropertyNotWritableException(contextRefVarName);
        }
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        return base == null && (contextPathVarName.equals(property) || contextRefVarName.equals(property));
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        if (base == null) {
            FeatureDescriptor fd1 = new FeatureDescriptor();
            fd1.setName(contextVarName);
            fd1.setDisplayName(contextVarName);
            fd1.setExpert(false);
            fd1.setShortDescription("value of the current context");

            FeatureDescriptor fd2 = new FeatureDescriptor();
            fd2.setName(contextPathVarName);
            fd2.setDisplayName(contextPathVarName);
            fd2.setExpert(true);
            fd2.setShortDescription("the current context path");

            FeatureDescriptor fd3 = new FeatureDescriptor();
            fd3.setName(contextRefVarName);
            fd3.setDisplayName(contextRefVarName);
            fd3.setExpert(true);
            fd3.setShortDescription("the current context ref");

            return Arrays.asList(fd1, fd2).iterator();
        }
        return null;
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base == null) {
            return Object.class;
        }
        return null;
    }
}
