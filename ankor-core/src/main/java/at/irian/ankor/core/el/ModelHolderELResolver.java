package at.irian.ankor.core.el;

import at.irian.ankor.core.application.ModelHolder;

import javax.el.ELContext;
import javax.el.ELResolver;
import java.beans.FeatureDescriptor;
import java.util.Iterator;

/**
 * @author Thomas Spiegl
 */
public class ModelHolderELResolver extends ELResolver {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelHolderELResolver.class);

    private final ModelHolder modelHolder;

    public ModelHolderELResolver(ModelHolder modelHolder) {
        this.modelHolder = modelHolder;
    }

    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        if (base == null && "root".equals(property)) {
            context.setPropertyResolved(true);
            return modelHolder.getModel();
        }
        return null;
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
