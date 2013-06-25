package at.irian.ankor.el;

import at.irian.ankor.context.ModelHolder;
import com.typesafe.config.Config;

import javax.el.ELContext;
import javax.el.ELResolver;
import java.beans.FeatureDescriptor;
import java.util.Collections;
import java.util.Iterator;

/**
* @author MGeiler (Manfred Geiler)
*/
public class ModelRootELResolver extends ELResolver {

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
