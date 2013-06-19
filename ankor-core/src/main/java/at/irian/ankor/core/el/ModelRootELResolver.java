package at.irian.ankor.core.el;

import at.irian.ankor.core.application.ModelHolder;

import javax.el.ELContext;
import javax.el.ELResolver;
import java.beans.FeatureDescriptor;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author Manfred Geiler
 */
public class ModelRootELResolver extends ELResolver {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelHolderELResolver.class);

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
