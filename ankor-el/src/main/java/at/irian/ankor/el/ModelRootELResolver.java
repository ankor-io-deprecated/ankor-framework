package at.irian.ankor.el;

import at.irian.ankor.context.ModelHolder;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefFactory;
import com.typesafe.config.Config;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;
import java.beans.FeatureDescriptor;
import java.util.Arrays;
import java.util.Iterator;

/**
* @author Manfred Geiler
*/
public class ModelRootELResolver extends ELResolver {

    private final String modelRootVarName;
    private final String modelRootRefVarName;
    private final String modelHolderVarName;
    private final ModelHolder modelHolder;
    private final RefFactory refFactory;

    public ModelRootELResolver(Config config, ModelHolder modelHolder, RefFactory refFactory) {
        this.modelRootVarName    = config.getString("ankor.variable-names.modelRoot");
        this.modelRootRefVarName = config.getString("ankor.variable-names.modelRootRef");
        this.modelHolderVarName  = config.getString("ankor.variable-names.modelHolder");
        this.modelHolder = modelHolder;
        this.refFactory = refFactory;
    }

    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        if (base == null && modelRootVarName.equals(property)) {
            context.setPropertyResolved(true);
            return modelHolder.getModel();
        } else if (base == null && modelRootRefVarName.equals(property)) {
            context.setPropertyResolved(true);
            return refFactory.ref(modelRootVarName);
        } else if (base == null && modelHolderVarName.equals(property)) {
            context.setPropertyResolved(true);
            return modelHolder;
        }
        return null;
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        if (base == null && modelRootVarName.equals(property)) {
            context.setPropertyResolved(true);
            return modelHolder.getModelType();
        } else if (base == null && modelRootRefVarName.equals(property)) {
            context.setPropertyResolved(true);
            return Ref.class;
        } else if (base == null && modelHolderVarName.equals(property)) {
            context.setPropertyResolved(true);
            return ModelHolder.class;
        }
        return null;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        if (base == null && modelRootVarName.equals(property)) {
            context.setPropertyResolved(true);
            modelHolder.setModel(value);
        } else if (base == null && modelRootRefVarName.equals(property)) {
            throw new PropertyNotWritableException(property.toString());
        } else if (base == null && modelHolderVarName.equals(property)) {
            throw new PropertyNotWritableException(property.toString());
        }
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        if (base == null && modelRootVarName.equals(property)) {
            return false;
        } else if (base == null && modelRootRefVarName.equals(property)) {
            return true;
        } else if (base == null && modelHolderVarName.equals(property)) {
            return true;
        }
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

            FeatureDescriptor fd2 = new FeatureDescriptor();
            fd2.setName(modelRootRefVarName);
            fd2.setDisplayName(modelRootRefVarName);
            fd2.setExpert(true);
            fd2.setShortDescription("ref to the root of the current model");

            FeatureDescriptor fd3 = new FeatureDescriptor();
            fd3.setName(modelHolderVarName);
            fd3.setDisplayName(modelHolderVarName);
            fd3.setExpert(true);
            fd3.setShortDescription("ref to the current model holder");

            return Arrays.asList(fd1, fd2, fd3).iterator();
        }
        return null;
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return modelHolder.getModelType();
    }
}
