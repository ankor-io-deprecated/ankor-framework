package at.irian.ankor.el;

import javax.el.ArrayELResolver;
import javax.el.ELContext;
import javax.el.PropertyNotFoundException;
import java.lang.reflect.Array;

/**
 * Like a ArrayELResolver but will not throw a <code>PropertyNotFoundException</code>
 * when setting out of upper bounds indices. It will instead fill the intermediate fields with null.
 *
 * @see ArrayELResolver
 * @see java.util.List
 */
public class FriendlyArrayELResolver extends ArrayELResolver {

    @Override
    public Class<?> getType(ELContext context,
                         Object base,
                         Object property) {

        if (context == null) {
            throw new NullPointerException();
        }

        if (base != null && base.getClass().isArray()) {
            context.setPropertyResolved(true);
            int index = toInteger (property);
            if (index < 0) {
                throw new PropertyNotFoundException();
            }
            return base.getClass().getComponentType();
        }
        return null;
    }

    @Override
    public void setValue(ELContext context,
                         Object base,
                         Object property,
                         Object val) {

        if (context == null) {
            throw new NullPointerException();
        }

        if (base != null && base.getClass().isArray()) {
            context.setPropertyResolved(true);
            Class<?> type = base.getClass().getComponentType();
            if (val != null && ! type.isAssignableFrom(val.getClass())) {
                throw new ClassCastException();
            }
            int index = toInteger (property);
            if (index < 0) {
                throw new PropertyNotFoundException();
            }
            int size = Array.getLength(base);

            // XXX: This is wrong! Should create a new Array when necessary and copy the old values
            while (size <= index) {
                Array.set(base, size++, null);
            }
            Array.set(base, index, val);
        }
    }

    private int toInteger(Object p) {

        if (p instanceof Integer) {
            return ((Integer) p).intValue();
        }
        if (p instanceof Character) {
            return ((Character) p).charValue();
        }
        if (p instanceof Boolean) {
            return ((Boolean) p).booleanValue()? 1: 0;
        }
        if (p instanceof Number) {
            return ((Number) p).intValue();
        }
        if (p instanceof String) {
            return Integer.parseInt((String) p);
        }
        throw new IllegalArgumentException();
    }
}
