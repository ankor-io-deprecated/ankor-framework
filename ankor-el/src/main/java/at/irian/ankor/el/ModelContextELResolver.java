package at.irian.ankor.el;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.connection.ModelRootFactory;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;
import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
* @author Manfred Geiler
*/
public class ModelContextELResolver extends ELResolver {

    private final ModelContext modelContext;
    private final RefFactory refFactory;
    private final ModelRootFactory modelRootFactory;

    public ModelContextELResolver(ModelContext modelContext, RefFactory refFactory, ModelRootFactory modelRootFactory) {
        this.modelContext = modelContext;
        this.refFactory = refFactory;
        this.modelRootFactory = modelRootFactory;
    }

    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        if (base == null) {
            String propertyName = property.toString();
            if (modelRootFactory.getKnownRootNames().contains(propertyName)) {
                context.setPropertyResolved(true);
                return modelContext.getModelRoot(propertyName);
            } else if (propertyName.startsWith("&") && modelRootFactory.getKnownRootNames().contains(propertyName.substring(1))) {
                context.setPropertyResolved(true);
                return refFactory.ref(propertyName.substring(1));
            }
        }
        return null;
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        if (base == null) {
            String propertyName = property.toString();
            if (modelRootFactory.getKnownRootNames().contains(propertyName)) {
                context.setPropertyResolved(true);
                Object modelRoot = modelContext.getModelRoot(propertyName);
                return modelRoot != null ? modelRoot.getClass() : Object.class;
            } else if (propertyName.startsWith("&")) {
                context.setPropertyResolved(true);
                return Ref.class;
            }
        }
        return null;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        if (base == null) {
            String propertyName = property.toString();
            if (modelRootFactory.getKnownRootNames().contains(propertyName)) {
                context.setPropertyResolved(true);
                modelContext.setModelRoot(propertyName, value);
            } else if (propertyName.startsWith("&")) {
                throw new PropertyNotWritableException(property.toString());
            }
        }
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        if (base == null) {
            String propertyName = property.toString();
            if (modelRootFactory.getKnownRootNames().contains(propertyName)) {
                return false;
            } else if (propertyName.startsWith("&")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        if (base == null) {
            List<FeatureDescriptor> featureDescriptors = new ArrayList<FeatureDescriptor>();

            FeatureDescriptor fd;
            for (String rootName : modelRootFactory.getKnownRootNames()) {
                fd = new FeatureDescriptor();
                fd.setName(rootName);
                fd.setDisplayName(rootName);
                fd.setExpert(false);
                fd.setShortDescription("root of the current model");
                featureDescriptors.add(fd);

                fd = new FeatureDescriptor();
                fd.setName('&' + rootName);
                fd.setDisplayName('&' + rootName);
                fd.setExpert(true);
                fd.setShortDescription("ref to the root of the current model");
                featureDescriptors.add(fd);
            }

            return featureDescriptors.iterator();
        }
        return null;
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return Object.class;
    }
}
