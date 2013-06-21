package at.irian.ankor.el;

import at.irian.ankor.ref.Ref;
import com.typesafe.config.Config;

import javax.el.*;
import java.beans.FeatureDescriptor;
import java.util.Arrays;
import java.util.Iterator;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ModelContextELContext extends ELContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SingleReadonlyVariableELContext.class);

    private final CompositeELResolver elResolver;
    private FunctionMapper functionMapper;
    private VariableMapper variableMapper;

    public ModelContextELContext(ELContext baseELContext, Config config, Ref modelContext) {
        this.elResolver = new CompositeELResolver();
        this.elResolver.add(new ModelContextELResolver(config, modelContext));
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
        protected final String contextRefVarName;
        protected final Ref    contextRef;

        public ModelContextELResolver(Config config, Ref modelContext) {
            this.contextVarName     = config.getString("ankor.variable-names.context");
            this.contextRefVarName  = config.getString("ankor.variable-names.contextRef");
            this.contextRef = modelContext;
        }

        @Override
        public Object getValue(ELContext context, Object base, Object property) {
            if (base == null && contextVarName.equals(property)) {
                context.setPropertyResolved(true);
                return contextRef.getValue();
            } else if (base == null && contextRefVarName.equals(property)) {
                context.setPropertyResolved(true);
                return contextRef;
            }
            return null;
        }

        @Override
        public Class<?> getType(ELContext context, Object base, Object property) {
            if (base == null && contextVarName.equals(property)) {
                context.setPropertyResolved(true);
                return Object.class;
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
                contextRef.setValue(value);
            } else if (base == null && contextRefVarName.equals(property)) {
                throw new PropertyNotWritableException(contextRefVarName);
            }
        }

        @Override
        public boolean isReadOnly(ELContext context, Object base, Object property) {
            return base == null && contextRefVarName.equals(property);
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
                fd2.setName(contextRefVarName);
                fd2.setDisplayName(contextRefVarName);
                fd2.setExpert(true);
                fd2.setShortDescription("reference to the current context");

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
}
