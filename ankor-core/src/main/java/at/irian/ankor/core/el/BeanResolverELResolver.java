package at.irian.ankor.core.el;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;
import java.beans.FeatureDescriptor;
import java.util.Iterator;

/**
 * @author Thomas Spiegl
 */
public class BeanResolverELResolver extends ELResolver {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelHolderELResolver.class);

    private final BeanResolver beanResolver;

    public BeanResolverELResolver(BeanResolver beanResolver) {
        this.beanResolver = beanResolver;
    }

    private FeatureDescriptor createFeatureDescriptor(String modelHolderVarName) {
        FeatureDescriptor featureDescriptor = new FeatureDescriptor();
        featureDescriptor.setName(modelHolderVarName);
        featureDescriptor.setDisplayName(modelHolderVarName);
        featureDescriptor.setExpert(true);
        featureDescriptor.setShortDescription("model holder singleton instance");
        return featureDescriptor;
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
