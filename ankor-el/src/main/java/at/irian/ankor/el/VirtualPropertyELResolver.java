package at.irian.ankor.el;

import at.irian.ankor.viewmodel.metadata.BeanMetadata;
import at.irian.ankor.viewmodel.metadata.BeanMetadataProvider;
import at.irian.ankor.viewmodel.metadata.PropertyMetadata;
import at.irian.ankor.viewmodel.metadata.VirtualMetadata;

import javax.el.ELContext;
import javax.el.ELResolver;
import java.beans.FeatureDescriptor;
import java.util.Iterator;

/**
* @author Manfred Geiler
*/
public class VirtualPropertyELResolver extends ELResolver {

    private final BeanMetadataProvider beanMetadataProvider;

    public VirtualPropertyELResolver(BeanMetadataProvider beanMetadataProvider) {
        this.beanMetadataProvider = beanMetadataProvider;
    }

    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        if (base != null) {
            BeanMetadata beanMetadata = beanMetadataProvider.getMetadata(base);
            String propertyName = property.toString();
            PropertyMetadata propertyMetadata = beanMetadata.getPropertyMetadata(propertyName);
            if (propertyMetadata != null) {
                VirtualMetadata virtualMetadata = propertyMetadata.getGenericMetadata(VirtualMetadata.class);
                if (virtualMetadata != null) {
                    if (virtualMetadata.isVirtual()) {
                        context.setPropertyResolved(true);
                        return null;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        if (base != null) {
            BeanMetadata beanMetadata = beanMetadataProvider.getMetadata(base);
            String propertyName = property.toString();
            PropertyMetadata propertyMetadata = beanMetadata.getPropertyMetadata(propertyName);
            if (propertyMetadata != null) {
                VirtualMetadata virtualMetadata = propertyMetadata.getGenericMetadata(VirtualMetadata.class);
                if (virtualMetadata != null) {
                    if (virtualMetadata.isVirtual()) {
                        context.setPropertyResolved(true);
                        return virtualMetadata.getPropertyType();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        if (base != null) {
            BeanMetadata beanMetadata = beanMetadataProvider.getMetadata(base);
            String propertyName = property.toString();
            PropertyMetadata propertyMetadata = beanMetadata.getPropertyMetadata(propertyName);
            if (propertyMetadata != null) {
                VirtualMetadata virtualMetadata = propertyMetadata.getGenericMetadata(VirtualMetadata.class);
                if (virtualMetadata != null) {
                    if (virtualMetadata.isVirtual()) {
                        context.setPropertyResolved(true);
                    }
                }
            }
        }
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        if (base != null) {
            BeanMetadata beanMetadata = beanMetadataProvider.getMetadata(base);
            String propertyName = property.toString();
            PropertyMetadata propertyMetadata = beanMetadata.getPropertyMetadata(propertyName);
            if (propertyMetadata != null) {
                VirtualMetadata virtualMetadata = propertyMetadata.getGenericMetadata(VirtualMetadata.class);
                if (virtualMetadata != null) {
                    if (virtualMetadata.isVirtual()) {
                        context.setPropertyResolved(true);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        // todo
        return null;
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return Object.class;
    }
}
