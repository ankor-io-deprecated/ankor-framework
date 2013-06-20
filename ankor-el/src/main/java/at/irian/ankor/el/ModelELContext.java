package at.irian.ankor.el;

import at.irian.ankor.application.ModelHolder;

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

    public ModelELContext(ELContext baseELContext,
                          ModelHolder modelHolder,
                          String modelRootVarName,
                          String modelHolderVarName) {
        this.elResolver = new CompositeELResolver();
        this.elResolver.add(new ModelRootELResolver(modelRootVarName, modelHolder));
        this.elResolver.add(new ModelHolderELResolver(modelHolderVarName, modelHolder));
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

        public ModelHolderELResolver(String modelHolderVarName, ModelHolder modelHolder) {
            super(modelHolderVarName, modelHolder);
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
        private final FeatureDescriptor featureDescriptor;

        public ModelRootELResolver(String modelRootVarName, ModelHolder modelHolder) {
            this.modelHolder = modelHolder;
            this.modelRootVarName = modelRootVarName;
            this.featureDescriptor = createFeatureDescriptor(modelRootVarName);
        }

        private FeatureDescriptor createFeatureDescriptor(String modelRootVarName) {
            FeatureDescriptor featureDescriptor = new FeatureDescriptor();
            featureDescriptor.setName(modelRootVarName);
            featureDescriptor.setDisplayName(modelRootVarName);
            featureDescriptor.setExpert(false);
            featureDescriptor.setShortDescription("root of the current model");
            return featureDescriptor;
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
                return Collections.singleton(featureDescriptor).iterator();
            }
            return null;
        }

        @Override
        public Class<?> getCommonPropertyType(ELContext context, Object base) {
            return modelHolder.getModelType();
        }
    }
}
