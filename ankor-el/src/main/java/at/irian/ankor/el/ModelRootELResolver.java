package at.irian.ankor.el;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.session.DefaultServerSession;
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
    private final String modelContextVarName;
    private final ModelContext modelContext;
    private final RefFactory refFactory;

    public ModelRootELResolver(Config config, ModelContext modelContext, RefFactory refFactory) {
        this.modelRootVarName    = config.getString("ankor.variable-names.modelRoot");
        this.modelRootRefVarName = config.getString("ankor.variable-names.modelRootRef");
        this.modelContextVarName = config.getString("ankor.variable-names.modelContext");
        this.modelContext = modelContext;
        this.refFactory = refFactory;
    }

    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        if (base == null && modelRootVarName.equals(property)) {
            context.setPropertyResolved(true);
            return modelContext.getModelRoot();
        } else if (base == null && modelRootRefVarName.equals(property)) {
            context.setPropertyResolved(true);
            return refFactory.ref(modelRootVarName);
        } else if (base == null && modelContextVarName.equals(property)) {
            context.setPropertyResolved(true);
            return modelContext;
        }
        return null;
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        if (base == null && modelRootVarName.equals(property)) {
            context.setPropertyResolved(true);
            Object modelRoot = modelContext.getModelRoot();
            return modelRoot != null ? modelRoot.getClass() : Object.class;
        } else if (base == null && modelRootRefVarName.equals(property)) {
            context.setPropertyResolved(true);
            return Ref.class;
        } else if (base == null && modelContextVarName.equals(property)) {
            context.setPropertyResolved(true);
            return DefaultServerSession.class;
        }
        return null;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        if (base == null && modelRootVarName.equals(property)) {
            context.setPropertyResolved(true);
            modelContext.setModelRoot(value);
        } else if (base == null && modelRootRefVarName.equals(property)) {
            throw new PropertyNotWritableException(property.toString());
        } else if (base == null && modelContextVarName.equals(property)) {
            throw new PropertyNotWritableException(property.toString());
        }
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        if (base == null && modelRootVarName.equals(property)) {
            return false;
        } else if (base == null && modelRootRefVarName.equals(property)) {
            return true;
        } else if (base == null && modelContextVarName.equals(property)) {
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
            fd3.setName(modelContextVarName);
            fd3.setDisplayName(modelContextVarName);
            fd3.setExpert(true);
            fd3.setShortDescription("ref to the current model context");

            return Arrays.asList(fd1, fd2, fd3).iterator();
        }
        return null;
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return Object.class;
    }
}
