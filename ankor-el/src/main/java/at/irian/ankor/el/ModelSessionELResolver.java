package at.irian.ankor.el;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.session.ModelSession;

import javax.el.ELContext;
import javax.el.ELResolver;
import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
* @author Manfred Geiler
*/
public class ModelSessionELResolver extends ELResolver {

    private final ModelSession modelSession;
    private final RefFactory refFactory;

    public ModelSessionELResolver(ModelSession modelSession, RefFactory refFactory) {
        this.modelSession = modelSession;
        this.refFactory = refFactory;
    }

    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        if (base == null) {
            String propertyName = property.toString();
            if (modelSession.getModelNames().contains(propertyName)) {
                context.setPropertyResolved(true);
                return modelSession.getModelRoot(propertyName);
            } else if (propertyName.startsWith("&") && modelSession.getModelNames().contains(propertyName.substring(1))) {
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
            if (modelSession.getModelNames().contains(propertyName)) {
                context.setPropertyResolved(true);
                Object modelRoot = modelSession.getModelRoot(propertyName);
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
            context.setPropertyResolved(true);
            modelSession.addModelRoot(propertyName, value);
        }
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        if (base == null) {
            String propertyName = property.toString();
            if (modelSession.getModelNames().contains(propertyName)) {
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
            for (String rootName : modelSession.getModelNames()) {
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
