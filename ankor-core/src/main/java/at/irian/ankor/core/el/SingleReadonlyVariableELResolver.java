package at.irian.ankor.core.el;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;
import java.beans.FeatureDescriptor;
import java.util.Collections;
import java.util.Iterator;

/**
* @author Manfred Geiler
*/
public class SingleReadonlyVariableELResolver extends ELResolver {

    protected final String varName;
    protected final Object value;

    public SingleReadonlyVariableELResolver(String varName, Object value) {
        this.varName = varName;
        this.value = value;
    }

    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        if (base == null && varName.equals(property)) {
            context.setPropertyResolved(true);
            return value;
        }
        return null;
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        if (base == null && varName.equals(property)) {
            context.setPropertyResolved(true);
            return value != null ? value.getClass() : Object.class;
        }
        return null;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        if (base == null && varName.equals(property)) {
            throw new PropertyNotWritableException(varName);
        }
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        if (base == null && varName.equals(property)) {
            context.setPropertyResolved(true);
            return true;
        }
        return false;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        if (base == null) {
            FeatureDescriptor featureDescriptor = new FeatureDescriptor();
            featureDescriptor.setName(varName);
            featureDescriptor.setDisplayName(varName);
            featureDescriptor.setExpert(true);
            featureDescriptor.setShortDescription(varName + " singleton instance");
            return Collections.singleton(featureDescriptor).iterator();
        }
        return null;
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base == null) {
            return value != null ? value.getClass() : Object.class;
        }
        return null;
    }
}
