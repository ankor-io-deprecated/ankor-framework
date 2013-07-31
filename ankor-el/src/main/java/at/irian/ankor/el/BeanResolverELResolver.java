package at.irian.ankor.el;

import at.irian.ankor.base.BeanResolver;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;
import java.beans.FeatureDescriptor;
import java.util.Iterator;

/**
* @author Manfred Geiler
*/
public class BeanResolverELResolver extends ELResolver {

    private final BeanResolver beanResolver;

    public BeanResolverELResolver(BeanResolver beanResolver) {
        this.beanResolver = beanResolver;
    }

    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        if (base == null && property != null && property instanceof String) {
            Object bean = beanResolver.resolveByName((String) property);
            if (bean != null) {
                context.setPropertyResolved(true);
                return bean;
            }
        }
        return null;
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        if (base == null && property != null && property instanceof String) {
            Object bean = beanResolver.resolveByName((String) property);
            if (bean != null) {
                context.setPropertyResolved(true);
                return bean.getClass();
            }
        }
        return null;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        if (base == null && property != null && property instanceof String) {
            Object bean = beanResolver.resolveByName((String) property);
            if (bean != null) {
                throw new PropertyNotWritableException((String) property);
            }
        }
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        if (base == null && property != null && property instanceof String) {
            Object bean = beanResolver.resolveByName((String) property);
            if (bean != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base == null) {
            return Object.class;
        }
        return null;
    }
}
