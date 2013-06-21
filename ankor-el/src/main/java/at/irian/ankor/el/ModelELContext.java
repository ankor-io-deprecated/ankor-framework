package at.irian.ankor.el;

import at.irian.ankor.application.ModelHolder;
import com.typesafe.config.Config;

import javax.el.*;
import java.beans.FeatureDescriptor;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author Manfred Geiler
 */
public class ModelELContext extends ELContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StandardELContext.class);

    private final CompositeELResolver elResolver;
    private FunctionMapper functionMapper;
    private VariableMapper variableMapper;

    public ModelELContext(ELContext baseELContext, ModelHolder modelHolder, Config config) {
        this.elResolver = new CompositeELResolver();
        this.elResolver.add(new ModelRootELResolver(config, modelHolder));
        this.elResolver.add(new ModelHolderELResolver(config, modelHolder));
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


    private static class ModelHolderELResolver extends SingleReadonlyVariableELResolver {

        public ModelHolderELResolver(Config config, ModelHolder modelHolder) {
            super(config.getString("ankor.variable-names.modelHolder"), modelHolder);
        }

        @Override
        public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
            if (base == null) {
                FeatureDescriptor featureDescriptor = new FeatureDescriptor();
                featureDescriptor.setName(varName);
                featureDescriptor.setDisplayName(varName);
                featureDescriptor.setExpert(true);
                featureDescriptor.setShortDescription("model holder singleton instance");
                return Collections.singleton(featureDescriptor).iterator();
            }
            return null;
        }

    }


    private static class ModelRootELResolver extends ELResolver {

        private final ModelHolder modelHolder;
        private final String modelRootVarName;

        public ModelRootELResolver(Config config, ModelHolder modelHolder) {
            this.modelHolder = modelHolder;
            this.modelRootVarName    = config.getString("ankor.variable-names.modelRoot");
        }

        @Override
        public Object getValue(ELContext context, Object base, Object property) {
            if (base == null && modelRootVarName.equals(property)) {
                context.setPropertyResolved(true);
                return modelHolder.getModel();
            }
            return null;
        }

        @Override
        public Class<?> getType(ELContext context, Object base, Object property) {
            if (base == null && modelRootVarName.equals(property)) {
                context.setPropertyResolved(true);
                return modelHolder.getModelType();
            }
            return null;
        }

        @Override
        public void setValue(ELContext context, Object base, Object property, Object value) {
            if (base == null && modelRootVarName.equals(property)) {
                context.setPropertyResolved(true);
                modelHolder.setModel(value);
            }
        }

        @Override
        public boolean isReadOnly(ELContext context, Object base, Object property) {
            return false;
        }

        @Override
        public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
            if (base == null) {
                FeatureDescriptor fd1 = new FeatureDescriptor();
                fd1.setName(modelRootVarName);
                fd1.setDisplayName(modelRootVarName);
                fd1.setExpert(false);
                fd1.setShortDescription("root of the current model");
                return Collections.singleton(fd1).iterator();
            }
            return null;
        }

        @Override
        public Class<?> getCommonPropertyType(ELContext context, Object base) {
            return modelHolder.getModelType();
        }
    }
}
