package at.irian.ankor.el;

import at.irian.ankor.ref.Ref;

import javax.el.*;
import java.beans.FeatureDescriptor;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ModelContextELContext extends ELContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SingleReadonlyVariableELContext.class);

    private final CompositeELResolver elResolver;
    private FunctionMapper functionMapper;
    private VariableMapper variableMapper;

    public ModelContextELContext(ELContext baseELContext,
                                 String contextVarName,
                                 Ref contextRef) {
        this.elResolver = new CompositeELResolver();
        this.elResolver.add(new ModelContextELResolver(contextVarName, contextRef));
        this.elResolver.add(baseELContext.getELResolver());
        this.functionMapper = baseELContext.getFunctionMapper();
        this.variableMapper = baseELContext.getVariableMapper();
    }

    @Override
    public ELResolver getELResolver() {
        return elResolver;
    }

    @Override
    public FunctionMapper getFunctionMapper() {
        return functionMapper;
    }

    @Override
    public VariableMapper getVariableMapper() {
        return variableMapper;
    }


    private static class ModelContextELResolver extends ELResolver {

        protected final String contextVarName;
        protected final Ref contextRef;

        public ModelContextELResolver(String contextVarName, Ref contextRef) {
            this.contextVarName = contextVarName;
            this.contextRef = contextRef;
        }

        @Override
        public Object getValue(ELContext context, Object base, Object property) {
            if (base == null && contextVarName.equals(property)) {
                context.setPropertyResolved(true);
                return contextRef.getValue();
            }
            return null;
        }

        @Override
        public Class<?> getType(ELContext context, Object base, Object property) {
            if (base == null && contextVarName.equals(property)) {
                context.setPropertyResolved(true);
                return Object.class;
            }
            return null;
        }

        @Override
        public void setValue(ELContext context, Object base, Object property, Object value) {
            if (base == null && contextVarName.equals(property)) {
                context.setPropertyResolved(true);
                contextRef.setValue(value);
            }
        }

        @Override
        public boolean isReadOnly(ELContext context, Object base, Object property) {
            return false;
        }

        @Override
        public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
            if (base == null) {
                FeatureDescriptor featureDescriptor = new FeatureDescriptor();
                featureDescriptor.setName(contextVarName);
                featureDescriptor.setDisplayName(contextVarName);
                featureDescriptor.setExpert(true);
                featureDescriptor.setShortDescription(contextVarName + " ref");
                return Collections.singleton(featureDescriptor).iterator();
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
}
